package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import com.mr.framework.core.collection.CollectionUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.colligationsite.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther 1.信用中国（甘肃）
 * 2.url:http://www.gscredit.gov.cn/blackList/94107.jhtml
 */
@Slf4j
@Component("gansu_94107")
@Scope("prototype")
public class Gansu_94107 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94107.jhtml";

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
				if (subject.contains("一、市食药监局发布32家食药经营失信企业")) {
					subject = "市食药监局经营失信企业";
					continue;
				}

				subject = elelementSpans.first().text().replaceAll("\\s*", "");
				if (subject.contains("四、市建设局发布4家建设工程及参建单位")) {
					subject = "市建设局建设单位";
					continue;
				}

				subject = "";
				continue;
			}

			//市食药监局经营失信企业
			if (subject.equals("市食药监局经营失信企业")) {
				String text = elelementSpans.first().text();
				//市中级人民法院失信被执行人 处理结束
				if (text.contains("二、市质监局发布2家质量违法企业")) {
					subject = "市质监局质量违法企业名单";
					continue;
				}
				if (text.trim().startsWith("企业名称")) {
					continue;
				}
				String[] blackInfos = text.replace("   ","  ").split("  ");
				if (blackInfos.length < 4) {
					continue;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(blackInfos[0]);
				discreditBlacklist.setPunishReason(blackInfos[2]);
				discreditBlacklist.setPunishResult(blackInfos[3]);
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;

			}

			//市质监局质量违法企业名单
			if (subject.equals("市质监局质量违法企业名单")) {
				String text = elelementSpans.first().text();
				//市质监局质量违法企业名单 处理结束
				if (text.contains("三、市国税局发布3家失信企业")) {
					subject = "市国税局失信企业";
					continue;
				}
				if (text.trim().startsWith("企业名称")) {
					continue;
				}
				List<String> blackList = new ArrayList<String>();
				String[] blackInfos = text.split(" ");
				for(String blackInfo : blackInfos){
					if(StrUtil.isNotBlank(blackInfo)){
						blackList.add(blackInfo);
					}
				}
				if (blackList.size() < 7) {
					continue;
				}

				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(blackList.get(0));
				discreditBlacklist.setPersonName(blackList.get(1));
				discreditBlacklist.setJudgeAuth(blackList.get(4));
				discreditBlacklist.setPublishDate(blackList.get(5));
				discreditBlacklist.setPunishReason(blackList.get(3));
				discreditBlacklist.setPunishResult(blackList.get(6));
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;

			}


			//市国税局失信企业
			if (subject.equals("市国税局失信企业")) {
				String text = elelementSpans.first().text();
				//市国税局失信企业 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}

				if (text.contains("上榜原因：")) {
					dcbl.setPunishReason(text.replace("上榜原因：", ""));
					saveDisneycreditBlackListOne(dcbl, false);
					continue;
				}

				String punishResult = "1.依法追缴纳税人不进行纳税申报，不缴或少缴的税款。" +
						"2.依法对纳税人处以欠缴税款百分之五十以上五倍以下的罚款;构成犯罪的，依法追求刑事责任。" +
						"3.从纳税人滞纳税款之日起，依法按日加收滞纳税款万分之五的滞纳金。" +
						"4.继续加大黑榜纳税人信息曝光力度，对黑榜纳税人日常经营活动重点监控，进一步完善奖励诚信、约束失信的制度体系。";
				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject(subject);
				dcbl.setEnterpriseName(text.substring(text.indexOf(".") + 1));
				dcbl.setPunishResult(punishResult);
				continue;
			}

			//市建设局建设单位
			if (subject.equals("市建设局建设单位")) {

				String text = elelementSpans.first().text();
				//市建设局建设单位 处理结束
				if (text.contains("发布红黑榜单位投诉举报电话")) {
					subject = "";
					break;
				}
				//建设单位：兰州高科新元房地产开发有限公司施工单位：重庆荣信建设工程有限公司兰州鑫源通有限公司监理单位：兰州黄河工程监理有限责任公司
				//上榜原因：现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致4月6日、5月31日，连续发生安全事故，造成2人死亡。惩戒措施：根据事故调查组的调查结论，分别对建设单位、施工单位、监理单位依据相关法律法规的规定给予行政处罚。

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("兰州高科新元房地产开发有限公司");
				dcbl.setPunishReason("现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致4月6日、5月31日，连续发生安全事故，造成2人死亡。");
				dcbl.setPunishResult("根据事故调查组的调查结论，分别对建设单位、施工单位、监理单位依据相关法律法规的规定给予行政处罚。");
				saveDisneycreditBlackListOne(dcbl, false);

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("兰重庆荣信建设工程有限公司兰州鑫源通有限公司");
				dcbl.setPunishReason("现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致4月6日、5月31日，连续发生安全事故，造成2人死亡。");
				dcbl.setPunishResult("根据事故调查组的调查结论，分别对建设单位、施工单位、监理单位依据相关法律法规的规定给予行政处罚。");
				saveDisneycreditBlackListOne(dcbl, false);


				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("兰州黄河工程监理有限责任公司");
				dcbl.setPunishReason("现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致4月6日、5月31日，连续发生安全事故，造成2人死亡。");
				dcbl.setPunishResult("根据事故调查组的调查结论，分别对建设单位、施工单位、监理单位依据相关法律法规的规定给予行政处罚。");
				saveDisneycreditBlackListOne(dcbl, false);


				//建设单位：兰州国资投资(控投)建设集团有限公司施工单位：中国葛洲坝集团第二工程有限公司,监理单位：甘肃方圆工程监理有限公司
				//上榜原因：现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致5月10日发生安全事故，造成1人死亡。惩戒措施：根据事故调查组的调查结论，分别对建设单位、施工单位、监理单位依据相关法律法规的规定给予行政处罚。
				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("兰州国资投资(控投)建设集团有限公司");
				dcbl.setPunishReason("现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致5月10日发生安全事故，造成1人死亡。");
				dcbl.setPunishResult("根据事故调查组的调查结论，分别对建设单位、施工单位、监理单位依据相关法律法规的规定给予行政处罚。");
				saveDisneycreditBlackListOne(dcbl, false);

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("中国葛洲坝集团第二工程有限公司");
				dcbl.setPunishReason("现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致5月10日发生安全事故，造成1人死亡。");
				dcbl.setPunishResult("根据事故调查组的调查结论，分别对建设单位、施工单位、监理单位依据相关法律法规的规定给予行政处罚。");
				saveDisneycreditBlackListOne(dcbl, false);

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("甘肃方圆工程监理有限公司");
				dcbl.setPunishReason("现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致5月10日发生安全事故，造成1人死亡。");
				dcbl.setPunishResult("根据事故调查组的调查结论，分别对建设单位、施工单位、监理单位依据相关法律法规的规定给予行政处罚。");
				saveDisneycreditBlackListOne(dcbl, false);

				//建设单位：安宁区安宁堡街道办事处施工单位：兰州市安宁区第二建筑公司监理单位：甘肃建德工程监理有限公司
				//上榜原因：现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致5月24日发生安全事故，造成1人死亡。惩戒措施：正在进行事故调查，调查结束后，依据相关法律法规的规定对事故责任单位、责任人员进行处理。
				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("安宁区安宁堡街道办事处");
				dcbl.setPunishReason("现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致5月24日发生安全事故，造成1人死亡。");
				dcbl.setPunishResult("正在进行事故调查，调查结束后，依据相关法律法规的规定对事故责任单位、责任人员进行处理。");
				saveDisneycreditBlackListOne(dcbl, false);

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("兰州市安宁区第二建筑公司");
				dcbl.setPunishReason("现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致5月24日发生安全事故，造成1人死亡。");
				dcbl.setPunishResult("正在进行事故调查，调查结束后，依据相关法律法规的规定对事故责任单位、责任人员进行处理。");
				saveDisneycreditBlackListOne(dcbl, false);

				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("甘肃建德工程监理有限公司");
				dcbl.setPunishReason("现场管理混乱，安全防护措施不到位，安全制度不落实，安全生产体系不健全，导致5月24日发生安全事故，造成1人死亡。");
				dcbl.setPunishResult("正在进行事故调查，调查结束后，依据相关法律法规的规定对事故责任单位、责任人员进行处理。");
				saveDisneycreditBlackListOne(dcbl, false);
				//施工单位：甘肃第四建设集团有限责任公司
				//上榜原因：在2015年4月组织开展的工程质量治理两年行动及建筑施工安全检查中发现存在问题：中标项目经理杨普荣不在岗，未履行建造师义务。现场项目经理刘占军超出二级注册建筑师执业范围从事执业活动。且在之后的多次现场复查中，问题一直未整改落实到位。
				//惩戒措施：依据相关法律法规的规定对责任单位、责任人员进行处理。
				dcbl = createDefaultDiscreditBlacklist();
				dcbl.setSubject("市建设局建设单位");
				dcbl.setEnterpriseName("甘肃第四建设集团有限责任公司");
				dcbl.setPunishReason("在2015年4月组织开展的工程质量治理两年行动及建筑施工安全检查中发现存在问题：中标项目经理杨普荣不在岗，未履行建造师义务。现场项目经理刘占军超出二级注册建筑师执业范围从事执业活动。且在之后的多次现场复查中，问题一直未整改落实到位。");
				dcbl.setPunishResult("依据相关法律法规的规定对责任单位、责任人员进行处理。");
				saveDisneycreditBlackListOne(dcbl, false);
			}
		}
		//兰州丰泽强生医药连锁有限责任公司健民药店   药品零售   销售假药“瑞立停”等，案件已移交公安。行政处罚
		dcbl = createDefaultDiscreditBlacklist();
		dcbl.setSubject("市质监局质量违法企业名单");
		dcbl.setEnterpriseName("兰州丰泽强生医药连锁有限责任公司健民药店");
		dcbl.setPunishReason("销售假药“瑞立停”等，案件已移交公安");
		dcbl.setPunishResult("行政处罚");
		saveDisneycreditBlackListOne(dcbl, false);
	}

	protected boolean saveDisneycreditBlackListOne(DiscreditBlacklist discreditBlacklist, Boolean isForce) {
		try{
			discreditBlacklist.setUniqueKey(MD5Util.encode(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth()));
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
		discreditBlacklist.setPublishDate("2015/10/26");
		return discreditBlacklist;
	}

}
