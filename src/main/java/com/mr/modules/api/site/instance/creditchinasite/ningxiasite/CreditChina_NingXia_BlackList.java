package com.mr.modules.api.site.instance.creditchinasite.ningxiasite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ImmediateRefreshHandler;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
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
 * 主题：信用中国（宁夏）
 * 属性：企业名称、统一社会信用代码、企业地址、列入原因、决定机关、移出日期、移出原因、信息提供部门、信息报送人、信息报送日期、最后修改日期
 * url：http://www.nxcredit.gov.cn/entBlackList.jspx?searchContent=  TODO searchContent为名称或者统一社会信用代码
 */
@Slf4j
@Component("creditchina_ningxia_blacklist")
@Scope("prototype")
public class CreditChina_NingXia_BlackList extends SiteTaskExtend_CreditChina{
    @Autowired
    SiteParams siteParams;
    @Override
    protected String execute() throws Throwable {
        String keyWork = siteParams.map.get("keyWork");
        if(keyWork==null){
            keyWork = "";
        }
        webContext(keyWork);
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    String baseUrl = "http://www.nxcredit.gov.cn";
    public void webContext(String keyWord){
        try {
            keyWord = URLEncoder.encode(keyWord,"utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("不支持Encodeing编码异常···"+e.getMessage());
        }
        String url = "http://www.nxcredit.gov.cn/entBlackList.jspx?searchContent="+keyWord;
        try {

            WebClient webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElement = htmlPage.getByXPath("//body//div[@class='main_body']//div[@class='content-container']//table//tbody//tr");
            for(HtmlElement htmlElement1Tr : htmlElement){
                String href = baseUrl+ htmlElement1Tr.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getAttribute("href");
                String companyName = htmlElement1Tr.getElementsByTagName("td").get(0).asText();
                String creditCode = htmlElement1Tr.getElementsByTagName("td").get(1).asText();
                String reason = htmlElement1Tr.getElementsByTagName("td").get(2).asText();
                HtmlPage htmlPageDetail = htmlElement1Tr.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).click();
                webClient.waitForBackgroundJavaScript(20000);
                log.info("context"+htmlPageDetail.asXml());
                List<HtmlElement> htmlElementList = htmlPageDetail.getByXPath(
                        "//body" +
                                "//div[@id='content']" +
                                "//div[@class='content clearfix']" +
                                "//div[@class='detailcompanyWrap']" +
                                "//div[@class='company-showmessage-box']" +
                                "//div[@class='company-messages-tab']" +
                                "//div[@class='result-tabs']" +
                                "//div[@class='result-tab result-tab6 ']" +
                                "//div[@class='result-tab6-showhide ']" +
                                "//ul[@class='administrative-licensing-lists administrative-licensing-lists-tab6 .administrative-licensing-lists-bg']" +
                                "//li[@class='administrative-licensing position-re']" +
                                "//table[@class='licensing-table']//tbody");
                for(HtmlElement htmlElementTbody : htmlElementList){
                    Map map = new HashMap();
                    map.put("source","信用中国（宁夏）");
                    map.put("subject","失信黑名单");
                    map.put("sourceUrl",href);
                    map.put("objectType","01");
                    map.put("enterpriseName",companyName);
                    map.put("enterpriseCode1",creditCode);
                    map.put("discreditType",reason);
                    List<HtmlElement> htmlElementsTrs = htmlElementTbody.getElementsByTagName("tr");
                    if(htmlElementsTrs.size()==8){

                        //列入原因：案件号：(2018)宁0181执594号。 案由：其他案由。  失信被执行人具体情况：其他有履行能力而拒不履行生效法律文书确定义务的。
                        String[] insertReason = htmlElementsTrs.get(0).getElementsByTagName("td").get(1).asText().split("。");
                        if(insertReason.length==3){
                            if(insertReason[0].contains("案件号：")){
                                map.put("judgeNo",insertReason[0].replaceAll("案件号：",""));
                            }else if(insertReason[1].contains("案由：")){
                                map.put("punishReason",insertReason[1].replaceAll("案由：",""));
                            }else if(insertReason[2].contains("失信被执行人具体情况：")){
                                map.put("punishResult",insertReason[2].replaceAll("失信被执行人具体情况：",""));
                            }else {
                                map.put("punishReason",htmlElementsTrs.get(0).getElementsByTagName("td").get(1).asText().replaceAll(".*：", ""));
                            }
                        }
                        //决定机关：
                        map.put("judgeAuth",htmlElementsTrs.get(1).getElementsByTagName("td").get(1).asText().replaceAll(".*：", ""));
                        //移出日期：
                        map.put("ycrq",htmlElementsTrs.get(2).getElementsByTagName("td").get(1).asText().replaceAll(".*：", ""));
                        //移出原因：
                        map.put("ycyy",htmlElementsTrs.get(3).getElementsByTagName("td").get(1).asText().replaceAll(".*：", ""));
                        //信息提供部门：
                        map.put("xxtgbm",htmlElementsTrs.get(4).getElementsByTagName("td").get(1).asText().replaceAll(".*：", ""));
                        //信息报送人：
                        map.put("xxbsr",htmlElementsTrs.get(5).getElementsByTagName("td").get(1).asText().replaceAll(".*：", ""));
                        //信息报送日期：
                        map.put("publishDate",htmlElementsTrs.get(6).getElementsByTagName("td").get(1).asText().replaceAll(".*：", ""));
                        //最后修改日期：
                        map.put("zhxgrq",htmlElementsTrs.get(7).getElementsByTagName("td").get(1).asText().replaceAll(".*：", ""));

                        //入库操作
                        discreditBlacklistInsert(map);
                    }
                }
            }

        } catch (Throwable throwable) {
            log.error("网络连接异常···清查看···"+throwable.getMessage());
        }
    }

    /**
     * 数据入口保存
     * @param map
     * @return
     */
    public DiscreditBlacklist discreditBlacklistInsert(Map<String,String> map){
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();
        //created_at	本条记录创建时间
        discreditBlacklist.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        discreditBlacklist.setUpdatedAt(new Date());
        //source	数据来源
        discreditBlacklist.setSource(map.get("source")==null?"":map.get("source"));
        //subject	主题
        discreditBlacklist.setSubject(map.get("subject")==null?"":map.get("subject"));
        //url	url
        discreditBlacklist.setUrl(map.get("sourceUrl")==null?"":map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        discreditBlacklist.setObjectType(map.get("objectType")==null?"":map.get("objectType"));
        //enterprise_name	企业名称
        discreditBlacklist.setEnterpriseName(map.get("enterpriseName")==null?"":map.get("enterpriseName"));
        //enterprise_code1	统一社会信用代码
        discreditBlacklist.setEnterpriseCode1(map.get("enterpriseCode1")==null?"":map.get("enterpriseName1"));
        //enterprise_code2	营业执照注册号
        discreditBlacklist.setEnterpriseCode2(map.get("enterpriseCode2")==null?"":map.get("enterpriseName2"));
        //enterprise_code3	组织机构代码
        discreditBlacklist.setEnterpriseCode3(map.get("enterpriseCode3")==null?"":map.get("enterpriseName3"));
        //enterprise_code3	组织机构代码
        discreditBlacklist.setEnterpriseCode4(map.get("enterpriseCode4")==null?"":map.get("enterpriseName4"));
        //person_name	法定代表人/负责人姓名|负责人姓名
        discreditBlacklist.setPersonName(map.get("personName")==null?"":map.get("personName"));
        //person_id	法定代表人身份证号|负责人身份证号
        discreditBlacklist.setPersonId(map.get("personId")==null?"":map.get("personId"));
        //discredit_type	失信类型
        discreditBlacklist.setDiscreditType(map.get("discreditType")==null?"":map.get("discreditType"));
        //discredit_action	失信行为
        discreditBlacklist.setDiscreditAction(map.get("discreditAction")==null?"":map.get("discreditAction"));
        //punish_reason	列入原因
        discreditBlacklist.setPunishReason(map.get("punishReason")==null?"":map.get("punishReason"));
        //punish_result	处罚结果
        discreditBlacklist.setPunishResult(map.get("punishResult")==null?"":map.get("punishResult"));
        //judge_no	执行文号
        discreditBlacklist.setJudgeNo(map.get("judgeNo")==null?"":map.get("judgeNo"));
        //judge_date	执行时间
        discreditBlacklist.setJudgeDate(map.get("judgeDate")==null?"":map.get("judgeDate"));
        //judge_auth	判决机关
        discreditBlacklist.setJudgeAuth(map.get("judgeAuth")==null?"":map.get("judgeAuth"));
        //publish_date	发布日期
        discreditBlacklist.setPublishDate(map.get("publishDate")==null?"":map.get("publishDate"));
        //status	当前状态
        discreditBlacklist.setStatus(map.get("status")==null?"":map.get("status"));
        saveDisneycreditBlackListOne(discreditBlacklist,false);
        return discreditBlacklist;
    }
}
