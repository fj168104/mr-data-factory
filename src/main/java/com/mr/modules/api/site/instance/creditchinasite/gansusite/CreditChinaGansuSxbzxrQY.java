package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.mr.common.util.CrawlerUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（甘肃）-失信被执行人(企业)
 * 
 * http://www.gscredit.gov.cn/ssbzxr/legal/list.jspx?isLegal=L&keyword=
 * 
 * @author pxu 2018年7月12日
 */
@Slf4j
@Component("creditchina-gansu-sxbzxr-qy")
@Scope("prototype")
public class CreditChinaGansuSxbzxrQY extends SiteTaskExtend_CreditChina {
	private String baseUrl = CreditChinaSite.GANSU.getBaseUrl();

	@Override
	protected String execute() throws Throwable {
		log.info("抓取“信用中国（甘肃）-失信被执行人”信息开始...");

		String keyWord = SiteParams.map.get("keyWord");// 支持传入关键字进行查询
		if (StrUtil.isEmpty(keyWord)) {
			keyWord = "";
		} else {
			keyWord = URLEncoder.encode(keyWord, "UTF-8");// 对关键字进行UTF-8编码
		}
		HashMap<String, String> urlMap = extractPageUrlList(keyWord);//
		// 抓取列表页面，获取详情页面URL列表
		for (String url : urlMap.keySet()) {
			if (StringUtils.isEmpty(url)) {
				continue;
			}
			try {
				extractContent(url, urlMap.get(url));// 抽取内容并入库
			} catch (Exception e) {
				log.warn("请检查此条url：{}", url, e);
				continue;
			} catch (Throwable e) {
				log.warn("请检查此条url：{}", url, e);
				continue;
			}
		}
		log.info("抓取“信用中国（甘肃）-失信被执行人”信息结束！");
		return null;
	}

	/**
	 * 获取全部的详情页面URL集合
	 * 
	 * @param keyWord
	 *            查询关键字
	 * @return
	 */
	private HashMap<String, String> extractPageUrlList(String keyWord) {
		// 详情页URL做为key，失信领域作为value
		HashMap<String, String> urlMap = new LinkedHashMap<String, String>();

		WebClient wc = CrawlerUtil.createDefaultWebClient();
		wc.getOptions().setJavaScriptEnabled(true);// 启用JS解释器
		try {
			// 解析第一个页面，获取这个页面上下文
			String indexHtml = getData(wc, baseUrl + "/ssbzxr/legal/list.jspx?isLegal=L&keyword=" + keyWord);
			// 获取总页数
			int pageAll = getPageNum(indexHtml);
			int j = 0;
			// 循环遍历所有页获取URL集合
			for (int i = 1; i <= pageAll; i++) {
				Document doc = null;
				if (i == 1) {// 直接解析首页
					doc = Jsoup.parse(indexHtml);
				} else {// 翻页获取URL集合
					String resultHtml = getData(wc, baseUrl + "/ssbzxr/legal/list_" + i + ".jspx?isLegal=L&keyword=" + keyWord);
					doc = Jsoup.parse(resultHtml);
				}
				Elements elementsHerf = doc.getElementsByTag("table").select("tr");
				for (Element element : elementsHerf) {
					Element elementUrl = element.getElementsByTag("a").first();
					if (elementUrl == null) {
						continue;
					}
					String urlInfo = baseUrl + elementUrl.attr("href");// 详情url
					log.info("第" + (++j) + "个链接:" + urlInfo);
					urlMap.put(urlInfo, element.getElementsByTag("td").get(2).text());
				}
			}
		} finally {
			wc.close();
		}
		log.info("总计有效链接个数：" + urlMap.size());
		return urlMap;
	}

	/**
	 * 获取总页数
	 * 
	 * @param document
	 * @return
	 */
	private int getPageNum(String indexHtml) {
		Document doc = Jsoup.parse(indexHtml);
		Element element = doc.getElementsByClass("pages").select("li.count").first();// 获取分页元素
		if (element == null) {
			return 1;
		}
		int pageNum = 1;
		String totalPage = element.text().replace(Jsoup.parse("&nbsp;").text(), "");
		int beginIndex = totalPage.indexOf("共");
		int endIndex = totalPage.indexOf("页");
		if (totalPage.length() > 0 && beginIndex != -1 && endIndex > beginIndex + 1) {
			pageNum = Integer.valueOf(totalPage.substring(beginIndex + 1, endIndex).trim());
		}
		log.debug("==============================");
		log.debug("总页数为：" + pageNum);
		log.debug("==============================");
		return pageNum;
	}

	/**
	 * 获取网页内容,封装对象
	 */
	public void extractContent(String url, String discreditType) throws Throwable {
		WebClient wc = CrawlerUtil.createDefaultWebClient();
		wc.getOptions().setJavaScriptEnabled(true);// 启用JS解释器
		try {
			String contentHtml = getData(wc, url);
			Document doc = Jsoup.parse(contentHtml);

			log.debug("==============================");
			log.debug("url={}", url);

			Elements li = doc.getElementById("company_detail").select("ul > li");
			if (li != null && li.size() > 0) {
				DiscreditBlacklist blackList = createDefaultDiscreditBlacklist(url);
				blackList.setSubject("失信被执行人查询");
				blackList.setDiscreditType(discreditType);
				StringBuilder punishReason = new StringBuilder();
				for (int i = 0; i < li.size(); i++) {
					Element nameEle = li.get(i).getElementsByClass("tab1-p-left").first();
					Element valueEle = li.get(i).getElementsByClass("tab1-p-right").first();
					if (nameEle == null || valueEle == null) {
						continue;
					}
					String name = nameEle.text();
					String value = valueEle.text();
					log.debug(name + "===" + value);
					switch (name) {
						case "主体名称：":
							blackList.setEnterpriseName(value);
							break;
						case "案件标识：":
							break;
						case "统一社会信用代码：":
							blackList.setEnterpriseCode1(value);
							break;
						case "登记证号：":
							blackList.setEnterpriseCode2(value);
							break;
						case "案号：":
							blackList.setJudgeNo(value);
							break;
						case "组织机构代码：":
							blackList.setEnterpriseCode3(value);
							break;
						case "被执行人序号：":
							break;
						case "法定代表人：":
							blackList.setPersonName(value);
							break;
						case "法院代码：":
							break;
						case "裁判作出机构/执行法院：":
						case "执行法院名称：":
							blackList.setJudgeAuth(value);
							break;
						case "税务登记证号：":
							blackList.setEnterpriseCode4(value);
							break;
						case "立案时间：":
							blackList.setJudgeDate(value);
							break;
						case "执行案由：":
						case "执行依据文号：":
						case "执行依据制作单位：":
						case "执行依据主文：":
							punishReason.append(name).append(value).append("，");
							break;
						case "罪名：":
						case "履行情况：":
							blackList.setDiscreditAction(value);
							break;
						case "失信行为情形：":
							break;
						case "失信状态：":
							blackList.setStatus(value);
							break;
						case "失信名单类别：":
							blackList.setDiscreditType(discreditType);
							break;

						default:
							break;
					}
				}
				blackList.setPunishReason(punishReason.toString());
				saveDisneycreditBlackListOne(blackList,false);

			}
		} finally {
			wc.close();
		}
		log.debug("==============================");
	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist(String url) {
		Date nowDate = new Date();
		DiscreditBlacklist blackList = new DiscreditBlacklist();
		blackList.setCreatedAt(nowDate);// 本条记录创建时间
		blackList.setUpdatedAt(nowDate);// 本条记录最后更新时间
		blackList.setSource(CreditChinaSite.GANSU.getSiteName());// 数据来源
		blackList.setSubject("");// 主题
		blackList.setUrl(url);// url
		blackList.setObjectType("01");// 主体类型: 01-企业 02-个人。默认为企业
		blackList.setEnterpriseName("");// 企业名称
		blackList.setEnterpriseCode1("");// 统一社会信用代码
		blackList.setEnterpriseCode2("");// 营业执照注册号
		blackList.setEnterpriseCode3("");// 组织机构代码
		blackList.setEnterpriseCode4("");// 税务登记号
		blackList.setPersonName("");// 法定代表人/负责人姓名|负责人姓名
		blackList.setPersonId("");// 法定代表人身份证号|负责人身份证号
		blackList.setDiscreditType("");// 失信类型
		blackList.setDiscreditAction("");// 失信行为
		blackList.setPunishReason("");// 列入原因
		blackList.setPunishResult("");// 处罚结果
		blackList.setJudgeNo("");// 执行文号
		blackList.setJudgeDate("");// 执行时间
		blackList.setJudgeAuth("");// 判决机关
		blackList.setPublishDate("");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
