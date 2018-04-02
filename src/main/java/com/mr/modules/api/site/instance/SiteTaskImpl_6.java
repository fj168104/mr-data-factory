package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by feng on 18-3-16
 * 上交所
 * 交易监管
 */

@Slf4j
@Component("site6")
@Scope("prototype")
public class SiteTaskImpl_6 extends SiteTaskExtend {

	protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);

	ArrayList<String> filterTags = Lists.newArrayList("<SPAN>",
			"<br />",
			" &nbsp;",
			"<p align=\"center\">",
			"</strong>",
			"<strong>",
			"</p>",
			" ");

	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	@Override
	protected String execute() throws Throwable {
		log.info("*******************call site6 task********************");

		/**
		 * get请求
		 * 1.监管类型：纪律处分
		 * 2.处理事由：
		 */

		String targetUri = "http://www.sse.com.cn/disclosure/credibility/regulatory/punishment/";
		String pageTxt = getData(targetUri);
		Document doc = Jsoup.parse(pageTxt);
		String pageCount = doc.getElementById("createPage").attr("PAGE_COUNT");
		log.info("pageCount:" + pageCount);
		List<LinkedHashMap<String, String>> lists = extract(pageCount);
		exportToXls("site6.xlsx", lists);
		return null;

	}

	/**
	 * 提取所需信息
	 * 处分对象、处分对象类型、函号、函件标题、发函日期、涉及债券
	 */
	private List<LinkedHashMap<String, String>> extract(String pageCount) throws Exception {
		List<LinkedHashMap<String, String>> lists = Lists.newLinkedList();

		java.util.Map<String, String> requestParams = Maps.newHashMap();
		Map<String, String> headParams = Maps.newHashMap();
		headParams.put("Referer", "http://www.szse.cn/main/disclosure/zqxx/jlcf/");
		headParams.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");

		for (int pageNo = 1; pageNo <= Integer.parseInt(pageCount); pageNo++) {
			String url = String.format("http://www.sse.com.cn" +
					"/disclosure/credibility/regulatory/punishment/s_index_%d.htm", pageNo);
			if (pageNo == 1)
				url = "http://www.sse.com.cn/disclosure/credibility/regulatory/punishment/";
			String fullTxt = postData(url, requestParams, headParams);
			Document doc = Jsoup.parse(fullTxt);
			Elements dds = doc.getElementsByClass("sse_list_1 js_listPage").get(0)
					.getElementsByTag("dd");
			for (Element dd : dds) {
				Element aElement = dd.getElementsByTag("a").get(0);
				String title = aElement.attr("title");
				String docUrl = "http://www.sse.com.cn" + aElement.attr("href");

				LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
				map.put("mType", "纪律处分");    //监管类型
				map.put("title", title);        //标题
				map.put("docUrl", docUrl);
				doFetch(map);

				lists.add(map);
			}
		}
		return lists;
	}

	/**
	 * 抓取并解析单条数据
	 * map[mType; title; docUrl]
	 *
	 * @param map
	 */
	private LinkedHashMap<String, String> doFetch(LinkedHashMap<String, String> map) throws Exception {
		String docUrl = map.get("docUrl");
		log.info(docUrl);
		String mContent = "";

		if (docUrl.endsWith("doc") || docUrl.endsWith("docx")) {
			mContent = ocrUtil.getTextFromDoc(downLoadFile(docUrl));
		} else if (docUrl.endsWith("shtml")) {
			Document sDoc = Jsoup.parse(getData(docUrl));
			Element std = sDoc.getElementsByClass("allZoom").get(0)
					.getElementsByTag("td").get(0);
			mContent = filter(std.text(), filterTags);
		}

		map.put("mContent", mContent);    //处理事由
		return map;
	}
}
