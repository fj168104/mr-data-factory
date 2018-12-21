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

import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（甘肃）-甘肃153家单位上诚信“红黑榜”名单，83 家企业上“黑榜”
 * 
 * http://www.gscredit.gov.cn/blackList/91101.jhtml
 * 
 * 
 * @author pxu 2018年6月15日
 */
@Slf4j
@Component("creditchina-gansu-black-91101")
@Scope("prototype")
public class CreditChinaGansuBlack91101 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/91101.jhtml";

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
	 * 抓取内容：新闻发布会时间、处罚机关、企业名称、主要违法行为、处罚惩罚措施
	 */
	public void extractContent(String url) throws Throwable {
		String contentHtml = getData(url);
		Document doc = Jsoup.parse(contentHtml);

		log.debug("==============================");
		Elements contents = doc.getElementsByClass("artical_content_wrap").first().getElementsByTag("p");

		DiscreditBlacklist discreditBlacklist = null;
		int itemIndex = 0;// 条目索引
		String subject = "";// 主题
		int iCount = 0;// 计数器
		for (Element content : contents) {
			String text = content.text();
			if (itemIndex == 0) {
				if (text.contains("甘肃省高级人民法院失信被执行人黑榜名单及惩戒措施")) {
					itemIndex = 1;
					iCount = 0;
					subject = "失信被执行人黑榜名单";
					continue;
				}
				if (text.contains("甘肃省地方税务局诚信纳税黑榜名单及惩戒措施")) {
					itemIndex = 2;
					iCount = 0;
					subject = "地方税务局失信";
					continue;
				}
				if (text.contains("甘肃省工商行政管理局黑榜企业名单")) {
					itemIndex = 3;
					iCount = 0;
					subject = "工商行政管理局黑榜";
					continue;
				}
				if (text.contains("甘肃省质量技术监督局质量违法黑榜企业名单")) {
					itemIndex = 4;
					iCount = 0;
					subject = "质量技术监督局黑名单";
					continue;
				}
				if (text.contains("甘肃省食品药品监督管理局食品药品黑榜名单及惩戒措施")) {
					itemIndex = 5;
					iCount = 0;
					subject = "食品药品监督管理局黑名单";
					continue;
				}
				if (text.contains("甘肃省国家税务局诚信纳税黑榜名单及惩戒措施")) {
					itemIndex = 6;
					iCount = 0;
					subject = "国家税务局诚信纳税黑榜";
					continue;
				}
				continue;
			}

			// 一、甘肃省高级人民法院失信被执行人黑榜名单及惩戒措施（28家）
			if (itemIndex == 1) {
				if (text.contains("惩戒措施")) {// 处理结束,重新判断itemIndex
					log.debug("1、共处理数据{}条，实际28条", iCount);
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省高级人民法院");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1).trim());// 企业名称
					discreditBlacklist.setPunishReason("均为拒不履行生效法律文书确定的义务。");
					String punishResult = "1.对上述单位及其法定代表人、主要负责人、影响债务履行的直接责任人员、实际控制人限制高消费，包括禁止乘坐飞机、列车软卧以上座位；禁止在星级以上酒店、餐馆、娱乐场所消费；禁止购买不动产或者新建、扩建、高档装修房屋；禁止租赁高档写字楼、宾馆、公寓等场所办公；禁止购买非经营必需车辆；禁止旅游、度假；禁止子女就读高收费私立学校；禁止支付高额保费购买保险理财产品；禁止乘坐G字头动车组列车全部座位、其他动车组列车一等以上座位等其他非生活和工作必需的消费行为。"
							+ "2.对上述单位及其法定代表人、主要负责人、影响债务履行的直接责任人员、实际控制人的失信行为，作为不良记录推送到人民银行征信系统、工商信用系统以及发改、财政、税务、国土、房产、海关等部门，联合实施信用惩戒。包括限制其在金融机构贷款和办理信用卡，限制其开办新公司和投资入股，限制参与政府采购、招标投标，限制行政审批、政府扶持、市场准入、资质认证等。"
							+ "3.对违反禁令高消费、有能力履行而拒不履行生效裁判的失信被执行人，人民法院将依法对单位处以100万元以下、对个人10万元以下罚款，对法定代表人和实际控制人采取司法拘留强制措施。情节严重构成犯罪的，根据《刑法》第三百一十三条和全国人大关于追究拒不执行法院判决罪立法解释的规定，严肃追究刑事责任。";
					discreditBlacklist.setPunishResult(punishResult);
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 二、甘肃省地方税务局诚信纳税黑榜名单及惩戒措施（21家）
			if (itemIndex == 2) {
				if (text.contains("惩戒措施")) {// 处理结束,重新判断itemIndex
					log.debug("2、共处理数据{}条，实际21条", iCount);
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[、].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省地方税务局");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("、") + 1).trim());
					String punishResult = "1. 根据《中华人民共和国税收征收管理法》第六十条规定：未按照规定的期限申报办理税务登记、变更或者注销登记的，未按照规定设置、保管账簿或者保管记账凭证和有关资料的,由税务机关责令限期改正，可以处二千元以下的罚款。情节严重的，处二千元以上一万元以下的罚款。"
							+ "2.根据《中华人民共和国税收征收管理法》第六十二条规定：纳税人未按照规定的期限办理纳税申报和报送纳税资料的，由税务机关责令限期改正，可以处二千元以下的罚款；情节严重的，可以处二千元以上一万元以下的罚款。"
							+ "3.根据《中华人民共和国税收征收管理法》第六十三条规定：纳税人进行虚假的纳税申报，不缴或者少缴应纳税款的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款；构成犯罪的，依法追究刑事责任。";
					discreditBlacklist.setPunishResult(punishResult);
				} else if (text.contains("原因：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("原因：", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 三、甘肃省工商行政管理局黑榜企业名单（14家）
			if (itemIndex == 3) {
				if (text.contains("惩戒措施")) {// 处理结束,重新判断itemIndex
					log.debug("3、共处理数据{}条，实际14条", iCount);
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[\\.|,].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省工商行政管理局");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1).trim());
					String punishResult = "1、吊销营业执照。" //
							+ "2、列入经营异常名录并通过国家企业信用信息公示系统公示。"//
							+ "3、撤销变更登记，列入严重违法失信企业名单并通过国家企业信用信息公示系统公示。";//
					if ("白银倚天通物资有限公司".equals(discreditBlacklist.getEnterpriseName())) {
						punishResult = punishResult + "4、给予2万元行政处罚，将行政处罚决定通过国家企业信用信息公示系统公示。";
					}
					discreditBlacklist.setPunishResult(punishResult);
				} else if (text.contains("原因:") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("原因:", ""));
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 四、甘肃省质量技术监督局质量违法黑榜企业名单（2家）
			if (itemIndex == 4) {
				if (text.contains("惩戒措施")) {
					continue;
				}
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省质量技术监督局");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1).trim());
				} else if (text.contains("原因：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("原因：", ""));
				} else if (discreditBlacklist != null && StrUtil.isNotEmpty(text)) {
					discreditBlacklist.setPunishResult(text);
                    discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
					if (iCount == 2) {// 若处理了两条数据，则处理结束
						log.debug("4、共处理数据{}条，实际2条", iCount);
						itemIndex = 0;
					}
				}
				continue;
			}
			// 五、甘肃省食品药品监督管理局食品药品黑榜名单及惩戒措施（15家）
			if (itemIndex == 5) {
				if (text.contains("惩戒措施")) {// 处理结束
					log.debug("5、共处理数据{}条，实际15条", iCount);
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省食品药品监督管理局");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1).trim());
					String punishResult = "1. 各级食品药品监督管理部门加大“黑榜”信息的公开曝光力度。对辖区内列入食品药品“黑榜”企业和单位要在其政务网站和当地媒体公开曝光，并将“黑榜”名单向同级卫生、工商、金融等部门通报。"
							+ "2. 各级食品药品监督管理部门加大“黑榜”企业和单位的监管力度。把列入食品药品“黑榜”名单的生产经营企业和使用单位作为重点监管对象，通过增加监督检查和抽验频次等方式加强监管。列入食品药品“黑榜”名单的生产经营者、责任人员，再次发生违法违规行为的，依法从重处罚。"
							+ "3. 充分依靠人民群众，营造食品药品安全社会共治格局。畅通12331食品药品安全投诉举报渠道，落实举报奖励制度，鼓励社会组织或个人对食品药品“黑榜”企业和单位进行监督，举报列入食品药品“黑榜”名单的生产经营者和责任人员的违法行为。";
					discreditBlacklist.setPunishResult(punishResult);
				} else if (text.contains("原因：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("原因：", ""));
                    discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			//六、甘肃省国家税务局诚信纳税黑榜名单及惩戒措施（3家）
			if (itemIndex == 6) {
				if (text.contains("惩戒措施")) {// 处理结束
					log.debug("6、共处理数据{}条，实际3条", iCount);
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省国家税务局");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1).trim());
					String punishResult = "1.对于达到重大税收违法案件信息公布标准的案件，依据《关于对重大税收违法案件当事人实施联合惩戒措施的合作备忘录(2016版)》的惩戒措施及操作程序，与有关部门共同进行联合惩戒。"
							+ "2.纳税人不进行纳税申报，不缴或者少缴应纳税款的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款。"
							+ "3.纳税人、扣缴义务人有《税收征管法》第六十三条、第六十五条、第六十六条、第六十七条、第七十一条规定的行为涉嫌犯罪的，税务机关应当依法移交司法机关追究刑事责任。";
					discreditBlacklist.setPunishResult(punishResult);
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
		blackList.setPublishDate("2017/10/23");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
