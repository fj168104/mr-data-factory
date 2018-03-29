package com.mr.modules.api.site.instance;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by feng on 18-3-16
 * 保监会
 * 行政处罚决定
 */

@Slf4j
@Component("site3")
@Scope("prototype")
public class SiteTaskImpl_3 extends SiteTaskExtend {

	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	@Override
	protected String execute() throws Throwable {
		log.info("*******************call site3 task**************");

		//保监会处罚
		String targetUri1 = "http://www.circ.gov.cn/web/site0/tab5240/info4099702.htm";
		String fullTxt = getData(targetUri1);
		log.info(fullTxt);
		extract1(fullTxt);

		//保监局处罚
		String targetUri2 = "http://www.circ.gov.cn/web/site0/tab5241/info4102424.htm";
		fullTxt = getData(targetUri2);
		log.info(fullTxt);
		extract2(fullTxt);
		return null;
	}

	/**
	 * 保监会处罚 提取所需要的信息
	 * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
	 * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
	 */
	private void extract1(String fullTxt) {
		ArrayList<String> filterTags = Lists.newArrayList("<SPAN>",
				"</SPAN>",
				"<a>",
				"</a>",
				"&nbsp;",
				"　",
				" ",
				" ");

		//序号 TODO 需确认
		String seqNo = "";
		seqNo = "";

		//处罚文号
		String punishNo = "";
		punishNo = "保监罚〔2018〕10号";    //丛链接中提取


		boolean isPunishOn = false;
		int pCnt = 1;
		//机构当事人名称
		String orgPerson = "";

		//机构当事人住所
		String orgAddress = "";

		//机构负责人姓名
		String orgHolderName = "";


		//当事人集合
		boolean isAllPersonOn = true;
		String allPerson = "";

		//发布机构
		String releaseOrg = "";
		releaseOrg = "中国保监会";    //链接中提取

		//发布日期
		String releaseDate = "";
		releaseDate = "2018-02-23";    //链接中提取

		//行政处罚详情
		String punishDetail = "";
		boolean detailIsOn = false;
		ArrayList<String> details = Lists.newArrayList();

		//处罚机关	TODO 需确认
		String punishOrg = "";
		punishOrg = "中国保监会";

		//处罚日期
		String punishDate = "";

		Document doc = Jsoup.parse(fullTxt);
		Elements Ps = doc.getElementsByTag("P");
		for (Element pElement : Ps) {
			//orgPerson orgAddress orgHolderName 处理
			if (Strings.isNullOrEmpty(orgPerson) || isPunishOn) {
				String pString = filter(pElement.text().trim(), filterTags);
				if (pString.startsWith("当事人：")) {
					isPunishOn = !isPunishOn;
					isAllPersonOn = !isAllPersonOn; //首次关闭当事人集合提取, 机构信息提取完成后打开
				}
				if (isPunishOn) {
					if (pCnt == 1) {
						orgPerson = pString;
					}
					if (pCnt == 2) {
						orgAddress = pString;
					}
					if (pCnt == 3) {
						orgHolderName = pString;
					}
					pCnt++;
				}
			}

			//当事人集合
			if (isAllPersonOn && !Strings.isNullOrEmpty(orgPerson)) {
				String pString = filter(pElement.text().trim(), filterTags);
				if (pString.startsWith("依据《中华人民共和国保险法》")) {
					isAllPersonOn = false;
					detailIsOn = true;
				} else {
					allPerson += pString + System.getProperty("line.separator");
				}

			}

			//punishDetail 处理
			if (detailIsOn) {
				String pString = filter(pElement.text().trim(), filterTags);
				if (pString.startsWith("中国保监会")) {
					detailIsOn = false;
					punishDetail = details.toString().replace("[", "").replace("]", "");
				} else {
					details.add(pString);
				}
			}

			//punishDate 处理
			{
				String pString = extracterZH(pElement.text().trim());
				if ("年月日".equals(pString))
					punishDate = filter(pElement.text().trim(), filterTags);
			}
		}

		log.info("parse demo...\n" + punishDetail);
	}

	/**
	 * 保监局处罚 提取所需要的信息
	 * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
	 * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
	 */
	private void extract2(String fullTxt) {
		ArrayList<String> filterTags = Lists.newArrayList("<SPAN>",
				"</SPAN>",
				"<a>",
				"</a>",
				"&nbsp;",
				"　",
				" ",
				" ");

		//序号 TODO 需确认
		String seqNo = "";
		seqNo = "";

		//处罚文号
		String punishNo = "";
		punishNo = "〔2018〕12号";    //丛链接中提取


		boolean isPunishOn = false;
		int pCnt = 1;
		//机构当事人名称
		String orgPerson = "";

		//机构当事人住所
		String orgAddress = "";

		//机构负责人姓名
		String orgHolderName = "";


		//当事人集合
		boolean isAllPersonOn = true;
		String allPerson = "";

		//发布机构
		String releaseOrg = "";
		releaseOrg = "湖北保监局";    //链接中提取

		//发布日期
		String releaseDate = "";
		releaseDate = "2018-03-19";    //链接中提取

		//行政处罚详情
		String punishDetail = "";
		boolean detailIsOn = false;
		ArrayList<String> details = Lists.newArrayList();

		//处罚机关	TODO 需确认
		String punishOrg = "";
		punishOrg = "湖北保监局";

		//处罚日期
		String punishDate = "";
		boolean punishDateIsOn = false;

		/**
		 * 规则的当事人为公司的处理
		 */
		Document doc = Jsoup.parse(fullTxt);
		Elements Ps = doc.getElementsByTag("P");
		for (Element pElement : Ps) {
			//orgPerson orgAddress orgHolderName 处理
			if (Strings.isNullOrEmpty(orgPerson) || isPunishOn) {
				Element spanElement = pElement.getElementsByTag("SPAN").get(0);
				String pString = filter(spanElement.text().trim(), filterTags);
				if (pString.startsWith("当事人：")) {
					isPunishOn = !isPunishOn;
					isAllPersonOn = !isAllPersonOn; //首次关闭当事人集合提取, 机构信息提取完成后打开
				}
				if (isPunishOn) {
					if (pCnt == 1) {
						orgPerson = pString;
					}
					if (pCnt == 2) {
						orgAddress = pString;
					}
					if (pCnt == 3) {
						orgHolderName = pString;
					}
					pCnt++;
				}
			}

			//当事人集合
			if (isAllPersonOn && !Strings.isNullOrEmpty(orgPerson)) {
				Elements spanElements = pElement.getElementsByTag("SPAN");
				for (Element spanElement : spanElements) {
					String pString = filter(spanElement.text().trim(), filterTags);
					if (pString.startsWith("依据《中华人民共和国保险法》")) {
						isAllPersonOn = false;
						detailIsOn = true;
					}else {
						allPerson += pString;
					}
					allPerson += System.getProperty("line.separator");
				}



			}

			//punishDetail 处理
			if (detailIsOn) {
				Elements spanElements = pElement.getElementsByTag("SPAN");
				for (Element spanElement : spanElements) {
					String pString = filter(spanElement.text().trim(), filterTags);
					if (pString.length() < 10 && pString.endsWith("保监局")) {
						detailIsOn = false;
						punishDetail = details.toString().replace("[", "").replace("]", "");
						punishDateIsOn = true;
						break;
					} else {
						details.add(pString);
					}
				}
			}

			//punishDate 处理
			if (punishDateIsOn) {
				String tmp1 = "";
				Elements spanElements = pElement.getElementsByTag("SPAN");
				for (Element spanElement : spanElements) {
					tmp1 += filter(spanElement.text().trim(), filterTags);
				}
				if (extracterZH(tmp1).endsWith("年月日")){
					punishDate = tmp1.substring(tmp1.indexOf("年") - 4);
				}
			}
		}

		log.info("parse demo...\n" + punishDetail);
	}

}
