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
			Elements spanElements = divElement.getElementsByTag("span");
			AdminPunish adminPunish = createDefaultAdminPunish();
			adminPunish.setUrl(url);
			for (int j = 0; j < spanElements.size(); j++) {
				Element spanElement = spanElements.get(j);
				String[] kvString = spanElement.text().split("：");
				if(kvString.length < 2) continue;
				String key = kvString[0];
				String value = kvString[1];

				if (key.contains("企业名称")) {
					if(adminPunishMapper.selectByUrl(url, value, null, null, null).size() > 0) return;
					adminPunish.setEnterpriseName(value);
					continue;
				}

				if (key.contains("统一社会信用代码")) {
					adminPunish.setEnterpriseCode1(value);
					continue;
				}

			}
			try{
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
		adminPunish.setSource("信用河北");
		adminPunish.setSubject("");
		adminPunish.setObjectType("01");
		adminPunish.setEnterpriseCode1("");
		adminPunish.setEnterpriseCode2("");
		adminPunish.setEnterpriseCode3("");
		adminPunish.setPersonName("");
		adminPunish.setPersonId("");
		return adminPunish;
	}

}
