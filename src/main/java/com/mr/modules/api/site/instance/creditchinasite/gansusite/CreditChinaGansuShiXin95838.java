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
 * 信用中国（甘肃）-住房公积金贷款失信人员名单公示
 * 
 * http://www.gscredit.gov.cn/shiXin/95838.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-shixin-95838")
@Scope("prototype")
public class CreditChinaGansuShiXin95838 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/shiXin/95838.jhtml";

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

		//boolean bStart = false;
		Elements trs = doc.getElementsByTag("table").select("tr");
		System.out.println(trs.size());
		trFor: for (Element tr : trs) {
			DiscreditBlacklist blackList = createDefaultDiscreditBlacklist();
			blackList.setSubject("住房公积金贷款失信人员名单公示");
			blackList.setJudgeAuth("甘肃省住房资金管理中心");
			StringBuilder punishReason = new StringBuilder();
			punishReason.append("为维护甘肃省住房资金管理中心（以下简称“省中心”）广大缴存职工的利益，降低住房公积金贷款逾期率，防范资金风险，省中心决定，自2016年8月10日起，对住房公积金借款合同期内连续三个付款期或累计六个付款期未按时偿还本息的借款人在省中心官方网站进行公示。");
			punishReason.append("公示名单的最终解释权归省中心所有。如对公示内容存在疑问，请致电0931—7654223咨询。");
			blackList.setPunishReason(punishReason.toString());

			blackList.setDiscreditType("2017年10月逾期3期及以上失信人员名单");
			Elements tds = tr.getElementsByTag("td");
			int tdNo = 0;
			for (Element td : tds) {
				if (StrUtil.isNotEmpty(td.attr("colspan"))) {
					continue trFor;// 直接跳到下一行
				}
				String text = CrawlerUtil.replaceHtmlNbsp(td.text()).replace("　", " ").trim();// 替换&nbsp;和全角空格
				if ("序号".equals(text)) {
					continue trFor;// 直接跳到下一行
				}
				tdNo++;
				switch (tdNo) {
					case 1:// 序号
						break;
					case 2:// 借款编号
						break;
					case 3:// 借款人姓名
						blackList.setPersonName(text);
						break;
					case 4:// 证件号码
						blackList.setPersonId(text);
						break;
					case 5:// 借款人单位名称
						blackList.setEnterpriseName(text);
						//blackList.setEnterpriseName("");
						break;
					default:
						break;
				}
			}
			blackList.setUniqueKey(blackList.getUrl() + "@" + blackList.getEnterpriseName() + "@" + blackList.getPersonName() + "@" + blackList.getJudgeNo() + "@" + blackList.getJudgeAuth());
			saveDisneycreditBlackListOne(blackList,false);
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
		blackList.setObjectType("02");// 主体类型: 01-企业 02-个人。默认为企业
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
		blackList.setPublishDate("2017/11/10");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
