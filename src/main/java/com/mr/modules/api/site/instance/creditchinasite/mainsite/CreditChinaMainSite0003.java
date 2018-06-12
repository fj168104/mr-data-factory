package com.mr.modules.api.site.instance.creditchinasite.mainsite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther
 * 1.信用中国主站
 * 2.url:http://www.creditchina.gov.cn/xinxigongshi/?navPage=4
 * 3.需求：2017 年环境违法企业“黑名单”
 * 4.提取内容：企业名称、统一社会信用代码（或组织机构代码或工商注册号）、法定代表人/实际经营者姓名、详细地址、违法情形、日期
 */
@Slf4j
@Component("creditChinaMainSite0003")
@Scope("prototype")
public class CreditChinaMainSite0003 extends SiteTaskExtend_CreditChina {
    protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);


    String url ="https://www.creditchina.gov.cn/xinxigongshi/huanbaolingyu/201804/t20180425_114081.html";
    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    @Override
    protected String execute() throws Throwable {
        extractContext(url);
        return null;
    }
    /**
     * 获取网页内容
     */
    public void extractContext(String url) throws Throwable{
        List<Map<String, String>> listPersonObjectMap = new ArrayList<>();
        //来源
        String source = "信用中国";
        //来源地址
        String sourceUrl = url;
        //企业名称、
        String commpanyName = "";
        // 统一社会信用代码（或组织机构代码或工商注册号）、
        String nnifiedSocialCreditCode = "";
        // 法定代表人/实际经营者姓名、
        String legalRepresentative = "";
        // 详细地址、
        String detailAddress = "";
        // 违法情形、
        String transgress = "";
        // 日期
        String dateString = "2018年1月16日";
        Document document  = Jsoup.parse(getHtmlPage(url,1000));
        Element element = document.getElementsByClass("TRS_Editor").first();
        Element elementA_PDF = element.getElementsByTag("a").first();
        String  fName = elementA_PDF.attr("href").substring(elementA_PDF.attr("href").lastIndexOf("/")+1);
        WebClient wc = new  WebClient(BrowserVersion.CHROME);
        wc.getOptions().setUseInsecureSSL(true);
        String pagePDFUrl = "https://www.creditchina.gov.cn/xinxigongshi/huanbaolingyu/201804/"+elementA_PDF.attr("href").replace("./","");
        Page pagePDF =wc.getPage(pagePDFUrl);
        String fileName = saveFile(pagePDF,fName);
        String pdfString = "";
        //重试次数
        int resetCount = 1;
        System.out.println("fileName-----------------------\n"+fileName);
        try{
            pdfString = ocrUtil.getTextFromPdf(fileName);
        }catch (Exception e){

            while (resetCount==1){
                pdfString =  ocrUtil.getTextFromPdf(fileName);
                resetCount++;
            }

        }
        pdfString = pdfString.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)","");
        pdfString = pdfString.replaceAll("附件 2017年环境违法企业“黑名单” ","");
        pdfString = pdfString.replaceAll("序号 企业名称 组织机构代码/统一社会信用代码/工商登记注册号 法定代表人/实际经营者姓名 详细地址 环境违法情形 ","").trim();
        String[] strPdf = pdfString.split(" ");

        for(int i = 0 ;i<strPdf.length;i++){
            int countFlag = strPdf.length%6;


            if(i%6==1){
                //企业名称、String commpanyName = "";
                commpanyName = strPdf[i];

            }
            if(i%6==2){
                // 统一社会信用代码（或组织机构代码或工商注册号）、String nnifiedSocialCreditCode = "";
                nnifiedSocialCreditCode = strPdf[i];

            }
            if(i%6==3){
                // 法定代表人/实际经营者姓名、String legalRepresentative = "";
                legalRepresentative = strPdf[i];

            }
            if(i%6==4){
                // 详细地址、String detailAddress = "";
                detailAddress = strPdf[i];

            }
            if(i%6==4){
                // 违法情形、String transgress = "";
                transgress = strPdf[i];

            }
            if(countFlag==0){
                Map<String,String> personObjectMap  = new HashMap<>();
                //来源String source = "信用中国";
                personObjectMap.put("source",source);
                //来源地址String sourceUrl = url;
                personObjectMap.put("sourceUrl",sourceUrl);
                // 日期String dateString = "";
                personObjectMap.put("dateString",dateString);
                personObjectMap.put("commpanyName",commpanyName);
                personObjectMap.put("nnifiedSocialCreditCode",nnifiedSocialCreditCode);
                personObjectMap.put("legalRepresentative",legalRepresentative);
                personObjectMap.put("detailAddress",detailAddress);
                personObjectMap.put("transgress",transgress);

                listPersonObjectMap.add(personObjectMap);
            }
        }

        //System.out.println("-----------------------\n"+pdfString);
        for(Map<String,String> map : listPersonObjectMap){
            log.info(
                    "\n来源："+map.get("source") +
                    "\n来源地址："+map.get("sourceUrl") +
                    "\n日期："+map.get("dateString")+
                    "\n企业名称："+map.get("commpanyName")+
                    "\n统一社会信用代码："+map.get("nnifiedSocialCreditCode")+
                    "\n法定代表人："+map.get("legalRepresentative")+
                    "\n详细地址："+map.get("detailAddress")+
                    "\n违法情形："+map.get("transgress")
            );

        }



    }

}

