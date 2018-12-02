package com.mr.modules.api.site.instance.creditchinasite.shanxisite;

import com.mr.framework.json.JSON;
import com.mr.framework.json.JSONObject;
import com.mr.framework.json.JSONUtil;
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
 * 1、法人行政处罚信息
 * 2.http://www.creditsx.gov.cn/xzcfListNew.jspx
 */
@Slf4j
@Component("shanxi_xzcf")
@Scope("prototype")
public class Shanxi_xzcf extends SiteTaskExtend_CreditChina {
	String url = "http://www.creditsx.gov.cn/xzcfListNew.jspx";

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
		String detailUrlTp = "http://www.creditsx.gov.cn/xzcfDetialNew-%s.jspx";

		Document document = Jsoup.parse(getData(url, 3));
		Element ulElement = document.getElementById("search-result-list");
		Elements liElements = ulElement.getElementsByTag("li");

		for (int i = 0; i < liElements.size(); i++) {
			Element divEle = liElements.get(i).getElementsByClass("company-box clearfix").first();
			String sId = divEle.id();
			String detailUrl = String.format(detailUrlTp, sId);
			log.info("detailUrl=" + detailUrl);
			JSONObject json = JSONUtil.parseObj(getData(detailUrl, 3));

			AdminPunish adminPunish = createDefaultAdminPunish();
			//行政处罚决定书文号
			adminPunish.setJudgeNo(json.getStr("cfWsh"));
			//处罚类别
			adminPunish.setPunishType(json.getStr("cfCflb"));
			//处罚事由
			adminPunish.setPunishReason(json.getStr("cfSy"));
			//处罚依据
			adminPunish.setPunishAccording(json.getStr("cfYj"));
			//行政相对人名称
			adminPunish.setEnterpriseName(json.getStr("cfXdrMc"));
			//统一社会信用代码
			adminPunish.setEnterpriseCode1(json.getStr("cfXdrShxym"));
			//组织机构代码
			adminPunish.setEnterpriseCode3("");
			//工商登记码
			adminPunish.setEnterpriseCode2("");
			//法定代表人居民身份证号
			adminPunish.setPersonId("");
			//法定代表人姓名
			adminPunish.setPersonName("");
			//处罚结果
			adminPunish.setPunishResult(json.getStr("cfJg"));
			//处罚生效期
			adminPunish.setPublishDate(json.getStr("cfSxq"));
			//处罚机关
			adminPunish.setJudgeAuth(json.getStr("cfXzjg"));
			try {
				adminPunish.setUniqueKey(adminPunish.getUrl() + "@" + adminPunish.getEnterpriseName() + "@" + adminPunish.getPersonName() + "@" + adminPunish.getJudgeNo() + "@" + adminPunish.getJudgeAuth());
				saveAdminPunishOne(adminPunish, false);
			} catch (Exception e) {
				writeBizErrorLog(detailUrl, e.getMessage());
			}
		}


	}

	private AdminPunish createDefaultAdminPunish() {
		AdminPunish adminPunish = new AdminPunish();

		adminPunish.setCreatedAt(new Date());
		adminPunish.setUpdatedAt(new Date());
		adminPunish.setSource("信用中国（山西）");
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
