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
 * 信用中国（甘肃）-安全生产“黑名单”企业公告
 * 
 * http://www.gscredit.gov.cn/blackList/94132.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-black-94132")
@Scope("prototype")
public class CreditChinaGansuBlack94132 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/94132.jhtml";

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
		Elements contents = doc.select("div.artical_content_wrap > div");

		DiscreditBlacklist discreditBlacklist = null;
		boolean flag = false;// 判断是否记录数据
		for (Element content : contents) {
			String text = CrawlerUtil.replaceHtmlNbsp(content.text()).replace("　", " ").trim();// 替换全角空格
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (text.contains("以上列入")) {
				break;
			}
			if (text.contains("一、") || text.contains("二、") || text.contains("三、")) {
				flag = true;
				continue;
			}
			if (flag) {
				discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject("安全生产黑名单");
				discreditBlacklist.setJudgeAuth("甘肃省安全生产委员会办公室");
				discreditBlacklist.setJudgeDate("2013/10/14");
				discreditBlacklist.setEnterpriseName(text);// 企业名称
				discreditBlacklist.setPunishResult("列入“黑名单”的企业，从2013年10月14日起执行，相关市州、县（区）、部门要按照《甘肃省安全生产“黑名单”管理制度》第7条规定，采取相应的监管监察措施");
				discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
				saveDisneycreditBlackListOne(discreditBlacklist,false);
				flag = false;
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
		blackList.setPublishDate("2014/03/25");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
