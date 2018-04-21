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

import java.util.*;

/**
 * @Auther :zjxu
 * @DateTime：201803
 */
@Slf4j
@Component("anhui")
@Scope("prototype")
public  class SiteTaskImpl_BOIS_AnHui extends SiteTaskExtend{
    @Override
    protected String execute() throws Throwable {
//        String url = "http://anhui.circ.gov.cn/web/site11/tab3388/module8940/page1.htm";
        List<String> urlList = extractPageUrlList();
        for(String urlResult : urlList){
            log.info("urlResult:"+urlResult);
            Map map = extractContent(getData(urlResult));
            getObj(map,urlResult);
        }
        return null;
    }
    /**
     *  xtractPageAll,URl集合
     * @return
     */
    public List extractPageUrlList(){
        List<String> urlList = new ArrayList<>();
        //第一个页面，用于获取总页数
        String baseUrl = "http://anhui.circ.gov.cn/web/site11/tab3388/module8940/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        for(int i=1;i<=pageAll;i++){
            String url ="http://anhui.circ.gov.cn/web/site11/tab3388/module8940/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                String resultUrl = "http://anhui.circ.gov.cn"+elementUrl.attr("href");
                log.info("编号："+i+"==resultUrl:"+resultUrl);
                urlList.add(resultUrl);
            }
        }
        return urlList;
    }
    /**
     * 获取保监会处罚列表所有页数
     * @param fullTxt
     * @return
     */
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
    /**
     * 安徽保监局处罚 提取所需要的信息
     * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
     * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
     */
    public Map extractContent(String fullTxt) {
        //发布机构
        String publishOrg = "中国保监会安徽监管局";
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
        //TODO 受处罚当时人地址（自然人）
        StringBuffer priAddress = new StringBuffer();
        //TODO 受处罚当时人职位（自然人）
        StringBuffer priJob =  new StringBuffer();
        //TODO 判断处罚的是法人，还是自然人
        String fileType = "";
        //数据来源  TODO 来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）
        String source = "保监局";
        //主题 TODO 主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录
        String object = "行政处罚决定";
        //TODO 全文
        String stringDetail ="";
        Document doc = Jsoup.parse(fullTxt.replace("、","，")
                .replace("(","（").
                replace(")","）")
                .replace(":","：")
                .replace("&nbps;","")
                .replace(" ","")


        );
        Element elementsTxt = doc.getElementById("tab_content");

        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Elements elementsSpan = elementsTxt.getElementsByClass("xilanwb");
        stringDetail =elementsSpan.text();
        /*TODO 通用型*/
        //TODO 提取主题
        Element elementsTitle = elementsTD.first();
        String titleStr = elementsTitle.text();
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+5,publishDateStr.indexOf("分享到："));

        //全文提取
        String txtAll = elementsTxt.text()
                .replace("、","，")
                .replace("(","（")
                .replace(")","）")
                .replace(":","：")
                .replace("地  址：","地址：")
                .replaceAll("地 址: ","地址：")
                .replace("当事人： ","当事人：")
                .replace("负责人： ","负责人：")
                .replace("地址： ","地址：")
                .replace("证件号码： ","证件号码：")
                .replace("职务： ","职务：")
                .replace("。","，")
                .replace(" ","，")
                .replace("　","，")
                .replace("地，址：，","地址：")
                .replace("受处罚人：","当事人：")
                .replace("法定代表人（主要负责人）： ","负责人：")
                .replace("受罚人姓名：","当事人：")
                .replace("受处罚人姓名：","当事人：")
                .replace("受处罚人名称：","当事人：")
                .replace("受处罚机构名称：","当事人：")
                .replace("处罚人姓名：","当事人：")
                .replace("主要负责人姓名：","负责人：")
                .replace("法定代表人或负责人：","负责人：")
                .replace("法定代表人或主要负责人：","负责人：")
                .replace("法定代表人或主要负责人姓名：","负责人：")
                .replace("身份证号码：","证件号码：")
                .replace("身份证号码","证件号码")
                .replace("地  址：","地址：")
                .replace("住址：","地址：")
                .replace("年龄：","，年龄：")
                .replace(" 号","号")
                .replace("行为：","，")
                .replace(" ","，")
                ;
        String[] txtAllArr = txtAll.split("，");
        //判断是法人还是自然人true为自然人，false为法人
        boolean personFlag = true;
        if(txtAll.contains("当事人：")){
            for(String arrStr : txtAllArr){
                String[] str = arrStr.split("：");

                if(arrStr.contains("当事人：")&&str.length>=2){
                    if(str[1].trim().length()<6){
                        //TODO 受处罚当时人名称（自然人）
                        priPerson.append(str[1]).append("，");
                        //TODO 判断处罚的是法人，还是自然人
                        fileType = "个人处罚";
                        personFlag=true;
                    }else{
                        //TODO 受处罚机构
                        punishToOrg.append(str[1]).append("，");
                        //TODO 判断处罚的是法人，还是自然人
                        fileType = "对公处罚";
                        personFlag=false;
                    }
                }
                if(personFlag==false&&arrStr.contains("地址：")&&str.length>=2){
                    //TODO 受处罚机构地址
                    punishToOrgAddress.append(str[1]).append("，");
                }
                if(personFlag==false&&arrStr.contains("负责人：")&&str.length>=2){
                    //TODO 法定代表人或主要负责人
                    punishToOrgHolder.append(str[1]).append("，");
                }

                if(personFlag==true&&arrStr.contains("证件号码：")&&str.length>=2){
                    //TODO 受处罚当时人证件号码（自然人）
                    priPersonCert.append(str[1]).append("，");
                }
                if(personFlag==true&&arrStr.contains("职务：")&&str.length>=2){
                    //TODO 受处罚当时人职位（自然人）
                    priJob.append(str[1]).append("，");
                }
                if(personFlag==true&&arrStr.contains("地址：")&&str.length>=2){
                    //TODO 受处罚当时人地址（自然人）
                    priAddress.append(str[1]).append("，");
                }

                if(arrStr.contains("年")&&arrStr.endsWith("日")){
                    //TODO 处罚时间
                    punishDate=arrStr;
                }
                if(arrStr.contains("保监罚")&&arrStr.endsWith("号")){
                    //TODO 处罚文号
                    punishNo=arrStr;
                }
            }
            if(punishOrg.equals("")){
                punishOrg ="安徽保监局";
            }
        }else{
            //标记公司：companyFlag第一次出现
            boolean companyFlag= false;
            for(String arrStr : txtAllArr){
                if(arrStr.contains("于")&&arrStr.contains("作出")&&arrStr.contains("处罚决定")) {
                    punishOrg = arrStr.split("于")[0];
                    punishDate = arrStr.split("于")[1].split("作出")[0];
                    punishNo = arrStr.split("于")[1].split("作出")[1].split("处罚决定")[0];
                }
                if(arrStr.contains("公司于")){
                    punishToOrg.append(arrStr.split("公司于")[0]+"公司").append("，");
                }
                if(arrStr.contains("（证件号码")){
                    String priPersonStr = arrStr.split("（证件号码")[0];
                    if(priPersonStr.contains("时任")&&priPersonStr.contains("理")){
                        priPerson.append(priPersonStr.split("理")[1]).append("，");
                        priJob.append(priPersonStr.split("理")[0]+"理").append("，");
                    }
                     priPersonCert.append(arrStr.split("（证件号码")[1].split("）")[0]).append("，") ;
                    if(arrStr.split("（证件号码")[1].split("）").length==2){
                        String priJobStr= arrStr.split("（证件号码")[1].split("）")[1];
                        if(priJobStr.contains("时任")){
                            priJob.append(priJobStr).append("，");
                        }
                    }
                }
                if(arrStr.contains("公司")&&companyFlag==false){
                    punishToOrg.append(arrStr.split("公司")[0]+"公司");
                    companyFlag = true;
                }
            }
            if(punishOrg.equals("")){
                punishOrg ="安徽保监局";
            }
        }
        if(punishNo.equals("")||punishNo.equals("null")||punishNo.equals("NULL")||punishNo==null){
            punishNo = "无文号"+new Date().getTime();
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
