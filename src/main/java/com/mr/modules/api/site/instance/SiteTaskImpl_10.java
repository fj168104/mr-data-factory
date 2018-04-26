package com.mr.modules.api.site.instance;

import com.google.common.collect.Lists;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Struct;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by feng on 18-3-16
 * 证监会
 */

@Slf4j
@Component("site10")
@Scope("prototype")
public class SiteTaskImpl_10 extends SiteTaskExtend {

	protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);

	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	@Override
	protected String execute() throws Throwable {
		log.info("*******************call site10 task**************");
		int pageCount = 0;
		String targetUri = "http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/index.html";
		String fulltxt = getData(targetUri);
		if (fulltxt.contains("var countPage =") && fulltxt.indexOf("var countPage =") < fulltxt.indexOf("//共多少页")) {
			pageCount = Integer.parseInt(fulltxt.substring(
					fulltxt.indexOf("var countPage =") + "var countPage =".length(),
					fulltxt.indexOf("//共多少页")).trim());

		} else {
			log.error("page parse error.");
		}

		log.info("pageCount:" + pageCount);
		List<FinanceMonitorPunish> listResults = extract(pageCount);
		if (!CollectionUtils.isEmpty(listResults)) {
			exportToXls("site10.xlsx", listResults);
		}

		return null;
	}

	@Override
	protected String executeOne() throws Throwable {
		log.info("*******************call site10 task for One Record**************");

		Assert.notNull(oneFinanceMonitorPunish.getPunishTitle());
		String title = oneFinanceMonitorPunish.getPunishTitle();
		String 关于对 = null;
		if (title.contains("关于对")) {
			关于对 = "关于对";
		} else if (title.contains("关于与对")) {
			关于对 = "关于与对";
		}

		String 采取 = null;
		采取 = "采取";

		String punishObj = null;
		if (StrUtil.isNotEmpty(关于对) && StrUtil.isNotEmpty(采取)) {
			punishObj = title.substring(title.indexOf(关于对) + 3, title.lastIndexOf(采取));
		}
		if (StrUtil.isNotEmpty(punishObj) && (punishObj.contains("公司") || punishObj.contains("事务所"))) {
			oneFinanceMonitorPunish.setPartyInstitution(punishObj);
		} else {
			oneFinanceMonitorPunish.setPartyPerson(punishObj);
		}
		Assert.notNull(oneFinanceMonitorPunish.getPublishDate());
		Assert.notNull(oneFinanceMonitorPunish.getUrl());
		oneFinanceMonitorPunish.setSource("证监会");
		initDate();
		doFetch(oneFinanceMonitorPunish, true);
		return null;
	}


	/**
	 * 提取所需要的信息
	 * 标题、对象、处罚机构、处罚时间、处罚事由、违反条例、处罚结果、整改时限
	 */
	private List<FinanceMonitorPunish> extract(int pageCount) throws Exception {

		List<FinanceMonitorPunish> lists = Lists.newLinkedList();
		for (int pageNo = 1; pageNo <= pageCount; pageNo++) {
			String url = String.format("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/index_%d.html",
					pageNo - 1);
			if (pageNo == 1)
				url = "http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/index.html";

			String recordPageTxt = getData(url);
			Document doc = Jsoup.parse(recordPageTxt);
			Element divElement = doc.getElementById("documentContainer");
			for (Element liElement : divElement.getElementsByTag("li")) {
				String title = "";    //标题
				String punishObj = "";    //对象
				String punishDate = "";    //处罚时间
				String href = "";

				Element aElement = liElement.getElementsByTag("a").get(0);
				title = aElement.attr("title");

				FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
				String 关于对 = null;
				if (title.contains("关于对")) {
					关于对 = "关于对";
				} else if (title.contains("关于与对")) {
					关于对 = "关于与对";
				}

				String 采取 = null;
				采取 = "采取";

				if (StrUtil.isNotEmpty(关于对) && StrUtil.isNotEmpty(采取)) {
					punishObj = title.substring(title.indexOf(关于对) + 3, title.lastIndexOf(采取));
				}
				punishDate = liElement.getElementsByTag("span").get(0).text();

				href = "http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/"
						+ aElement.attr("href").substring(2);
				if (!href.endsWith(".pdf")) {
					log.warn("href is not pdf");
					continue;
				}
				log.info(href);

				financeMonitorPunish.setPunishTitle(title);
				if (StrUtil.isNotEmpty(punishObj) && (punishObj.contains("公司") || punishObj.contains("事务所"))) {
					financeMonitorPunish.setPartyInstitution(punishObj);
				} else {
					financeMonitorPunish.setPartyPerson(punishObj);
				}

				financeMonitorPunish.setPunishDate(punishDate);
				financeMonitorPunish.setUrl(href);
				financeMonitorPunish.setSource("证监会");

				//增量抓取
				if (!doFetch(financeMonitorPunish, false)) {
					return lists;
				}

				lists.add(financeMonitorPunish);
			}
		}


		return lists;
	}

	/**
	 * 抓取并解析单条数据
	 *
	 * @param financeMonitorPunish
	 * @param isForce
	 */
	private boolean doFetch(FinanceMonitorPunish financeMonitorPunish,
							Boolean isForce) throws Exception {
		try {
			String fileName = downLoadFile(financeMonitorPunish.getUrl());
			String fullTxt = null;
			try {
				fullTxt = ocrUtil.getTextFromImg(fileName);
			} catch (RuntimeException e) {
				log.error("ocr 解析失败，失败原因：" + e.getMessage());
				return true;
			}
			log.info(fullTxt);
			financeMonitorPunish.setDetails(fullTxt);
			extractTxt(financeMonitorPunish);
			processSpecial(financeMonitorPunish);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("site>>>{} parse error", financeMonitorPunish.getUrl());
		}

		return saveOne(financeMonitorPunish, isForce);
	}

	/**
	 * 提取所需要的信息
	 * 处罚机构、违反条例、处罚事由、处罚结果、整改时限
	 */
	private void extractTxt(FinanceMonitorPunish financeMonitorPunish) {
		String details = "";
		String fullTxt = financeMonitorPunish.getDetails();
		fullTxt = fullTxt.replace("证怡会", "证监会").replace("怡管局", "监管局");

		String punishOrg = "";    //处罚机构
		int poPostion = fullTxt.indexOf("证监局办公室");
		if (poPostion < 0) poPostion = fullTxt.indexOf("监管局办公室");
		if (poPostion < 0) poPostion = fullTxt.indexOf("证监局");
		if (poPostion > 0) {
			for (int i = poPostion; ; i--) {
				if (i < 2) break;
				if (fullTxt.substring(i, i + 1).equals("\n")) {
					punishOrg = fullTxt.substring(i + 1, poPostion) + "证监局";
					punishOrg = punishOrg.replace("中国证监会", "");
					break;
				}
			}
		}

		fullTxt = fullTxt.replaceAll("\\s*", "")
				.replace("\n", "").trim();
		fullTxt = fullTxt.replace("o", "")
				.replace(")", "，")
				.replace("′", "")
				.replace("月艮", "服")
				.replace("，，", "，")
				.replace("曰", "日")
				.replace("邢言用", "信用")
				.replace("咋言", "信用")
				.replace("官芳", "官方")
				.replace("于2们", "201")
				.replace("016年3月4日", "2016年3月4日")
				.replace("特殊普逼合伙，", "特殊普逼合伙）")
				.replace("1~言用毒平乡及有1混", "信用评级有限")
				.replace("关干又寸丁豪木梁采耳叉'_海管毒炎i舌措方钜白勺决定丁豪本梁", "关于对丁豪樑采取监管谈话措施的决定-2016，丁豪樑")
				.replace("充勿》衣据", "充分依据。")
				.replace("才莫型", "模型")
				.replace("上、、户", "上沪")
				.replace("201813日E、", "")
				.replace("言用", "信用")
				.replace("犬公", "大公")
				.replace("关干", "关于")
				.replace("又寸", "对")
				.replace("耳关", "联合")
				.replace("夭津", "天津");

		if (fullTxt.contains("沪证监")) {
			punishOrg = "沪证监局";
		}

		//详情
		{
			if (fullTxt.indexOf("的决定") > -1) {
				int begin = fullTxt.indexOf("的决定") + 4;
				financeMonitorPunish.setDetails(fullTxt.substring(begin));
			}
		}

		//处罚文号
		{
			if (fullTxt.contains("20") && fullTxt.indexOf("20") < fullTxt.indexOf("号")) {
				int begin = fullTxt.indexOf("20") - 1;
				int end = fullTxt.indexOf("号") + 1;
				if (end - begin < 12) {
					financeMonitorPunish.setPunishNo(fullTxt.substring(begin, end));
				}
			}
		}


		String punishContent = "";    //处罚事由
		String sTmps[] = {"你公司的上述问题违反了", "上述情况", "上述问题", "上述行为", "违反了"};
		int pcPosition = -1;
		for (String sTmp : sTmps) {
			if (fullTxt.contains(sTmp)) {
				punishContent = fullTxt.substring(fullTxt.indexOf(":") + 1, fullTxt.indexOf(sTmp)).trim();
				if (punishContent.contains("。") && !punishContent.endsWith("。")) {
					punishContent = punishContent.substring(0, punishContent.lastIndexOf("。") + 1);
				}
				break;
			}
		}

		//解析完处罚事由后替换.replace(":", "，")
		details = fullTxt.contains(":") ? fullTxt.substring(fullTxt.indexOf(":") + 1) : fullTxt;
		fullTxt = fullTxt.replace(":", "，");


		String punishClause = "";    //违反条例
		String punishResult = "";    //处罚结果
		String improveLimit = "";    //整改时限


		String 如果对 = null;
		if (fullTxt.contains("如果对")) {
			如果对 = "如果对";
		} else if (fullTxt.contains("如对")) {
			如果对 = "如对";
		}

		String 违反了 = null;
		if (fullTxt.contains("违反了")) {
			违反了 = "违反了";
		} else if (fullTxt.contains("不符合")) {
			违反了 = "不符合";
		}

		if (StrUtil.isNotEmpty(违反了) && StrUtil.isNotEmpty(如果对)) {
			String punishSub = fullTxt.substring(fullTxt.indexOf(违反了), fullTxt.indexOf(如果对)).trim();
			String 根据 = null;
			if (punishSub.contains("根据")) {
				根据 = "根据";
			} else if (punishSub.contains("按照")) {
				根据 = "按照";
			}

			if (StrUtil.isNotEmpty(根据)) {
				punishClause = punishSub.substring(0, punishSub.indexOf(根据));
				punishResult = punishSub.substring(punishSub.indexOf(根据));
				if (extracterZH(punishSub).contains("年月日")) {
					improveLimit = punishSub.substring(punishSub.indexOf("年") - 4, punishSub.lastIndexOf("日") + 1);
				} else if (extracterZH(punishSub).contains("年月曰")) {
					int i = punishSub.indexOf("曰");
					improveLimit = punishSub.substring(punishSub.indexOf("年") - 4, punishSub.lastIndexOf("曰") + 1);
				}
			}
		}
		if (StrUtil.isNotEmpty(improveLimit)) {
			improveLimit = improveLimit.replace("于2们", "201")
					.replace("016年3月4日", "2016年3月4日");
			if (improveLimit.contains("你公司应在收到本决定之日起")) {
				improveLimit = improveLimit.substring(0, improveLimit.indexOf("你公司应在收到本决定之日起"));
			}
		}
		financeMonitorPunish.setPunishInstitution(punishOrg);
		financeMonitorPunish.setIrregularities(punishContent);
		financeMonitorPunish.setRelatedLaw(punishClause);
		financeMonitorPunish.setPunishResult(punishResult);
		financeMonitorPunish.setRemedialLimitTime(improveLimit);
		financeMonitorPunish.setDetails(details);
	}


	/**
	 * 特殊格式处理
	 *
	 * @param financeMonitorPunish
	 */
	private void processSpecial(FinanceMonitorPunish financeMonitorPunish) {
		String punishNo = financeMonitorPunish.getPunishNo();

		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201804/P020180417582550854670.pdf")) {
			punishNo = "〔2018〕23号";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201804/P020180417582113350410.pdf")) {
			punishNo = "〔2018〕21号";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201803/P020180302500821281240.pdf")) {
			punishNo = "〔2017〕115号";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201803/P020180302501004402508.pdf")) {
			punishNo = "〔2017〕113号";
		}

		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201712/P020171222595922177417.pdf")) {
			punishNo = "〔2016〕13号";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201712/P020171222596789053526.pdf")) {
			punishNo = "〔2016〕12号";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201712/P020171222596391704033.pdf")) {
			punishNo = "〔2016〕13号";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201712/P020171222575688119110.pdf")) {
			punishNo = "〔2016〕8号";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201712/P020171222593919368602.pdf")) {
			punishNo = "〔2014〕7号";
			financeMonitorPunish.setRemedialLimitTime("2014年4月15日");
		}
		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201712/P020171222575371239254.pdf")) {
			punishNo = "〔2014〕2号";
		}
		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201712/P020171222582821704312.pdf")) {
			punishNo = "〔2014〕3号";
		}

		if (financeMonitorPunish.getUrl().contains("http://www.csrc.gov.cn/pub/newsite/gszqjgb/rcjg/zjjgrcjg/201712/P020171222588743272338.pdf")) {
			punishNo = "〔2016〕3号";
			financeMonitorPunish.setPartyPerson("张志军、万华伟");
			financeMonitorPunish.setPartyInstitution(null);
			financeMonitorPunish.setPunishInstitution("天津证监局");
			financeMonitorPunish.setIrregularities("经查，我局发现你公司存在以下违规事项:未依据巳披露的评级方法对评级对象评级;评级委员会未按规定要求和业务流程对初评报告进行审查，做出决议，确定信用级别，未按照规定勤勉尽责的开展跟踪评级。");
		}
		financeMonitorPunish.setPunishNo(punishNo);
	}


}
