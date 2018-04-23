package com.mr.modules.api.site.instance.boissite;

import com.mr.common.OCRUtil;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtendSub;
import com.mr.modules.api.site.instance.boissite.util.ParseHuNan;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component("hunan")
@Scope("prototype")
public class SiteTaskImpl_BOIS_HuNan extends SiteTaskExtendSub {
    @Override
    protected String execute() throws Throwable {
//        String url = "http://hunan.circ.gov.cn/web/site22/tab3410/module9893/page1.htm";
        List<String> urlList = extractPageUrlList();
        for(String urlResult : urlList){
            log.info("urlResult:"+urlResult);
            List<Map<String,String>> mapList = extractContent(getData(urlResult));
            for(Map<String,String> map : mapList){
                getObj(map,urlResult);
            }
        }
        //2007年报表：http://hunan.circ.gov.cn/web/site22/tab3410/info87811.htm
//        List map = extractContent(getData("http://hunan.circ.gov.cn/web/site22/tab3410/info77085.htm"));
        return null;
    }

    /**  xtractPageAll,URl集合
     * @return*/

    public List extractPageUrlList(){
        List<String> urlList = new ArrayList<>();
        //第一个页面，用于获取总页数
        String baseUrl = "http://hunan.circ.gov.cn/web/site22/tab3410/module9893/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        for(int i=1;i<=pageAll;i++){
            String url ="http://hunan.circ.gov.cn/web/site22/tab3410/module9893/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                String resultUrl = "http://hunan.circ.gov.cn"+elementUrl.attr("href");
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


    public List<Map<String,String> > extractContent(String fullTxt) {
        List<Map<String,String> > mapRecord = new ArrayList<>();
        //发布机构
        String publishOrg = "中国保监会湖南保监局行政处";
        //发布时间
        String publishDate = "";
        //TODO 处罚机关（由于有些页面没有，所以暂且给予默认值）
        String punishOrg = "湖南保监局";
        //TODO 处罚时间
        String punishDate = "";
        //TODO 处罚文号
        String punishNo = "";
        //TODO 受处罚机构
        String punishToOrg = "";
        //TODO 受处罚机构地址
        String punishToOrgAddress = "";
        //TODO 法定代表人或主要负责人
        String punishToOrgHolder = "";
        //TODO 受处罚当时人名称（自然人）
        StringBuffer priPerson = new StringBuffer();
        //TODO 受处罚当时人证件号码（自然人）
        StringBuffer priPersonCert = new StringBuffer();
        //TODO 受处罚当时人职位（自然人）
        StringBuffer priJob = new StringBuffer();
        //TODO 受处罚当时人地址（自然人）
        StringBuffer priAddress = new StringBuffer();
        //TODO 判断处罚的是法人，还是自然人
        String priBusiType = "";
        String stringDetail = "";
        //数据来源  TODO 来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）
        String source = "保监局";
        //主题 TODO 主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录
        String object = "行政处罚决定";
        String titleStr = "";
        //Doc格式文件内容

        Document docDoc = Jsoup.parse(fullTxt);
        Element elementsTxt = docDoc.getElementById("tab_content");
        Elements elementsDoc = docDoc.getElementsByClass("xilanwb");
        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Element elementsTitle = elementsTD.first();
         titleStr = elementsTitle.text();

        String txtAll = elementsDoc.text().toString()
                .replace(" ","")
                .replace("　","")
                .replace(" ","");

        //TODO ********用户处理表格类文案********
        Elements elementsTR  = elementsDoc.select("tr");
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+5,publishDateStr.indexOf("分享到："));
        OCRUtil ocrUtil = new OCRUtil();
        if(fullTxt.contains("2008年1-4月湖南保监局行政处罚事项统计表.doc")){
            //TODO 获取文档连接
            Elements elementDocUrls = elementsDoc.select("a");
            String elementDocUrl = elementDocUrls.attr("href");
            try {
                log.info("url-doc:"+"http://hunan.circ.gov.cn"+elementDocUrl);
                String fileName = downLoadFile("http://hunan.circ.gov.cn"+elementDocUrl,elementDocUrls.text());
                String docTxt = ocrUtil.getTextFromDoc(fileName);
                String[] docArr = docTxt.split(System.lineSeparator());
                for(String strDoc : docArr ){
                    String strDocSub = strDoc.substring(2,strDoc.length());
                    Pattern p = Pattern.compile("\t");
                    Matcher m = p.matcher(strDocSub);
                    strDocSub = m.replaceAll("，");
                    String[] strDocSubArrs = strDocSub.split("，");
                    for(String strDocSubArr : strDocSubArrs){
                        if(strDocSubArr.contains("月")&&strDocSubArr.endsWith("日")){
                            punishDate = "2008年"+strDocSubArr;
                        }
                        if(strDocSubArr.contains("保监罚")&&strDocSubArr.endsWith("号")){
                            punishNo = strDocSubArr;
                        }
                        if(strDocSubArr.contains("公司")&&strDocSubArr.contains("（")&&strDocSubArr.endsWith("）")){
                            priPerson.delete(0,priPerson.length());
                            priPerson.append(strDocSubArr.split("（")[0]).append("，");
                            punishToOrg=strDocSubArr.split("（")[1].split("）")[0];
                        }
                        if(strDocSubArr.contains("公司")&&!strDocSubArr.contains("（")&&!strDocSubArr.endsWith("）")){
                            priPerson.delete(0,priPerson.length());
                            punishToOrg=strDocSubArr;
                        }
                    }
                    if(strDocSub.contains("保监罚")&&strDocSub.contains("号")){
                        titleStr = "2008年1-4月湖南保监局行政处罚事项统计表";
                        Map<String,String> map = new HashMap<String,String>();
                        stringDetail = strDocSub;
                        map.put("titleStr",titleStr);
                        map.put("publishOrg",publishOrg);
                        map.put("publishDate",publishDate);
                        map.put("punishOrg",punishOrg);
                        map.put("punishDate",punishDate);
                        map.put("punishNo",punishNo);
                        map.put("punishToOrg",punishToOrg);
                        map.put("punishToOrgAddress",punishToOrgAddress);
                        map.put("punishToOrgHolder",punishToOrgHolder);
                        map.put("priPerson",priPerson.toString());
                        map.put("priPersonCert",priPersonCert.toString());
                        map.put("priJob",priJob.toString());
                        map.put("priAddress",priAddress.toString());
                        map.put("source",source);
                        map.put("object",object);
                        map.put("stringDetail",stringDetail);

                        mapRecord.add(map);
                        log.info("发布主题：" + titleStr);
                        log.info("发布机构：" + publishOrg);
                        log.info("发布时间：" + publishDate);
                        log.info("处罚机关：" + punishOrg);
                        log.info("处罚时间：" + punishDate);
                        log.info("处罚文号：" + punishNo);
                        log.info("受处罚机构：" + punishToOrg);
                        log.info("受处罚机构地址：" + punishToOrgAddress);
                        log.info("受处罚机构负责人：" + punishToOrgHolder);
                        log.info("受处罚人：" + priPerson);
                        log.info("受处罚人证件：" + priPersonCert);
                        log.info("受处罚人职位：" + priJob);
                        log.info("受处罚人地址：" + priAddress);
                        log.info("来源："+source);
                        log.info("主题："+object);
                        log.info("正文：" + stringDetail);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(fullTxt.contains("2007年湖南保监局行政处罚事项统计表.doc")){
            //TODO 获取文档连接
            Elements elementDocUrls = elementsDoc.select("a");
            String elementDocUrl = elementDocUrls.attr("href");
            try {
                log.info("url-doc:"+"http://hunan.circ.gov.cn"+elementDocUrl);
                String fileName = downLoadFile("http://hunan.circ.gov.cn"+elementDocUrl,elementDocUrls.text());
                String docTxt = ocrUtil.getTextFromDoc(fileName);
                String[] docArr = docTxt.split(System.lineSeparator());
                for(String strDoc : docArr ){
                    String strDocSub = strDoc.substring(2,strDoc.length());
                    Pattern p = Pattern.compile("\t");
                    Matcher m = p.matcher(strDocSub);
                    strDocSub = m.replaceAll("，");
                    String[] strDocSubArrs = strDocSub.split("，");
                    for(String strDocSubArr : strDocSubArrs){
                        if(strDocSubArr.contains("月")&&strDocSubArr.endsWith("日")){
                            punishDate = "2007年"+strDocSubArr;
                        }
                        if(strDocSubArr.contains("保监罚")&&strDocSubArr.endsWith("号")){
                            punishNo = strDocSubArr;
                        }
                        if(strDocSubArr.contains("公司")&&strDocSubArr.contains("（")&&strDocSubArr.endsWith("）")){
                            priPerson.delete(0,priPerson.length());
                            priPerson.append(strDocSubArr.split("（")[0]).append("，");
                            punishToOrg=strDocSubArr.split("（")[1].split("）")[0];
                        }
                        if(strDocSubArr.contains("公司")&&!strDocSubArr.contains("（")&&!strDocSubArr.endsWith("）")){
                            priPerson.delete(0,priPerson.length());
                            punishToOrg=strDocSubArr;
                        }
                    }
                    if(strDocSub.contains("保监罚")&&strDocSub.contains("号")){
                        titleStr = "2007年湖南保监局行政处罚事项统计表";
                        Map<String,String> map = new HashMap<String,String>();
                        stringDetail = strDocSub;
                        map.put("titleStr",titleStr);
                        map.put("publishOrg",publishOrg);
                        map.put("publishDate",publishDate);
                        map.put("punishOrg",punishOrg);
                        map.put("punishDate",punishDate);
                        map.put("punishNo",punishNo);
                        map.put("punishToOrg",punishToOrg);
                        map.put("punishToOrgAddress",punishToOrgAddress);
                        map.put("punishToOrgHolder",punishToOrgHolder);
                        map.put("priPerson",priPerson.toString());
                        map.put("priPersonCert",priPersonCert.toString());
                        map.put("priJob",priJob.toString());
                        map.put("priAddress",priAddress.toString());
                        map.put("source",source);
                        map.put("object",object);
                        map.put("stringDetail",stringDetail);

                        mapRecord.add(map);

                        log.info("发布主题：" + titleStr);
                        log.info("发布机构：" + publishOrg);
                        log.info("发布时间：" + publishDate);
                        log.info("处罚机关：" + punishOrg);
                        log.info("处罚时间：" + punishDate);
                        log.info("处罚文号：" + punishNo);
                        log.info("受处罚机构：" + punishToOrg);
                        log.info("受处罚机构地址：" + punishToOrgAddress);
                        log.info("受处罚机构负责人：" + punishToOrgHolder);
                        log.info("受处罚人：" + priPerson);
                        log.info("受处罚人证件：" + priPersonCert);
                        log.info("受处罚人职位：" + priJob);
                        log.info("受处罚人地址：" + priAddress);
                        log.info("来源："+source);
                        log.info("主题："+object);
                        log.info("正文：" + stringDetail);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(txtAll.contains("处罚对象")&&txtAll.contains("文书编号")&&txtAll.contains("处罚日期")){
            //TODO 表格：处罚日期	文书编号	处罚对象	违法事实	法律依据	处罚依据	处罚措施
            for(Element elementTR :elementsTR){
                if(elementTR.text().contains("湘保监罚")&&!elementTR.text().contains("处罚日期")&&!elementTR.text().contains("文书编号")&&!elementTR.text().contains("湖南保监局")){
                    Map<String,String> mapTR = new HashMap<String,String>();
                    Elements elementsTRTDS = elementTR.select("td");
                    if(elementsTRTDS.get(2).text().length()>5){

                        if(elementsTRTDS.get(2).text().contains("（")&&elementsTRTDS.get(2).text().split("（")[0].length()<6&&elementsTRTDS.get(2).text().split("（")[1].split("）")[0].length()>3){
                            mapTR.put("priPerson",elementsTRTDS.get(2).text().split("（")[0]);
                            mapTR.put("punishToOrg",elementsTRTDS.get(2).text().split("（")[1].replace("）",""));
                        }else{
                            mapTR.put("punishToOrg",elementsTRTDS.get(2).text());
                        }
                    }else{
                        mapTR.put("priPerson",elementsTRTDS.get(2).text());
                    }
                    mapTR.put("titleStr",titleStr);
                    mapTR.put("publishOrg",publishOrg);
                    mapTR.put("publishDate",publishDate);
                    mapTR.put("punishOrg",punishOrg);
                    mapTR.put("punishDate",elementsTRTDS.get(0).text());
                    mapTR.put("punishNo",elementsTRTDS.get(1).text());
                    mapTR.put("punishToOrg",punishToOrg);
                    mapTR.put("punishToOrgAddress",punishToOrgAddress.toString());
                    mapTR.put("punishToOrgHolder",punishToOrgHolder.toString());
                    mapTR.put("priPerson",priPerson.toString());
                    mapTR.put("priPersonCert",priPersonCert.toString());
                    mapTR.put("priAddress",priAddress.toString());
                    mapTR.put("source",source);
                    mapTR.put("object",object);
                    mapTR.put("stringDetail",elementTR.text());
                    mapRecord.add(mapTR);

                }

            }

        }else if(txtAll.contains("：经查")&&!txtAll.contains("受处罚机构：")&&!txtAll.contains("受处罚人：")){
            log.info("---------------"+txtAll);
            if(txtAll.contains("：经查")){
                String strSpec = txtAll.split("：经查")[0];
                if(strSpec.length()<6){
                    priPerson.append(strSpec) ;
                }else{
                    punishToOrg = strSpec;
                }
            }
            if(txtAll.contains("湖南保监局")){
                String[] strSpecs = txtAll.split("湖南保监局");
                if(strSpecs.length>1){
                    if(strSpecs[1].contains("年")&&strSpecs[1].endsWith("日")){
                        punishDate = strSpecs[1] ;
                    }
                }
            }
            if(punishOrg.equals("")){
                punishOrg = "湖南保监局";
            }
            if(titleStr.contains("保监罚")&&titleStr.contains("号")){
                punishNo = titleStr.substring(titleStr.indexOf("保监罚")-1,titleStr.indexOf("号")+1);
            }
            Map<String,String> mapTRSpecial = new HashMap<String,String>();
            mapTRSpecial.put("titleStr",titleStr);
            mapTRSpecial.put("publishOrg",publishOrg);
            mapTRSpecial.put("publishDate",publishDate);
            mapTRSpecial.put("punishOrg",punishOrg);
            mapTRSpecial.put("punishDate",punishDate);
            mapTRSpecial.put("punishNo",punishNo);
            mapTRSpecial.put("punishToOrg",punishToOrg);
            mapTRSpecial.put("punishToOrgAddress",punishToOrgAddress.toString());
            mapTRSpecial.put("punishToOrgHolder",punishToOrgHolder.toString());
            mapTRSpecial.put("priPersonCert",priPersonCert.toString());
            mapTRSpecial.put("priAddress",priAddress.toString());
            mapTRSpecial.put("source",source);
            mapTRSpecial.put("object",object);
            mapTRSpecial.put("stringDetail",txtAll);
            mapRecord.add(mapTRSpecial);
        }else{
            //HTML格式内容处理
            Map resMap = new ParseHuNan().parseInfo(fullTxt);
            publishDate = (String) resMap.get("publishDate");
            punishDate = (String) resMap.get("punishDate");
            punishNo = (String) resMap.get("punishNo");
            punishToOrg = (String) resMap.get("punishToOrg");
            punishToOrgAddress = (String) resMap.get("punishToOrgAddress");
            punishToOrgHolder = (String) resMap.get("punishToOrgHolder");
            priPerson = (StringBuffer) resMap.get("priPerson");
            priPersonCert = (StringBuffer) resMap.get("priPersonCert");
            priJob = (StringBuffer) resMap.get("priJob");
            priAddress = (StringBuffer) resMap.get("priAddress");
            stringDetail = (String) resMap.get("stringDetail");
            titleStr = (String) resMap.get("titleStr");


            log.info("发布主题：" + titleStr);
            log.info("发布机构：" + publishOrg);
            log.info("发布时间：" + publishDate);
            log.info("处罚机关：" + punishOrg);
            log.info("处罚时间：" + punishDate);
            log.info("处罚文号：" + punishNo);
            log.info("受处罚机构：" + punishToOrg);
            log.info("受处罚机构地址：" + punishToOrgAddress);
            log.info("受处罚机构负责人：" + punishToOrgHolder);
            log.info("受处罚人：" + priPerson);
            log.info("受处罚人证件：" + priPersonCert);
            log.info("受处罚人职位：" + priJob);
            log.info("受处罚人地址：" + priAddress);
            log.info("来源："+source);
            log.info("主题："+object);
            log.info("正文：" + stringDetail);

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
            map.put("priPerson",priPerson.toString());
            map.put("priPersonCert",priPersonCert.toString());
            map.put("priJob",priJob.toString());
            map.put("priAddress",priAddress.toString());
            map.put("source",source);
            map.put("object",object);
            map.put("stringDetail",stringDetail);
            mapRecord.add(map);

        }
        return mapRecord;
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
        financeMonitorPunish.setCompanyFullName(mapInfo.get("companyFullName"));//公司全称
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
