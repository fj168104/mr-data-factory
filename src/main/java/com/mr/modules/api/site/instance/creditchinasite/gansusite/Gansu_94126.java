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
 * 2.url:http://www.gscredit.gov.cn/blackList/94126.jhtml
 */
@Slf4j
@Component("gansu_94126")
@Scope("prototype")
public class Gansu_94126 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94126.jhtml";

	@Autowired
	DiscreditBlacklistMapper discreditBlacklistMapper;

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

		String objectType = "";
		for (Element elementDiv : elementDivs) {

			Elements elelementSpans = elementDiv.getElementsByTag("span");
			if (CollectionUtil.isEmpty(elelementSpans)) continue;

			if (StrUtil.isEmpty(subject)) {
				subject = elelementSpans.first().text().replaceAll("\\s*", "");
				if (subject.contains("市中级人民法院失信被执行人名单")) {
					subject = "市中级人民法院失信被执行人";
					continue;
				}
				if (subject.contains("嘉峪关市食品药品监督管理局食品药品黑榜名单")) {
					subject = "市食药监管局诚信黑榜";
					continue;
				}

				subject = "";
				continue;
			}

			//市中级人民法院失信被执行人
			if (subject.equals("市中级人民法院失信被执行人")) {
				String text = elelementSpans.first().text();
				//市中级人民法院失信被执行人 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}

				if(text.contains("法人（")){
					objectType = "01";
					continue;
				}

				if(text.contains("自然人（")){
					objectType = "02";
					continue;
				}

				String punishResult = "1.曝光被执行人身份。通过广播、电视、互联网、报纸等各种媒介向社会公开不履行债务被执行人名单，将被执行人失信情况公之于众，形成舆论压力，减损其名誉；" +
						"2.进行信用惩戒。在政府采购、招标投标、行政审批、政府扶持、融资信贷、市场准入、资质认定等方面受到限制；" +
						"3.根据最高人民法院《关于限制被执行人高消费的若干规定》限制其不得有以下七种高消费：（1）乘坐交通工具时，选择飞机、列车软卧、轮船二等以上舱位；（2）在星级以上宾馆、酒店、夜总会、高尔夫球场等场所进行高消费；（3）购买不动产或者新建、扩建、高档装修房屋；（4）租赁高档写字楼、宾馆、公寓等场所办公；（5）购买非经营必需车辆；（6）旅游、度假；（7）子女就读高收费私立学校；（8）支付高额保费购买保险理财产品；（9）其他非生活和工作必需的高消费行为。";
				if(objectType.equals("01")){
					DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setObjectType("01");
					discreditBlacklist.setEnterpriseName(text);
					discreditBlacklist.setPunishResult(punishResult);
					discreditBlacklistMapper.insert(discreditBlacklist);
					continue;
				}
				if(objectType.equals("02")){
					DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setObjectType("02");
					discreditBlacklist.setPersonName(text.substring(0, text.indexOf("：")));
					discreditBlacklist.setPersonId(text.substring(text.indexOf("：") + 1));
					discreditBlacklist.setPunishResult(punishResult);
					discreditBlacklistMapper.insert(discreditBlacklist);
					continue;
				}

			}

			//市食药监管局诚信黑榜
			if (subject.equals("市食药监管局诚信黑榜")) {
				String text = elelementSpans.first().text();
				//市食药监管局诚信黑榜 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					break;
				}

				String punishResult = "1.加大黑榜信息公开的曝光力度。对列入食品药品黑榜名单的企业和单位要在市政务网站和市属各级各类新闻媒体公开曝光，黑榜名单同时向市卫生局、市工商局、市金融行业等单位通报。" +
						"2.加强黑榜企业和单位的监管力度。被列入食品药品黑榜名单的生产经营企业和单位作为重点监管对象，通过增加监督检查和抽验频次等方式加强监管。被列入食品药品黑榜名单的生产经营者、责任人员，再次发生违法违规行为的，依法从重处罚。" +
						"3.充分依靠人民群众，营造食品药品安全社会共治格局。畅通12331食品药品安全投诉举报渠道，落实举报奖励制度，鼓励社会组织或市民群众对食品药品黑榜企业和单位进行监督，举报列入食品药品黑榜名单的生产经营者和责任人员的违法行为。";

				if(text.contains("（")){
					DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setObjectType("02");
					discreditBlacklist.setPersonName(text.substring(0 , text.indexOf("（")));
					discreditBlacklist.setPunishResult(punishResult);
					discreditBlacklistMapper.insert(discreditBlacklist);
					continue;
				}else {
					DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setObjectType("01");
					discreditBlacklist.setEnterpriseName(text);
					discreditBlacklist.setPunishResult(punishResult);
					discreditBlacklistMapper.insert(discreditBlacklist);
					continue;
				}
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
