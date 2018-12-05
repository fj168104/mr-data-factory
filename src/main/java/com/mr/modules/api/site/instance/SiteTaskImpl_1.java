package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.util.StrUtil;
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

		Assert.notNull(oneFinanceMonitorPunish.getUrl());
		Assert.notNull(oneFinanceMonitorPunish.getPunishTitle());

		String punishTitle = oneFinanceMonitorPunish.getPunishTitle();
		if (punishTitle.contains("撤销")) return null;
		punishTitle = punishTitle.replace("\\u201c", "“")
				.replace("\\u201d", "”");
		//当事人 从链接中提取
		String person = getPartyByTitle(punishTitle);
		oneFinanceMonitorPunish.setObject("全国中小企业股转系统-监管公告");
		oneFinanceMonitorPunish.setSource("全国中小企业股转系统");
		oneFinanceMonitorPunish.setPartyPerson(person);
		oneFinanceMonitorPunish.setPartyInstitution(person);

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
		java.util.Map<String, String> headParams = Maps.newHashMap();
		headParams.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
//
		requestParams.put("disclosureType", "8");
		requestParams.put("companyCd", "公司名称/拼音/代码");
		requestParams.put("keyword", "关键字");
//		requestParams.put("startTime", DateUtil.toString(new java.util.Date(), DatePattern.NORM_DATE_PATTERN));
//		requestParams.put("endTime", DateUtil.toString(new java.util.Date(), DatePattern.NORM_DATE_PATTERN));

		Integer pageCount = Integer.MAX_VALUE;
		for (int pageNo = 0; pageNo <= pageCount; pageNo++) {
			log.info("page:" + pageNo);
			requestParams.put("page", String.valueOf(pageNo));
			String strTime = String.format("jQuery183079464724343002%d_%d", 10 + pageNo, System.currentTimeMillis());
			requestParams.put("callback", strTime);
			String bodyStr = null;
			int waitTime = 0;
			while (StrUtil.isEmpty(bodyStr) && waitTime++ < 10) {
				try {
					bodyStr = postData(url, requestParams, headParams)
							.replace(strTime + "([", "");
				} catch (Exception e) {
					log.warn("site can not be visited:" + e.getMessage() + " | sleep time:" + waitTime * 10000);
					Thread.sleep(waitTime * 10000);
				}
			}


			bodyStr = bodyStr.substring(0, bodyStr.length() - 2);

			JSONObject jsonObject = JSONUtil.parseObj(bodyStr);
			JSONObject listInfoObj = jsonObject.getJSONObject("listInfo");
			pageCount = Integer.parseInt(listInfoObj.getStr("totalPages"));
			JSONArray contentArray = listInfoObj.getJSONArray("content");
			for (int i = 0; i < contentArray.size(); i++) {
				FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();

				JSONObject contentObj = contentArray.getJSONObject(i);


				String disclosureTitle = contentObj.getStr("disclosureTitle");
				if (disclosureTitle.contains("撤销")) continue;
				disclosureTitle = disclosureTitle.replace("\\u201c", "“")
						.replace("\\u201d", "”");
				//当事人 从链接中提取
				String person = getPartyByTitle(disclosureTitle);
				String targetUrl = "http://www.neeq.com.cn" + contentObj.getStr("destFilePath");

				financeMonitorPunish.setPartyInstitution(person);
				financeMonitorPunish.setPartyPerson(person);
				financeMonitorPunish.setUrl(targetUrl);
				financeMonitorPunish.setPunishTitle(disclosureTitle);
				financeMonitorPunish.setObject("全国中小企业股转系统-监管公告");
				financeMonitorPunish.setSource("全国中小企业股转系统");

				Thread.sleep(500);
				//增量抓取
				try {
					if (!doFetchForRetry(financeMonitorPunish, false)) {
						return lists;
					}
				}catch (Exception e){
					writeBizErrorLog(financeMonitorPunish.getUrl(), e.getMessage());
					continue;
				}


				lists.add(financeMonitorPunish);
			}

		}
		return lists;
	}

	private String getPartyByTitle(String punishTitle) {
		String person = null;
		String 关于对 = null;
		if (punishTitle.contains("关于给予")) {
			关于对 = "关于给予";
		} else if (punishTitle.contains("关于对")) {
			关于对 = "关于对";
		}

		if (StrUtil.isNotEmpty(关于对) && punishTitle.contains("采取")) {
			person = punishTitle.substring(关于对.length(), punishTitle.indexOf("采取"))
					.replace("“", "")
					.replace("”", "");
		}
		return person;
	}

	/**
	 * 抓取并解析单条数据
	 * map[person; company; destFilePath]
	 *
	 * @param isForce              false：存在就不抓取， true：不管存在于否都抓取
	 * @param financeMonitorPunish
	 * @return true:处理成功数据  false：未处理数据
	 */
	@Override
	protected boolean doFetch(FinanceMonitorPunish financeMonitorPunish, Boolean isForce) throws Exception {
		String targetUrl = financeMonitorPunish.getUrl();
		log.info("targetUrl:" + targetUrl);
		String content = "";
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
		financeMonitorPunish.setDetails(filterErrInfo(content));
		extract(content, financeMonitorPunish);
		processSpecial(financeMonitorPunish);
		return saveOne(financeMonitorPunish, isForce);
	}

	/**
	 * 提取所需要的信息 PDF
	 * 当事人、公司、住所地、法定代表人、一码通代码（当事人为个人）、当事人补充情况、违规情况、相关法规、处罚结果、处罚结果补充情况
	 */
	private void extract(String fullTxt, FinanceMonitorPunish financeMonitorPunish) {

		//debug
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201707/20170711084536_dxd12jwngk.pdf")) {
			log.debug("*******");
		}

		fullTxt = fullTxt.replace(",", "，")
				.replace(":", "：")
				.replace("o", "。");
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
		//处罚日期
		String punishDate = "";

		Boolean isCompany = (financeMonitorPunish.getPunishTitle().contains("公司")
				|| financeMonitorPunish.getPunishTitle().contains("事务所")
				|| financeMonitorPunish.getPunishTitle().contains("（有限合伙）")
				|| financeMonitorPunish.getPunishTitle().contains("Ltd.")
				|| financeMonitorPunish.getPunishTitle().contains("Limited"));

		//住所为空的当事人按照个人当事人来处理
		String[] zsd = {"住所地：", "住\n所地：", "住所\n地：", "住所地\n：", "住\n所地", "住所\n地",
				"住所：", "住所",
				"注\n册地：", "注册\n地：", "注册地\n：", "注册地：", "注册地址：", "注 册 地 址 ："};
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
					} else if (sTmp.indexOf("：") > -1 && sTmp.indexOf("。") > -1) {
						address = sTmp.substring(sTmp.indexOf("：") + 1, sTmp.indexOf("。"));
					} else if (sTmp.indexOf("：") > -1) {
						address = sTmp.substring(sTmp.indexOf("：") + 1);
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
				if (sTmp.contains("）")) {
					commonCode = sTmp.substring(0, sTmp.indexOf("）"));
				} else if (sTmp.contains(")")) {
					commonCode = sTmp.substring(0, sTmp.indexOf(")"));
				}

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
					|| punishTitle.contains("公开谴责，")
					|| punishTitle.contains("投资者"))) {
				String tmp = fullTxt.substring(sIndx);

				insperson = tmp.substring(0, tmp.indexOf("，"))
						.replace("当事人：", "")
						.replace("当事人", "");
				if (insperson.contains("（")) {
					companyFullName = insperson.substring(0, insperson.indexOf("（"));
				} else {
					companyFullName = insperson;
				}
				if (StringUtils.isNotEmpty(companyFullName)) {
					companyFullName = companyFullName.replace("\n", "")
							.replace(":", "");
					if (companyFullName.contains("一码通代码"))
						companyFullName = companyFullName.substring(0, companyFullName.indexOf("一码通代码") - 1);
					if (companyFullName.contains("〈以下筒称"))
						companyFullName = companyFullName.substring(0, companyFullName.indexOf("〈以下筒称"));
				}

				//当事人为公司下个人
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

					if (punishTitle.contains("投资者")) {
						holderAddition = person.replace("当事人：", "");
						person = holderAddition.substring(0, person.indexOf("投资者"));
					} else if (punishTitle.contains("实际控制人")) {
						holderAddition = person.replace("当事人：", "");
						person = holderAddition.substring(0, person.indexOf("实际控制人"));
					} else if (StringUtils.isNotEmpty(person)) {
						if (person.startsWith("。"))
							person = person.substring(1);

						String pTmps[] = person.split("。");
						person = "";
						for (String pTmp : pTmps) {
							String str = pTmp.replace("法定代表人：", "");
							if (str.contains("，男")) {
								person += "," + str.substring(0, str.indexOf("，男"));
							} else if (str.contains("，女")) {
								person += "," + str.substring(0, str.indexOf("，女"));
							}
							holderAddition += str + "。";
						}

						if (StrUtil.isNotEmpty(person)) {
							person = person.substring(1).replace("\n", "").trim();
							if (person.contains("，女")) person = person.substring(0, person.indexOf("，女"));
							if (person.contains("，男")) person = person.substring(0, person.indexOf("，男"));
						}

					}
					//当事人是个人时也需要公司名
					String 公司 = null;
					if (punishTitle.contains("（特殊普通合伙）")) {
						公司 = "（特殊普通合伙）";
					} else if (punishTitle.contains("（有限合伙）")) {
						公司 = "（有限合伙）";
					} else if (punishTitle.contains("事务所")) {
						公司 = "事务所";
					} else if (punishTitle.contains("有限公司、")) {
						公司 = "有限公司、";
					} else if (punishTitle.contains("公司")) {
						公司 = "公司";
					}

					String 关于对 = null;
					if (punishTitle.contains("关于给予")) {
						关于对 = "关于给予";
					} else if (punishTitle.contains("关于对")) {
						关于对 = "关于对";
					}
					if (StrUtil.isNotEmpty(公司) && StrUtil.isNotEmpty(关于对)) {
						companyFullName = punishTitle.substring(关于对.length(), punishTitle.indexOf(公司)) + 公司;
					}

				}
			}

			sIndx = fullTxt.indexOf("违反");
			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("违反") > -1) {
				rule = fullTxt.substring(fullTxt.indexOf("违反"), fullTxt.indexOf("鉴于"));
			}

			if (fullTxt.indexOf("鉴于") > -1 && fullTxt.indexOf("全国股转公司") > -1) {
				result = fullTxt.substring(fullTxt.indexOf("鉴于"), fullTxt.lastIndexOf("全国股转公司"));
			}

			if (fullTxt.lastIndexOf("全国股转公司") > -1) {
				String tmp = fullTxt.substring(fullTxt.lastIndexOf("全国股转公司") + 7);
				if (tmp.contains("日")) {
					punishDate = tmp.substring(0, tmp.indexOf("日") + 1);
				}
			}

			if (resultSupplementIndex > -1 && fullTxt.lastIndexOf("全国股转公司") > -1) {
				resultAddition = fullTxt.substring(resultSupplementIndex, fullTxt.lastIndexOf("全国股转公司")).trim();
				if (resultAddition.contains("全国股转公司")) {
					String tmp = resultAddition.substring(resultAddition.lastIndexOf("全国股转公司"));
					punishDate = tmp.substring(7, tmp.indexOf("日") + 1).trim();
					resultAddition = resultAddition.substring(0, resultAddition.lastIndexOf("全国股转公司"));
				}

				resultAddition = filterErrInfo(resultAddition);
			}


			financeMonitorPunish.setDomicile(address);
			financeMonitorPunish.setPartyPerson(person);
			if (StrUtil.isNotEmpty(companyFullName)) {
				companyFullName = companyFullName.replace("“", "").replace("”", "");
			}
			financeMonitorPunish.setPartyInstitution(companyFullName);
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

			if (fullTxt.lastIndexOf("全国股转公司") > -1) {
				String tmp = fullTxt.substring(fullTxt.lastIndexOf("全国股转公司") + 7);
				if (tmp.contains("日")) {
					punishDate = tmp.substring(0, tmp.indexOf("日") + 1);
				}
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

		financeMonitorPunish.setPunishInstitution("全国股转公司");
		financeMonitorPunish.setPunishNo(punishNo);
		financeMonitorPunish.setLegalRepresentative(holder);
		financeMonitorPunish.setUnicode(commonCode.trim());
		if (StrUtil.isNotEmpty(financeMonitorPunish.getPartyPerson())) {
			financeMonitorPunish.setPartySupplement(holderAddition.trim());
		}

		financeMonitorPunish.setIrregularities(violation.trim());
		financeMonitorPunish.setRelatedLaw(rule.trim());
		financeMonitorPunish.setPunishResult(result.trim());
		financeMonitorPunish.setPunishResultSupplement(resultAddition);
		financeMonitorPunish.setPunishDate(punishDate.contains("20")
				? punishDate.substring(punishDate.indexOf("20")) : null);

		return;
	}

	/**
	 * 特殊格式处理
	 *
	 * @param financeMonitorPunish
	 */
	private void processSpecial(FinanceMonitorPunish financeMonitorPunish) {
		String person = financeMonitorPunish.getPartyPerson();
		String address = financeMonitorPunish.getDomicile();
		String companyFullName = financeMonitorPunish.getPartyInstitution();
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201803/20180312092410_phjxs2yvt1.pdf")) {
			person = "宋昊，赵艳清，徐娇";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201712/20171227171013_6cm4brmxaw.pdf")) {
			person = "张家龙，张陈松娜";
			companyFullName = "深圳海龙精密股份有限公司";
			financeMonitorPunish.setPartySupplement("张陈松娜，女，1962 年出生，实际控制人兼董事长	。" +
					"海龙精密实际控制人兼董事张家龙，男，1958 年出生。。");
			financeMonitorPunish.setLegalRepresentative("张晓健");
			financeMonitorPunish.setPunishNo("股转系统发[2017] 1710号");
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201712/20171212163151_vot7a4fj56.pdf")) {
			person = "曾建宁，黎振宇";
			companyFullName = "广州资源环保科技股份有限公司";
			financeMonitorPunish.setPartySupplement("曾建宁，男，1966 年出生，实际控制人兼董事长。" +
					"黎振宇，男，1976 年出生，财务负责人兼董事会秘书。");
			financeMonitorPunish.setLegalRepresentative("张晓健");
			financeMonitorPunish.setPunishNo("股转系统发[2017] 1626号");
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201711/20171101171207_40v0iosjag.pdf")) {
			person = "刘通生，赵艳芳";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201710/20171012161505_pr00j8j7ib.pdf")) {
			person = "李国勇，付桑英";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201710/20171010172331_42g7932k6k.pdf")) {
			person = "叶振华，杭飞燕";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201709/20170905201150_80qn8i7ado.pdf")) {
			person = "袁地保，王东海，张江华";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201708/20170817184908_gtu8bn8h22.pdf")) {
			person = "商永强，洪波";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201708/20170803182920_urwglokob3.pdf")) {
			person = "宋夫华，何钐";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201708/20170801175637_2uv0btkn4v.pdf")) {
			person = "周翔，余峥，周楠";
			companyFullName = "安徽瑞格电梯服务股份有限公司";
			financeMonitorPunish.setPartySupplement("周翔：男，1983 年生，现任瑞格股份董事长、总经理。" +
					"余峥：女，1968 年生，现任瑞格股份董事、财务总监。" +
					"周楠：女，1988 年生，现任瑞格股份董事、董事会秘书。");
			financeMonitorPunish.setLegalRepresentative("周翔");
			financeMonitorPunish.setDomicile("安徽蚌埠市蚌山区万达甲写 A1-2003");
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201707/20170721181416_h2i7hr8b0i.pdf")) {
			person = "王炳刚，宏梦伟业";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201707/20170714193202_7hxlckcpnb.pdf")) {
			person = "陈文，申金晚";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201707/20170713172143_dp6brp1rr8.pdf")) {
			person = "余晓曼";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201707/20170703185124_6mvf9niw33.pdf")) {
			person = "吴介忠";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201706/20170628194222_tuw7ndwm12.pdf")) {
			person = "董晶";
		}

		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201706/20170609172526_5n8wgsh07b.pdf")) {
			person = "李君波";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201705/20170509184314_3k0q614nu0.pdf")) {
			person = "吕尚简";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201705/20170509184129_x2t8xs1ko8.pdf")) {
			person = "王建军，王广军";
			companyFullName = "中矿微星软件股份有限公司";
			financeMonitorPunish.setPartySupplement("王广军，男，1973 年 5 月出生, 时任中矿微星董事长、法定代表人。" +
					"王建军，男，1964 年 3 月出生，为徐州中矿微星软件股份有限公司（以下简称“中矿微星”）股东。");
			financeMonitorPunish.setLegalRepresentative("王广军");
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201705/20170508172122_52tgqaxfrw.pdf")) {
			person = "刘代城";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201705/20170505145905_1j5ehfcqbf.pdf")) {
			person = "吴云天，李方萍";
		}


		financeMonitorPunish.setPartyPerson(person);

		if (StrUtil.isNotEmpty(person = financeMonitorPunish.getPartyPerson())) {
			person = person.replace(" ", "")
					.replace(" ", "")
					.replace("\n", "")
					.replace("　", "").trim();
			if (person.contains("投资者")) {
				person = person.substring(0, person.indexOf("投资者"));
			}
			if (person.contains("实际控制人")) {
				person = person.substring(0, person.indexOf("实际控制人"));
			}

			if (person.endsWith("公司") || person.endsWith("Ltd.")
					|| person.endsWith("Limited") || person.endsWith("（有限合伙）")) {
				companyFullName = person;
				person = null;
			}
		}

//		if (StrUtil.isNotEmpty(financeMonitorPunish.getPartySupplement())
//				&& !financeMonitorPunish.getPartySupplement().contains("出生")) {
//			financeMonitorPunish.setPartySupplement(null);
//		}
		if (financeMonitorPunish.getUrl().contains("http://www.neeq.com.cn/uploads/1/file/public/201801/20180117164949_9jcafdixo4.pdf")) {
			financeMonitorPunish.setPartySupplement("侯爱忠，衡阳鸿铭科技股份有限公司（证券代码：831419，证券简称：鸿铭科技）董事、总经理，住所地：湖南省株洲市芦淞区 " +
					"雷霖，鸿铭科技董事，住所地：湖南省衡阳市衡东县。 " +
					"李可松，鸿铭科技董事、财务总监，住所地：湖南省衡阳市蒸湘区。 单邱平，鸿铭科技监事会主席，住所地：湖南省岳阳市平江县 雷菊枚，鸿铭科技监事，住所地：湖南省衡阳市衡东县。" +
					"刘小林， 时任鸿铭科技监事，住所地：湖南省衡阳市雁峰区。");
		}


		financeMonitorPunish.setDomicile(address);
		financeMonitorPunish.setPartyInstitution(companyFullName);
		financeMonitorPunish.setPartyPerson(person);
		financeMonitorPunish.setCompanyFullName(companyFullName);

	}
}
