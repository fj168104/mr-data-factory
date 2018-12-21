package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * 信用中国（甘肃）-兰州：48家物业企业拟列入诚信“黑名单”
 * 
 * http://www.gscredit.gov.cn/blackList/137827.jhtml
 * 
 * @author pxu 2018年7月12日
 */
@Slf4j
@Component("creditchina-gansu-black-137827")
@Scope("prototype")
public class CreditChinaGansuBlack137827 extends SiteTaskExtend_CreditChina {

	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/137827.jhtml";

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
		Elements spans = doc.select("div.artical_content_wrap > div").select("span");

		DiscreditBlacklist discreditBlacklist = null;
		boolean bStart = false;
		for (Element span : spans) {
			String text = CrawlerUtil.replaceHtmlNbsp(span.text()).replace("　", " ").trim();// 替换全角空格
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (!bStart) {
				if (text.contains("拟纳入物业管理诚信档案“黑名单”的48家企业")) {
					bStart = true;
				}
				continue;
			}
			//替换其中的数字，获得企业名称
			Pattern p = Pattern.compile("[0-9]");
			Matcher matcher = p.matcher(text);
			String enterpriseName = matcher.replaceAll("");
			
			discreditBlacklist = createDefaultDiscreditBlacklist();
			discreditBlacklist.setSubject("物业企业黑名单");
			discreditBlacklist.setJudgeAuth("兰州市住房保障和房产管理局");
			discreditBlacklist.setEnterpriseName(enterpriseName);
			String punishReason = "在2017年11月6日至2018年1月5日期间，兰州市物业企业信用等级和星级测评领导小组组织15个测评小组，对城关区、高新区425家物业企业、767个物业项目进行了全面测评。在测评中下发整改通知书767份，查找问题4608项，已整改落实3660项，正在整改落实的948项。测评中发现物业企业普遍存在从业人员上岗证不齐全、业主投诉机制不健全、基础设施较差、消防设施不完备、各种预案准备不充分等突出问题。"//
					+ "在信用等级初评中，425家物业企业中被评定为A级企业的有25家，AA级企业290家，AAA企业42家，AAAA企业20家。拟列入物业管理诚信档案“黑名单”物业企业48家。";
			discreditBlacklist.setPunishReason(punishReason);
			discreditBlacklist.setPunishResult("该企业拟列入物业管理诚信档案“黑名单”");
			discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
			saveDisneycreditBlackListOne(discreditBlacklist,false);
			discreditBlacklist = null;
			continue;
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
		blackList.setPublishDate("2018/01/17");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
