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
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * Created by feng on 18-3-16
 * 全国中小企业股转系统
 * 全国中小企业股转系统-监管公告
 */

@Slf4j
@Component("site1")
@Scope("prototype")
public class SiteTaskImpl_1 extends SiteTaskExtend {

	protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);

	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	@Override
	protected String execute() throws Throwable {
		log.info("*******************call site1 task**************");
//		String targetUri = "http://www.neeq.com.cn/uploads/1/file/public/201802/20180226182405_lc6vjyqntd.pdf";
		List<FinanceMonitorPunish> lists = extractPage();
		if(!CollectionUtils.isEmpty(lists)) {
			exportToXls("Site1.xlsx", lists);
		}
		return null;
	}

	@Override
	protected String executeOne() throws Throwable {
		log.info("*******************call site1 task for One Record**************");
		if (org.springframework.util.StringUtils.isEmpty(oneFinanceMonitorPunish.getPartyInstitution())) {
			oneFinanceMonitorPunish.setPartyInstitution(oneFinanceMonitorPunish.getPartyPerson());
			oneFinanceMonitorPunish.setPartyPerson(oneFinanceMonitorPunish.getPartyPerson());
		} else {
			oneFinanceMonitorPunish.setPartyInstitution(oneFinanceMonitorPunish.getPunishInstitution());
			oneFinanceMonitorPunish.setPartyPerson(oneFinanceMonitorPunish.getPunishInstitution());
		}

		Assert.notNull(oneFinanceMonitorPunish.getPunishInstitution());
		if(StringUtils.isEmpty(oneFinanceMonitorPunish.getCompanyFullName())){
			oneFinanceMonitorPunish.setCompanyFullName(oneFinanceMonitorPunish.getPunishInstitution());
		}
		Assert.notNull(oneFinanceMonitorPunish.getSource());
		Assert.notNull(oneFinanceMonitorPunish.getPunishTitle());
		oneFinanceMonitorPunish.setObject("全国中小企业股转系统-监管公告");
		doFetch(oneFinanceMonitorPunish, true);
		return null;
	}

	/**
	 * 提取所需要的信息
	 * 当事人、公司、住所地、法定代表人、一码通代码（当事人为个人）、当事人补充情况、违规情况、相关法规、处罚结果、处罚结果补充情况
	 */
	private List<FinanceMonitorPunish> extractPage() throws Exception {
		List<FinanceMonitorPunish> lists = Lists.newLinkedList();

		String url = "http://www.neeq.com.cn/disclosureInfoController/infoResult.do";
		java.util.Map<String, String> requestParams = Maps.newHashMap();
		requestParams.put("callback", "jQuery18305898860958323444_1521941846680");
		requestParams.put("disclosureType", "8");
		requestParams.put("companyCd", "公司名称/拼音/代码");
		requestParams.put("keyword", "关键字");
//		requestParams.put("startTime", DateUtil.toString(new java.util.Date(), DatePattern.NORM_DATE_PATTERN));
//		requestParams.put("endTime", DateUtil.toString(new java.util.Date(), DatePattern.NORM_DATE_PATTERN));

		Integer pageCount = Integer.MAX_VALUE;
		for (int pageNo = 0; pageNo <= pageCount; pageNo++) {
			log.info("page:" + pageNo);
			requestParams.put("page", String.valueOf(pageNo));
			String bodyStr = postData(url, requestParams)
					.replace("jQuery18305898860958323444_1521941846680([", "");
			bodyStr = bodyStr.substring(0, bodyStr.length() - 2);
			JSONObject jsonObject = JSONUtil.parseObj(bodyStr);
			JSONObject listInfoObj = jsonObject.getJSONObject("listInfo");
			pageCount = Integer.parseInt(listInfoObj.getStr("totalPages"));
			JSONArray contentArray = listInfoObj.getJSONArray("content");
			for (int i = 0; i < contentArray.size(); i++) {
				FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();

				JSONObject contentObj = contentArray.getJSONObject(i);
				//公司
				String company = contentObj.getStr("companyName");
				//当事人 从链接中提取
				String person = "";
				String disclosureTitle = contentObj.getStr("disclosureTitle");
				if (disclosureTitle.contains("撤销")) continue;
				if (disclosureTitle.contains("关于对") && disclosureTitle.contains("采取")) {
					person = disclosureTitle.substring(3, disclosureTitle.indexOf("采取"))
							.replace("“", "")
							.replace("”", "");
				}

				if (StringUtils.isEmpty(person.trim())) {
					person = company;
				}

				String targetUrl = "http://www.neeq.com.cn" + contentObj.getStr("destFilePath");

				financeMonitorPunish.setPartyInstitution(person);
				financeMonitorPunish.setPartyPerson(person);
				financeMonitorPunish.setCompanyFullName(company);
				financeMonitorPunish.setSource(targetUrl);
				financeMonitorPunish.setPunishTitle(disclosureTitle);
				financeMonitorPunish.setObject("全国中小企业股转系统-监管公告");
				//增量抓取
				if (!doFetch(financeMonitorPunish, false)) {
					return lists;
				}

				lists.add(financeMonitorPunish);
			}

		}
		return lists;
	}

	/**
	 * 抓取并解析单条数据
	 * map[person; company; destFilePath]
	 *
	 * @param isForce              false：存在就不抓取， true：不管存在于否都抓取
	 * @param financeMonitorPunish
	 * @return true:处理成功数据  false：未处理数据
	 */
	private boolean doFetch(FinanceMonitorPunish financeMonitorPunish, Boolean isForce) throws Exception {
		String targetUrl = financeMonitorPunish.getSource();
		log.info("targetUrl:" + targetUrl);
		String content = "";
		try {
			String fileName = downLoadFile(targetUrl);
			if (fileName.toLowerCase().endsWith("doc")) {
				content = ocrUtil.getTextFromDoc(fileName);
			} else if (fileName.toLowerCase().endsWith("pdf")) {
				content = ocrUtil.getTextFromPdf(fileName);
				if (!content.contains("当事人")) {
					fileName = downLoadFile(targetUrl);
					content = ocrUtil.getTextFromImg(fileName);
				}
			} else {
				log.warn("url{} is not doc or pdf", content);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		try {
			extract(content, financeMonitorPunish);
			String primaryKey = buildFinanceMonitorPunishBizKey(financeMonitorPunish);
			if (isForce || Objects.isNull(financeMonitorPunishMapper.selectByBizKey(primaryKey))) {
				insertOrUpdate(financeMonitorPunish);
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return true;
	}

	/**
	 * 提取所需要的信息 PDF
	 * 当事人、公司、住所地、法定代表人、一码通代码（当事人为个人）、当事人补充情况、违规情况、相关法规、处罚结果、处罚结果补充情况
	 */
	private void extract(String fullTxt, FinanceMonitorPunish financeMonitorPunish) {

		//住所地
		String address = "";
		//法定代表人
		String holder = "";
		//一码通代码（当事人为个人）
		String commonCode = "";
		//当事人补充情况
		String holderAddition = "";
		//违规情况
		String violation = "";
		//相关法规
		String rule = "";
		//处罚结果
		String result = "";
		//处罚结果补充情况
		String resultAddition = "";

//		Boolean isCompany = StringUtils.isNotEmpty(map.get("company"))
//				|| map.get("person").contains("公司")
//				|| map.get("person").contains("事务所");    //当事人是否为公司
		Boolean isCompany = StringUtils.isNotEmpty(financeMonitorPunish.getPartyInstitution())
				&& (financeMonitorPunish.getPartyInstitution().contains("公司")
				|| financeMonitorPunish.getPartyInstitution().contains("事务所"));

		//当事人为公司，格式规则
		if (isCompany) {
			financeMonitorPunish.setPartyPerson(null);
			int sIndx = 0;
			String[] zsd = {"住所地：", "住\n所地：", "住所\n地：", "住所地\n：",
					"住所：", "住所",
					"注\n册地：", "注册\n地：", "注册地\n：", "注册地：", "注册地址："};
			String zsdStr = "";
			int zsdindex = -1;
			for (int i = 0; i < zsd.length; i++) {
				if (fullTxt.indexOf(zsd[i]) > -1) {
					zsdStr = zsd[i];
					zsdindex = fullTxt.indexOf(zsd[i]);
					break;
				}
			}
			String[] fddbr = {"法定代表人：", "法\n定代表人：",
					"法定\n代表人：",
					"法定代\n表人：",
					"法定代表\n人：",
					"法定代表人\n：",
					"法定代表人",
					"控制人"};
			String fddbrStr = "";
			int fddbrIndex = -1;
			if (fullTxt.indexOf("经查明") < 0) return;
			String fddTxt = fullTxt.substring(0, fullTxt.indexOf("经查明"));
			for (int i = 0; i < fddbr.length; i++) {
				if (fddTxt.indexOf(fddbr[i]) > -1) {
					fddbrStr = fddbr[i];
					fddbrIndex = fddTxt.indexOf(fddbr[i]);
//					break;
				}
			}


			if (fddbrIndex > -1) {
				if (zsdindex > -1) {
					if (zsdindex < fddbrIndex) {
						address = fullTxt.substring(zsdindex, fddbrIndex)
								.replace(zsdStr, "").trim();
						address = address.substring(0, address.length() - 1).replace("\n", "");
						holder = fullTxt.substring(fddbrIndex, fullTxt.indexOf("经查明"))
								.replace(fddbrStr, "")
								.trim().replace("\n", "");
					} else {
						holder = fullTxt.substring(fddbrIndex, zsdindex)
								.replace(fddbrStr, "").trim();

						holder = holder.substring(0, holder.length() - 1).replace("\n", "");

						address = fullTxt.substring(zsdindex, fullTxt.indexOf("经查明"))
								.replace(zsdStr, "")
								.trim().replace("\n", "");
					}

				}

			} else {
				if (zsdindex > -1 && fullTxt.indexOf("经查明") > -1) {
					String sTmp = fullTxt.substring(zsdindex, fullTxt.indexOf("经查明"));
					if (sTmp.indexOf("；") > -1 && sTmp.indexOf("。") > -1) {
						address = sTmp.substring(0, sTmp.indexOf("；"));
						holder = sTmp.substring(sTmp.indexOf("；"), sTmp.lastIndexOf("。"));
					} else if (sTmp.indexOf("。") > -1) {
						address = sTmp.substring(0, sTmp.indexOf("。"));
						holder = sTmp.substring(sTmp.indexOf("。"));
					} else {
						address = "";
						holder = "";
					}
				}

			}

			//公司一码通代码处理
			String[] ymtdm = {"一码通代码：", "一\n码通代码：", "一码\n通代码：", "一码通\n代码：", "一码通代\n码：", "一码通代码\n：",};
			String ymtdmStr = "";
			int ymtdmIndex = -1;
			for (int i = 0; i < ymtdm.length; i++) {
				if (fullTxt.indexOf(ymtdm[i]) > -1) {
					ymtdmStr = ymtdm[i];
					ymtdmIndex = fullTxt.indexOf(ymtdm[i]);
				}
			}

			if (ymtdmIndex > -1) {
				String sTmp = fullTxt.substring(ymtdmIndex)
						.replace(ymtdmStr, "").trim();
				commonCode = sTmp.substring(0, sTmp.indexOf("）"));
			}

			sIndx = fullTxt.indexOf("当事人：") == -1 ? fullTxt.indexOf("当事人") : fullTxt.indexOf("当事人：");
			if (sIndx == -1) sIndx = fullTxt.indexOf("的决定");
			if (sIndx == -1) return;

			if (fullTxt.indexOf("住所地") > -1) {
				holderAddition = fullTxt.substring(sIndx, fullTxt.indexOf("住所地"))
						.replace("当事人：", "").trim().replace("\n", "");
			}

			sIndx = fullTxt.indexOf("经查明");
			if (sIndx > -1 && fullTxt.indexOf("违反了") > -1) {
				String sTmp = fullTxt.substring(sIndx, fullTxt.indexOf("违反了"));
				violation = sTmp.substring(0, sTmp.lastIndexOf("\n")).replace("经查明", "")
						.trim().replace("\n", "");
			}

			sIndx = fullTxt.indexOf("违反了");
			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("违反了") > -1) {
				rule = fullTxt.substring(fullTxt.indexOf("违反了"), fullTxt.indexOf("鉴于"));
			}

			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("全国股转公司") > -1) {
				result = fullTxt.substring(fullTxt.indexOf("鉴于"), fullTxt.lastIndexOf("全国股转公司"));
			}

			sIndx = fullTxt.indexOf("你公司应自收到本决定书之");
			if (fullTxt.indexOf("你公司应自收到本决定书之") > -1 && fullTxt.indexOf("全国股转公司") > -1) {
				resultAddition = fullTxt.substring(sIndx, fullTxt.indexOf("全国股转公司"));
			}
			financeMonitorPunish.setDomicile(address.trim());
		} else {
			//当事人为个人
			financeMonitorPunish.setPartyInstitution(null);
			address = "";
			holder = "";
			int sIndx = fullTxt.indexOf("当事人：") == -1 ? fullTxt.indexOf("当事人") : fullTxt.indexOf("当事人：");
			if (sIndx == -1) sIndx = fullTxt.indexOf("的决定");
			if (sIndx == -1) return;

			String[] ymtdm = {"一码通代码：", "一\n码通代码：", "一码\n通代码：", "一码通\n代码：", "一码通代\n码：", "一码通代码\n：",};
			String ymtdmStr = "";
			int ymtdmIndex = -1;
			for (int i = 0; i < ymtdm.length; i++) {
				if (fullTxt.indexOf(ymtdm[i]) > -1) {
					ymtdmStr = ymtdm[i];
					ymtdmIndex = fullTxt.indexOf(ymtdm[i]);
				}
			}

			if (ymtdmIndex > -1) {
				String sTmp = fullTxt.substring(ymtdmIndex)
						.replace(ymtdmStr, "").trim();
				commonCode = sTmp.substring(0, sTmp.indexOf("）"));
			}

			if (fullTxt.indexOf("经查明") > -1) {
				holderAddition = fullTxt.substring(sIndx, fullTxt.indexOf("经查明"))
						.replace("当事人：", "").trim().replace("\n", "");
			}

			sIndx = fullTxt.indexOf("经查明");
			if (fullTxt.indexOf("违反了") > -1) {
				String sTmp = fullTxt.substring(sIndx, fullTxt.indexOf("违反了"));
				violation = sTmp.substring(0, sTmp.lastIndexOf("\n")).replace("经查明，", "")
						.trim().replace("\n", "");
			}

			sIndx = fullTxt.indexOf("违反了");
			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("违反了") > -1) {
				rule = fullTxt.substring(fullTxt.indexOf("违反了"), fullTxt.indexOf("鉴于"));
			}

			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("全国股转公司") > -1) {
				result = fullTxt.substring(fullTxt.indexOf("鉴于"), fullTxt.indexOf("全国股转公司"));
			}

			resultAddition = "";
			financeMonitorPunish.setPartyPersonDomi(address.trim());
		}

		financeMonitorPunish.setLegalRepresentative(holder.trim());
		financeMonitorPunish.setUnicode(commonCode.trim());
		financeMonitorPunish.setPartySupplement(holderAddition.trim());
		financeMonitorPunish.setIrregularities(violation.trim());
		financeMonitorPunish.setRelatedLaw(rule.trim());
		financeMonitorPunish.setPunishResult(result.trim());
		financeMonitorPunish.setPunishResultSupplement(resultAddition.trim());

		return;
	}

}
