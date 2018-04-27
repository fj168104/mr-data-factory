package com.mr.modules.api.site.instance.boissite;


import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtendSub;
import com.mr.modules.api.site.instance.boissite.util.ParseNeiMengGu;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component("bois_neimenggu")
@Scope("prototype")
public class SiteTaskImpl_BOIS_NeiMengGu extends SiteTaskExtendSub{
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
                log.error("请检查此条url："+urlResult+"\n"+e.getMessage());
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
                log.error("请检查此条url："+oneFinanceMonitorPunish.getUrl()+"\n"+e.getMessage());
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
                    log.error("请检查此条url："+urlResult+"\n"+e.getMessage());
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
        String baseUrl = "http://neimenggu.circ.gov.cn/web/site4/tab3394/module8916/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        for(int i=1;i<=pageAll;i++){
            String url ="http://neimenggu.circ.gov.cn/web/site4/tab3394/module8916/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                String resultUrl = "http://neimenggu.circ.gov.cn"+elementUrl.attr("href");
                log.info("编号："+i+"==resultUrl:"+resultUrl);
                if(Objects.isNull(financeMonitorPunishMapper.selectByUrl(resultUrl))){
                    urlList.add(resultUrl);
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
        String baseUrl = "http://neimenggu.circ.gov.cn/web/site4/tab3394/module8916/page1.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        for(int i=1;i<=pageAll;i++){
            String url ="http://neimenggu.circ.gov.cn/web/site4/tab3394/module8916/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                //发布时间
                Element element_td = element.nextElementSibling();
                String extract_Date = "20" + element_td.text().replace("(","").replace(")","");
                if(new SimpleDateFormat("yyyy-MM-dd").parse(extract_Date).compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(date))>=0){
                    Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                    String resultUrl = "http://neimenggu.circ.gov.cn"+elementUrl.attr("href");
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
        String publishOrg = "中国保监会内蒙古监管局行政处";
        //发布时间
        String publishDate = "";
        //TODO 处罚机关
        String punishOrg = "内蒙古监管局";
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
        Document doc = Jsoup.parse(fullTxt.replace("(","（").
                replace(")","）").replace(":","：").replace("&nbps;","").replace(" ",""));
        Element elementsTxt = doc.getElementById("tab_content");
        //全文提取
        String txtAll = elementsTxt.text();
        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Elements elementsSpan = elementsTxt.getElementsByClass("xilanwb");
        //TODO 正文
        stringDetail =elementsSpan.text().replace(":","：")
                .replaceAll("受处罚人(公民):姓名：","当事人：")
                .replaceAll("受处罚人：姓名：","当事人：")
                .replaceAll("受处罚人：姓名","当事人：")
                .replaceAll("受处罚单位：","当事人：")
                .replaceAll("、","，")
                .replaceAll(",","，")
                .replace("(","（")
                .replace(")","）")
                .replace("&nbsp;","")
                .replace(" ","")
                .replace("被处罚单位：","当事人：")
                .replace("受罚人名称：","当事人：")
                .replace("受罚单位名称：","当事人：")
                .replace("单位名称：","当事人：")
                .replace("被处罚人名称：","当事人：")
                .replaceAll("受处罚机构：名称","当事人：")
                .replaceAll("当 事 人：","当事人：")
                .replaceAll("受处罚机构：","当事人：")
                .replaceAll("受处罚机构名称：","当事人：")
                .replaceAll("受处罚人（机构）名称：","当事人：")
                .replaceAll("受处罚人名称：","当事人：")
                .replaceAll("机构名称：","当事人：")
             //   .replaceAll("名称：","当事人：")
                .replace("被告知人：","当事人：")
                .replace("被处罚人：","当事人：")
                .replaceAll("法定代表人或主要负责人姓名：","负责人：")
                .replaceAll("法定代表人或者主要负责人姓名","负责人：")
                .replaceAll("法定代表人或者主要负责人姓名：","负责人：")
                .replaceAll("主要负责人姓名：","负责人：")
                .replaceAll("负责人姓名：","负责人：")
                .replaceAll("受处罚人姓名：","当事人：")
                .replaceAll("法定代表人姓名：","当事人：")
                .replaceAll("姓名：","当事人：")
                //      .replaceAll("当(.*)事(.*)人：","当事人：")
                .replaceAll("受处罚人：","当事人：")
                .replace("受处理人：","当事人：")
                .replaceAll("当事人：：","当事人：")
                .replace("营业地址：","地址：")
                .replaceAll("工作单位：","地址：")
                .replaceAll("工作单位地址：","地址：")
                .replace("受处罚机构地址：","地址：")
                .replace("单位地址：","地址：")
                .replace("公司住址：","地址：")
                .replace("机构住所：","地址：")
                .replaceAll("营业场所：","地址：")
                .replaceAll("营业住所：","地址：")
                .replaceAll("个人住址：","地址：")
                .replaceAll("家庭住址：","地址：")
                .replaceAll("地址","地址：")
                .replaceAll("住所：","地址：")
                .replaceAll("住所:","地址：") //存在有些为英文格式的:
                .replaceAll("住所地","地址：")
                .replaceAll("住所地：","地址：")
                .replaceAll("住址＿","地址：")
                .replaceAll("住址","地址：")
                .replaceAll("住址：","地址：")
                .replaceAll("住 址：","地址：")
                .replaceAll("住 所：","地址：")
                .replaceAll("地 址：","地址：")
                .replaceFirst("，住所地","，地址：")
                .replaceFirst("；住所地","，地址：")
                .replaceFirst("机构负责人：","负责人：")
                .replaceAll("负 责 人：","负责人：")
                .replaceFirst("，负责人","，负责人：")
                .replaceAll("，负责人：：","，负责人：")
                .replaceAll("负责人：：","负责人：")
                .replaceAll("地址：：","地址：")
                .replaceFirst("，住","，地址：")
                .replaceAll("工作单位及职务：","职务：")
                .replaceAll("职 务：","职务：")
                .replaceAll("时任：","职务：")
                .replaceAll("，时任","，职务：")
                .replaceAll("临时负责人：","负责人：")
                .replaceAll("总经理：","负责人：")
                .replaceAll("主要负责人：","负责人：")
                .replaceAll("公司负责人：","负责人：")
                .replaceAll("法定代表人：","负责人：")
                .replaceAll("身份证号码","身份证号")
                .replaceAll("身份证号码：","身份证号：")
                .replaceAll("证件号码：","，身份证号：")
                .replaceFirst("经检查","经查")
        ;
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
            if(stringDetail.indexOf("当事人：") < stringDetail.lastIndexOf("当事人：")){
                String detail1 = stringDetail.substring(0,stringDetail.lastIndexOf("当事人："));
                if(detail1.contains("地址：")){
                    String detail1_2 = "";
                    if(detail1.indexOf("当事人：") <detail1.lastIndexOf("当事人：") ){
                        String deatilInfo = detail1;
                        detail1 = deatilInfo.substring(0,deatilInfo.lastIndexOf("当事人："));
                        detail1_2 = deatilInfo.substring(deatilInfo.lastIndexOf("当事人："));
                    }
                    String name = detail1.substring(detail1.indexOf("当事人：")+4,detail1.indexOf("地址："));
                    if(name.length()>5 && !name.contains("，")){
                        punishToOrg = name;
                        if(detail1.contains("负责人")){
                            punishToOrgAddress = detail1.substring(detail1.indexOf("地址：")+3,detail1.indexOf("负责人"));
                            punishToOrgHolder = detail1.substring(detail1.indexOf("负责人：")+4);
                        }else{
                            punishToOrgAddress = detail1.substring(detail1.indexOf("地址：")+3);
                        }
                    }
                    if(!detail1_2.equalsIgnoreCase("")){
                        String p_name = "";
                        String p_job = "";
                        String p_id = "";
                        String p_address = "";
                        if(detail1_2.contains("身份证号")){
                            p_name = textFormat(detail1_2.substring(detail1_2.indexOf("当事人：")+4,detail1_2.indexOf("身份证号")));
                            if(detail1_2.contains("职务：")){
                                p_id = textFormat(detail1_2.substring(detail1_2.indexOf("身份证号：")+5,detail1_2.indexOf("职务")));
                                if(detail1_2.contains("地址：")){
                                    p_job =textFormat(detail1_2.substring(detail1_2.indexOf("职务：")+3,detail1_2.indexOf("地址")));
                                    p_address = textFormat(detail1_2.substring(detail1_2.indexOf("地址：")+3));
                                }
                            }else{
                                if(detail1_2.contains("地址：")){
                                    p_id = textFormat(detail1_2.substring(detail1_2.indexOf("身份证号：")+5,detail1_2.indexOf("地址")));
                                    p_address = textFormat(detail1_2.substring(detail1_2.indexOf("地址：")+3));
                                }
                            }
                        }
                        if(!p_name.equalsIgnoreCase("")){
                            priPerson.append(p_name).append("，");
                        }
                        if(!p_job.equalsIgnoreCase("")){
                            priJob.append(p_job).append("，");
                        }
                        if(!p_id.equalsIgnoreCase("")){
                            priPersonCert.append(p_id).append("，");
                        }
                        if(!p_address.equalsIgnoreCase("")){
                            priAddress.append(p_address).append("，");
                        }
                    }
                }
                String detail2 = stringDetail.substring(stringDetail.lastIndexOf("当事人："));
                String people = "";
                String job = "";
                String address = "";
                if(detail2.contains("负责人：") && detail2.contains("地址：")){
                    String name = detail2.substring(detail2.indexOf("当事人：")+4,detail2.indexOf("地址"));
                    String addressInfo = detail2.substring(detail2.indexOf("地址：")+3,detail2.indexOf("负责人"));
                    String detail2_1 = detail2.substring(detail2.indexOf("负责人：")+4);
                    String holderInfo = detail2_1.substring(0,detail2_1.indexOf("，"));
                    if(holderInfo.indexOf("依据《")>-1){
                        holderInfo = holderInfo.substring(0,holderInfo.indexOf("依据《")).trim();
                    }else if(addressInfo.indexOf("经查")>-1){
                        holderInfo = holderInfo.substring(0,holderInfo.indexOf("经查")).trim();
                    }else if(addressInfo.contains("年") && addressInfo.contains("月")){
                        holderInfo = holderInfo.substring(0,holderInfo.indexOf("年")-4).trim();
                    }
                    punishToOrg = punishToOrg+"，"+name;
                    punishToOrgAddress = punishToOrgAddress+"，"+addressInfo;
                    punishToOrgHolder = punishToOrgHolder+"，"+holderInfo;
                }else{
                    if(detail2.contains("身份证号：")){
                        String name = detail2.substring(detail2.indexOf("当事人：")+4,detail2.indexOf("身份证号"));
                        if(name.contains("，")){
                            String[] nameInfos = name.split("，");
                            people = nameInfos[0];
                            job = nameInfos[1];
                        }else{
                            people = name;
                        }
                        if(detail2.contains("职务：")){
                            String certNo = detail2.substring(detail2.indexOf("身份证号：")+5,detail2.indexOf("职务"));
                            priPersonCert.append(certNo).append("，");
                            if(detail2.contains("地址：") && detail2.contains("我局")){
                                job = detail2.substring(detail2.indexOf("职务：")+3,detail2.indexOf("地址"));
                                if(detail2.indexOf("依据《")>-1 && detail2.indexOf("依据《") < detail2.indexOf("我局")){
                                    address = detail2.substring(detail2.indexOf("地址：")+3,detail2.indexOf("依据《"));
                                }else{
                                    address = detail2.substring(detail2.indexOf("地址：")+3,detail2.indexOf("我局"));
                                    if(address.contains("年") && address.contains("月")){
                                        address = address.substring(0,address.indexOf("年")-4);
                                    }
                                }
                            }else{
                                if(detail2.indexOf("依据《")>-1 && detail2.indexOf("依据《") < detail2.indexOf("我局")){
                                    job = detail2.substring(detail2.indexOf("职务：")+3,detail2.indexOf("依据《"));
                                }else{
                                    job = detail2.substring(detail2.indexOf("职务：")+3,detail2.indexOf("我局"));
                                    if(job.contains("年") && job.contains("月")){
                                        job = job.substring(0,job.indexOf("年")-4);
                                    }
                                }
                            }
                        }else if(detail2.contains("地址：") && detail2.contains("我局")){
                            String certNo = detail2.substring(detail2.indexOf("身份证号：")+5,detail2.indexOf("地址"));
                            if(detail2.indexOf("依据《")>-1 && detail2.indexOf("依据《") < detail2.indexOf("我局")){
                                address = detail2.substring(detail2.indexOf("地址：")+3,detail2.indexOf("依据《"));
                            }else{
                                address = detail2.substring(detail2.indexOf("地址：")+3,detail2.indexOf("我局"));
                                if(address.contains("年") && address.contains("月")){
                                    address = address.substring(0,address.indexOf("年")-4);
                                }
                            }

                            priPersonCert.append(certNo).append("，");
                        }else{
                            String certNo = "";
                            if(detail2.indexOf("依据《")>-1 && detail2.indexOf("依据《") < detail2.indexOf("我局")){
                                certNo = detail2.substring(detail2.indexOf("身份证号：")+5,detail2.indexOf("依据《"));
                            }else{
                                certNo = detail2.substring(detail2.indexOf("身份证号：")+5,detail2.indexOf("我局"));
                                if(certNo.contains("年") && certNo.contains("月")){
                                    certNo = certNo.substring(0,certNo.indexOf("年")-4);
                                }
                            }
                            if(!certNo.equalsIgnoreCase("")){
                                priPersonCert.append(certNo).append("，");
                            }
                        }
                    }else if(detail2.contains("地址：") && detail2.contains("职务：")){
                        people = detail2.substring(detail2.indexOf("当事人：")+4,detail2.indexOf("职务："));
                        if(people.contains("，")){
                            String[] peopleInfo = people.split("，");
                            if(peopleInfo.length>1){
                                job = peopleInfo[1];
                                people = peopleInfo[0];
                            }
                        }
                        job = detail2.substring(detail2.indexOf("职务：")+3,detail2.indexOf("地址："));
                        if(detail2.indexOf("依据《")>-1 && detail2.indexOf("依据《") < detail2.indexOf("我局")){
                            address = detail2.substring(detail2.indexOf("地址：")+3,detail2.indexOf("依据《"));
                        }else{
                            address = detail2.substring(detail2.indexOf("地址：")+3,detail2.indexOf("我局"));
                            if(address.contains("年") && address.contains("月")){
                                address = address.substring(0,address.indexOf("年")-4);
                            }
                        }
                    }else if(detail2.contains("地址：") && detail2.contains("我局")){
                        people = detail2.substring(detail2.indexOf("当事人：")+4,detail2.indexOf("地址："));
                        if(people.contains("，")){
                            String[] peopleInfo = people.split("，");
                            if(peopleInfo.length>1){
                                job = peopleInfo[1];
                                people = peopleInfo[0];
                            }
                        }
                        if(detail2.indexOf("依据《")>-1 && detail2.indexOf("依据《") < detail2.indexOf("我局")){
                            address = detail2.substring(detail2.indexOf("地址：")+3,detail2.indexOf("依据《"));
                        }else{
                            address = detail2.substring(detail2.indexOf("地址：")+3,detail2.indexOf("我局"));
                            if(address.contains("年") && address.contains("月")){
                                address = address.substring(0,address.indexOf("年")-4);
                            }
                        }
                    }

                    if(!people.equalsIgnoreCase("")){
                        priPerson.append(people).append("，");
                    }
                    if(!job.equalsIgnoreCase("")){
                        priJob.append(job).append("，");
                    }
                    if(!address.equalsIgnoreCase("")){
                        priAddress.append(address).append("，");
                    }
                }

            }else {
                //TODO 判断是否为自然人
                if(stringDetail.indexOf("身份证号：")>-1){
                    if(stringDetail.indexOf("职务：")>-1){//有职务
                        String dangshiren = stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("身份证号："));
                        if(dangshiren.contains("性别")){
                            dangshiren = textFormat(dangshiren.substring(0,dangshiren.indexOf("性别")));
                        }
                        priPerson.append(dangshiren).append("，");
                        priPersonCert.append(stringDetail.substring(stringDetail.indexOf("身份证号：")+5,stringDetail.indexOf("职务："))).append("，");
                        String job = stringDetail.substring(stringDetail.indexOf("职务：")+3,stringDetail.indexOf("地址："));
                        job = textFormat(job);
                        if(job.contains("（") && job.contains("）") && job.contains("以下简称")){
                            String part1 = job.substring(0,job.indexOf("（"));
                            String part2 = job.substring(job.indexOf("）")+1);
                            job = part1+part2;
                        }
                        priJob.append(job).append("，");
                        String stringDetail2 = stringDetail.replaceAll("根据保","依据《");
                        String detailInfo = stringDetail2.substring(stringDetail2.indexOf("地址：")+3);
                        String addressInfo = detailInfo.substring(0,detailInfo.indexOf("，"));
                        if(addressInfo.indexOf("依据《")>-1){
                            priAddress.append(addressInfo.substring(0,addressInfo.indexOf("依据《")).trim()).append("，");
                        }else if(addressInfo.indexOf("经查")>-1){
                            priAddress.append(addressInfo.substring(0,addressInfo.indexOf("经查")).trim()).append("，");
                        }else if(addressInfo.contains("年") && addressInfo.contains("月")){
                            priAddress.append(addressInfo.substring(0,addressInfo.indexOf("年")-4).trim()).append("，");
                        }
                    }else{//无职务
                        String dangshiren = stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("身份证号："));
                        if(dangshiren.contains("性别")){
                            dangshiren = textFormat(dangshiren.substring(0,dangshiren.indexOf("性别")));
                        }
                        if(dangshiren.contains("年龄")){
                            dangshiren = textFormat(dangshiren.substring(0,dangshiren.indexOf("年龄")));
                        }
                        priPerson.append(dangshiren).append("，");
                        if(stringDetail.contains("地址：")){
                            priPersonCert.append(stringDetail.substring(stringDetail.indexOf("身份证号：")+5,stringDetail.indexOf("地址："))).append("，");
                            String stringDetail2 = stringDetail.replaceAll("根据保","依据《");
                            String detailInfo = stringDetail2.substring(stringDetail2.indexOf("地址：")+3);
                            String addressInfo = detailInfo.substring(0,detailInfo.indexOf("，"));
                            if(addressInfo.indexOf("依据《")>-1){
                                priAddress.append(addressInfo.substring(0,addressInfo.indexOf("依据《")).trim()).append("，");
                            }else if(addressInfo.indexOf("经查")>-1){
                                priAddress.append(addressInfo.substring(0,addressInfo.indexOf("经查")).trim()).append("，");
                            }else if(addressInfo.contains("年") && addressInfo.contains("月")){
                                priAddress.append(addressInfo.substring(0,addressInfo.indexOf("年")-4).trim()).append("，");
                            }
                        }else{

                      //      log.info("----------stringDetail---------"+stringDetail);
                       //     String certInfo = stringDetail.substring(stringDetail.indexOf("身份证号：")+5,stringDetail.indexOf("，")).trim();
                            String detailInfo = stringDetail.substring(stringDetail.indexOf("身份证号：")+5);
                            String certInfo = detailInfo.substring(0,detailInfo.indexOf("，"));
                            if(certInfo.indexOf("依据《")>-1){
                                priPersonCert.append(certInfo.substring(0,certInfo.indexOf("依据《")).trim()).append("，");
                            }else if(certInfo.indexOf("经查")>-1){
                                priPersonCert.append(certInfo.substring(0,certInfo.indexOf("经查")).trim()).append("，");
                            }else if(certInfo.contains("年") && certInfo.contains("月")){
                                priPersonCert.append(certInfo.substring(0,certInfo.indexOf("年")-4).trim()).append("，");
                            }

                        }
                    }

                }else if(stringDetail.indexOf("职务：")>-1){//无证号有职务
                    priPerson.append(stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("职务："))).append("，");
                    if(stringDetail.indexOf("地址：")>-1){
                        String job = stringDetail.substring(stringDetail.indexOf("职务：")+3,stringDetail.indexOf("地址："));
                        job = textFormat(job);
                        if(job.contains("（") && job.contains("）") && job.contains("以下简称")){
                            String part1 = job.substring(0,job.indexOf("（"));
                            String part2 = job.substring(job.indexOf("）")+1);
                            job = part1+part2;
                        }
                        priJob.append(job).append("，");

                        String stringDetail2 = stringDetail.replaceAll("根据保","依据《");
                        String detailInfo = stringDetail2.substring(stringDetail2.indexOf("地址：")+3);
                        String addressInfo = detailInfo.substring(0,detailInfo.indexOf("，"));

                        if(addressInfo.indexOf("依据《")>-1){
                            priAddress.append(addressInfo.substring(0,addressInfo.indexOf("依据《")).trim()).append("，");
                        }else if(addressInfo.indexOf("经查")>-1){
                            priAddress.append(addressInfo.substring(0,addressInfo.indexOf("经查")).trim()).append("，");
                        }else if(addressInfo.contains("年") && addressInfo.contains("月")){
                            priAddress.append(addressInfo.substring(0,addressInfo.indexOf("年")-4).trim()).append("，");
                        }


                    }
                }else if(stringDetail.indexOf("地址：")>-1 && !stringDetail.contains("负责人：") ){//无证号无职务
                    String peopleName = stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("地址："));
                    peopleName = textFormat(peopleName.replaceAll(",","，"));
                    if(peopleName.contains("，")){
                        peopleName = peopleName.substring(0,peopleName.indexOf("，"));
                    }else if(peopleName.length()>3 && (peopleName.contains("年龄") || peopleName.contains("岁") || peopleName.contains("男"))){
                        peopleName = peopleName.substring(0,3);
                    }
                    priPerson.append(peopleName).append("，");
                    String stringDetail2 = stringDetail.replaceAll("根据保","依据《");
                    String detailInfo = stringDetail2.substring(stringDetail2.indexOf("地址：")+3);
                    String addressInfo = detailInfo.substring(0,detailInfo.indexOf("，"));

                    if(addressInfo.indexOf("依据《")>-1){
                        priAddress.append(addressInfo.substring(0,addressInfo.indexOf("依据《")).trim()).append("，");
                    }else if(addressInfo.indexOf("经查")>-1){
                        priAddress.append(addressInfo.substring(0,addressInfo.indexOf("经查")).trim()).append("，");
                    }else if(addressInfo.contains("年") && addressInfo.contains("月")){
                        priAddress.append(addressInfo.substring(0,addressInfo.indexOf("年")-4).trim()).append("，");
                    }
                }else if(stringDetail.indexOf("负责人：")>-1){
                    if(stringDetail.contains("地址：")){
                        punishToOrg = stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("地址："));
                        punishToOrgAddress = stringDetail.substring(stringDetail.indexOf("地址：")+3,stringDetail.indexOf("负责人："));
                    }else{//无地址
                        punishToOrg = stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("负责人："));

                    }

                    String detailInfo = stringDetail.substring(stringDetail.indexOf("负责人：")+4);
                    String holderInfo = detailInfo.substring(0,detailInfo.indexOf("，"));

                    if(holderInfo.indexOf("依据《")>-1){
                        punishToOrgHolder = holderInfo.substring(0,holderInfo.indexOf("依据《")).trim();
                    }else if(holderInfo.indexOf("经查")>-1){
                        punishToOrgHolder = holderInfo.substring(0,holderInfo.indexOf("经查"));
                    }else if(holderInfo.contains("年") && holderInfo.contains("月")){
                        punishToOrgHolder = holderInfo.substring(0,holderInfo.indexOf("年")-4);
                    }
                }else{
                    String nameInfo = stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("，"));
                    if(nameInfo.contains("年龄")){
                        nameInfo = nameInfo.substring(0,nameInfo.indexOf("年龄"));
                    }
                    priPerson.append(nameInfo).append("，");
                }
            }

        }else{
            String[] nameInfo = stringDetail.split("：");
            String name = textFormat(nameInfo[0]);
            if(name.contains("内保监罚")){
                name = name.substring(name.indexOf("号")+1);
            }
            if(name.length()>6){
                punishToOrg = name;
            }else{
                priPerson.append(name).append("，");
            }


        }

        String spantext = elementsSpan.text().trim();
        if(punishDate.equalsIgnoreCase("") || punishDate.length()>12 ){
            if(spantext.lastIndexOf("日")>spantext.lastIndexOf("月") && spantext.lastIndexOf("月")> spantext.lastIndexOf("年")){
                String spantext2 = spantext.replaceAll(" ","");
                punishDate =spantext2.substring(spantext2.lastIndexOf("年")-4,spantext2.lastIndexOf("日")+1);
            }
        }

        punishToOrg = textFormat(punishToOrg).replaceAll("名称：","");
        if(punishToOrg.contains("以下简称")){
            punishToOrg = punishToOrg.substring(0,punishToOrg.indexOf("以下简称")-1);
        }


        punishNo = titleStr.substring(titleStr.indexOf("内保监罚"),titleStr.indexOf("号")+1).replaceAll(" ","");

        String priPerson2 = textFormat(priPerson.toString()).replaceAll("，，","，");

        punishToOrgHolder = punishToOrgHolder.replaceAll("在","");

        /*log.info("发布主题："+titleStr);
        log.info("发布机构："+publishOrg);
        log.info("发布时间："+publishDate);
        log.info("处罚机关："+punishOrg);
        log.info("处罚时间："+punishDate);
        log.info("处罚文号："+punishNo);
        log.info("受处罚机构："+textFormat(punishToOrg));
        log.info("受处罚机构地址："+textFormat(punishToOrgAddress));
        log.info("受处罚机构负责人："+textFormat(punishToOrgHolder));
        log.info("受处罚人："+priPerson2);
        log.info("受处罚人证件："+textFormat(priPersonCert.toString()));
        log.info("受处罚人职位："+textFormat(priJob.toString()));
        log.info("受处罚人地址："+textFormat(priAddress.toString()));
        log.info("来源："+source);
        log.info("主题："+object);
        log.info("正文："+stringDetail);*/

        Map<String,String> map = new HashMap<String,String>();
        map.put("titleStr",titleStr);
        map.put("publishOrg",publishOrg);
        map.put("publishDate",publishDate);
        map.put("punishOrg",punishOrg);
        map.put("punishDate",punishDate);
        map.put("punishNo",punishNo);
        map.put("punishToOrg",textFormat(punishToOrg));
        map.put("companyFullName",textFormat(punishToOrg));
        map.put("punishToOrgAddress",textFormat(punishToOrgAddress));
        map.put("punishToOrgHolder",textFormat(punishToOrgHolder));
        map.put("priPerson",priPerson2);
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

    public String textFormat(String text){
        return text.replace((char) 12288, ' ').replaceAll(" ","").trim();
    }
}
