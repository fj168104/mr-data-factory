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
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by feng on 18-3-16
 * 深交所
 * 信息披露->上市公司信息->上市公司诚信档案->处罚与处分记录
 */

@Slf4j
@Component("site7")
@Scope("prototype")
public class SiteTaskImpl_7 extends SiteTaskExtend {

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
		log.info("*******************call site7 task**************");
		//可直接载excel
		String exlUri = "http://www.szse.cn/szseWeb/ShowReport.szse?SHOWTYPE=xlsx&CATALOGID=1759_cxda&tab1PAGENO=1&ENCODE=1&TABKEY=tab1";

		//download pdf并解析成文本
		String xlsName = downLoadFile(exlUri, "处罚与处分记录.xlsx");
		log.info("fileName=" + xlsName);
		List<FinanceMonitorPunish> lists = extract(xlsName);
		if (!CollectionUtils.isEmpty(lists)) {
			exportToXls("site7.xlsx", lists);
		}

		return null;
	}

	@Override
	protected String executeOne() throws Throwable {
		log.info("*******************call site7 task for One Record**************");

		Assert.notNull(oneFinanceMonitorPunish.getCompanyCode());
		Assert.notNull(oneFinanceMonitorPunish.getCompanyShortName());
		Assert.notNull(oneFinanceMonitorPunish.getPunishDate());
		Assert.notNull(oneFinanceMonitorPunish.getPunishCategory());
		Assert.notNull(oneFinanceMonitorPunish.getPunishTitle());
		Assert.notNull(oneFinanceMonitorPunish.getUrl());
		Assert.notNull(oneFinanceMonitorPunish.getPartyInstitution());
		oneFinanceMonitorPunish.setSource("深交所");
		oneFinanceMonitorPunish.setObject("上市公司处罚与处分记录");
		initDate();
		doFetch(oneFinanceMonitorPunish, true);
		return null;
	}

	/**
	 * 提取所需信息
	 * 公司代码、公司简称、处分日期、处分类别、当事人、标题、全文
	 */
	private List<FinanceMonitorPunish> extract(String xlsName) throws Exception {
		List<FinanceMonitorPunish> lists = Lists.newLinkedList();
		String[] columeNames = {
				"companycode",    //公司代码
				"companyAlias",    //涉及公司简称
				"punishDate",    //处分日期
				"punishType",    //处分类别
				"person",        //当事人
				"title",        //标题
				"contentUri"};   //全文URI
		List<Map<String, Object>> maps = importFromXls(xlsName, columeNames);
		for (Map map : maps) {
			log.info(map.toString());
			FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
			financeMonitorPunish.setCompanyCode((String) map.get("companycode"));
			financeMonitorPunish.setCompanyShortName((String) map.get("companyAlias"));
			financeMonitorPunish.setPunishDate((String) map.get("punishDate"));
			financeMonitorPunish.setPunishCategory(((String) map.get("punishType")).substring(0, 4));
			financeMonitorPunish.setPunishTitle((String) map.get("title"));
			financeMonitorPunish.setUrl("http://www.szse.cn/UpFiles/cfwj/" + map.get("contentUri"));
			financeMonitorPunish.setPartyInstitution((String) map.get("person"));
			financeMonitorPunish.setSource("深交所");
			financeMonitorPunish.setObject("上市公司处罚与处分记录");

			try {
				//增量抓取
				if (!doFetchForRetry(financeMonitorPunish, false)) {
					FinanceMonitorPunish srcFmp = financeMonitorPunishMapper
							.selectByBizKey(financeMonitorPunish.getPrimaryKey());
					if (srcFmp.getPunishCategory().contains(financeMonitorPunish.getPunishCategory())) {
						return lists;
					} else {
//					srcFmp.setPunishCategory(srcFmp.getPunishCategory()
//							+ "|" + financeMonitorPunish.getPunishCategory());
						srcFmp.setPunishCategory(financeMonitorPunish.getPunishCategory());
						srcFmp.setId(null);
						financeMonitorPunishMapper.insert(srcFmp);
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

	@Override
	protected boolean doFetch(FinanceMonitorPunish financeMonitorPunish,
							Boolean isForce) throws Exception {
		String person = financeMonitorPunish.getPartyInstitution();
		financeMonitorPunish.setPartyInstitution(null);
		//当事人（公司）
		String partyInstitution = "";
		//当事人（个人）
		String partyPerson = "";
		if (StrUtil.isNotEmpty(person)) {
			String personArray[] = person.split("、");
			for (String psub : personArray) {
				if (psub.contains("公司") || psub.contains("（有限合伙）")
						|| psub.contains("处理厂") || psub.contains("集团")
						|| psub.contains("实业有限") || psub.contains("联社")) {
					if (StrUtil.isNotEmpty(partyInstitution)) {
						partyInstitution += "，";
					}
					if (psub.contains("公司") && !psub.endsWith("公司")) {
						psub = psub.substring(0, psub.indexOf("公司") + 2);
						String tmp = psub.substring(psub.indexOf("公司") + 2);
						if (StrUtil.isNotEmpty(partyPerson)) {
							partyPerson += "，";
						}
						partyPerson += tmp.replace("和", "，")
								.replace("及其股东", "");
					}
					partyInstitution += psub;
				} else {
					if (StrUtil.isNotEmpty(partyPerson)) {
						partyPerson += "，";
					}
					partyPerson += psub;
				}
			}
			if (StrUtil.isNotEmpty(partyPerson)) {
				financeMonitorPunish.setPartyPerson(partyPerson);
			}
			if (StrUtil.isNotEmpty(partyInstitution)) {
				financeMonitorPunish.setPartyInstitution(partyInstitution);
			}
		}

		financeMonitorPunish.setPartyCategory(PLATE.getPlate(
				financeMonitorPunish.getCompanyCode().substring(0, 3)).name);
		//公司全称
		String companyFullName = null;
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(financeMonitorPunish.getPunishTitle())) {
			String title = financeMonitorPunish.getPunishTitle();
			if (title.contains("关于对") && title.contains("公司")) {
				companyFullName = title.substring(title.indexOf("关于对"), title.lastIndexOf("公司") + 2);
				financeMonitorPunish.setCompanyFullName(companyFullName.replace("关于对", ""));
			}
		}

		String contentFile = downLoadFile(financeMonitorPunish.getUrl());

		//详情
		String details = "";
		if (contentFile.toLowerCase().endsWith("doc")) {
			details = filterErrInfo(ocrUtil.getTextFromDoc(contentFile));
		} else if (contentFile.toLowerCase().endsWith("pdf")) {
			details = filterErrInfo(ocrUtil.getTextFromPdf(contentFile));
			if (!details.contains("经查明")) {
				details = filterErrInfo(ocrUtil.getTextFromImg(downLoadFile(financeMonitorPunish.getUrl())));
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
		processSpecial(financeMonitorPunish);
		return saveOne(financeMonitorPunish, isForce);
	}

	/**
	 * 特殊格式处理
	 *
	 * @param financeMonitorPunish
	 */
	private void processSpecial(FinanceMonitorPunish financeMonitorPunish) {
		String person = financeMonitorPunish.getPartyPerson();
		String partyInstitution = financeMonitorPunish.getPartyInstitution();

		if (financeMonitorPunish.getUrl().contains("http://www.szse.cn/UpFiles/cfwj/2010-09-07_002034111.doc")) {
			partyInstitution = null;
			person = "周信钢，圣美伦，李欣，周晨";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.szse.cn/UpFiles/cfwj/2010-03-01_000587637.doc")) {
			partyInstitution = "光明集团股份有限公司，上海鸿扬投资管理有限公司";
			person = null;
		}
		if (financeMonitorPunish.getUrl().contains("http://www.szse.cn/UpFiles/cfwj/2011-09-21_000955679.doc")) {
			partyInstitution = "海南筑华科工贸有限公司";
			person = null;
		}
		if (financeMonitorPunish.getUrl().contains("http://www.szse.cn/UpFiles/cfwj/2011-09-21_000056680.doc")) {
			partyInstitution = "深圳茂业商厦有限公司，大华投资（中国）有限公司";
			person = null;
		}

		financeMonitorPunish.setPartyPerson(person);
		financeMonitorPunish.setPartyInstitution(partyInstitution);
	}

}
