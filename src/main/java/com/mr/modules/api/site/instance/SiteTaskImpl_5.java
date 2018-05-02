package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.util.StrUtil;
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
import org.springframework.util.CollectionUtils;

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
		if (!CollectionUtils.isEmpty(lists)) {
			exportToXls("Site5.xlsx", lists);
		}

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
		Assert.notNull(oneFinanceMonitorPunish.getUrl());
		oneFinanceMonitorPunish.setSource("上交所");
		oneFinanceMonitorPunish.setObject("债券监管");

		FinanceMonitorPunish srcFmp = financeMonitorPunishMapper
				.selectByUrl(oneFinanceMonitorPunish.getUrl());
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
			try {

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
				financeMonitorPunish.setUrl(href);
				financeMonitorPunish.setSource("上交所");
				financeMonitorPunish.setObject("债券监管");

				if (!doFetchForRetry(financeMonitorPunish, false)) {
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
			} catch (Exception e) {
				writeBizErrorLog(financeMonitorPunish.getUrl(), e.getMessage());
				continue;
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
	@Override
	protected boolean doFetch(FinanceMonitorPunish financeMonitorPunish,
							  Boolean isForce) throws Exception {
		String href = financeMonitorPunish.getUrl();

		String fullTxt = "";
		if (href.endsWith(".doc")) {
			fullTxt = ocrUtil.getTextFromDoc(downLoadFile(href));

		} else {
			Document pDoc = Jsoup.parse(getData(href));
			Element allZoomDiv = pDoc.getElementsByClass("allZoom").get(0);
			fullTxt = allZoomDiv.text();
		}

		financeMonitorPunish.setDetails(fullTxt);
		extractTxt(fullTxt, financeMonitorPunish);
		financeMonitorPunish.setPunishInstitution("上海证券交易所");
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
		String partyPerson = "";
		String partyInstitution = "";
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

		String punishTitle = financeMonitorPunish.getPunishTitle();
		String companyName = punishTitle.substring(punishTitle.indexOf("关于对") + 3, punishTitle.indexOf("公司") + 2)
				.replace(" ", "")
				.replace("\n", "")
				.replace("　", "");
		financeMonitorPunish.setPartyInstitution(companyName);

		int sIndx = fullTxt.indexOf("当事人：") == -1 ?
				fullTxt.indexOf("当事人") : fullTxt.indexOf("当事人：");
		int pIndx = fullTxt.indexOf("经查明，") == -1 ?
				fullTxt.indexOf("经查明") : fullTxt.indexOf("经查明，");
		int pIndexlength = 4;
		if (pIndx < 0) {
			int tmpIndex = fullTxt.indexOf(companyName);
			if (tmpIndex < 0) {
				log.error("格式不规则，无法解析");
				return;
			}
			String tmp1 = fullTxt.substring(tmpIndex + companyName.length());
			if (tmp1.indexOf(companyName) < 0) {
				log.error("格式不规则，无法解析");
				return;
			}

			pIndx = fullTxt.indexOf(tmp1.substring(0, 30));
		}

		if (sIndx >= 0 && punishTitle.contains("关责任人")) {
			person = fullTxt.substring(sIndx, pIndx)
					.replace("当事人：", "")
					.replace("当事人:", "")
					.replace("当事人", "");
			String pTag = "";

			if (person.contains("；")) {
				pTag = "；";
			} else if (person.contains("。")) {
				pTag = "。";
			}
			if (StrUtil.isNotEmpty(pTag)) {
				String[] pArrs = person.split(pTag);
				int j = 0;
				for (String p : pArrs) {
					String p1 = p.replace(" ", "")
							.replace(" ", "")
							.replace("\n", "")
							.replace("　", "").trim();
					if (!p1.startsWith(companyName)) {
						if (p1.contains("，"))
							partyPerson += "," + p1.substring(0, p1.indexOf("，"));
						else
							partyPerson += "," + p1;
					} else {
						if (j++ > 0) break;
					}
				}
			}

			if (StrUtil.isNotEmpty(partyPerson)) {
				financeMonitorPunish.setPartyPerson(filterErrInfo(partyPerson.substring(1)));
			}

		}

		{
			String tmp = fullTxt.substring(pIndx);
			if (tmp.lastIndexOf("上海证券交易所") > pIndx) {
				violation = tmp.substring(pIndexlength, tmp.lastIndexOf("上海证券交易所"));
			} else {
				violation = tmp.substring(pIndexlength);
			}
		}

		if (StringUtils.isEmpty(violation)) {
			log.error("内容不规则 URL:" + financeMonitorPunish.getUrl());
			return;
		}
		financeMonitorPunish.setIrregularities(filterErrInfo(violation));
	}
}