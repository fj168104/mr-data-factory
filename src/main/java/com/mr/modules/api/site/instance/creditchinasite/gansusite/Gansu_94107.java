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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther 1.信用中国（甘肃）
 * 2.url:http://www.gscredit.gov.cn/blackList/94107.jhtml
 */
@Slf4j
@Component("gansu_94107")
@Scope("prototype")
public class Gansu_94107 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94107.jhtml";

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

		String pr = "";
		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text().replaceAll("\\s*", "");
				if (subject.contains("一、市食药监局发布32家食药经营失信企业")) {
					subject = "市食药监局经营失信企业";
					continue;
				}

				subject = elelementSpans.first().text().replaceAll("\\s*", "");
				if (subject.contains("四、市建设局发布4家建设工程及参建单位")) {
					subject = "市建设局建设单位";
					continue;
				}

				subject = "";
				continue;
			}

			//市食药监局经营失信企业
			if (subject.equals("市食药监局经营失信企业")) {
				String text = elelementSpans.first().text();
				//市中级人民法院失信被执行人 处理结束
				if (text.contains("二、市质监局发布2家质量违法企业")) {
					subject = "市质监局质量违法企业名单";
					continue;
				}
				if (text.trim().startsWith("企业名称")) {
					continue;
				}
				String[] blackInfos = text.split("   ");
				if (blackInfos.length < 4) {
					continue;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(blackInfos[0]);
				discreditBlacklist.setPunishReason(blackInfos[2]);
				discreditBlacklist.setPunishResult(blackInfos[3]);
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;

			}

			//市质监局质量违法企业名单
			if (subject.equals("市质监局质量违法企业名单")) {
				String text = elelementSpans.first().text();
				//市质监局质量违法企业名单 处理结束
				if (text.contains("三、市国税局发布3家失信企业")) {
					subject = "市国税局失信企业";
					continue;
				}
				if (text.trim().startsWith("企业名称")) {
					continue;
				}
				List<String> blackList = new ArrayList<String>();
				String[] blackInfos = text.split(" ");
				for(String blackInfo : blackInfos){
					if(StrUtil.isNotBlank(blackInfo)){
						blackList.add(blackInfo);
					}
				}
				if (blackList.size() < 7) {
					continue;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(blackList.get(0));
				discreditBlacklist.setPersonName(blackList.get(1));
				discreditBlacklist.setJudgeAuth(blackList.get(4));
				discreditBlacklist.setPublishDate(blackList.get(5));
				discreditBlacklist.setPunishReason(blackList.get(3));
				discreditBlacklist.setPunishResult(blackList.get(6));
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;

			}


			//市国税局失信企业
			if (subject.equals("市国税局失信企业")) {
				String text = elelementSpans.first().text();
				//市国税局失信企业 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}

				if (text.contains("上榜原因：")) {
					dcbl.setPunishReason(text.replace("上榜原因：", ""));
					discreditBlacklistMapper.insert(dcbl);
					continue;
				}

				String punishResult = "1.依法追缴纳税人不进行纳税申报，不缴或少缴的税款。" +
						"2.依法对纳税人处以欠缴税款百分之五十以上五倍以下的罚款;构成犯罪的，依法追求刑事责任。" +
						"3.从纳税人滞纳税款之日起，依法按日加收滞纳税款万分之五的滞纳金。" +
						"4.继续加大黑榜纳税人信息曝光力度，对黑榜纳税人日常经营活动重点监控，进一步完善奖励诚信、约束失信的制度体系。";
				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(text.indexOf(".") + 1));
				dcbl.setPunishResult(punishResult);
				continue;
			}

			//市建设局建设单位
			if (subject.equals("市建设局建设单位")) {
				String text = elelementSpans.first().text();
				//市建设局建设单位 处理结束
				if (text.contains("发布红黑榜单位投诉举报电话")) {
					subject = "";
					break;
				}

				if (text.contains("建设单位：")) continue;
				if (text.contains("施工单位：")) continue;
				if (text.contains("监理单位：")) continue;

				if (text.contains("上榜原因：")) {
					dcbl.setPunishReason(text.replace("上榜原因：", ""));
					continue;
				}
				if (text.contains("惩戒措施：")) {
					dcbl.setPunishReason(text.replace("惩戒措施：", ""));
					discreditBlacklistMapper.insert(dcbl);
					continue;
				}
				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(text.indexOf("：") + 1));
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
