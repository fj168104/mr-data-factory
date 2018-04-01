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

/**
 * Created by feng on 18-3-16
 * 上交所
 * 债券监管
 */

@Slf4j
@Component("site5")
@Scope("prototype")
public class SiteTaskImpl_5 extends SiteTaskExtend {

	protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);

	//过滤字段设置
	ArrayList<String> filterTags = Lists.newArrayList("<Strong>", "</Strong>", "&nbsp;", "　");

	//	公开认定 通报批评 公开谴责
	enum MType {
		A2("a-2", "公开认定"),
		A3("a-3", "通报批评"),
		A4("a-4", "公开谴责");

		public String code;
		public String name;

		MType(String code, String name) {
			this.code = code;
			this.name = name;
		}

	}

	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	@Override
	protected String execute() throws Throwable {
		log.info("*******************call site5 task**************");

		String targetUri = "http://www.sse.com.cn/disclosure/credibility/bonds/disposition/";
		String fullTxt = getData(targetUri);
		List<LinkedHashMap<String, String>> lists = Lists.newLinkedList();
		lists.addAll(extract(MType.A3, fullTxt));
		lists.addAll(extract(MType.A4, fullTxt));
		exportToXls("Site5.xlsx", lists);
		return null;
	}

	/**
	 * 提取所需信息
	 * 证券代码、证券简称、监管类型、标题、处理事由、处理日期
	 *
	 * @param mType   监管类型
	 * @param fullTxt 提取文本
	 */
	private List<LinkedHashMap<String, String>> extract(MType mType, String fullTxt) throws Exception {
		List<LinkedHashMap<String, String>> lists = Lists.newLinkedList();

		Document doc = Jsoup.parse(fullTxt);
		Element divElement = doc.getElementById(mType.code);
		Element tableElement = divElement.getElementsByTag("table").get(0);
		Elements trElements = tableElement.getElementsByTag("tr");
		for (int i = 1; i < trElements.size(); i++) {
			Elements tdElements = trElements.get(i).getElementsByTag("td");

			log.info(tdElements.text());

			//证券代码
			String code = tdElements.get(0).text();    //从链接中提取

			//证券简称
			String sAbstract = tdElements.get(1).text(); //从链接中提取

			Element aElement = tdElements.get(2).getElementsByTag("a").get(0);
			String href = "http://www.sse.com.cn" + aElement.attr("href");
			//标题
			String title = tdElements.get(2).text();    //从链接中提取

			//处理日期
			String punishDate = tdElements.get(3).text();    //链接中提取

			LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
			map.put("href", href);
			map.put("code", code);
			map.put("sAbstract", sAbstract);
			map.put("mType", mType.name);
			map.put("title", title);
			map.put("punishDate", punishDate);

			doFetch(map);
			lists.add(map);
		}
		return lists;
	}

	/**
	 * 抓取并解析单条数据
	 *	map[code; sAbstract; mType; title; punishDate; href]
	 * @param map
	 */
	private LinkedHashMap<String, String> doFetch(LinkedHashMap<String, String> map) throws Exception {
		String href = map.get("href");

		//处理事由
		String pReason = "";
		if(href.endsWith(".doc")){
			pReason = ocrUtil.getTextFromDoc(downLoadFile(href));
			map.put("pReason", pReason);
			return map;
		}

		Document pDoc = Jsoup.parse(getData(href));
		Element allZoomDiv = pDoc.getElementsByClass("allZoom").get(0);
		Elements ppEles = allZoomDiv.getElementsByTag("p");
		for (Element pp : ppEles) {
			pReason += filter(pp.text(), filterTags);
		}
		map.put("pReason", pReason);
		return map;
	}
}