package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.SiteTaskDict;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

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

		// 普通方法
		static MType getName(String name) {
			for (MType s : MType.values()) {
				if (s.name.equals(name)) {
					return s;
				}
			}
			return null;
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
		List<FinanceMonitorPunish> lists = Lists.newLinkedList();
		lists.addAll(extract(MType.A3, fullTxt));
		lists.addAll(extract(MType.A4, fullTxt));
		exportToXls("Site5.xlsx", lists);
		return null;
	}

	@Override
	protected String executeOne() throws Throwable {
		log.info("*******************call site5 task for One Record**************");

		String typeName = oneFinanceMonitorPunish.getSupervisionType();
		Assert.notNull(oneFinanceMonitorPunish.getStockCode());
		Assert.notNull(oneFinanceMonitorPunish.getStockShortName());
		Assert.notNull(oneFinanceMonitorPunish.getSupervisionType());
		Assert.notNull(oneFinanceMonitorPunish.getPunishTitle());
		Assert.notNull(oneFinanceMonitorPunish.getPunishDate());
		Assert.notNull(oneFinanceMonitorPunish.getSource());
		oneFinanceMonitorPunish.setObject("上交所-债券监管");

		FinanceMonitorPunish srcFmp = financeMonitorPunishMapper
				.selectBySource(oneFinanceMonitorPunish.getSource());
		if (!Objects.isNull(srcFmp)) {
			if (!srcFmp.getSupervisionType().contains(typeName)) {
				oneFinanceMonitorPunish.setSupervisionType(srcFmp.getSupervisionType() + "|" + typeName);
			}
		}

		initDate();
		doFetch(oneFinanceMonitorPunish, true);
		return null;
	}

	/**
	 * 提取所需信息
	 * 证券代码、证券简称、监管类型、标题、处理事由、处理日期
	 *
	 * @param mType   监管类型
	 * @param fullTxt 提取文本
	 */
	private List<FinanceMonitorPunish> extract(MType mType, String fullTxt) throws Exception {
		List<FinanceMonitorPunish> lists = Lists.newLinkedList();

		Document doc = Jsoup.parse(fullTxt);
		Element divElement = doc.getElementById(mType.code);
		Element tableElement = divElement.getElementsByTag("table").get(0);
		Elements trElements = tableElement.getElementsByTag("tr");
		for (int i = 1; i < trElements.size(); i++) {
			FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();

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

			financeMonitorPunish.setStockCode(code);
			financeMonitorPunish.setStockShortName(sAbstract);
			financeMonitorPunish.setSupervisionType(mType.name);
			financeMonitorPunish.setPunishTitle(title);
			financeMonitorPunish.setPunishDate(punishDate);
			financeMonitorPunish.setSource(href);
			financeMonitorPunish.setObject("上交所-债券监管");

			if (!doFetch(financeMonitorPunish, false)) {
				FinanceMonitorPunish srcFmp = financeMonitorPunishMapper
						.selectByBizKey(financeMonitorPunish.getPrimaryKey());
				if (srcFmp.getSupervisionType().contains(mType.name)) {
					return lists;
				} else {
					srcFmp.setSupervisionType(srcFmp.getSupervisionType() + "|" + mType.name);
					financeMonitorPunishMapper.updateByPrimaryKey(srcFmp);
					financeMonitorPunish.setSupervisionType(srcFmp.getSupervisionType());
				}
			}
			lists.add(financeMonitorPunish);
		}
		return lists;
	}

	/**
	 * 抓取并解析单条数据
	 *
	 * @param financeMonitorPunish
	 * @param isForce
	 */
	private boolean doFetch(FinanceMonitorPunish financeMonitorPunish,
							Boolean isForce) throws Exception {
		String href = financeMonitorPunish.getSource();

		String fullTxt = "";
		if (href.endsWith(".doc")) {
			fullTxt = ocrUtil.getTextFromDoc(downLoadFile(href));

		} else {
			Document pDoc = Jsoup.parse(getData(href));
			Element allZoomDiv = pDoc.getElementsByClass("allZoom").get(0);
			fullTxt = allZoomDiv.text();
		}
		extractTxt(fullTxt, financeMonitorPunish);
		return saveOne(financeMonitorPunish, isForce);
	}

	/**
	 * 提取所需要的信息
	 * 处罚文号、处罚对象、处理事由
	 */
	private void extractTxt(String fullTxt, FinanceMonitorPunish financeMonitorPunish) {
		//处罚文号
		String punishNo = "";
		//当事人
		String person = "";
		//处理事由
		String violation = "";

		{
			String tmp = fullTxt;
			if (tmp.indexOf("20") < tmp.indexOf("号")) {
				tmp = tmp.substring(tmp.indexOf("20") - 1, tmp.indexOf("号") + 1)
						.replace("\n", "")
						.replace(" ", "");
				if (tmp.length() >= 5 && tmp.length() < 10) {
					punishNo = tmp;
					financeMonitorPunish.setPunishNo(punishNo);
				}

			}
		}

		int sIndx = fullTxt.indexOf("当事人：") == -1 ?
				fullTxt.indexOf("当事人") : fullTxt.indexOf("当事人：");
		int pIndx = fullTxt.indexOf("经查明，") == -1 ?
				fullTxt.indexOf("经查明") : fullTxt.indexOf("经查明，");
		if (pIndx < 0) {
			log.error("文本格式不规则，无法识别");
			return;
		}

		if (sIndx >= 0) {
			person = fullTxt.substring(sIndx, pIndx).replace("当事人：", "")
					.replace("当事人", "");
			financeMonitorPunish.setPartyPerson(filterErrInfo(person));
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
			log.error("内容不规则 URL:" + financeMonitorPunish.getSource());
			return;
		}
		financeMonitorPunish.setIrregularities(filterErrInfo(violation));
	}
}