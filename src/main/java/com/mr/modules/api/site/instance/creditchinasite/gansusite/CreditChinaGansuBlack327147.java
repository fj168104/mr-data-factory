package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mr.common.util.CrawlerUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（甘肃）-江西发布今年第1期“诚信红黑榜”----黑榜
 * 
 * http://www.gscredit.gov.cn/blackList/327147.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-black-327147")
@Scope("prototype")
public class CreditChinaGansuBlack327147 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/327147.jhtml";

	/**
	 * 抓取页面数据
	 */
	@Override
	protected String execute() throws Throwable {
		//discreditBlacklistMapper.deleteAllByUrl(url);// 删除该URL下的全部数据
		log.info("开始抓取url={}", url);
		extractContent(url);
		log.info("抓取url={}结束！", url);
		return null;
	}

	/**
	 * 抓取内容
	 */
	public void extractContent(String url) throws Throwable {
		String contentHtml = getData(url);
		Document doc = Jsoup.parse(contentHtml);

		log.debug("==============================");
		Elements contents = doc.getElementsByClass("artical_content_wrap").first().getElementsByTag("span");

		DiscreditBlacklist discreditBlacklist = null;
		String discreditType = "";// 失信黑名单类型
		boolean start = false;
		for (Element content : contents) {
			String text = CrawlerUtil.replaceHtmlNbsp(content.text()).replace("　", " ").trim();// 替换&nbsp;和全角空格
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (text.contains("2.环保黑名单企业")) {
				discreditType = "环保黑名单企业";
				start = true;
				continue;
			}
			if (text.contains("3.重点运输企业交通失信名单")) {
				discreditType = "重点运输企业交通失信名单";
				continue;
			}
			if (text.contains("4.重大劳动保障违法单位")) {
				discreditType = "重大劳动保障违法单位";
				continue;
			}
			if (start) {
				discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject("江西黑榜");
				discreditBlacklist.setDiscreditType(discreditType);
				discreditBlacklist.setEnterpriseName(text);
				discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
				saveDisneycreditBlackListOne(discreditBlacklist,false);
			}
		}
		log.debug("==============================");

	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist() {
		Date nowDate = new Date();
		DiscreditBlacklist blackList = new DiscreditBlacklist();
		blackList.setCreatedAt(nowDate);// 本条记录创建时间
		blackList.setUpdatedAt(nowDate);// 本条记录最后更新时间
		blackList.setSource(CreditChinaSite.GANSU.getSiteName());// 数据来源
		blackList.setSubject("");// 主题
		blackList.setUrl(url);// url
		blackList.setObjectType("01");// 主体类型: 01-企业 02-个人。默认为企业
		blackList.setEnterpriseName("");// 企业名称
		blackList.setEnterpriseCode1("");// 统一社会信用代码
		blackList.setEnterpriseCode2("");// 营业执照注册号
		blackList.setEnterpriseCode3("");// 组织机构代码
		blackList.setEnterpriseCode4("");// 税务登记号
		blackList.setPersonName("");// 法定代表人/负责人姓名|负责人姓名
		blackList.setPersonId("");// 法定代表人身份证号|负责人身份证号
		blackList.setDiscreditType("");// 失信类型
		blackList.setDiscreditAction("");// 失信行为
		blackList.setPunishReason("");// 列入原因
		blackList.setPunishResult("");// 处罚结果
		blackList.setJudgeNo("");// 执行文号
		blackList.setJudgeDate("");// 执行时间
		blackList.setJudgeAuth("");// 判决机关
		blackList.setPublishDate("2018/04/02");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
