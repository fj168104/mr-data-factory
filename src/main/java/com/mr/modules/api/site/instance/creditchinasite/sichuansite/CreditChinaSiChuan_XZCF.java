package com.mr.modules.api.site.instance.creditchinasite.sichuansite;

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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 来源：信用中国（四川）
 * 主题：行政处罚
 * 属性：处罚号 案件名称 行政相对人名称 处罚机关
 * url：http://www.creditsc.gov.cn/SCMH/doublePublicController/toDoublePublicPage?keyword=巴中广子石商贸有限公司
 */
@Slf4j
@Component("creditchinasichuan_xzcf")
@Scope("prototype")
public class CreditChinaSiChuan_XZCF extends SiteTaskExtend_CreditChina {
    @Autowired
    SiteParams siteParams;
    @Override
    protected String execute() throws Throwable {
        String keyWord = siteParams.map.get("keyWord") ;
        if(keyWord==null){
            keyWord = "";
        }
        webContext(keyWord);
        siteParams.map.remove("keyWord");
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    @Test
    public void webContext(String keyWord){
        //翻页标识 true 需要翻页；false 不需要翻页
        Boolean nextPageFlag = true;
        //记录是否存在的标识 true:存在；false存在
        Boolean recordFlag = false ;
        int pageSize = 1;
        String keyWordQuery = "";
        if(!keyWord.equals("add")){
            keyWordQuery = keyWord;
        }
        String url = "http://www.creditsc.gov.cn/SCMH/doublePublicController/toDoublePublicPage?keyWord="+keyWordQuery;
        String baseUrl = "http://www.creditsc.gov.cn";
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            //获取行记录
            List<HtmlElement> htmlElementTrList = htmlPage.getByXPath("//body//div[@class='fright con clearfix']//div//table//tbody//tr");
            //获取下一页事件
            List<HtmlElement> htmlElementNextPage = htmlPage.getByXPath("//body//div[@class='pagebox']//ul[@class='pagination jqPaginator']//li[@class='next']");
            for(HtmlElement htmlElementTr : htmlElementTrList){
                List<HtmlElement> htmlElementTds = htmlElementTr.getElementsByTagName("td");
                if(htmlElementTds.size()==4&&!htmlElementTr.asText().contains("处罚机关")){
                    Map map = new HashMap();
                    //处罚编号
                    String judgeNo = htmlElementTds.get(0).getElementsByTagName("a").get(0).getAttribute("title");
                    String sourceUrl = baseUrl+htmlElementTds.get(0).getElementsByTagName("a").get(0).getAttribute("href");
                    //处罚事由==案件名称
                    String punishReason = htmlElementTds.get(1).getElementsByTagName("a").get(0).getAttribute("title");
                    //处罚对象
                    String punishObject = htmlElementTds.get(2).getElementsByTagName("a").get(0).getAttribute("title");
                    //处罚机构
                    String enterpriseName = "";
                    //法定代表人
                    String personName = "";
                    if(punishObject.length()>0&&punishObject.length()<6){
                        personName = punishObject;
                    }
                    if(punishObject.length()>=6){
                        enterpriseName = punishObject;
                    }
                    //处罚机构 judgeAuth
                    String judgeAuth = htmlElementTds.get(3).getElementsByTagName("a").get(0).getAttribute("title");
                    map.put("judgeNo",judgeNo);
                    map.put("sourceUrl",sourceUrl);
                    map.put("punishReason",punishReason);
                    map.put("enterpriseName",enterpriseName);
                    map.put("personName",personName);
                    map.put("judgeAuth",judgeAuth);
                    map.put("source","信用中国（四川）");
                    map.put("subject","行政处罚");
                    recordFlag = adminPunishInsert(map);
                    log.info("\n处罚编号:"+judgeNo+"\n处罚事由:"+punishReason+"\n处罚对象:"+punishObject+"\n处罚机构:"+judgeAuth+"\n资源位置："+sourceUrl);
                    //TODO 入库

                }
            }
            log.info("*******************************"+pageSize+"*****************************************");
            //翻页操作
            HtmlElement nextPageClick = null;
            if(htmlElementNextPage.size()<0){
                nextPageFlag = false;
            }else {
                nextPageClick =htmlElementNextPage.get(0);
            }

            while(nextPageFlag){
                pageSize++;
                htmlPage = nextPageClick.click();
                //获取行记录
                htmlElementTrList = htmlPage.getByXPath("//body//div[@class='fright con clearfix']//div//table//tbody//tr");
                //获取下一页事件
                htmlElementNextPage = htmlPage.getByXPath("//body//div[@class='pagebox']//ul[@class='pagination jqPaginator']//li[@class='next']");
                for(HtmlElement htmlElementTr : htmlElementTrList){
                    List<HtmlElement> htmlElementTds = htmlElementTr.getElementsByTagName("td");
                    if(htmlElementTds.size()==4&&!htmlElementTr.asText().contains("处罚机关")){
                        Map map = new HashMap();
                        //处罚编号
                        String judgeNo = htmlElementTds.get(0).getElementsByTagName("a").get(0).getAttribute("title");
                        String sourceUrl = baseUrl+htmlElementTds.get(0).getElementsByTagName("a").get(0).getAttribute("href");
                        //处罚事由==案件名称
                        String punishReason = htmlElementTds.get(1).getElementsByTagName("a").get(0).getAttribute("title");
                        //处罚对象
                        String punishObject = htmlElementTds.get(2).getElementsByTagName("a").get(0).getAttribute("title");
                        //处罚机构
                        String enterpriseName = "";
                        //法定代表人
                        String personName = "";
                        if(punishObject.length()>0&&punishObject.length()<6){
                            personName = punishObject;
                        }
                        if(punishObject.length()>=6){
                            enterpriseName = punishObject;
                        }
                        //处罚机构 judgeAuth
                        String judgeAuth = htmlElementTds.get(3).getElementsByTagName("a").get(0).getAttribute("title");
                        map.put("judgeNo",judgeNo);
                        map.put("sourceUrl",sourceUrl);
                        map.put("punishReason",punishReason);
                        map.put("enterpriseName",enterpriseName);
                        map.put("personName",personName);
                        map.put("judgeAuth",judgeAuth);
                        map.put("source","信用中国（四川）");
                        map.put("subject","行政处罚");
                        recordFlag = adminPunishInsert(map);
                        log.info("\n处罚编号:"+judgeNo+"\n处罚事由:"+punishReason+"\n处罚对象:"+punishObject+"\n处罚机构:"+judgeAuth+"\n资源位置："+sourceUrl);
                        //TODO 入库

                    }
                }
                log.info("*******************************"+pageSize+"*****************************************");
                //翻页操作
                if(htmlElementNextPage.size()<0||htmlElementNextPage==null){
                    nextPageFlag = false;
                }else {
                    nextPageClick =htmlElementNextPage.get(0);
                }
                //增量触发标识
                if(keyWord.equals("add")&&recordFlag){
                    break;
                }
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        webClient.close();
    }
}
