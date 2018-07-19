package com.mr.modules.api.site.instance.creditchinasite.anhuisite;

import com.gargoylesoftware.htmlunit.WebClient;
import com.mr.common.CrawlerConstants;
import com.mr.common.util.ProxyIpUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.model.Proxypool;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

/**
 * 信用中国（安徽）-省地方税务局-重大税收违法案件信息（法人）
 * <p>
 * http://www.creditah.gov.cn/Black.htm-->http://www.creditah.gov.cn/remote/1481/index.htm
 *
 * @author pxu 2018年6月11日
 */
@Slf4j
@Component("creditchina-anhui-l-tax")
@Scope("prototype")
public class CreditChinaAnHuiLocalTaxPunish extends SiteTaskExtend_CreditChina {

    @Override
    protected String execute() throws Throwable {
        log.info("抓取“信用中国（安徽）-省地方税务局-重大税收违法案件信息（法人）”信息开始...");
        extractPageList();
        log.info("抓取“信用中国（安徽）-省地方税务局-重大税收违法案件信息（法人）”信息结束！");
        return null;
    }

    /**
     * 开始抓取网页列表数据
     */
    private void extractPageList() {
        List<Proxypool> ipList = new ArrayList<Proxypool>();// 可用的ip代理池
        List<Proxypool> proxyPoolList = getProxyPool();
        for (Proxypool ipPool : proxyPoolList) {
            String ip = ipPool.getIpaddress();
            int port = 80;
            try {
                port = Integer.parseInt(ipPool.getIpport());
            } catch (NumberFormatException e) {
            }
            if (ProxyIpUtil.checkProxy(ip, port, null) == 200) {
                ipList.add(ipPool);
                log.debug("一个可用的IP加入代理池中，当前可用IP{}个", ipList.size());
            }
            if (ipList.size() == 15) {// 取出15个可用的ip代理
                break;
            }
        }
        if (ipList.size() == 0) {
            log.warn("无可用的IP代理！");
        } else {
            log.info("获取可用IP代理个数{}个", ipList.size());
        }
        int ipIndex = 0;
        // 解析第一个页面，获取这个页面上下文
        String indexHtml = getHtmlContent(ipList, ipIndex, CreditChinaSite.ANHUI.getBaseUrl() + "/remote/1481/index_1.htm");
        if (indexHtml == null) {
            log.warn("获取首页页面内容失败，抓取结束");
            return;
        }
        //定义一个HashSet，用于存储抓取成功的页面URL
        HashSet<String> infoUrlSet = new LinkedHashSet<>();
        int j = 0;
        // 获取总页数
        int pageAll = getPageNum(indexHtml);
        for (int i = 1; i <= pageAll; i++) {
            String listUrl = CreditChinaSite.ANHUI.getBaseUrl() + "/remote/1481/index_" + i + ".htm";
            String listHtml = null;
            if (i == 1) {
                listHtml = indexHtml;
            } else {
                listHtml = getHtmlContent(ipList, ipIndex, listUrl);
            }
            if (listHtml == null) {
                log.warn("获取列表页面{}内容失败，跳过该页内容", listUrl);
                continue;
            }
            Document doc = Jsoup.parse(listHtml);
            Elements elementsHerf = doc.select(".right-box").select("table").select("div:has(a)");
            for (Element element : elementsHerf) {
                Element elementUrl = element.getElementsByTag("a").get(0);
                String urlInfo = CreditChinaSite.ANHUI.getBaseUrl() + elementUrl.attr("href");
                if (infoUrlSet.contains(urlInfo)) {
                    continue;
                }
                String infoHtml = getHtmlContent(ipList, ipIndex, urlInfo);//获取详情页面网页内容
                if (infoHtml == null) {
                    log.warn("抓取详情页{}失败", infoHtml);
                    continue;
                }
                DiscreditBlacklist blackList = createDefaultDiscreditBlacklist("重大税收违法案件信息", "01", urlInfo);
                extractPageInfo(blackList, infoHtml);
                blackList.setUniqueKey(urlInfo);
                discreditBlacklistMapper.insert(blackList);
                infoUrlSet.add(infoHtml);
                log.info("第" + (++j) + "个链接成功");
            }
        }
        log.info("共计成功抓取" + (infoUrlSet.size()) + "个链接:");
    }

    /**
     * 获取网页内容
     *
     * @param ipList  代理IP列表
     * @param ipIndex 索引序号
     * @param sUrl    URL地址
     * @return
     */
    private String getHtmlContent(List<Proxypool> ipList, int ipIndex, String sUrl) {
        WebClient client = null;
        //最多进行3次尝试抓取该网页
        for (int i = 0; i < 3; i++) {
            try {
                if (ipIndex == ipList.size()) {
                    ipIndex = 0;
                }
//                Proxypool ipProxy = ipList.get(ipIndex);
////                ipIndex++;
////                client = createWebClient(ipProxy.getIpaddress(), ipProxy.getIpport());
////                // 抓取网页内容
////                String htmlContent = CrawlerUtil.getHtmlPage(client, url, 3000L);
////                if (StrUtil.isEmpty(htmlContent) || htmlContent.contains("访问被拒绝")) {
////                    continue;
////                }
                Proxypool ipProxy = ipList.get(ipIndex);
                ipIndex++;
                Thread.sleep(3000);//访问每个URL前睡眠3秒
                URL url = new URL(sUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipProxy.getIpaddress(), Integer.parseInt(ipProxy.getIpport()))));
                httpURLConnection.setRequestProperty("User-Agent", CrawlerConstants.USER_AGENT_CHROME);
                httpURLConnection.setConnectTimeout(30000);
                httpURLConnection.setReadTimeout(30000);
                httpURLConnection.connect();
                int statusCode = httpURLConnection.getResponseCode();
                try {
                    if (statusCode == 200) {
                        @Cleanup
                        InputStream inputStream = httpURLConnection.getInputStream();
                        String htmlContent = IOUtils.toString(inputStream, "UTF-8");
                        if (StrUtil.isEmpty(htmlContent) || htmlContent.contains("访问被拒绝")) {
                            continue;
                        }
                        return htmlContent;
                    }
                } finally {
                    httpURLConnection.disconnect();
                }
            } catch (Throwable e) {
                continue;
            } finally {
                if (client != null) {
                    client.close();
                }
            }
        }
        log.debug("抓取{}失败", sUrl);
        return null;
    }

    /**
     * 获取列表总页数
     *
     * @param indexHtml
     * @return
     */
    private int getPageNum(String indexHtml) {
        int pageNum = 1;
        Document doc = Jsoup.parse(indexHtml);
        Element element = doc.getElementsByClass("allPage").first();
        String totalPage = element.text();
        if (totalPage.length() > 0) {
            pageNum = Integer.valueOf(totalPage);
        }
        log.debug("==============================");
        log.debug("总页数为：" + pageNum);
        log.debug("==============================");
        return pageNum;
    }

    /**
     * 获取网页字段，入库
     */
    private void extractPageInfo(DiscreditBlacklist blacklist, String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);

        log.debug("==============================");
        Elements trs = doc.getElementsByClass("infor").select("tr");
        for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).select("td");
            String name = tds.get(0).text();
            String value = tds.get(1).text();
            log.debug(name + "===" + value);
            if (Objects.equals(name, "企业名称")) {
                blacklist.setEnterpriseName(value);
            } else if (Objects.equals(name, "统一社会信用代码")) {
                blacklist.setEnterpriseCode1(value);
            } else if (Objects.equals(name, "纳税人识别号")) {
                blacklist.setEnterpriseCode4(value);
            } else if (Objects.equals(name, "法定代表人姓名")) {
                blacklist.setPersonName(value);
            } else if (Objects.equals(name, "案件性质")) {
                blacklist.setDiscreditType(value);
            } else if (Objects.equals(name, "组织机构代码")) {
                blacklist.setEnterpriseCode3(value);
            } else if (Objects.equals(name, "性别")) {

            } else if (Objects.equals(name, "主要违法事实")) {
                blacklist.setDiscreditAction(value);
            } else if (Objects.equals(name, "处罚依据")) {
                blacklist.setPunishReason(value);
            } else if (Objects.equals(name, "处罚结果")) {
                blacklist.setPunishResult(value);
            } else if (Objects.equals(name, "公布日期")) {
                blacklist.setPublishDate(value);
            } else if (Objects.equals(name, "撤销日期")) {
                if (StrUtil.isNotBlank(value)) {
                    blacklist.setStatus("撤销");
                }
            } else if (Objects.equals(name, "注销原因")) {

            } else if (Objects.equals(name, "实施检查单位")) {
                blacklist.setJudgeAuth(value);
            }
        }
        log.debug("==============================");
    }

    private DiscreditBlacklist createDefaultDiscreditBlacklist(String subject, String objectType, String infoUrl) {
        Date nowDate = new Date();
        DiscreditBlacklist blackList = new DiscreditBlacklist();
        blackList.setCreatedAt(nowDate);// 本条记录创建时间
        blackList.setUpdatedAt(nowDate);// 本条记录最后更新时间
        blackList.setSource(CreditChinaSite.ANHUI.getSiteName());// 数据来源
        blackList.setSubject(subject);// 主题
        blackList.setUrl(infoUrl);// url
        blackList.setUniqueKey("");//唯一性标识
        blackList.setObjectType(objectType);// 主体类型: 01-企业 02-个人。默认为企业
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
        return blackList;
    }
}
