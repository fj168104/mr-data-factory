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
 * 2.url:http://www.gscredit.gov.cn/blackList/94130.jhtml
 */
@Slf4j
@Component("gansu_94130")
@Scope("prototype")
public class Gansu_94130 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94130.jhtml";

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

		int w = 0;
		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text().replaceAll("\\s*", "");
				if (subject.contains("一、白银区法院“黑榜”名单及惩戒措施")) {
					subject = "白银区法院黑榜";
					continue;
				}
				if (subject.contains("白银区工商局“黑榜”名单及惩戒措施")) {
					subject = "白银区工商局黑榜";
					continue;
				}
				if (subject.contains("白银区环保局“黑榜”企业名单及惩戒措施")) {
					subject = "白银区环保局黑榜";
					continue;
				}
				if (subject.contains("白银区质监局“黑榜”企业名单及惩戒措施")) {
					subject = "白银区质监局黑榜";
					continue;
				}

				if (subject.contains("白银区食药局“黑榜”企业名单及惩戒措施")) {
					subject = "白银区食药局黑榜";
					continue;
				}
				subject = "";
				continue;
			}

			//白银区法院黑榜
			if (subject.equals("白银区法院黑榜")) {
				String text = elelementSpans.first().text();
				//白银区法院黑榜 处理结束
				if (text.contains("白银区工商局")) {
					subject = "白银区工商局黑榜";
					continue;
				}

				if (text.contains("原因：")) {
					dcbl.setPunishReason(text.replace("原因：", "").replaceAll("\\s*", ""));
					continue;
				}

				if (text.contains("惩戒措施：")) {
					dcbl.setPunishResult(text.replace("惩戒措施：", "").replaceAll("\\s*", ""));
					discreditBlacklistMapper.insert(dcbl);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setObjectType("02");
				dcbl.setPersonName(text.substring(text.indexOf("姓名：") + 3, text.indexOf("身份证号：")).replaceAll("\\s*", ""));
				dcbl.setPersonId(text.substring(text.indexOf("身份证号：") + 5));
				continue;
			}

			//白银区工商局黑榜
			if (subject.equals("白银区工商局黑榜")) {
				String text = elelementSpans.first().text();
				//白银区工商局黑榜 处理结束
				if (text.contains("白银区环保局")) {
					subject = "白银区环保局黑榜";
					continue;
				}

				if (text.contains("原因：")) {
					dcbl.setPunishReason(text.replace("原因：", "").replaceAll("\\s*", ""));
					if (w < 2) {
						w++;
						discreditBlacklistMapper.insert(dcbl);
					}
					continue;
				}

				if (text.contains("惩戒措施：")) {
					dcbl.setPunishResult(text.replace("惩戒措施：", "").replaceAll("\\s*", ""));
					discreditBlacklistMapper.insert(dcbl);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				continue;
			}

			//白银区环保局黑榜
			if (subject.equals("白银区环保局黑榜")) {
				String text = elelementSpans.first().text();
				//白银区环保局黑榜 处理结束
				if (text.contains("白银区质监局")) {
					subject = "白银区质监局黑榜";
					continue;
				}

				if (text.contains("原因：")) {
					dcbl.setPunishReason(text.replace("原因：", "").replaceAll("\\s*", ""));
					continue;
				}

				if (text.contains("惩戒措施：")) {
					dcbl.setPunishResult(text.replace("惩戒措施：", "").replaceAll("\\s*", ""));
					discreditBlacklistMapper.insert(dcbl);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				continue;
			}

			//白银区质监局黑榜
			if (subject.equals("白银区质监局黑榜")) {

				String text = elelementSpans.first().text();
				//白银区质监局黑榜 处理结束
				if (text.contains("原因：")) {
					subject = "";
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				String reason = "2015年5 月7日，白银市白银区质量技术监督局特种设备安全监察人员对该白银区刘家梁冷库制冷使用的特种设备及其主要部件进行检查，发现存在如下几个方面的问题：" +
						"1、液氨压缩机及液氨储罐未办理使用登记手续；" +
						"2、现场无法提供安全技术资料档案；" +
						"3、设备未经定期检验；" +
						"4、安全附件、安全保护装置未经定期检验；" +
						"5、未配备专职管理人员。" +
						"2015年5月11日，白银市白银区质量技术监督局特种设备安全监察人员对该单位制冷使用的特种设备及其主要部件进行复查时，发现该单位对以上存在的问题均未整改。";

				dcbl.setPunishReason(reason);
				dcbl.setEnterpriseName(text);
				String punishResult = "责令该单位使用的相关特种设备自2015年5月11日起立即停止使用。";
				dcbl.setPunishResult(punishResult);
				discreditBlacklistMapper.insert(dcbl);
				continue;
			}

			//白银区食药局黑榜
			if (subject.equals("白银区食药局黑榜")) {
				String text = elelementSpans.first().text();
				//白银区食药局黑榜 处理结束
				if (text.contains("白银区文明办")) {
					subject = "";
					break;
				}

				if (text.contains("原因：")) {
					dcbl.setPunishReason(text.replace("原因：", "").replaceAll("\\s*", ""));
					continue;
				}

				if (text.contains("惩戒措施：")) {
					dcbl.setPunishResult(text.replace("惩戒措施：", "").replaceAll("\\s*", ""));
					discreditBlacklistMapper.insert(dcbl);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(text.indexOf("、") + 1));
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
