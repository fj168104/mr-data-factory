package com.mr.modules.api.site.instance.creditchinasite.shandongsite;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（山东）-证监会市场禁入名单
 * 
 * http://www.creditsd.gov.cn/creditsearch.redlist.dhtml?source_id=160&type=black
 * 
 * @author pxu 2018年6月11日
 */
@Slf4j
@Component("creditchina-shandong-black-zqscjr")
@Scope("prototype")
public class CreditChinaShanDongBlackZqscjr extends SiteTaskExtend_CreditChina {
	@Override
	protected String execute() throws Throwable {
		log.info("抓取“信用中国（山东）-证监会市场禁入名单”信息开始...");

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
			int dbCount = discreditBlacklistMapper.selectCountByUrl(url);
			if (dbCount > 0) {// 若数据库中存在该记录，直接跳过
				continue;
			}
			try {
				extractContent(url);// 抽取内容并入库
			} catch (Exception e) {
				log.error("请检查此条url：{}", url, e);
				continue;
			} catch (Throwable e) {
				log.error("请检查此条url：{}", url, e);
				continue;
			}
		}
		log.info("抓取“信用中国（山东）-证监会市场禁入名单”信息结束！");
		return null;
	}

	/**
	 * 获取全部的详情页面URL集合
	 * 
	 * @return
	 */
	private HashSet<String> extractPageUrlList(String keyWord) {
		HashSet<String> urlSet = new LinkedHashSet<String>();

		String baseUrl = CreditChinaSite.SHANDONG.getBaseUrl();
		// 解析第一个页面，获取这个页面上下文
		String indexHtml = getData(baseUrl + "/creditsearch.redlist.dhtml?source_id=160&kw=" + keyWord + "&page=1");
		// 获取总页数
		int pageAll = getPageNum(indexHtml);
		int j = 0;
		// 循环遍历所有页获取URL集合
		for (int i = 1; i <= pageAll; i++) {
			Document doc = null;
			if (i == 1) {// 直接解析首页
				doc = Jsoup.parse(indexHtml);
			} else {// 翻页获取URL集合
				String resultHtml = getData(baseUrl + "/creditsearch.redlist.dhtml?source_id=160&kw=" + keyWord + "&page=" + i);
				doc = Jsoup.parse(resultHtml);
			}
			Elements elementsHerf = doc.select("div.right-content").select("table").select("tr:has(a)");
			for (Element element : elementsHerf) {
				Element elementUrl = element.getElementsByTag("a").first();
				String urlInfo = baseUrl + elementUrl.attr("href");// 详情url
				log.info("第" + (++j) + "个链接:" + urlInfo);
				urlSet.add(urlInfo);
			}
		}
		log.info("总计有效链接个数：" + urlSet.size());
		return urlSet;
	}

	/**
	 * @param indexHtml
	 * @return
	 */
	private int getPageNum(String indexHtml) {
		int pageNum = 1;
		Document doc = Jsoup.parse(indexHtml);
		Element element = doc.getElementsByClass("pagination").select("a[class='disabled']").first();// 获取分页元素
		String totalPage = element.text().replace(Jsoup.parse("&nbsp;").text(), "");
		int beginIndex = totalPage.indexOf("/");
		int endIndex = totalPage.indexOf("共");
		if (totalPage.length() > 0 && beginIndex > 0 && endIndex > beginIndex + 1) {
			pageNum = Integer.valueOf(totalPage.substring(beginIndex + 1, endIndex));
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

		Date nowDate = new Date();
		DiscreditBlacklist blackList = new DiscreditBlacklist();
		blackList.setCreatedAt(nowDate);// 本条记录创建时间
		blackList.setUpdatedAt(nowDate);// 本条记录最后更新时间
		blackList.setSource(CreditChinaSite.SHANDONG.getSiteName());// 数据来源
		blackList.setSubject("证监会市场禁入名单");// 主题
		blackList.setUrl(url);// url
		blackList.setObjectType("02");// 主体类型: 01-企业 02-个人
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
		blackList.setPublishDate("");// 发布日期
		blackList.setStatus("");// 当前状态

		boolean bIdentify = false;// 处罚类型是否为身份证
		Elements trs = doc.getElementsByClass("_main_content").select("tr");
		for (int i = 0; i < trs.size(); i++) {
			Element th = trs.get(i).select("th").first();
			Element td = trs.get(i).select("td").first();
			String name = th.ownText();
			String value = td.ownText();
			log.debug(name + "===" + value);
			if (Objects.equals(name, "处罚对象名称")) {
				blackList.setPersonName(value);// 法定代表人/负责人姓名|负责人姓名
			} else if (Objects.equals(name, "处罚类型")) {
				if ("身份证".equals(value)) {
					bIdentify = true;
				}
			} else if (Objects.equals(name, "加密证件号码")) {
				if (bIdentify) {
					blackList.setPersonId(value);// 法定代表人身份证号|负责人身份证号
				}
			} else if (Objects.equals(name, "处罚处理名称")) {
				blackList.setJudgeNo(value);// 执行文号
			} else if (Objects.equals(name, "处罚处理种类")) {
				blackList.setPunishResult(value);// 处罚结果
			} else if (Objects.equals(name, "处罚对象类型")) {

			} else if (Objects.equals(name, "真实证件号码")) {
				if (bIdentify && StrUtil.isNotEmpty(value)) {
					blackList.setPersonId(value);// 法定代表人身份证号|负责人身份证号
				}
			} else if (Objects.equals(name, "信息类型")) {

			} else if (Objects.equals(name, "处罚机关")) {
				blackList.setJudgeAuth(value);// 判决机关
			} else if (Objects.equals(name, "处罚处理日期")) {
				blackList.setJudgeDate(value);// 执行时间
			} else if (Objects.equals(name, "处罚处理内容")) {

			} else if (Objects.equals(name, "处罚决定书ID")) {

			} else if (Objects.equals(name, "实际处罚部门")) {

			} else if (Objects.equals(name, "有效截止期")) {

			}
		}
		blackList.setUniqueKey(blackList.getUrl() + "@" + blackList.getEnterpriseName() + "@" + blackList.getPersonName() + "@" + blackList.getJudgeNo() + "@" + blackList.getJudgeAuth());
		discreditBlacklistMapper.insert(blackList);
		log.debug("==============================");
	}
}
