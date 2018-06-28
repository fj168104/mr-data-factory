package com.mr.modules.api.site.instance.creditchinasite.shanghaisite;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.model.Proxypool;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther zjxu to 201806
 * 提取主题：失信名单查询
 * 提取属性：企业名称、统一社会信用代码、失信类型数量
 * url:http://www.shcredit.gov.cn/credit/f/credit/query/?model=sxmdc
 * urlResult:http://www.shcredit.gov.cn/credit/f/credit/query/?model=sxmdc&page=3&keywords=%E9%9F%A9%E6%B8%85%E4%BA%AE
 * 备注：网页有限制，最多只能浏览3页数据，建议通过精确查询进行处理
 * page：页数
 * keyWord:关键字查询
 **/
@Slf4j
@Component("ShangHaiSite_SXMDC")
@Scope("prototype")
public class ShangHaiSite_SXMDC extends SiteTaskExtend_CreditChina{
    String keyWord =null;
    int pageSize = 1;
    @Autowired
    SiteParams siteParams;
    @Override
    protected String execute() throws Throwable {
        String ip="",  port="";
        keyWord = siteParams.map.get("keyWord");
        if(keyWord==null){
            keyWord="";
        }

        webContext(keyWord,ip,port);
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }
    public void webContext(String keyWord,String ip, String port){
        List<Proxypool> listIps = getProxyPool();
        String urlResult = "http://www.shcredit.gov.cn/credit/f/credit/query/?model=sxmdc";
        WebClient webClient = createWebClient(ip,port);
        //网络拒绝连接，调用IP池
        Boolean connectFlag = true;
        while (connectFlag){
            try{
                HtmlPage htmlPage = webClient.getPage(urlResult);
                HtmlForm htmlForm = htmlPage.getForms().get(0);
                HtmlInput htmlInput = (HtmlInput)htmlForm.getByXPath("//div[@class='creditcode']//div[@class='creditcode-search']//input[@name='keywords']").get(0);
//        log.info("------------keyWord-------------"+keyWord);
                htmlInput.setValueAttribute(keyWord);
//        log.info("------------htmlInput-------------"+htmlInput.getValueAttribute());
                HtmlPage htmlPage1 = htmlForm.click();
                String pageStr = htmlPage1.asXml();
//        log.info("--------htmlPage1----------"+htmlPage1.asXml());
                if(pageStr.contains("javascript:page(1);")){
                    HtmlNav htmlNav = (HtmlNav)htmlPage1.getElementsByTagName("nav").get(0);
                    HtmlAnchor htmlAnchor = (HtmlAnchor) htmlNav.getByXPath("//ul[@class='pagination']//li//a[@onclick='javascript:page(1);']").get(0);
                    htmlPage1 = htmlAnchor.click();
                    pageStr = htmlPage1.asXml();
//            log.info("--------pageStr1----------"+pageStr);
                    pageParse(pageStr,urlResult);
                }
                if(pageStr.contains("javascript:page(2);")){
                    HtmlNav htmlNav = (HtmlNav)htmlPage1.getElementsByTagName("nav").get(0);
                    HtmlAnchor htmlAnchor = (HtmlAnchor) htmlNav.getByXPath("//ul[@class='pagination']//li//a[@onclick='javascript:page(2);']").get(0);
                    htmlPage1 = htmlAnchor.click();
                    pageStr = htmlPage1.asXml();
//            log.info("--------pageStr2----------"+pageStr);
                    pageParse(pageStr,urlResult);
                }
                if(pageStr.contains("javascript:page(3);")){
                    HtmlNav htmlNav = (HtmlNav)htmlPage1.getElementsByTagName("nav").get(0);
                    HtmlAnchor htmlAnchor = (HtmlAnchor) htmlNav.getByXPath("//ul[@class='pagination']//li//a[@onclick='javascript:page(3);']").get(0);
                    htmlPage1 = htmlAnchor.click();
                    pageStr = htmlPage1.asXml();
//            log.info("--------pageStr3----------"+pageStr);
                    pageParse(pageStr,urlResult);
                }
                connectFlag = false;
            }catch (IOException e){
                log.error("IO异常···异常信息为："+e.getMessage());
                if(listIps.size()<=0){
                        break;
                }
                listIps.remove(0);
                ip = listIps.get(0).getIpaddress();
                port = listIps.get(0).getIpport();
                connectFlag = true;
                log.info("服务决绝连接，切换代理，等待中···"+"ip地址："+ip+" port端口："+port);

            }
        }


        webClient.close();
    }

    /**
     * 页面解析，提取相应信息
     * @param webContext
     * @param url
     */
    public void pageParse(String webContext,String url){
        Document document = Jsoup.parse(webContext);
        Element element = document.getElementsByTag("tbody").get(0);
        Elements elementsTr = element.getElementsByTag("tr");
        for(Element elementTr:elementsTr){
            Map map = new HashMap();
            Elements elementsTd = elementTr.getElementsByTag("td");
            if(elementsTd.size()==3){

                map.put("sourceUrl",url+"&keywords="+elementsTd.get(0).text());
                map.put("commpanyName",elementsTd.get(0).text());
                map.put("nnifiedSocialCreditCode",elementsTd.get(1).text());
                map.put("judgeNo",elementsTd.get(2).text());
                discreditBlacklistInsert(map);

            }
        }
    }
    public DiscreditBlacklist discreditBlacklistInsert(Map<String,String> map){
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();
        //created_at	本条记录创建时间
        discreditBlacklist.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        discreditBlacklist.setUpdatedAt(new Date());
        //source	数据来源
        discreditBlacklist.setSource("信用中国（上海）");
        //subject	主题
        discreditBlacklist.setSubject("失信名单查询");
        //url	url
        discreditBlacklist.setUrl(map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        discreditBlacklist.setObjectType("01");
        //enterprise_name	企业名称
        discreditBlacklist.setEnterpriseName(map.get("commpanyName"));
        //enterprise_code1	统一社会信用代码
        discreditBlacklist.setEnterpriseCode1(map.get("nnifiedSocialCreditCode"));
        //enterprise_code2	营业执照注册号
        discreditBlacklist.setEnterpriseCode2("");
        //enterprise_code3	组织机构代码
        discreditBlacklist.setEnterpriseCode3("");
        //person_name	法定代表人/负责人姓名|负责人姓名
        discreditBlacklist.setPersonName("");
        //person_id	法定代表人身份证号|负责人身份证号
        discreditBlacklist.setPersonId("");
        //discredit_type	失信类型
        discreditBlacklist.setDiscreditType("");
        //discredit_action	失信行为
        discreditBlacklist.setDiscreditAction("");
        //punish_reason	列入原因
        discreditBlacklist.setPunishReason("");
        //punish_result	处罚结果
        discreditBlacklist.setPunishReason("");
        //judge_no	执行文号
        discreditBlacklist.setJudgeNo(map.get("judgeNo"));
        //judge_date	执行时间
        discreditBlacklist.setJudgeDate("");
        //judge_auth	判决机关
        discreditBlacklist.setJudgeAuth("");
        //publish_date	发布日期
        discreditBlacklist.setPublishDate("");
        //status	当前状态
        discreditBlacklist.setStatus("");
        saveDisneycreditBlackListOne(discreditBlacklist,false);
        return discreditBlacklist;
    }
}
