package com.mr.modules.api.site.instance.creditchinasite.guizhousite;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Maps;
import com.mr.common.IdempotentOperator;
import com.mr.framework.core.collection.CollectionUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.net.URLCodec;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.xpath.operations.Bool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.html.HTMLIFrameElement;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 来源：信用中国（贵州）
 * 主题：行政处罚
 * 属性：处罚文书号 处罚类别 处罚结果 处罚事由 处罚依据 处罚决定日期 处罚有效期
 * 地址：http://www.gzcx.gov.cn/a/xinxishuanggongshi/shuanggongshichaxun/?creditCorpusCode=S&sgstype=xzcf&sgskeywords=
 * RESULT_URL:http://202.98.195.12:7809/CreditWebApi/f/sgs/xzcfxx/list?accessKey=556d8122421340ae8799bae4ad5c03a1&sid=eqk1koj1i23e72scs7f6osq4k3&keywords=
 * 注：关键字 进行了URLencord.encode(keyWord,"utf-8")两次转码 如：String str = URLEncoder.encode(URLEncoder.encode("贵州松河公司松河煤矿","utf-8"),"utf-8");
 */
@Slf4j
@Component("guizhou_xzcf")
@Scope("prototype")
public class GuiZhou_XZCF extends SiteTaskExtend_CreditChina {
	private String url = "http://www.gzcx.gov.cn/a/xinxishuanggongshi/";
//	private String listUrl = "http://202.98.195.12:7809/CreditWebApi/f/sgs/xzcfxx/list?" +
//			"accessKey=556d8122421340ae8799bae4ad5c03a1&sid=bo2jbfvecte32biererdln2533&areacode=%s&areatype=4&";

	private String listUrl = "http://202.98.195.12:7809/CreditWebApi/f/sgs/xzcfxx/list";

	private String detailUrl = "http://service.gzcx.gov.cn:7809/CreditWebApi/%s";
	@Value("${download-dir}")
	private String downloadDir;

	//可能会变，变得时候再调整吧
	private static String SID = "uh3jdmu3ndh6kiv15n22sje4d7";

	@Override
	protected String executeOne() throws Throwable {
		return super.executeOne();
	}

	@Override
	protected String execute() throws Throwable {
		try {
			extractContext(url);
		} catch (Exception e) {
			e.printStackTrace();
			writeBizErrorLog(url, e.getMessage());
		}
		return null;
	}

	private class ElementCaller implements Callable<Boolean> {
		private Element listElement;
		private CountDownLatch countDownLatch;
		private int i;

		public ElementCaller(Element listElement, CountDownLatch countDownLatch, int i) {
			this.listElement = listElement;
			this.countDownLatch = countDownLatch;
			this.i = i;
		}

		@Override
		public Boolean call() throws Exception {
			SID = getSID();
			Boolean result = false;
			try {
				Elements liElements = listElement.getElementsByTag("li");
				for (int j = 0; j < liElements.size(); j++) {
					Element liElement = liElements.get(j);
					String onclick = liElement.attr("onclick");
					if (StrUtil.isNotEmpty(onclick)) {
						String code = onclick.substring(onclick.lastIndexOf(",") + 1)
								.replace("'", "").replace(")", "");
						String address = liElement.text();
//					String sListUrl = String.format(listUrl, code);
//					log.info("address: {}, url: {} ", address, sListUrl);

						//区域内的主题明细
						inner:
						for (int k = 1; ; k++) {
							log.info("i = {}, j = {}, k = {}", i, j, k);
							Map<String, String> params = Maps.newHashMap();
							params.put("accessKey", "556d8122421340ae8799bae4ad5c03a1");
							params.put("sid", SID);
							params.put("pageNo", String.valueOf(k));
							params.put("pageSize", "10");
							params.put("count", "16");
							params.put("areacode", code);
							params.put("areatype", "4");
							Document listDoc = getListDocument(listUrl, params);
							Elements detailElements = listDoc.getElementsByClass("publicitylist").first().getElementsByTag("li");
							if (CollectionUtil.isEmpty(detailElements)) break inner;

							for (int m = 0; m < detailElements.size(); m++) {
								AdminPunish adminPunish = createDefaultAdminPunish();

								Element detailElement = detailElements.get(m);
								Element aElement = detailElement.getElementsByTag("a").first();
								String holderName = aElement.text();
								String shref = aElement.attr("href");
								String dHref = String.format(detailUrl, shref.substring(shref.indexOf("?") + 1));
								Document holderDoc = getHolderDocument(dHref);
								Element divElement = holderDoc.getElementsByClass("companyinfo").first();
								Element topElement = divElement.getElementsByClass("top").first();
								if (topElement.getElementsByTag("h3") == null
										|| topElement.getElementsByTag("h3").size() <= 0) continue;
								String name = topElement.getElementsByTag("h3").first().text();
								String pCode = topElement.getElementsByTag("p").first().text();

								adminPunish.setUrl(dHref);
								if (pCode.contains("身份证号")) {
									adminPunish.setPersonName(name);
									adminPunish.setPersonId(pCode.replace("身份证号：",""));
									adminPunish.setObjectType("02");
								} else {
									if(name.trim().length()<4){
										adminPunish.setPersonName(name);
										adminPunish.setPersonId(pCode.replace("身份证号：",""));
										adminPunish.setObjectType("02");
									}else {
										adminPunish.setEnterpriseName(name);
										adminPunish.setEnterpriseCode1(pCode.replace("统一社会信用代码：",""));
									}
								}

								Elements trElements = divElement.getElementById("bot_3").getElementsByTag("tr");
								for (int t = 0; t < trElements.size(); t++) {
									Elements tdElements = trElements.get(i).getElementsByTag("td");
									if (tdElements.first().text().contains("处罚文书号") && StrUtil.isNotEmpty(tdElements.last().text())) {
										adminPunish.setJudgeNo(tdElements.last().text());
									}
									if (tdElements.first().text().contains("法定代表人姓名") && StrUtil.isNotEmpty(tdElements.last().text())) {
										adminPunish.setPersonName(tdElements.last().text());
									}
									if (tdElements.first().text().contains("处罚类别") && StrUtil.isNotEmpty(tdElements.last().text())) {
										adminPunish.setPunishType(tdElements.last().text());
									}
									if (tdElements.first().text().contains("处罚结果") && StrUtil.isNotEmpty(tdElements.last().text())) {
										adminPunish.setPunishResult(tdElements.last().text());
									}
									if (tdElements.first().text().contains("处罚事由") && StrUtil.isNotEmpty(tdElements.last().text())) {
										adminPunish.setPunishReason(tdElements.last().text());
									}
									if (tdElements.first().text().contains("处罚依据") && StrUtil.isNotEmpty(tdElements.last().text())) {
										adminPunish.setPunishAccording(tdElements.last().text());
									}
									if (tdElements.first().text().contains("处罚机关") && StrUtil.isNotEmpty(tdElements.last().text())) {
										adminPunish.setJudgeAuth(tdElements.last().text());
									}
									if (tdElements.first().text().contains("处罚决定日期") && StrUtil.isNotEmpty(tdElements.last().text())) {
										adminPunish.setJudgeDate(tdElements.last().text());
									}
									if (tdElements.first().text().contains("处罚公示期") && StrUtil.isNotEmpty(tdElements.last().text())) {
										adminPunish.setPublishDate(tdElements.last().text());
									}
								}
								adminPunish.setUniqueKey(dHref + "@" + adminPunish.getEnterpriseName() + "@" + adminPunish.getPersonName() + "@" + adminPunish.getJudgeNo() + "@" + adminPunish.getJudgeAuth());
								saveAdminPunishOne(adminPunish, false);
							}
						}

					}
				}
				result = true;
			} catch (Exception e) {
				log.warn(e.getMessage());
			} finally {
				countDownLatch.countDown();
				log.info("listElement execute ok, detail:{}", listElement.text());
			}

			return result;
		}
	}

	/**
	 * 获取网页内容
	 * 行政处罚决定书文号、案件名称、处罚类别、处罚事由、处罚依据、行政相对人名称、组织机构代码、工商登记码、税务登记号、
	 * 法定代表人居民身份证号、法定代表人姓名、处罚结果、处罚生效期、处罚机关、当前状态、地方编码、备注、信息提供部门、数据报送时间
	 */
	public void extractContext(String url) throws Exception {
		Document totalDoc = Jsoup.parse(getData(url));
		Elements listElements = totalDoc.getElementsByClass("header-area-list");
		CountDownLatch countDownLatch = new CountDownLatch(listElements.size()) {
			@Override
			public void await() throws InterruptedException {
				super.await();
				log.info(Thread.currentThread().getName() + " count down is ok");
			}
		};

		for (int i = 0; i < listElements.size(); i++) {
			Element listElement = listElements.get(i);
			ExecutorService executor = Executors.newFixedThreadPool(listElements.size());
			Future<Boolean> future = executor.submit(new ElementCaller(listElement, countDownLatch, i));
		}
		countDownLatch.await();
	}

	private Document getListDocument(String url, Map<String, String> requestParams) {
		return new IdempotentOperator<Document>(new Callable<Document>() {
			@Override
			public Document call() throws Exception {
				//log.debug("url:{}--requestParams--{}",url,requestParams);
				String sListStr = postData(url, requestParams);
				Document listDoc = Jsoup.parse(sListStr);
				Elements detailElements = listDoc.getElementsByClass("publicitylist").first().getElementsByTag("li");
				return listDoc;
			}
		}) {
			@Override
			protected void callOnExection(Throwable e) {
				e.printStackTrace();
				GuiZhou_XZCF.super.getData(GuiZhou_XZCF.this.url);
			}
		}.execute();
	}


	private Document getHolderDocument(String url) {
		return new IdempotentOperator<Document>(new Callable<Document>() {
			@Override
			public Document call() throws Exception {
				Document holderDoc = Jsoup.parse(getData(url));
				Element divElement = holderDoc.getElementsByClass("companyinfo").first();
				Element topElement = divElement.getElementsByClass("top").first();
				return holderDoc;
			}
		}) {
			@Override
			protected void callOnExection(Throwable e) {
				log.warn(e.getMessage());
				GuiZhou_XZCF.super.getData(GuiZhou_XZCF.this.url);
			}
		}.execute();
	}


	private AdminPunish createDefaultAdminPunish() {
		AdminPunish adminPunish = new AdminPunish();

		adminPunish.setCreatedAt(new Date());
		adminPunish.setUpdatedAt(new Date());
		adminPunish.setSource("信用中国（贵州）");
//		adminPunish.setUrl(url);
		adminPunish.setSubject("行政处罚");
		adminPunish.setObjectType("01");
		adminPunish.setEnterpriseCode1("");
		adminPunish.setEnterpriseCode2("");
		adminPunish.setEnterpriseCode3("");
		adminPunish.setEnterpriseName("");
		adminPunish.setPersonName("");
		adminPunish.setPersonId("");
		adminPunish.setJudgeNo("");
		adminPunish.setJudgeAuth("");
		return adminPunish;
	}
	public   String getSID(){
		String strSID = "";
		try {
			WebClient webClient = createWebClient("","");
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);
			HtmlPage htmlPage = webClient.getPage(url);

			//5.1 获取内嵌界面Iframe
			HtmlElement htmlElementIframe =  (HtmlElement)htmlPage.getByXPath("//body[@class='wrapper']//div[@class='main_body']//div[@id='sgsmain']//iframe[@id='sgsmain']").get(0);
			log.warn(htmlElementIframe.asXml());
			strSID = htmlElementIframe.getAttribute("src").split("=")[2];
		}catch (ScriptException unexpectedPage){
			log.warn("页面JS解析异常异常···请查看"+unexpectedPage.getMessage());
		}catch (Throwable e){
			log.warn("获取SID编号异常···请查看"+e.getMessage());
		}
		return strSID;
	}

}
