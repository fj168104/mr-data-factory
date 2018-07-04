package com.mr.modules.api.site.instance.creditchinasite.shanxisite;

import com.mr.framework.core.collection.CollectionUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
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
 * 1、法人行政处罚信息
 * 2.http://www.creditsx.gov.cn/xzcfList.jspx
 */
@Slf4j
@Component("shanxi_xzcf")
@Scope("prototype")
public class Shanxi_xzcf extends SiteTaskExtend_CreditChina {
	String url = "http://www.creditsx.gov.cn/xzcfList.jspx";

	@Autowired
	AdminPunishMapper adminPunishMapper;

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
		String dUrlPrefix = "http://www.creditsx.gov.cn";

		Document document = Jsoup.parse(getData(url));
		Element elementPageDiv = document.getElementsByClass("page").first();
		int pages = elementPageDiv.getElementsByTag("option").size();

		for (int page = 1; page <= pages; page++) {
			Map<String, String> map = new HashMap<>();
			map.put("pageNo", String.valueOf(page));
			map.put("area_id", "140000");
			Document listDoc = Jsoup.parse(postData(url, map));
			Element div = listDoc.getElementsByTag("body-view-bottom").first();
			Elements aElements = div.getElementsByTag("a");
			for(int i = 0; i < aElements.size(); i++){
				String infoUrl = dUrlPrefix + aElements.get(i).attr("href");
				Document infoDoc = Jsoup.parse(getData(infoUrl));
				Elements trElements = infoDoc.getElementsByTag("tr");
				AdminPunish adminPunish = createDefaultAdminPunish();
				for(Element trElement : trElements){
					String thString = trElement.getElementsByTag("th").first().text();
					String tdString = trElement.getElementsByTag("td").first().text().trim();
					if(thString.contains("行政处罚决定书文号")){
						adminPunish.setJudgeNo(tdString);
					}

					if(thString.contains("处罚类别")){
						adminPunish.setPunishType(tdString);
					}

					if(thString.contains("处罚事由")){
						adminPunish.setPunishReason(tdString);
					}

					if(thString.contains("处罚依据")){
						adminPunish.setPunishAccording(tdString);
					}

					if(thString.contains("行政相对人名称")){
						adminPunish.setEnterpriseName(tdString);
					}

					if(thString.contains("统一社会信用代码")){
						adminPunish.setEnterpriseCode1(tdString);
					}

					if(thString.contains("组织机构代码")){
						adminPunish.setEnterpriseCode3(tdString);
					}

					if(thString.contains("工商登记码")){
						adminPunish.setEnterpriseCode2(tdString);
					}

					if(thString.contains("法定代表人居民身份证号")){
						adminPunish.setPersonId(tdString);
					}

					if(thString.contains("法定代表人姓名")){
						adminPunish.setPersonName(tdString);
					}

					if(thString.contains("处罚结果")){
						adminPunish.setPunishResult(tdString);
					}

					if(thString.contains("处罚生效期")){
						adminPunish.setPublishDate(tdString);
					}

					if(thString.contains("处罚机关")){
						adminPunish.setJudgeAuth(tdString);
					}
					adminPunishMapper.insert(adminPunish);
				}
			}
		}
	}

	private AdminPunish createDefaultAdminPunish() {
		AdminPunish adminPunish = new AdminPunish();

		adminPunish.setCreatedAt(new Date());
		adminPunish.setUpdatedAt(new Date());
		adminPunish.setSource("信用山西");
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
