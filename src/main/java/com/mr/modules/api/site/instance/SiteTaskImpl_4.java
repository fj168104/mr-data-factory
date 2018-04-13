package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.json.JSONArray;
import com.mr.framework.json.JSONObject;
import com.mr.framework.json.JSONUtil;
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
import schemasMicrosoftComVml.STTrueFalse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by feng on 18-3-16
 * 上交所
 * 公司监管
 */

@Slf4j
@Component("site4")
@Scope("prototype")
public class SiteTaskImpl_4 extends SiteTaskExtend {


	protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);

	ArrayList<String> filterTags = Lists.newArrayList("<strong>", "</strong>", "&nbsp;", "　", "<br>");

	@Override
	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	protected String execute() throws Throwable {
		log.info("*******************call site4 task**************");
		String[] types = {"公开认定", "通报批评", "公开谴责"};
		for (String type : types) {
			List<FinanceMonitorPunish> lists = doSJSData(type);
			if (!CollectionUtils.isEmpty(lists)) {
				exportToXls(String.format("Site4_%s.xlsx", type), lists);
			}

		}
		return null;
	}


	@Override
	protected String executeOne() throws Throwable {
		log.info("*******************call site4 task for One Record**************");

		String typeName = oneFinanceMonitorPunish.getSupervisionType();
		Assert.notNull(oneFinanceMonitorPunish.getStockCode());
		Assert.notNull(oneFinanceMonitorPunish.getStockShortName());
		Assert.notNull(oneFinanceMonitorPunish.getSupervisionType());
		Assert.notNull(oneFinanceMonitorPunish.getPunishTitle());
		Assert.notNull(oneFinanceMonitorPunish.getPunishDate());
		Assert.notNull(oneFinanceMonitorPunish.getSource());
		oneFinanceMonitorPunish.setSource("上交所");
		oneFinanceMonitorPunish.setObject("公司监管");

		FinanceMonitorPunish srcFmp = financeMonitorPunishMapper
				.selectByUrl(oneFinanceMonitorPunish.getSource());
		if (!Objects.isNull(srcFmp)) {
			if (!srcFmp.getSupervisionType().contains(typeName)) {
				oneFinanceMonitorPunish.setSupervisionType(srcFmp.getSupervisionType() + "|" + typeName);
			}
		}

		initDate();
		doFetch(oneFinanceMonitorPunish, true);
		return null;
	}


	protected List<FinanceMonitorPunish> doSJSData(String typeName) throws Throwable {
		List<FinanceMonitorPunish> lists = Lists.newLinkedList();

		//get请球
		//1、直接分析表格内容即可
		//2、提取链接的pdf的文本内容 SiteTaskImpl_4

		//公开认定解析
		String targetUri1 = "http://query.sse.com.cn/commonSoaQuery.do";
		Map<String, String> headParams = Maps.newHashMap();
		headParams.put("Referer", "http://www.sse.com.cn/disclosure/credibility/supervision/measures/");
		headParams.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");

		Map<String, String> params = Maps.newHashMap();
		params.put("jsonCallBack", "jsonpCallback89106");
		params.put("extWTFL", typeName);
		params.put("siteId", "28");
		params.put("sqlId", "BS_GGLL");
		params.put("channelId", "10007,10008,10009,10010");
		params.put("order", "createTime|desc,stockcode|asc");
		params.put("isPagination", "fasle");
		params.put("pageHelp.pageSize", "15");
		params.put("pageHelp.pageNo", "1");
		params.put("pageHelp.beginPage", "1");
		params.put("pageHelp.cacheSize", "1");

		String fullText = getData(targetUri1, params, headParams);
		fullText = fullText.replace("jsonpCallback89106(", "");
		fullText = fullText.substring(0, fullText.length()) + "}";
		JSONObject jsonObject = JSONUtil.parseObj(fullText);
		JSONObject pageHelp = jsonObject.getJSONObject("pageHelp");
		int pageCount = pageHelp.getInt("pageCount");

		for (int p = 1; p <= pageCount; p++) {
			params.put("pageHelp.pageNo", String.valueOf(p));
			params.put("pageHelp.beginPage", String.valueOf(p));
			fullText = getData(targetUri1, params, headParams);
			fullText = fullText.replace("jsonpCallback89106(", "");
			fullText = fullText.substring(0, fullText.length()) + "}";
			jsonObject = JSONUtil.parseObj(fullText);
			JSONArray resultStr = jsonObject.getJSONArray("result");
			log.info(resultStr.toString());
			JSONArray results = JSONUtil.parseArray(resultStr.toString());

			for (int i = 0; i < results.size(); i++) {
				FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
				JSONObject jsObj = results.getJSONObject(i);
				//证券代码
				String stockcode = jsObj.getStr("stockcode");
				//证券简称
				String extGSJC = jsObj.getStr("extGSJC");
				//名单分类
				String extWTFL = typeName;
				//标题名称
				String docTitle = jsObj.getStr("docTitle");
				//链接
				String docURL = jsObj.getStr("docURL");

				//涉及对象
//				String extTeacher = jsObj.getStr("extTeacher");
				//处理日期
				String createTime = jsObj.getStr("createTime");

				financeMonitorPunish.setStockCode(stockcode);
				financeMonitorPunish.setStockShortName(extGSJC);
				financeMonitorPunish.setSupervisionType(typeName);
				financeMonitorPunish.setPunishTitle(docTitle);
				financeMonitorPunish.setPunishDate(createTime);
				financeMonitorPunish.setUrl(docURL.startsWith("http") ? docURL : "http://" + docURL);
				financeMonitorPunish.setSource("上交所");
				financeMonitorPunish.setObject("公司监管");

				if (!doFetch(financeMonitorPunish, false)) {
					FinanceMonitorPunish srcFmp = financeMonitorPunishMapper
							.selectByBizKey(financeMonitorPunish.getPrimaryKey());
					if (srcFmp.getSupervisionType().contains(typeName)) {
						return lists;
					} else {
						srcFmp.setSupervisionType(srcFmp.getSupervisionType() + "|" + typeName);
						financeMonitorPunishMapper.updateByPrimaryKey(srcFmp);
					}


				}
				lists.add(financeMonitorPunish);
			}

		}

		return lists;
	}

	/**
	 * 抓取并解析单条数据
	 *
	 * @param financeMonitorPunish
	 */
	private boolean doFetch(FinanceMonitorPunish financeMonitorPunish,
							Boolean isForce) throws Exception {
		String docURL = financeMonitorPunish.getUrl();
		String docTitleDetail = "";

		if (docURL.endsWith("pdf")) {
			String fileName = downLoadFile(docURL);
			//处理事由正文详细文本信息
			docTitleDetail = ocrUtil.getTextFromPdf(fileName);
			extractPDF(docTitleDetail, financeMonitorPunish);
		} else {
			extractHTML(getData(docURL), financeMonitorPunish);

		}

		financeMonitorPunish.setPunishInstitution("上海证券交易所");

		return saveOne(financeMonitorPunish, isForce);
	}

	/**
	 * 提取所需要的信息
	 * 处罚文号、处罚对象、处理事由
	 */
	private void extractPDF(String fullTxt, FinanceMonitorPunish financeMonitorPunish) {
		//处罚文号
		String punishNo = "";
		//当事人
		String person = "";
		String partyInstitution = "";
		//处理事由
		String violation = "";

		int sIndx = fullTxt.indexOf("当事人：") == -1 ?
				fullTxt.indexOf("当事人") : fullTxt.indexOf("当事人：");
		int pIndx = fullTxt.indexOf("经查明，") == -1 ?
				fullTxt.indexOf("经查明") : fullTxt.indexOf("经查明，");
		if (pIndx < 0) {
			log.error("文本格式不规则，无法识别");
			return;
		}

		{
			String tmp = fullTxt;
			if (tmp.indexOf("20") < tmp.indexOf("号")) {
				tmp = tmp.substring(tmp.indexOf("20") - 1, tmp.indexOf("号") + 1)
						.replace("\n", "")
						.replace(" ", "");
				if (tmp.length() >= 5 && tmp.length() < 10) {
					punishNo = tmp;
				}

			}
		}
		if (sIndx > 0) {
			person = fullTxt.substring(sIndx, pIndx).replace("当事人：", "")
					.replace("当事人", "");
		} else {
			String tmp = fullTxt.substring(0, pIndx);

			if (tmp.contains(" \n \n")) {
				person = tmp.substring(tmp.lastIndexOf(" \n \n"))
						.replace("：", "")
						.replace("\n", "")
						.replace(" ", "");
			}
		}

		{
			String tmp = fullTxt.substring(pIndx);
			if (tmp.lastIndexOf("上海证券交易所") > pIndx) {
				violation = tmp.substring(4, tmp.lastIndexOf("上海证券交易所"));
			} else {
				violation = tmp.substring(4);
			}
		}

		if (StringUtils.isEmpty(violation)) {
			log.error("内容不规则 URL:" + financeMonitorPunish.getUrl());
			return;
		}


		if (sIndx > 0 && sIndx < pIndx) {
			person = "";
			String tmp = fullTxt.substring(sIndx + 4, pIndx);
			String[] tArr = tmp.split("；");
			for (String t : tArr) {
				if (t.indexOf("，") > -1) {
					String s = t.substring(0, t.indexOf("，"));
					if (s.contains("公司"))
						partyInstitution += "," + s;
					else
						person += "," + s;
				}
			}
			person = StringUtils.isNotEmpty(person)
					? filterErrInfo(person.substring(1)) : null;
		}


		financeMonitorPunish.setPunishNo(punishNo);
		financeMonitorPunish.setPartyPerson(person);
		financeMonitorPunish.setPartyInstitution(StringUtils.isNotEmpty(partyInstitution)
				? partyInstitution.substring(1) : null);
		financeMonitorPunish.setIrregularities(filterErrInfo(violation));
		financeMonitorPunish.setDetails(filterErrInfo(fullTxt));
	}

	/**
	 * 提取所需要的信息
	 * 处罚文号、处罚对象、处理事由
	 */
	private void extractHTML(String fullTxt, FinanceMonitorPunish financeMonitorPunish) {
		//处罚文号
		String punishNo = "";
		//当事人
		String person = "";
		String partyInstitution = "";
		//处理事由
		String violation = "";
		//详情
		String detail = "";

		//取处罚文号开关
		boolean isPunishNo = true;
		//取当事人开关
		boolean isPersonOn = false;
		//取处理事由开关
		boolean isViolation = false;

		Document doc = Jsoup.parse(fullTxt);
		Element allElement = doc.getElementsByClass("allZoom").first();
		detail = allElement.text();
		String allZoomString = allElement.html();
		String allDivString = allZoomString.substring(0, allZoomString.indexOf("<p"));
		String allPString = allZoomString.substring(allZoomString.indexOf("<p"));

		if (StringUtils.isNotEmpty(allDivString) &&
				allDivString.trim().length() > 0 && allDivString.contains("</div>")) {
			Document divDoc = Jsoup.parse(allDivString.substring(allDivString.indexOf("<div"),
					allDivString.lastIndexOf("</div>") + 6));
			punishNo = filter(divDoc.getElementsByTag("div").first().text(), filterTags);
			log.debug("div punishNo:" + punishNo);

		}

		Document pDoc = Jsoup.parse(allPString);
		for (Element ps : pDoc.getElementsByTag("p")) {
			String pString = filter(ps.text(), filterTags);

			if (isPunishNo && StringUtils.isNotEmpty(punishNo)) {
				isPunishNo = false;
				isPersonOn = true;
			}

			if (pString.contains("经查明")) {
				isPersonOn = false;
				isViolation = true;
			}

			if (filter(pString, filterTags).equals("上海证券交易所")) {
				isViolation = false;
				break;
			}
			if (isPunishNo) {
				punishNo += pString;
			}
			if (isPersonOn) {
				person += pString;
			}
			if (isViolation) {
				violation += pString;
			}

		}

		if (StringUtils.isEmpty(violation)) {
			log.error("内容不规则 URL:" + financeMonitorPunish.getUrl());
			return;
		}

		//解析出当事人信息
		int sIndx = detail.indexOf("当事人：") == -1 ?
				detail.indexOf("当事人") : detail.indexOf("当事人：");
		int pIndx = detail.indexOf("经查明，") == -1 ?
				detail.indexOf("经查明") : detail.indexOf("经查明，");
		if (pIndx < 0) {
			log.error("文本格式不规则，无法识别");
			return;
		}

		if (sIndx > 0 && sIndx < pIndx) {
			person = "";
			String tmp = detail.substring(sIndx + 4, pIndx);
			String[] tArr = tmp.split("；");
			for (String t : tArr) {
				if (t.indexOf("，") > -1) {
					String s = t.substring(0, t.indexOf("，"));
					if (s.contains("公司"))
						partyInstitution += "," + s;
					else
						person += "," + s;
				}
			}
			person = StringUtils.isNotEmpty(person)
					? filterErrInfo(person.substring(1)) : null;
		}

		financeMonitorPunish.setPunishNo(punishNo);
		financeMonitorPunish.setPartyPerson(person);
		financeMonitorPunish.setPartyInstitution(StringUtils.isNotEmpty(partyInstitution)
				? partyInstitution.substring(1) : null);
		financeMonitorPunish.setIrregularities(filterErrInfo(violation));
		financeMonitorPunish.setDetails(filterErrInfo(detail));
	}

}
