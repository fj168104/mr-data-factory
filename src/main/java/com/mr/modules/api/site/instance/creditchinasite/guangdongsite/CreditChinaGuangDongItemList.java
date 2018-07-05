package com.mr.modules.api.site.instance.creditchinasite.guangdongsite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ImmediateRefreshHandler;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther zjxu
 * @DateTime 201807
 * 来源：信用中国（广东）
 * 严重交通违法行为信息（含第9、11、4、5、6、7、13类）
 * 政府采购严重违法失信行为记录名单
 * 统计上严重失信企业
 * 广东省环境违法企业黑名单
 * 涉金融黑名单
 * 重大税收违法案件信息
 * 安全生产黑名单
 * 食品药品违法违规企业黑名单
 * 重大税收违法案件信息(省地税)
 * 重大税收违法案件信息(省国税)
 * 药品经营企业信用等级评定(严重失信)
 * 广东省地方税务局2016年第1号欠税公告名单
 * url:http://www.gdcredit.gov.cn/infoTypeAction!getInfoTypeList.do?type=2
 * 获取页面题主List<Map>
 *     item
 *     source
 *     count
 *     publishDate
 */
@Component
@Slf4j
public class CreditChinaGuangDongItemList {
    /**
     * 提取各主体信息
     */
    public void webContext(){
        String url = "http://www.gdcredit.gov.cn/infoTypeAction!getInfoTypeList.do?type=2";
        String urlTwo ="http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruelList.do?infoType.id=bc74880ef8534758ae068aeb8871fc15&dataCount=111&type=2";
        String urlThree ="http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=04F71764C5A1415FAB21D7A3E6EF5AA8&infoType.id=a6dc894db0f04b83940be0cf4cbe5ce5&type=6";
        //ItemList(url);
        //ItemObjectList(urlTwo,"");
        log.info(detailGDSDFSWJ2016ND1HHGGMD(urlThree,"").toString());
    }
    public void ItemObjectList(String url,String publishDate){
        //翻页标识 false：没有下一页了 true：有下一页
        boolean naxtPageNextFlag = false;
        int pageSize = 1;
        //下一页对象载体
        HtmlAnchor htmlAnchorNextPageEvent = null;
        List<Map> mapList = new ArrayList<>();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            log.info("\n******************************第 "+pageSize+" 页*********************************\n");
            List<HtmlElement> htmlElementTbodyList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//form//div[@class='content-div']//div[@class='right_div']//div[@class='list_content list_content_continue']//table//tbody");
            List<HtmlElement> htmlElementNextList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//form//div[@class='content-div']//div[@class='right_div']//div[@class='page_div']//div[@class='pagination']");
            List<HtmlElement> htmlElementAList = htmlElementNextList.get(0).getElementsByTagName("a");
            //获取行
            for(HtmlElement htmlElementTbody:htmlElementTbodyList){
                String href ="";
                String item ="";
                log.info("\n***************************************************************\n");
                List<HtmlElement> htmlElementsTr = htmlElementTbody.getElementsByTagName("tr");
                //获取列
                if(htmlElementsTr.size()==3){
                    //获取第二层链接地址与主题信息
                    HtmlAnchor htmlAnchor = (HtmlAnchor)htmlElementsTr.get(0).getElementsByTagName("a").get(0);
                    href = "http://www.gdcredit.gov.cn"+htmlAnchor.getAttribute("href");
                    item = htmlAnchor.getAttribute("title");
                    log.info("item:"+item+"---------------href:"+href);
                    //获取详情页面信息

                }

            }

            if(htmlElementAList.size()>0){
                for(HtmlElement htmlElementA : htmlElementAList){
                    if(htmlElementA.asText().contains("下一页")){
                        naxtPageNextFlag =true;
                        htmlAnchorNextPageEvent = (HtmlAnchor) htmlElementA;
                    }
                }
            }
            //递归翻页
            while(naxtPageNextFlag){
                //循环进入，间翻页标识先置位为 false
                naxtPageNextFlag = false;
                log.info("\n******************************第 "+pageSize++ +" 页*********************************\n");
                htmlPage = htmlAnchorNextPageEvent.click();
                log.info("\n******************************第 "+pageSize+" 页*********************************\n");
                htmlElementTbodyList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//form//div[@class='content-div']//div[@class='right_div']//div[@class='list_content list_content_continue']//table//tbody");
                htmlElementNextList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//form//div[@class='content-div']//div[@class='right_div']//div[@class='page_div']//div[@class='pagination']");
                htmlElementAList = htmlElementNextList.get(0).getElementsByTagName("a");
                //获取行
                for(HtmlElement htmlElementTbody:htmlElementTbodyList){
                    String href ="";
                    String item ="";
                    log.info("\n***************************************************************\n");
                    List<HtmlElement> htmlElementsTr = htmlElementTbody.getElementsByTagName("tr");
                    //获取列
                    if(htmlElementsTr.size()==3){
                        //获取第二层链接地址与主题信息
                        HtmlAnchor htmlAnchor = (HtmlAnchor)htmlElementsTr.get(0).getElementsByTagName("a").get(0);
                        href = "http://www.gdcredit.gov.cn"+htmlAnchor.getAttribute("href");
                        item = htmlAnchor.getAttribute("title");
                        log.info("item:"+item+"---------------href:"+href);
                        //获取详情页面信息

                    }

                }

                if(htmlElementAList.size()>0){
                    for(HtmlElement htmlElementA : htmlElementAList){
                        if(htmlElementA.asText().contains("下一页")){
                            naxtPageNextFlag =true;
                            htmlAnchorNextPageEvent = (HtmlAnchor) htmlElementA;
                        }
                    }
                }
            }

        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }
    }
    /**
     * 第一步
     * 提取主题清单
     * @param url
     * @return
     */
    public List<Map> ItemList(String url){
        //翻页标识 false：没有下一页了 true：有下一页
        boolean naxtPageNextFlag = false;
        int pageSize = 1;
        //下一页对象载体
        HtmlAnchor htmlAnchorNextPageEvent = null;
        List<Map> mapList = new ArrayList<>();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            log.info("\n******************************第 "+pageSize+" 页*********************************\n");
            List<HtmlElement> htmlElementTbodyList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//div[@class='content-div']//div[@class='right_div']//div[@class='list_content']//table//tbody");
            List<HtmlElement> htmlElementNextList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//div[@class='content-div']//div[@class='right_div']//div[@class='page_div']//div[@class='pagination']");
            List<HtmlElement> htmlElementAList = htmlElementNextList.get(0).getElementsByTagName("a");
            //获取行
            for(HtmlElement htmlElementTbody:htmlElementTbodyList){
                Map map = new HashMap();
                String href ="";
                String item ="";
                String source = "";
                String countAll = "";
                String publishDate ="";
                log.info("\n***************************************************************\n");
                List<HtmlElement> htmlElementsTr = htmlElementTbody.getElementsByTagName("tr");
                //获取列
                if(htmlElementsTr.size()==2){

                    //获取第一层链接地址与主题信息
                    HtmlAnchor htmlAnchor = (HtmlAnchor)htmlElementsTr.get(0).getElementsByTagName("a").get(0);
                    href = "http://www.gdcredit.gov.cn"+htmlAnchor.getAttribute("href");
                    item = htmlAnchor.getAttribute("title");
                    List<HtmlElement> htmlElementTd = htmlElementsTr.get(1).getElementsByTagName("td");
                    if(htmlElementTd.size()==4){
                        source = htmlElementTd.get(0).asText().replaceAll(".*：","");
                        countAll = htmlElementTd.get(1).asText().replaceAll(".*：","");
                        publishDate = htmlElementTd.get(2).asText().replaceAll(".*：","");
                    }
                    log.info("\n href:"+href+"\n item:"+item+"\n source:"+source+"\n countAll:"+countAll+"\n publishDate:"+publishDate);
                }
                if(href.length()>0&&item.length()>0&&source.length()>0&&countAll.length()>0&&publishDate.length()>0){
                    map.put("href",href);
                    map.put("item",item);
                    map.put("source",source);
                    map.put("countAll",countAll);
                    map.put("publishDate",publishDate);


                }
                if(map.size()>0){
                    mapList.add(map);
                }
            }

            if(htmlElementAList.size()>0){
                for(HtmlElement htmlElementA : htmlElementAList){
                    if(htmlElementA.asText().contains("下一页")){
                        naxtPageNextFlag =true;
                        htmlAnchorNextPageEvent = (HtmlAnchor) htmlElementA;
                    }
                }
            }
            //递归翻页
            while(naxtPageNextFlag){
                //循环进入，间翻页标识先置位为 false
                naxtPageNextFlag = false;
                log.info("\n******************************第 "+pageSize++ +" 页*********************************\n");
                htmlPage = htmlAnchorNextPageEvent.click();
                htmlElementTbodyList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//div[@class='content-div']//div[@class='right_div']//div[@class='list_content']//table//tbody");
                htmlElementNextList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//div[@class='content-div']//div[@class='right_div']//div[@class='page_div']//div[@class='pagination']");
                htmlElementAList = htmlElementNextList.get(0).getElementsByTagName("a");
                //获取行
                for(HtmlElement htmlElementTbody:htmlElementTbodyList){
                    Map map = new HashMap();
                    String href ="";
                    String item ="";
                    String source = "";
                    String countAll = "";
                    String publishDate ="";
                    log.info("\n***************************************************************\n");
                    List<HtmlElement> htmlElementsTr = htmlElementTbody.getElementsByTagName("tr");
                    //获取列
                    if(htmlElementsTr.size()==2){

                        //获取第一层链接地址与主题信息
                        HtmlAnchor htmlAnchor = (HtmlAnchor)htmlElementsTr.get(0).getElementsByTagName("a").get(0);
                        href = "http://www.gdcredit.gov.cn"+htmlAnchor.getAttribute("href");
                        item = htmlAnchor.getAttribute("title");
                        List<HtmlElement> htmlElementTd = htmlElementsTr.get(1).getElementsByTagName("td");
                        if(htmlElementTd.size()==4){
                            source = htmlElementTd.get(0).asText().replaceAll(".*：","");
                            countAll = htmlElementTd.get(1).asText().replaceAll(".*：","");
                            publishDate = htmlElementTd.get(2).asText().replaceAll(".*：","");
                        }
                        log.info("\n href:"+href+"\n item:"+item+"\n source:"+source+"\n countAll:"+countAll+"\n publishDate:"+publishDate);
                    }
                    if(href.length()>0&&item.length()>0&&source.length()>0&&countAll.length()>0&&publishDate.length()>0){
                        map.put("href",href);
                        map.put("item",item);
                        map.put("source",source);
                        map.put("countAll",countAll);
                        map.put("publishDate",publishDate);


                    }
                    if(map.size()>0){
                        mapList.add(map);
                    }
                }

                if(htmlElementAList.size()>0){
                    for(HtmlElement htmlElementA : htmlElementAList){
                        if(htmlElementA.asText().contains("下一页")){
                            naxtPageNextFlag =true;
                            htmlAnchorNextPageEvent = (HtmlAnchor) htmlElementA;
                        }
                    }
                }
            }

        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }
        return mapList;
    }
    /**
     * 创建一个htmlUnit webClient 客户端
     * @param ip
     * @param port
     * @return
     */
    public WebClient createWebClient(String ip, String port) throws Throwable{
        WebClient wc =  null;
        if ("".equals(ip) || "".equals(port)||ip==null||port==null) {
            wc = new WebClient(BrowserVersion.getDefault());
            log.info("通过本地ip进行处理···");
        } else {
            //获取代理对象
            wc = new WebClient(BrowserVersion.getDefault(), ip,Integer.valueOf(port));
            log.info("通过代理进行处理···");
        }

        //设置浏览器版本
        //是否使用不安全的SSL
        wc.getOptions().setUseInsecureSSL(false);
        //启用JS解释器，默认为true
        wc.getOptions().setJavaScriptEnabled(true);
        //禁用CSS
        wc.getOptions().setCssEnabled(false);
        //js运行错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnScriptError(false);
        //状态码错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        //是否允许使用ActiveX
        wc.getOptions().setActiveXNative(false);
        //等待js时间
        wc.waitForBackgroundJavaScript(5000);
        //设置Ajax异步处理控制器即启用Ajax支持
        wc.setAjaxController(new NicelyResynchronizingAjaxController());
        //设置超时时间
        wc.getOptions().setTimeout(20000);
        //不跟踪抓取
        wc.getOptions().setDoNotTrackEnabled(false);
        //启动客户端重定向
        wc.getOptions().setRedirectEnabled(true);
        //
        wc.getCookieManager().clearCookies();
        //
        wc.setRefreshHandler(new ImmediateRefreshHandler());
        return wc;
    }
    /**
     * 食品药品违法违规企业黑名单
     * @param url
     * @param publishDate
     */
    public Map detailSPYPWFWGQYMD(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("xymc", htmlElementTdList.get(1).asText());//企业名称
            map.put("frdb", htmlElementTdList.get(3).asText());//法人代表
            map.put("dwdz", htmlElementTdList.get(5).asText());//单位地址
            map.put("zywfwgxw",htmlElementTdList.get(7).asText());//主要违法违规行为
            map.put("cfyj", htmlElementTdList.get(9).asText());// 处罚依据
            map.put("cfjg", htmlElementTdList.get(11).asText());//处罚结果
            map.put("gbqx", htmlElementTdList.get(15).asText());//公布期限
            map.put("sacpmc", htmlElementTdList.get(17).asText());//涉案产品名称
            map.put("sacpbs", htmlElementTdList.get(19).asText());//涉案产品标识
            map.put("sacpxkzh", htmlElementTdList.get(23).asText());//涉案产品生产许可证号
            map.put("sacppzwh", htmlElementTdList.get(25).asText());//涉案产品批准文号
            map.put("fbdw", htmlElementTdList.get(27).asText()); //发布单位
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }

    /**
     * 严重交通违法行为信息（含第9、11、4、5、6、7、13类）
     * @param url
     * @param publishDate
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=883F79A1FCA14EF2AD169EB7E7A3D44D&infoType.id=00C4F00DAF35452F8D288FEFAC7CD8D3&type=2
     */
    public Map detailYZJTWFXWXX(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("dsr", htmlElementTdList.get(1).asText());//当事人
            map.put("hphm", htmlElementTdList.get(3).asText());//号牌号码
            map.put("xzcfjdsbh", htmlElementTdList.get(5).asText());//行政处罚决定书编号
            map.put("wfxw",htmlElementTdList.get(7).asText());//违法行为
            map.put("wfsj", htmlElementTdList.get(9).asText());// 违法时间
            map.put("wfdz", htmlElementTdList.get(13).asText());//违法地址
            map.put("cfje", htmlElementTdList.get(15).asText());//罚款金额
            map.put("wfjlf", htmlElementTdList.get(17).asText());//违法记分数
            map.put("cljgmc", htmlElementTdList.get(19).asText());//处理机关名称
            map.put("clsj", htmlElementTdList.get(21).asText());//处理时间
            map.put("jszh", htmlElementTdList.get(23).asText());//驾驶证号
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }
    // TODO 政府采购严重违法失信行为记录名单  未找到此主题
    /*public Map detailZFCGYZWFSXXWJLMD(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("dsr", htmlElementTdList.get(1).asText());//当事人
            map.put("hphm", htmlElementTdList.get(3).asText());//号牌号码
            map.put("xzcfjdsbh", htmlElementTdList.get(5).asText());//行政处罚决定书编号
            map.put("wfxw",htmlElementTdList.get(7).asText());//违法行为
            map.put("wfsj", htmlElementTdList.get(9).asText());// 违法时间
            map.put("wfdz", htmlElementTdList.get(13).asText());//违法地址
            map.put("cfje", htmlElementTdList.get(15).asText());//罚款金额
            map.put("wfjlf", htmlElementTdList.get(17).asText());//违法记分数
            map.put("cljgmc", htmlElementTdList.get(19).asText());//处理机关名称
            map.put("clsj", htmlElementTdList.get(21).asText());//处理时间
            map.put("jszh", htmlElementTdList.get(23).asText());//驾驶证号
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }
*/
    /**
     * 统计上严重失信企业
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=AB184264C9364046969B24B8AC6ABC51&infoType.id=8cc6867f2b2d4b038e29e1707e32aa16&type=2
     */
    public Map detailTJSYZSXQY(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("qymc", htmlElementTdList.get(1).asText());//企业名称
            map.put("cfjg", htmlElementTdList.get(3).asText());//处罚机关
            map.put("gsrq", htmlElementTdList.get(5).asText());//公示日期
            map.put("fddbr",htmlElementTdList.get(7).asText());//法定代表人或主要负责人
            map.put("tyshxydm", htmlElementTdList.get(9).asText());//统一社会信用代码（组织机构代码）
            map.put("qydz", htmlElementTdList.get(13).asText());//企业地址
            map.put("wfxw", htmlElementTdList.get(15).asText());//违法行为
            map.put("clqk", htmlElementTdList.get(19).asText());//处理情况
            map.put("sjly", htmlElementTdList.get(21).asText());//数据来源
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }

    /**
     * 广东省环境违法企业黑名单
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=AB184264C9364046969B24B8AC6ABC51&infoType.id=8cc6867f2b2d4b038e29e1707e32aa16&type=2
     */
    public Map detailGDSHJWFQYHMD(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("qymc", htmlElementTdList.get(1).asText());//企业名称
            map.put("fddbr",htmlElementTdList.get(3).asText());//法定代表人或主要负责人
            map.put("tyshxydm", htmlElementTdList.get(5).asText());//组织机构代码/工商登记号/统一社会信用代码
            map.put("qydz", htmlElementTdList.get(7).asText());//企业地址
            map.put("wfxw", htmlElementTdList.get(9).asText());//环境违法事实
            map.put("cfxdrq", htmlElementTdList.get(11).asText());//行政处罚决定书下达日期
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }
    /**
     * 涉金融黑名单
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=E666C9DEADFF4134A0B02372DD6E4804&infoType.id=9ca6df4a152f4f9d97b0ae8594a111e2&type=2
     */
    public Map detailSJRHMD(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("sxzt", htmlElementTdList.get(1).asText());//失信主体
            map.put("sf",htmlElementTdList.get(3).asText());//省份
            map.put("tyshxydm", htmlElementTdList.get(5).asText());//组织机构代码/工商登记号/统一社会信用代码
            map.put("ah", htmlElementTdList.get(7).asText());//案号
            map.put("pdzcjg", htmlElementTdList.get(9).asText());//判决做出机关
            map.put("fddbr", htmlElementTdList.get(11).asText());//法定代表人
            map.put("sjrhmdlx", htmlElementTdList.get(13).asText());//涉金融黑名单类型
            map.put("sxrlb", htmlElementTdList.get(15).asText());//失信人类别
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }
    /**
     * 重大税收违法案件信息
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=CCA4DEAD44E94EC59AACBF9E855E938E&infoType.id=D21DDEAFDB5C4BA2BC497FB61B68FDDB&type=2
     */
    public Map detailZDSSWFAJXX(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("nsrmc", htmlElementTdList.get(1).asText());//纳税人名称
            map.put("nsrsbh",htmlElementTdList.get(5).asText());//纳税人识别号
            map.put("tyshxydm", htmlElementTdList.get(7).asText());//组织机构代码/工商登记号/统一社会信用代码
            map.put("zcdz", htmlElementTdList.get(9).asText());//注册地址
            map.put("fddbr", htmlElementTdList.get(11).asText());//法定代表人
            map.put("fddbrxb", htmlElementTdList.get(13).asText());//法定代表人性别
            map.put("fddbrzjhm", htmlElementTdList.get(15).asText());//法定代表人证件号码
            map.put("zywfss", htmlElementTdList.get(17).asText());//主要违法事实
            map.put("xgflyjjswclcfqk", htmlElementTdList.get(19).asText());//相关法律依据及税务处理处罚情况
            map.put("ztlx", htmlElementTdList.get(21).asText());//主体类型
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }
    /**
     * 安全生产黑名单
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=3E75BCCD040448EF98477CD580A66572&infoType.id=af10eb42a27b4dd1bbac0495cf18c439&type=2
     */
    public Map detailAQSCHMD(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("dwmc", htmlElementTdList.get(1).asText());//单位名称
            map.put("tyshxydm", htmlElementTdList.get(5).asText());//组织机构代码/工商登记号/统一社会信用代码
            map.put("fddbr", htmlElementTdList.get(7).asText());//法定代表人
            map.put("fddbrzjhm", htmlElementTdList.get(11).asText());//法定代表人证件号码
            map.put("xxbsjg", htmlElementTdList.get(13).asText());//信息报送机关
            map.put("nrly", htmlElementTdList.get(17).asText());//相纳入理由
            map.put("sxxwjj", htmlElementTdList.get(19).asText());//失信行为简况
            map.put("zcdz", htmlElementTdList.get(21).asText());//注册地址
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }

    /**
     * 食品药品违法违规企业黑名单
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=934F64A46559485DABAE348EFD8F7967&infoType.id=bc74880ef8534758ae068aeb8871fc15&type=2
     */
    public Map detailSPYPWFWGQYHMD(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("qymc", htmlElementTdList.get(1).asText());//企业名称
            map.put("fddr", htmlElementTdList.get(3).asText());//单位地址
            map.put("dwdz", htmlElementTdList.get(5).asText());//单位地址
            map.put("zywfwgxw", htmlElementTdList.get(7).asText());//主要违法违规行为
            map.put("cfyj", htmlElementTdList.get(11).asText());//处罚依据
            map.put("cfjg", htmlElementTdList.get(13).asText());//处罚结果
            map.put("gbqx", htmlElementTdList.get(15).asText());//公布期限
            map.put("sacpmc", htmlElementTdList.get(17).asText());//涉案产品名称
            map.put("sapcbs", htmlElementTdList.get(19).asText());//涉案产品标识
            map.put("sacppzwh", htmlElementTdList.get(21).asText());//涉案产品批准文号
            map.put("sacpscxkzh", htmlElementTdList.get(23).asText());//涉案产品生产许可证号
            map.put("fbdw", htmlElementTdList.get(25).asText());//发布单位
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }
    /**
     * 重大税收违法案件信息(省地税)
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=15d71f88b6234220af4634d1f6ebbeef&infoType.id=5CAC6DDCFB5243C9B129841D3B39627A&type=6
     */
    public Map detailZDSHWFAJXXSDS(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("jcjg", htmlElementTdList.get(1).asText());//检查机关
            map.put("nsrmc", htmlElementTdList.get(3).asText());//纳税人名称
            map.put("nsrsbh", htmlElementTdList.get(5).asText());//纳税人识别号
            map.put("zzjgdm", htmlElementTdList.get(7).asText());//组织机构代码
            map.put("zcdz", htmlElementTdList.get(11).asText());//注册地址
            map.put("ajxz", htmlElementTdList.get(13).asText());//案件性质
            map.put("zywfss", htmlElementTdList.get(17).asText());//主要违法事实
            map.put("xgflyjjclcfqk", htmlElementTdList.get(19).asText());//相关法律依据及处理处罚情况
            map.put("gbjb", htmlElementTdList.get(21).asText());//公布级别
            map.put("gbrq", htmlElementTdList.get(23).asText());//公布时间
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }
    /**
     * 重大税收违法案件信息(省国税)
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=151C2C9722AB4E43B65D75515FF78208&infoType.id=e207eb6e64bd45efab895fdf3b0a94c6&type=6
     */
    public Map detailZDSHWFAJXXSGS(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("jcjg", htmlElementTdList.get(1).asText());//检查机关
            map.put("nsrmc", htmlElementTdList.get(3).asText());//纳税人名称
            map.put("nsrsbh", htmlElementTdList.get(5).asText());//纳税人识别号
            map.put("zzjgdm", htmlElementTdList.get(7).asText());//组织机构代码
            map.put("zcdz", htmlElementTdList.get(9).asText());//注册地址
            map.put("zjjg", htmlElementTdList.get(11).asText());//中介机构信息
            map.put("ajxz", htmlElementTdList.get(13).asText());//案件性质
            map.put("zywfss", htmlElementTdList.get(15).asText());//主要违法事实
            map.put("xgflyjjclcfqk", htmlElementTdList.get(17).asText());//相关法律依据及处理处罚情况
            map.put("gbjb", htmlElementTdList.get(19).asText());//公布级别
            map.put("gbrq", htmlElementTdList.get(21).asText());//公布时间
            map.put("xyzt", htmlElementTdList.get(23).asText());//信用状态
            map.put("tjsj", htmlElementTdList.get(27).asText());//添加时间

            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }

    /**
     * 药品经营企业信用等级评定(严重失信)
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=FD4C943273A347BF83D6B0C505BEB01F&infoType.id=d2895e0a037b42b49ec7fcb42a11e38d&type=6
     */
    public Map detailYPJYQYXYDJPDYZSX(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("qymc", htmlElementTdList.get(1).asText());//企业名称
            map.put("xkzh", htmlElementTdList.get(3).asText());//许可证号
            map.put("pddj", htmlElementTdList.get(5).asText());//评定等级
            map.put("sxrq", htmlElementTdList.get(7).asText());//生效日期
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }

    /**
     * 广东省地方税务局2016年第1号欠税公告名单
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=04F71764C5A1415FAB21D7A3E6EF5AA8&infoType.id=a6dc894db0f04b83940be0cf4cbe5ce5&type=6
     */
    public Map detailGDSDFSWJ2016ND1HHGGMD(String url,String publishDate){
        Map map = new HashMap();
        WebClient webClient = null;
        try {
            webClient = createWebClient("","");
            HtmlPage htmlPage = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            map.put("nsrmc", htmlElementTdList.get(1).asText());//纳税人名称
            map.put("nsrsbh", htmlElementTdList.get(5).asText());//纳税人识别号
            map.put("fddbr", htmlElementTdList.get(7).asText());//法定代表人
            map.put("jydz", htmlElementTdList.get(11).asText());//经营地址
            map.put("zsxmmc", htmlElementTdList.get(13).asText());//征收项目名称
            map.put("qse", htmlElementTdList.get(15).asText()+"元");//欠税额
            map.put("bqqse", htmlElementTdList.get(17).asText()+"元");//本期欠税额
            map.put("qylx", htmlElementTdList.get(19).asText());//企业类型
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }


}
