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
 * 2.url:http://www.gscredit.gov.cn/blackList/94099.jhtml
 */
@Slf4j
@Component("gansu_94099")
@Scope("prototype")
public class Gansu_94099 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94099.jhtml";

	@Autowired
	DiscreditBlacklistMapper discreditBlacklistMapper;

	@Override
	protected String executeOne() throws Throwable {
		return super.executeOne();
	}

	@Override
	protected String execute() throws Throwable {
		extractContext(url);
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

		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text();
				if (subject.contains("食品药品不安全企业、商户名单(市食药监局发布)")) {
					subject = "食品药品不安全企业名单";
					continue;
				}
				if (subject.contains("安全生产不落实企业名单(市安监局发布)")) {
					subject = "安全生产不落实企业名单";
					continue;
				}
				if (subject.contains("道路运输行业不诚信企业名单(市运管局发布)")) {
					subject = "道路运输行业不诚信企业名单";
					continue;
				}
				if (subject.contains("拒不履行法院判决企业及个人名单(市法院发布)")) {
					subject = "拒不履行法院判决企业名单";
					continue;
				}
				if (subject.contains("不诚信纳税企业、商户名单(市地税局发布)")) {
					subject = "不诚信纳税企业名单";
					continue;
				}

				subject = "";
				continue;
			}

			//食品药品不安全企业名单
			if (subject.equals("食品药品不安全企业名单")) {
				String text = elelementSpans.first().text();
				//食品药品不安全企业名单 处理结束
				if (text.contains("安全生产不落实企业名单(市安监局发布)")) {
					subject = "安全生产不落实企业名单";
					continue;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}

			//安全生产不落实企业名单
			if (subject.equals("安全生产不落实企业名单")) {
				String text = elelementSpans.first().text();
				//安全生产不落实企业名单 处理结束
				if (text.contains("道路运输行业不诚信企业名单(市运管局发布)")) {
					subject = "道路运输行业不诚信企业名单";
					continue;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}

			//道路运输行业不诚信企业名单
			if (subject.equals("道路运输行业不诚信企业名单")) {
				String text = elelementSpans.first().text();
				//道路运输行业不诚信企业名单 处理结束
				if (text.contains("拒不履行法院判决企业及个人名单(市法院发布)")) {
					subject = "拒不履行法院判决企业名单";
					continue;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}

			//拒不履行法院判决企业名单
			if (subject.equals("拒不履行法院判决企业名单")) {
				String text = elelementSpans.first().text();
				//拒不履行法院判决企业名单 处理结束
				if (text.contains("不诚信纳税企业、商户名单(市地税局发布)")) {
					subject = "不诚信纳税企业名单";
					continue;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				if(text.contains("身份证号")){
					discreditBlacklist.setPersonName(text.substring(text.indexOf("、")+1, text.indexOf("(身份证号")));
					discreditBlacklist.setPersonId(text.substring(text.indexOf("身份证号：")+ 5, text.indexOf(")")));
					discreditBlacklist.setObjectType("02");
				}else {
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				}
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}

			//不诚信纳税企业名单
			if (subject.equals("不诚信纳税企业名单")) {
				String text = elelementSpans.first().text();
				//不诚信纳税企业名单 处理结束
				if (text.contains("庆阳市文明办")) {
					subject = "";
					break;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("、") + 1));
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
		discreditBlacklist.setPublishDate("2015/10/27");
		return discreditBlacklist;
	}

}
