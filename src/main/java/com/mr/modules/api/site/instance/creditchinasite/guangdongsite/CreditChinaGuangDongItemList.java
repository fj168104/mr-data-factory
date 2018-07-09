package com.mr.modules.api.site.instance.creditchinasite.guangdongsite;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

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
 * url:
 * http://www.gdcredit.gov.cn/infoTypeAction!getInfoTypeList.do?type=2
 * http://www.gdcredit.gov.cn/infoTypeAction!getInfoTypeList.do?type=6
 * 获取页面题主List<Map>
 *     item
 *     source
 *     count
 *     publishDate
 */
@Component("creditchinaguangdongitemlist")
@Slf4j
@Scope("prototype")
public class CreditChinaGuangDongItemList extends SiteTaskExtend_CreditChina{
    @Override
    protected String execute() throws Throwable {
        String url2 = "http://www.gdcredit.gov.cn/infoTypeAction!getInfoTypeList.do?type=2";
        String url6 = "http://www.gdcredit.gov.cn/infoTypeAction!getInfoTypeList.do?type=6";
        webContext(url2);
        webContext(url6);
        return super.execute();
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    /**
     * 提取各主体信息
     */
    public void webContext(String url ){
        //
        //String urlTwo ="http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruelList.do?infoType.id=bc74880ef8534758ae068aeb8871fc15&dataCount=111&type=2";
        //String urlThree ="http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=28442BC9BC7449529E2AF8D357552E7F&infoType.id=3D8BD2F74D9E4D5FAC0F0D242CE72FD2&type=2";
        //ItemList(url);
        //ItemObjectList(urlTwo,"");
        //log.info(detailTJSYZSXQY(new WebClient(),urlThree,"").toString());
        try {
            List<Map> mapList = ItemList(createWebClient("",""),url);
            if(mapList.size()>0){
                for(Map map :mapList ){
                    ItemObjectList(createWebClient("",""),map.get("href").toString(),map.get("item").toString(),map.get("publishDate").toString());
                }
            }
        }catch (Throwable throwable){
            log.error("");
        }
    }
    /**
     * 第一步
     * 提取主题清单
     * @param url
     * @return
     */
    public List<Map> ItemList(WebClient webClient,String url){
        //翻页标识 false：没有下一页了 true：有下一页
        boolean naxtPageNextFlag = false;
        int pageSize = 1;
        //下一页对象载体
        HtmlAnchor htmlAnchorNextPageEvent = null;
        List<Map> mapList = new ArrayList<>();
        try {
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
     * 第二步
     * 提取每个主题下的对象，进行处理
     * @param webClient
     * @param url
     * @param publishDate
     */
    public void ItemObjectList(WebClient webClient,String url,String item,String publishDate){

        if(item.contains("统计上严重失信企业")||item.contains("安全生产黑名单")||item.contains("严重交通违法行为信息")||item.contains("广东省环境违法企业黑名单")||item.contains("涉金融黑名单")||item.contains("重大税收违法案件信息")||item.contains("食品药品违法违规企业黑名单")||item.contains("药品经营企业信用等级评定(严重失信)")||item.contains("广东省地方税务局2016年第1号欠税公告名单")){
            //翻页标识 false：没有下一页了 true：有下一页
            boolean naxtPageNextFlag = false;
            int pageSize = 1;
            //下一页对象载体
            HtmlAnchor htmlAnchorNextPageEvent = null;
            try {
                //用于打开详情界面 TODO 这个地方需要注意，如果不新建打开窗体，原来的窗体会被覆盖
                WebClient webClientDetail = createWebClient("","");
                HtmlPage htmlPage = webClient.getPage(url);
                log.info("\n******************************第 "+pageSize+" 页*********************************\n");
                //获取列表
                List<HtmlElement> htmlElementTbodyList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//form//div[@class='content-div']//div[@class='right_div']//div[@class='list_content list_content_continue']//table//tbody");
                //翻页
                List<HtmlElement> htmlElementNextList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//form//div[@class='content-div']//div[@class='right_div']//div[@class='page_div']//div[@class='pagination']");
                List<HtmlElement> htmlElementAList = htmlElementNextList.get(0).getElementsByTagName("a");

                //获取行
                for(HtmlElement htmlElementTbody:htmlElementTbodyList){
                    String href ="";
                    String itemObject ="";

                    List<HtmlElement> htmlElementsTr = htmlElementTbody.getElementsByTagName("tr");
                    //获取列
                    if(htmlElementsTr.size()==3){
                        Map mapResult = new HashMap();
                        //获取第二层链接地址与主题信息
                        HtmlAnchor htmlAnchor = (HtmlAnchor)htmlElementsTr.get(0).getElementsByTagName("a").get(0);
                        href = "http://www.gdcredit.gov.cn"+htmlAnchor.getAttribute("href");
                        itemObject = htmlAnchor.getAttribute("title");
                        log.info("itemObject:"+itemObject+"---------------href:"+href);
                        //获取详情页面信息
                        if(item.contains("安全生产黑名单")){
                            Map map = detailAQSCHMD(webClientDetail,href,publishDate);
                            //企业名称 失信行为 纳入理由
                            mapResult.put("enterpriseName",map.get("dwmc"));
                            mapResult.put("discreditAction",map.get("sxxwjj"));
                            mapResult.put("punishReason",map.get("nrly"));
                            mapResult.put("publishDate",map.get("publishDate"));
                        }else if(item.contains("统计上严重失信企业")){
                            Map map  = detailTJSYZSXQY(webClientDetail,href,publishDate);
                            //企业名称 违法行为 处罚情况 公示日期
                            mapResult.put("enterpriseName",map.get("qymc"));
                            mapResult.put("discreditAction",map.get("wfxw"));
                            mapResult.put("punishResult",map.get("clqk"));
                            mapResult.put("judgeDate",map.get("gsrq"));
                            mapResult.put("publishDate",map.get("publishDate"));
                        } else if(item.contains("严重交通违法行为信息（第9类）")){
                            Map map  = detailYZJTWFXWXX9Type(webClientDetail,href,publishDate);
                            //当事人 违法行为  行政处罚决定编号 违法时间
                            mapResult.put("enterpriseName",map.get("dsr"));
                            mapResult.put("discreditAction",map.get("wfxw"));
                            mapResult.put("judgeNo",map.get("xzcfjdsbh"));
                            mapResult.put("judgeDate",map.get("wfsj"));
                            mapResult.put("publishDate",map.get("publishDate"));
                        }else if(item.contains("严重交通违法行为信息（第4、5、6、7类）")||item.contains("严重交通违法行为信息（第11类）")){
                            Map map  = detailYZJTWFXWXX114567Type(webClientDetail,href,publishDate);
                            //当事人 违法行为  行政处罚决定编号 违法时间
                            mapResult.put("enterpriseName",map.get("dsr"));
                            mapResult.put("discreditAction",map.get("wfxw"));
                            mapResult.put("judgeNo",map.get("xzcfjdsbh"));
                            mapResult.put("judgeDate",map.get("wfsj"));
                            mapResult.put("publishDate",map.get("publishDate"));
                        }else if(item.contains("严重交通违法行为信息（第13类）")){
                            Map map  = detailYZJTWFXWXX13Type(webClientDetail,href,publishDate);
                            //当事人 违法行为  行政处罚决定编号 违法时间
                            mapResult.put("enterpriseName",map.get("dsr"));
                            mapResult.put("discreditAction",map.get("wfxw"));
                            mapResult.put("judgeNo",map.get("xzcfjdsbh"));
                            mapResult.put("judgeDate",map.get("wfsj"));
                            mapResult.put("publishDate",map.get("publishDate"));
                        }else if(item.contains("广东省环境违法企业黑名单")){
                            Map map  = detailGDSHJWFQYHMD(webClientDetail,href,publishDate);
                            //企业名称 违法行为 处罚日期
                            mapResult.put("enterpriseName",map.get("qymc"));
                            mapResult.put("discreditAction",map.get("wfxw"));

                        }else if(item.contains("涉金融黑名单")){
                            Map map   = detailSJRHMD(webClientDetail,href,publishDate);
                            //失信主体 案号  判决类型  失信类型
                            mapResult.put("enterpriseName",map.get("sxzt"));
                            mapResult.put("judgeNo",map.get("ah"));
                            mapResult.put("discreditAction",map.get("sjrhmdlx"));
                            mapResult.put("discreditType",map.get("zm"));
                        }else if(item.contains("重大税收违法案件信息")){
                            Map map = detailZDSSWFAJXX(webClientDetail,href,publishDate);
                            //纳税人名称 纳税人识别号 违法事实 处罚情况
                            mapResult.put("enterpriseName",map.get("nsrmc"));
                            mapResult.put("enterpriseCode1",map.get("nsrsbh"));
                            mapResult.put("discreditAction",map.get("zywfss"));
                            mapResult.put("punishResult",map.get("xgflyjjswclcfqk"));
                        }else if(item.contains("食品药品违法违规企业黑名单")){
                            Map map = detailSPYPWFWGQYHMD(webClientDetail,href,publishDate);
                            //企业名称 违法行为 处罚依据 处罚结果 公布期限
                            mapResult.put("enterpriseName",map.get("qymc"));
                            mapResult.put("discreditAction",map.get("zywfwgxw"));
                            mapResult.put("punishReason",map.get("cfyj"));
                            mapResult.put("punishResult",map.get("cfjg"));
                            //mapResult.put("punishResult",map.get("gbqx"));
                        }else if(item.contains("重大税收违法案件信息(省地税)")){
                            Map map = detailZDSSWFAJXXSDS(webClientDetail,href,publishDate);
                            //企业名称 违法行为 处罚情况 公示日期
                            mapResult.put("enterpriseName",map.get("nsrmc"));
                            mapResult.put("discreditAction",map.get("zywfss"));
                            mapResult.put("punishResult",map.get("xgflyjjclcfqk"));
                            mapResult.put("judgeDate",map.get("gbrq"));
                        }else if(item.contains("重大税收违法案件信息(省国税)")){
                            Map map = detailZDSSWFAJXXSGS(webClientDetail,href,publishDate);
                            //企业名称 违法行为 处罚情况 公示日期
                            mapResult.put("enterpriseName",map.get("nsrmc"));
                            mapResult.put("discreditAction",map.get("zywfss"));
                            mapResult.put("punishResult",map.get("xgflyjjclcfqk"));
                            mapResult.put("judgeDate",map.get("gbrq"));
                        }else if(item.contains("药品经营企业信用等级评定(严重失信)")){
                            Map map = detailYPJYQYXYDJPDYZSX(webClientDetail,href,publishDate);
                            //企业名称 评定等级 生效日期
                            mapResult.put("enterpriseName",map.get("qymc"));
                            //mapResult.put("XXXX",map.get("pddj"));
                            mapResult.put("judgeDate",map.get("sxrq"));
                        }else if(item.contains("广东省地方税务局2016年第1号欠税公告名单")){
                            Map map = detailGDSDFSWJ2016ND1HHGGMD(webClientDetail,href,publishDate);
                            //企业名称 征收项目名称 欠税额（元） 企业类型
                            mapResult.put("enterpriseName",map.get("nsrmc"));
                            //mapResult.put("XXXX",map.get("zsxmmc"));
                            //mapResult.put("XXXX",map.get("qse"));
                            //mapResult.put("XXXX",map.get("qylx"));

                        }else {
                            continue;
                        }
                        if(mapResult.size()>0){
                            mapResult.put("subject",item);
                            mapResult.put("sourceUrl",url);
                            mapResult.put("source","信用中国（广东）");
                            if(mapResult.get("enterpriseName").toString().length()<6){
                                mapResult.put("objectType","02");
                            }else{
                                mapResult.put("objectType","01");
                            }
                            discreditBlacklistInsert(mapResult);
                        }

                    }
                }

                if(htmlElementAList.size()>0){
                    for(HtmlElement htmlElementA : htmlElementAList){
                        Boolean flag = htmlElementA.getAttribute("class").equals("next");
                        if(flag){
                            naxtPageNextFlag =true;
                            htmlAnchorNextPageEvent = (HtmlAnchor) htmlElementA;
                        }
                    }
                }
                //递归翻页
                log.info("\n***************************************************************\n");
                while(naxtPageNextFlag){
                    //循环进入，间翻页标识先置位为 false
                    naxtPageNextFlag = false;
                    log.info("\n******************************第 "+ ++pageSize +" 页*********************************\n");
                    htmlPage = htmlAnchorNextPageEvent.click();
                    log.info("\n*************************htmlPage**************************************\n"+htmlPage.asXml());
                    List<HtmlElement> htmlElementTbodyListNext = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//form//div[@class='content-div']//div[@class='right_div']//div[@class='list_content list_content_continue']//table//tbody");
                    htmlElementNextList = htmlPage.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='credit-public']//form//div[@class='content-div']//div[@class='right_div']//div[@class='page_div']//div[@class='pagination']");
                    htmlElementAList = htmlElementNextList.get(0).getElementsByTagName("a");
                    //获取行
                    int row = htmlElementTbodyListNext.size();
                    log.info("\n*****************************行数**********************************\n"+row);
                    for(HtmlElement htmlElementTbody:htmlElementTbodyListNext){
                        Map mapResult = new HashMap();
                        String href ="";
                        String itemObject ="";
                        log.info("\n***************************************************************\n");
                        List<HtmlElement> htmlElementsTr = htmlElementTbody.getElementsByTagName("tr");
                        //获取列
                        if(htmlElementsTr.size()==3){
                            //获取第二层链接地址与主题信息
                            HtmlAnchor htmlAnchor = (HtmlAnchor)htmlElementsTr.get(0).getElementsByTagName("a").get(0);
                            href = "http://www.gdcredit.gov.cn"+htmlAnchor.getAttribute("href");
                            itemObject = htmlAnchor.getAttribute("title");
                            log.info("itemObject:"+itemObject+"---------------href:"+href);
                            //获取详情页面信息
                            if(item.contains("安全生产黑名单")){
                                Map map = detailAQSCHMD(webClientDetail,href,publishDate);
                                //企业名称 失信行为 纳入理由
                                mapResult.put("enterpriseName",map.get("dwmc"));
                                mapResult.put("discreditAction",map.get("sxxwjj"));
                                mapResult.put("punishReason",map.get("nrly"));
                                mapResult.put("publishDate",map.get("publishDate"));
                            }else if(item.contains("统计上严重失信企业")){
                                Map map  = detailTJSYZSXQY(webClientDetail,href,publishDate);
                                //企业名称 违法行为 处罚情况 公示日期
                                mapResult.put("enterpriseName",map.get("qymc"));
                                mapResult.put("discreditAction",map.get("wfxw"));
                                mapResult.put("punishResult",map.get("clqk"));
                                mapResult.put("judgeDate",map.get("gsrq"));
                                mapResult.put("publishDate",map.get("publishDate"));
                            } else if(item.contains("严重交通违法行为信息（第9类）")){
                                Map map  = detailYZJTWFXWXX9Type(webClientDetail,href,publishDate);
                                //当事人 违法行为  行政处罚决定编号 违法时间
                                mapResult.put("enterpriseName",map.get("dsr"));
                                mapResult.put("discreditAction",map.get("wfxw"));
                                mapResult.put("judgeNo",map.get("xzcfjdsbh"));
                                mapResult.put("judgeDate",map.get("wfsj"));
                                mapResult.put("publishDate",map.get("publishDate"));
                            }else if(item.contains("严重交通违法行为信息（第4、5、6、7类）")||item.contains("严重交通违法行为信息（第11类）")){
                                Map map  = detailYZJTWFXWXX114567Type(webClientDetail,href,publishDate);
                                //当事人 违法行为  行政处罚决定编号 违法时间
                                mapResult.put("enterpriseName",map.get("dsr"));
                                mapResult.put("discreditAction",map.get("wfxw"));
                                mapResult.put("judgeNo",map.get("xzcfjdsbh"));
                                mapResult.put("judgeDate",map.get("wfsj"));
                                mapResult.put("publishDate",map.get("publishDate"));
                            }else if(item.contains("严重交通违法行为信息（第13类）")){
                                Map map  = detailYZJTWFXWXX13Type(webClientDetail,href,publishDate);
                                //当事人 违法行为  行政处罚决定编号 违法时间
                                mapResult.put("enterpriseName",map.get("dsr"));
                                mapResult.put("discreditAction",map.get("wfxw"));
                                mapResult.put("judgeNo",map.get("xzcfjdsbh"));
                                mapResult.put("judgeDate",map.get("wfsj"));
                                mapResult.put("publishDate",map.get("publishDate"));
                            }else if(item.contains("广东省环境违法企业黑名单")){
                                Map map  = detailGDSHJWFQYHMD(webClientDetail,href,publishDate);
                                //企业名称 违法行为 处罚日期
                                mapResult.put("enterpriseName",map.get("qymc"));
                                mapResult.put("discreditAction",map.get("wfxw"));
                            }else if(item.contains("涉金融黑名单")){
                                Map map   = detailSJRHMD(webClientDetail,href,publishDate);
                                //失信主体 案号  判决类型  失信类型
                                mapResult.put("enterpriseName",map.get("sxzt"));
                                mapResult.put("judgeNo",map.get("ah"));
                                mapResult.put("discreditAction",map.get("sjrhmdlx"));
                                mapResult.put("discreditType",map.get("zm"));
                            }else if(item.contains("重大税收违法案件信息")){
                                Map map = detailZDSSWFAJXX(webClientDetail,href,publishDate);
                                //纳税人名称 纳税人识别号 违法事实 处罚情况
                                mapResult.put("enterpriseName",map.get("nsrmc"));
                                mapResult.put("enterpriseCode1",map.get("nsrsbh"));
                                mapResult.put("discreditAction",map.get("zywfss"));
                                mapResult.put("punishResult",map.get("xgflyjjswclcfqk"));
                            }else if(item.contains("食品药品违法违规企业黑名单")){
                                Map map = detailSPYPWFWGQYHMD(webClientDetail,href,publishDate);
                                //企业名称 违法行为 处罚依据 处罚结果 公布期限
                                mapResult.put("enterpriseName",map.get("qymc"));
                                mapResult.put("discreditAction",map.get("zywfwgxw"));
                                mapResult.put("punishReason",map.get("cfyj"));
                                mapResult.put("punishResult",map.get("cfjg"));
                                //mapResult.put("punishResult",map.get("gbqx"));
                            }else if(item.contains("重大税收违法案件信息(省地税)")){
                                Map map = detailZDSSWFAJXXSDS(webClientDetail,href,publishDate);
                                //企业名称 违法行为 处罚情况 公示日期
                                mapResult.put("enterpriseName",map.get("nsrmc"));
                                mapResult.put("discreditAction",map.get("zywfss"));
                                mapResult.put("punishResult",map.get("xgflyjjclcfqk"));
                                mapResult.put("judgeDate",map.get("gbrq"));
                            }else if(item.contains("重大税收违法案件信息(省国税)")){
                                Map map = detailZDSSWFAJXXSGS(webClientDetail,href,publishDate);
                                //企业名称 违法行为 处罚情况 公示日期
                                mapResult.put("enterpriseName",map.get("nsrmc"));
                                mapResult.put("discreditAction",map.get("zywfss"));
                                mapResult.put("punishResult",map.get("xgflyjjclcfqk"));
                                mapResult.put("judgeDate",map.get("gbrq"));
                            }else if(item.contains("药品经营企业信用等级评定(严重失信)")){
                                Map map = detailYPJYQYXYDJPDYZSX(webClientDetail,href,publishDate);
                                //企业名称 评定等级 生效日期
                                mapResult.put("enterpriseName",map.get("qymc"));
                                //mapResult.put("XXXX",map.get("pddj"));
                                mapResult.put("judgeDate",map.get("sxrq"));
                            }else if(item.contains("广东省地方税务局2016年第1号欠税公告名单")){
                                Map map = detailGDSDFSWJ2016ND1HHGGMD(webClientDetail,href,publishDate);
                                //企业名称 征收项目名称 欠税额（元） 企业类型
                                mapResult.put("enterpriseName",map.get("nsrmc"));
                                //mapResult.put("XXXX",map.get("zsxmmc"));
                                //mapResult.put("XXXX",map.get("qse"));
                                //mapResult.put("XXXX",map.get("qylx"));
                            }else {
                                continue;
                            }
                            if(mapResult.size()>0){
                                mapResult.put("subject",item);
                                mapResult.put("sourceUrl",url);
                                mapResult.put("source","信用中国（广东）");
                                if(mapResult.get("enterpriseName").toString().length()<6){
                                    mapResult.put("objectType","02");
                                }else{
                                    mapResult.put("objectType","01");
                                }
                                discreditBlacklistInsert(mapResult);
                            }
                        }
                    }

                    if(htmlElementAList.size()>0){
                        for(HtmlElement htmlElementA : htmlElementAList){
                            if(htmlElementA.getAttribute("class").equals("next")){
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
    }

    /**
     * 食品药品违法违规企业黑名单
     * @param url
     * @param publishDate
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=3770DE60F74B4C4F9522A7EF2E03A92D&infoType.id=bc74880ef8534758ae068aeb8871fc15&type=2
     */
    public Map detailSPYPWFWGQYHMD(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("企业名称")) {
                    map.put("qymc", value.get(j));//企业名称
                } else if (lable.get(j).contains("法人代表")) {
                    map.put("frdb", value.get(j));//法人代表
                }else if (lable.get(j).contains("单位地址")) {
                    map.put("dwdz", value.get(j));//单位地址
                }else if (lable.get(j).contains("主要违法违规行为")) {
                    map.put("zywfwgxw",value.get(j));//主要违法违规行为
                }else if (lable.get(j).contains("处罚依据")) {
                    map.put("cfyj",value.get(j));// 处罚依据
                }else if (lable.get(j).contains("处罚结果")) {
                    map.put("cfjg", value.get(j));//处罚结果
                }else if (lable.get(j).contains("公布期限")) {
                    map.put("gbqx", value.get(j));//公布期限
                }else if (lable.get(j).contains("涉案产品名称")) {
                    map.put("sacpmc", value.get(j));//涉案产品名称
                }else if (lable.get(j).contains("涉案产品标识")) {
                    map.put("sacpbs", value.get(j));//涉案产品标识
                }else if (lable.get(j).contains("涉案产品生产许可证号")) {
                    map.put("sacpxkzh", value.get(j));//涉案产品生产许可证号
                }else if (lable.get(j).contains("涉案产品批准文号")) {
                    map.put("sacppzwh", value.get(j));//涉案产品批准文号
                }else if (lable.get(j).contains("发布单位")) {
                    map.put("fbdw", value.get(j)); //发布单位
                }
            }

            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }

    /**
     * 严重交通违法行为信息（含第9、11、4、5、6、7、13类） TODO 第9类
     * @param url
     * @param publishDate
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=883F79A1FCA14EF2AD169EB7E7A3D44D&infoType.id=00C4F00DAF35452F8D288FEFAC7CD8D3&type=2
     */
    public Map detailYZJTWFXWXX9Type(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++){
                if(lable.get(j).contains("当事人")){
                    map.put("dsr", value.get(j));//当事人
                }else if(lable.get(j).contains("号牌号码")){
                    map.put("hphm", value.get(j));//号牌号码
                }else if(lable.get(j).contains("行政处罚决定书编号")){
                    map.put("xzcfjdsbh", value.get(j));//行政处罚决定书编号
                }else if(lable.get(j).contains("违法行为")){
                    map.put("wfxw",value.get(j));//违法行为
                }else if(lable.get(j).contains("违法时间")){
                    map.put("wfsj", value.get(j));// 违法时间
                }else if(lable.get(j).contains("违法地址")){
                    map.put("wfdz", value.get(j));//违法地址
                }else if(lable.get(j).contains("罚款金额")){
                    map.put("cfje", value.get(j));//罚款金额
                }else if(lable.get(j).contains("违法记分数")){
                    map.put("wfjlf", value.get(j));//
                }else if(lable.get(j).contains("处理机关名称")){
                    map.put("cljgmc",value.get(j));//处理机关名称
                }else if(lable.get(j).contains("处理时间")){
                    map.put("clsj", value.get(j));//处理时间
                }else if(lable.get(j).contains("驾驶证号")){
                    map.put("jszh", value.get(j));//驾驶证号
                }else {
                    continue;
                }
            }

            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }
        return map;
    }
    /**
     * 严重交通违法行为信息（含第9、11、4、5、6、7、13类）TODO 第11、、4、5、6、7 类
     * @param url
     * @param publishDate
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=54BE745633284D998921336FAF221B2E&infoType.id=73EC277E42224876AB8A725230AC7419&type=2
     */
    public Map detailYZJTWFXWXX114567Type(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++){
                if(lable.get(j).contains("当事人")){
                    map.put("dsr", value.get(j));//当事人
                }else if(lable.get(j).contains("号牌号码")){
                    map.put("hphm", value.get(j));//号牌号码
                }else if(lable.get(j).contains("行政处罚决定书编号")){
                    map.put("xzcfjdsbh", value.get(j));//行政处罚决定书编号
                }else if(lable.get(j).contains("违法行为")){
                    map.put("wfxw",value.get(j));//违法行为
                }else if(lable.get(j).contains("违法时间")){
                    map.put("wfsj", value.get(j));// 违法时间
                }else if(lable.get(j).contains("违法地址")){
                    map.put("wfdz", value.get(j));//违法地址
                }else if(lable.get(j).contains("罚款金额")){
                    map.put("cfje", value.get(j));//罚款金额
                }else if(lable.get(j).contains("违法记分数")){
                    map.put("wfjlf", value.get(j));//
                }else if(lable.get(j).contains("车辆所有人")){
                    map.put("clsyr", value.get(j));//车辆所有人
                }else if(lable.get(j).contains("处理机关名称")){
                    map.put("cljgmc",value.get(j));//处理机关名称
                }else if(lable.get(j).contains("处理时间")){
                    map.put("clsj", value.get(j));//处理时间
                }else if(lable.get(j).contains("驾驶证号")){
                    map.put("jszh", value.get(j));//驾驶证号
                }else {
                    continue;
                }
            }

            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }
    /**
     * 严重交通违法行为信息（含第9、11、4、5、6、7、13类）TODO 第13 类
     * @param url
     * @param publishDate
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=A5EF869E304040719ECD00F53219DC61&infoType.id=5B1302FE753142318AF9AB1179F004F9&type=2
     */
    public Map detailYZJTWFXWXX13Type(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++){
                if(lable.get(j).contains("姓名")){
                    map.put("xm", value.get(j));//姓名
                }else if(lable.get(j).contains("号牌号码")){
                    map.put("hphm", value.get(j));//号牌号码
                }else if(lable.get(j).contains("事故发生时间")){
                    map.put("sgfssj", value.get(j));//事故发生时间
                }else if(lable.get(j).contains("事故地点")){
                    map.put("sgdd",value.get(j));//事故地点
                }else if(lable.get(j).contains("车辆所有人")){
                    map.put("clsyr", value.get(j));//车辆所有人
                }else if(lable.get(j).contains("办案机关")){
                    map.put("bajg",value.get(j));//办案机关
                }else if(lable.get(j).contains("身份证明号码")){
                    map.put("sfzmhm", value.get(j));//身份证明号码
                }else {
                    continue;
                }
            }

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
    }
*/
    /**
     * 统计上严重失信企业
     * @param url
     * @param publishDate
     * @return
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=28442BC9BC7449529E2AF8D357552E7F&infoType.id=3D8BD2F74D9E4D5FAC0F0D242CE72FD2&type=2
     */
    public Map detailTJSYZSXQY(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("企业名称")) {
                    map.put("qymc", value.get(j));//企业名称
                }else if (lable.get(j).contains("处罚机关")) {
                    map.put("cfjg", value.get(j));//处罚机关
                }else if (lable.get(j).contains("公示日期")) {
                    map.put("gsrq", value.get(j));//公示日期
                }else if (lable.get(j).contains("法定代表人")) {
                    map.put("fddbr",value.get(j));//法定代表人或主要负责人
                }else if (lable.get(j).contains("统一社会信用代码")) {
                    map.put("tyshxydm", value.get(j));//统一社会信用代码（组织机构代码）
                }else if (lable.get(j).contains("企业地址")) {
                    map.put("qydz", value.get(j));//企业地址
                }else if (lable.get(j).contains("违法行为")) {
                    map.put("wfxw", value.get(j));//违法行为
                }else if (lable.get(j).contains("处理情况")) {
                    map.put("clqk", value.get(j));//处理情况
                }else if (lable.get(j).contains("数据来源")) {
                    map.put("sjly", value.get(j));//数据来源
                }
            }
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
     * 案例：http://www.gdcredit.gov.cn/infoTypeAction!getAwardAndGruel.do?id=B28D01F442B64B25A05CA24B3BE32337&infoType.id=8cc6867f2b2d4b038e29e1707e32aa16&type=2
     */
    public Map detailGDSHJWFQYHMD(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("企业名称")) {
                    map.put("qymc", value.get(j));//企业名称
                } else if (lable.get(j).contains("法定代表人")) {
                    map.put("fddbr", value.get(j));//法定代表人或主要负责人
                }else if (lable.get(j).contains("组织机构代码")) {
                    map.put("tyshxydm", value.get(j));//组织机构代码/工商登记号/统一社会信用代码
                }else if (lable.get(j).contains("地址")) {
                    map.put("qydz", value.get(j));//企业地址
                }else if (lable.get(j).contains("环境违法事实")) {
                    map.put("wfxw", value.get(j));//环境违法事实
                }else if (lable.get(j).contains("行政处罚决定书下达日期")) {
                    map.put("cfxdrq", value.get(j));//行政处罚决定书下达日期
                }
            }
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
     * 注：对公与个人内容有差异，需要做特殊处理
     */
    public Map detailSJRHMD(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            //需要判断是企业还是个人 true:企业；false：个人
            boolean itemFlag = true;
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("失信主体")) {
                    map.put("sxzt", value.get(j));//失信主体
                    if(value.get(j).trim().length()<6){
                        itemFlag = false;
                    }
                }else if (lable.get(j).contains("省份")) {
                    map.put("sf",value.get(j));//省份
                }else if (lable.get(j).contains("组织机构代码")) {
                    map.put("zjjgdm",value.get(j));//组织机构代码
                }else if (lable.get(j).contains("工商登记号")) {
                    map.put("gsdjh",value.get(j));//工商登记号
                }else if (lable.get(j).contains("统一社会信用代码")) {
                    map.put("tyshxydm",value.get(j));//统一社会信用代码
                }
                else if (lable.get(j).contains("案号")) {
                    map.put("ah", value.get(j));//案号
                }else if (lable.get(j).contains("判决做出机关")) {
                    map.put("pdzcjg", value.get(j));//判决做出机关
                }else if (lable.get(j).contains("法定代表人")) {
                    map.put("fddbr", value.get(j));//法定代表人
                }else if (lable.get(j).contains("涉金融黑名单类型")) {
                    map.put("sjrhmdlx", value.get(j));//涉金融黑名单类型
                }else if (lable.get(j).contains("失信人类别")) {
                    map.put("sxrlb", value.get(j));//失信人类别
                }else if (lable.get(j).contains("罪名")&&!itemFlag) {
                    map.put("zm", value.get(j));//罪名
                } else if (lable.get(j).contains("自然人身份证号")&&!itemFlag) {
                    map.put("zrrsfzh", value.get(j));//自然人身份证号
                }else{
                    continue;
                }
            }

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
    public Map detailZDSSWFAJXX(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("纳税人名称")) {
                    map.put("nsrmc", value.get(j));//纳税人名称
                }else if(lable.get(j).contains("纳税人识别号")){
                    map.put("nsrsbh",value.get(j));//纳税人识别号
                }else if(lable.get(j).contains("组织机构代码")){
                    map.put("zjjgdm", value.get(j));//组织机构代码
                }else if(lable.get(j).contains("工商登记号")){
                    map.put("gsdjh", value.get(j));//工商登记号
                }else if(lable.get(j).contains("统一社会信用代码")){
                    map.put("tyshxydm", value.get(j));//统一社会信用代码
                }else if(lable.get(j).contains("注册地址")){
                    map.put("zcdz", value.get(j));//注册地址
                }else if(lable.get(j).contains("法定代表人")){
                    map.put("fddbr", value.get(j));//法定代表人
                }else if(lable.get(j).contains("性别")){
                    map.put("fddbrxb", value.get(j));//法定代表人性别
                }else if(lable.get(j).contains("证件号码")){
                    map.put("fddbrzjhm", value.get(j));//法定代表人证件号码
                }else if(lable.get(j).contains("主要违法事实")){
                    map.put("zywfss", value.get(j));//主要违法事实
                }else if(lable.get(j).contains("相关法律依据及税务处理处罚情况")){
                    map.put("xgflyjjswclcfqk", value.get(j));//相关法律依据及税务处理处罚情况
                }else if(lable.get(j).contains("主体类型")){
                    map.put("ztlx", value.get(j));//主体类型
                }
            }
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
    public Map detailAQSCHMD(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("单位名称")) {
                    map.put("dwmc", value.get(j));//单位名称
                } else if (lable.get(j).contains("组织机构代码")) {
                    map.put("tyshxydm", value.get(j));//组织机构代码
                }else if (lable.get(j).contains("工商登记号")) {
                    map.put("gsdjh", value.get(j));//工商登记号
                }else if (lable.get(j).contains("统一社会信用代码")) {
                    map.put("tyshxydm", value.get(j));//统一社会信用代码
                }else if (lable.get(j).contains("主要负责人")) {
                    map.put("fddbr", value.get(j));//主要负责人
                }else if (lable.get(j).contains("身份证号")) {
                    map.put("fddbrzjhm", value.get(j));//身份证号
                }else if (lable.get(j).contains("信息报送机关")) {
                    map.put("xxbsjg", value.get(j));//信息报送机关
                }else if (lable.get(j).contains("纳入理由")) {
                    map.put("nrly", value.get(j));//纳入理由
                }else if (lable.get(j).contains("失信行为简况")) {
                    map.put("sxxwjj", value.get(j));//失信行为简况
                }else if (lable.get(j).contains("注册地址")) {
                    map.put("zcdz", value.get(j));//注册地址
                }else {
                    continue;
                }
            }
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
    public Map detailZDSSWFAJXXSDS(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("检查机关")) {
                    map.put("jcjg", value.get(j));//检查机关
                } else if (lable.get(j).contains("纳税人名称")) {
                    map.put("nsrmc", value.get(j));//纳税人名称
                }else if (lable.get(j).contains("纳税人识别号")) {
                    map.put("nsrsbh", value.get(j));//纳税人识别号
                }else if (lable.get(j).contains("组织机构代码")) {
                    map.put("zzjgdm", value.get(j));//组织机构代码
                }else if (lable.get(j).contains("注册地址")) {
                    map.put("zcdz", value.get(j));//注册地址
                }else if (lable.get(j).contains("案件性质")) {
                    map.put("ajxz", value.get(j));//案件性质
                }else if (lable.get(j).contains("主要违法事实")) {
                    map.put("zywfss", value.get(j));//主要违法事实
                }else if (lable.get(j).contains("相关法律依据及处理处罚情况")) {
                    map.put("xgflyjjclcfqk", value.get(j));//相关法律依据及处理处罚情况
                }else if (lable.get(j).contains("公布级别")) {
                    map.put("gbjb", value.get(j));//公布级别
                }else if (lable.get(j).contains("公布时间")) {
                    map.put("gbrq", value.get(j));//公布时间
                }else if (lable.get(j).contains("信用状态")) {
                    map.put("xyzt", value.get(j));//信用状态
                }else if (lable.get(j).contains("添加时间")) {
                    map.put("tjsj", value.get(j));//添加时间
                }else {
                    continue;
                }
            }


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
    public Map detailZDSSWFAJXXSGS(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("检查机关")) {
                    map.put("jcjg", value.get(j));//检查机关
                } else if (lable.get(j).contains("纳税人名称")) {
                    map.put("nsrmc", value.get(j));//纳税人名称
                }else if (lable.get(j).contains("纳税人识别号")) {
                    map.put("nsrsbh", value.get(j));//纳税人识别号
                }else if (lable.get(j).contains("组织机构代码")) {
                    map.put("zzjgdm", value.get(j));//组织机构代码
                }else if (lable.get(j).contains("注册地址")) {
                    map.put("zcdz", value.get(j));//注册地址
                }else if (lable.get(j).contains("中介机构信息")) {
                    map.put("zjjg", value.get(j));//中介机构信息
                }else if (lable.get(j).contains("案件性质")) {
                    map.put("ajxz", value.get(j));//案件性质
                }else if (lable.get(j).contains("主要违法事实")) {
                    map.put("zywfss", value.get(j));//主要违法事实
                }else if (lable.get(j).contains("相关法律依据及处理处罚情况")) {
                    map.put("xgflyjjclcfqk", value.get(j));//相关法律依据及处理处罚情况
                }else if (lable.get(j).contains("公布级别")) {
                    map.put("gbjb", value.get(j));//公布级别
                }else if (lable.get(j).contains("公布时间")) {
                    map.put("gbrq", value.get(j));//公布时间
                }else {
                    continue;
                }
            }

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
    public Map detailYPJYQYXYDJPDYZSX(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("企业名称")) {
                    map.put("qymc", value.get(j));//企业名称
                } else if (lable.get(j).contains("纳税人名称")) {
                    map.put("xkzh", value.get(j));//许可证号
                }else if (lable.get(j).contains("纳税人名称")) {
                    map.put("pddj", value.get(j));//评定等级
                }else if (lable.get(j).contains("纳税人名称")) {
                    map.put("sxrq", value.get(j));//生效日期
                }
            }

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
    public Map detailGDSDFSWJ2016ND1HHGGMD(WebClient webClient,String url,String publishDate){
        Map map = new HashMap();
        try {
            HtmlPage htmlPageDetail = webClient.getPage(url);
            List<HtmlElement> htmlElementTdList = htmlPageDetail.getByXPath("//body//div[@class='page-outside']//div[@class='page-inside']//div[@class='pageFragment_bg_mid']//div[@class='content-other']//div[@class='data']//div[@class='content']//table//tbody//tr//td");
            List<String> lable = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for(int i=0;i<htmlElementTdList.size();i++){
                if(i%2==0){
                    lable.add(htmlElementTdList.get(i).asText());
                }
                if(i%2==1){
                    value.add(htmlElementTdList.get(i).asText());
                }
            }
            for(int j=0;j<lable.size();j++) {
                if (lable.get(j).contains("纳税人名称")) {
                    map.put("nsrmc", value.get(j));//纳税人名称
                } else if (lable.get(j).contains("纳税人识别号")) {
                    map.put("nsrsbh", value.get(j));//纳税人识别号
                }else if (lable.get(j).contains("法定代表人")) {
                    map.put("fddbr", value.get(j));//法定代表人
                }else if (lable.get(j).contains("经营地址")) {
                    map.put("jydz", value.get(j));//经营地址
                }else if (lable.get(j).contains("征收项目名称")) {
                    map.put("zsxmmc", value.get(j));//征收项目名称
                }else if (lable.get(j).contains("欠税额")) {
                    map.put("qse", value.get(j)+"元");//欠税额
                }else if (lable.get(j).contains("本期欠税额")) {
                    map.put("bqqse", value.get(j)+"元");//本期欠税额
                }else if (lable.get(j).contains("本期欠税额")) {
                    map.put("qylx", value.get(j));//企业类型
                }
            }
            map.put("publishDate", publishDate);//发布时间
        }catch (Throwable throwable){
            log.info("WebClient 创建异常，请检查···"+throwable.getMessage());
        }finally {
            webClient.close();
        }

        return map;
    }

    /**
     * 数据入口保存
     * @param map
     * @return
     */
    public DiscreditBlacklist discreditBlacklistInsert(Map<String,String> map){
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();
        //created_at	本条记录创建时间
        discreditBlacklist.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        discreditBlacklist.setUpdatedAt(new Date());
        //source	数据来源
        discreditBlacklist.setSource(map.get("source")==null?"":map.get("source"));
        //subject	主题
        discreditBlacklist.setSubject(map.get("subject")==null?"":map.get("subject"));
        //url	url
        discreditBlacklist.setUrl(map.get("sourceUrl")==null?"":map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        discreditBlacklist.setObjectType(map.get("objectType")==null?"":map.get("objectType"));
        //enterprise_name	企业名称
        discreditBlacklist.setEnterpriseName(map.get("enterpriseName")==null?"":map.get("enterpriseName"));
        //enterprise_code1	统一社会信用代码
        discreditBlacklist.setEnterpriseCode1(map.get("enterpriseCode1")==null?"":map.get("enterpriseName1"));
        //enterprise_code2	营业执照注册号
        discreditBlacklist.setEnterpriseCode2(map.get("enterpriseCode2")==null?"":map.get("enterpriseName2"));
        //enterprise_code3	组织机构代码
        discreditBlacklist.setEnterpriseCode3(map.get("enterpriseCode3")==null?"":map.get("enterpriseName3"));
        //person_name	法定代表人/负责人姓名|负责人姓名
        discreditBlacklist.setPersonName(map.get("personName")==null?"":map.get("personName"));
        //person_id	法定代表人身份证号|负责人身份证号
        discreditBlacklist.setPersonId(map.get("personId")==null?"":map.get("personId"));
        //discredit_type	失信类型
        discreditBlacklist.setDiscreditType(map.get("discreditType")==null?"":map.get("discreditType"));
        //discredit_action	失信行为
        discreditBlacklist.setDiscreditAction(map.get("discreditAction")==null?"":map.get("discreditAction"));
        //punish_reason	列入原因
        discreditBlacklist.setPunishReason(map.get("punishReason")==null?"":map.get("punishReason"));
        //punish_result	处罚结果
        discreditBlacklist.setPunishResult(map.get("punishResult")==null?"":map.get("punishResult"));
        //judge_no	执行文号
        discreditBlacklist.setJudgeNo(map.get("judgeNo")==null?"":map.get("judgeNo"));
        //judge_date	执行时间
        discreditBlacklist.setJudgeDate(map.get("judgeDate")==null?"":map.get("judgeDate"));
        //judge_auth	判决机关
        discreditBlacklist.setJudgeAuth(map.get("judgeAuth")==null?"":map.get("judgeAuth"));
        //publish_date	发布日期
        discreditBlacklist.setPublishDate(map.get("publishDate")==null?"":map.get("publishDate"));
        //status	当前状态
        discreditBlacklist.setStatus(map.get("status")==null?"":map.get("status"));
        saveDisneycreditBlackListOne(discreditBlacklist,false);
        return discreditBlacklist;
    }


}
