package com.mr.modules.api.site.instance.creditchinasite.guizhousite;

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
import org.apache.commons.codec.net.URLCodec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.html.HTMLIFrameElement;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 来源：信用中国（贵州）
 * 主题：行政处罚
 * 属性：处罚文书号 处罚类别 处罚结果 处罚事由 处罚依据 处罚决定日期 处罚有效期
 * 地址：http://www.gzcx.gov.cn/a/xinxishuanggongshi/shuanggongshichaxun/?creditCorpusCode=S&sgstype=xzcf&sgskeywords=
 * RESULT_URL:http://202.98.195.12:7809/CreditWebApi/f/sgs/xzcfxx/list?accessKey=556d8122421340ae8799bae4ad5c03a1&sid=eqk1koj1i23e72scs7f6osq4k3&keywords=
 * 注：关键字 进行了URLencord.encode(keyWord,"utf-8")两次转码 如：String str = URLEncoder.encode(URLEncoder.encode("贵州松河公司松河煤矿","utf-8"),"utf-8");
 *
 */
@Slf4j
@Component("guizhou_xzcf")
@Scope("prototype")
public class GuiZhou_XZCF extends SiteTaskExtend_CreditChina{
    @Autowired
    SiteParams siteParams;
    @Override
    protected String execute() throws Throwable {
        String keyWord = siteParams.map.get("keyWord");
        if(keyWord==null){
            keyWord="";
        }
        webContext(keyWord);
        return  null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    public void webContext(String keyWord){
        try {
            keyWord = URLEncoder.encode(keyWord,"UTF-8");
            String url = "http://www.gzcx.gov.cn/a/xinxishuanggongshi/shuanggongshichaxun/?creditCorpusCode=S&sgstype=xzcf&sgskeywords="+keyWord;

            WebClient webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);

            //5.1 获取内嵌界面Iframe
            HtmlElement htmlElementIframe =  (HtmlElement)htmlPage.getByXPath("//body[@class='wrapper']//div[@class='main_body']//div[@id='sgsmain']//iframe[@id='sgsmain']").get(0);
            String Iframe_SRC = htmlElementIframe.getAttribute("src")+"&keywords="+keyWord;
            if(Iframe_SRC.contains("xzxkxx")){
                Iframe_SRC = Iframe_SRC.replaceAll("xzxkxx","xzcfxx");
            }
            HtmlPage htmlPageIframe = webClient.getPage(Iframe_SRC);
            //5.2 获取列表清单数据
            List<HtmlElement> htmlElementList = htmlPageIframe.getByXPath("//body//div[@class='publicitywrap']//div[@class='publicitylist']//ul//li");
            int pageIndex =1;
            log.info("************************************第 "+pageIndex+" 页**************************************");
            for(HtmlElement htmlElementLi : htmlElementList){
                HtmlElement htmlElementA_Href = htmlElementLi.getElementsByTagName("h3").get(0).getElementsByTagName("a").get(0);
                //明细地址
                String detailHref = htmlElementA_Href.getAttribute("href");
                HtmlElement htmlElementP = htmlElementLi.getElementsByTagName("p").get(0);
                //更新时间
                String updateDate = htmlElementP.asText().replaceAll(".*：","");
                log.info("\n updateDate:"+updateDate+"---detailHref:"+detailHref);
                resultDetail(detailHref);
            }
            //翻页操作paginate_button next
            List<HtmlElement> htmlElementNextPage = htmlPageIframe.getByXPath("//body//div[@class='publicitywrap']//div[@class='pagination']//div[@class='col-sm-6 padding-right0']//ul[@class='no-mar pull-right']//li[@class='paginate_button next']");
            HtmlElement htmlElementNext = null;
            //翻页标识
            Boolean nextFlag = false;
            if(htmlElementNextPage.size()>0){
                htmlElementNext = htmlElementNextPage.get(0);
                nextFlag = true;
            }

            while (nextFlag){
                log.info("************************************第 "+ ++pageIndex +" 页**************************************");
                htmlPageIframe = htmlElementNext.click();
                //5.2 获取列表清单数据
                htmlElementList = htmlPageIframe.getByXPath("//body//div[@class='publicitywrap']//div[@class='publicitylist']//ul//li");

                for(HtmlElement htmlElementLi : htmlElementList){
                    HtmlElement htmlElementA_Href = htmlElementLi.getElementsByTagName("h3").get(0).getElementsByTagName("a").get(0);
                    //明细地址
                    String detailHref = htmlElementA_Href.getAttribute("href");
                    HtmlElement htmlElementP = htmlElementLi.getElementsByTagName("p").get(0);
                    //更新时间
                    String updateDate = htmlElementP.asText().replaceAll(".*：","");
                    log.info("\n updateDate:"+updateDate+"---detailHref:"+detailHref);
                    resultDetail(detailHref);
                }
                //翻页操作
                htmlElementNextPage = htmlPageIframe.getByXPath("//body//div[@class='publicitywrap']//div[@class='pagination']//div[@class='col-sm-6 padding-right0']//ul[@class='no-mar pull-right']//li[@class='paginate_button next']");
                //翻页标识
                if(htmlElementNextPage.size()>0&&htmlElementNextPage.get(0).asXml().contains("下一页")){
                    htmlElementNext = htmlElementNextPage.get(0);
                    nextFlag = true;
                }else {
                    break;
                }
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
    public Map resultDetail(String url){
        Map map = new HashMap();
        try {
            WebClient webClient = createWebClient("","");
            HtmlPage htmlPageDetail = webClient.getPage(url);
            HtmlElement htmlElementIframe = (HtmlElement)htmlPageDetail.getByXPath("//body//div[@class='main_body']//div[@class='body_part_left']//div[@class='xycxbody']//iframe[@id='xycxmain']").get(0);
            String detailSrc = htmlElementIframe.getAttribute("src");
            htmlPageDetail = webClient.getPage(detailSrc);
            HtmlElement htmlElementCompanyName = (HtmlElement)htmlPageDetail.getByXPath("//body//div[@class='companyinfo']//div[@class='top']").get(0);
            //公司名称
            String qymc = htmlElementCompanyName.getElementsByTagName("h3").get(0).asText();
            //统一社会信用代码
            String tyshxydm = htmlElementCompanyName.getElementsByTagName("p").get(0).asText();
            //企业地址
            String zydz = htmlElementCompanyName.getElementsByTagName("p").get(1).asText();
            List<HtmlElement> htmlElementDetail = htmlPageDetail.getByXPath("//body//div[@class='companyinfo']//div[@class='tab-wrap']//ul[@class='tab-bot']//li[@id='bot_3']//div[@class='con']//table//tbody");
            for(HtmlElement htmlElementTRs : htmlElementDetail){
                List<HtmlElement> htmlElementTR = htmlElementTRs.getElementsByTagName("tr");
                if(htmlElementTR.size()==10){
                    String cfwh = htmlElementTR.get(0).getElementsByTagName("td").get(1).asText();
                    String fddbr = htmlElementTR.get(1).getElementsByTagName("td").get(1).asText();
                    String cflb = htmlElementTR.get(2).getElementsByTagName("td").get(1).asText();
                    String cfjg = htmlElementTR.get(3).getElementsByTagName("td").get(1).asText();
                    String cfsy = htmlElementTR.get(4).getElementsByTagName("td").get(1).asText();
                    String cfyj = htmlElementTR.get(5).getElementsByTagName("td").get(1).asText();
                    String punishOrg = htmlElementTR.get(6).getElementsByTagName("td").get(1).asText();
                    String cfrq = htmlElementTR.get(7).getElementsByTagName("td").get(1).asText();
                    String cfyxq = htmlElementTR.get(8).getElementsByTagName("td").get(1).asText();
                    String gssj = htmlElementTR.get(9).getElementsByTagName("td").get(1).asText();
                    map.put("enterpriseName",qymc);
                    map.put("enterpriseCode1",tyshxydm);
                    map.put("address",zydz);
                    map.put("judgeNo",cfwh);
                    map.put("personName",fddbr);
                    map.put("punishType",cflb);
                    map.put("punishResult",cfjg);
                    map.put("punishReason",cfsy);
                    map.put("punishAccording",cfyj);
                    map.put("punishValidateDate",cfyxq);
                    map.put("punishDate",cfrq);
                    map.put("judgeAuth",punishOrg);
                    map.put("publishDate",gssj);
                    map.put("sourceUrl","http://www.gzcx.gov.cn/a/xinxishuanggongshi/shuanggongshichaxun/?creditCorpusCode=S&sgstype=xzcf&sgskeywords="+qymc);
                    map.put("source","信用中国（贵州）");
                    map.put("subject","行政处罚");
                    //数据入库
                    adminPunishInsert(map);
                }
            }

        } catch (Throwable throwable) {
            log.error("网络连接异常···清查看···"+throwable.getMessage());
        }
        return map;
    }


}
