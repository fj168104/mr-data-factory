package com.mr.modules.api.site.instance.boissite;

import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtendSub;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *@ auther :zjxu
 *@ dateTime : 201803
 * 山西保监局处罚 提取所需要的信息
 * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
 * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
 *
 */
@Slf4j
@Component("bois_shanxi")
@Scope("prototype")
public class SiteTaskImpl_BOIS_ShanXi extends SiteTaskExtendSub {

    /**
     * 获取：全量、增量
     * 通过发布时间：yyyy-mm-dd格式进行增量处理
     * 注：请求参数：publishDate
     */
    @Override
    protected String execute() throws Throwable {
//        String url = "http://anhui.circ.gov.cn/web/site11/tab3388/module8940/page1.htm";
        List<String> urlList = extractPageUrlList();
        for(String urlResult : urlList){
            log.info("urlResult:"+urlResult);
            Map map = extractContent(getData(urlResult));
            try{
                getObj(map,urlResult);
            }catch (Exception e){
                writeBizErrorLog(urlResult,"请检查此条url："+"\n"+e.getMessage());
                continue;
            }
        }
        return null;
    }
    /**
     * 获取：单笔
     * 注：请求参数传入：url
     */
    @Override
    protected String executeOne() throws Throwable {
        if(oneFinanceMonitorPunish.getUrl()!=null){
            log.info("oneUrl:"+oneFinanceMonitorPunish.getUrl());
            Map map = extractContent(getData(oneFinanceMonitorPunish.getUrl()));

            try{
                getObj(map,oneFinanceMonitorPunish.getUrl());
            }catch (Exception e){
                writeBizErrorLog(oneFinanceMonitorPunish.getUrl(),"请检查此条url："+"\n"+e.getMessage());
            }
        }
        if(oneFinanceMonitorPunish.getPublishDate()!=null){
            List<String> urlList = extractPageUrlListAdd(oneFinanceMonitorPunish.getPublishDate());
            for(String urlResult : urlList){
                log.info("urlResult:"+urlResult);
                Map map = extractContent(getData(urlResult));
                try{
                    getObj(map,urlResult);
                }catch (Exception e){
                    writeBizErrorLog(urlResult,"请检查此条url："+"\n"+e.getMessage());
                    continue;
                }
            }
        }
        return null;
    }
    /**  xtractPageAll,URl集合
     * @return*/

    public List extractPageUrlList(){
        List<String> urlList = new ArrayList<>();
        //第一个页面，用于获取总页数
        String baseUrl = "http://shanxi.circ.gov.cn/web/site31/tab3452/module9077/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        ok:for(int i=1;i<=pageAll;i++){
            String url ="http://shanxi.circ.gov.cn/web/site31/tab3452/module9077/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                String resultUrl = "http://shanxi.circ.gov.cn"+elementUrl.attr("href");
                log.info("编号："+i+"==resultUrl:"+resultUrl);
                if(Objects.isNull(financeMonitorPunishMapper.selectByUrl(resultUrl))){
                    urlList.add(resultUrl);
                }else{
                    break ok;
                }
            }
        }
        return urlList;
    }
    /**  xtractPageAll,增量集合
     * @return*/

    public List extractPageUrlListAdd(String date)throws Throwable{
        List<String> urlList = new ArrayList<>();
        //第一个页面，用于获取总页数
        String baseUrl = "http://shanxi.circ.gov.cn/web/site31/tab3452/module9077/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        for(int i=1;i<=pageAll;i++){
            String url ="http://shanxi.circ.gov.cn/web/site31/tab3452/module9077/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                //发布时间
                Element element_td = element.nextElementSibling();
                String extract_Date = "20" + element_td.text().replace("(","").replace(")","");
                if(new SimpleDateFormat("yyyy-MM-dd").parse(extract_Date).compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(date))>=0){
                    Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                    String resultUrl = "http://shanxi.circ.gov.cn"+elementUrl.attr("href");
                    log.info("编号："+i+"==resultUrl:"+resultUrl);
                    if(Objects.isNull(financeMonitorPunishMapper.selectByUrl(resultUrl))){
                        urlList.add(resultUrl);
                    }
                }

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
        String publishOrg = "中国保监会山西保监局";
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
        String stringDetail ="";
        Document doc = Jsoup.parse(fullTxt.replaceAll("、","，")
                .replace("(","（")
                .replace(")","）")
                .replace(":","：")
                .replace("&nbsp;","")
                .replace(" ","")
                .replace("受处罚人：","当事人：")
                .replace("受处罚个人：","当事人：")
                .replace("处罚人：","当事人：")
                .replace("受处罚人名称：","当事人：")
                .replace("受处罚机构名称 ：","当事人：")
                .replace("受处罚机构名称：","当事人：")
                .replace("机构名称：","当事人：")
                .replace("受处罚机构：","当事人：")
                .replace("法定代表人或者主要负责人姓名：","负责人：")
                .replace("姓名：","当事人：")
                .replaceAll("住址：","地址：")
                .replace("机构地址：","地址：")
                .replace("营业地址：","地址：")
                .replace("住所：","地址：")
                .replace("住  址：","地址：")
                .replace("住 址：","地址：")
                .replace("地 址：","地址：")
                .replace("职  务：","职务：")
                .replace("职 务：","职务：")
                .replace("职　务：","职务：")
                .replace("职 务：","职务：")
                .replace("临时负责人：","负责人：")
                .replace("主要负责人：","负责人：")
                .replace("法定代表人：","负责人：")
                .replace("主要负责人姓名：","负责人：")
                .replace("单位负责人：","负责人：")
                .replace("身份证号码：","身份证号：")
                .replace("身份证号码","身份证号")
                .replaceFirst("机构名称","当事人")
                .replaceFirst("主要负责人","负责人")
                .replaceFirst("营业地址","地址")

        );
        //TODO 全文
        Element elementsTxt = doc.getElementById("tab_content");
        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Elements elementsSpan = elementsTxt.getElementsByClass("xilanwb");
        Elements elementsSpanPRE = elementsSpan.select("PRE");
        Elements elementsP = elementsTxt.getElementsByTag("P");
        Elements elementsA = elementsTxt.getElementsByTag("A");
        //TODO 正文
        stringDetail =elementsP.text();
//        log.info("stringDetail:"+stringDetail);
        /*TODO 通用型*/
        //TODO 提取主题
        Element elementsTitle = elementsTD.first();
        String titleStr = elementsTitle.text();
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+5,publishDateStr.indexOf("分享到："));
        //TODO 正文中没有文号

        /*String[] punishNoStr = titleStr.split("处罚决定书");
        if(punishNoStr.length==2){
            punishNo =punishNoStr[1].replaceAll("　","").trim();
        }
        if(elementsSpan.text().indexOf("晋保监罚字")>-1&&elementsSpan.select("DIV").text().indexOf("晋保监罚字")>-1){
            punishNo =elementsSpan.select("DIV").text().replace("　","").trim();
        }*/
        punishNo = titleStr.substring(titleStr.indexOf("晋保监"),titleStr.lastIndexOf("号")+1);

        /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/
        if(stringDetail.indexOf("当事人：")>-1){
            //TODO 默认值
            punishOrg = "山西监管局";
            List<String> listStr = new ArrayList();
            //TODO 判断是否为法人
            for(Element elementP : elementsP){
                String elementPStr =  elementP.text().replaceAll("　","").replaceAll("职(.*)务","职务").replaceAll("性别：(.*)","").replaceAll("住址：","地址：").trim();
                log.info("---elementPStr----"+elementPStr);
               /* if(elementPStr.indexOf("：")>-1&&elementP.text().split("：").length==2){
                    listStr.add(elementP.text().replaceAll("　","").trim());
                }*/
                elementPStr = textFormat(elementPStr);
                //处理当事人，地址，负责人在同一个P标签中的情况
                if(elementPStr.indexOf("：")>-1&&elementPStr.split("：").length==3&&elementPStr.indexOf("负责人")==-1){

                    if(elementPStr.indexOf("地址")>-1){
                        listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("地址") ));
                        listStr.add(elementPStr.substring(elementPStr.indexOf("地址"),elementPStr.length()-1 ));
                    }
                    if(elementPStr.indexOf("身份证号")>-1){
                        listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("身份证号") ));
                        listStr.add(elementPStr.substring(elementPStr.indexOf("身份证号"),elementPStr.length()-1 ));
                    }
                    if(elementPStr.indexOf("职务")>-1){
                        listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("职务") ));
                        listStr.add(elementPStr.substring(elementPStr.indexOf("职务"),elementPStr.length()-1 ));
                    }

                    if(elementPStr.contains("1497家保险兼业代理机构")){//特殊处理
                        punishToOrg.append("1497家保险兼业代理机构").append("，");
                    }
                }else if(elementPStr.contains("负责人")&&elementPStr.contains("当事人")&&elementPStr.contains("地址")){//处理当事人，地址，负责人在同一个P标签中的情况

                    if(elementPStr.indexOf("地址")<elementPStr.indexOf("负责人")){
                        listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("地址")));
                        listStr.add(elementPStr.substring(elementPStr.indexOf("地址"),elementPStr.indexOf("负责人")));
                        listStr.add(elementPStr.substring(elementPStr.indexOf("负责人"),elementPStr.length()));
                    }else{
                        listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("负责人")));
                        listStr.add(elementPStr.substring(elementPStr.indexOf("负责人"),elementPStr.indexOf("地址")));
                        listStr.add(elementPStr.substring(elementPStr.indexOf("地址"),elementPStr.length()));
                    }

                }else if(elementPStr.contains("负责人")&&elementPStr.contains("当事人")&&elementPStr.contains("地址")&&elementPStr.contains("身份证号")&&elementPStr.contains("职务")){//处理当事人，身份证号，职务，地址在同一个P标签中的情况
                    if(elementPStr.indexOf("职务")<elementPStr.indexOf("地址")){
                      listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("身份证号") ));
                      listStr.add(elementPStr.substring(elementPStr.indexOf("身份证号"),elementPStr.indexOf("职务") ));
                      listStr.add(elementPStr.substring(elementPStr.indexOf("职务"),elementPStr.indexOf("地址") ));
                      listStr.add(elementPStr.substring(elementPStr.indexOf("地址"),elementPStr.length() ));
                  }else{
                      if(elementPStr.indexOf("身份证号")<elementPStr.indexOf("地址")){
                          listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("身份证号") ));
                          listStr.add(elementPStr.substring(elementPStr.indexOf("身份证号"),elementPStr.indexOf("地址") ));
                          listStr.add(elementPStr.substring(elementPStr.indexOf("地址"),elementPStr.indexOf("职务") ));
                          listStr.add(elementPStr.substring(elementPStr.indexOf("职务"),elementPStr.length() ));
                      }else{
                          listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("地址") ));
                          listStr.add(elementPStr.substring(elementPStr.indexOf("地址"),elementPStr.indexOf("身份证号") ));
                          listStr.add(elementPStr.substring(elementPStr.indexOf("身份证号"),elementPStr.indexOf("职务") ));
                          listStr.add(elementPStr.substring(elementPStr.indexOf("职务"),elementPStr.length() ));
                      }

                  }

                }else if(elementPStr.contains("当事人：") || elementPStr.contains("地址：") || elementPStr.contains("负责人：") || elementPStr.contains("职务：") || elementPStr.contains("身份证号：")){
                    if(elementPStr.contains("地址：") && elementPStr.contains("负责人：")){
                        listStr.add(elementPStr.substring(elementPStr.indexOf("地址："),elementPStr.indexOf("负责人：")));
                        listStr.add(elementPStr.substring(elementPStr.indexOf("负责人：")));
                    }else{
                        listStr.add(elementPStr.replaceAll("　","").trim());
                    }

                }
            }

            //TODO 需要判断是法人还是自然人
            boolean busiPersonFlag = false;
            for(int i=0;i<listStr.size();i++ ){
                String[] currentPersonStr  = listStr.get(i).split("：");
                if(currentPersonStr.length>=2){
                    currentPersonStr[0] = textFormat(currentPersonStr[0]);

                    //TODO 法人
                    if(currentPersonStr[1].length()>5&&currentPersonStr[0].equals("当事人") && !currentPersonStr[1].contains("身份证")){
                        busiPersonFlag =true;
                        if(currentPersonStr[1].contains("以下简称")){
                            currentPersonStr[1] = currentPersonStr[1].substring(0,currentPersonStr[1].indexOf("以下简称")-1);
                        }
                        punishToOrg.append(currentPersonStr[1]).append("，");
                    }

                    currentPersonStr[1] = currentPersonStr[1].replaceAll("经查实，","经查，");
                    if(currentPersonStr[1].contains("经查，")){
                        currentPersonStr[1] = currentPersonStr[1].substring(0,currentPersonStr[1].indexOf("经查，"));
                    }
                    log.info("----currentPersonStr[0]----"+currentPersonStr[0]+"-------currentPersonStr[1]----"+currentPersonStr[1]);
                    //TODO 自然人
                    if(currentPersonStr[1].length()<5&&currentPersonStr[0].equals("当事人")){
                        busiPersonFlag =false;
                        priPerson.append(currentPersonStr[1]).append("，");
                    }
                    // TODO 法人
                    if(busiPersonFlag==true&&currentPersonStr[0].trim().equals("地址")){
                        punishToOrgAddress.append(currentPersonStr[1]).append("，");
                    }
                    if(busiPersonFlag==true&&currentPersonStr[0].trim().equals("负责人")){
                        if(currentPersonStr[1].contains("当事人")){
                            currentPersonStr[1] = currentPersonStr[1].substring(0,currentPersonStr[1].indexOf("当事人"));
                        }
                        punishToOrgHolder.append(currentPersonStr[1]).append("，");
                    }
                    //TODO 自然人
                    if(busiPersonFlag==false&&currentPersonStr[0].trim().equals("地址")){
                        priAddress.append(currentPersonStr[1]).append("，");
                    }
                    if(busiPersonFlag==false&&currentPersonStr[0].trim().equals("身份证号")){
                        priPersonCert.append(currentPersonStr[1]).append("，");
                    }
                    if(busiPersonFlag==false&&currentPersonStr[0].trim().equals("职务")){
                        priJob.append(currentPersonStr[1]).append("，");
                    }
                }
            }
        }else{
            stringDetail = elementsSpan.text().trim();
            if(stringDetail.contains("当事人：")){
                punishOrg = "山西监管局";
                if(stringDetail.contains("地址：")&&stringDetail.contains("负责人：")){
                    String orgName = stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("地址："));
                    String address = stringDetail.substring(stringDetail.indexOf("地址：")+3,stringDetail.indexOf("负责人："));
                    punishToOrg.append(orgName).append("，");
                    punishToOrgAddress.append(address).append("，");
                    if(stringDetail.contains("经查，")){
                        String holder = stringDetail.substring(stringDetail.indexOf("负责人：")+4,stringDetail.indexOf("经查，"));
                        punishToOrgHolder.append(holder).append("，");
                    }else if(stringDetail.contains("经查实，")){
                        String holder = stringDetail.substring(stringDetail.indexOf("负责人：")+4,stringDetail.indexOf("经查实，"));
                        punishToOrgHolder.append(holder).append("，");
                    }else if(stringDetail.contains("根据")){
                        String holder = stringDetail.substring(stringDetail.indexOf("负责人：")+4,stringDetail.indexOf("根据"));
                        punishToOrgHolder.append(holder).append("，");
                    }
                }
            }else{
                punishOrg = "山西监管局";
                if(stringDetail.contains("地址：")&&stringDetail.contains("负责人：")){
                    String address = stringDetail.substring(stringDetail.indexOf("地址：")+3,stringDetail.indexOf("负责人："));
                    punishToOrgAddress.append(address).append("，");
                    if(stringDetail.contains("经查，")){
                        String holder = stringDetail.substring(stringDetail.indexOf("负责人：")+4,stringDetail.indexOf("经查，"));
                        punishToOrgHolder.append(holder).append("，");
                    }else if(stringDetail.contains("经查实，")){
                        String holder = stringDetail.substring(stringDetail.indexOf("负责人：")+4,stringDetail.indexOf("经查实，"));
                        punishToOrgHolder.append(holder).append("，");
                    }else if(stringDetail.contains("根据")){
                        String holder = stringDetail.substring(stringDetail.indexOf("负责人：")+4,stringDetail.indexOf("根据"));
                        punishToOrgHolder.append(holder).append("，");
                    }
                }
            }
        }


        String spantext = elementsSpan.text().trim();
        if(punishDate.equalsIgnoreCase("") || punishDate.length()>12 ){
            if(spantext.lastIndexOf("日")>spantext.lastIndexOf("月") && spantext.lastIndexOf("月")> spantext.lastIndexOf("年")){
                String spantext2 = spantext.replaceAll(" ","");
                punishDate =spantext2.substring(spantext2.lastIndexOf("年")-4,spantext2.lastIndexOf("日")+1);
            }
        }

        /*log.info("发布主题："+titleStr);
        log.info("发布机构："+publishOrg);
        log.info("发布时间："+publishDate);
        log.info("处罚机关："+punishOrg);
        log.info("处罚时间："+textFormat(punishDate));
        log.info("处罚文号："+textFormat(punishNo));
        log.info("受处罚机构："+textFormat(punishToOrg.toString()));
        log.info("受处罚机构地址："+textFormat(punishToOrgAddress.toString()));
        log.info("受处罚机构负责人："+textFormat(punishToOrgHolder.toString()));
        log.info("受处罚人："+textFormat(priPerson.toString()));
        log.info("受处罚人证件："+textFormat(priPersonCert.toString()));
        log.info("受处罚人职位："+textFormat(priJob.toString()));
        log.info("受处罚人地址："+textFormat(priAddress.toString()));
        log.info("来源："+source);
        log.info("主题："+object);
        log.info("正文："+stringDetail);*/

        Map<String,String> map = new HashMap<String,String>();
        map.put("titleStr",titleStr);
        map.put("publishOrg",publishOrg);
        map.put("publishDate",textFormat(punishDate));
        map.put("punishOrg",punishOrg);
        map.put("punishDate",punishDate);
        map.put("punishNo",textFormat(punishNo));
        map.put("punishToOrg",textFormat(punishToOrg.toString()));
        map.put("companyFullName",textFormat(punishToOrg.toString()));
        map.put("punishToOrgAddress",textFormat(punishToOrgAddress.toString()));
        map.put("punishToOrgHolder",textFormat(punishToOrgHolder.toString()));
        map.put("priPerson",textFormat(priPerson.toString()));
        map.put("priPersonCert",textFormat(priPersonCert.toString()));
        map.put("priJob",textFormat(priJob.toString()));
        map.put("priAddress",textFormat(priAddress.toString()));
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
        financeMonitorPunish.setPartyInstitution(delFinallyString(mapInfo.get("punishToOrg"),"，").replace("（"," ").replace("）"," "));//当事人（公司）=处罚对象
        financeMonitorPunish.setCompanyFullName(delFinallyString(mapInfo.get("punishToOrg"),"，").replace("（"," ").replace("）"," "));//公司全称
        financeMonitorPunish.setDomicile(delFinallyString(mapInfo.get("punishToOrgAddress"),"，"));//机构住址
        financeMonitorPunish.setLegalRepresentative(delFinallyString(mapInfo.get("punishToOrgHolder"),"，"));//机构负责人
        financeMonitorPunish.setPartyPerson(delFinallyString(mapInfo.get("priPerson"),"，"));//受处罚人
        financeMonitorPunish.setPartyPersonId(delFinallyString(mapInfo.get("priPersonCert"),"，"));//受处罚人证件号码
        financeMonitorPunish.setPartyPersonTitle(delFinallyString(mapInfo.get("priJob"),"，"));//职务
        financeMonitorPunish.setPartyPersonDomi(delFinallyString(mapInfo.get("priAddress"),"，"));//自然人住址
        financeMonitorPunish.setDetails(mapInfo.get("stringDetail"));//详情
        financeMonitorPunish.setUrl(href);
        financeMonitorPunish.setSource(mapInfo.get("source"));
        financeMonitorPunish.setObject(mapInfo.get("object"));

        //保存入库
        saveOne(financeMonitorPunish,false);

        return financeMonitorPunish;
    }

    public String textFormat(String text){
        return text.replace((char) 12288, ' ').replaceAll(" ","").trim();
    }
}
