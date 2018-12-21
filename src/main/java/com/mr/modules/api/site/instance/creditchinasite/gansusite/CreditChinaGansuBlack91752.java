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
 * 信用中国（甘肃）-第三批“诚信平凉”黑榜名单
 * 
 * http://www.gscredit.gov.cn/blackList/91752.jhtml
 * 
 * 
 * @author pxu 2018年6月26日
 */
@Slf4j
@Component("creditchina-gansu-black-91752")
@Scope("prototype")
public class CreditChinaGansuBlack91752 extends SiteTaskExtend_CreditChina {
	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/91752.jhtml";

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
			String text = CrawlerUtil.replaceHtmlNbsp(content.text()).replace("　", " ").trim();// 替换&nbsp;和全角空格
			if (StrUtil.isEmpty(text)) {// 跳过空的div
				continue;
			}
			if (itemIndex == 0) {
				if (text.contains("一、市中级人民法院失信企业（5户）")) {
					itemIndex = 1;
					iCount = 0;
					subject = "市中级人民法院诚信黑榜";
					continue;
				}
				if (text.contains("二、人行平凉中心支行金融系统失信企业（3户）")) {
					itemIndex = 2;
					iCount = 0;
					subject = "人行平凉支行失信黑榜";
					continue;
				}
				if (text.contains("三、市住建局建筑失信企业（3户）")) {
					itemIndex = 3;
					iCount = 0;
					subject = "市住建局建筑失信";
					continue;
				}
				if (text.contains("四、市人社局支付农民工工资失信企业（4户）")) {
					itemIndex = 4;
					iCount = 0;
					subject = "市人社局支付工资失信";
					continue;
				}
				if (text.contains("五、市工商局失信企业（3户）")) {
					itemIndex = 5;
					iCount = 0;
					subject = "市工商局失信";
					continue;
				}
				if (text.contains("六、市食药监局食品药品失信企业（3户）")) {
					itemIndex = 6;
					iCount = 0;
					subject = "市食药监局失信";
					continue;
				}
				continue;
			}

			// 一、市中级人民法院失信企业（5户）
			if (itemIndex == 1) {
				if (text.contains("惩戒措施:")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", "").trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省平凉市中级人民法院");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1, text.indexOf("（")).trim());
					discreditBlacklist.setPersonName(text.substring(text.indexOf("（") + 1, text.indexOf("）")).replace("法定代表人：", "").trim());
					String punishResult = "依照《中华人民共和国民事诉讼法》第一百一十一条、第二百四十二条、第二百五十五条的规定，对企业及法定代表人采取以下惩戒措施：（1）查封、扣押企业抵押财产，冻结银行账户；（2）对企业的失信行为在征信系统记录，并纳入失信被执行人名单库；";
					if (discreditBlacklist.getPersonName().equals("陈双弟") || discreditBlacklist.getPersonName().equals("邹会岐")) {
						punishResult = punishResult + "（3）对法定代表人限制出境、限制高消费、纳入人民银行征信系统。";
					}
					discreditBlacklist.setPunishResult(punishResult);
				}
				continue;
			}
			// 二、人行平凉中心支行金融系统失信企业（3户）
			if (itemIndex == 2) {
				if (text.contains("惩戒措施：")) {// 处理结束,重新判断itemIndex
					itemIndex = 0;
					continue;
				}
				if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", "").trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				} else {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("人行平凉中心支行");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1, text.indexOf("（")).trim());
					discreditBlacklist.setPersonName(text.substring(text.indexOf("（") + 1, text.indexOf("）")).replace("法定代表人：", "").trim());
					String punishResult = "（1）违约记录记入全国金融信用信息基础数据库；（2）降低金融机构内部评级结果；（3）各金融机构终止授信，将企业及法定代表人列为信贷交易禁入客户。";
					discreditBlacklist.setPunishResult(punishResult);
				}
				continue;
			}
			// 三、市住建局建筑失信企业（3户）
			if (itemIndex == 3) {
				if (text.contains("惩戒措施：") && iCount == 3) {
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setJudgeAuth("甘肃省平凉市住建局");
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1, text.indexOf("（")).trim());
					discreditBlacklist.setPersonName(text.substring(text.indexOf("（") + 1, text.indexOf("）")).replace("法定代表人：", "").trim());
					if (iCount == 1) {
						String punishResult = "依照《建设工程质量管理条例》的规定，通过企业信用信息公示系统将该企业向社会公示。";
						discreditBlacklist.setPunishResult(punishResult);
					} else {
						String punishResult = "依照《建筑法》第六十四条之规定，责令停止施工，限期改正，并予以通报批评。";
						discreditBlacklist.setPunishResult(punishResult);
					}
				} else if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", "").trim());
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 四、市人社局支付农民工工资失信企业（4户）
			// 五、市工商局失信企业（3户）
			if (itemIndex == 4 || itemIndex == 5) {
				if (text.contains("事由：")) {
					itemIndex = 0;
					continue;
				}
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1, text.indexOf("（")).trim());
					discreditBlacklist.setPersonName(text.substring(text.indexOf("（") + 1, text.indexOf("）")).replace("法定代表人：", "").trim());
					if (itemIndex == 4) {
						discreditBlacklist.setJudgeAuth("甘肃省平凉市人社局");
						discreditBlacklist.setPunishReason("该企业存在拖欠农民工工资行为。在处理拖欠农民工工资问题中不及时，导致农民工群体性上访或多批次上访，造成不良影响。");
						discreditBlacklist.setPunishResult("依照《劳动保障监察条例》第二十二条和国务院办公厅《关于全面治理拖欠农民工工资问题的意见》（国办发〔2016〕1号）规定，对该企业采取以下惩戒措施：（1）通过企业信用信息公示系统向社会公布；（2）建议行业主管部门在工程招投标、生产许可、资质审核、融资贷款、市场准入等方面依法依规予以限制。");
					} else {
						discreditBlacklist.setJudgeAuth("甘肃省平凉市工商局");
						discreditBlacklist.setPunishReason("该企业连续三年未公示企业年报。");
						discreditBlacklist.setPunishResult("依照《企业信息公示暂行条例》第十七条、十八条之规定，对该户“黑榜”企业采取以下惩戒措施：（1）将其列入严重违法企业名单；（2）通过企业信用信息公示系统向社会公示；（3）企业的法定代表人3年内不得担任其他企业的法定代表人或负责人；（4）在政府采购、工程招投标、国有土地出让、授予荣誉称号等工作中，对被列入经营异常名录或者严重违法企业名单的企业依法予以限制或者禁入。");
					}
					discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
					saveDisneycreditBlackListOne(discreditBlacklist,false);
				}
				continue;
			}
			// 六、市食药监局食品药品失信企业（3户）
			if (itemIndex == 6) {
				if (text.contains("注：")) {// 全部处理结束
					itemIndex = 0;
					break;
				}
				if (Pattern.compile("\\d+[\\.].*").matcher(text).matches()) {
					iCount++;
					discreditBlacklist = createDefaultDiscreditBlacklist();
					discreditBlacklist.setSubject(subject);
					discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1, text.indexOf("（")).trim());
					discreditBlacklist.setPersonName(text.substring(text.indexOf("（") + 1, text.indexOf("）")).replace("法定代表人：", "").trim());
					discreditBlacklist.setJudgeAuth("甘肃省平凉市食药监局");
				} else if (text.contains("事由：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishReason(text.replace("事由：", ""));
				} else if (text.contains("惩戒措施：") && discreditBlacklist != null) {
					discreditBlacklist.setPunishResult(text.replace("惩戒措施：", ""));
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
		blackList.setPublishDate("2016/08/01");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
