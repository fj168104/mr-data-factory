package com.mr.modules.api.site.instance;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import schemasMicrosoftComVml.STTrueFalse;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by feng on 18-3-16
 * 地方证监局
 * 行政处罚决定
 */

@Slf4j
@Component("site2")
@Scope("prototype")
public class SiteTaskImpl_2 extends SiteTaskExtend {

	private static LinkedHashMap<String, String> cityMap = Maps.newLinkedHashMap();

	@PostConstruct
	public void initAfter() {
		log.info("cityMap instance created..............");
		initCityMap();
	}

	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	@Override
	protected String execute() throws Throwable {
		log.info("*******************call site2 task**************");
		List<LinkedHashMap<String, String>> lists = extractPage();
		exportToXls("Site2.xlsx", lists);
		return null;
	}


	/**
	 * 提取所需要的信息
	 * 序号、处罚文号、处罚对象、处罚日期、发布机构、发布日期、名单分类、标题名称、详情
	 */
	private List<LinkedHashMap<String, String>> extractPage() throws Exception {
		List<LinkedHashMap<String, String>> lists = Lists.newLinkedList();
		for (Map.Entry<String, String> entry : cityMap.entrySet()) {
			log.info("city:" + entry.getKey());
			String city = entry.getKey();
			String url = entry.getValue();
			if (StringUtils.isNotEmpty(url)) {
				for (int i = 0; ; i++) {
					String targetUri = url;
					if (i != 0)
						targetUri = String.format(url + "index_%d.html", i);
					log.info("targetUri>>>" + targetUri);
					String fullTxt = "";
					try {
						fullTxt = getData(targetUri);
					} catch (HttpClientErrorException ex) {
						if (ex.getMessage().trim().equals("404 Not Found"))
							break;
					}

					Document doc = Jsoup.parse(fullTxt);

					Elements liElements = doc.getElementsByClass("fl_list").get(0)
							.getElementsByTag("li");
					for (Element li : liElements) {
						LinkedHashMap<String, String> map = Maps.newLinkedHashMap();

						Element aElement = li.getElementsByTag("a").get(0);
						String href = url + aElement.attr("href").substring(2);
						try {
							String hrefContent = getData(targetUri);
							extract(getData(href), map);
						} catch (HttpClientErrorException ex) {
							if (ex.getMessage().trim().equals("404 Not Found"))
								continue;
						} catch (Exception e) {
							log.error(e.getMessage());
							e.printStackTrace();
						}

						String title = aElement.attr("title");
						String releaseDate = li.getElementsByTag("span").get(0).text();
						//标题
						map.put("title", title);
						//发布日期
						map.put("releaseDate", releaseDate);
						//发布机构
						map.put("org", String.format("中国证监会%s监管局", city));
						lists.add(map);
					}
				}
			}
		}
		return lists;
	}


	/**
	 * 提取所需要的信息
	 * 序号、处罚文号、处罚对象、处罚日期、发布机构、发布日期、名单分类、标题名称、详情
	 */
	private void extract(String fullTxt, LinkedHashMap<String, String> map) {
		//兼容异常发生时继续跑
		map.put("punishNo", "");
		map.put("punishObject", "");
		map.put("punishDate", "");
		map.put("detail", "");

		String 根据[] = {"依据《中华人民共和国证券法》", "依据《私募投资基金监督管理暂行办法》"};
		ArrayList<String> filterTags = Lists.newArrayList("<SPAN>", "</SPAN>", "&nbsp;", "　");
		//序号 TODO 需确认
		String seqNo = "";
		seqNo = "";

		//处罚文号
		String punishNo = "";

		//处罚对象
		String punishObject = "";
		ArrayList<String> punishObjects = Lists.newArrayList();

		boolean isOn = false;

		//处罚日期
		String punishDate = "";

		//名单分类 TODO 需确认
		String listType = "";

		//详情
		String detail = "";
		boolean detailIsOn = false;
		ArrayList<String> details = Lists.newArrayList();

		Document doc = Jsoup.parse(fullTxt);
		if (doc.getElementsByTag("title").get(0).text().contains("404"))
			return;

		Elements Ps = doc.getElementsByTag("P");
		for (Element pElement : Ps) {
			//punishNo 处理
			if (Strings.isNullOrEmpty(punishNo)) {
				Elements strongEles = pElement.getElementsByTag("STRONG");
				if (!CollectionUtils.isEmpty(strongEles)) {
					punishNo = strongEles.get(0).text().trim();
//					continue;
				} else {
					punishNo = Strings.isNullOrEmpty(pElement.text()) ? "" : pElement.text().trim();
				}
				// 处罚文号 不存在
				if(punishNo.contains("当事人")) punishNo = "不存在";
			}

			//punishObject 处理
			if (CollectionUtils.isEmpty(punishObjects) || isOn) {
				String pString = filter(pElement.text().trim(), filterTags).trim();
				if (pString.contains("当事人：")) {
					isOn = true;    //打开当事人提取开关
					punishObjects.add(pString);
				}
				if (StringUtils.isNotEmpty(contains(pString, 根据))) {
					isOn = false;
					punishObject = punishObjects.toString().replace("[", "").replace("]", "");
					detailIsOn = true;    //打开详情提取开关
				}
			}

			//punishDate 处理
			{
				String pString = extracterZH(pElement.text().trim());
				if ("年月日".equals(pString))
					punishDate = filter(pElement.text().trim(), filterTags);
			}


			//detail 处理
			if (detailIsOn) {
				details.add(filter(pElement.text().trim(), filterTags));
				if (filter(pElement.text().trim(), filterTags).startsWith("中国证监会")) {
					detailIsOn = false;
					detail = details.toString().replace("[", "").replace("]", "");
				}

			}
		}

		map.put("punishNo", punishNo);
		map.put("punishObject", punishObject);
		map.put("punishDate", punishDate);
		map.put("detail", detail);

	}

	public LinkedHashMap<String, String> initCityMap() {
		if (cityMap.size() > 0) return cityMap;
		cityMap.put("北京", "http://www.csrc.gov.cn/pub/beijing/bjxzcf/");
		cityMap.put("天津", "http://www.csrc.gov.cn/pub/tianjin/xzcf/");
		cityMap.put("河北", "");
		cityMap.put("山西", "http://www.csrc.gov.cn/pub/shanxi/xzcf/");
		cityMap.put("内蒙古", "http://www.csrc.gov.cn/pub/neimenggu/nmgxzcf/");
		cityMap.put("辽宁", "http://www.csrc.gov.cn/pub/liaoning/lnjxzcf/");
		cityMap.put("吉林", "http://www.csrc.gov.cn/pub/jilin/jlxzcf/");
		cityMap.put("黑龙江", "http://www.csrc.gov.cn/pub/heilongjiang/hljjxzcf/");
		cityMap.put("上海", "http://www.csrc.gov.cn/pub/shanghai/xzcf/");
		cityMap.put("江苏", "http://www.csrc.gov.cn/pub/jiangsu/jsxzcf/");
		cityMap.put("浙江", "http://www.csrc.gov.cn/pub/zhejiang/zjxzcf/");
		cityMap.put("安徽", "http://www.csrc.gov.cn/pub/anhui/ahxzcf/");
		cityMap.put("福建", "http://www.csrc.gov.cn/pub/fujian/fjjxzcf/");
		cityMap.put("江西", "http://www.csrc.gov.cn/pub/jiangxi/jxxzcf/");
		cityMap.put("山东", "http://www.csrc.gov.cn/pub/shandong/sdxzcf/");
		cityMap.put("河南", "http://www.csrc.gov.cn/pub/henan/hnxzcf/");
		cityMap.put("湖北", "http://www.csrc.gov.cn/pub/hubei/hbxzcf/");
		cityMap.put("湖南", "http://www.csrc.gov.cn/pub/hunan/hnxzcf/");
		cityMap.put("广东", "http://www.csrc.gov.cn/pub/guangdong/xzcf/");
		cityMap.put("广西", "");
		cityMap.put("海南", "http://www.csrc.gov.cn/pub/hainan/hnjxzcf/");
		cityMap.put("重庆", "http://www.csrc.gov.cn/pub/chongqing/cqjxzcf/");
		cityMap.put("四川", "http://www.csrc.gov.cn/pub/sichuan/scxzcf/");
		cityMap.put("贵州", "http://www.csrc.gov.cn/pub/guizhou/gzxzcf/");
		cityMap.put("云南", "");
		cityMap.put("西藏", "http://www.csrc.gov.cn/pub/xizang/xzxzcf/");
		cityMap.put("陕西", "");
		cityMap.put("甘肃", "");
		cityMap.put("青海", "http://www.csrc.gov.cn/pub/qinghai/qhxzcf/");
		cityMap.put("宁夏", "");
		cityMap.put("新疆", "http://www.csrc.gov.cn/pub/xinjiang/xjxzcf/");
		cityMap.put("深圳", "http://www.csrc.gov.cn/pub/shenzhen/xzcf/");
		cityMap.put("大连", "http://www.csrc.gov.cn/pub/dalian/dlxzcf/");
		cityMap.put("宁波", "http://www.csrc.gov.cn/pub/ningbo/nbxzcf/");
		cityMap.put("厦门", "http://www.csrc.gov.cn/pub/xiamen/xmxzcf/");
		cityMap.put("青岛", "http://www.csrc.gov.cn/pub/qingdao/xzcf/");

		return cityMap;
	}

	/**
	 * txt 是否包含 keys中的记录
	 *
	 * @param txt
	 * @param keys
	 * @return
	 */
	private String contains(String txt, String[] keys) {
		if (Strings.isNullOrEmpty(txt))
			return null;
		for (String key : keys)
			if (txt.contains(key))
				return key;
		return null;
	}
}
