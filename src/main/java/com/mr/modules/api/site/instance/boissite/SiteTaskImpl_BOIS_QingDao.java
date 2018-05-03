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
 * 青岛保监局处罚 提取所需要的信息
 * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
 * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
 */
@Component("bois_qingdao")
@Slf4j
@Scope("prototype")
public class SiteTaskImpl_BOIS_QingDao extends SiteTaskExtendSub {
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
        String baseUrl = "http://qingdao.circ.gov.cn/web/site29/tab3435/module9005/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        ok:for(int i=1;i<=pageAll;i++){
            String url ="http://qingdao.circ.gov.cn/web/site29/tab3435/module9005/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                String resultUrl = "http://qingdao.circ.gov.cn"+elementUrl.attr("href");
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
        String baseUrl = "http://qingdao.circ.gov.cn/web/site29/tab3435/module9005/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        ok:for(int i=1;i<=pageAll;i++){
            String url ="http://qingdao.circ.gov.cn/web/site29/tab3435/module9005/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                //发布时间
                Element element_td = element.nextElementSibling();
                String extract_Date = "20" + element_td.text().replace("(","").replace(")","");
                if(new SimpleDateFormat("yyyy-MM-dd").parse(extract_Date).compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(date))>=0){
                    Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                    String resultUrl = "http://qingdao.circ.gov.cn"+elementUrl.attr("href");
                    log.info("编号："+i+"==resultUrl:"+resultUrl);
                    if(Objects.isNull(financeMonitorPunishMapper.selectByUrl(resultUrl))){
                        urlList.add(resultUrl);
                    }else{
                        break ok;
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
        String publishOrg = "中国保监会青岛保监局行政处";
        //发布时间
        String publishDate = "";
        //TODO 处罚机关
        String punishOrg = "青岛保监局";
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
        String stringDetail ="";
        Document doc = Jsoup.parse(fullTxt.replaceAll("、","，")
                .replace("(","（")
                .replace(")","）")
                .replaceAll(":","：")
                .replace("&nbps;","")
                .replace(" ","")
                .replaceAll("受处罚人（公民）：","当事人：")
                .replaceAll("受处罚机构名称：","当事人：")
                .replaceAll("机构名称：","当事人：")
                .replaceAll("受罚人（机构）：","当事人：")
                .replaceAll("受罚人机构：","当事人：")
                .replaceAll("受罚（机构）：","当事人：")
                .replaceAll("受罚人：姓名","当事人：")
                .replaceAll("姓 名：","当事人：")
                .replaceAll("名 称：","当事人：")
                .replaceAll("受 罚 人：","当事人：")
                .replaceAll("主要负责人姓名：","负责人：")
                .replaceAll("姓名：","当事人：")
                .replaceAll("营业地址：","地址：")
                .replaceAll("住所：","地址：")
                .replaceAll("住址：","地址：")
                .replace("住 址：","地址：")
                .replaceFirst("住址","地址：")
                .replaceAll("工作单位地址：","地址：")
                .replaceAll("住 所：","地址：")
                .replaceAll("职 务：","职务：")
                .replaceAll("主要负责人：","负责人：")
                .replaceAll("法 定 代 表人：","负责人：")
                .replaceAll("法 人 代表：","负责人：")
                .replaceAll("法定代表人：","负责人：")
                .trim()
        );
        //TODO 全文
        Element elementsTxt = doc.getElementById("tab_content");
        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Elements elementsSpan = elementsTxt.getElementsByClass("xilanwb");
        Elements elementsP = elementsTxt.getElementsByTag("P");
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

        String spantext = elementsSpan.text().trim();
        if(spantext.lastIndexOf("日")>spantext.lastIndexOf("月") && spantext.lastIndexOf("月")> spantext.lastIndexOf("年")){
            punishDate =spantext.substring(spantext.lastIndexOf("年")-4,spantext.lastIndexOf("日")+1);
        }

        //TODO 正文中没有文号
        if(elementsSpan.get(0).text().indexOf("青岛保监罚")>-1){
            String textInfo = elementsSpan.get(0).text();
            punishNo = textInfo.substring(textInfo.indexOf("青岛保监罚"),textInfo.indexOf("号")+1);
        }else{
            if(titleStr.contains("青岛保监罚")){
                punishNo = titleStr.substring(titleStr.indexOf("青岛保监罚"),titleStr.indexOf("号")+1);
            }
        }
        /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/
        if(stringDetail.indexOf("当事人：")>-1){
            List<String> listStr = new ArrayList();
            //TODO 判断是否为法人
            for(Element elementP : elementsP){
                String elementPStr =  elementP.text().replaceAll("　","").trim();
                if(elementP.text().indexOf("：")>-1){
                    listStr.add(elementP.text().replaceAll("　","").trim());
                }

            }
            //TODO 需要判断是法人还是自然人
            boolean busiPersonFlag = false;
            for(int i=0;i<listStr.size();i++ ){
                String[] currentPersonStr  = listStr.get(i).split("：");

                if(currentPersonStr.length>1){
                    currentPersonStr[0] = currentPersonStr[0].replace(" ","");
                    currentPersonStr[1] = currentPersonStr[1].replace(" ","");
                }
                if(i==0&&currentPersonStr[1].length()>3 && !currentPersonStr[1].contains("年龄") && !currentPersonStr[1].contains("性别") && !currentPersonStr[1].contains("0")){
                    busiPersonFlag =true;
                    punishToOrg = currentPersonStr[1];
                }
                // TODO 法人
                if(i==1&&busiPersonFlag==true&&currentPersonStr[0].trim().equals("地址")){
                    punishToOrgAddress = currentPersonStr[1];
                }
                if(i==2&&busiPersonFlag==true&&currentPersonStr[0].trim().equals("地址")){
                    punishToOrgAddress = currentPersonStr[1];
                }
                if(busiPersonFlag==true&&currentPersonStr[0].trim().equals("负责人")){
                    punishToOrgHolder = currentPersonStr[1];
                }
                if(i>1&&busiPersonFlag==true&&currentPersonStr[0].trim().equals("当事人")){
                    priPerson.append(currentPersonStr[1]).append("，");
                }
                if(i>2&&busiPersonFlag==true&&currentPersonStr[0].trim().equals("地址")){
                    priAddress.append(currentPersonStr[1]).append("，");
                }
                if(busiPersonFlag==true&&currentPersonStr[0].trim().equals("身份证号")){
                    priPersonCert.append(currentPersonStr[1]).append("，");
                }
                if(busiPersonFlag==true&&currentPersonStr[0].trim().equals("职务")){
                    priJob.append(currentPersonStr[1]).append("，");
                }

                //TODO 自然人
                if(busiPersonFlag==false&&currentPersonStr[0].trim().equals("当事人")){
                    if(currentPersonStr[1].contains("（") && currentPersonStr[1].contains("0")){
                        String name = currentPersonStr[1].substring(0,currentPersonStr[1].indexOf("（"));
                        String certNo = currentPersonStr[1].substring(currentPersonStr[1].indexOf("（")+1,currentPersonStr[1].indexOf("）"));
                        priPerson.append(name).append("，");
                        priPersonCert.append(certNo).append("，");
                    }else if(currentPersonStr[1].contains("年龄")){
                        String name = currentPersonStr[1].substring(0,currentPersonStr[1].indexOf("年龄"));
                        priPerson.append(name).append("，");
                    }else{
                        priPerson.append(currentPersonStr[1]).append("，");
                    }


                }
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
        }else if(spantext.contains("当事人：")){
            if(titleStr.contains("关于对") && titleStr.contains("的行政")){
                String name = titleStr.substring(titleStr.indexOf("关于对")+3,titleStr.indexOf("的行政"));
                if(name.length()>5){
                    punishToOrg = name;
                }else{
                    priPerson.append(name).append("，");
                    if(spantext.contains("身份证号：") && spantext.contains("地址：") && spantext.contains("经查")){
                        String certNo = spantext.substring(spantext.indexOf("身份证号：")+5,spantext.indexOf("地址：")).trim();
                        certNo = textTransfer(certNo);
                        priPersonCert.append(certNo).append("，");

                        String address = spantext.substring(spantext.indexOf("地址：")+3,spantext.indexOf("经查")).trim();
                        address = textTransfer(address);
                        priAddress.append(address).append("，");
                    }
                }
            }
            stringDetail = spantext;
        }

        if(punishToOrg.contains("以下简称")){
            punishToOrg = punishToOrg.substring(0,punishToOrg.indexOf("以下简称")-1);
        }

        /*log.info("发布主题："+titleStr);
        log.info("发布机构："+publishOrg);
        log.info("发布时间："+publishDate);
        log.info("处罚机关："+punishOrg);
        log.info("处罚时间："+textTransfer(punishDate));
        log.info("处罚文号："+textTransfer(punishNo));
        log.info("受处罚机构："+textTransfer(punishToOrg));
        log.info("受处罚机构地址："+textTransfer(punishToOrgAddress));
        log.info("受处罚机构负责人："+textTransfer(punishToOrgHolder));
        log.info("受处罚人："+textTransfer(priPerson.toString()));
        log.info("受处罚人证件："+textTransfer(priPersonCert.toString()));
        log.info("受处罚人职位："+textTransfer(priJob.toString()));
        log.info("受处罚人地址："+textTransfer(priAddress.toString()));
        log.info("来源："+source);
        log.info("主题："+object);
        log.info("正文："+stringDetail.trim());*/

        Map<String,String> map = new HashMap<String,String>();
        map.put("titleStr",titleStr);
        map.put("publishOrg",publishOrg);
        map.put("publishDate",publishDate);
        map.put("punishOrg",punishOrg);
        map.put("punishDate",textTransfer(punishDate));
        map.put("punishNo",textTransfer(punishNo));
        map.put("punishToOrg",textTransfer(punishToOrg));
        map.put("companyFullName",textTransfer(punishToOrg));
        map.put("punishToOrgAddress",textTransfer(punishToOrgAddress));
        map.put("punishToOrgHolder",textTransfer(punishToOrgHolder));
        map.put("priPerson",textTransfer(priPerson.toString()));
        map.put("priPersonCert",textTransfer(priPersonCert.toString()));
        map.put("priJob",textTransfer(priJob.toString()));
        map.put("priAddress",textTransfer(priAddress.toString()));
        map.put("source",source);
        map.put("object",object);
        map.put("stringDetail",stringDetail.trim());

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

    /**
     * 去除全角空格
     * */
    public String textTransfer(String text){
        text = text.replace((char) 12288, ' ').trim();
        return  text;
    }
}
