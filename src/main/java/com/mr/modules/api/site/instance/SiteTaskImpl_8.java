package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by feng on 18-3-16
 * 深交所
 * 信息披露->上市公司信息->上市公司诚信档案->中介处罚与处分记录
 */

@Slf4j
@Component("site8")
@Scope("prototype")
public class SiteTaskImpl_8 extends SiteTaskExtend {

	protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);

	private enum PLATE {
		MAIN("000", "主板"),
		MINOR("002", "中小企业板"),
		GROWTH("300", "创业板");

		public String code;
		public String name;

		PLATE(String code, String name) {
			this.code = code;
			this.name = name;
		}

		// 普通方法
		static PLATE getPlate(String code) {
			for (PLATE s : PLATE.values()) {
				if (s.code.equals(code)) {
					return s;
				}
			}
			return MAIN;
		}

	}

	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	@Override
	protected String execute() throws Throwable {
		log.info("*******************call site8 task**************");
		//可直接载excel
		String exlUri = "http://www.szse.cn/szseWeb/ShowReport.szse?SHOWTYPE=xlsx&CATALOGID=1903_detail&tab1PAGENO=1&ENCODE=1&TABKEY=tab1";

		//download pdf并解析成文本
		String xlsName = downLoadFile(exlUri, "中介处罚与处分记录.xlsx");
		log.info("fileName=" + xlsName);
		List<FinanceMonitorPunish> lists = extract(xlsName);
		if (!CollectionUtils.isEmpty(lists)) {
			exportToXls("site8.xlsx", lists);
		}

		return null;
	}

	@Override
	protected String executeOne() throws Throwable {
		log.info("*******************call site8 task for One Record**************");

		Assert.notNull(oneFinanceMonitorPunish.getIntermediaryCategory());
		Assert.notNull(oneFinanceMonitorPunish.getPunishDate());
		Assert.notNull(oneFinanceMonitorPunish.getPunishCategory());
		Assert.notNull(oneFinanceMonitorPunish.getCompanyCode());
		Assert.notNull(oneFinanceMonitorPunish.getCompanyShortName());
		Assert.notNull(oneFinanceMonitorPunish.getPartyInstitution());
		Assert.notNull(oneFinanceMonitorPunish.getPunishTitle());
		Assert.notNull(oneFinanceMonitorPunish.getUrl());
		oneFinanceMonitorPunish.setSource("深交所");
		oneFinanceMonitorPunish.setObject("中介机构处罚与处分记录");
		initDate();
		doFetch(oneFinanceMonitorPunish, true);
		return null;
	}

	/**
	 * 提取所需信息
	 * 中介机构名称、中介机构类别、处分日期、处分类别、涉及公司代码、涉及公司简称、当事人、标题、全文
	 */
	private List<FinanceMonitorPunish> extract(String xlsName) throws Exception {
		List<FinanceMonitorPunish> lists = Lists.newLinkedList();
		String[] columeNames = {
				"orgName",    //中介机构名称
				"orgType",//中介机构类别
				"punishDate",    //处分日期
				"punishType",    //处分类别
				"companycode",    //涉及公司代码
				"companyAlias",    //涉及公司简称
				"person",        //当事人
				"title",        //标题
				"contentUri"};   //全文URI
		List<Map<String, Object>> maps = importFromXls(xlsName, columeNames);
		for (Map map : maps) {
			log.info(map.toString());
			FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
			financeMonitorPunish.setIntermediaryCategory((String) map.get("orgType"));
			financeMonitorPunish.setPunishDate((String) map.get("punishDate"));
			financeMonitorPunish.setPunishCategory((String) map.get("punishType"));
			financeMonitorPunish.setCompanyCode((String) map.get("companycode"));
			financeMonitorPunish.setCompanyShortName((String) map.get("companyAlias"));
			financeMonitorPunish.setPartyInstitution((String) map.get("person"));
			financeMonitorPunish.setPunishTitle((String) map.get("title"));
			financeMonitorPunish.setUrl("http://www.szse.cn/UpFiles/cfwj/" + (String) map.get("contentUri"));
			financeMonitorPunish.setSource("深交所");
			financeMonitorPunish.setObject("中介机构处罚与处分记录");

			try {
				//增量抓取
				if (!doFetch(financeMonitorPunish, false)) {
					FinanceMonitorPunish srcFmp = financeMonitorPunishMapper
							.selectByBizKey(financeMonitorPunish.getPrimaryKey());
					if (srcFmp.getPunishCategory().contains(financeMonitorPunish.getPunishCategory())
							&& srcFmp.getIntermediaryCategory().trim().equals(
							financeMonitorPunish.getIntermediaryCategory().trim())) {
						return lists;
					} else {
						srcFmp.setPunishCategory(financeMonitorPunish.getPunishCategory());
						srcFmp.setId(null);
						financeMonitorPunishMapper.insert(srcFmp);
					}
				}

			} catch (Exception e) {
				log.error(e.getMessage());
			}

			lists.add(financeMonitorPunish);

		}
		return lists;
	}

	private boolean doFetch(FinanceMonitorPunish financeMonitorPunish,
							Boolean isForce) throws Exception {

		financeMonitorPunish.setPartyCategory(PLATE.getPlate(
				financeMonitorPunish.getCompanyCode().substring(0, 3)).name);

		String contentFile = downLoadFile(financeMonitorPunish.getUrl());

		//详情
		String details = "";
		if (contentFile.toLowerCase().endsWith("doc")) {
			details = filterErrInfo(ocrUtil.getTextFromDoc(contentFile));
		} else if (contentFile.toLowerCase().endsWith("pdf")) {
			details = filterErrInfo(ocrUtil.getTextFromPdf(contentFile));
			if (!details.contains("经查明")) {
				try {
					details = filterErrInfo(ocrUtil.getTextFromImg(downLoadFile(financeMonitorPunish.getUrl())));
				} catch (Exception e) {
					log.debug(e.getMessage());
					return true;
				}
			}
		}
		details = details.replaceAll("\\s*", "")
				.replace(":", "：")
				.replace(",", "，")
				.replace("o", "。")
				.replace("　", "")
				.replace(" ", "")
				.replace("　　", "")
				.replace("\n", "").trim();
		financeMonitorPunish.setDetails(details);

		//当事人
		String partyPerson = "";
		String partyInstitution = "";

		if (details.contains("当事人：") && details.indexOf("当事人：") < details.indexOf("经查明，")) {
			String pString = details.substring(details.indexOf("当事人：") + 4, details.indexOf("经查明，"));

			String personArray[] = pString.contains("；") ? pString.split("；") : pString.split("。");
			for (String psub : personArray) {
				if (StrUtil.isEmpty(psub)) continue;
				psub = psub.contains("，") ? psub.substring(0, psub.indexOf("，")) : psub;
				psub = psub.contains("：") ? psub.substring(0, psub.indexOf("：")) : psub;
				if (psub.contains("公司") || psub.contains("（有限合伙）")
						|| psub.contains("事务所") || psub.contains("集团")
						|| psub.contains("实业有限") || psub.contains("联社")) {
					partyInstitution += "，" + psub;
				} else {
					partyPerson += "，" + psub;
				}
			}
		}
		if (StrUtil.isNotEmpty(partyPerson)) {
			financeMonitorPunish.setPartyPerson(partyPerson.substring(1));
		}
		if (StrUtil.isNotEmpty(partyInstitution)) {
			financeMonitorPunish.setPartyInstitution(partyInstitution.substring(1));
		} else {
			financeMonitorPunish.setPartyInstitution(null);
		}

		//违规情况
		//股票上市规则
		String pStr = "";
		int pIndx = -1;
		String[] p = {"《股票上市规则", "《创业板股票上市规则", "《证券法》", "《股示上市规则"};

		for (int i = 0; i < p.length; i++) {
			if (details.indexOf(p[i]) > -1) {
				pStr = p[i];
				pIndx = details.indexOf(p[i]);
				break;
			}
		}

		if (details.contains("经查明，") && details.indexOf("经查明，") < pIndx) {
			String tmp = details.substring(details.indexOf("经查明，") + 4, pIndx);
			tmp = tmp.substring(0, tmp.lastIndexOf("。") + 1);
			financeMonitorPunish.setIrregularities(tmp);
		}


		financeMonitorPunish.setPunishInstitution("深圳证券交易所");
		return saveOne(financeMonitorPunish, isForce);
	}

}
