package com.mr.modules.api.site.instance;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import io.jsonwebtoken.lang.Assert;
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

	private static ArrayList<String> pdfOrDocType = Lists.newArrayList("天津");
	protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);

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
		List<FinanceMonitorPunish> lists = extractPage();
		if (!CollectionUtils.isEmpty(lists)) {
			exportToXls("Site2.xlsx", lists);
		}

		return null;
	}

	@Override
	protected String executeOne() throws Throwable {
		log.info("*******************call site2 task for One Record**************");

//		if (org.springframework.util.StringUtils.isEmpty(oneFinanceMonitorPunish.getPartyInstitution())) {
//			oneFinanceMonitorPunish.setPartyInstitution(oneFinanceMonitorPunish.getPartyPerson());
//			oneFinanceMonitorPunish.setPartyPerson(oneFinanceMonitorPunish.getPartyPerson());
//		} else {
//			oneFinanceMonitorPunish.setPartyInstitution(oneFinanceMonitorPunish.getPunishInstitution());
//			oneFinanceMonitorPunish.setPartyPerson(oneFinanceMonitorPunish.getPunishInstitution());
//		}

		Assert.notNull(oneFinanceMonitorPunish.getPunishTitle());
		Assert.notNull(oneFinanceMonitorPunish.getPublishDate());
		Assert.notNull(oneFinanceMonitorPunish.getRegion());
		Assert.notNull(oneFinanceMonitorPunish.getUrl());
		oneFinanceMonitorPunish.setPublisher(String.format("中国证监会%s监管局",
				oneFinanceMonitorPunish.getRegion()));
		oneFinanceMonitorPunish.setPunishInstitution(String.format("中国证监会%s监管局",
				oneFinanceMonitorPunish.getRegion()));

		oneFinanceMonitorPunish.setSource("地方证监局");
		oneFinanceMonitorPunish.setObject("行政处罚决定");

		//通过source查找
		FinanceMonitorPunish originFinanceMonitorPunish = financeMonitorPunishMapper
				.selectByUrl(oneFinanceMonitorPunish.getUrl());
		if (!Objects.isNull(oneFinanceMonitorPunish)) {
			oneFinanceMonitorPunish.setCreateTime(originFinanceMonitorPunish.getCreateTime());
			oneFinanceMonitorPunish.setUpdateTime(new Date());
		}

		initDate();
		doFetch(oneFinanceMonitorPunish, true);
		return null;
	}


	/**
	 * 提取所需要的信息
	 * 序号、处罚文号、处罚对象、处罚日期、发布机构、发布日期、名单分类、标题名称、详情
	 */
	private List<FinanceMonitorPunish> extractPage() throws Exception {
		List<FinanceMonitorPunish> lists = Lists.newLinkedList();
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
						fullTxt = getData(targetUri, 1);
					} catch (RuntimeException ex) {
						if (ex instanceof HttpClientErrorException && ex.getMessage().trim().equals("404 Not Found"))
							break;
						else throw ex;
					}

					Document doc = Jsoup.parse(fullTxt);

					Elements liElements = doc.getElementsByClass("fl_list").get(0)
							.getElementsByTag("li");
					for (Element li : liElements) {
						FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();

						Element aElement = li.getElementsByTag("a").get(0);
						String title = StringUtils.isEmpty(aElement.attr("title"))
								? aElement.text() : aElement.attr("title");
						String releaseDate = li.getElementsByTag("span").first().text();
						String href = url + aElement.attr("href").substring(2);

						financeMonitorPunish.setPunishTitle(title);
						//根据title提取的punishNo
						{
							String punishNoTmp = null;
							if (title.contains("20")) {
								punishNoTmp = title.substring(title.indexOf("20") - 1);
								if (punishNoTmp.contains("号")) {
									punishNoTmp.substring(0, punishNoTmp.indexOf("号") + 1);
									punishNoTmp.replace("（", "")
											.replace("）", "")
											.replace(" ", "")
											.trim();
									financeMonitorPunish.setPunishNo(punishNoTmp);
								}

							}
						}

						financeMonitorPunish.setPublishDate(releaseDate);
						financeMonitorPunish.setPublisher(String.format("中国证监会%s监管局", city));
						financeMonitorPunish.setPunishInstitution(String.format("中国证监会%s监管局", city));
						financeMonitorPunish.setUrl(href);
						financeMonitorPunish.setRegion(city);
						financeMonitorPunish.setSource("地方证监局");
						financeMonitorPunish.setObject("行政处罚决定");

						if (!doFetch(financeMonitorPunish, false)) {
							return lists;
						}
						lists.add(financeMonitorPunish);
					}
				}
			}
		}
		return lists;
	}

	/**
	 * 抓取并解析单条数据
	 * map[city; title; releaseDate; org; url]
	 *
	 * @param financeMonitorPunish
	 */
	private boolean doFetch(FinanceMonitorPunish financeMonitorPunish,
							Boolean isForce) {
		String url = financeMonitorPunish.getUrl();

		try {
			extract(getData(url, 1), financeMonitorPunish);
			return saveOne(financeMonitorPunish, isForce);
		} catch (RuntimeException ex) {
			if (ex instanceof HttpClientErrorException && ex.getMessage().trim().equals("404 Not Found"))
				return true;
			else
				ex.printStackTrace();
		} catch (Throwable e) {
			log.error(e.getMessage());
		}
		return true;


	}

	/**
	 * 提取所需要的信息
	 * 序号、处罚文号、处罚对象、处罚日期、发布机构、发布日期、名单分类、标题名称、详情
	 */
	private void extract(String fullTxt, FinanceMonitorPunish financeMonitorPunish) {

		String 根据[] = {"根据《中华人民共和国证券法》",
				"依照《中华人民共和国证券法》",
				"依据《中华人民共和国证券法》",
				"依据《私募投资基金监督管理暂行办法》",
				"根据2006年1月1日起施行的《中华人民共和国证券法》",
				"依据中国证监会《私募投资基金监督管理暂行办法》",
				"依据《期货交易管理条例》",
				"依据2004年6月1日起实施的《中华人民共和国证券投资基金法》",
				"依据2012年12月28日修订并于2013年6月1日起施行的《中华人民共和国证券投资基金法》"};
		ArrayList<String> filterTags = Lists.newArrayList("<SPAN>", "</SPAN>", "&nbsp;", "　", "<BR>");

		String city = financeMonitorPunish.getRegion();
		//仅仅天津是pdf,doc
		boolean isHtml = !pdfOrDocType.contains(city);
		if (!fullTxt.contains("当事人") && fullTxt.contains(".pdf")) {
			isHtml = false;
		}

		//处罚文号
		String punishNo = "";

		//处罚对象
		String punishObject = "";
		ArrayList<String> punishObjects = Lists.newArrayList();

		boolean isOn = false;

		//处罚日期
		String punishDate = null;

		//名单分类 TODO 需确认
		String listType = "";

		//详情
		String detail = "";
		boolean detailIsOn = false;
		ArrayList<String> details = Lists.newArrayList();

		Document doc = Jsoup.parse(fullTxt);

		String[] zjh = {"中国证监会", city + "证监局"};
		String zjhStr = "";
		int zjhIndex = -1;
		for (int i = 0; i < zjh.length; i++) {
			if (fullTxt.indexOf(zjh[i]) > -1) {
				zjhStr = zjh[i];
				zjhIndex = fullTxt.indexOf(zjh[i]);
				break;
			}
		}

		String[] p = {"谢锦芬，", "当事人：", "当事人", "当事人:"};
		String pStr = "";
		int pIndex = -1;
		for (int i = 0; i < p.length; i++) {
			if (fullTxt.indexOf(p[i]) > -1) {
				pStr = p[i];
				pIndex = fullTxt.indexOf(p[i]);
				break;
			}
		}


		if (isHtml) {
			Element classPunishNoEl = doc.getElementsByClass("title").first();
			if (StringUtils.isEmpty(financeMonitorPunish.getPunishNo()) &&
					!Objects.isNull(classPunishNoEl)) {
				String tmp = classPunishNoEl.text();
				if (tmp.contains("20") && tmp.indexOf("20") < tmp.indexOf("号")) {
					financeMonitorPunish.setPunishNo(tmp.substring(tmp.indexOf("20") - 1, tmp.indexOf("号") + 1));
				}
			}


			if (doc.getElementsByTag("title").first().text().contains("404"))
				return;
			Elements Ps = doc.getElementsByTag("P");
			if (city.equals("贵州"))
				Ps = doc.getElementsByTag("SPAN");

			for (Element pElement : Ps) {
				if (city.equals("贵州") && !Ps.text().contains(pStr) && !isOn)
					continue;

				//punishNo 处理
				if (Strings.isNullOrEmpty(punishNo)) {
//					Elements strongEles = pElement.getElementsByTag("STRONG");
//					if (!CollectionUtils.isEmpty(strongEles)) {
//						punishNo = strongEles.get(0).text().trim();
////					continue;
//					} else {
//						punishNo = Strings.isNullOrEmpty(pElement.text()) ? "" : pElement.text().trim();
//					}
					punishNo += pElement.text();

					// 处罚文号 不存在
					if (punishNo.contains(pStr)) punishNo = "";
				}

				//punishObject 处理
				if (CollectionUtils.isEmpty(punishObjects) || isOn) {
					String pString = filter(pElement.text().trim(), filterTags).trim();
					if (pString.contains(pStr) && StringUtils.isNotEmpty(contains(pString, 根据))) {
						punishObject = pString.substring(0, pString.indexOf(contains(pString, 根据)));
						if (zjhIndex > -1) {
							detail = pString.substring(pString.indexOf(contains(pString, 根据)),
									pString.length() > zjhIndex ? zjhIndex : pString.length());
							if (zjhIndex + 10 > pString.length()) {
								punishDate = null;
							} else {
								punishDate = filter(pString.substring(zjhIndex + 10), filterTags);
							}

						}
						break;
					}

					if (pString.contains(pStr)) {
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
					if ("年月日".equals(pString) || (pElement.text().contains("二〇")))
						punishDate = filter(pElement.text().trim(), filterTags);
				}


				//detail 处理
				if (detailIsOn) {
					details.add(filter(pElement.text().trim(), filterTags));
					if (filter(pElement.text().trim(), filterTags).startsWith(zjhStr)) {
						detailIsOn = false;
						detail = details.toString().replace("[", "").replace("]", "");
					}

				}
			}

			if (Strings.isNullOrEmpty(detail)) {
				detail = details.toString().replace("[", "").replace("]", "");
			}
		} else {
			String text = fullTxt.substring(fullTxt.indexOf("var file_appendix"));
			String href = financeMonitorPunish.getUrl().substring(0, financeMonitorPunish.getUrl().lastIndexOf("/"))
					+ text.substring(text.indexOf("href=\"."), text.indexOf("\">"))
					.replace("href=\".", "'")
					.replace("'", "");
			String content = "";
			try {
				String fileName = downLoadFile(href);
				if (fileName.toLowerCase().endsWith("doc")) {
					content = ocrUtil.getTextFromDoc(fileName);
				} else if (fileName.toLowerCase().endsWith("pdf")) {
					content = ocrUtil.getTextFromImg(fileName);
				} else {
					log.warn("url{} is not doc or pdf", content);
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
				return;
			}

			//punishNo 处理
			if (content.contains("20") && content.indexOf("20") < content.indexOf("号")) {
				String tmp = content.substring(content.indexOf("20") - 1, content.indexOf("号") + 1);
				if (tmp.length() > 5 && tmp.length() < 15) {
					punishNo = tmp;
				}
			}


			String[] zjh1 = {"中国证监会", "中国证券监督管理委员会", city + "证监会", city + "监管局"};
			String zjhStr1 = "";
			int zjhIndex1 = -1;
			for (int i = 0; i < zjh1.length; i++) {
				if (content.lastIndexOf(zjh1[i]) > -1) {
					zjhStr1 = zjh1[i];
					zjhIndex1 = content.lastIndexOf(zjh1[i]);
					break;
				}
			}

			if (StringUtils.isNotEmpty(content)) {

				if (content.contains(pStr) && content.contains("依据")) {

					punishObject = content.substring(content.indexOf(pStr), content.indexOf("依据"));
					if (zjhIndex1 > -1) {
						punishDate = content.substring(zjhIndex1).replace(zjhStr1, "").trim();
						if (punishDate.length() > 15) {
							log.warn("punishDate:{}无法解析", punishDate);
							punishDate = null;
						}
						detail = content.substring(content.indexOf("依据"), zjhIndex1);
					} else {
						detail = content.substring(content.indexOf("依据"));
					}
				}
			}

		}

		if (org.springframework.util.StringUtils.isEmpty(financeMonitorPunish.getPunishNo())) {
			if (punishNo.contains("号")) {
				punishNo = punishNo.substring(0, punishNo.indexOf("号") + 1);
			}
			financeMonitorPunish.setPunishNo(punishNo.trim());
		}
		if (StringUtils.isNotEmpty(financeMonitorPunish.getPunishNo())) {
			String pTmp = financeMonitorPunish.getPunishNo();
			if (pTmp.lastIndexOf("（") > pTmp.indexOf("号")) {
				pTmp = pTmp.substring(0, pTmp.lastIndexOf("（"));
			} else if (pTmp.lastIndexOf("(") > pTmp.indexOf("号")) {
				pTmp = pTmp.substring(0, pTmp.lastIndexOf("("));
			}
			financeMonitorPunish.setPunishNo(pTmp);
		}

		if (!Objects.isNull(financeMonitorPunish.getPunishNo()) &&
				!financeMonitorPunish.getPunishNo().contains("20")
				|| financeMonitorPunish.getPunishNo().length() > 10) {
			financeMonitorPunish.setPunishNo(null);
		}

		//当事人
		punishObject = punishObject.replace(pStr, "");
		String partyPerson = "";
		String partyInstitution = "";

		for (String ps : punishObject.split("。")) {
			if (StringUtils.isEmpty(ps)) continue;
			if (ps.contains("出生")
					|| ps.contains("住址")
					|| ps.contains("时任")
					|| ps.contains("现居住于")
					|| ps.contains("生，")) {
				if (ps.contains("，男")) {
					partyPerson += "," + ps.substring(0, ps.indexOf("，男"));
				} else if (ps.contains("，女")) {
					partyPerson += "," + ps.substring(0, ps.indexOf("，女"));
				}
			} else {
				partyInstitution += ps;

			}
		}

		if (StringUtils.isNotEmpty(partyPerson)) {
			partyPerson = partyPerson.substring(1).replace("\n", "").trim();
			if (partyPerson.contains("，女")) partyPerson = partyPerson.substring(0, partyPerson.indexOf("，女"));
			if (partyPerson.contains("，男")) partyPerson = partyPerson.substring(0, partyPerson.indexOf("，男"));

			if (partyPerson.contains("公司")) {
				partyInstitution = partyPerson;
				partyPerson = null;
			} else {
				financeMonitorPunish.setPartyInstitution(null);
			}

			financeMonitorPunish.setPartyPerson(partyPerson);
		}

		if (StringUtils.isNotEmpty(partyInstitution)) {
			if (partyInstitution.contains("，住所")) {
				partyInstitution = partyInstitution.substring(0, partyInstitution.indexOf("，住所"));
			} else if (partyInstitution.contains("，注册地")) {
				partyInstitution = partyInstitution.substring(0, partyInstitution.indexOf("，注册地"));
			} else if (partyInstitution.contains("）注册地")) {
				partyInstitution = partyInstitution.substring(0, partyInstitution.indexOf("）注册地"));
			} else if (partyInstitution.contains("）注册地")) {
				partyInstitution = partyInstitution.substring(0, partyInstitution.indexOf("）注册地"));
			}

			if (partyInstitution.contains("，女") || partyInstitution.contains("，男")) {
				financeMonitorPunish.setPartyPerson(partyInstitution.trim());
				partyInstitution = null;
			} else {
				financeMonitorPunish.setPartyPerson(null);
			}

			financeMonitorPunish.setPartyInstitution(partyInstitution);
		}

		//处罚日期
		if (StringUtils.isNotEmpty(punishDate)) {
			financeMonitorPunish.setPunishDate(punishDate.replace("'", "").trim());
		}


		//解析 违规情况 相关法规 处罚结果 监管类型
		String irregularities = null;
		String relatedLaw = null;
		String punishResult = null;
		String listClassification = "行政处罚";
		String detailAll = "";
		if (isHtml) {
			detailAll = doc.text();
		} else {
			detailAll = detail;
		}

		String[] p1 = {"谢锦芬，", "当事人：", "当事人", "当事人:"};
		String p1Str = "";
		int p1Index = -1;
		for (int i = 0; i < p1.length; i++) {
			if (detailAll.indexOf(p1[i]) > -1) {
				p1Str = p1[i];
				p1Index = detailAll.indexOf(p1[i]);
				break;
			}
		}
		if (p1Index > -1)
			detailAll = detailAll.substring(p1Index);


		String[] zjhDetail = {"中国证监会", "中国证券监督管理委员会", city + "证监会", city + "监管局"};
		;
		String zjhDetailStr = "";
		int zjhDetailIndex = -1;
		for (int i = 0; i < zjhDetail.length; i++) {
			if (detailAll.lastIndexOf(zjhDetail[i]) > -1) {
				zjhDetailStr = zjhDetail[i];
				zjhDetailIndex = detailAll.lastIndexOf(zjhDetail[i]);
				break;
			}
		}

		//以上事实 关键字
		String factStr = "";
		int factIndex = -1;
		String[] fact = {"以上事实：", "上述违法事实", "以上事实，",
				"以上情况有相关账户的开户", "上述事实",
				"以上违法事实", "经复核，本局认为", "本局认为", "上述事实，"};
		for (int i = 0; i < fact.length; i++) {
			if (detailAll.indexOf(fact[i]) > -1) {
				factStr = fact[i];
				factIndex = detailAll.lastIndexOf(fact[i]);
				break;
			}
		}

		//违法行为的事实 关键字
		String wfssStr = "";
		int wfssIndex = -1;
		String[] wfss = {"根据当事人违法行为的事实", "本局决定", "我局决定"};
		for (int i = 0; i < wfss.length; i++) {
			if (detailAll.indexOf(wfss[i]) > -1) {
				wfssStr = wfss[i];
				wfssIndex = detailAll.lastIndexOf(wfss[i]);
				break;
			}
		}

		if (zjhDetailIndex > -1) {
			financeMonitorPunish.setDetails(detailAll.substring(0, zjhDetailIndex));
		}
		if (StringUtils.isEmpty(financeMonitorPunish.getPunishDate()) && detailAll.lastIndexOf("二○") > -1) {
			String tmp = detailAll.substring(detailAll.lastIndexOf("二○"));
			financeMonitorPunish.setPunishDate(tmp.substring(0, tmp.indexOf("日")));
		}


		//irregularities
		if (StringUtils.isEmpty(detailAll)) return;
		{
			if (detailAll.indexOf("经查") > -1 && detailAll.indexOf("：") > -1 && factIndex > -1) {
				String tmp = detailAll.substring(detailAll.indexOf("经查") + 4);
				irregularities = tmp.substring(detailAll.indexOf("：") + 1, factIndex);
			}
		}

		//relatedLaw
		{
			if (detailAll.contains("违反了")) {
				String tmp = detailAll.substring(detailAll.lastIndexOf("违反了"));
				if (tmp.indexOf("。") > -1)
					relatedLaw = tmp.substring(0, tmp.indexOf("。"));
			} else if (detailAll.contains("违反")) {
				String tmp = detailAll.substring(detailAll.lastIndexOf("违反"));
				if (tmp.indexOf("。") > -1)
					relatedLaw = tmp.substring(0, tmp.indexOf("。"));
			}

		}

		//punishResult
		{
			if (wfssIndex > -1 && wfssIndex < zjhDetailIndex) {
				punishResult = detailAll.substring(wfssIndex, zjhDetailIndex);
			}
		}

		financeMonitorPunish.setIrregularities(irregularities);
		financeMonitorPunish.setRelatedLaw(relatedLaw);
		financeMonitorPunish.setPunishResult(punishResult);
		financeMonitorPunish.setListClassification(listClassification);

	}

	public LinkedHashMap<String, String> initCityMap() {
		if (cityMap.size() > 0) return cityMap;
		cityMap.put("北京", "http://www.csrc.gov.cn/pub/beijing/bjxzcf/");

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

		cityMap.put("天津", "http://www.csrc.gov.cn/pub/tianjin/xzcf/");

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
