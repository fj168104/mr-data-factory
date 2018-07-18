package com.mr.modules.api.site.instance.creditchinasite.shanxisite;

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
 * @auther 1.信用中国（山西）
 * 1、法人黑名单信息
 * 2.http://www.creditsx.gov.cn/legalblackList.jspx?redBlackType=redBlack
 */
@Slf4j
@Component("shanxi_legalblack")
@Scope("prototype")
public class Shanxi_legalblack extends SiteTaskExtend_CreditChina {
	String url = "http://www.creditsx.gov.cn/legalblackList.jspx?redBlackType=redBlack";

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
	 * 主体名称、统一社会信用代码、工商登记号、失信领域、列入原因、决定机关、移出时间、
	 * 移出原因、待办推送日期、信息报送人、信息提供部门、信息报送日期、最后修改日期、
	 * 原始数据
	 */
	public void extractContext(String url) {
		String dUrlPrefix = "http://www.creditsx.gov.cn";

		Document document = Jsoup.parse(getData(url));
		Element elementPageDiv = document.getElementsByClass("page").first();
		int pages = elementPageDiv.getElementsByTag("option").size();

		for (int page = 1; page <= pages; page++) {
			Map<String, String> map = new HashMap<>();
			map.put("pageNo", String.valueOf(page));
			map.put("area_id", "140000");
			map.put("redBlackType", "redBlack");
			Document listDoc = Jsoup.parse(postData(url, map, 3));
			Element div = listDoc.getElementsByClass("body-view-bottom").first();
			Elements aElements = div.getElementsByTag("a");
			for (int i = 0; i < aElements.size(); i++) {
				String infoUrl = dUrlPrefix + aElements.get(i).attr("href");
				Document infoDoc = Jsoup.parse(getData(infoUrl));
				Elements trElements = infoDoc.getElementsByTag("tr");
				AdminPunish adminPunish = createDefaultAdminPunish();
				for (Element trElement : trElements) {

					String thString = trElement.getElementsByTag("th").first().text();
					String tdString = trElement.getElementsByTag("td").first().text();

					if (thString.contains("主体名称")) {
						adminPunish.setEnterpriseName(tdString.trim());
						continue;
					}
					if (thString.contains("统一社会信用代码")) {
						adminPunish.setEnterpriseCode1(tdString.trim());
						continue;
					}
					if (thString.contains("列入原因")) {
						adminPunish.setPunishReason(tdString.trim());
						continue;
					}
					if (thString.contains("决定机关")) {
						adminPunish.setJudgeAuth(tdString.trim());
						continue;
					}
					if (thString.contains("最后修改日期")) {
						adminPunish.setPublishDate(tdString.trim());
						continue;
					}

					if (!adminPunish.getEnterpriseCode1().trim().equals("空") && thString.contains("原始数据")) {
						String pString = trElement.getElementsByTag("p").first().text().trim();
						String[] details = pString.split(";");
						for (String detail : details) {
							String[] infos = detail.split(":");
							if (infos.length < 2) {
								continue;
							}
							if (infos[0].contains("纳税人名称")) {
								adminPunish.setEnterpriseName(infos[1].trim());
								continue;
							}
							if (infos[0].contains("组织机构代码")) {
								adminPunish.setEnterpriseCode3(infos[1].trim());
								continue;
							}

							if (infos[0].contains("公示日期")) {
								adminPunish.setPublishDate(infos[1].trim().substring(0, 10));
								continue;
							}
							if (infos[0].contains("法定代表人或者负责人姓名")) {
								adminPunish.setPersonName(infos[1].trim());
								continue;
							}
							if (infos[0].contains("法定代表人或者负责人证件号码")) {
								adminPunish.setPersonName(infos[1].trim());
								continue;
							}
							if (infos[0].contains("案件性质")) {
								adminPunish.setPunishType(infos[1].trim());
								continue;
							}
							if (infos[0].contains("主要违法事实")) {
								adminPunish.setPunishReason(infos[1].trim());
								continue;
							}
							if (infos[0].contains("相关法律依据及税务处理处罚情况")) {
								adminPunish.setPunishResult(infos[1].trim());
								continue;
							}
						}
					}
				}
				try {
					saveAdminPunishOne(adminPunish, false);
				} catch (Exception e) {
					writeBizErrorLog(infoUrl, e.getMessage());
				}
			}
		}
	}

	private AdminPunish createDefaultAdminPunish() {
		AdminPunish adminPunish = new AdminPunish();

		adminPunish.setCreatedAt(new Date());
		adminPunish.setUpdatedAt(new Date());
		adminPunish.setSource("信用山西");
		adminPunish.setSubject("");
		adminPunish.setUrl(url);
		adminPunish.setObjectType("02");
		adminPunish.setEnterpriseCode1("");
		adminPunish.setEnterpriseCode2("");
		adminPunish.setEnterpriseCode3("");
		adminPunish.setPersonName("");
		adminPunish.setPersonId("");
		return adminPunish;
	}

}
