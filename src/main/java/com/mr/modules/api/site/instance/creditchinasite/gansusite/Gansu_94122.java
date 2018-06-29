package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import com.mr.framework.core.collection.CollectionUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
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

/**
 * @auther 1.信用中国（甘肃）
 * 2.url:http://www.gscredit.gov.cn/blackList/94122.jhtml
 */
@Slf4j
@Component("gansu_94122")
@Scope("prototype")
public class Gansu_94122 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94122.jhtml";

	@Autowired
	DiscreditBlacklistMapper discreditBlacklistMapper;

	@Override
	protected String executeOne() throws Throwable {
		return super.executeOne();
	}

	@Override
	protected String execute() throws Throwable {
		try {
			extractContext(url);
		}catch (Exception e){
			writeBizErrorLog(url, e.getMessage());
		}
		return null;
	}

	/**
	 * 获取网页内容
	 * 发布单位投诉电话、新闻发布日期、企业名称、企业所在地、上榜原因、惩戒措施、
	 */
	public void extractContext(String url) {
		DiscreditBlacklist dcbl = null;
		Document document = Jsoup.parse(getData(url));
		Elements elementDivs = document.getElementsByTag("div");
		String subject = "";

		String objectType = "";
		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text().replaceAll("\\s*", "");
				if (subject.contains("嘉峪关市中级人民法院失信被执行人名单")) {
					subject = "市中级人民法院失信被执行人";
					continue;
				}

				subject = "";
				continue;
			}

			//市中级人民法院失信被执行人
			if (subject.equals("市中级人民法院失信被执行人")) {
				String text = elelementSpans.first().text();
				//市中级人民法院失信被执行人 处理结束
				if (text.contains("嘉峪关市城区人民法院失信被执行人名单")) {
					subject = "市城区人民法院失信被执行人";
					continue;
				}

				if(text.contains("法人(")){
					objectType = "01";
					continue;
				}

				if(text.contains("自然人(")){
					objectType = "02";
					continue;
				}

				if(objectType.equals("01")){
					DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setObjectType("01");
					discreditBlacklist.setEnterpriseName(text);
					discreditBlacklistMapper.insert(discreditBlacklist);
					continue;
				}
				if(objectType.equals("02")){
					DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setObjectType("02");
					discreditBlacklist.setPersonName(text);
					discreditBlacklistMapper.insert(discreditBlacklist);
					continue;
				}

			}

			//市城区人民法院失信被执行人
			if (subject.equals("市城区人民法院失信被执行人")) {
				String text = elelementSpans.first().text();
				//市城区人民法院失信被执行人 处理结束
				if (text.contains("嘉峪关市国税局纳税信用")) {
					subject = "市国税局纳税信用黑名单";
					continue;
				}

				if(text.contains("自然人(")){
					objectType = "02";
					continue;
				}

				if(objectType.equals("02")){
					String[] pNames = text.split("、");
					for(String pName : pNames){
						DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
						discreditBlacklist.setSubject(subject);
						discreditBlacklist.setObjectType("02");
						discreditBlacklist.setPersonName(pName);
						discreditBlacklistMapper.insert(discreditBlacklist);
					}
					continue;
				}

			}

			//市国税局纳税信用黑名单
			if (subject.equals("市国税局纳税信用黑名单")) {
				String text = elelementSpans.first().text();
				//市国税局纳税信用黑名单 处理结束
				if (text.contains("嘉峪关市地税局纳税信用")) {
					subject = "市地税局纳税信用黑名单";
					continue;
				}
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setObjectType("01");
				discreditBlacklist.setEnterpriseName(text);
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}

			//市地税局纳税信用黑名单
			if (subject.equals("市地税局纳税信用黑名单")) {
				String text = elelementSpans.first().text();
				//市地税局纳税信用黑名单 处理结束
				if (text.contains("相关文章")) {
					subject = "";
					break;
				}
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setObjectType("01");
				discreditBlacklist.setEnterpriseName(text);
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}


		}

	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist() {
		DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();

		discreditBlacklist.setCreatedAt(new Date());
		discreditBlacklist.setUpdatedAt(new Date());
		discreditBlacklist.setSource("信用中国（甘肃）");
		discreditBlacklist.setUrl(url);
		discreditBlacklist.setObjectType("01");
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
		discreditBlacklist.setPublishDate("2015/10/26");
		return discreditBlacklist;
	}

}
