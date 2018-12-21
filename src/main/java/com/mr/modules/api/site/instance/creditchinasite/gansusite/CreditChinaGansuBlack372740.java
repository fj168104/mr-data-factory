package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mr.common.util.CrawlerUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（甘肃）-兰州市2018年第一季度诚信“红黑榜”发布---黑榜
 * 
 * http://www.gscredit.gov.cn/blackList/372740.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-black-372740")
@Scope("prototype")
public class CreditChinaGansuBlack372740 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/372740.jhtml";

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
		Elements contents = doc.getElementsByClass("artical_content_wrap").first().getElementsByTag("span");

		DiscreditBlacklist discreditBlacklist = null;
		int itemIndex = 0;// 条目索引
		String subject = "";// 主题
		String judgeAuth = "";// 发布机关
		int iCount = 0;// 计数器
		for (Element content : contents) {
			String text = CrawlerUtil.replaceHtmlNbsp(content.text()).replace("　", " ").trim();// 替换&nbsp;和全角空格
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (itemIndex == 0) {
				if (text.contains("一、兰州市中级人民法院发布19家失信被执行人（法人）")) {
					itemIndex = 1;
					iCount = 0;
					subject = "失信被执行人名单";
					judgeAuth = "甘肃省兰州市中级人民法院";
					continue;
				}
				if (text.contains("二、兰州市交通运输委员会发布1家履约失信企业和1名失信自然人")) {
					itemIndex = 2;
					iCount = 0;
					subject = "交通运输委员会黑名单";
					judgeAuth = "甘肃省兰州市交通运输委员会";
					continue;
				}
				if (text.contains("三、兰州市质量技术监督局发布2家计量监督失信单位")) {
					itemIndex = 3;
					iCount = 0;
					subject = "质量技术监督局失信企业";
					judgeAuth = "甘肃省兰州市质量技术监督局";
					continue;
				}
				if (text.contains("四、兰州市地方税务局发布6家纳税失信企业和1名失信自然人")) {
					itemIndex = 4;
					iCount = 0;
					subject = "地方税务局失信";
					judgeAuth = "甘肃省兰州市地方税务局";
					continue;
				}
				continue;
			}
			// 一、兰州市中级人民法院发布19家失信被执行人（法人）
			if (itemIndex == 1) {
				if (text.contains("惩戒措施：")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				iCount++;
				discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setJudgeNo(text.substring(0, text.indexOf("号") + 1));
				discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("号") + 1, text.indexOf("公司") + 2));
				discreditBlacklist.setJudgeAuth(text.substring(text.indexOf("兰州市"), text.indexOf("法院") + 2));
				StringBuilder punishResult = new StringBuilder();
				punishResult.append("（一）根据最高人民法院《关于限制被执行人高消费及有关消费的若干规定》，纳入失信被执行人名单的被执行人，将对其采取限制消费措施。被执行人为单位的，被执行人及其法定代表人、主要负责人、影响债务履行的直接责任人员、实际控制人不得实施以下行为：");
				punishResult.append("1.乘坐交通工具时，选择飞机、列车软卧、轮船二等以上舱位；");
				punishResult.append("2.在星级以上宾馆、酒店、夜总会、高尔夫球场等场所进行高消费；");
				punishResult.append("3.购买不动产或者新建、扩建、高档装修房屋；");
				punishResult.append("4.租赁高档写字楼、宾馆、公寓等场所办公；");
				punishResult.append("5.购买非经营必需车辆；");
				punishResult.append("6.旅游、度假；");
				punishResult.append("7.子女就读高收费私立学校；");
				punishResult.append("8.支付高额保费购买保险理财产品；");
				punishResult.append("9.乘坐G字头动车组列车全部座位、其他动车组列车一等以上座位等其他非生活和工作必须的消费行为。");
				punishResult.append("（二）被执行人违反限制高消费令进行消费的行为属于拒不履行人民法院已经发生法律效力的判决、裁定的行为，经查证属实的，依照《中华人民共和国民事诉讼法》第一百一十一条的规定，予以拘留、罚款；情节严重，构成犯罪的，追究其刑事责任。");
				discreditBlacklist.setPunishResult(punishResult.toString());
				discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
				saveDisneycreditBlackListOne(discreditBlacklist,false);
				discreditBlacklist = null;
				continue;
			}
			// 二、兰州市交通运输委员会发布1家履约失信企业和1名失信自然人
			if (itemIndex == 2) {
				if (text.contains("（一）甘肃路通工程监理有限责任公司")) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth(judgeAuth);
					discreditBlacklist.setEnterpriseName("甘肃路通工程监理有限责任公司");
					String discreditAction = "1.作为榆中小双公路监理单位，其总监理工程师一直未到场履约，且发生人员多次变更，没有建设单位批复变更。专业监理工程师无专监证，到场监理人员职称不符合合同承诺，且重新更换的监理人员仍不能提供有效的职称证书和其他证件。"//
							+ "2.开工至今没有监理抽检资料、试验资料、旁站记录，监理日志、监理指令、原始数据记录等极不规范。"//
							+ "3.监理工地实验室至今未建立验收备案，不能开展试验检测工作，监理工地实验室只挂牌，实验室没有一件检测设备，对工程质量管理失控。";
					discreditBlacklist.setDiscreditAction(discreditAction);
					String punishResult = "1.对榆中小双公路监理单位甘肃路通工程监理有限责任公司全市通报批评。"//
							+ "2.建设单位榆中县公路局依据《X324线白榆公路双店子至小康营段维修改造工程路项目管理办法》对监理单位甘肃路通工程监理有限责任公司进行经济处罚。"//
							+ "3.将监理单位甘肃路通工程监理有限责任公司的失信行为记录在案，纳入兰州市公路建设市场参建主体信用评价系统，并将评价结果报送省交通工程质量安全监督管理局，进行全省公布。"//
							+ "4.责令监理单位在3天时间内按投标承诺的总监履约到场，监理人员满足本工程监理工作需求，建立监理工地实验室，完善监理管理体系。";
					discreditBlacklist.setPunishResult(punishResult);
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
					discreditBlacklist = null;
				}
				if (text.contains("（二）自然人：曹铁军（从业资格证号：6201211980********）")) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth(judgeAuth);
					discreditBlacklist.setObjectType("02");
					discreditBlacklist.setPersonName("曹铁军");
					discreditBlacklist.setPersonId("6201211980********");
					String discreditAction = "出租汽车司机，醉酒驾驶";
					discreditBlacklist.setDiscreditAction(discreditAction);
					discreditBlacklist.setPunishResult("在兰州市出租汽车驾驶员信息库中将驾驶员曹铁军记入不良行为“黑名单”，终身禁止驾驶出租汽车；吊销驾驶员曹铁军《从业资格证》。");
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
					discreditBlacklist = null;
					itemIndex = 0;
				}
				continue;
			}
			// 三、兰州市质量技术监督局发布2家计量监督失信单位
			if (itemIndex == 3) {
				if (text.contains("（")) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth(judgeAuth);
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("）") + 1, text.lastIndexOf("（")).trim());
					continue;
				}
				if (text.contains("失信行为：") && discreditBlacklist != null) {
					discreditBlacklist.setDiscreditAction(text.replace("失信行为：", "").trim());
					continue;
				}
				if (text.contains("惩戒措施：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishResult(text.replace("惩戒措施：", "").trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
					discreditBlacklist = null;
				}
				if (iCount == 2) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
				}
				continue;
			}
			// 四、兰州市地方税务局发布6家纳税失信企业和1名失信自然人
			if (itemIndex == 4) {
				if (text.contains("失信行为：") && discreditBlacklist != null) {
					discreditBlacklist.setDiscreditAction(text.replace("失信行为：", "").trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
					discreditBlacklist = null;
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth(judgeAuth);
					if (text.contains("身份证号：")) {
						discreditBlacklist.setObjectType("02");
						discreditBlacklist.setPersonName("李国良");
						discreditBlacklist.setPersonId("62242519650********");
					} else {
						discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("）") + 1).trim());
					}
					String punishResult = "1.纳税信用级别直接判为D级，适用相应的D级纳税人管理措施。"//
							+ "2.对单位及其法定代表人、主要负责人、实际控制人的失信行为，作为不良记录推送到参与实施联合惩戒的相关部门，共同进行联合惩戒。";
					discreditBlacklist.setPunishResult(punishResult);
					continue;
				}
				if (iCount == 7) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
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
		blackList.setPublishDate("2018/04/11");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
