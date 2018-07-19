package com.mr.modules.api.site.instance.creditchinasite.hubeisite;

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
import java.util.HashMap;
import java.util.Map;

/**
 * @auther 1.信用中国（湖北）
 * 1、行政处罚信息
 * 2.http://www.hbcredit.gov.cn/credithb/gkgs/list.html
 */
@Slf4j
@Component("hubei_publicitypunishment")
@Scope("prototype")
public class Hubei_PublicityPunishment extends SiteTaskExtend_CreditChina {
	String url = "http://www.hbcredit.gov.cn/credithb/gkgs/list.html";

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
		String dUrlPrefix = "http://www.hbcredit.gov.cn";

		String gUrl = "http://www.hbcredit.gov.cn/credithb/gkgs/list.html?type=PublicityPunishment";
		String textList = getData(gUrl);
		if(textList.indexOf("var totalPages =") < 0){
			return;
		}
		String s1 = textList.substring(textList.indexOf("var totalPages ="))
				.replace("var totalPages =", "");
		int pages = Integer.parseInt(s1.substring(0,s1.indexOf(";")).trim());

		for (int page = 1; page <= pages; page++) {
			Map<String, String> map = new HashMap<>();
			map.put("pageIndex", String.valueOf(page));
			map.put("type", "PublicityPunishment");
			Document listDoc = Jsoup.parse(postData(url, map, 3));
			Element div = listDoc.getElementsByClass("right_xkgs").first();
			Elements aElements = div.getElementsByTag("a");
			for (int i = 0; i < aElements.size(); i++) {
				String infoUrl = dUrlPrefix + aElements.get(i).attr("href");
				Document infoDoc = Jsoup.parse(getData(infoUrl));
				Elements trElements = infoDoc.getElementsByTag("tr");
				AdminPunish adminPunish = createDefaultAdminPunish();
				for (int j = 1; j < trElements.size(); j++) {
					Element trElement = trElements.get(j);
					String keyString = trElement.getElementsByTag("td").get(0).text();
					String valueString = trElement.getElementsByTag("td").get(1).text().trim();
					if (keyString.contains("行政处罚决定书文号")) {
						adminPunish.setJudgeNo(valueString);
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

					if (keyString.contains("信用主体名称")) {
						adminPunish.setEnterpriseName(valueString);
						continue;
					}



					if (keyString.trim().equals("代码")) {
						adminPunish.setEnterpriseCode1(valueString);
						continue;
					}

					if (keyString.contains("法定代表人姓名")) {
						adminPunish.setPersonName(valueString);
						continue;
					}

					if (keyString.contains("处罚结果")) {
						if(StrUtil.isNotEmpty(valueString) && valueString.trim().startsWith("<")){
							Element document = Jsoup.parse("<html>" + valueString + "</html>");
							String s = document.text();
							adminPunish.setPunishResult(s);
						}else{
							adminPunish.setPunishResult(valueString);
						}
						continue;
					}

					if (keyString.contains("处罚决定日期")) {
						adminPunish.setPublishDate(valueString);
						continue;
					}

					if (keyString.contains("处罚机构")) {
						adminPunish.setJudgeAuth(valueString);
						continue;
					}
				}
				try{
					saveAdminPunishOne(adminPunish, false);
				}catch (Exception e){
					writeBizErrorLog(infoUrl, e.getMessage());
				}

			}
		}
	}

	private AdminPunish createDefaultAdminPunish() {
		AdminPunish adminPunish = new AdminPunish();

		adminPunish.setCreatedAt(new Date());
		adminPunish.setUpdatedAt(new Date());
		adminPunish.setSource("信用湖北");
		adminPunish.setSubject("");
		adminPunish.setUrl(url);
		adminPunish.setObjectType("01");
		adminPunish.setEnterpriseCode1("");
		adminPunish.setEnterpriseCode2("");
		adminPunish.setEnterpriseCode3("");
		adminPunish.setPersonName("");
		adminPunish.setPersonId("");
		return adminPunish;
	}

}
