package com.mr.modules.api.site.instance.creditchinasite.hebeisite;

import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
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

import java.sql.Struct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther 1.信用中国（河北）
 * 1、行政处罚信息
 * 2.http://www.credithebei.gov.cn:8082/was5/web/detail?record=%d&channelid=284249
 */
@Slf4j
@Component("hebei_xzcf")
@Scope("prototype")
public class Hebei_xzcf extends SiteTaskExtend_CreditChina {
	String url = "http://www.credithebei.gov.cn:8082/was5/web/detail?record=%d&channelid=284249";

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
			log.debug("hebei_xzcf record=" + i);
			Document detailDoc = Jsoup.parse(getData(aUrl));
			Element divElement = detailDoc.getElementsByClass("punishSearchDetailTop").first();
			Elements trElements = divElement.getElementsByTag("tr");
			AdminPunish adminPunish = createDefaultAdminPunish();
			adminPunish.setUrl(aUrl);
			for (int j = 0; j < trElements.size(); j++) {
				Element trElement = trElements.get(j);
				String keyString = trElement.getElementsByTag("td").get(0).text();
				String valueString = trElement.getElementsByTag("td").get(1).text().trim();
				if (keyString.contains("行政处罚决定文书号")) {
					adminPunish.setJudgeNo(valueString);
					if(StrUtil.isEmpty(valueString)) return;
					continue;
				}

				if (keyString.contains("统一社会信用代码")) {
					adminPunish.setEnterpriseCode1(valueString);
					if(StrUtil.isNotEmpty(valueString)){
						adminPunish.setObjectType("01");
					}
					continue;
				}


				if (keyString.contains("处罚类别")) {
					adminPunish.setPunishType(valueString);
					continue;
				}

				if (keyString.contains("处罚事由")) {
					if(StrUtil.isNotEmpty(valueString) && valueString.trim().startsWith("<")){
						Element document = Jsoup.parse("<html>" + valueString + "</html>");
						String s = document.text();
						adminPunish.setPunishReason(s);
					}else{
						adminPunish.setPunishReason(valueString);
					}
					continue;
				}

				if (keyString.contains("处罚依据")) {
					adminPunish.setPunishAccording(valueString);
					continue;
				}

				if (keyString.contains("行政相对人名称")) {
					if(StrUtil.isEmpty(valueString)) return;
					if(valueString.contains("公司")){
						adminPunish.setEnterpriseName(valueString);
					}else {
						adminPunish.setPersonName(valueString);
					}
					continue;
				}

				if (keyString.contains("工商登记码")) {
					adminPunish.setEnterpriseCode2(valueString);
					continue;
				}

				if (keyString.contains("居民身份证号")) {
					adminPunish.setPersonId(valueString);
					if(StrUtil.isNotEmpty(valueString)){
						adminPunish.setObjectType("02");
					}
					continue;
				}

				if (keyString.contains("法定代表人姓名")) {
					adminPunish.setPersonName(valueString);
					continue;
				}

				if (keyString.contains("处罚决定日期")) {
					adminPunish.setPublishDate(valueString);
					continue;
				}

				if (keyString.contains("处罚机关")) {
					adminPunish.setJudgeAuth(valueString);
					continue;
				}
			}
			try{
				adminPunish.setUniqueKey(adminPunish.getUrl() + "@" + adminPunish.getEnterpriseName() + "@" + adminPunish.getPersonName() + "@" + adminPunish.getJudgeNo() + "@" + adminPunish.getJudgeAuth());
				saveAdminPunishOne(adminPunish, false);
			}catch (Exception e){
				writeBizErrorLog(String.format(aUrl,i), e.getMessage());
			}

		}
	}

	private AdminPunish createDefaultAdminPunish() {
		AdminPunish adminPunish = new AdminPunish();

		adminPunish.setCreatedAt(new Date());
		adminPunish.setUpdatedAt(new Date());
		adminPunish.setSource("信用中国（河北）");
		adminPunish.setUrl(url);
		adminPunish.setSubject("行政处罚");
		adminPunish.setObjectType("01");
		adminPunish.setEnterpriseCode1("");
		adminPunish.setEnterpriseCode2("");
		adminPunish.setEnterpriseCode3("");
		adminPunish.setEnterpriseName("");
		adminPunish.setPersonName("");
		adminPunish.setPersonId("");
		adminPunish.setJudgeNo("");
		adminPunish.setJudgeAuth("");
		return adminPunish;
	}

}
