package com.mr.modules.api.site.instance.creditchinasite.guangdongsite;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author zjxu
 * @DateTime 2018-07
 * 来源：信用中国（广东）
 * 主题：行政处罚
 * 属性：行政处罚决定文书号 处罚名称 处罚类别 处罚事由 处罚依据 处罚结果 行政相对人名称 处罚决定日期
 * 注：需要通过关键字进行查询，页面有访问限制，最多只能浏览5页
 * url：http://www.gdcredit.gov.cn/infoTypeAction!xzTwoPublicListIframe.do?type=7
 */
@Slf4j
@Component("creditchinaguangdong_xzcf")
@Scope("prototype")
public class CreditChinaGuangDong_XZCF extends SiteTaskExtend_CreditChina{
    @Autowired
    SiteParams siteParams;
    @Override
    protected String execute() throws Throwable {
        String keyWord =   siteParams.map.get("keyWord");
        if(keyWord == null){
            keyWord = "";
        }
        webContext(keyWord);
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    public void webContext(String keyWord){
        String url = "http://www.gdcredit.gov.cn/infoTypeAction!xzTwoPublicListIframe.do?type=7";
        try {
            WebClient webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            //TODO 获取页面上关键字查询要素
            HtmlElement htmlElementQureyEvent = (HtmlElement)htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//div[@class='content-div']//div[@class='right_div']//div[@class='select-twopublic']//div[@class='tabs-depart tabs']//div[@class='tabs-container']//div[@class='checkbox-container']//div[@class='search-container']").get(0);
            //查询事件
            HtmlElement htmlImage = htmlElementQureyEvent.getElementsByTagName("img").get(0);
            //关键字输入框
            HtmlElement htmlInput = htmlElementQureyEvent.getElementsByTagName("input").get(0);
            if(!"".equals(keyWord)){
                //给输入框赋值
                htmlInput.setAttribute("value",keyWord);
                htmlPage = htmlImage.click();
                webClient.waitForBackgroundJavaScript(10000);
            }


            //TODO 获取默认5页记录 注：获取iframe 中嵌套的界面
            HtmlElement htmlElementIframe = (HtmlElement)htmlPage.getElementsByTagName("iframe").get(0);
            String src=htmlElementIframe.getAttribute("src");
            HtmlPage ifrpage=webClient.getPage("http://www.gdcredit.gov.cn"+src);//读取iframe网页
            //读取列表清单
            List<HtmlElement> htmlElementIframe_Tr_List = ifrpage.getByXPath("//body//form//table//tbody//tr");
            for(HtmlElement htmlElement_Tr : htmlElementIframe_Tr_List){
                List<HtmlElement> htmlElementTds = htmlElement_Tr.getElementsByTagName("td");
                if(htmlElementTds.size()==3){
                    HtmlElement htmlElementTd = htmlElementTds.get(1);
                    String name = htmlElementTd.asText();
                    String href = "http://www.gdcredit.gov.cn"+htmlElementTd.getElementsByTagName("a").get(0).getAttribute("href");
                    String publishDate = htmlElementTds.get(2).asText();
                    //获取子页面详情
                    HtmlPage htmlPageDetail = createWebClient("","").getPage(href);
                    List<HtmlElement> htmlElementDetail = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content infoType-content']//table//tbody//tr");
                    //将明细存放起来
                    Map<String,String> map = new HashMap<>();
                    map.put("sourceUrl",href);
                    for(HtmlElement htmlElementTRs_detael:htmlElementDetail){
                        map.put("source","信用中国（广东）");
                        map.put("subject","行政处罚");
                        List<HtmlElement> htmlElementTds_detael = htmlElementTRs_detael.getElementsByTagName("td");
                        if(htmlElementTds_detael.size()==2&&!htmlElementTRs_detael.asText().contains("行政相对人代码")){
                            // TODO 行政处罚决定文书号 处罚名称(入库表中没这个属性) 处罚类别 处罚事由 处罚依据 处罚结果 行政相对人名称 处罚决定日期
                            if(htmlElementTds_detael.get(0).asText().contains("行政处罚决定文书号")){
                                map.put("judgeNo",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("处罚名称")){
                                map.put("cfmc",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("处罚类型1")){
                                map.put("punishType",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("处罚类型2")){
                                map.put("cflx2",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("处罚事由")){
                                map.put("punishReason",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("处罚依据")){
                                map.put("punishAccording",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("处罚结果")){
                                map.put("punishResult",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("行政相对人名称")){
                                map.put("enterpriseName",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("法人代表姓名")){
                                map.put("personName",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("处罚决定日期")){
                                map.put("judgeDate",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("处罚机关")){
                                map.put("judgeAuth",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("地方编码")){
                                map.put("dfbm",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("当前状态")){
                                map.put("dqzt",htmlElementTds_detael.get(1).asText());
                            }else if(htmlElementTds_detael.get(0).asText().contains("数据更新时间")){
                                map.put("publishDate",htmlElementTds_detael.get(1).asText());
                            }else {
                                continue;
                            }
                        }
                        if(htmlElementTds_detael.size()==5&&!htmlElementTRs_detael.asText().contains("行政相对人代码")){
                            if(!htmlElementTRs_detael.asText().contains("统一社会信用代码")){
                                map.put("enterpriseCode1",htmlElementTds_detael.get(0).asText());
                                map.put("enterpriseCode3",htmlElementTds_detael.get(1).asText());
                                map.put("enterpriseCode2",htmlElementTds_detael.get(2).asText());
                                map.put("swdjh",htmlElementTds_detael.get(3).asText());
                                map.put("jmsfzh",htmlElementTds_detael.get(4).asText());
                            }
                        }
                    }
                    //TODO 在此入库
                    adminPunishInsert(map);
                }
            }
            //TODO 为翻页读取列表清单做准备
            HtmlElement htmlElementNextPage = null;
            Boolean nextFlag = false;
            List<HtmlElement> htmlElementIframe_Page_A_List = ifrpage.getByXPath("//body//form//div[@class='page_div']//div[@class='pagination']//a");
            for(HtmlElement htmlElementA : htmlElementIframe_Page_A_List){
                if(htmlElementA.asText().contains("下一页")){
                    //TODO 执行翻页事件
                    htmlElementNextPage = htmlElementA;
                    nextFlag = true;
                }
            }
            //TODO 获取翻页事件
            while(htmlElementNextPage != null && nextFlag){
                nextFlag = false;
                //TODO 获取页面上关键字查询要素
                htmlElementQureyEvent = (HtmlElement)htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//div[@class='content-div']//div[@class='right_div']//div[@class='select-twopublic']//div[@class='tabs-depart tabs']//div[@class='tabs-container']//div[@class='checkbox-container']//div[@class='search-container']").get(0);
                //查询事件
                htmlImage = htmlElementQureyEvent.getElementsByTagName("img").get(0);
                //关键字输入框
                htmlInput = htmlElementQureyEvent.getElementsByTagName("input").get(0);
                //给输入框赋值
                htmlInput.setAttribute("value",keyWord);
                //TODO 获取默认5页记录 注：获取iframe 中嵌套的界面
                htmlElementIframe = (HtmlElement)htmlPage.getElementsByTagName("iframe").get(0);
                src=htmlElementIframe.getAttribute("src");
                //ifrpage=webClient.getPage("http://www.gdcredit.gov.cn"+src);//读取iframe网页
                ifrpage=htmlElementNextPage.click();
                //读取列表清单
                htmlElementIframe_Tr_List = ifrpage.getByXPath("//body//form//table//tbody//tr");
                for(HtmlElement htmlElement_Tr : htmlElementIframe_Tr_List){
                    List<HtmlElement> htmlElementTds = htmlElement_Tr.getElementsByTagName("td");
                    if(htmlElementTds.size()==3){
                        HtmlElement htmlElementTd = htmlElementTds.get(1);
                        String name = htmlElementTd.asText();
                        String href = "http://www.gdcredit.gov.cn"+htmlElementTd.getElementsByTagName("a").get(0).getAttribute("href");
                        String publishDate = htmlElementTds.get(2).asText();
                        log.info("publishDate"+publishDate+"-------------name:"+name+"----------------href:"+href);
                        //获取子页面详情
                        HtmlPage htmlPageDetail = createWebClient("","").getPage(href);
                        List<HtmlElement> htmlElementDetail = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content infoType-content']//table//tbody//tr");
                        //将明细存放起来
                        Map<String,String> map = new HashMap<>();
                        map.put("sourceUrl",href);
                        for(HtmlElement htmlElementTRs_detael:htmlElementDetail){
                            map.put("source","信用中国（广东）");
                            map.put("subject","行政处罚");
                            List<HtmlElement> htmlElementTds_detael = htmlElementTRs_detael.getElementsByTagName("td");
                            if(htmlElementTds_detael.size()==2&&!htmlElementTRs_detael.asText().contains("行政相对人代码")){
                                // TODO 行政处罚决定文书号 处罚名称(入库表中没这个属性) 处罚类别 处罚事由 处罚依据 处罚结果 行政相对人名称 处罚决定日期
                                if(htmlElementTds_detael.get(0).asText().contains("行政处罚决定文书号")){
                                    map.put("judgeNo",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("处罚名称")){
                                    map.put("cfmc",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("处罚类型1")){
                                    map.put("punishType",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("处罚类型2")){
                                    map.put("cflx2",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("处罚事由")){
                                    map.put("punishReason",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("处罚依据")){
                                    map.put("punishAccording",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("处罚结果")){
                                    map.put("punishResult",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("行政相对人名称")){
                                    map.put("enterpriseName",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("法人代表姓名")){
                                    map.put("personName",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("处罚决定日期")){
                                    map.put("judgeDate",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("处罚机关")){
                                    map.put("judgeAuth",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("地方编码")){
                                    map.put("dfbm",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("当前状态")){
                                    map.put("dqzt",htmlElementTds_detael.get(1).asText());
                                }else if(htmlElementTds_detael.get(0).asText().contains("数据更新时间")){
                                    map.put("publishDate",htmlElementTds_detael.get(1).asText());
                                }else {
                                    continue;
                                }
                            }
                            if(htmlElementTds_detael.size()==5&&!htmlElementTRs_detael.asText().contains("行政相对人代码")){
                                if(!htmlElementTRs_detael.asText().contains("统一社会信用代码")){
                                    map.put("enterpriseCode1",htmlElementTds_detael.get(0).asText());
                                    map.put("enterpriseCode3",htmlElementTds_detael.get(1).asText());
                                    map.put("enterpriseCode2",htmlElementTds_detael.get(2).asText());
                                    map.put("swdjh",htmlElementTds_detael.get(3).asText());
                                    map.put("jmsfzh",htmlElementTds_detael.get(4).asText());
                                }
                            }
                        }
                        //TODO 在此入库
                        adminPunishInsert(map);
                    }
                }
                //TODO 为翻页读取列表清单做准备
                htmlElementIframe_Page_A_List = ifrpage.getByXPath("//body//form//div[@class='page_div']//div[@class='pagination']//a");
                for(HtmlElement htmlElementA : htmlElementIframe_Page_A_List){
                    if(htmlElementA.asText().contains("下一页")){
                        //TODO 执行翻页事件
                        htmlElementNextPage = htmlElementA;
                        nextFlag = true;
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
