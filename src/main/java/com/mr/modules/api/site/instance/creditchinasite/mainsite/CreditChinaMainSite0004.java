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
 * 3.需求：2017 年第一季度国家重点监控企业主要污染物排放严重超标和处罚情况
 * 4.提取内容：企业名称、行政区划、处罚情况、整改情况、日期
 */
@Slf4j
@Component("creditChinaMainSite0004")
@Scope("prototype")
public class CreditChinaMainSite0004 extends SiteTaskExtend_CreditChina {
    protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);


    String url ="http://www.creditchina.gov.cn/xinxigongshi/huanbaolingyu/201804/t20180418_113468.html";
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
        // 行政区划、
        String administrativeArea = "";
        // 违法情形、
        String punishGress = "";
        // 整改情况、
        String rectifyAndReform = "";

        // 日期
        String dateString = "　2018年4月10日";
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
        try{
            pdfString = ocrUtil.getTextFromPdf(fileName);
        }catch (Exception e){

            while (resetCount==1){
                pdfString =  ocrUtil.getTextFromPdf(fileName);
                resetCount++;
            }

        }

        pdfString = pdfString.replace(" ","");
        pdfString = pdfString.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)"," ");
        pdfString = pdfString.replace("附件 2017年第一季度国家重点监控企业主要污染物排放严重超标和处罚情况 ","");
        pdfString = pdfString.replace("序 号 行政区划企业名称处罚情况整改情况","");
        pdfString = pdfString.replaceAll("—(.*)— ","");
        pdfString = pdfString.replaceAll("\\u0020+([0-9]{1,3})","\n");

        System.out.println("----------\n"+pdfString);

        //通过空格 数字 空格 来处理
        //replaceAll("\\u0020+([0-9]+)\\u0020+", "(\r\n|\r|\n|\n\r)")
       /* String[] strPdf = pdfString.split("\\u0020+([0-9]{1,6})\\u0020+");
        for(String str : strPdf){
            String[] resultList = str.trim().split(" ");
            StringBuffer detailAdd = new StringBuffer("");

            if(resultList.length>=5){
                for(int h=3;h<resultList.length-1;h++){
                    detailAdd = detailAdd.append(resultList[h]);
                }
                //企业名称、String commpanyName = "";
                commpanyName = resultList[0];
                // 统一社会信用代码（或组织机构代码或工商注册号）、String nnifiedSocialCreditCode = "";
                nnifiedSocialCreditCode = resultList[1];
                // 法定代表人/实际经营者姓名、String legalRepresentative = "";
                legalRepresentative = resultList[2];
                // 详细地址、String detailAddress = "";
                detailAddress = detailAdd.toString();
                // 违法情形、String transgress = "";
                transgress = resultList[resultList.length-1];

            }

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
            *//*log.info(
                    "\n来源："+personObjectMap.get("source") +
                            "\n来源地址："+personObjectMap.get("sourceUrl") +
                            "\n日期："+personObjectMap.get("dateString")+
                            "\n企业名称："+personObjectMap.get("commpanyName")+
                            "\n统一社会信用代码："+personObjectMap.get("nnifiedSocialCreditCode")+
                            "\n法定代表人："+personObjectMap.get("legalRepresentative")+
                            "\n详细地址："+personObjectMap.get("detailAddress")+
                            "\n违法情形："+personObjectMap.get("transgress")
            );*//*

        }


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

        }*/



    }

}
