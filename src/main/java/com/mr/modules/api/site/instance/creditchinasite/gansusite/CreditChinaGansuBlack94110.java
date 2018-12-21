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
 * 信用中国（甘肃）-兰州市首批诚信“黑榜”名单
 * 
 * http://www.gscredit.gov.cn/blackList/94110.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-black-94110")
@Scope("prototype")
public class CreditChinaGansuBlack94110 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/94110.jhtml";

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
		int itemIndex = 0;// 条目索引
		String subject = "";// 主题
		int iCount = 0;// 计数器
		for (Element content : contents) {
			String text = CrawlerUtil.replaceHtmlNbsp(content.text()).replace("　", " ").trim();// 替换全角空格
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (itemIndex == 0) {
				if (text.contains("一、市食药监局发布11户食药经营失信企业")) {
					itemIndex = 1;
					iCount = 0;
					subject = "市食药监局经营失信";
					continue;
				}
				if (text.contains("二、市质监局发布2户质量违法企业")) {
					itemIndex = 2;
					iCount = 0;
					subject = "市质监局违法企业";
					continue;
				}
				continue;
			}

			// 一、市食药监局发布11户食药经营失信企业
			if (itemIndex == 1) {
				if (text.contains("惩戒措施：")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				int index = text.indexOf("药品经营");
				if (index == -1) {
					index = text.indexOf("餐饮服务");
				}
				if (index == -1) {
					continue;
				}
				iCount++;
				discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setJudgeAuth("甘肃省兰州市食药监局");
				discreditBlacklist.setEnterpriseName(text.substring(0, index).trim());// 企业名称
				if (text.contains("药品经营")) {
					discreditBlacklist.setPunishReason("非法渠道购进药品");
					discreditBlacklist.setPunishResult("行政处罚");
				} else {
					discreditBlacklist.setPunishReason("提炼使用废弃油脂");
					discreditBlacklist.setPunishResult("公安立案");
				}
				discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
				saveDisneycreditBlackListOne(discreditBlacklist,false);
			}
			// 二、市质监局发布2户质量违法企业
			if (itemIndex == 2) {
				if (text.contains("惩戒措施：")) {// 处理结束
					itemIndex = 0;
					break;
				}
				iCount++;
				discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setJudgeAuth("甘肃省兰州市质监局");
				discreditBlacklist.setEnterpriseName(text);// 企业名称
				String punishResult = "1.责令停止生产，并对违法行为进行整改。"//
						+ "2.采取罚款、停业整顿、没收违法所得等行政处罚。"//
						+ "3.根据违法事实和情节，触犯刑法的一律移送司法机关。"//
						+ "4.不受理纳入黑榜名单的企业及其经营管理人员任何评先选优等表彰申请。";
				discreditBlacklist.setPunishResult(punishResult);
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
		blackList.setPublishDate("2015/10/26");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
