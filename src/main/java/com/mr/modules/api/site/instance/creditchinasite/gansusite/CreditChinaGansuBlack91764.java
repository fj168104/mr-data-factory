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
 * 信用中国（甘肃）-白银市举行诚信“红黑榜”第二期发布会 27家单位上“黑榜”
 * 
 * http://www.gscredit.gov.cn/blackList/91764.jhtml
 * 
 * 
 * @author pxu 2018年6月15日
 */
@Slf4j
@Component("creditchina-gansu-black-91764")
@Scope("prototype")
public class CreditChinaGansuBlack91764 extends SiteTaskExtend_CreditChina {
	@Autowired
	private DiscreditBlacklistMapper discreditBlacklistMapper;

	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/91764.jhtml";

	/**
	 * 抓取页面数据
	 */
	@Override
	protected String execute() throws Throwable {
		log.info("抓取“信用中国（甘肃）-白银市举行诚信“红黑榜”第二期发布会 27家单位上“黑榜”信息开始...");
		discreditBlacklistMapper.deleteAllByUrl(url);// 删除该URL下的全部数据
		log.info("url={}", url);
		extractContent(url);
		log.info("抓取“信用中国（甘肃）-白银市举行诚信“红黑榜”第二期发布会 27家单位上“黑榜”信息结束！");
		return null;
	}

	/**
	 * 抓取内容：新闻发布会时间、处罚机关、企业名称、主要违法行为、处罚惩罚措施
	 */
	public void extractContent(String url) throws Throwable {
		String contentHtml = getData(url);
		Document doc = Jsoup.parse(contentHtml.replace("<div>&nbsp; </div>", ""));

		log.debug("==============================");
		Elements contents = doc.getElementsByClass("artical_content_wrap").first().getElementsByTag("div");
		int[] sign = new int[5];
		ok: for (int i = 0; i < contents.size(); i++) {// 查找开始标记的位置并存入数组
			switch (contents.get(i).text()) {
				case "一、被白银市国税局列入诚信“黑榜”的企业及惩戒措施：":
					sign[0] = i;
					break;
				case "二、被白银市法院系统列入失信被执行人企业名单：":
					sign[1] = i;
					break;
				case "三、被白银市食品药品监督管理局列入食品药品“黑榜”企业名单：":
					sign[2] = i;
					break;
				case "四、白银市工商局企业诚信“黑榜”企业名单：":
					sign[3] = i;
					break;
				case "五、白银市度税局企业诚信“黑榜”企业名单：":
					sign[4] = i;
					break ok;
			}
		}
		log.info("=====一、被白银市国税局列入诚信“黑榜”的企业及惩戒措施：======");
		DiscreditBlacklist blackList = null;
		for (Element span : contents.subList(sign[0] + 1, sign[1])) {
			String text = CrawlerUtil.replaceHtmlNbsp(span.text());
			if (StrUtil.isEmpty(text)) {// 跳过空行
				continue;
			}
			if (Pattern.compile("\\d+、.*").matcher(text).matches()) {// 每笔记录的开头
				blackList = createDiscreditBlacklistObject();
				blackList.setSubject("市国税局诚信黑榜");// 主题
				blackList.setJudgeAuth("甘肃省白银市国税局");// 判决机关
				blackList.setEnterpriseName(text.substring(text.indexOf("、") + 1));// 企业名称
			} else {
				String[] items = text.replace("上榜事由：", "@!@上榜事由：").replace("惩戒措施：", "@!@惩戒措施：").split("@!@");
				for (String item : items) {
					if (item.contains("上榜事由：") && blackList != null) {
						blackList.setPunishReason(item.substring(5));// 处罚原因
					} else if (item.contains("惩戒措施：") && blackList != null) {
						blackList.setPunishResult(item.substring(5));// 处罚结果
						discreditBlacklistMapper.insert(blackList);// 保存记录
					}
				}
			}
		}
		log.info("=====二、被白银市法院系统列入失信被执行人企业名单：======");
		for (Element span : contents.subList(sign[1] + 1, sign[2])) {
			String text = CrawlerUtil.replaceHtmlNbsp(span.text());
			if (StrUtil.isEmpty(text)) {
				continue;
			}
			if (Pattern.compile("\\d+、.*").matcher(text).matches()) {
				blackList = createDiscreditBlacklistObject();
				blackList.setSubject("市法院失信被执行人");// 主题
				blackList.setJudgeAuth("甘肃省白银市法院");// 判决机关
				blackList.setEnterpriseName(text.substring(text.indexOf("、") + 1));// 企业名称
				blackList.setDiscreditAction("不履行被执行义务");
				blackList.setPunishResult("依法进行信用惩戒，重拳打击“老赖”，维护社会诚信体系");
				discreditBlacklistMapper.insert(blackList);
			}
		}
		log.info("=====三、被白银市食品药品监督管理局列入食品药品“黑榜”企业名单：======");
		for (Element span : contents.subList(sign[2] + 1, sign[3])) {
			String text = CrawlerUtil.replaceHtmlNbsp(span.text());
			if (StrUtil.isEmpty(text)) {
				continue;
			}
			if (Pattern.compile("\\d+、.*").matcher(text).matches()) {
				blackList = createDiscreditBlacklistObject();
				blackList.setSubject("市食药监局失信黑榜");// 主题
				blackList.setJudgeAuth("甘肃省白银市食品药品监督管理局");// 判决机关
				blackList.setEnterpriseName(text.substring(text.indexOf("、") + 1));// 企业名称
			} else {
				String[] items = text.replace("上榜事由：", "@!@上榜事由：").replace("惩戒措施：", "@!@惩戒措施：").split("@!@");
				for (String item : items) {
					if (item.contains("上榜事由：") && blackList != null) {
						blackList.setPunishReason(item.substring(5));// 处罚原因
					} else if (item.contains("惩戒措施：") && blackList != null) {
						blackList.setPunishResult(item.substring(5));// 处罚结果
						discreditBlacklistMapper.insert(blackList);// 保存记录
					}
				}
			}
		}
		log.info("=====四、白银市工商局企业诚信“黑榜”企业名单：======");
		// for (Element span : contents.subList(sign[3] + 1, sign[4])) {
		// String text = CrawlerUtil.replaceHtmlNbsp(span.text());
		// if (StrUtil.isEmpty(text)) {
		// continue;
		// }
		// // System.out.println(text);
		// }
		// 1、白银大福鑫矿业投资有限公司和白银佰力信投资管理有限公司
		// 上榜事由：均超过6个月未营业 惩戒措施：吊销营业执照。
		// 2、孙晓云、马如军
		// 上榜事由：介绍他人参加传销违法行为 惩戒措施：分别处罚5000元和4900元。
		blackList = createDiscreditBlacklistObject();
		blackList.setSubject("市工商局企业失信黑榜");// 主题
		blackList.setJudgeAuth("甘肃省白银市工商管理局");// 判决机关
		blackList.setEnterpriseName("白银大福鑫矿业投资有限公司");// 企业名称
		blackList.setPunishReason("超过6个月未营业");// 处罚原因
		blackList.setPunishResult("吊销营业执照");// 处罚结果
		discreditBlacklistMapper.insert(blackList);// 保存记录
		
		blackList = createDiscreditBlacklistObject();
		blackList.setSubject("市工商局企业失信黑榜");// 主题
		blackList.setJudgeAuth("甘肃省白银市工商管理局");// 判决机关
		blackList.setPunishReason("超过6个月未营业");// 处罚原因
		blackList.setPunishResult("吊销营业执照");// 处罚结果
		blackList.setEnterpriseName("白银佰力信投资管理有限公司");// 企业名称
		discreditBlacklistMapper.insert(blackList);// 保存记录
		
		blackList = createDiscreditBlacklistObject();
		blackList.setSubject("市工商局企业失信黑榜");// 主题
		blackList.setJudgeAuth("甘肃省白银市工商管理局");// 判决机关
		blackList.setPunishReason("介绍他人参加传销违法行为");// 处罚原因
		blackList.setPunishResult("处以罚款5000元");// 处罚结果
		blackList.setObjectType("02");
		blackList.setPersonName("孙晓云");
		discreditBlacklistMapper.insert(blackList);// 保存记录
		
		blackList = createDiscreditBlacklistObject();
		blackList.setSubject("市工商局企业失信黑榜");// 主题
		blackList.setJudgeAuth("甘肃省白银市工商管理局");// 判决机关
		blackList.setPunishReason("介绍他人参加传销违法行为");// 处罚原因
		blackList.setPunishResult("处以罚款4900元");// 处罚结果
		blackList.setObjectType("02");
		blackList.setPersonName("马如军");
		discreditBlacklistMapper.insert(blackList);// 保存记录
		
		log.info("=====五、白银市地税局企业诚信“黑榜”企业名单：======");
		for (Element span : contents.subList(sign[4] + 1, contents.size())) {
			String text = CrawlerUtil.replaceHtmlNbsp(span.text());
			if (StrUtil.isEmpty(text)) {
				continue;
			}
			if (Pattern.compile("\\d+、.*").matcher(text).matches()) {
				blackList = createDiscreditBlacklistObject();
				blackList.setSubject("市地税局企业失信名单");// 主题
				blackList.setJudgeAuth("甘肃省白银市地方税务局");// 判决机关
				blackList.setEnterpriseName(text.substring(text.indexOf("、") + 1));// 企业名称
				discreditBlacklistMapper.insert(blackList);
			}
		}
		log.debug("==============================");
	}

	/**
	 * 创建失信黑名单对象
	 * 
	 * @return
	 */
	private DiscreditBlacklist createDiscreditBlacklistObject() {
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
		blackList.setPersonName("");// 法定代表人/负责人姓名|负责人姓名
		blackList.setPersonId("");// 法定代表人身份证号|负责人身份证号
		blackList.setDiscreditType("");// 失信类型
		blackList.setDiscreditAction("");// 失信行为
		blackList.setPunishReason("");// 列入原因
		blackList.setPunishResult("");// 处罚结果
		blackList.setJudgeNo("");// 执行文号
		blackList.setJudgeDate("");// 执行时间
		blackList.setJudgeAuth("");// 判决机关
		blackList.setPublishDate("2016/01/05");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
