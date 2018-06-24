package com.mr.modules.api.site.instance.creditchinasite.mainsite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Autowired
    DiscreditBlacklistMapper discreditBlacklistMapper;
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
        //主题
        String subject = "2017年环境违法企业“黑名单”";
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
        try{
            pdfString = ocrUtil.getTextFromPdf(fileName);
        }catch (Exception e){

            while (resetCount==1){
                pdfString =  ocrUtil.getTextFromPdf(fileName);
                resetCount++;
            }

        }
       // pdfString = pdfString.replace(" ","");
        pdfString = pdfString.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)","");
        pdfString = pdfString.replaceAll("附件 2017年环境违法企业“黑名单” ","");
        pdfString = pdfString.replaceAll("序号 企业名称 组织机构代码/统一社会信用代码/工商登记注册号 法定代表人/实际经营者姓名 详细地址 环境违法情形 ","").trim();
        pdfString = pdfString.replace("23号之 501","23号之501");
        pdfString = pdfString.replace("一路文锦广场文安中心 2904","一路文锦广场文安中心2904");
        pdfString = pdfString.replace("5016号京基一百大厦 A座 7201","5016号京基一百大厦A座7201");
        pdfString = pdfString.replace("广场五楼 583","广场五楼583");
        pdfString = pdfString.replace("社区广田路58号厂房 2栋 101","社区广田路58号厂房2栋101");
        pdfString = pdfString.replace("围工业区一路 1号 B栋 3楼 301","围工业区一路1号B栋3楼301");
        pdfString = pdfString.replace("八卦四路 430栋第三层 301","八卦四路430栋第三层301");
        pdfString = pdfString.replace("富文工业园 C栋 101","富文工业园C栋101");
        pdfString = pdfString.replace("秀峰工业区 A3栋 4楼东南座 403","秀峰工业区A3栋4楼东南座403");
        pdfString = pdfString.replace("龙东龙南路 289号 3区 101","龙东龙南路289号3区101");
        pdfString = pdfString.replace("玉石新村 425栋 103","玉石新村425栋103");
        pdfString = pdfString.replace("观澜桔塘社区下新塘 38号 301","观澜桔塘社区下新塘38号301");
        pdfString = pdfString.replace("口社区华苑路 3号 501","口社区华苑路3号501");
        pdfString = pdfString.replace("大林南路 890号办公楼第一层 102","大林南路890号办公楼第一层102");
        pdfString = pdfString.replace("日本中小企业工业园厂房 2-1首层 102","日本中小企业工业园厂房2-1首层102");
        pdfString = pdfString.replace("9144180309    23635000","914418030923635000");

        //通过空格 数字 空格 来处理
        //replaceAll("\\u0020+([0-9]+)\\u0020+", "(\r\n|\r|\n|\n\r)")
        String[] strPdf = pdfString.split("\\u0020+([0-9]{1,6})\\u0020+");
        for(String str : strPdf){
            if(str.contains("1 广州永和肉食加工有限公司")){
                str = str.replace("1 广州永和肉食加工有限公司","广州永和肉食加工有限公司");//1 广州永和肉食加工有限公司
            }
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
            personObjectMap.put("subject",subject);
            listPersonObjectMap.add(personObjectMap);
            /*log.info(
                    "\n来源："+personObjectMap.get("source") +
                            "\n来源地址："+personObjectMap.get("sourceUrl") +
                            "\n日期："+personObjectMap.get("dateString")+
                            "\n企业名称："+personObjectMap.get("commpanyName")+
                            "\n统一社会信用代码："+personObjectMap.get("nnifiedSocialCreditCode")+
                            "\n法定代表人："+personObjectMap.get("legalRepresentative")+
                            "\n详细地址："+personObjectMap.get("detailAddress")+
                            "\n违法情形："+personObjectMap.get("transgress")
            );*/

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
            discreditBlacklistInsert(map);

        }



    }
    public DiscreditBlacklist discreditBlacklistInsert(Map<String,String> map){
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();
        //created_at	本条记录创建时间
        discreditBlacklist.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        discreditBlacklist.setUpdatedAt(new Date());
        //source	数据来源
        discreditBlacklist.setSource(map.get("source"));
        //subject	主题
        discreditBlacklist.setSubject(map.get("subject"));
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
        discreditBlacklist.setPersonName(map.get("legalRepresentative"));
        //person_id	法定代表人身份证号|负责人身份证号
        discreditBlacklist.setPersonId("");
        //discredit_type	失信类型
        discreditBlacklist.setDiscreditType("环境违法");
        //discredit_action	失信行为
        discreditBlacklist.setDiscreditAction(map.get("transgress"));
        //punish_reason	列入原因
        discreditBlacklist.setPunishReason(map.get("transgress"));
        //punish_result	处罚结果
        discreditBlacklist.setPunishReason("");
        //judge_no	执行文号
        discreditBlacklist.setJudgeNo("广东省环境保护厅文件粤环〔2018〕3号");
        //judge_date	执行时间
        discreditBlacklist.setJudgeDate(map.get("dateString"));
        //judge_auth	判决机关
        discreditBlacklist.setJudgeAuth("广东省环境保护厅");
        //publish_date	发布日期
        discreditBlacklist.setPublishDate(map.get("dateString"));
        //status	当前状态
        discreditBlacklist.setStatus("黑名单");
        saveDisneycreditBlackListOne(discreditBlacklist,false);
        discreditBlacklistMapper.insert(discreditBlacklist);
        return discreditBlacklist;
    }

}

