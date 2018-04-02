package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.json.JSONArray;
import com.mr.framework.json.JSONObject;
import com.mr.framework.json.JSONUtil;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

		doSJSData("公开认定");
		doSJSData("通报批评");
		doSJSData("公开谴责");

		return null;

	}

	protected String doSJSData(String typeName) throws Throwable {

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

		List<LinkedHashMap<String, String>> lists = Lists.newLinkedList();

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
				LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
				JSONObject jsObj = results.getJSONObject(i);
//			证券代码
				String stockcode = jsObj.getStr("stockcode");
//			证券简称
				String extGSJC = jsObj.getStr("extGSJC");
//			监管类型
				String extWTFL = typeName;
//			处理事由
				String docTitle = jsObj.getStr("docTitle");
//			链接
				String docURL = jsObj.getStr("docURL");

//			涉及对象
				String extTeacher = jsObj.getStr("extTeacher");
//			处理日期
				String createTime = jsObj.getStr("createTime");

				map.put("stockcode", stockcode);
				map.put("extGSJC", extGSJC);
				map.put("extWTFL", typeName);
				map.put("docURL", docURL);
				map.put("extTeacher", extTeacher);
				map.put("createTime", createTime);

				doFetch(map);

				lists.add(map);
			}

		}

		exportToXls(String.format("Site4_%s.xlsx", typeName), lists);

		return null;
	}

	/**
	 * 抓取并解析单条数据
	 *	map[stockcode; extGSJC; extWTFL; docURL; extTeacher; createTime]
	 * @param map
	 */
	private LinkedHashMap<String, String> doFetch(LinkedHashMap<String, String> map) throws Exception {
		String docURL = map.get("docURL");
		String docTitleDetail = map.get("docTitleDetail");

		if (docURL.endsWith("pdf")) {
			String fileName = downLoadFile(docURL.startsWith("http") ? docURL : "http://" + docURL);
			//处理事由正文详细文本信息
			docTitleDetail = ocrUtil.getTextFromPdf(fileName);
		} else {
			String docTitleTxt = getData(docURL.startsWith("http") ? docURL : "http://" + docURL);
			Document doc = Jsoup.parse(docTitleTxt);
			Element dtElement = doc.getElementsByClass("allZoom").first();
			if (!Objects.isNull(dtElement.getElementsByTag("div").first())) {
				docTitleDetail += filter(dtElement.getElementsByTag("div").first().text(), filterTags);
				for (Element ps : dtElement.getElementsByTag("p")) {
					docTitleDetail += filter(ps.text(), filterTags);
				}
			}
		}

		map.put("docTitleDetail", docTitleDetail);
		return map;
	}

}
