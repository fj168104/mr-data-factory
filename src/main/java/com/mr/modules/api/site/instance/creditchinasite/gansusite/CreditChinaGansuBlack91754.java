package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mr.common.util.CrawlerUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（甘肃）-白银市发布今年首期诚信“黑榜”企业名单
 * 
 * http://www.gscredit.gov.cn/blackList/91754.jhtml
 * 
 * @author pxu 2018年6月25日
 */
@Slf4j
@Component("creditchina-gansu-black-91754")
@Scope("prototype")
public class CreditChinaGansuBlack91754 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/91754.jhtml";

	/**
	 * 抓取页面数据
	 */
	@Override
	protected String execute() throws Throwable {
		discreditBlacklistMapper.deleteAllByUrl(url);// 删除该URL下的全部数据
		log.info("开始抓取url={}", url);
		extractContent(url);
		log.info("抓取url={}结束！", url);
		return null;
	}

	/**
	 * 抓取内容：新闻发布会时间、处罚机关、企业名称、主要违法行为、处罚惩罚措施
	 */
	public void extractContent(String url) throws Throwable {
		String contentHtml = getData(url);
		Document doc = Jsoup.parse(contentHtml);

		log.debug("==============================");
		Elements contents = doc.getElementsByClass("artical_content_wrap").first().getElementsByTag("div");

		DiscreditBlacklist discreditBlacklist = null;
		int itemIndex = 0;// 条目索引
		String subject = "";// 主题
		int iCount = 0;// 计数器
		for (Element content : contents) {
			String text = CrawlerUtil.replaceHtmlNbsp(content.ownText());
			if (text.contains("17家企业上工商诚信“黑榜”")) {
				itemIndex = 1;
				iCount = 0;
				subject = "工商诚信黑榜";
				continue;
			}
			if (text.contains("2家企业上地税诚信“黑榜”")) {
				itemIndex = 2;
				subject = "地税、国税诚信黑榜";
				log.debug("1、共解析数据{}条", iCount);// 输出上一条目的数据量
				iCount = 0;
				continue;
			}
			if (text.contains("2家企业上国税诚信“黑榜”")) {
				itemIndex = 3;
				subject = "地税、国税诚信黑榜";
				log.debug("2、共解析数据{}条", iCount);// 输出上一条目的数据量
				iCount = 0;
				continue;
			}
			if (text.contains("1家企业上农产品生产经营诚信“黑榜”")) {
				itemIndex = 4;
				subject = "农产品生产经营黑榜";
				log.debug("3、共解析数据{}条", iCount);// 输出上一条目的数据量
				iCount = 0;
				continue;
			}
			if (text.contains("2家企业上食品药品“黑榜”")) {
				itemIndex = 5;
				subject = "食品药品黑榜";
				log.debug("4、共解析数据{}条", iCount);// 输出上一条目的数据量
				iCount = 0;
				continue;
			}
			if (text.contains("10家企业上白银市中级人民法诚信“黑榜”")) {
				itemIndex = 6;
				subject = "市中级人民法院诚信黑榜";
				log.debug("5、共解析数据{}条", iCount);// 输出上一条目的数据量
				iCount = 0;
				continue;
			}

			// 17家企业上工商诚信“黑榜”
			if (itemIndex == 1) {
				if (StrUtil.isNotEmpty(text)) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省白银市工商管理局");
					discreditBlacklist.setEnterpriseName(text.trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
				}
				continue;
			}
			// 2家企业上地税诚信“黑榜”
			if (itemIndex == 2) {
				if (StrUtil.isNotEmpty(text)) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省白银市地方税务局");
					discreditBlacklist.setEnterpriseName(text.trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
				}
				continue;
			}
			// 2家企业上国税诚信“黑榜”
			if (itemIndex == 3) {
				if (StrUtil.isNotEmpty(text)) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省白银市国家税务局");
					discreditBlacklist.setEnterpriseName(text.trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
				}
				continue;
			}
			// 1家企业上农产品生产经营诚信“黑榜”
			if (itemIndex == 4) {
				if (StrUtil.isNotEmpty(text)) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省白银市农牧局");
					discreditBlacklist.setEnterpriseName(text.trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
				}
				continue;
			}
			// 2家企业上食品药品“黑榜”
			if (itemIndex == 5) {
				if (StrUtil.isNotEmpty(text)) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省白银市食品药品监督管理局");
					discreditBlacklist.setEnterpriseName(text.trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
				}
				continue;
			}
			// 10家企业上白银市中级人民法诚信“黑榜”
			if (itemIndex == 6) {
				if (StrUtil.isNotEmpty(text)) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省白银市中级人民法院");
					discreditBlacklist.setEnterpriseName(text.trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
				}
				continue;
			}
		}
		log.debug("6、共解析数据{}条", iCount);// 输出上一条目的数据量
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
		blackList.setObjectType("01");// 主体类型: 01-企业 02-个人
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
		blackList.setPublishDate("2016/07/27");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
