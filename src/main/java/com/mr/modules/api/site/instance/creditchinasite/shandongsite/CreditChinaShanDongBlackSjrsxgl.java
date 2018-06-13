package com.mr.modules.api.site.instance.creditchinasite.shandongsite;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（山东）-涉金融失信关联黑名单
 * 
 * http://www.creditsd.gov.cn/creditsearch.redlist.dhtml?source_id=313&type=black
 * 
 * @author pxu 2018年6月11日
 */
@Slf4j
@Component("creditchina-shandong-black-sjrsxgl")
@Scope("prototype")
public class CreditChinaShanDongBlackSjrsxgl extends SiteTaskExtend_CreditChina {
	@Override
	protected String execute() throws Throwable {
		log.info("抓取“信用中国（山东）-涉金融失信关联黑名单”信息开始...");

		HashSet<String> urlSet = extractPageUrlList();
		for (String url : urlSet) {
			extractContent(url);
			try {// 数据入库

			} catch (Exception e) {
				writeBizErrorLog(url, "请检查此条url：" + "\n" + e.getMessage());
				continue;
			}
		}
		log.info("抓取“信用中国（山东）-涉金融失信关联黑名单”信息结束！");
		return null;
	}

	/**
	 * 获取全部的详情页面URL集合
	 * 
	 * @return
	 */
	private HashSet<String> extractPageUrlList() {
		HashSet<String> urlSet = new LinkedHashSet<String>();

		String baseUrl = CreditChinaSite.SHANDONG.getBaseUrl();
		// 解析第一个页面，获取这个页面上下文
		String indexHtml = getData(baseUrl + "/creditsearch.redlist.dhtml?source_id=313&kw=&page=1");
		// 获取总页数
		int pageAll = getPageNum(indexHtml);
		int j = 0;
		// 循环遍历所有页获取URL集合
		for (int i = 1; i <= pageAll; i++) {
			Document doc = null;
			if (i == 1) {// 直接解析首页
				doc = Jsoup.parse(indexHtml);
			} else {// 翻页获取URL集合
				String resultHtml = getData(baseUrl + "/creditsearch.redlist.dhtml?source_id=313&kw=&page=" + i);
				doc = Jsoup.parse(resultHtml);
			}
			Elements elementsHerf = doc.select("div.right-content").select("table").select("tr:has(a)");
			for (Element element : elementsHerf) {
				Element elementUrl = element.getElementsByTag("a").first();
				String urlInfo = baseUrl + elementUrl.attr("href");// 详情url
				log.info("第" + (++j) + "个链接:" + urlInfo);
				urlSet.add(urlInfo);
			}
		}
		log.info("总计有效链接个数：" + urlSet.size());
		return urlSet;
	}

	/**
	 * @param document
	 * @return
	 */
	private int getPageNum(String indexHtml) {
		int pageNum = 1;
		Document doc = Jsoup.parse(indexHtml);
		Element element = doc.getElementsByClass("pagination").select("a[class='disabled']").first();// 获取分页元素
		String totalPage = element.text().replace(Jsoup.parse("&nbsp;").text(), "");
		int beginIndex = totalPage.indexOf("/");
		int endIndex = totalPage.indexOf("共");
		if (totalPage.length() > 0 && beginIndex > 0 && endIndex > beginIndex + 1) {
			pageNum = Integer.valueOf(totalPage.substring(beginIndex + 1, endIndex));
		}
		log.debug("==============================");
		log.debug("总页数为：" + pageNum);
		log.debug("==============================");
		return pageNum;
	}

	/**
	 * 获取网页内容,封装对象
	 */
	public void extractContent(String url) throws Throwable {
		String contentHtml = getData(url);
		Document doc = Jsoup.parse(contentHtml);

		log.debug("==============================");
		log.debug("url={}", url);

		Elements trs = doc.getElementsByClass("_main_content").select("tr");
		for (int i = 0; i < trs.size(); i++) {
			Element th = trs.get(i).select("th").first();
			Element td = trs.get(i).select("td").first();

			String name = th.text();
			String value = td.text();
			log.debug(name + "===" + value);
			if (Objects.equals(name, "企业名称")) {

			} else if (Objects.equals(name, "法定代表人")) {

			} else if (Objects.equals(name, "统一社会信用代码")) {

			} else if (Objects.equals(name, "组织机构代码")) {

			} else if (Objects.equals(name, "公示原因")) {

			} else if (Objects.equals(name, "判决机关")) {

			} else if (Objects.equals(name, "失联企业性质类型")) {

			}
		}
		log.debug("==============================");
	}
}
