package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import com.mr.framework.core.collection.CollectionUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @auther 1.信用中国（甘肃）
 * 2.url:http://www.gscredit.gov.cn/blackList/94112.jhtml
 */
@Slf4j
@Component("gansu_94112")
@Scope("prototype")
public class Gansu_94112 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94112.jhtml";

	@Override
	protected String executeOne() throws Throwable {
		return super.executeOne();
	}

	@Override
	protected String execute() throws Throwable {
		try {
			extractContext(url);
		}catch (Exception e){
			writeBizErrorLog(url, e.getMessage());
		}
		return null;
	}

	/**
	 * 获取网页内容
	 * 发布单位投诉电话、新闻发布日期、企业名称、企业所在地、上榜原因、惩戒措施、
	 */
	public void extractContext(String url) {
		DiscreditBlacklist dcbl = null;
		Document document = Jsoup.parse(getData(url));
		Elements elementDivs = document.getElementsByTag("div");
		String subject = "";

		String pr = "";
		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text().replaceAll("\\s*", "");
				if (subject.contains("一、酒泉市中级人民法院失信被执行人")) {
					subject = "市中级人民法院失信被执行人";
					continue;
				}

				if (subject.contains("年度重大税收违法企业名单")) {
					subject = "市国税局诚信黑榜";
					continue;
				}

				if (subject.contains("食品药品生产经营失信黑名单")) {
					subject = "市食药监管局诚信黑榜";
					continue;
				}

				if (subject.contains("酒泉市环境保护局工业企业环境保护标准化建设暨企业环境信用评价名单")) {
					subject = "市环境保护局信用评价名单";
					continue;
				}
				subject = "";
				continue;
			}

			//市中级人民法院失信被执行人
			if (subject.equals("市中级人民法院失信被执行人")) {
				String text = elelementSpans.first().text();
				//市中级人民法院失信被执行人 处理结束
				if (text.contains("原因：")) {
					subject = "";
					continue;
				}

				String punishReason = "均为拒不履行生效法律文书确定的义务";
				String punishResult = "（1）对上述单位和法定代表人限制高消费，包括禁止乘坐飞机、列车软卧以上座位，禁止在星级以上酒店、餐馆、娱乐场所消费。" +
						"（2）对上述单位及其法定代表人的失信行为，作为不良记录推送到人民银行征信系统、工商信用系统以及发改、财政、税务、国土、房产、海关等部门，联合实施信用惩戒。包括限制其在金融机构贷款和办理信用卡，限制其开办新公司和投资入股，限制参与政府采购、招标投标，限制行政审批、政府扶持、市场准入、资质认证等。" +
						"（3）对违反禁令高消费、有能力履行而拒不履行生效裁判的失信被执行人，人民法院将依法对单位处以100万元以下、对个人10万元以下罚款，对法定代表人和实际控制人采取司法拘留强制措施。情节严重构成犯罪的，根据《刑法》第三百一十三条、全国人大关于追究拒不执行法院判决罪立法解释和最高人民法院《关于审理拒不执行判决、裁定刑事案件适用法律若干问题的解释》的规定，严肃追究刑事责任。";

				if (!text.contains("（法人：")) continue;
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setObjectType("01");
				discreditBlacklist.setEnterpriseName(text.substring(0, text.indexOf("（法人：")));
				discreditBlacklist.setPersonName(text.substring(text.indexOf("（法人：") + 1, text.indexOf("，")).replaceAll("\\s*", ""));
				discreditBlacklist.setPunishReason(punishReason);
				discreditBlacklist.setPunishResult(punishResult);
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;

			}

			//市国税局诚信黑榜
			if (subject.equals("市国税局诚信黑榜")) {
				String text = elelementSpans.first().text();
				//市国税局诚信黑榜 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}

				String punishResult = "依法追缴纳税人多列支出或者不列、少列收入，进行虚假的纳税申报不缴或者少缴的应纳税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款；对黑榜纳税人日常经营活动重点监控。";

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setObjectType("01");
				discreditBlacklist.setEnterpriseName(text);
				discreditBlacklist.setPunishResult(punishResult);
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;
			}

			//市食药监管局诚信黑榜
			if (subject.equals("市食药监管局诚信黑榜")) {
				String text = elelementSpans.first().text();
				//市食药监管局诚信黑榜 处理结束
				if (text.contains("酒泉市食品药品监督管理局对列入守信")) {
					subject = "";
					continue;
				}

				String punishResult = "酒泉市食品药品监督管理局对列入守信红名单的企业，优先评定为诚信企业和食品安全示范店，起到示范引领作用。" +
						"对列入失信黑名单的企业，依法对其违法行为作出行政处罚，" +
						"查处情况统一在市食药监管局政务网站和《酒泉日报飞天周刊》进行了公示。" +
						"在日常监管中，对黑名单的企业增加监管频次，落实跟踪整改。" +
						"对企业负责人及从业人员约谈培训，明确企业作为第一责任人的意识，督促守法经营诚信从商，直至达到规定的要求与标准。" +
						"对未整改的，实行市场禁入和退出机制。";
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setObjectType("01");
				discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("、") + 1));
				discreditBlacklist.setPunishResult(punishResult);
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;
			}

			//市环境保护局信用评价名单
			if (subject.equals("市环境保护局信用评价名单")) {
				String text = elelementSpans.first().text();
				//市环境保护局信用评价名单 处理结束
				if (text.contains("惩戒性措施：")) {
					subject = "";
					break;
				}


				if (text.contains("环境保护标准化建设评定C级、环境信用评价等级警示的企业名单")) {
					pr = "对标准化建设达到C级企业和环境信用评价为警示等级企业，采取约束性措施。" +
							"一是责令企业按季度向直接对该企业实施日常环境监管的环保部门，书面报告发现问题的整改情况；" +
							"二是从严审查其危险废物经营许可证、可用作原料的固体废物进口许可证以及其他行政许可申请事项；" +
							"三是加大执法监察频次；" +
							"四是从严审批各类环保专项资金补助申请；" +
							"五是环保部门在组织有关评优评奖活动中，暂停授予其有关荣誉称号；" +
							"六是建议银行业金融机构严格贷款条件；" +
							"七是建议保险机构适度提高环境污染责任保险费率；" +
							"八是将环保警示企业名单通报有关国有资产监督管理部门、有关工会组织、有关行业协会以及其他有关机构，建议对环保警示企业及其负责人暂停授予先进企业或者先进个人等荣誉称号。";
					continue;
				}

				if (text.contains("环境保护标准化建设评定不达标、环境信用评价等级不良的企业名单")) {
					pr = "对标准化建设达到不达标企业和环境信用评价为不良等级企业，采取以下约束性措施。" +
							"一是责令其向社会公布改善环境行为的计划或者承诺，按季度向直接对该企业实施日常环境监管的环保部门，书面报告发现问题的整改情况；改善环境行为的计划或者承诺的内容，应当包括加强内部环境管理，整改失信行为，增加自行监测频次，加大环保投资，落实环保责任人等具体措施及完成时限。" +
							"二是结合其环境问题的类别和具体情节，从严审查其危险废物经营许可证、可用作原料的固体废物进口许可证以及其他行政许可申请事项；" +
							"三是加大执法监察频次；" +
							"四是暂停各类环保专项资金补助；" +
							"五是建议财政等有关部门在确定和调整政府采购名录时，取消其产品或者服务；" +
							"六是环保部门在组织有关评优评奖活动中，不得授予其有关荣誉称号；" +
							"七是建议银行业金融机构对其审慎授信，在其环境标准化建设和环境信用等级提升之前，不予新增贷款，并视情况逐步压缩贷款，直至退出贷款；" +
							"八是建议保险机构提高环境污染责任保险费率；" +
							"九是将环保不良企业名单通报有关国有资产监督管理部门、有关工会组织、有关行业协会以及其他有关机构，建议对环保不良企业及其负责人不得授予先进企业或者先进个人等荣誉称号；" +
							"十是国家或者地方规定的其他惩戒性措施。";
					continue;
				}
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setObjectType("01");
				discreditBlacklist.setEnterpriseName(text);
				discreditBlacklist.setPunishResult(pr);
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;
			}

		}

	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist() {
		DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();

		discreditBlacklist.setCreatedAt(new Date());
		discreditBlacklist.setUpdatedAt(new Date());
		discreditBlacklist.setSource("信用中国（甘肃）");
		discreditBlacklist.setUrl(url);
		discreditBlacklist.setObjectType("01");
		discreditBlacklist.setEnterpriseCode1("");
		discreditBlacklist.setEnterpriseCode2("");
		discreditBlacklist.setEnterpriseCode3("");
		discreditBlacklist.setPersonName("");
		discreditBlacklist.setPersonId("");
		discreditBlacklist.setDiscreditType("");
		discreditBlacklist.setDiscreditAction("");
		discreditBlacklist.setJudgeNo("");
		discreditBlacklist.setJudgeDate("");
		discreditBlacklist.setJudgeAuth("");
		discreditBlacklist.setStatus("");
		discreditBlacklist.setPublishDate("2015/10/26");
		return discreditBlacklist;
	}

}
