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
		if (!CollectionUtils.isEmpty(lists)) {
			exportToXls("Site1.xlsx", lists);
		}
		return null;
	}

	@Override
	protected String executeOne() throws Throwable {
		log.info("*******************call site1 task for One Record**************");

		Assert.notNull(Objects.isNull(oneFinanceMonitorPunish.getPartyInstitution())
				? oneFinanceMonitorPunish.getPartyPerson() : oneFinanceMonitorPunish.getPartyInstitution());

		Assert.notNull(oneFinanceMonitorPunish.getUrl());
		Assert.notNull(oneFinanceMonitorPunish.getPunishTitle());
		oneFinanceMonitorPunish.setObject("全国中小企业股转系统-监管公告");
		oneFinanceMonitorPunish.setSource("全国中小企业股转系统");
		initDate();
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

				//当事人 从链接中提取
				String person = "";
				String disclosureTitle = contentObj.getStr("disclosureTitle");
				if (disclosureTitle.contains("撤销")) continue;
				disclosureTitle = disclosureTitle.replace("\\u201c", "“")
						.replace("\\u201d", "”");
				if (disclosureTitle.contains("关于对") && disclosureTitle.contains("采取")) {
					person = disclosureTitle.substring(3, disclosureTitle.indexOf("采取"))
							.replace("“", "")
							.replace("”", "");
				}

				String targetUrl = "http://www.neeq.com.cn" + contentObj.getStr("destFilePath");

				financeMonitorPunish.setPartyInstitution(person);
				financeMonitorPunish.setPartyPerson(person);
				financeMonitorPunish.setUrl(targetUrl);
				financeMonitorPunish.setPunishTitle(disclosureTitle);
				financeMonitorPunish.setObject("全国中小企业股转系统-监管公告");
				financeMonitorPunish.setSource("全国中小企业股转系统");

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
		String targetUrl = financeMonitorPunish.getUrl();
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
			financeMonitorPunish.setDetails(filterErrInfo(content));
			extract(content, financeMonitorPunish);
			return saveOne(financeMonitorPunish, isForce);

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

		fullTxt = fullTxt.replace(",", "，");
		//处罚文号
		String punishNo = null;
		//当事人个人
		String person = null;
		//当事人公司
		String insperson = null;
		//公司全名
		String companyFullName = null;
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

		Boolean isCompany = (financeMonitorPunish.getPunishTitle().contains("公司")
				|| financeMonitorPunish.getPunishTitle().contains("事务所"));

		//住所为空的当事人按照个人当事人来处理
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
		if (zsdindex < 0) isCompany = false;

		//违规事实关键字
		String weiguiStr = "";
		int weiguiIndex = -1;
		String[] weigui = {"违规事实如下：", "违规事实如下", "违规事实", "违规行为", "经查明"};

		for (int i = 0; i < weigui.length; i++) {
			if (fullTxt.indexOf(weigui[i]) > -1) {
				weiguiStr = weigui[i];
				weiguiIndex = fullTxt.indexOf(weigui[i]);
				break;
			}
		}

		//处罚结果补充情况
		String resultSupplementStr = "";
		int resultSupplementIndex = -1;
		String[] resultSupplement = {"天际数字应自收到本决定书之日",
				"海龙精密应自收到本决定书之日",
				"ST 展唐应自收到本",
				"你公司应在接到本决定书",
				"挂牌公司应自收到本自律监管决定书",
				"你公司应自收到本决定书之",
				"收到本决定书之", "收到本"};

		for (int i = 0; i < resultSupplement.length; i++) {
			if (fullTxt.indexOf(resultSupplement[i]) > -1) {
				resultSupplementStr = resultSupplement[i];
				resultSupplementIndex = fullTxt.indexOf(resultSupplement[i]);
				break;
			}
		}


		//当事人为公司，格式规则
		if (isCompany) {
			financeMonitorPunish.setPartyPerson(null);

			int sIndx = 0;

			String[] fddbr = {"法定代表人：", "法\n定代表人：",
					"法定\n代表人：",
					"法定代\n表人：",
					"法定代表\n人：",
					"法定代表人\n：",
					"法定代表人"};
//					"控制人"};
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
//						holder = sTmp.substring(sTmp.indexOf("；"), sTmp.lastIndexOf("。"));
					} else if (sTmp.indexOf("。") > -1) {
						address = sTmp.substring(0, sTmp.indexOf("。"));
//						holder = sTmp.substring(sTmp.indexOf("。"));
					} else {
						address = null;
						holder = null;
					}
				}

			}

			if (StringUtils.isNotEmpty(holder)) {
				if (extracterZH(holder.substring(0, 1)).length() == 0) {
					holder = holder.substring(1).trim();
				}
				if (holder.contains("，")) {
					holder = holder.substring(0, holder.indexOf("，"));
				}
				if (holder.contains("。")) {
					holder = holder.substring(0, holder.indexOf("。"));
				}
				if (holder.contains("o")) {
					holder = holder.substring(0, holder.indexOf("o"));
				}
			}

			if (StringUtils.isNotEmpty(address)) {
				if (address.contains("，")) {
					address = address.substring(0, address.indexOf("，"));
				}
				if (address.contains("。")) {
					address = address.substring(0, address.indexOf("。"));
				}

				address = address.replace(zsdStr, "");
				if (address.startsWith("地"))
					address = address.substring(1);
				address = address.replace(":", "").trim();
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

			{
				String tmp = fullTxt.substring(0, sIndx);
				if (tmp.contains(" \n \n \n")) {
					tmp = tmp.substring(tmp.indexOf(" \n \n \n"))
							.replace("\n", "")
							.replace(" ", "");
					if (tmp.contains("关于")) {
						tmp = tmp.substring(0, tmp.indexOf("关于"));
					}
					tmp = tmp.replace("碁", "").replace("号", "");
					if (tmp.length() >= 5)
						punishNo = "股转系统发[" + tmp.substring(0, 4) + "]" + tmp.substring(4) + "号";
				} else if (tmp.contains("20")) {
					tmp = tmp.substring(tmp.indexOf("20"))
							.replace("\n", "")
							.replace(" ", "");
					if (tmp.contains("关于")) {
						tmp = tmp.substring(0, tmp.indexOf("关于"));
					}
					tmp = tmp.replace("碁", "").replace("号", "");
					if (tmp.length() >= 5)
						punishNo = "股转系统发[" + tmp.substring(0, 4) + "]" + tmp.substring(4) + "号";
				}
			}

			//提取公司全名和当事人信息
			String punishTitle = financeMonitorPunish.getPunishTitle();

			if (!(punishTitle.contains("相关责任")
					|| punishTitle.contains("相关当事人")
					|| punishTitle.contains("实际控制人")
					|| punishTitle.contains("相关信息披露责任人")
					|| punishTitle.contains("有限公司、")
					|| punishTitle.contains("有限公司及")
					|| punishTitle.contains("公开谴责，"))) {
				String tmp = fullTxt.substring(sIndx);

				insperson = tmp.substring(0, tmp.indexOf("，"))
						.replace("当事人：", "")
						.replace("当事人", "");
				if (insperson.contains("（")) {
					companyFullName = insperson.substring(0, insperson.indexOf("（"));
				} else {
					companyFullName = insperson;
				}
				if (StringUtils.isNotEmpty(companyFullName))
					companyFullName = companyFullName.replace("\n", "")
							.replace(":", "");
				//当事人为公司下个人， 不需要法人
			} else {
				if (fullTxt.indexOf("经查明") > sIndx) {
					String tmp = fullTxt.substring(sIndx, fullTxt.indexOf("经查明"));
					if (tmp.contains("2 幢 1003 室；")) {
						person = tmp.substring(tmp.indexOf("2 幢 1003 室；") + "2 幢 1003 室；".length()).trim();
					} else if (tmp.contains("北京市海淀区永丰屯 538 号 2 号楼 211 室；")) {
						person = tmp.substring(tmp.indexOf("北京市海淀区永丰屯 538 号 2 号楼 211 室；")
								+ "北京市海淀区永丰屯 538 号 2 号楼 211 室；".length()).trim();
					} else if (tmp.contains("南宁广告产业园 A 栋 306 室；")) {
						person = tmp.substring(tmp.indexOf("南宁广告产业园 A 栋 306 室；")
								+ "南宁广告产业园 A 栋 306 室；".length()).trim();
					} else if (tmp.contains("上海市福山路 500 号城建国际中心 29 楼；")) {
						person = tmp.substring(tmp.indexOf("上海市福山路 500 号城建国际中心 29 楼；")
								+ "上海市福山路 500 号城建国际中心 29 楼；".length()).trim();
					} else if (tmp.contains("法定代表人：魏永良")) {
						person = tmp.substring(tmp.indexOf("法定代表人：魏永良")
								+ "法定代表人：魏永良".length()).trim();
					} else if (tmp.contains("。")) {
						person = tmp.substring(tmp.indexOf("。") + 1).trim();
					}


					if (StringUtils.isNotEmpty(person) && person.startsWith("。"))
						person = person.substring(1);

					holder = null;
				}
			}


			if (zsdindex > -1) {
				holderAddition = fullTxt.substring(sIndx, zsdindex)
						.replace("当事人：", "").trim().replace("\n", "");
				if (holderAddition.endsWith("，"))
					holderAddition = holderAddition.substring(0, holderAddition.length() - 1);
			}

			sIndx = fullTxt.indexOf("违反");
			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("违反") > -1) {
				rule = fullTxt.substring(fullTxt.indexOf("违反"), fullTxt.indexOf("鉴于"));
			}

			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("全国股转公司") > -1) {
				result = fullTxt.substring(fullTxt.indexOf("鉴于"), fullTxt.lastIndexOf("全国股转公司"));
			}

			if (resultSupplementIndex > -1 && fullTxt.lastIndexOf("全国股转公司") > -1) {
				resultAddition = fullTxt.substring(resultSupplementIndex, fullTxt.lastIndexOf("全国股转公司")).trim();
				resultAddition = filterErrInfo(resultAddition);
			}

			financeMonitorPunish.setDomicile(address);
			financeMonitorPunish.setPartyInstitution(companyFullName);
			financeMonitorPunish.setPartyPerson(person);
			financeMonitorPunish.setCompanyFullName(companyFullName);
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

			{
				String tmp = fullTxt.substring(0, sIndx);
				if (tmp.contains(" \n \n \n \n")) {
					tmp = tmp.substring(tmp.indexOf(" \n \n \n \n"))
							.replace("\n", "")
							.replace(" ", "");
					if (tmp.contains("关于")) {
						tmp = tmp.substring(0, tmp.indexOf("关于"));
					}
					tmp = tmp.replace("碁", "").replace("号", "");
					if (tmp.length() >= 5)
						punishNo = "股转系统发[" + tmp.substring(0, 4) + "]" + tmp.substring(4) + "号";
				} else if (tmp.contains("20")) {
					tmp = tmp.substring(tmp.indexOf("20"))
							.replace("\n", "")
							.replace(" ", "");
					if (tmp.contains("关于")) {
						tmp = tmp.substring(0, tmp.indexOf("关于"));
					}
					tmp = tmp.replace("碁", "").replace("号", "");
					if (tmp.length() >= 5)
						punishNo = "股转系统发[" + tmp.substring(0, 4) + "]" + tmp.substring(4) + "号";
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
				if (holderAddition.endsWith("，"))
					holderAddition = holderAddition.substring(0, holderAddition.length() - 1);
			}

			sIndx = fullTxt.indexOf("违反");
			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("违反") > -1) {
				rule = fullTxt.substring(fullTxt.indexOf("违反"), fullTxt.indexOf("鉴于"));
			}

			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("全国股转公司") > -1) {
				result = fullTxt.substring(fullTxt.indexOf("鉴于"), fullTxt.indexOf("全国股转公司"));
			}

			resultAddition = null;
			financeMonitorPunish.setPartyPersonDomi(address.trim());
		}

		//违规情况
		if (fullTxt.indexOf("违反") > -1 && weiguiIndex < fullTxt.indexOf("违反")) {
			violation = fullTxt.substring(weiguiIndex, fullTxt.indexOf("违反")).replace(weiguiStr, "");
			if (violation.lastIndexOf("。") > violation.lastIndexOf("，")) {
				violation = violation.substring(0, violation.lastIndexOf("。") + 1);
			} else {
				if (violation.lastIndexOf("，") > 0) {
					violation = violation.substring(0, violation.lastIndexOf("，"));
				}
			}
		} else if (fullTxt.indexOf("经查明") < fullTxt.indexOf("违反")) {
			weiguiIndex = fullTxt.indexOf("经查明") + 4;
			violation = fullTxt.substring(weiguiIndex, fullTxt.indexOf("违反"));
			if (violation.lastIndexOf("。") > violation.lastIndexOf("，")) {
				violation = violation.substring(0, violation.lastIndexOf("。") + 1);
			} else {
				if (violation.lastIndexOf("，") > 0) {
					violation = violation.substring(0, violation.lastIndexOf("，"));
				}
			}
		}
		if (StringUtils.isNotEmpty(violation) && violation.startsWith("：")) {
			violation = violation.substring(1);
		}

		violation = filterErrInfo(violation);
		rule = filterErrInfo(rule);
		result = filterErrInfo(result);

		financeMonitorPunish.setPunishNo(punishNo);
		financeMonitorPunish.setLegalRepresentative(holder);
		financeMonitorPunish.setUnicode(commonCode.trim());
		financeMonitorPunish.setPartySupplement(holderAddition.trim());
		financeMonitorPunish.setIrregularities(violation.trim());
		financeMonitorPunish.setRelatedLaw(rule.trim());
		financeMonitorPunish.setPunishResult(result.trim());
		financeMonitorPunish.setPunishResultSupplement(resultAddition);

		return;
	}

}
