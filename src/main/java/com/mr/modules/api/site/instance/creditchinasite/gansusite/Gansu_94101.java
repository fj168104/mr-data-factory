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
 * 2.url:http://www.gscredit.gov.cn/blackList/94101.jhtml
 */
@Slf4j
@Component("gansu_94101")
@Scope("prototype")
public class Gansu_94101 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94101.jhtml";

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

		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text();
				if (subject.contains("企业名单(市住房和城乡建设局发布)")) {
					subject = "建筑业黑榜企业名单";
					continue;
				}
				if (subject.contains("企业名单(市食品药品监督管理局发布)")) {
					subject = "食品药品安全黑榜企业";
					continue;
				}
				if (subject.contains("企业名单(市环境保护局发布)")) {
					subject = "环境保护黑榜企业";
					continue;
				}
				if (subject.contains("名单(市金融管理局发布)")) {
					subject = "金融机构黑榜企业";
					continue;
				}
				if (subject.contains("企业名单(市工商行政管理局发布)")) {
					subject = "市场经营黑榜企业名单";
					continue;
				}

				subject = "";
				continue;
			}

			//建筑业黑榜企业名单
			if (subject.equals("建筑业黑榜企业名单")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//建筑业黑榜企业名单 处理结束
				if (text.contains("企业名单（市食品药品监督管理局发布）")) {
					subject = "食品药品安全黑榜企业";
					continue;
				}

				if (text.contains("失信事实：")) {
					dcbl.setPunishReason(text.replace("失信事实：", ""));
					continue;
				}

				if (text.contains("惩治措施：")) {
					dcbl.setPunishResult(text.replace("惩治措施：", ""));
					saveDisneycreditBlackListOne(dcbl, false);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				continue;
			}

			//食品药品安全黑榜企业
			if (subject.equals("食品药品安全黑榜企业")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//食品药品安全黑榜企业 处理结束
				if (text.contains("企业名单（市环境保护局发布）")) {
					subject = "环境保护黑榜企业";
					continue;
				}

				if (text.contains("失信事实：")) {
					dcbl.setPunishReason(text.replace("失信事实：", ""));
					continue;
				}

				if (text.contains("惩治措施：")) {
					dcbl.setPunishResult(text.replace("惩治措施：", ""));
					saveDisneycreditBlackListOne(dcbl, false);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				continue;
			}

			//环境保护黑榜企业
			if (subject.equals("环境保护黑榜企业")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//环境保护黑榜企业 处理结束
				if (text.contains("名单（市金融管理局发布）")) {
					subject = "金融机构黑榜企业";
					continue;
				}

				if (text.contains("失信事实：")) {
					dcbl.setPunishReason(text.replace("失信事实：", ""));
					continue;
				}

				if (text.contains("惩治措施：")) {
					dcbl.setPunishResult(text.replace("惩治措施：", ""));
					saveDisneycreditBlackListOne(dcbl, false);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				if (text.contains("、")) {
					dcbl.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				} else {
					dcbl.setEnterpriseName(text);
				}
				continue;
			}

			//金融机构黑榜企业
			if (subject.equals("金融机构黑榜企业")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//金融机构黑榜企业 处理结束
				if (text.contains("企业名单（市工商行政管理局发布）")) {
					subject = "市场经营黑榜企业名单";
					continue;
				}

				if (text.contains("失信事实：")) {
					dcbl.setPunishReason(text.replace("失信事实：", ""));
					continue;
				}

				if (text.contains("惩治措施：")) {
					dcbl.setPunishResult(text.replace("惩治措施：", ""));
					saveDisneycreditBlackListOne(dcbl, false);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				if (text.contains("、")) {
					dcbl.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				} else {
					dcbl.setEnterpriseName(text);
				}
				continue;
			}

			//市场经营黑榜企业名单
			if (subject.equals("市场经营黑榜企业名单")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//市场经营黑榜企业名单 处理结束
				if (text.contains("庆阳市文明办")) {
					subject = "";
					break;
				}

				if (text.contains("失信事实：")) {
					dcbl.setPunishReason(text.replace("失信事实：", ""));
					continue;
				}

				if (text.contains("惩治措施：")) {
					dcbl.setPunishResult(text.replace("惩治措施：", ""));
					saveDisneycreditBlackListOne(dcbl, false);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				if (text.contains("、")) {
					dcbl.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				} else {
					dcbl.setEnterpriseName(text);
				}
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
