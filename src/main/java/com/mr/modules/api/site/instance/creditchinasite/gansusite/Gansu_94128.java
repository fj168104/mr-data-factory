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
 * 2.url:http://www.gscredit.gov.cn/blackList/94128.jhtml
 */
@Slf4j
@Component("gansu_94128")
@Scope("prototype")
public class Gansu_94128 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94128.jhtml";

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

		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text().replaceAll("\\s*", "");
				if (subject.contains("一、白银市中级人民法院公布")) {
					subject = "市中级人民法院公布黑榜";
					continue;
				}

				subject = "";
				continue;
			}

			//市中级人民法院公布黑榜
			if (subject.equals("市中级人民法院公布黑榜")) {
				String text = elelementSpans.first().text();
				//市中级人民法院公布黑榜 处理结束
				if (text.contains("白银市地税局公布")) {
					subject = "市地税局公布黑榜";
					continue;
				}
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text);
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}

			//市地税局公布黑榜
			if (subject.equals("市地税局公布黑榜")) {
				String text = elelementSpans.first().text();
				//市地税局公布黑榜 处理结束
				if (text.contains("三、白银市国税局公布")) {
					subject = "市国税局公布黑榜";
					continue;
				}
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text);
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}

			//市国税局公布黑榜
			if (subject.equals("市国税局公布黑榜")) {
				String text = elelementSpans.first().text();
				//市国税局公布黑榜 处理结束
				if (text.contains("四、白银市食药监局公布")) {
					subject = "市市药监局公布黑榜";
					continue;
				}
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text);
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}

			//市市药监局公布黑榜
			if (subject.equals("市市药监局公布黑榜")) {
				String text = elelementSpans.first().text();
				//市市药监局公布黑榜 处理结束
				if (text.contains("相关文章")) {
					subject = "";
					break;
				}
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
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
