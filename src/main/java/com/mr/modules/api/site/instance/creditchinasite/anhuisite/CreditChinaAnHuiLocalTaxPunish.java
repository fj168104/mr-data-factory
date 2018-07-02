package com.mr.modules.api.site.instance.creditchinasite.anhuisite;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mr.modules.api.model.T03TaxPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（安徽）-省地方税务局-重大税收违法案件信息（法人）
 * 
 * http://www.creditah.gov.cn/Black.htm-->http://www.creditah.gov.cn/remote/1481/index.htm
 * 
 * @author pxu 2018年6月11日
 */
@Slf4j
@Component("creditchina-anhui-l-tax")
@Scope("prototype")
public class CreditChinaAnHuiLocalTaxPunish extends SiteTaskExtend_CreditChina {
	@Override
	protected String execute() throws Throwable {
		log.info("抓取“信用中国（安徽）-省地方税务局-重大税收违法案件信息（法人）”信息开始...");
		HashSet<String> urlSet = extractPageUrlList();
		for (String url : urlSet) {
			T03TaxPunish tax = extractContent(url);
			try {// 数据入库

			} catch (Exception e) {
				writeBizErrorLog(url, "请检查此条url：" + "\n" + e.getMessage());
				continue;
			}
		}
		log.info("抓取“信用中国（安徽）-省地方税务局-重大税收违法案件信息（法人）”信息结束！");
		return null;
	}

	/**
	 * 获取全部的详情页面URL集合
	 * 
	 * @return
	 */
	private HashSet<String> extractPageUrlList() {
		HashSet<String> urlSet = new LinkedHashSet<String>();

		String baseUrl = CreditChinaSite.ANHUI.getBaseUrl();
		// 解析第一个页面，获取这个页面上下文
		String indexHtml = getData(baseUrl + "/remote/1481/index.htm");// http://www.creditah.gov.cn/remote/1481/index.htm
		// 获取总页数
		int pageAll = getPageNum(indexHtml);
		int j = 0;
		// 循环遍历所有页获取URL集合
		for (int i = 1; i <= pageAll; i++) {
			String url = baseUrl + "/remote/1481/index_" + i + ".htm";// 分页列表页面
			String resultTxt = getData(url);
			Document doc = Jsoup.parse(resultTxt);
			Elements elementsHerf = doc.select(".right-box").select("table").select("div:has(a)");
			for (Element element : elementsHerf) {
				Element elementUrl = element.getElementsByTag("a").get(0);
				String urlInfo = baseUrl + elementUrl.attr("href");// 详情url
				log.info("第" + (++j) + "个链接:" + urlInfo);
				urlSet.add(urlInfo);
			}
		}
		return urlSet;
	}

	/**
	 * @param indexHtml
	 * @return
	 */
	private int getPageNum(String indexHtml) {
		int pageNum = 1;
		Document doc = Jsoup.parse(indexHtml);
		Element element = doc.getElementsByClass("allPage").first();
		String totalPage = element.text();
		if (totalPage.length() > 0) {
			pageNum = Integer.valueOf(totalPage);
		}
		log.debug("==============================");
		log.debug("总页数为：" + pageNum);
		log.debug("==============================");
		return pageNum;
	}

	/**
	 * 获取网页内容,封装对象
	 */
	public T03TaxPunish extractContent(String url) throws Throwable {
		String contentHtml = getData(url);
		Document doc = Jsoup.parse(contentHtml);
		
		log.debug("==============================");
		
		T03TaxPunish tax = new T03TaxPunish();
		tax.setPublishSource("安徽省地方税务局");
		tax.setUrl(url);
		tax.setDataSource(CreditChinaSite.ANHUI.getSiteName());
		
		Elements trs = doc.getElementsByClass("infor").select("tr");
		for (int i = 0; i < trs.size(); i++) {
			Elements tds = trs.get(i).select("td");
			String name = tds.get(0).text();
			String value = tds.get(1).text();
			log.debug(name + "===" + value);
			if (Objects.equals(name, "企业名称")) {
				tax.setTaxName(value);
			} else if (Objects.equals(name, "统一社会信用代码")) {
				tax.setCreditNo(value);
			} else if (Objects.equals(name, "纳税人识别号")) {
				tax.setTaxId(value);
			} else if (Objects.equals(name, "法定代表人姓名")) {
				tax.setFrName(value);
			} else if (Objects.equals(name, "案件性质")) {
				tax.setCaseNature(value);
			} else if (Objects.equals(name, "组织机构代码")) {
				tax.setOrgCode(value);
			} else if (Objects.equals(name, "性别")) {
				tax.setFrSex(value);
			} else if (Objects.equals(name, "主要违法事实")) {
				tax.setIllegFact(value);
			} else if (Objects.equals(name, "处罚依据")) {
				tax.setPenBasis(value);
			} else if (Objects.equals(name, "处罚结果")) {
				tax.setPenResult(value);
			} else if (Objects.equals(name, "公布日期")) {
				tax.setPublishDate(value);
			} else if (Objects.equals(name, "撤销日期")) {
				tax.setCancelDate(value);
			} else if (Objects.equals(name, "注销原因")) {
				tax.setCancelReason(value);
			} else if (Objects.equals(name, "实施检查单位")) {
				tax.setInspectOrg(value);
			}
		}
		log.debug(url);
		log.debug(tax.toString());
		log.debug("==============================");
		return tax;
	}
}
