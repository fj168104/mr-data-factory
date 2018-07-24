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
 * 信用中国（甘肃）-2015年兰州市诚信“红黑榜”发布 41家企业被列入“黑榜”名单
 * 
 * http://www.gscredit.gov.cn/blackList/94089.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-black-94089")
@Scope("prototype")
public class CreditChinaGansuBlack94089 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/94089.jhtml";

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
		String judgeAuth = "";// 发布机关
		int iCount = 0;// 计数器
		for (Element content : contents) {
			String text = CrawlerUtil.replaceHtmlNbsp(content.text()).replace("　", " ").trim();// 替换全角空格
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (itemIndex == 0) {
				if (text.contains("一.市食药监局发布32家食药经营失信企业")) {
					itemIndex = 1;
					iCount = 0;
					subject = "市食药监局经营失信";
					judgeAuth = "甘肃省兰州市食药监局";
					continue;
				}
				if (text.contains("二.市质监局发布2家质量违法企业")) {
					itemIndex = 2;
					iCount = 0;
					subject = "市质监局质量违法";
					judgeAuth = "甘肃省兰州市质监局";
					continue;
				}
				if (text.contains("三.市国税局发布3家失信企业")) {
					itemIndex = 3;
					iCount = 0;
					subject = "市国税局诚信黑榜";
					judgeAuth = "甘肃省兰州市国税局";
					continue;
				}
				if (text.contains("四.市建设局发布4家建设工程及参建单位")) {
					itemIndex = 4;
					iCount = 0;
					subject = "市建设局建设单位";
					judgeAuth = "甘肃省兰州市建设局";
					continue;
				}
				continue;
			}

			// 一.市食药监局发布32家食药经营失信企业
			if (itemIndex == 1) {
				String[] items = text.split(" ");
				if ("企业名称".equals(items[0])) {
					continue;
				}
				iCount++;
				discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setJudgeAuth(judgeAuth);
				discreditBlacklist.setEnterpriseName(items[0]);
				discreditBlacklist.setDiscreditType("食药经营失信企业");
				if ("甘肃爱昕医疗设备有限公司".equals(items[0]) && items.length == 2) {
					discreditBlacklist.setPunishReason("不符合医疗器械经营质量管理规范");
					discreditBlacklist.setPunishResult("行政处罚");
				} else if (items.length == 3) {
					if ("兰州红古区银萍压面铺".equals(items[0])) {// 处理结束,重新判断itemIndex
						log.debug("主题：{},数据量：{}", subject, iCount);
						itemIndex = 0;
					}
					discreditBlacklist.setPunishReason(items[1]);
					discreditBlacklist.setPunishResult(items[2]);
				}
				discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
				discreditBlacklistMapper.insert(discreditBlacklist);
				discreditBlacklist = null;
				continue;
			}
			// 二.市质监局发布2家质量违法企业
			if (itemIndex == 2) {
				String[] items = text.split(" ");
				if ("企业名称".equals(items[0])) {
					continue;
				}
				iCount++;
				discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setJudgeAuth(judgeAuth);
				discreditBlacklist.setEnterpriseName(items[0]);// 企业名称
				discreditBlacklist.setPunishReason(items[1]);
				discreditBlacklist.setPunishResult(items[2]);
				discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
				discreditBlacklistMapper.insert(discreditBlacklist);
				discreditBlacklist = null;
				if (text.contains("兰州陕西宝粤物资有限公司")) {// 处理结束,重新判断itemIndex
					log.debug("主题：{},数据量：{}", subject, iCount);
					itemIndex = 0;
				}
				continue;
			}
			// 三.市国税局发布3家失信企业
			if (itemIndex == 3) {
				if (text.contains("惩戒措施：")) {// 处理结束,重新判断itemIndex
					log.debug("主题：{},数据量：{}", subject, iCount);
					itemIndex = 0;
					continue;
				}
				if (text.contains("上榜原因：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("上榜原因：", ""));
					StringBuilder punishResult = new StringBuilder();
					punishResult.append("1.依法追缴纳税人不进行纳税申报，不缴或少缴的税款。");
					punishResult.append("2.依法对纳税人处以欠缴税款百分之五十以上五倍以下的罚款;构成犯罪的，依法追求刑事责任。");
					punishResult.append("3.从纳税人滞纳税款之日起，依法按日加收滞纳税款万分之五的滞纳金。");
					punishResult.append("4.继续加大“黑榜”纳税人信息曝光力度，对“黑榜”纳税人日常经营活动重点监控。");
					discreditBlacklist.setPunishResult(punishResult.toString());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
					discreditBlacklist = null;
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth(judgeAuth);
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1));
				}
				continue;
			}
			// 四.市建设局发布4家建设工程及参建单位
			if (itemIndex == 4) {

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
		blackList.setPublishDate("2015/12/19");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
