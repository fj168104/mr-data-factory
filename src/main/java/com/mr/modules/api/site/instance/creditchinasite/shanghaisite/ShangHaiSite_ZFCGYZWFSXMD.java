package com.mr.modules.api.site.instance.creditchinasite.shanghaisite;

import com.gargoylesoftware.htmlunit.WebClient;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther zjxu to 201806
 *提取主题：政府采购严重违法失信名单查询
 *提取属性：企业名称、统一社会信用代码、纳入原因（TODO 严重违法失信行为的具体情形）
 **/
@Slf4j
@Component("shanghaisite_zfcgyzwfsxmd")
@Scope("prototype")
public class ShangHaiSite_ZFCGYZWFSXMD extends SiteTaskExtend_CreditChina {
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
    public void webContext(String keyWord,String ip, String port)throws Throwable{
        List<Proxypool> listIps = getProxyPool();
        String urlResult = "http://www.shcredit.gov.cn/credit/f/credit/query/?model=zfcgyzwfsxmd";
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

                map.put("sourceUrl",url+"&keywords="+elementsTd.get(0).text());//资源地址
                map.put("source","信用中国（上海）");
                map.put("subject","政府采购严重违法失信名单查询");
                map.put("objectType","01");
                map.put("enterpriseName",elementsTd.get(0).text());//企业名称
                map.put("enterpriseCode1",elementsTd.get(1).text());//企业社会统一代码
                map.put("punishReason",elementsTd.get(2).text());//案件性质 TODO 目前先放在列入原因属性中
                insertDiscreditBlacklist(map);

            }
        }
    }
}
