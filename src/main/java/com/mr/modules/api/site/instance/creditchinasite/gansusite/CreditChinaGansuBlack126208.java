package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import java.util.Date;
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
 * 信用中国（甘肃）-兰州发布2017第四季度诚信“红黑榜” 34家失信企业进入“黑榜”
 * 
 * http://www.gscredit.gov.cn/blackList/126208.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-black-126208")
@Scope("prototype")
public class CreditChinaGansuBlack126208 extends SiteTaskExtend_CreditChina {

	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/126208.jhtml";

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
		Elements contents = doc.select("div.artical_content_wrap > div > p");

		DiscreditBlacklist discreditBlacklist = null;
		int itemIndex = 0;// 条目索引
		String subject = "";// 主题
		int iCount = 0;// 计数器
		for (Element content : contents) {
			String text = CrawlerUtil.replaceHtmlNbsp(content.text()).replace("　", " ").trim();// 替换&nbsp;和全角空格
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (text.contains("兰州市法院发布16家失信被执行人")) {
				itemIndex = 1;
				iCount = 0;
				subject = "兰州市法院发布16家失信被执行人";
				continue;
			}
			if (text.contains("兰州市农委发布5家农产品质量安全失信企业")) {
				itemIndex = 2;
				iCount = 0;
				subject = "兰州市农委黑榜";
				continue;
			}
			if (text.contains("兰州市食药监局发布10家失信企业和个人")) {
				itemIndex = 3;
				iCount = 0;
				subject = "兰州市食药监局黑榜";
				continue;
			}
			if (text.contains("兰州市工信委发布3家失信企业")) {
				itemIndex = 4;
				iCount = 0;
				subject = "兰州市工信委黑榜";
				continue;
			}

			// 兰州市法院发布16家失信被执行人
			if (itemIndex == 1) {
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省兰州市法院");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1, text.indexOf(" ")).trim());
					discreditBlacklist.setPersonName(text.substring(text.indexOf(" ") + 1).trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 兰州市农委发布5家农产品质量安全失信企业
			if (itemIndex == 2) {
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省兰州市农委");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1).trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 兰州市食药监局发布10家失信企业和个人
			if (itemIndex == 3) {
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省兰州市食药监局");
					if (text.contains("失信企业")) {
						discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("：") + 1).trim());
					} else {
						discreditBlacklist.setObjectType("02");
						discreditBlacklist.setPersonName(text.substring(text.indexOf("：") + 1).trim());
					}
				} else if (text.contains("违规行为：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("违规行为：", ""));
				} else if (text.contains("处罚措施：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishResult(text.replace("处罚措施：", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 兰州市工信委发布3家失信企业
			if (itemIndex == 4) {
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省兰州市工信委");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1).trim());
					discreditBlacklist.setPersonName(text.substring(text.indexOf("公司") + 2).trim());
                    discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
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
		blackList.setPublishDate("2018/01/11");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
