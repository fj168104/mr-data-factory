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

@Component("ningxia")
@Slf4j
@Scope("prototype")
public class SiteTaskImpl_BOIS_NingXia extends SiteTaskExtend {
    /*@Override
    protected String execute() throws Throwable {
        String url ="http://ningxia.circ.gov.cn/web/site28/tab3598/info4085456.htm";
        extractContent(getData(url));
        return null;
    }*/

    @Override
    protected String execute() throws Throwable {
        List<String> urlList = extractPageUrlList();
        for(String urlResult : urlList){
            log.info("urlResult:"+urlResult);
            Map mapInfo = extractContent(getData(urlResult));
            getObj(mapInfo,urlResult);

        }
        return null;
    }
    /**  xtractPageAll,URl集合
     * @return*/

    public List extractPageUrlList(){
        List<String> urlList = new ArrayList<>();
        //第一个页面，用于获取总页数
        String baseUrl = "http://ningxia.circ.gov.cn/web/site28/tab3598/module9872/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        for(int i=1;i<=pageAll;i++){
            String url ="http://ningxia.circ.gov.cn/web/site28/tab3598/module9872/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                String resultUrl = "http://ningxia.circ.gov.cn"+elementUrl.attr("href");
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
        String publishOrg = "中国保监会宁夏保监局行政处";
        //发布时间
        String publishDate = "";
        //TODO 处罚机关
        String punishOrg ="";
        //TODO 处罚时间
        String punishDate = "";
        //TODO 处罚文号
        String  punishNo = "";
        //TODO 受处罚机构
        String punishToOrg = "";
        //TODO 受处罚机构地址
        String punishToOrgAddress = "";
        //TODO 法定代表人或主要负责人
        String punishToOrgHolder = "";
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
        Document doc = Jsoup.parse(fullTxt.replace("、","，").replace("(","（").
                replace(")","）").replace(":","：").replace("&nbps;","").replace(" ",""));
        Element elementsTxt = doc.getElementById("tab_content");
        //全文提取
        String txtAll = elementsTxt.text();
        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Elements elementsSpan = elementsTxt.getElementsByClass("xilanwb");
        //TODO 正文
        stringDetail =elementsSpan.text().replaceAll("当 事 人","当事人")
                .replaceAll(":","：")
                .replaceAll("&nbps;","")
                .replaceAll(" ","")
                .replaceAll("职 务","职务")
                .replaceAll("住 所","地址")
                .replaceAll("地 址","地址")
                .replaceAll("受处罚人名称","当事人")
                .replaceAll("受处罚人：名称","当事人")
                .replaceAll("法定代表人姓名","法定代表人")
                .replaceAll("主要负责人","法定代表人");
        log.info("stringDetail:"+stringDetail);
        /*TODO 通用型*/
        //TODO 提取主题
        Element elementsTitle = elementsTD.first();
        String titleStr = elementsTitle.text();
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+5,publishDateStr.indexOf("分享到："));



        /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/
        if(stringDetail.indexOf("当事人：")>-1){
            //TODO 正文中没有文号
            String[] punishNoStr = titleStr.split("（");
            if(punishNoStr.length==2 && titleStr.contains("号")){
                punishNo ="（"+punishNoStr[1];
            }else if(stringDetail.contains("行政处罚决定书") && stringDetail.contains("号") && stringDetail.indexOf("号")< stringDetail.indexOf("当事人")){
                punishNo = stringDetail.substring(stringDetail.indexOf("行政处罚决定书")+7,stringDetail.indexOf("当事人")).replace((char) 12288, ' ').trim();
            }


            //TODO 判断是否为自然人
            if(stringDetail.indexOf("身份证号：")>-1){
                if(stringDetail.indexOf("职务：")>-1){
                    priPerson.append(stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("身份证号："))).append("，");
                    priPersonCert.append(stringDetail.substring(stringDetail.indexOf("身份证号：")+5,stringDetail.indexOf("职务："))).append("，");
                    priJob.append(stringDetail.substring(stringDetail.indexOf("职务：")+3,stringDetail.indexOf("地址："))).append("，");
                    priAddress.append(stringDetail.substring(stringDetail.indexOf("地址：")+3,stringDetail.indexOf("经查"))).append("，");
                }
                punishOrg = "宁夏保监局";

            }else if(stringDetail.indexOf("职务：")>-1){
                priPerson.append(stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("职务："))).append("，");
                if(stringDetail.indexOf("地址：")>-1){
                    priJob.append(stringDetail.substring(stringDetail.indexOf("职务：")+3,stringDetail.indexOf("地址："))).append("，");
                    priAddress.append(stringDetail.substring(stringDetail.indexOf("地址：")+3,stringDetail.indexOf("经查")).trim()).append("，");
                }
            }

            //TODO 判断是否为法人
            else if(stringDetail.indexOf("法定代表人：")>-1){

                punishToOrg = stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("地址："));
                punishToOrgAddress=stringDetail.substring(stringDetail.indexOf("地址：")+3,stringDetail.indexOf("法定代表人："));
                punishToOrgHolder = stringDetail.substring(stringDetail.indexOf("法定代表人：")+6,stringDetail.indexOf("经查"));
                punishOrg = "宁夏保监局";
            }else{
                priPerson.append(stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("经查"))).append("，");
                punishOrg = "宁夏保监局";
                String[] punishDateStr = stringDetail.split("。");
                punishDate= punishDateStr[punishDateStr.length-1].replaceAll(" ","");
            }
        }else{
            //TODO 从标题中提取当时人
            String personInfo = titleStr.replace("（","").replace("）","");
            personInfo = personInfo.substring(personInfo.indexOf("行政处罚信息")+6);
        //    String CurrentPersonStr = titleStr.replaceAll("行政处罚信息（","").replaceAll("）","");
            String[] CurrentPersonS = personInfo.split("，");
            //提取法人与自然人 TODO 有法人与自然人
            if(CurrentPersonS.length>=2&& CurrentPersonS[0].length()>3){
                punishToOrg = CurrentPersonS[0];

                for(int k=1;k<CurrentPersonS.length;k++){
                    String str = CurrentPersonS[k];
                    if(str.length()>3){// 存在多个法人的情况
                        punishToOrg = punishToOrg+"，"+str;
                    }else{
                        priPerson.append(str).append("，");
                    }
                }
            //    priPerson.toString().replaceAll(punishToOrg,"");

            }
            //提取法人 TODO 只有法人
            if(CurrentPersonS.length<2&& CurrentPersonS[0].length()>3){
                punishToOrg = CurrentPersonS[0];
            }
            //提取自然人
            if(CurrentPersonS.length<2&& CurrentPersonS[0].length()<=3){
                for(String str : CurrentPersonS){
                    priPerson.append(str).append("，");
                }
            }

            //TODO 此类数据正文中没有继续没有
            punishOrg = "宁夏保监局";
            punishDate = publishDate;

            //TODO 提取文号
            if(stringDetail.indexOf("行政处罚决定")>-1){
                String[] punishNoStr = stringDetail.split("于");
                punishDate = punishNoStr[1].split("作出")[0];
                punishNo ="宁夏保监局"+punishNoStr[1].split("作出")[1].split("行政处罚决定")[0];
            }
        }

        String spantext = elementsSpan.text().trim();
        if(punishDate.equalsIgnoreCase("") || punishDate.length()>12 ){
            if(spantext.lastIndexOf("日")>spantext.lastIndexOf("月") && spantext.lastIndexOf("月")> spantext.lastIndexOf("年")){
                punishDate =spantext.substring(spantext.lastIndexOf("年")-4,spantext.lastIndexOf("日")+1);
            }
        }

        log.info("发布主题："+titleStr);
        log.info("发布机构："+publishOrg);
        log.info("发布时间："+publishDate);
        log.info("处罚机关："+punishOrg);
        log.info("处罚时间："+punishDate);
        log.info("处罚文号："+punishNo);
        log.info("受处罚机构："+punishToOrg);
        log.info("受处罚机构地址："+punishToOrgAddress);
        log.info("受处罚机构负责人："+punishToOrgHolder);
        log.info("受处罚人："+priPerson.toString().replaceAll(punishToOrg,""));
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
        map.put("punishToOrg",punishToOrg);
        map.put("punishToOrgAddress",punishToOrgAddress);
        map.put("punishToOrgHolder",punishToOrgHolder);
        map.put("priPerson",priPerson.toString().replaceAll(punishToOrg,""));
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
