package com.mr.modules.api.site.instance.creditchinasite.mainsite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * @auther
 * 1.信用中国主站
 * 2.url:http://www.creditchina.gov.cn/xinxigongshi/?navPage=4
 * 3.需求：环保部公布的环评机构不良行为记录名单
 * 4.提取内容：机构名称、资质证号、惩罚时间、奖惩部门、惩罚类型、惩罚原因
 */

@Slf4j
@Component("creditchinamainsite0001")
@Scope( "prototype")
public class CreditChinaMainSite0001 extends SiteTaskExtend_CreditChina{
    @Autowired
    AdminPunishMapper adminPunishMapper;

    protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);

    //地址：http://htmlunit.sourceforge.net/apidocs/com/gargoylesoftware/htmlunit/httpclient/HtmlUnitSSLConnectionSocketFactory.html    https://www.creditchina.gov.cn/xinxigongshi/huanbaolingyu/201804/t20180419_113582.html
    String url ="https://www.creditchina.gov.cn/xinxigongshi/huanbaolingyu/201804/t20180419_113582.html";
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
        List<Map<String,String>> listOrgObjectMap = new ArrayList<>();
        String subject = "环保部公布的环评机构不良行为记录名单";
        //来源
        String source = "信用中国";
        //主题
        //来源地址
        String sourceUrl = url;
        //环评机构名称、
        String environDiscussOrg = "";
        // 资质证号、
        String aptitudeNo = "";
        // 惩罚时间、
        String punishDate = "";
        // 奖惩部门、
        String executeOrg = "";
        // 惩罚类型、
        String punishType = "";
        // 惩罚原因
        String punishReason = "";
        //行政处理方式
        String punishMethod = "";

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



        pdfString = pdfString.replace("限期整改","@限期整改");
        pdfString = pdfString.replace("通报批评","@通报批评&");
        pdfString = pdfString.replace("一年内不","@一年内不");
        pdfString = pdfString.replace("护厅","护厅@");
        pdfString = pdfString.replace("护局","护局@");
        pdfString = pdfString.replace("护部","护部@");
        pdfString = pdfString.replace("个月","个月&");
        pdfString = pdfString.replace("申请资质","请资质&");
        pdfString = pdfString.replace("甲","#");
        pdfString = pdfString.replace("乙","#");
        pdfString = pdfString.replace("附件 1","");
        pdfString = pdfString.replace("2017 年度受到环保部门两次及以上","");
        pdfString = pdfString.replace("行政处理的环评机构名单","");
        pdfString = pdfString.replaceAll("—(.*)—","");
        String[] stringsPdf = pdfString.split("#");
        //处罚机构
        String punishOrgTemp = "";
        String OrgTemp = "";
        //序号，用于替换被罚公司名称前的号码
        int serialNo = 1;

        for(String stringPdf : stringsPdf){
            String [] stringList = stringPdf.split("&");
            OrgTemp = stringList[stringList.length-1];
            //证书编号
            aptitudeNo = stringList[0].substring(1,5);
            stringList[0] = stringList[0].substring(5);
            if(stringList.length>1){
                int recordCount = 0;
                for(String string : stringList){
                    if (recordCount<stringList.length-1){
                        punishOrgTemp=punishOrgTemp.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)","").replaceAll("序号环评机构名(.*)处理次数","");
                        stringList[stringList.length-1] = punishOrgTemp.replaceAll(String.valueOf(serialNo),"");
                        string = string.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)","").replaceAll("序号环评机构名(.*)处理次数","");
                        //log.info("-------"+string);
                        //实施部门，受到行政处理的主要原因，行政处理方式
                        String[] resultList = string.split("@");

                        Map<String,String> OrgObjectMap = new HashMap<>();
                        //来源 String source = "信用中国";
                        OrgObjectMap.put("source",source);
                        //来源地址 String sourceUrl = url;
                        OrgObjectMap.put("sourceUrl",pagePDFUrl);
                        //环评机构名称、String environDiscussOrg = "";
                        environDiscussOrg = punishOrgTemp;
                        OrgObjectMap.put("enterpriseName",environDiscussOrg);
                        // 资质证号、String aptitudeNo = "";
                        OrgObjectMap.put("judgeNo",aptitudeNo);
                        // 惩罚时间、
                        punishDate = "2018年3月26日";
                        OrgObjectMap.put("judgeDate",punishDate);
                        // 奖惩部门、String executeOrg = "";
                        executeOrg = resultList[0];
                        OrgObjectMap.put("judgeAuth",executeOrg);
                        //行政处理方式  String punishMethod = "";
                        punishMethod = resultList[2];
                        if(punishMethod.contains("通报")&&!punishMethod.contains("整改")){
                            // 惩罚类型、String punishType = "";
                            OrgObjectMap.put("punishType","通报");
                        }else if(punishMethod.contains("整改")){
                            // 惩罚类型、String punishType = "";
                            OrgObjectMap.put("punishType","整改");
                        }else{
                            OrgObjectMap.put("punishType","其他");
                        }
                        // 惩罚原因 String punishReason = "";
                        punishReason = resultList[1];
                        OrgObjectMap.put("punishReason",punishReason);

                        listOrgObjectMap.add(OrgObjectMap);
                        //主题
                        OrgObjectMap.put("subject",subject);
                    }
                    recordCount ++;
                }
            }
            serialNo ++;
            punishOrgTemp = OrgTemp;
        }
        for(Map<String,String> map : listOrgObjectMap){
            adminPunishInsert(map);
        }
    }
}
