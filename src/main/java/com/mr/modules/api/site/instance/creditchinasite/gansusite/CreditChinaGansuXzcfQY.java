package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.mr.common.util.CrawlerUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（甘肃）-行政处罚（法人）
 * 
 * http://www.gscredit.gov.cn/sgs/xzcf/list.jspx?type=legal&keyword=
 * 
 * @author pxu 2018年6月13日
 */
@Slf4j
@Component("creditchina-gansu-xzcf-qy")
@Scope("prototype")
public class CreditChinaGansuXzcfQY extends SiteTaskExtend_CreditChina {
	private String baseUrl = CreditChinaSite.GANSU.getBaseUrl();

	@Override
	protected String execute() throws Throwable {
		log.info("抓取“信用中国（甘肃）-行政处罚（法人）”信息开始...");

		// adminPunishMapper.updateByPrimaryKey(record)

		String keyWord = SiteParams.map.get("keyWord");// 支持传入关键字进行查询
		if (StrUtil.isEmpty(keyWord)) {
			keyWord = "";
		} else {
			keyWord = URLEncoder.encode(keyWord, "UTF-8");// 对关键字进行UTF-8编码
		}
		HashSet<String> urlSet = extractPageUrlList(keyWord);// 抓取列表页面，获取详情页面URL列表
		for (String url : urlSet) {
			if (StringUtils.isEmpty(url)) {
				continue;
			}
			try {
				extractContent(url);// 抽取内容并入库
			} catch (Exception e) {
				log.warn("请检查此条url：{}", url, e);
				continue;
			} catch (Throwable e) {
				log.warn("请检查此条url：{}", url, e);
				continue;
			}
		}
		log.info("抓取“信用中国（甘肃）-行政处罚（法人）”信息结束！");
		return null;
	}

	/**
	 * 获取全部的详情页面URL集合
	 * 
	 * @param keyWord
	 *            查询关键字
	 * @return
	 */
	private HashSet<String> extractPageUrlList(String keyWord) {
		HashSet<String> urlSet = new LinkedHashSet<String>();

		WebClient wc = CrawlerUtil.createDefaultWebClient();
		wc.getOptions().setJavaScriptEnabled(true);// 启用JS解释器
		try {
			// 解析第一个页面，获取这个页面上下文
			String indexHtml = getData(wc, baseUrl + "/sgs/xzcf/list_1.jspx?type=legal&keyword=" + keyWord);
			// 获取总页数
			int pageAll = getPageNum(indexHtml);
			int j = 0;
			// 循环遍历所有页获取URL集合
			for (int i = 1; i <= pageAll; i++) {
				Document doc = null;
				if (i == 1) {// 直接解析首页
					doc = Jsoup.parse(indexHtml);
				} else {// 翻页获取URL集合
					String resultHtml = getData(wc, baseUrl + "/sgs/xzcf/list_" + i + ".jspx?type=legal&keyword=" + keyWord);
					doc = Jsoup.parse(resultHtml);
				}
				Elements elementsHerf = doc.getElementsByClass("conpany-name").select("a");
				for (Element element : elementsHerf) {
					Element elementUrl = element.getElementsByTag("a").first();
					String urlInfo = baseUrl + elementUrl.attr("href");// 详情url
					log.info("第" + (++j) + "个链接:" + urlInfo);
					urlSet.add(urlInfo);
				}
			}
		} finally {
			wc.close();
		}
		log.info("总计有效链接个数：" + urlSet.size());
		return urlSet;
	}

	/**
	 * 获取总页数
	 * 
	 * @param document
	 * @return
	 */
	private int getPageNum(String indexHtml) {
		Document doc = Jsoup.parse(indexHtml);
		Element element = doc.getElementsByClass("pages").select("li.count").first();// 获取分页元素
		if (element == null) {
			return 1;
		}
		int pageNum = 1;
		String totalPage = element.text().replace(Jsoup.parse("&nbsp;").text(), "");
		int beginIndex = totalPage.indexOf("共");
		int endIndex = totalPage.indexOf("页");
		if (totalPage.length() > 0 && beginIndex != -1 && endIndex > beginIndex + 1) {
			pageNum = Integer.valueOf(totalPage.substring(beginIndex + 1, endIndex).trim());
		}
		log.debug("==============================");
		log.debug("总页数为：" + pageNum);
		log.debug("==============================");
		return pageNum;
	}

	/**
	 * 获取网页内容,封装对象
	 */
	public void extractContent(String url) throws Throwable {
		String contentHtml = getData(url);
		Document doc = Jsoup.parse(contentHtml);

		log.debug("==============================");
		log.debug("url={}", url);

		Elements li = doc.getElementsByClass("artical_page_detail").select("ul > li");
		if (li != null && li.size() > 0) {
			Date nowDate = new Date();
			AdminPunish adminPunish = new AdminPunish();
			adminPunish.setCreatedAt(nowDate);// 本条记录创建时间
			adminPunish.setUpdatedAt(nowDate);// 本条记录最后更新时间
			adminPunish.setSource(CreditChinaSite.GANSU.getSiteName()); // 数据来源
			adminPunish.setSubject("行政处罚（对公）");// 主题
			adminPunish.setUrl(url);// url
			adminPunish.setObjectType("01");// 主体类型: 01-企业 02-个人
			adminPunish.setEnterpriseName("");// 企业名称
			adminPunish.setEnterpriseCode1("");// 统一社会信用代码
			adminPunish.setEnterpriseCode2("");// 营业执照注册号
			adminPunish.setEnterpriseCode3("");// 组织机构代码
			adminPunish.setEnterpriseCode4("");// 税务登记号
			adminPunish.setPersonName("");// 法定代表人/负责人姓名|负责人姓名
			adminPunish.setPersonId("");// 法定代表人身份证号|负责人身份证号
			adminPunish.setPunishType("");// 处罚类型
			adminPunish.setPunishReason("");// 处罚事由
			adminPunish.setPunishAccording("");// 处罚依据
			adminPunish.setPunishResult("");// 处罚结果
			adminPunish.setJudgeNo("");// 执行文号
			adminPunish.setJudgeDate("");// 执行时间
			adminPunish.setJudgeAuth("");// 判决机关
			adminPunish.setPublishDate("");// 发布日期

			String judgeDate = "";
			for (int i = 0; i < li.size(); i++) {
				Element nameEle = li.get(i).getElementsByClass("tab1-p-left").first();
				Element valueEle = li.get(i).getElementsByClass("tab1-p-right").first();
				if (nameEle == null || valueEle == null) {
					continue;
				}

				String name = nameEle.text();
				String value = valueEle.text();
				log.debug(name + "===" + value);
				switch (name) {
					case "行政处罚决定书文号：":
						adminPunish.setJudgeNo(value);
						break;
					case "案件名称：":
						break;
					case "处罚类别：":
						adminPunish.setPunishType(value);
						break;
					case "处罚事由：":
						adminPunish.setPunishReason(value);
						break;
					case "处罚依据：":
						adminPunish.setPunishAccording(value);
						break;
					case "行政相对人名称：":
						adminPunish.setEnterpriseName(value);
						break;
					case "统一社会信用代码：":
						adminPunish.setEnterpriseCode1(value);
						break;
					case "组织机构代码：":
						adminPunish.setEnterpriseCode3(value);
						break;
					case "工商登记码：":
						adminPunish.setEnterpriseCode2(value);
						break;
					case "税务登记号：":
						adminPunish.setEnterpriseCode4(value);
						break;
					case "法定代表人居民身份证号：":
						adminPunish.setPersonId(value);
						break;
					case "法定代表人姓名：":
						adminPunish.setPersonName(value);
						break;
					case "处罚结果：":
						adminPunish.setPunishResult(value);
						break;
					case "处罚生效期：":
						judgeDate = value + "~";
						break;
					case "处罚截止期：":
						judgeDate = judgeDate + value;
						break;
					case "处罚机关：":
						adminPunish.setJudgeAuth(value);
						break;
					case "当前状态：":
						break;
					case "地方编码：":
						break;
					case "备注：":
						break;
					case "信息提供部门：":
						break;
					case "公示日期：":
						adminPunish.setPublishDate(value);
						break;
					default:
						break;
				}
			}
			adminPunish.setJudgeDate(judgeDate);
			// 只插入不存在的记录
			int dbCount = adminPunishMapper.selectCountByJudgeNoAndName(adminPunish.getObjectType(), adminPunish.getJudgeNo(), adminPunish.getEnterpriseName());
			if (dbCount == 0) {
				adminPunishMapper.insert(adminPunish);
			}
		}
		log.debug("==============================");
	}
}
