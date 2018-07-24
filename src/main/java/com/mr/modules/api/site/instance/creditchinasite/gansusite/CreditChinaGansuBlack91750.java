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
 * 信用中国（甘肃）-第三期“共筑诚信·德润武威”诚信“黑榜”
 * 
 * http://www.gscredit.gov.cn/blackList/91750.jhtml
 * 
 * 
 * @author pxu 2018年6月25日
 */
@Slf4j
@Component("creditchina-gansu-black-91750")
@Scope("prototype")
public class CreditChinaGansuBlack91750 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/91750.jhtml";

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
	 * 抓取内容：处罚机关、相关条令、企业名称、处罚事由、惩戒措施、审核机关
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
			String text = CrawlerUtil.replaceHtmlNbsp(content.text());
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (itemIndex == 0) {
				if (text.contains("市农牧局发布诚信“黑榜”名单")) {
					itemIndex = 1;
					iCount = 0;
					subject = "市农牧局诚信黑榜";
					continue;
				}
				if (text.contains("市质监局发布诚信“黑榜”名单")) {
					itemIndex = 2;
					iCount = 0;
					subject = "市质监局诚信黑榜";
					continue;
				}
				if (text.contains("市食品药品监督管理局发布2015年诚信“黑榜”名单")) {
					itemIndex = 3;
					iCount = 0;
					subject = "市食品药品质量监督局黑榜";
					continue;
				}
				if (text.contains("市国税局发布企业纳税诚信“黑榜”名单")) {
					itemIndex = 4;
					iCount = 0;
					subject = "市国税局诚信黑榜";
					continue;
				}
				if (text.contains("市地税局发布企业纳税诚信“黑榜”名单")) {
					itemIndex = 5;
					iCount = 0;
					subject = "市地税局诚信黑榜";
					continue;
				}
				if (text.contains("人行武威中心支行发布金融系统信贷诚信“黑榜”名单")) {
					itemIndex = 6;
					iCount = 0;
					subject = "人行武威支行信贷黑榜";
					continue;
				}
				continue;
			}

			// 市农牧局发布诚信“黑榜”名单（1家）
			if (itemIndex == 1) {
				if (text.contains("惩戒措施：")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省武威市农牧局");
					discreditBlacklist.setEnterpriseName(text);// 企业名称
					discreditBlacklist.setPunishResult("1.根据《农药管理条例》规定，罚款0.1万元；2.列入重点监控对象，提高监督检查频次。");
				}
				continue;
			}
			// 市质监局发布诚信“黑榜”名单（1家）
			if (itemIndex == 2) {
				if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", ""));
				} else if (text.contains("惩戒措施：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishResult(text.replace("惩戒措施：", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
					itemIndex = 0;// 处理结束
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省武威市质监局");
					discreditBlacklist.setEnterpriseName(text);// 企业名称
				}
				continue;
			}
			// 市食品药品监督管理局发布2015年诚信“黑榜”名单（11家）
			if (itemIndex == 3) {
				if (text.contains("（一）食品生产环节") || text.contains("（二）食品流通环节") || text.contains("（三）餐饮服务环节") || text.contains("（四）药品使用环节") || text.contains("（五）药品流通环节")) {
					continue;
				}
				if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", ""));
				} else if (text.contains("惩戒措施：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishResult(text.replace("惩戒措施：", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
					if (iCount == 11) {// 处理结束
						itemIndex = 0;
					}
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省武威市食品药品监督管理局");
					discreditBlacklist.setEnterpriseName(text);// 企业名称
				}
				continue;
			}
			// 市国税局发布企业纳税诚信“黑榜”名单（1家）
			if (itemIndex == 4) {
				if (text.contains("惩戒措施：")) {// 处理结束
					itemIndex = 0;
					continue;
				}
				if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省武威市国税局");
					discreditBlacklist.setEnterpriseName(text);// 企业名称
					String punishResult = "1.根据《中华人民共和国税收征收管理法》及相关税收法律法规的规定，追缴该公司少缴的税款12.66万元，加收滞纳金0.23万元，并处以少缴税款0.5倍的罚款6.33万元，共计追缴入库19.22万元。" //
							+ "2.将纳税信用级别直接判为D级，适用《纳税信用管理办法（试行）》关于D级纳税人的管理措施；"//
							+ "3.增值税专用发票领用按辅导期一般纳税人政策办理，普通发票的领用实行交（验）旧供新、严格限量供应；"//
							+ "4.缩短纳税评估周期，严格审核其报送的各种资料；"//
							+ "5.列入重点监控对象，提高监督检查频次，发现税收违法违规行为的，不得适用规定处罚幅度内的最低标准；"//
							+ "6.将纳税信用评价结果通报相关部门，与相关部门实施联合惩戒措施，并结合实际情况依法采取其他严格管理措施。";
					discreditBlacklist.setPunishResult(punishResult);
				}
				continue;
			}
			// 市地税局发布企业纳税诚信“黑榜”名单（3家）
			if (itemIndex == 5) {
				if (text.equals("惩戒措施：")) {// 第二条数据单独处理
					continue;
				}
				if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", ""));
				} else if (text.contains("惩戒措施：") && discreditBlacklist != null) {// 第1和第3条数据入库
					discreditBlacklist.setPunishResult(text.replace("惩戒措施：", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
					if (iCount == 3) {// 处理结束
						itemIndex = 0;
					}
				} else if (Pattern.compile("\\d+[\\.].*").matcher(text).matches() && discreditBlacklist != null) {// 第2条数据入库
					discreditBlacklist.setPunishResult(discreditBlacklist.getPunishResult() + text);
					if (text.startsWith("3")) {
						discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
						discreditBlacklistMapper.insert(discreditBlacklist);
					}
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省武威市地税局");
					discreditBlacklist.setEnterpriseName(text);// 企业名称
				}
				continue;
			}
			// 人行武威中心支行发布金融系统信贷诚信“黑榜”名单（2家）
			if (itemIndex == 6) {
				if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", ""));
				} else if (text.contains("惩戒措施：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishResult(text.replace("惩戒措施：", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					discreditBlacklistMapper.insert(discreditBlacklist);
					if (iCount == 2) {// 处理结束
						itemIndex = 0;
					}
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("人行武威中心支行");
					discreditBlacklist.setEnterpriseName(text);// 企业名称
				}
				continue;
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
		blackList.setPublishDate("2016/08/02");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
