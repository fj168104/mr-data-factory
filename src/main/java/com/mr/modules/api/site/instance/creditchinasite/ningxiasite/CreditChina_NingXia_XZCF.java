package com.mr.modules.api.site.instance.creditchinasite.ningxiasite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ImmediateRefreshHandler;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther zjxu
 * @DateTime 2018-07
 * 主题：行政处罚
 * 来源：信用中国（宁夏）
 * 属性：企业名称、统一社会信用代码、企业地址、决定书文号、处罚名称、法人代表人姓名、处罚类别、处罚结果、处罚事由、处罚依据、处罚机关、处罚生效期、处罚截止期、数据更新时间
 */

@Slf4j
@Component("creditchina_ningxia_xzcf")
@Scope("prototype")
public class CreditChina_NingXia_XZCF extends SiteTaskExtend_CreditChina {
    @Autowired
    SiteParams siteParams;
    @Override
    protected String execute() throws Throwable {
        String keyWord = siteParams.map.get("keyWord");
        if(keyWord==null){
            keyWord="";
        }
        webContext(keyWord);
       return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    String baseUrl = "http://www.nxcredit.gov.cn ";

    public void webContext(String keyWord) {

        try {
            keyWord = URLEncoder.encode(keyWord, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("不支持Encodeing编码异常···" + e.getMessage());
        }
        String url = "http://www.nxcredit.gov.cn/lXzcfList.jspx?searchContent=" + keyWord;
        try {

            WebClient webClient = createWebClient("", "");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElement = htmlPage.getByXPath("//body//div[@id='content']//div[@class='content ']//div[@class='result-tab result-tab1 search-result-wrap ']//ul[@class='search-result-list']//li[@class='tab1-item']");
            for (HtmlElement htmlElement1A : htmlElement) {
                String href = baseUrl + htmlElement1A.getElementsByTagName("a").get(0).getAttribute("href");

                HtmlPage htmlPageDetail = htmlElement1A.getElementsByTagName("a").get(0).click();
                webClient.waitForBackgroundJavaScript(10000);
                log.info("context" + htmlPageDetail.asXml());
                //获取信息头
                String companyName = "";
                String creditCode = "";
                List<HtmlElement> htmlElementHeader = htmlPageDetail.getByXPath("//body//div[@id='content']//div[@class='content clearfix']//div[@class='company-messages-box']//div[@class='messages-show-hide ']//div[@class='ajaxmessagebox']");
                if (htmlElementHeader.size() > 0) {
                    companyName = htmlElementHeader.get(0).getElementsByTagName("div").get(0).getElementsByTagName("h3").get(0).asText();
                    creditCode = htmlElementHeader.get(0).getElementsByTagName("ul").get(0).getElementsByTagName("li").get(0).asText().replaceAll(".*：", "");
                }

                List<HtmlElement> htmlElementList = htmlPageDetail.getByXPath(
                        "//body" +
                                "//div[@id='content']" +
                                "//div[@class='content clearfix']" +
                                "//div[@class='detailcompanyWrap']" +
                                "//div[@class='company-showmessage-box']" +
                                "//div[@class='company-messages-tab']" +
                                "//div[@class='result-tabs']" +
                                "//div[@class='result-tab result-tab3']" +
                                "//div[@class='result-tab3-showhide ']" +
                                "//ul[@class='administrative-licensing-lists administrative-licensing-lists-tab3 .administrative-licensing-lists-bg']" +
                                "//li[@class='administrative-licensing position-re']" +
                                "//table[@class='licensing-table']//tbody");
                for (HtmlElement htmlElementTbody : htmlElementList) {
                    Map map = new HashMap();
                    List<HtmlElement> htmlElementsTrs = htmlElementTbody.getElementsByTagName("tr");
                    if (htmlElementsTrs.size() == 11) {
                        map.put("sourceUrl", href);
                        map.put("enterpriseName", companyName);
                        map.put("enterpriseCode1", creditCode);
                        //决定书文号：
                        map.put("judgeNo", htmlElementsTrs.get(0).asText().replaceAll(".*：", ""));
                        //处罚名称：
                        map.put("cfmc", htmlElementsTrs.get(1).asText().replaceAll(".*：", ""));
                        //法人代表人姓名：
                        map.put("personName", htmlElementsTrs.get(2).asText().replaceAll(".*：", ""));
                        //处罚类别：
                        map.put("punishType", htmlElementsTrs.get(3).asText().replaceAll(".*：", ""));
                        //处罚结果：
                        map.put("punishResult", htmlElementsTrs.get(4).asText().replaceAll(".*：", ""));
                        //处罚事由：
                        map.put("publishDate", htmlElementsTrs.get(5).asText().replaceAll(".*：", ""));
                        //处罚依据：
                        map.put("punishAccording", htmlElementsTrs.get(6).asText().replaceAll(".*：", ""));
                        //处罚机关：
                        map.put("judgeAuth", htmlElementsTrs.get(7).asText().replaceAll(".*：", ""));
                        //处罚生效期：
                        map.put("judgeDate", htmlElementsTrs.get(8).asText().replaceAll(".*：", ""));
                        //处罚截止期：
                        map.put("zhxgrq", htmlElementsTrs.get(9).asText().replaceAll(".*：", ""));
                        //数据更新时间
                        map.put("publishDate", htmlElementsTrs.get(10).asText().replaceAll(".*：", ""));
                        //入库操作
                        adminPunishInsert(map);
                    }
                }
            }

        } catch (Throwable throwable) {
            log.error("网络连接异常···清查看···" + throwable.getMessage());
        }
    }

    public AdminPunish adminPunishInsert(Map<String, String> map) {
        AdminPunish adminPunish = new AdminPunish();
        //created_at	本条记录创建时间
        adminPunish.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        adminPunish.setUpdatedAt(new Date());
        //source	数据来源
        adminPunish.setSource("信用中国（宁夏）");
        //subject	主题
        adminPunish.setSubject("行政处罚");
        //url	url
        adminPunish.setUrl(map.get("sourceUrl") == null ? "" : map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        adminPunish.setObjectType("01");
        //enterprise_name	企业名称
        adminPunish.setEnterpriseName(map.get("enterpriseName") == null ? "" : map.get("enterpriseName"));
        //enterprise_code1	统一社会信用代码--cfXdrShxym
        adminPunish.setEnterpriseCode1(map.get("enterpriseCode1") == null ? "" : map.get("enterpriseCode1"));
        //enterprise_code2	营业执照注册号
        adminPunish.setEnterpriseCode2(map.get("enterpriseCode2") == null ? "" : map.get("enterpriseCode2"));
        //enterprise_code3	组织机构代码
        adminPunish.setEnterpriseCode3(map.get("enterpriseCode3") == null ? "" : map.get("enterpriseCode3"));
        //enterprise_code4	纳税人识别号
        adminPunish.setEnterpriseCode3(map.get("enterpriseCode4") == null ? "" : map.get("enterpriseCode4"));
        //person_name	法定代表人/负责人姓名|负责人姓名
        adminPunish.setPersonName(map.get("personName") == null ? "" : map.get("personName"));
        //person_id	法定代表人身份证号|负责人身份证号
        adminPunish.setPersonId(map.get("personId") == null ? "" : map.get("personId"));
        //punish_type	处罚类型
        adminPunish.setPunishType(map.get("punishType") == null ? "" : map.get("punishType"));
        //punish_reason	处罚事由
        adminPunish.setPunishReason(map.get("punishReason") == null ? "" : map.get("punishReason"));
        //punish_according	处罚依据
        adminPunish.setPunishAccording(map.get("punishAccording") == null ? "" : map.get("punishAccording"));
        //punish_result	处罚结果
        adminPunish.setPunishResult(map.get("punishResult") == null ? "" : map.get("punishResult"));
        //judge_no	执行文号
        adminPunish.setJudgeNo(map.get("judgeNo") == null ? "" : map.get("judgeNo"));
        //judge_date	执行时间
        adminPunish.setJudgeDate(map.get("judgeDate") == null ? "" : map.get("judgeDate"));
        //judge_auth	判决机关
        adminPunish.setJudgeAuth(map.get("judgeAuth") == null ? "" : map.get("judgeAuth"));
        //publish_date	发布日期
        adminPunish.setPublishDate(map.get("publishDate") == null ? "" : map.get("publishDate"));

        saveAdminPunishOne(adminPunish, false);
        return adminPunish;
    }
}
