package com.mr.modules.api.site.instance.boissite;

import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *@ auther :zjxu
 *@ dateTime : 201803
 * 宁波保监局处罚 提取所需要的信息
 * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
 * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
 */
@Component("ningbo")
@Slf4j
@Scope("prototype")
public class SiteTaskImpl_BOIS_NingBo extends SiteTaskExtend{
    @Override
    protected String execute() throws Throwable {
//        String url = "http://ningbo.circ.gov.cn/web/site27/tab3466/module9866/page1.htm";
        List<String> urlList = extractPageUrlList();
        for(String urlResult : urlList){
            log.info("urlResult:"+urlResult);
            Map map = extractContent(getData(urlResult));
            getObj(map,urlResult);
        }
        return null;
    }

    /**  xtractPageAll,URl集合
     * @return*/

    public List extractPageUrlList(){
        List<String> urlList = new ArrayList<>();
        //第一个页面，用于获取总页数
        String baseUrl = "http://ningbo.circ.gov.cn/web/site27/tab3466/module9866/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        for(int i=1;i<=pageAll;i++){
            String url ="http://ningbo.circ.gov.cn/web/site27/tab3466/module9866/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                String resultUrl = "http://ningbo.circ.gov.cn"+elementUrl.attr("href");
                log.info("编号："+i+"==resultUrl:"+resultUrl);
                urlList.add(resultUrl);
            }
        }
        return urlList;
    }

    /** 获取保监会处罚列表所有页数
     * @param fullTxt
     * @return*/

    public int extractPage(String fullTxt){
        int pageAll = 1;
        Document doc = Jsoup.parse(fullTxt);
        Elements td = doc.getElementsByClass("Normal");
        //记录元素的数量
        int serialNo = td.size();
        pageAll = Integer.valueOf(td.get(serialNo-1).text().split("/")[1]);
        log.info("-------------********---------------");
        log.info("处罚列表清单总页数为："+pageAll);
        log.info("-------------********---------------");
        return  pageAll;
    }


    public Map extractContent(String fullTxt) {
        //发布机构
        String publishOrg = "中国保监会宁波监管局行政处";
        //发布时间
        String publishDate = "";
        //TODO 处罚机关
        String punishOrg ="";
        //TODO 处罚时间
        String punishDate = "";
        //TODO 处罚文号
        String  punishNo = "";
        //TODO 受处罚机构
        StringBuffer punishToOrg = new StringBuffer();
        //TODO 受处罚机构地址
        StringBuffer punishToOrgAddress = new StringBuffer();
        //TODO 法定代表人或主要负责人
        StringBuffer punishToOrgHolder = new StringBuffer();
        //TODO 受处罚当时人名称（自然人）
        StringBuffer priPerson =  new StringBuffer();
        //TODO 受处罚当时人证件号码（自然人）
        StringBuffer priPersonCert = new StringBuffer();
        //TODO 受处罚当时人职位（自然人）
        StringBuffer priJob = new StringBuffer();
        //TODO 受处罚当时人地址（自然人）
        StringBuffer priAddress = new StringBuffer();
        //TODO 判断处罚的是法人，还是自然人
        String priBusiType = "";
        //数据来源  TODO 来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）
        String source = "保监局";
        //主题 TODO 主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录
        String object = "行政处罚决定";
        //TODO 全文
        String stringDetail ="";
        Document doc = Jsoup.parse(fullTxt.replace("(","（")
                .replace(")","）"));
        Element elementsTxt = doc.getElementById("tab_content");
        //全文提取

        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Elements elementsSpan = elementsTxt.getElementsByClass("xilanwb");
        String txtAll = elementsSpan.text();
        stringDetail = txtAll;
        //TODO 正文
        String stringDetailTemp =txtAll
                .replace("：名称　","：")
                .replace("受处罚机构：","当事人：")
                .replace("受处罚单位：","当事人：")
                .replace("受处罚人：","当事人：")
                .replace("住址： ","地址：")
                .replace("地址","地址：")
                .replace("地址 ","地址：")
                .replace("住所地：","地址：")
                .replace("主要负责人：　","负责人：")
                .replace("法定代表人姓名：","负责人：")
                .replace("法定代表人：","负责人：")
                .replace("主要负责人：","负责人：")
                .replace("单位地址：","地址：")
                .replace("住所：","地址：")
                .replace("住址：","地址：")
                .replace("身份证号：","证件号码：")
                .replace("身份证号码：","证件号码：")
                .replace("：姓名 ","：")
                .replace("： 姓名","：")
                .replace("年龄","，年龄")
                .replace("：名称","：")
                .replace("：名称 ","：")
                .replace(" 月","月")
                .replace(" 日","日")
                .replace(" ","，")
                .replace("　","，")
                .replace("。","，")
                .replace("：","：")
                .replace(":","：")
                .replace("、","，")
                .replace("(","（")
                .replace(")","）")
                .replace(":","：")
                .replace(" ","，").replace("地址：：","地址：");

        log.info("stringDetailTemp:"+stringDetailTemp);
        /*TODO 通用型*/
        //TODO 提取主题
        Element elementsTitle = elementsTD.first();
        String titleStr = elementsTitle.text();
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+5,publishDateStr.indexOf("分享到："));
        String[] textStrArr = stringDetailTemp.split("，");
        List<String> textStrList = new ArrayList<>();
        for(String textStr : textStrArr ){
            textStr.trim();
            if(textStr.contains("当事人：")|| textStr.contains("负责人：")||textStr.contains("地址：")||textStr.contains("职务：")||textStr.contains("证件号码：")){
                textStrList.add(textStr);
            }
            if(textStr.contains("年")|| textStr.contains("月")||textStr.endsWith("日")){
                punishDate=textStr;
            }
            if(textStr.contains("保监罚")&&textStr.contains("号")){
                punishNo = textStr;
            }
            if(textStr.endsWith("保监局")){
                punishOrg = textStr;
            }
        }
        //判断是自然人(false)，还是法人(true)
        boolean pirBusFlag = false;
        for(String textStrListS : textStrList){
            String[] textStrListSArr = textStrListS.split("：");
            if(textStrListSArr.length>=2&&textStrListSArr[0].contains("当事人")&&textStrListSArr[1].length()>=6){
                punishToOrg.append(textStrListSArr[1]).append("，");
                pirBusFlag = true;
            }
            if(textStrListSArr.length>=2&&textStrListSArr[0].contains("当事人")&&textStrListSArr[1].length()<6){
                priPerson.append(textStrListSArr[1]).append("，");
                pirBusFlag = false;
            }
            if(pirBusFlag==true&&textStrListSArr.length>=2&&textStrListSArr[0].contains("地址")){
                punishToOrgAddress.append(textStrListSArr[1]).append("，");
            }
            if(pirBusFlag==true&&textStrListSArr.length>=2&&textStrListSArr[0].contains("负责人")){
                punishToOrgHolder.append(textStrListSArr[1]).append("，");
            }
            if(pirBusFlag==false&&textStrListSArr.length>=2&&textStrListSArr[0].contains("地址")){
                priAddress.append(textStrListSArr[1]).append("，");
            }
            if(pirBusFlag==false&&textStrListSArr.length>=2&&textStrListSArr[0].contains("职务")){
                priJob.append(textStrListSArr[1]).append("，");
            }
            if(pirBusFlag==false&&textStrListSArr.length>=2&&textStrListSArr[0].contains("证件号码")){
                priPersonCert.append(textStrListSArr[1]).append("，");
            }


        }
        if(punishOrg.equals("")){
            punishOrg ="宁波保监局";
        }
        if(punishNo.equals("")&&titleStr.contains("号")&&titleStr.contains("保监罚")){
            punishNo = titleStr.split("（")[1].split("）")[0];
        }
        /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/

        log.info("发布主题："+titleStr);
        log.info("发布机构："+publishOrg);
        log.info("发布时间："+publishDate);
        log.info("处罚机关："+punishOrg);
        log.info("处罚时间："+punishDate);
        log.info("处罚文号："+punishNo);
        log.info("受处罚机构："+punishToOrg);
        log.info("受处罚机构地址："+punishToOrgAddress);
        log.info("受处罚机构负责人："+punishToOrgHolder);
        log.info("受处罚人："+priPerson);
        log.info("受处罚人证件："+priPersonCert);
        log.info("受处罚人职位："+priJob);
        log.info("受处罚人地址："+priAddress);
        log.info("来源："+source);
        log.info("主题："+object);
        log.info("正文："+stringDetail);

        Map<String,String> map = new HashMap<String,String>();
        map.put("titleStr",titleStr);
        map.put("publishOrg",publishOrg);
        map.put("publishDate",publishDate);
        map.put("punishOrg",punishOrg);
        map.put("punishDate",punishDate);
        map.put("punishNo",punishNo);
        map.put("punishToOrg",punishToOrg.toString());
        map.put("punishToOrgAddress",punishToOrgAddress.toString());
        map.put("punishToOrgHolder",punishToOrgHolder.toString());
        map.put("priPerson",priPerson.toString());
        map.put("priPersonCert",priPersonCert.toString());
        map.put("priJob",priJob.toString());
        map.put("priAddress",priAddress.toString());
        map.put("source",source);
        map.put("object",object);
        map.put("stringDetail",stringDetail);

        return map;
    }
    /**
     * 获取Obj,并入库
     * */
    public FinanceMonitorPunish getObj(Map<String,String> mapInfo, String href){

        FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
        financeMonitorPunish.setPunishNo(mapInfo.get("punishNo"));//处罚文号
        financeMonitorPunish.setPunishTitle(mapInfo.get("titleStr"));//标题
        financeMonitorPunish.setPublisher(mapInfo.get("publishOrg"));//发布机构
        financeMonitorPunish.setPublishDate(mapInfo.get("publishDate"));//发布时间
        financeMonitorPunish.setPunishInstitution(mapInfo.get("punishOrg"));//处罚机关
        financeMonitorPunish.setPunishDate(mapInfo.get("punishDate"));//处罚时间
        financeMonitorPunish.setPartyInstitution(mapInfo.get("punishToOrg"));//当事人（公司）=处罚对象
        financeMonitorPunish.setDomicile(mapInfo.get("punishToOrgAddress"));//机构住址
        financeMonitorPunish.setLegalRepresentative(mapInfo.get("punishToOrgHolder"));//机构负责人
        financeMonitorPunish.setPartyPerson(mapInfo.get("priPerson"));//受处罚人
        financeMonitorPunish.setPartyPersonId(mapInfo.get("priPersonCert"));//受处罚人证件号码
        financeMonitorPunish.setPartyPersonTitle(mapInfo.get("priJob"));//职务
        financeMonitorPunish.setPartyPersonDomi(mapInfo.get("priAddress"));//自然人住址
        financeMonitorPunish.setDetails(mapInfo.get("stringDetail"));//详情
        financeMonitorPunish.setUrl(href);
        financeMonitorPunish.setSource(mapInfo.get("source"));
        financeMonitorPunish.setObject(mapInfo.get("object"));

        //保存入库
        saveOne(financeMonitorPunish,false);

        return financeMonitorPunish;
    }

}
