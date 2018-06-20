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

import java.util.regex.Matcher;
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

        pdfString = pdfString.replace(" ","#");
        pdfString = pdfString.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)","@");
        pdfString = pdfString.replaceAll("([—]{1}[#]{1}[0-9]+[#]{1}[—]{1})","");
        pdfString = pdfString.replace("@附件@2017#年第一季度国家重点监控企业主要污染物排放严重超标和处罚情况@","");
        pdfString = pdfString.replace("序@号@行政区划#企#业#名#称#处#罚#情#况#整#改#情#况","");
        pdfString = pdfString.replace("年","&年");
        pdfString = pdfString.replace("月","&月");
        pdfString = pdfString.replace("日","&日");
        pdfString = pdfString.replace("万","&万");
        pdfString = pdfString.replace("@&","");
        pdfString = pdfString.replace("#&","");
        pdfString = pdfString.replace("&","");
        pdfString = pdfString.replaceAll("([。,@])+([0-9]+)+([#]+)","\n");
        pdfString = pdfString.replaceAll("([@])+([0-9]+)+([@]+)","\n");
        pdfString = pdfString.replace("@@序号#行政区划#企#业#名#称#处#罚#情#况#整#改#情#况","");
        pdfString = pdfString.replace("@序号#行政区划#企#业#名#称#处#罚#情#况#整#改#情#况","");
        //替换特殊字符(数字，小数)
        Matcher dd = java.util.regex.Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])").matcher(pdfString);
        Matcher ss = java.util.regex.Pattern.compile("[@,&,#]{0,2}([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])[@,&,#]{0,2}").matcher(pdfString) ;

        while(dd.find()&&ss.find()){
            pdfString = pdfString.replace(ss.group(), dd.group());
        }
        pdfString = pdfString.replaceAll("#","@");
        pdfString = pdfString.replaceAll("。2017","。@2017");
        pdfString = pdfString.replaceAll("@自治区@","自治区@");
        pdfString = pdfString.replaceAll("@建设兵团@","建设兵团@");
        log.info("换行处理后的文档：\n"+pdfString);

        //通过空格 数字 空格 来处理
        pdfString = pdfString.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)", "#");
        log.info("去掉换行处理后的文档：\n"+pdfString);
        String[] strPdf = pdfString.split("#");
        for(String str : strPdf){
            String[] resultList = str.trim().split("@");
            StringBuffer detailAdd = new StringBuffer("");

            if(resultList.length>=5){
                for(int h=3;h<resultList.length-1;h++){
                    detailAdd = detailAdd.append(resultList[h]);
                }
                //企业名称、String commpanyName = "";
                commpanyName = resultList[0];
                // 行政区划
                administrativeArea = resultList[1];

                // 违法情形、
                punishGress = resultList[2];
                // 整改情况、
                rectifyAndReform = detailAdd.toString();
                //存储对象
            }
            if(resultList.length==4){
                detailAdd = detailAdd.append(resultList[3]);
                //企业名称、String commpanyName = "";
                commpanyName = resultList[0];
                // 行政区划
                administrativeArea = resultList[1];

                // 违法情形、
                punishGress = resultList[2];
                // 整改情况、
                rectifyAndReform = detailAdd.toString();
                //存储对象
            }
            if(resultList.length<4){
                log.info("这条记录有异常，请查验···\n"+str);
            }

            Map<String,String> personObjectMap  = new HashMap<>();
            //来源String source = "信用中国";
            personObjectMap.put("source",source);
            //来源地址String sourceUrl = url;
            personObjectMap.put("sourceUrl",sourceUrl);
            // 日期String dateString = "";
            personObjectMap.put("dateString",dateString);
            personObjectMap.put("commpanyName",commpanyName);
            personObjectMap.put("administrativeArea",administrativeArea);
            personObjectMap.put("punishGress",punishGress);
            personObjectMap.put("rectifyAndReform",rectifyAndReform);

            listPersonObjectMap.add(personObjectMap);
            /*log.info(
                    "\n来源："+personObjectMap.get("source") +
                            "\n来源地址："+personObjectMap.get("sourceUrl") +
                            "\n日期："+personObjectMap.get("dateString")+
                            "\n企业名称："+personObjectMap.get("commpanyName")+
                            "\n行政区划："+personObjectMap.get("administrativeArea")+
                            "\n处罚情况："+personObjectMap.get("punishGress")+
                            "\n整改情况："+personObjectMap.get("rectifyAndReform")
            );*/

        }


        for(Map<String,String> map : listPersonObjectMap){
            log.info(
                    "\n来源："+map.get("source") +
                            "\n来源地址："+map.get("sourceUrl") +
                            "\n日期："+map.get("dateString")+
                            "\n企业名称："+map.get("commpanyName")+
                            "\n行政区划："+map.get("administrativeArea")+
                            "\n处罚情况："+map.get("punishGress")+
                            "\n整改情况："+map.get("rectifyAndReform")
            );

        }



    }

}
