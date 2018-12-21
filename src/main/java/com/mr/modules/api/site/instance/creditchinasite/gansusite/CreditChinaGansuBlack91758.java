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
 * 信用中国（甘肃）-甘肃发布诚信“红黑榜”名单 61家单位上“黑榜”
 * 
 * http://www.gscredit.gov.cn/blackList/91758.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-black-91758")
@Scope("prototype")
public class CreditChinaGansuBlack91758 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/91758.jhtml";

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
				if (text.contains("一、甘肃省高级人民法院失信被执行人黑榜名单及惩戒措施（35家）")) {
					itemIndex = 1;
					iCount = 0;
					subject = "省高级人民法院诚信黑榜";
					continue;
				}
				if (text.contains("二、甘肃省地方税务局诚信纳税黑榜名单及惩戒措施（10家）")) {
					itemIndex = 2;
					iCount = 0;
					subject = "省地方税务局诚信黑榜";
					continue;
				}
				if (text.contains("三、甘肃省质量技术监督局质量违法企业黑榜名单及惩戒措施（2家）")) {
					itemIndex = 3;
					iCount = 0;
					subject = "省质监局诚信黑榜";
					continue;
				}
				if (text.contains("四、甘肃省国家税务局诚信纳税黑榜名单及惩戒措施（14家）")) {
					itemIndex = 4;
					iCount = 0;
					subject = "省国税局诚信黑榜";
					continue;
				}
				continue;
			}

			// 一、甘肃省高级人民法院失信被执行人黑榜名单及惩戒措施（35家）
			if (itemIndex == 1) {
				if (text.contains("原因：")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {// 数据行
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省高级人民法院");
					int startIndex = text.indexOf(".");
					int endIndex = text.indexOf("（");
					if (endIndex == -1) {
						endIndex = text.length();
					}
					discreditBlacklist.setEnterpriseName(text.substring(startIndex + 1, endIndex).trim());// 企业名称
					discreditBlacklist.setPunishReason("拒不履行生效法律文书确定的义务");
					String punishResult = "1.对该单位及其法定代表人、主要负责人、影响债务履行的直接责任人员、实际控制人限制高消费，包括禁止乘坐飞机、列车软卧以上座位；禁止在星级以上酒店、餐馆、娱乐场所消费；禁止购买不动产或者新建、扩建、高档装修房屋；禁止租赁高档写字楼、宾馆、公寓等场所办公；禁止购买非经营必需车辆；禁止旅游、度假；禁止子女就读高收费私立学校；禁止支付高额保费购买保险理财产品；禁止乘坐G字头动车组列车全部座位、其他动车组列车一等以上座位等其他非生活和工作必需的消费行为。"//
							+ "2.对该单位及其法定代表人、主要负责人、影响债务履行的直接责任人员、实际控制人的失信行为，作为不良记录推送到人民银行征信系统、工商信用系统以及发改、财政、税务、国土、房产、海关等部门，联合实施信用惩戒。包括限制其在金融机构贷款和办理信用卡，限制其开办新公司和投资入股，限制参与政府采购、招标投标，限制行政审批、政府扶持、市场准入、资质认证等。"//
							+ "3.对违反禁令高消费、有能力履行而拒不履行生效裁判的失信被执行人，人民法院将依法对单位处以100万元以下、对个人10万元以下罚款，对法定代表人和实际控制人采取司法拘留强制措施。情节严重构成犯罪的，根据《刑法》第三百一十三条和全国人大关于追究拒不执行法院判决罪立法解释的规定，严肃追究刑事责任。";
					discreditBlacklist.setPunishResult(punishResult);
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 二、甘肃省地方税务局诚信纳税黑榜名单及惩戒措施（10家）
			// 三、甘肃省质量技术监督局质量违法企业黑榜名单及惩戒措施（2家）
			// 四、甘肃省国家税务局诚信纳税黑榜名单及惩戒措施（14家）
			if (itemIndex == 2 || itemIndex == 3 || itemIndex == 4) {
				if (text.contains("惩戒措施：")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {// 数据行
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					int startIndex = text.indexOf(".");
					int endIndex = text.indexOf("（");
					if (endIndex == -1) {
						endIndex = text.length();
					}
					discreditBlacklist.setEnterpriseName(text.substring(startIndex + 1, endIndex).trim());// 企业名称
					if (itemIndex == 2) {
						discreditBlacklist.setJudgeAuth("甘肃省地方税务局");
						String punishResult = "1.根据《中华人民共和国税收征收管理法》第六十条规定：未按照规定的期限申报办理税务登记、变更或者注销登记的，未按照规定设置、保管账簿或者保管记账凭证和有关资料的,由税务机关责令限期改正，可以处二千元以下的罚款。情节严重的，处二千元以上一万元以下的罚款。"//
								+ "2.根据《中华人民共和国税收征收管理法》第六十二条规定：纳税人未按照规定的期限办理纳税申报和报送纳税资料的，由税务机关责令限期改正，可以处二千元以下的罚款；情节严重的，可以处二千元以上一万元以下的罚款。"//
								+ "3.根据《中华人民共和国税收征收管理法》第六十三条规定：纳税人进行虚假的纳税申报，不缴或者少缴应纳税款的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款；构成犯罪的，依法追究刑事责任。"//
								+ "4.根据《中华人民共和国税收征收管理法》第六十四条规定：纳税人、扣缴义务人编造虚假计税依据的，由税务机关责令限期改正，并处五万元以下的罚款。纳税人不进行纳税申报，不缴或者少缴应纳税款的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款。"//
								+ "5.根据《中华人民共和国税收征收管理法》第六十八条规定：纳税人、扣缴义务人在规定期限内不缴或者少缴应纳或者应解缴的税款，经税务机关责令限期缴纳，逾期仍未缴纳的，税务机关除依照本法第四十条的规定采取强制执行措施追缴其不缴或者少缴的税款外，可以处不缴或者少缴的税款百分之五十以上五倍以下的罚款。";
						discreditBlacklist.setPunishResult(punishResult);
					} else if (itemIndex == 3) {
						discreditBlacklist.setJudgeAuth("甘肃省质量技术监督局");
						discreditBlacklist.setPunishResult("1、责令企业停止生产，并对违法行为进行整改。2、采取处罚、停业整顿、没收违法所得等行政处罚措施。3、根据违法事实和情节，触犯刑法的一律移送司法机关。4、不受理纳入黑榜名单企业及其经营管理人员的任何评先选优等表彰申请。");
					} else {
						discreditBlacklist.setJudgeAuth("甘肃省国家税务局");
						discreditBlacklist.setPunishResult("1.对于达到重大税收违法案件信息公布标准的案件，依据《关于对重大税收违法案件当事人实施联合惩戒措施的合作备忘录》的惩戒措施及操作程序，与有关部门共同进行联合惩戒。2.纳税人不进行纳税申报，不缴或者少缴应纳税款的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款。3.纳税人、扣缴义务人有《税收征管法》第六十三条、第六十五条、第六十六条、第六十七条、第七十一条规定的行为涉嫌犯罪的，税务机关应当依法移交司法机关追究刑事责任。");
					}
				} else if (text.contains("原因：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("原因：", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
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
		blackList.setPublishDate("2016/06/27");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
