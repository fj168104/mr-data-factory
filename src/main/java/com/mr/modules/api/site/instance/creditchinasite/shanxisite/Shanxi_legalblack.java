package com.mr.modules.api.site.instance.creditchinasite.shanxisite;

import com.google.common.collect.Maps;
import com.mr.framework.core.util.StrUtil;
import com.mr.framework.json.JSONArray;
import com.mr.framework.json.JSONObject;
import com.mr.framework.json.JSONUtil;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.colligationsite.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
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
	String PAGE_SIZE = "100";
	String url = "http://www.creditsx.gov.cn/legalblackListNew.jspx";
	String detailUrlTp = "http://www.creditsx.gov.cn/legalblackDetial-%s.jspx";

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
		Map<String, String> requestMap = Maps.newHashMap();
		requestMap.put("pageNo", "1");
		requestMap.put("pageSize", PAGE_SIZE);
		requestMap.put("value", "");

		String json = postData(url, requestMap);
		JSONArray jsonArray = JSONUtil.parseArray(json);

		for(int i = 1; i < jsonArray.size(); i++){
			JSONObject object = jsonArray.getJSONObject(i);
			String detailUrl = String.format(detailUrlTp, object.getStr("id"));
			log.info("detailUrl = " + detailUrl);
			Document document = Jsoup.parse(getData(detailUrl, 3));
			Element div = document.getElementsByClass("main_body").first();
			Elements trElements = div.getElementsByTag("tr");
			DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
			for (Element trElement : trElements) {


				String tdString = trElement.getElementsByTag("td").first().text();
				if(StrUtil.isEmpty(tdString)) continue;
				if(tdString.contains("主体名称：")){
					discreditBlacklist.setEnterpriseName(tdString.replace("主体名称：", "").trim());
					continue;
				}

				String thString = trElement.getElementsByTag("th").first().text();

				if (thString.contains("统一社会信用代码")) {
					discreditBlacklist.setEnterpriseCode1(tdString.trim());
					continue;
				}
				if (thString.contains("列入原因")) {
					discreditBlacklist.setPunishReason(tdString.trim());
					continue;
				}
				if (thString.contains("决定机关")) {
					discreditBlacklist.setJudgeAuth(tdString.trim());
					continue;
				}
				if (thString.contains("最后修改日期")) {
					discreditBlacklist.setPublishDate(tdString.trim());
					continue;
				}
			}
			try{
				discreditBlacklist.setUniqueKey(MD5Util.encode(discreditBlacklist.getUrl()+"@"+discreditBlacklist.getEnterpriseName()+"@"+discreditBlacklist.getPersonName()+"@"+discreditBlacklist.getJudgeNo()+"@"+discreditBlacklist.getJudgeAuth()));
				saveDisneycreditBlackListOne(discreditBlacklist, false);
			}catch (Exception e){
				writeBizErrorLog(detailUrl, e.getMessage());
			}
		}

	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist() {
		DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();

		discreditBlacklist.setCreatedAt(new Date());
		discreditBlacklist.setUpdatedAt(new Date());
		discreditBlacklist.setSource("信用中国（山西）");
		discreditBlacklist.setUrl(url);
		discreditBlacklist.setSubject("法人黑名单");
		discreditBlacklist.setObjectType("01");
		discreditBlacklist.setEnterpriseCode1("");
		discreditBlacklist.setEnterpriseCode2("");
		discreditBlacklist.setEnterpriseCode3("");
		discreditBlacklist.setEnterpriseName("");
		discreditBlacklist.setPersonName("");
		discreditBlacklist.setPersonId("");
		discreditBlacklist.setJudgeNo("");
		discreditBlacklist.setJudgeAuth("");
		return discreditBlacklist;
	}

}
