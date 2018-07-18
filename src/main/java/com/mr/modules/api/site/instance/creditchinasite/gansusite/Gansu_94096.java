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
 * 2.url:http://www.gscredit.gov.cn/blackList/94096.jhtml
 */
@Slf4j
@Component("gansu_94096")
@Scope("prototype")
public class Gansu_94096 extends SiteTaskExtend_CreditChina {
	String url = "http://www.gscredit.gov.cn/blackList/94096.jhtml";

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
				if (subject.contains("甘肃省工商局黑榜企业名单")) {
					subject = "省工商局黑榜企业名单";
					continue;
				}
				if (subject.contains("甘肃省质量技术监督局质量违法企业黑榜名单")) {
					subject = "市质监局质量违法企业名单";
					continue;
				}
				if (subject.contains("甘肃省食品药品监督管理局食品药品黑榜名单")) {
					subject = "省食药监管局诚信黑榜";
					continue;
				}
				subject = "";
				continue;
			}

			//省工商局黑榜企业名单
			if (subject.equals("省工商局黑榜企业名单")) {
				String text = elelementSpans.first().text();
				//省工商局黑榜企业名单 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}
				if(text.contains("投诉电话")) continue;
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text);
				String punishResult = "自上述企业被吊销之日起该企业法定代表人三年内不得担任企业法定代表人";
				discreditBlacklist.setPunishResult(punishResult);
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;
			}

			//市质监局质量违法企业名单
			if (subject.equals("市质监局质量违法企业名单")) {
				String text = elelementSpans.first().text();
				//市质监局质量违法企业名单 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					continue;
				}
				if(text.contains("投诉电话")) continue;
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);
				discreditBlacklist.setEnterpriseName(text);
				String punishResult = "(1)责令停止生产，并对违法行为进行整改。" +
						"(2)采取罚款、停业整顿、没收违法所得等行政处罚。" +
						"(3)根据违法事实和情节，触犯刑法的一律移送司法机关。" +
						"(4)不受理纳入黑榜名单的企业及其经营管理人员任何评先选优等表彰申请。";
				discreditBlacklist.setPunishResult(punishResult);
				saveDisneycreditBlackListOne(discreditBlacklist, false);
				continue;
			}

			//省食药监管局诚信黑榜
			if (subject.equals("省食药监管局诚信黑榜")) {
				String text = elelementSpans.first().text();
				//省食药监管局诚信黑榜 处理结束
				if (text.contains("惩戒措施：")) {
					subject = "";
					break;
				}
				if(text.contains("投诉电话")) continue;
				DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
				discreditBlacklist.setSubject(subject);

				discreditBlacklist.setEnterpriseName(text.substring(0, text.indexOf("   ")));
				discreditBlacklist.setPunishReason(text.substring(text.indexOf("   ") + 3));

				String punishResult = "(1)各级食品药品监督管理部门要加大黑榜信息的公开曝光力度。对辖区内列入食品药品黑榜企业和单位要在其政务网站和当地媒体公开曝光，并将黑榜名单向同级卫生、工商、金融等部门通报。" +
						"(2)各级食品药品监督管理部门要加大黑榜企业和单位的监管力度。把列入食品药品黑榜名单的生产经营企业和使用单位作为重点监管对象，通过增加监督检查和抽验频次等方式加强监管。列入食品药品黑榜名单的生产经营者、责任人员，再次发生违法违规行为的，依法从重处罚。" +
						"(3)充分依靠人民群众，营造食品药品安全社会共治格局。畅通12331食品药品安全投诉举报渠道，落实举报奖励制度，鼓励社会组织或个人对食品药品黑榜企业和单位进行监督，举报列入食品药品黑榜名单的生产经营者和责任人员的违法行为。";
				discreditBlacklist.setPunishResult(punishResult);
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
		discreditBlacklist.setPublishDate("2015/10/27");
		return discreditBlacklist;
	}

}
