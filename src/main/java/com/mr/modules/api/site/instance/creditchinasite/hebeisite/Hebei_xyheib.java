package com.mr.modules.api.site.instance.creditchinasite.hebeisite;

import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @auther 1.信用中国（河北）
 * 1、信用黑榜
 * 2.http://www.credithebei.gov.cn:8082/was5/web/detail?record=%d&channelid=220802
 */
@Slf4j
@Component("hebei_xyheib")
@Scope("prototype")
public class Hebei_xyheib extends SiteTaskExtend_CreditChina {
	String url = "http://www.credithebei.gov.cn:8082/was5/web/detail?record=%d&channelid=220802";

	@Override
	protected String executeOne() throws Throwable {
		return super.executeOne();
	}

	@Override
	protected String execute() throws Throwable {
		try {
			extractContext(url);
		} catch (Exception e) {
			writeBizErrorLog(url, e.getMessage());
		}
		return null;
	}

	/**
	 * 获取网页内容
	 * 行政处罚决定书文号、案件名称、处罚类别、处罚事由、处罚依据、行政相对人名称、组织机构代码、工商登记码、税务登记号、
	 * 法定代表人居民身份证号、法定代表人姓名、处罚结果、处罚生效期、处罚机关、当前状态、地方编码、备注、信息提供部门、数据报送时间
	 */
	public void extractContext(String url) {
		int i = 0;
		while (true){
			i++;
			String aUrl = String.format(url,i);
			log.debug("hebei_xyheib record=" + i);
			Document detailDoc = Jsoup.parse(getData(aUrl));
			Element divElement = detailDoc.getElementsByClass("div_slider").first();
			Elements trElements = divElement.getElementsByTag("tr");
			DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
			discreditBlacklist.setUrl(aUrl);
			for (int j = 0; j < trElements.size(); j++) {
				Element trElement = trElements.get(j);
				//每排tr有2组td
				for(int k =0; k < 2; k++){
					String keyString = trElement.getElementsByTag("td").get(2 * k).text();
					String valueString = trElement.getElementsByTag("td").get(1 + 2 * k).text().trim();

					if (keyString.contains("企业名称")) {
						if(StrUtil.isEmpty(valueString)) return;
						discreditBlacklist.setEnterpriseName(valueString);
						continue;
					}


					if (keyString.contains("统一社会信用代码")) {
						discreditBlacklist.setEnterpriseCode1(valueString);
						continue;
					}

					if (keyString.contains("工商注册号码")) {
						discreditBlacklist.setEnterpriseCode2(valueString);
						continue;
					}

					if (keyString.contains("组织机构代码")) {
						discreditBlacklist.setEnterpriseCode3(valueString);
						continue;
					}

					if (keyString.contains("载入黑名单原因")) {
						discreditBlacklist.setPunishReason(valueString);
						continue;
					}

					if (keyString.contains("当前黑名单状态")) {
						discreditBlacklist.setStatus(valueString);
						continue;
					}

					if (keyString.contains("载入日期")) {
						discreditBlacklist.setPublishDate(valueString);
						continue;
					}

					if (keyString.contains("判定机关")) {
						discreditBlacklist.setJudgeAuth(valueString);
						continue;
					}
				}
			}
			saveDisneycreditBlackListOne(discreditBlacklist, false);
		}
	}

	protected boolean saveDisneycreditBlackListOne(DiscreditBlacklist discreditBlacklist, Boolean isForce) {
		try{
			discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
			return super.saveDisneycreditBlackListOne(discreditBlacklist, false);
		}catch (Exception e){
			writeBizErrorLog(url, e.getMessage());
		}
		return true;
	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist() {
		DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();

		discreditBlacklist.setCreatedAt(new Date());
		discreditBlacklist.setUpdatedAt(new Date());
		discreditBlacklist.setSource("信用中国（河北）");
		discreditBlacklist.setSubject("黑名单");
		discreditBlacklist.setUrl(url);
		discreditBlacklist.setObjectType("01");
		discreditBlacklist.setEnterpriseName("");
		discreditBlacklist.setEnterpriseCode1("");
		discreditBlacklist.setEnterpriseCode2("");
		discreditBlacklist.setEnterpriseCode3("");
		discreditBlacklist.setPersonName("");
		discreditBlacklist.setPersonId("");
		discreditBlacklist.setDiscreditType("");
		discreditBlacklist.setDiscreditAction("");
		discreditBlacklist.setJudgeNo("");
		discreditBlacklist.setJudgeDate("");
		discreditBlacklist.setJudgeAuth("");
		discreditBlacklist.setStatus("");
		return discreditBlacklist;
	}

}
