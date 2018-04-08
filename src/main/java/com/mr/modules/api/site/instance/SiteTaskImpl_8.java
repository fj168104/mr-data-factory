package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
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

			//增量抓取
			if (!doFetch(financeMonitorPunish, false)) {
				FinanceMonitorPunish srcFmp = financeMonitorPunishMapper
						.selectByBizKey(financeMonitorPunish.getPrimaryKey());
				if (srcFmp.getPunishCategory().contains(financeMonitorPunish.getPunishCategory())) {
					return lists;
				} else {
					srcFmp.setPunishCategory(srcFmp.getPunishCategory()
							+ "|" + financeMonitorPunish.getPunishCategory());
					financeMonitorPunishMapper.updateByPrimaryKey(srcFmp);
					financeMonitorPunish.setPunishCategory(srcFmp.getPunishCategory());
				}
			}

			lists.add(financeMonitorPunish);

		}
		return lists;
	}

	private boolean doFetch(FinanceMonitorPunish financeMonitorPunish,
							Boolean isForce) throws Exception {
		String person = financeMonitorPunish.getPartyInstitution();
		financeMonitorPunish.setPartyInstitution(null);
		//当事人（公司）
		String partyInstitution = "";
		//当事人（个人）
		String partyPerson = "";
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(person)) {
			String personArray[] = person.split("、");
			for (String psub : personArray) {
				if (psub.contains("公司") || psub.contains("事务所")) {
					if (org.apache.commons.lang3.StringUtils.isNotEmpty(partyInstitution)) {
						partyInstitution += "、";
					}
					partyInstitution += psub;
				} else {
					if (org.apache.commons.lang3.StringUtils.isNotEmpty(partyPerson)) {
						partyPerson += "、";
					}
					partyPerson += psub;
				}
			}
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(partyPerson)) {
				financeMonitorPunish.setPartyPerson(partyPerson);
			}
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(partyInstitution)) {
				financeMonitorPunish.setPartyInstitution(partyInstitution);
			}
		}

		financeMonitorPunish.setPartyCategory(PLATE.getPlate(
				financeMonitorPunish.getCompanyCode().substring(0, 3)).name);
		//公司全称
		String companyFullName = null;
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(financeMonitorPunish.getPunishTitle())) {
			String title = financeMonitorPunish.getPunishTitle();
			//公司或者事务所关键字
			String corpStr = "";
			int corpIndex = -1;
			String[] corp = {"公司", "事务所"};

			for (int i = 0; i < corp.length; i++) {
				if (title.indexOf(corp[i]) > -1) {
					corpStr = corp[i];
					corpIndex = title.indexOf(corp[i]) + corpStr.length() + 1;
					break;
				}
			}


			if (title.contains("关于对") && corpIndex > 0) {
				companyFullName = title.substring(title.indexOf("关于对") + 3, corpIndex);
			}
		}

		String contentFile = downLoadFile(financeMonitorPunish.getUrl());

		//详情
		if (contentFile.toLowerCase().endsWith("doc")) {
			financeMonitorPunish.setDetails(filterErrInfo(ocrUtil.getTextFromDoc(contentFile)));
		} else if (contentFile.toLowerCase().endsWith("pdf")) {
			financeMonitorPunish.setDetails(filterErrInfo(ocrUtil.getTextFromPdf(contentFile)));
		}

		return saveOne(financeMonitorPunish, isForce);
	}

}
