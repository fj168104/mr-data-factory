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
 * 2.url:http://www.gscredit.gov.cn/blackList/94105.jhtml
 */
@Slf4j
@Component("gansu_94105")
@Scope("prototype")
public class Gansu_94105 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94105.jhtml";

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

		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text();
				if (subject.contains("市中级人民法院失信被执行企业")) {
					subject = "市中级人民法院诚信黑榜";
					continue;
				}
				if (subject.contains("人行平凉中心支行失信企业")) {
					subject = "人行平凉中心支行失信企业";
					continue;
				}
				if (subject.contains("市人社局支付农民工工资失信企业")) {
					subject = "市人社局支付工资失信";
					continue;
				}
				if (subject.contains("市工商局失信企业")) {
					subject = "市工商局失信企业";
					continue;
				}
				if (subject.contains("市食药监局食品药品失信企业")) {
					subject = "市食药监局失信企业";
					continue;
				}


				subject = "";
				continue;
			}

			//市中级人民法院诚信黑榜
			if (subject.equals("市中级人民法院诚信黑榜")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//市中级人民法院诚信黑榜 处理结束
				if (text.contains("事由：")) {
					subject = "";
					continue;
				}
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text.substring(0, text.indexOf("（")));
				discreditBlacklist.setPersonName(text.substring(text.indexOf("法定代表人："))
						.replace("法定代表人：", "").replace("）", ""));
				discreditBlacklist.setPunishReason("未按法院判决履行清偿义务，法定代表人躲债外出，下落不明。");
				String punishResult = "（1）查封、扣押企业抵押财产，冻结银行账户；" +
						"（2）对企业的失信行为在征信系统记录，并通过媒体公布不履行义务信息；" +
						"（3）对华亭县安源实业有限责任公司法定代表人采取拘留措施，并申请公安机关协助布控;对平凉崆峒水泥有限责任公司、平凉华陇宾馆有限责任公司法定代表人限制出境、限制高消费、纳入人民银行征信系统。";
				discreditBlacklist.setPunishResult(punishResult);
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;
			}

			//人行平凉中心支行失信企业
			if (subject.equals("人行平凉中心支行失信企业")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//人行平凉中心支行失信企业 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}

				if (text.contains("事由：")) {
					dcbl.setPunishReason(text.replace("事由：", ""));
					saveDisneycreditBlackListOne(dcbl, false);
					continue;
				}
				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(0, text.indexOf("（")));
				dcbl.setPersonName(text.substring(text.indexOf("法定代表人："))
						.replace("法定代表人：", "").replace("）", ""));

				String punishResult = "1）违约记录记入全国金融信用信息基础数据库；" +
						"（2）降低金融机构内部评级结果；" +
						"（3）各金融机构终止授信，将企业及法定代表人列为信贷交易禁入客户。";
				dcbl.setPunishResult(punishResult);
				continue;
			}

			//市人社局支付工资失信
			if (subject.equals("市人社局支付工资失信")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//市人社局支付工资失信 处理结束
				if (text.contains("市工商局失信企业")) {
					subject = "市工商局失信企业";
					continue;
				}

				if (text.contains("事由：")) {
					dcbl.setPunishReason(text.replace("事由：", ""));
					continue;
				}

				if (text.contains("惩戒措施：")) {
					dcbl.setPunishResult(text.replace("惩戒措施：", ""));
					saveDisneycreditBlackListOne(dcbl, false);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(0, text.indexOf("（")));
				dcbl.setPersonName(text.substring(text.indexOf("：") + 1, text.indexOf("）")));
				continue;
			}

			//市工商局失信企业
			if (subject.equals("市工商局失信企业")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//市工商局失信企业 处理结束
				if (text.contains("事由：")) {
					subject = "";
					continue;
				}
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text.substring(0, text.indexOf("（")));
				discreditBlacklist.setPersonName(text.substring(text.indexOf("法定代表人："))
						.replace("法定代表人：", "").replace("）", ""));
				discreditBlacklist.setPunishReason("连续三年未参加企业年度检验。");
				String punishResult = "1）吊销《营业执照》；" +
						"（2）通过企业信用信息公示系统向社会公示；" +
						"（3）自吊销《营业执照》之日起，法定代表人三年内不得担任其他企业的法定代表人。";
				discreditBlacklist.setPunishResult(punishResult);
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;
			}

			//市食药监局失信企业
			if (subject.equals("市食药监局失信企业")) {
				String text = elelementSpans.first().text();
				text = text.replace("(", "（").replace(")", "）");
				//市食药监局失信企业 处理结束
				if (text.contains("发布单位投诉举报电话")) {
					subject = "";
					break;
				}

				if (text.contains("事由：")) {
					dcbl.setPunishReason(text.replace("事由：", ""));
					continue;
				}
				if (text.contains("惩戒措施：")) {
					dcbl.setPunishResult(text.replace("惩戒措施：", ""));
					saveDisneycreditBlackListOne(dcbl, false);
					continue;
				}

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(0, text.indexOf("（")));
				dcbl.setPersonName(text.substring(text.indexOf("法定代表人："))
						.replace("法定代表人：", "").replace("）", ""));
				continue;
			}
		}

	}

	protected boolean saveDisneycreditBlackListOne(DiscreditBlacklist discreditBlacklist, Boolean isForce) {
		try{
			discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
			return super.saveDisneycreditBlackListOne(discreditBlacklist, false);
		}catch (Exception e){
			writeBizErrorLog(url, e.getMessage());
		}
		return true;
	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist() {
		DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();

		discreditBlacklist.setCreatedAt(new Date());
		discreditBlacklist.setUpdatedAt(new Date());
		discreditBlacklist.setSource("信用中国（甘肃）");
		discreditBlacklist.setUrl(url);
		discreditBlacklist.setObjectType("01");
		discreditBlacklist.setEnterpriseName("");
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
