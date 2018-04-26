package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.util.StrUtil;
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
				try {
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
					if (stockcode.contains("600610")
							|| stockcode.contains("900906")
							|| stockcode.contains("600698")
							|| stockcode.contains("900946")) {
						extGSJC = stockcode.replace("600610", "中毅达")
								.replace("900906", "中毅达B")
								.replace("600698", "湖南天雁")
								.replace("900946", "天雁 B 股");
					}
					financeMonitorPunish.setStockShortName(extGSJC);

					financeMonitorPunish.setSupervisionType(typeName);
					financeMonitorPunish.setPunishTitle(docTitle);
					financeMonitorPunish.setPunishDate(createTime);
					financeMonitorPunish.setUrl(docURL.startsWith("http") ? docURL : "http://" + docURL);
					financeMonitorPunish.setSource("上交所");
					financeMonitorPunish.setObject("公司监管");

					if (!doFetch(financeMonitorPunish, false)) {
						FinanceMonitorPunish srcFmp = financeMonitorPunishMapper
								.selectByUrl(financeMonitorPunish.getUrl());
						if (srcFmp.getSupervisionType().contains(typeName)) {
							return lists;
						} else {
							srcFmp.setSupervisionType(srcFmp.getSupervisionType() + "|" + typeName);
							financeMonitorPunishMapper.updateByPrimaryKey(srcFmp);
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					continue;
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
			financeMonitorPunish.setDetails(filterErrInfo(docTitleDetail));
			extractPDF(docTitleDetail, financeMonitorPunish);
		} else {
			extractHTML(getData(docURL), financeMonitorPunish);
		}
		processSpecial(financeMonitorPunish);
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


		//经查明关键字
		String pStr = "";
		int pIndx = -1;
		String[] p = {"经查明，", "经查明", "根据证监会行政处罚"};

		for (int i = 0; i < p.length; i++) {
			if (fullTxt.indexOf(p[i]) > -1) {
				pStr = p[i];
				pIndx = fullTxt.indexOf(p[i]);
				break;
			}
		}

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
					if (s.contains("公司") || s.contains("工程中心") || s.contains("咨询中心") || s.contains("检验中心"))
						partyInstitution += "," + s;
					else
						person += "," + s;
				}
			}
			person = StringUtils.isNotEmpty(person)
					? filterErrInfo(person.substring(1)) : null;
		}

		financeMonitorPunish.setPunishNo(punishNo.replace("纪律处分决定书", ""));
		financeMonitorPunish.setPartyPerson(person);
		financeMonitorPunish.setPartyInstitution(StringUtils.isNotEmpty(partyInstitution)
				? partyInstitution.substring(1) : null);
		financeMonitorPunish.setIrregularities(filterErrInfo(violation));
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
					if (s.contains("公司") || s.contains("工程中心") || s.contains("咨询中心") || s.contains("检验中心"))
						partyInstitution += "," + s;
					else
						person += "," + s;
				}
			}
			person = StringUtils.isNotEmpty(person)
					? filterErrInfo(person.substring(1)) : null;
		}

		financeMonitorPunish.setPunishNo(punishNo.replace("纪律处分决定书", ""));
		financeMonitorPunish.setPartyPerson(person);
		financeMonitorPunish.setPartyInstitution(StringUtils.isNotEmpty(partyInstitution)
				? partyInstitution.substring(1) : null);
		financeMonitorPunish.setIrregularities(filterErrInfo(violation));
		financeMonitorPunish.setDetails(filterErrInfo(detail));
	}

	/**
	 * 特殊格式处理
	 *
	 * @param financeMonitorPunish
	 */
	private void processSpecial(FinanceMonitorPunish financeMonitorPunish) {
		String person = financeMonitorPunish.getPartyPerson();
		String partyInstitution = financeMonitorPunish.getPartyInstitution();
		if (StrUtil.isNotEmpty(person)) {
			person = filterErrInfo(person.replace(" ", "")
					.replace(" ", "")
					.replace("\n", "")
					.replace("　", "").trim());
		}
		if (StrUtil.isNotEmpty(partyInstitution)) {
			partyInstitution = filterErrInfo(partyInstitution.replace(" ", "")
					.replace(" ", "")
					.replace("\n", "")
					.replace("　", "").trim());
		}

		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/c_20160524_4118411.shtml")) {
			person = "太原化工股份有限公司控股股东";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118355.pdf")) {
			partyInstitution = "北京通灵通电讯技术有限公司，上海秦砖投资管理有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118361.pdf")) {
			person = "刘守堂、张中峰";
			partyInstitution = "对山东和信会计师事务所（特殊普通合伙）";
		}

		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/c_20160524_4118395.shtml")) {
			person = "邓强，陈洪，游晓安";
			partyInstitution = "重庆钢铁股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/ident/c/dded5c9e-f4bc-4b71-9570-bfdf4bb5e108.pdf")) {
			person = "李友，方中华，易梅，侯郁波，李晓勤，徐文彬，千新国，刘欲晓，朱兆庆，胡永栓，贾朝心，黄肖锋，傅林生，何明珂，王善迈，董黎明，邱泽珺，蒋艳华";
			partyInstitution = "方正科技集团股份有限公司，北大方正集团有限公司，武汉国兴科技发展有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/ident/c/4118452.pdf")) {
			person = "鲜言";
			partyInstitution = "上海多伦实业股份有限公司、控股股东多伦投资（香港）有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/ident/c/4118451.pdf")) {
			person = "鲍崇宪，王星星";
			partyInstitution = "上海澄海企业发展股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/ident/c/4118453.pdf")) {
			person = "韩俊良";
			partyInstitution = "华锐风电科技（集团）股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/ident/c/4118454.pdf")) {
			person = "张春昌";
			partyInstitution = "海南椰岛（集团）股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/c_20160524_4118411.shtml")) {
			person = null;
			partyInstitution = "太原化学工业集团有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118397.pdf")) {
			person = "毛芳亮";
			partyInstitution = "上海新北股权投资基金合伙企业，山东江泉实业股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118355.pdf")) {
			person = null;
			partyInstitution = "北京通灵通电讯技术有限公司，上海秦砖投资管理有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118360.pdf")) {
			person = "危雯，张新峰";
			partyInstitution = "东风汽车股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118361.pdf")) {
			person = "刘守堂，张中峰";
			partyInstitution = "山东和信会计师事务所（特殊普通合伙）";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118358.pdf")) {
			person = "林云，黄宇，陈焕智，都豫蒙，林鹏，郭大鸿，胡居洪，许领，陈建，徐顺付，杨继座";
			partyInstitution = "山东金泰集团股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/c_20160524_4118396.shtml")) {
			person = "甘肃酒钢集团宏兴钢铁股份有限公司";
			partyInstitution = "齐晓东";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118362.pdf")) {
			person = "陶刚，于建军，汪晓";
			partyInstitution = "华锐风电科技（集团）股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118364.pdf")) {
			person = "张文卿，韩家红，沈磊，朱建忠";
			partyInstitution = "上海三毛企业（集团）股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118359.pdf")) {
			person = "张殿华，赵启超，王班，侯淑芬，张黎明";
			partyInstitution = "沈阳商业城股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118357.pdf")) {
			person = null;
			partyInstitution = "南京商贸旅游发展集团有限责任公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118363.pdf")) {
			person = "关长文，陈阿琴，王永和";
			partyInstitution = "安徽方兴科技股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118342.pdf")) {
			person = "成卫文，李曙光，黄俊岩，韩海霞，成清波";
			partyInstitution = "吉林成城集团股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118345.pdf")) {
			person = "钱俊，窦万波，徐盛富，李晓玲";
			partyInstitution = "安徽国通高新管业股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118340.pdf")) {
			person = "彭辰，吴杰，万毅";
			partyInstitution = "武汉钢铁股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118343.pdf")) {
			person = "郭永明，马婷婷";
			partyInstitution = "西安宏盛科技发展股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118341.pdf")) {
			person = "李晓斌，孙丽斌";
			partyInstitution = "山西省国新能源发展集团有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118346.pdf")) {
			person = "张静静，罗炜岚，王承宇，张健，何婧";
			partyInstitution = "上海新梅置业股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/4118344.pdf")) {
			person = null;
			partyInstitution = "上海兴盛实业发展（集团）有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/a3019d9a-e904-4b76-be5a-a4a5873977cc.pdf")) {
			person = "庄敏，陈海昌，庄明，蒋俊杰，童爱平，王务云，王培琴，林硕奇，茅建华，费滨海，沙智慧";
			partyInstitution = "江苏保千里视像科技集团股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/ident/c/c_20170315_4250475.shtml")) {
			person = null;
			partyInstitution = "永新华韵文化产业投资有限公司，宁波宏创股权投资合伙企业（有限合伙）";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/c_20160524_4118396.shtml")) {
			person = "齐晓东";
			partyInstitution = "甘肃酒钢集团宏兴钢铁股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/c_20160524_4118441.shtml")) {
			person = "张文卿，沈磊";
			partyInstitution = "上海三毛企业（集团）股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/c_20160524_4118428.shtml")) {
			person = "刘永跃";
			partyInstitution = "安徽四创电子股份有限公司";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/8129505052203282.pdf")) {
			financeMonitorPunish.setPunishNo("〔2017〕0056号");
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/criticism/c/8129382882712887.pdf")) {
			financeMonitorPunish.setPunishNo("〔2017〕0049 号");
		}
		if (financeMonitorPunish.getUrl().contains("http://www.sse.com.cn/disclosure/credibility/supervision/measures/ident/c/4118455.pdf")) {
			person = "鲍崇宪，王星星";
			partyInstitution = "上海澄海企业发展股份有限公司";
		}

		financeMonitorPunish.setPartyPerson(person);
		financeMonitorPunish.setPartyInstitution(partyInstitution);
	}
}
