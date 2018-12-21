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
 * 信用中国（甘肃）-庆阳市2017年第一期诚信“黑榜”
 * 
 * http://www.gscredit.gov.cn/blackList/91744.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-black-91744")
@Scope("prototype")
public class CreditChinaGansuBlack91744 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/91744.jhtml";

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
				if (text.contains("一、不履行法院裁判的单位、企业和个人“黑榜”名单")) {
					itemIndex = 1;
					iCount = 0;
					subject = "不履行法院裁判";
					continue;
				}
				if (text.contains("二、企业环境保护“黑榜”名单")) {
					itemIndex = 2;
					iCount = 0;
					subject = "企业环境保护黑榜";
					continue;
				}
				if (text.contains("三、企业用工薪酬“黑榜”名单")) {
					itemIndex = 3;
					iCount = 0;
					subject = "企业用工薪酬黑榜";
					continue;
				}
				if (text.contains("四、市场经营主体“黑榜”名单")) {
					itemIndex = 4;
					iCount = 0;
					subject = "市场经营主体黑榜";
					continue;
				}
				if (text.contains("五、道路运输行业“黑榜”名单")) {
					itemIndex = 5;
					iCount = 0;
					subject = "道路运输行业黑榜";
					continue;
				}
				if (text.contains("六、银行业金融机构违约失信企业和个人“黑榜”名单")) {
					itemIndex = 6;
					iCount = 0;
					subject = "银行业金融机构违约失信";
					continue;
				}
				continue;
			}

			// 一、不履行法院裁判的单位、企业和个人“黑榜”名单（市中级人民法院发布）
			if (itemIndex == 1) {
				if (text.contains("惩戒措施：")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[、].*").matcher(text).matches()) {// 数据行
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省庆阳市中级人民法院");
					if (text.contains("法定代表人") || text.contains("负责人")) {// 企业
						discreditBlacklist.setObjectType("01");
						String[] items = text.replace(",", "，").split("，");
						discreditBlacklist.setEnterpriseName(items[0].substring(items[0].indexOf("、") + 1).trim());// 企业名称
						discreditBlacklist.setPersonName(items[1].replace("法定代表人：", "").replace("负责人：", "").trim());// 法定代表人/负责人
						discreditBlacklist.setPersonId(items[2].replace("身份证号码：", "").trim());
					} else if (text.contains("身份证号码")) {// 个人
						discreditBlacklist.setObjectType("02");
						String[] items = text.replace(",", "，").replace("；", "，").split("，");
						discreditBlacklist.setPersonName(items[0].substring(items[0].indexOf("、") + 1).trim());
						discreditBlacklist.setPersonId(items[1].replace("身份证号码：", "").trim());
					} else {// 单位
						discreditBlacklist.setObjectType("01");
						discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("、") + 1).trim());// 企业名称
					}
					discreditBlacklist.setPunishReason("不履行法院裁决");
					String punishResult = "（一）对于自然人、企业及其法定代表人、主要负责人、实际控制人不得有以下高消费及非生活和工作必需的消费行为：（1）乘坐交通工具时，选择飞机、列车软卧、轮船二等以上舱位；（2）在星级以上宾馆、酒店、夜总会、高尔夫球场等场所进行高消费；（3）购买不动产或者新建、扩建、高档装修房屋；（4）租赁高档写字楼、宾馆、公寓等场所办公；（5）购买非经营必需车辆；（6）旅游、度假；（7）子女就读高收费私立学校；（8）支付高额保费以及购买保险理财产品；（9）乘坐G字头动车组列车全部座位、其他动车组列车一等以上座位等其他非生活和工作必需的消费行为。 "//
							+ "（二）对于自然人、单位及其法定代表人的失信行为，作为不良记录推送到人民银行征信系统、工商信用系统以及发改、财政、税务、国土、房产、海关等部门，联合实施信用惩戒。包括限制其在金融机构贷款和办理信用卡，限制其开办新公司和投资入股，限制参与政府采购、招标投标，限制行政审批、政府扶持、市场准入、资质认证，限制在国土、房地产管理部门办理产权转移、权属变更等。 "//
							+ "（三）对违反禁令高消费、有能力履行而拒不履行生效裁判的失信被执行人，人民法院将依法对单位处以100万元以下、对个人10万元以下罚款，对法定代表人和实际控制人采取司法拘留强制措施。情节严重构成犯罪的，将依法追究刑事责任。 ";
					discreditBlacklist.setPunishResult(punishResult);
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 二、企业环境保护“黑榜”名单（市环境保护局发布）
			// 三、企业用工薪酬“黑榜”名单（市人力资源和社会保障局发布）
			// 四、市场经营主体“黑榜”名单（市工商行政管理局发布）
			// 五、道路运输行业“黑榜”名单（市道路运输管理局发布）
			if (itemIndex == 2 || itemIndex == 3 || itemIndex == 4 || itemIndex == 5) {
				if (text.contains("惩戒措施：")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[、].*").matcher(text).matches()) {// 数据行
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf("、") + 1).trim());// 企业名称
					if (itemIndex == 2) {
						discreditBlacklist.setJudgeAuth("甘肃省庆阳市环境保护局");
						String punishResult = "（一）责令向社会公布该企业改善环境行为的计划或承诺，并向所在县（区）书面报告整改情况； "//
								+ "（二）所在县（区）在该企业整改未落实之前，不予受理、整改项目以外的环境行政许可申请。 ";
						discreditBlacklist.setPunishResult(punishResult);
					} else if (itemIndex == 3) {
						discreditBlacklist.setJudgeAuth("甘肃省庆阳市人力资源和社会保障局");
						String punishResult = "全市公开曝光，并在全市政府采购、工程招投标、国有土地转让、荣誉称号授予等工作中，依法予以限制或禁止。 ";
						discreditBlacklist.setPunishResult(punishResult);
					} else if (itemIndex == 4) {
						discreditBlacklist.setJudgeAuth("甘肃省庆阳市工商行政管理局");
						String punishResult = "（一）列为重点监督管理对象，企业的法定代表人、负责人，3年之内不得担任其他企业的法定代表人、负责人；"//
								+ "（二）工商部门不予通过“守合同重信用”、“诚信企业”等公示活动申报资格审核；"//
								+ "（三）在政府采购、工程招投标、国有土地出让、授予荣誉称号等方面依法予以限制或者禁入。";
						discreditBlacklist.setPunishResult(punishResult);
					} else {
						discreditBlacklist.setJudgeAuth("甘肃省庆阳市道路运输管理局");
						String punishResult = "（一）抄告涉货物资单位，上述列入“黑榜”的此类企业不得参加货物运输招投标业务；"//
								+ "（二）责令以上企业限期整改，整改期间不得增加和拓展新业务；"//
								+ "（三）通过整改，仍然不达标的企业吊销经营许可。";
						discreditBlacklist.setPunishResult(punishResult);
					}
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 六、银行业金融机构违约失信企业和个人“黑榜”名单（市银行业协会发布）
			if (itemIndex == 6) {
				if (text.contains("惩戒措施：")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[(\\s)?、].*").matcher(text).matches()) {// 数据行
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					if (text.contains("法人代表")) {// 企业
						discreditBlacklist.setObjectType("01");
						String[] items = text.replace(",", "，").split("，");
						discreditBlacklist.setEnterpriseName(items[0].substring(items[0].indexOf("、") + 1).trim());// 企业名称
						discreditBlacklist.setPersonName(items[1].replace("法人代表：", "").trim());
						String personId = items[2].replace("身份证号码：", "").trim();
						discreditBlacklist.setPersonId(personId.length()>18?personId.substring(0,18):personId);
					} else {// 个人
						discreditBlacklist.setObjectType("02");
						String[] items = text.replace(",", "，").split("，");
						discreditBlacklist.setPersonName(items[0].substring(items[0].indexOf("、") + 1).trim());
						String personId = items[1].replace("身份证号码：", "").trim();
						discreditBlacklist.setPersonId(personId.length()>18?personId.substring(0,18):personId);
					}
					discreditBlacklist.setJudgeAuth("甘肃省庆阳市银行业协会");
					String punishResult = "（一）全市公开曝光。 "//
							+ "（二）对在“黑榜”中公布的失信自然人、企业法定代表人、主要负责人不得有以下高消费及非生活和工作必需的消费行为：（1）乘坐交通工具时，选择飞机、列车软卧、轮船二等以上舱位；（2）在星级以上宾馆、酒店、夜总会、高尔夫球场等场所进行高消费；（3）购买不动产或者新建、扩建、高档装修房屋；（4）租赁高档写字楼、宾馆、公寓等场所办公；（5）购买非经营必需车辆；（6）旅游、度假；（7）子女就读高收费私立学校；（8）支付高额保费及购买保险理财产品；（9）乘坐G字头动车组列车全部座位、其他动车组列车一等以上座位等其他非生活和工作必需的消费行为。 "//
							+ "（三）对列入“黑榜”名单中的企业和个人，取消其参与市政项目、工程招投标资格。 "//
							+ "（四）对列入“黑榜”名单中的企业和个人，全市银行业金融机构限制其办理开户许可、融资授信、信用卡发卡等业务。";
					discreditBlacklist.setPunishResult(punishResult);
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
		blackList.setPublishDate("2017/09/04");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
