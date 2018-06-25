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
 * 2.url:http://www.gscredit.gov.cn/blackList/94092.jhtml
 */
@Slf4j
@Component("gansu_94092")
@Scope("prototype")
public class Gansu_94092 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94092.jhtml";

	@Autowired
	DiscreditBlacklistMapper discreditBlacklistMapper;

	@Override
	protected String executeOne() throws Throwable {
		return super.executeOne();
	}

	@Override
	protected String execute() throws Throwable {
		extractContext(url);
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

		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text();
				if (subject.contains("甘肃省高级人民法院失信被执行人黑榜名单及惩戒措施")) {
					subject = "省高级人民法院诚信黑榜";
					continue;
				}
				if (subject.contains("甘肃省质监局质量违法企业黑榜名单及惩戒措施")) {
					subject = "省质监局诚信黑榜";
					continue;
				}
				if (subject.contains("甘肃省地税局地方税收诚信纳税黑榜名单及惩戒措施")) {
					subject = "省地税局税收诚信黑榜";
					continue;
				}
				if (subject.contains("甘肃省国家税务局黑榜企业名单及惩戒措施")) {
					subject = "省国税局诚信黑榜";
					continue;
				}

				subject = "";
				continue;
			}

			//省高级人民法院诚信黑榜
			if (subject.equals("省高级人民法院诚信黑榜")) {
				String text = elelementSpans.first().text();
				//省高级人民法院诚信黑榜 处理结束
				if (text.contains("原因：")) {
					subject = "";
					continue;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text);
				discreditBlacklist.setPunishReason("均为拒不履行生效法律文书确定的义务");
				String punishResult = "（1）对上述单位和法定代表人限制高消费，包括禁止乘坐飞机、列车软卧以上座位，禁止在星级以上酒店、餐馆、娱乐场所消费。" +
						"（2）将上述单位及其法定代表人的失信行为，作为不良记录推送到人民银行征信系统、工商信用系统以及发改、财政、税务、国土、房产、海关等部门，联合实施信用惩戒。包括限制其在金融机构贷款和办理信用卡，限制其开办新公司和投资入股，限制参与政府采购、招标投标，限制行政审批、政府扶持、市场准入、资质认证等。" +
						"（3）对违反禁令高消费、有能力履行而拒不履行生效裁判的失信被执行人，人民法院将依法对单位处以100万元以下、对个人10万元以下罚款，对法定代表人和实际控制人采取司法拘留强制措施。情节严重构成犯罪的，根据《刑法》第三百一十三条和全国人大关于追究拒不执行法院判决罪立法解释的规定，严肃追究刑事责任。";
				discreditBlacklist.setPunishResult(punishResult);
				discreditBlacklistMapper.insert(discreditBlacklist);
				continue;
			}

			//省质监局诚信黑榜
			if (subject.equals("省质监局诚信黑榜")) {
				String text = elelementSpans.first().text();
				//省质监局诚信黑榜 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}

				if (text.contains("原因：")) {
					dcbl.setPunishReason(text.replace("原因：", ""));
					String punishResult = "（1）责令企业停止生产，并对违法行为进行整改。" +
							"（2）采取罚款、停业整顿、没收违法所得等行政处罚。" +
							"（3）根据违法事实和情节，触犯刑法的一律移送司法机关。兰州高科石油化工有限公司、兰州陕西宝粤物资有限公司、酒泉祁茂农资批发中心的违法事实已移送公安机关。" +
							"（4）不受理纳入黑榜名单的企业及其经营管理人员任何评先选优等表彰申请。";
					dcbl.setPunishResult(punishResult);
					discreditBlacklistMapper.insert(dcbl);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text);
				continue;
			}

			//省地税局税收诚信黑榜
			if (subject.equals("省地税局税收诚信黑榜")) {
				String text = elelementSpans.first().text();
				//省地税局税收诚信黑榜 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}

				if (text.contains("原因：")) {
					dcbl.setPunishReason(text.replace("原因：", ""));
					String punishResult = "（1）纳税人未按照规定的期限办理纳税申报和报送纳税资料的，或者扣缴义务人未按照规定的期限向税务机关报送代扣代缴、代收代缴税款报告表和有关资料的，由税务机关责令限期改正，可以处二千元以下的罚款；情节严重的，可以处二千元以上一万元以下的罚款。" +
							"（2）纳税人出现伪造、变造、隐匿、擅自销毁账簿、记账凭证，或者在账簿上多列支出或者不列、少列收入，或者经税务机关通知申报而拒不申报或者进行虚假的纳税申报，不缴或者少缴应纳税款等偷税行为的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款；构成犯罪的移交司法机关追究刑事责任。" +
							"（3）纳税人、扣缴义务人编造虚假计税依据的，由税务机关责令限期改正，并处五万元以下的罚款。" +
							"（4）纳税人不进行纳税申报，不缴或者少缴应纳税款的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款。";
					dcbl.setPunishResult(punishResult);
					discreditBlacklistMapper.insert(dcbl);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text);
				continue;
			}

			//省国税局诚信黑榜
			if (subject.equals("省国税局诚信黑榜")) {
				String text = elelementSpans.first().text();
				//省国税局诚信黑榜 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}

				if (text.contains("原因：")) {
					dcbl.setPunishReason(text.replace("原因：", ""));
					String punishResult = "（1）依法追缴纳税人不进行纳税申报，不缴或少缴的税款。" +
							"（2）依法对纳税人处以欠缴税款百分之五十以上五倍以下的罚款；构成犯罪的，依法追究刑事责任。" +
							"（3）从纳税人滞纳税款之日起，依法按日加收滞纳税款万分之五的滞纳金。" +
							"（4）继续加大黑榜纳税人信息曝光力度，对黑榜纳税人日常经营活动重点监控，进一步完善奖励诚信、约束失信的制度体系。";
					dcbl.setPunishResult(punishResult);
					discreditBlacklistMapper.insert(dcbl);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text);
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
		discreditBlacklist.setPublishDate("2015/10/27");
		return discreditBlacklist;
	}

}
