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
 * 信用中国（甘肃）-民政部再次公布22家非法社会组织
 * 
 * http://www.gscredit.gov.cn/shiXin/340085.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-shixin-340085")
@Scope("prototype")
public class CreditChinaGansuShiXin340085 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/shiXin/340085.jhtml";

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
		log.debug("url={}", url);

		boolean bStart = false;
		Elements contents = doc.select("div.artical_content_wrap span");
		for (Element content : contents) {
			String text = CrawlerUtil.replaceHtmlNbsp(content.text()).replace("　", " ").trim();// 替换&nbsp;和全角空格
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (text.contains("涉嫌非法社会组织名单（第五批）")) {
				bStart = true;
				continue;
			}

			if (bStart && text.indexOf("01、") >= 0) {
				String[] items = text.split(" ");
				for (String item : items) {
					DiscreditBlacklist blackList = createDefaultDiscreditBlacklist();
					int endIndex = item.indexOf("（");
					if (endIndex == -1) {
						endIndex = item.length();
					}
					blackList.setSubject("民政部再次公布22家非法社会组织");
					blackList.setDiscreditType("涉嫌非法社会组织名单");
					blackList.setJudgeAuth("民政部社会组织管理局");
					blackList.setEnterpriseName(item.substring(item.indexOf("、") + 1, endIndex).trim());
					if (endIndex != item.length()) {
						blackList.setPunishReason(item.substring(endIndex + 1, item.indexOf("）"))); // 列入原因
					}
					blackList.setUniqueKey(blackList.getUrl() + "@" + blackList.getEnterpriseName() + "@" + blackList.getPersonName() + "@" + blackList.getJudgeNo() + "@" + blackList.getJudgeAuth());
					saveDisneycreditBlackListOne(blackList,false);
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
		blackList.setPublishDate("2018/03/30");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
