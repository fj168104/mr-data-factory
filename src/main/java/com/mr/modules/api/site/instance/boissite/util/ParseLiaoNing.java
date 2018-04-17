package com.mr.modules.api.site.instance.boissite.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zqzhou
 * @Description:解析工具类
 * @Date: Created in 2018/4/3 16:04
 */
@Slf4j
public class ParseLiaoNing {

    /**
     * 解析法人信息
     * */
    private  Map<String,String> getFaRenInfo(String text){
        log.info("*****getFaRenInfo@1********"+text);
        Map<String,String> map = new HashMap<String,String>();
        String punishToOrg ="";
        String punishToOrgHolder = "";
        String punishToOrgAddress = "";

//  当事人：&&当事人：康平县邮政局四家子支局（中国邮政储蓄银行&&有限责任公司康平县四家子支行）&&地址：康平县北四家子乡北四家子村&&负责人：谢淑玲
        if(text.contains("当事人") && text.contains("地址")){
                String text2 = text.substring(text.lastIndexOf("当事人"),text.indexOf("地址"));
                if(text2.lastIndexOf("&&")>text2.indexOf("&&")){ //包含多个&&
                    text2 = text.substring(text.lastIndexOf("当事人：")).replace("&&","");
                    punishToOrg = text2.substring(text2.indexOf("当事人：")+4,text2.indexOf("地址")).trim();
                    if(text2.contains("负责人：") && text2.contains("经查")){
                        punishToOrgAddress = text2.substring(text2.indexOf("地址：")+3,text2.indexOf("负责人")).trim();
                        punishToOrgHolder = text2.substring(text2.indexOf("负责人：")+4,text2.indexOf("经查")).trim();
                    }
                }else {
                    if(text.contains("；")){//有些以"；"间断，统一以"，"
                        text = text.replaceAll("；","，");
                    }
                    if(text.contains("&&")){//有些以"&&"间断，统一以"，"
                        text = text.replaceAll("&&","，");
                    }
                    if(!text.contains("，地址：")){
                        text = text.replaceAll("，地址","，地址：");
                    }
                    log.info("*****getFaRenInfo@2********"+text);
                    String[] infoArray= text.split("，");
                    for(int j=0;j<infoArray.length;j++){
                        if(infoArray[j].contains("当事人：")){
                            if(infoArray[j].split("：").length>1){
                                punishToOrg = infoArray[j].split("：")[1].replace("。","");
                            }
                        }
                        if(infoArray[j].contains("地址：")){
                            if(infoArray[j].split("：").length>1){
                                punishToOrgAddress = infoArray[j].split("：")[1].replace("。","");
                            }

                        }

                        if(infoArray[j].contains("负责人：")){
                            if(infoArray[j].split("：").length>1){
                                punishToOrgHolder = infoArray[j].split("：")[1].replace("。","");
                            }
                        }
                    }
                }
        }

        log.info("*****getFaRenInfo-punishToOrg********"+punishToOrg);
        log.info("*****getFaRenInfo-punishToOrgHolder********"+punishToOrgHolder);
        log.info("*****getFaRenInfo-punishToOrgAddress********"+punishToOrgAddress);
        map.put("punishToOrg",punishToOrg);
        map.put("punishToOrgHolder",punishToOrgHolder);
        map.put("punishToOrgAddress",punishToOrgAddress);

        return map;
    }

    private  Map<String,String> getZiranRenInfo(String text){
        log.info("--getZiranRenInfo@1--text"+text);
        Map<String,String> map = new HashMap<String,String>();
        String priPerson =  "";
        String priPersonCert = "";
        String priAddress = "";
        String priJob = "";
        if(text.contains("&&")){//有些以"&&"间断，统一以"，"
            text = text.replaceAll("&&","，");
        }
        if(!text.contains("当事人：")){
            text = text.replace("当事人","当事人：");
        }
        if(!text.contains("身份证号：")){
            text = text.replace("身份证号","身份证号：");
        }
        if(!text.contains("职务：")){
            text = text.replace("职务","职务：");
        }
        if(!text.contains("，年龄")){
            text = text.replaceAll("年龄","，年龄");
        }
        if(!text.contains("，性别")){
            text = text.replaceAll("性别","，性别");
        }
        //  有时存在这种情况---当事人：张明照（身份证号：51222219551224XXXX）
        text = text.replaceAll("（","，").replaceAll("）","，");

        log.info("--getZiranRenInfo@2--text"+text);
        String[] infoArray =  text.split("，");
        for(int k=0;k<infoArray.length;k++){
            if(infoArray[k].contains("当事人")){
                if(infoArray[k].split("：").length>1){
                    priPerson = infoArray[k].split("：")[1].replace("。","");
                }
            }
            if(infoArray[k].contains("地址")){
                if(infoArray[k].split("：").length>1){
                    priAddress = infoArray[k].split("：")[1].replace("。","");
                }
            }
            if(infoArray[k].contains("身份证")){
                if(infoArray[k].split("：").length>1){
                    priPersonCert = infoArray[k].split("：")[1].replace("。","");
                }
            }
            if(infoArray[k].contains("职务")){
                if(infoArray[k].split("：").length>1){
                    priJob = infoArray[k].split("：")[1].replace("。","");
                }
            }

        }
        map.put("priPerson",priPerson);
        map.put("priAddress",priAddress);
        map.put("priPersonCert",priPersonCert);
        map.put("priJob",priJob);
        return map;
    }

    /**
     * 去除中文里的空格，保留英文里的空格
     * */
    private  String formatText(String fullTxt){
        String resString = fullTxt.replace((char) 12288, ' ').trim();
        resString = resString.replaceAll("(\\w) +(\\w)","$1@$2");
        resString = resString.replaceAll(" ","").replaceAll("@"," ");
        return resString.replace("<br>","&&")
                .replace("<br/>","&&")
                .replace("</br>","&&")
                .replace("<BR>","&&")
                .replace("<BR/>","&&")
                .replace("</BR>","&&");
    }

    /**
     * 将信息转换，文本统一化处理
     * */
    private  String textTransfer(String text){

        text = text.replace((char) 12288, ' ').trim(); //去掉全角空格
        String resText = text.replace(":","：")
                .replaceAll("受处罚人(公民):姓名：","当事人：")
                .replaceAll("受处罚人：姓名：","当事人：")
                .replaceAll("受处罚人：姓名","当事人：")
                .replaceAll("受处罚单位：","当事人：")
            //    .replaceAll("、","，")
                .replaceAll(",","，")
                .replace("(","（")
                .replace(")","）")
                .replaceAll("当事人[\u4e00-\u9fa5]{0,3}：", "当事人：")  //对存在多个当事人做处理
                .replace("&nbsp;","")
                .replace(" ","")
                .replace("被处罚单位：","当事人：")
                .replace("受罚人名称：","当事人：")
                .replace("受罚单位名称：","当事人：")
                .replace("被处罚人名称：","当事人：")
                .replaceAll("受处罚机构：名称","当事人：")
                .replaceAll("受处罚机构：","当事人：")
                .replaceAll("受处罚机构名称：","当事人：")
                .replaceAll("受处罚人（机构）名称：","当事人：")
                .replaceAll("受处罚人名称：","当事人：")
                .replaceAll("机构名称：","当事人：")
                .replaceAll("名称：","当事人：")
                .replace("被告知人：","当事人：")
                .replace("被处罚人：","当事人：")
                .replaceAll("法定代表人或主要负责人姓名：","负责人：")
                .replaceAll("法定代表人或者主要负责人姓名","负责人：")
                .replaceAll("法定代表人或者主要负责人姓名：","负责人：")
                .replaceAll("主要负责人姓名：","负责人：")
                .replaceAll("受处罚人姓名：","当事人：")
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
                .replaceAll("营业地址：","地址：")
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
                .replaceFirst("，住所地","，地址：")
                .replaceFirst("；住所地","，地址：")
                .replaceFirst("机构负责人：","负责人：")
                .replaceAll("投资人：","负责人：")
                .replaceFirst("，负责人","，负责人：")
                .replaceAll("，负责人：：","，负责人：")
                .replaceAll("负责人：：","负责人：")
                .replaceAll("地址：：","地址：")
                .replaceFirst("，住","，地址：")
                //      .replaceAll("地(.*)址：","地址：")
                //      .replaceAll("职(.*)务：","职务：")
                //   .replaceAll("时(.*)任：","职务：")
                .replaceAll("工作单位及职务：","职务：")
                .replaceAll("时任：","职务：")
                .replaceAll("，时任","，职务：")
                //  .replaceAll("负(.*)责(.*)人：","负责人：")
                .replaceAll("临时负责人：","负责人：")
                .replaceAll("总经理：","负责人：")
                .replaceAll("主要负责人：","负责人：")
                .replaceAll("法定代表人：","负责人：")
                .replaceAll("身份证号码","身份证号")
                .replaceAll("身份证号码：","身份证号：")

                //      .replace((char) 12288, ' ') //去掉全角空格
                ;

        return resText;
    }

    /**
     * 解析信息，对外的方法入口
     * */
    public  Map parseInfo(String fullText){
        Map resmap = new HashMap();
        //发布时间
        String publishDate = "";

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
        String stringDetail ="";

        String resText = formatText(fullText);
        Document doc = Jsoup.parse(resText);

        //TODO 全文
        Element elementsTxt = doc.getElementById("tab_content");
        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Elements elementsSpan = elementsTxt.getElementsByClass("xilanwb");
        Elements elementsP = elementsTxt.getElementsByTag("P");
        Elements elementsA = elementsTxt.getElementsByTag("A");
        Elements elementsPre = elementsTxt.getElementsByTag("Pre");

        //TODO 正文
        stringDetail = textTransfer(elementsP.text().trim());
        String stringDetailSource = "P";
        if(stringDetail.equalsIgnoreCase("") || stringDetail.indexOf("当事人：")<0){//有些版本的正文不在p标签里
            stringDetail = textTransfer(elementsSpan.text().trim());
            stringDetailSource = "span";
        }
        if(textTransfer(elementsPre.text().trim()).contains("当事人：")){
            stringDetail = textTransfer(elementsPre.text().trim())+" "+stringDetail;
        }
        log.info("stringDetailSource---"+stringDetailSource+"---stringDetail:"+stringDetail);

        /*TODO 通用型*/
        //TODO 提取主题
        Element elementsTitle = elementsTD.first();
        String titleStr = elementsTitle.text();
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+5,publishDateStr.indexOf("分享到："));


        String spantext = textTransfer(elementsSpan.text()).trim();
        if(spantext.lastIndexOf("日")>spantext.lastIndexOf("月") && spantext.lastIndexOf("月")> spantext.lastIndexOf("年")){
            punishDate =spantext.substring(spantext.lastIndexOf("年")-4,spantext.lastIndexOf("日")+1);
        }


        List<String> listStr = new ArrayList();

        for(Element elementPre : elementsPre){//存在过信息在pre标签里
            String elementPreStr = elementPre.text().replaceAll("\\s*", "");
            elementPreStr = textTransfer(elementPreStr);
            log.info("-------elementPreStr-------"+elementPreStr);
            if(elementPreStr.indexOf("：")>-1 && elementPreStr.split("：").length>1){
                elementPreStr = textTransfer(elementPreStr);
                if(elementPreStr.contains("当事人：") || elementPreStr.contains("负责人：")|| elementPreStr.contains("地址：") || elementPreStr.contains("身份证号：") || elementPreStr.endsWith("日")){
                    listStr.add(elementPreStr);
                }
            }

        }

        /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/
        if(stringDetail.indexOf("当事人")>-1){
            //TODO 判断是否为法人
            if(stringDetailSource.equalsIgnoreCase("p")){
                for(Element elementP : elementsP){
                    String elementPStr =  elementP.text().replaceAll("\\s*", "");
                    elementPStr = textTransfer(elementPStr);
                    if(elementPStr.indexOf("：")>-1&&elementPStr.split("：").length>1){
                        log.info("--查看p标签text--"+elementPStr);
                        listStr.add(elementPStr);
                    }

                }
            }else if(stringDetailSource.equalsIgnoreCase("span")){
                for(Element elementPan : elementsSpan){
                    String elementPStr =  elementPan.text().replaceAll("\\s*", "");
                    elementPStr = textTransfer(elementPStr);
                    if(elementPStr.indexOf("：")>-1&&elementPStr.split("：").length>1){
                        log.info("--查看span标签text--"+elementPStr);
                        listStr.add(elementPStr);
                    }
                }
            }



            //如果P标签中没有事件，事件在A标签中，需要获取A标签中的时间
            for(Element elementA : elementsA){
                String elementAStr =  elementA.text().replaceAll("　","").trim();
                if(elementAStr.indexOf("年")>-1 && elementAStr.indexOf("月")>-1&&elementAStr.indexOf("日")>-1){
                    punishDate = getPunishDate(elementAStr.replaceAll(" ","").trim());
                }
            }
            // 辽宁省中处罚文书的标题就是文号
            punishNo = titleStr.replace((char) 12288, ' ').trim();

            //TODO 需要判断是法人还是自然人
            boolean busiPersonFlag = false;
            int m = 0;
            log.info("listStr.size()----------"+listStr.size());
            for(int i=0;i<listStr.size();i++ ){


                if(i==0){
                    // 有些版本的处罚决定书，正文的第一行包括当事人，地址及负责人信息；有些版本决定书的正文全在一个p标签里，需特殊处理
                    String firstStr = listStr.get(i);
                    log.info("##################"+firstStr);
                    if((firstStr.contains("当事人") && (firstStr.contains("保险法》") || firstStr.contains("管理规定》") || (firstStr.contains("负责人：") && firstStr.contains("职务"))))){
                        Map resInfoMap = parseInfoInOneTag(firstStr);
                        punishToOrg = (String) resInfoMap.get("punishToOrg");
                        punishToOrgAddress = (String) resInfoMap.get("punishToOrgAddress");
                        punishToOrgHolder = (String) resInfoMap.get("punishToOrgHolder");
                        priPerson = (StringBuffer) resInfoMap.get("priPerson");
                        priPersonCert = (StringBuffer) resInfoMap.get("priPersonCert");
                        priJob = (StringBuffer) resInfoMap.get("priJob");
                        priAddress = (StringBuffer) resInfoMap.get("priAddress");
                        //有些处罚时间不在这个标签里
                        if(!((String) resInfoMap.get("punishDate")).equalsIgnoreCase("")){
                            punishDate = (String) resInfoMap.get("punishDate");
                        }
                        break;
                    }
                }
                String textInfo = listStr.get(i);
                if(textInfo.contains("当事人：") && textInfo.contains("地址：") && textInfo.substring(textInfo.indexOf("当事人：")+4,textInfo.indexOf("地址：")).replaceAll("&&","").length()>5
                        && !textInfo.contains("负责人：") && !textInfo.contains("身份证号") && !textInfo.contains("职务") && !textInfo.contains("年龄") && !textInfo.contains("性别")){

                    punishToOrg = textInfo.substring(textInfo.indexOf("当事人：")+4,textInfo.indexOf("地址：")).replaceAll("&&","");
                    punishToOrgAddress = textInfo.substring(textInfo.indexOf("地址：")+3).trim();
                    m = i;
                    busiPersonFlag =true;
                }else if(textInfo.contains("当事人：") && textInfo.contains("负责人：") && !textInfo.contains("身份证号") && !textInfo.contains("职务") && !textInfo.contains("年龄")){//此为法人
                    Map<String,String> resMap = getFaRenInfo(listStr.get(i));
                    punishToOrg = resMap.get("punishToOrg");
                    punishToOrgAddress = resMap.get("punishToOrgAddress");
                    punishToOrgHolder = resMap.get("punishToOrgHolder");
                }else if((textInfo.contains("当事人") && textInfo.contains("地址：")) || (textInfo.contains("当事人") && textInfo.contains("身份证号"))
                        || (textInfo.contains("当事人") && textInfo.contains("性别")) || (textInfo.contains("当事人") && textInfo.contains("年龄"))
                        || (textInfo.contains("当事人") && textInfo.contains("职务"))){//此为自然人
                    Map<String,String> map = getZiranRenInfo(textInfo);
                    if(!map.get("priPerson").equalsIgnoreCase("")){
                        priPerson.append(map.get("priPerson")).append("，");
                    }
                    if(!map.get("priAddress").equalsIgnoreCase("")){
                        priAddress.append(map.get("priAddress")).append("，");
                    }
                    if(!map.get("priPersonCert").equalsIgnoreCase("")){
                        priPersonCert.append(map.get("priPersonCert")).append("，");
                    }
                    if(!map.get("priJob").equalsIgnoreCase("")){
                        priJob.append(map.get("priJob")).append("，");
                    }
                }else {
                    //对有规律信息的处理
                    String[] currentPersonStr  = listStr.get(i).split("：");
                    log.info(currentPersonStr[0]+"-----------"+currentPersonStr[1]);
                    if(currentPersonStr[0].contains("当事人") && currentPersonStr[1].length()>50){
                        // &&当事人：辽宁华洋保险代理有限公司&&你公司上报的监管报表显示，2007年度及2008年上半年你公司共实现保险代理手续费收入34.56万元。.....
                        String text = listStr.get(i);
                        if(text.contains("&&")){
                            if(text.indexOf("当事人：")>0){
                                text = text.substring(text.indexOf("当事人："));
                                String[] textArray = text.split("&&");
                                punishToOrg = textArray[0].substring(textArray[0].indexOf("当事人：")+4).trim();
                                break;
                            }

                        }
                    }
                    if(currentPersonStr[1].length()>5&&currentPersonStr[0].replace("&&","").trim().equals("当事人")){
                        m = i;
                        busiPersonFlag =true;
                        if(currentPersonStr[1].contains("&&")){
                            currentPersonStr[1] = currentPersonStr[1].split("&&")[0];
                        }

                        log.info("----------currentPersonStr[1]----------"+currentPersonStr[1]);
                        if(currentPersonStr[1].contains("地址")){
                            currentPersonStr[1] = currentPersonStr[1].substring(0,currentPersonStr[1].indexOf("地址"));
                            if(currentPersonStr.length>2){
                                punishToOrgAddress = currentPersonStr[2].trim();
                            }

                        }
                        punishToOrg = currentPersonStr[1];
                    }
                    // TODO 法人
                    if(busiPersonFlag==true&&currentPersonStr[0].trim().equals("地址")){
                        m = i;
                        punishToOrgAddress = currentPersonStr[1];
                    }
                    if(busiPersonFlag==true&&currentPersonStr[0].trim().equals("负责人")){
                        punishToOrgHolder = currentPersonStr[1];
                    }else if(m!=i){
                        busiPersonFlag = false;
                    }

                    //TODO 自然人
                    if(busiPersonFlag==false&&currentPersonStr[0].trim().equals("当事人")){
                        if(currentPersonStr[1].contains("&&")){
                            currentPersonStr[1] = currentPersonStr[1].split("&&")[0];
                        }
                        priPerson.append(currentPersonStr[1]).append("，");
                    }
                    if(busiPersonFlag==false&&currentPersonStr[0].trim().equals("地址") && !currentPersonStr[1].contains("经查")){
                        priAddress.append(currentPersonStr[1]).append("，");
                    }
                    if(busiPersonFlag==false&& (currentPersonStr[0].trim().equals("身份证号") || currentPersonStr[0].trim().equals("身份证"))){
                        priPersonCert.append(currentPersonStr[1]).append("，");
                    }
                    if(busiPersonFlag==false&&currentPersonStr[0].trim().equals("职务")){
                        // 职务-----------时任中意人寿保险有限公司辽宁省分公司银保沈阳本部负责人&&地址
                        if(currentPersonStr[1].contains("&&")){
                            currentPersonStr[1] = currentPersonStr[1].split("&&")[0];
                        }
                        if(listStr.get(i).contains("地址")){
                            if(currentPersonStr.length>2 && !currentPersonStr[2].equalsIgnoreCase("")){
                                priAddress.append(currentPersonStr[2].trim()).append("，");
                            }

                        }
                        priJob.append(currentPersonStr[1]).append("，");
                    }
                }


            }
        }

        resmap.put("publishDate",publishDate);
        resmap.put("punishDate",punishDate);
        resmap.put("punishNo",punishNo);
        //中华联合财产保险股份有限公司黑龙江分公司（以下简称中华联黑龙江分公司）
        if(punishToOrg.contains("以下简称")){
            punishToOrg = punishToOrg.substring(0, punishToOrg.indexOf("以下简称")-1);
        }
        resmap.put("punishToOrg",punishToOrg);
        resmap.put("punishToOrgAddress",punishToOrgAddress);
        resmap.put("punishToOrgHolder",punishToOrgHolder);
        resmap.put("priPerson",priPerson);
        resmap.put("priPersonCert",priPersonCert);
        resmap.put("priJob",priJob);
        resmap.put("priAddress",priAddress);
        resmap.put("stringDetail",stringDetail.replaceAll("&&",""));
        resmap.put("titleStr",titleStr);
        return  resmap;
    }

    /**
     * 解析正文只在一个p标签里
     * */
    private Map parseInfoInOneTag(String textInfo){

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
        //TODO 需要判断是法人还是自然人
        boolean busiPersonFlag = false;

        Map resMap = new HashMap();
        int k = 0;//记录法人所在的位置
        String[] strArray = textInfo.split("&&");
        for(int i=0;i<strArray.length;i++){
            String[] strArray2 = strArray[i].split("：");
            if(strArray2.length>1){
                log.info(strArray2[0]+"---------------"+strArray2[1]);
                if(strArray2[1].contains("年龄") && strArray2[1].contains("性别")){
                    if(strArray2[1].indexOf("年龄") > strArray2[1].indexOf("性别")){
                        strArray2[1] = strArray2[1].substring(0,strArray2[1].indexOf("性别"));
                    }else{
                        strArray2[1] = strArray2[1].substring(0,strArray2[1].indexOf("年龄"));
                    }
                }else if(strArray2[1].contains("年龄")){
                    strArray2[1] = strArray2[1].substring(0,strArray2[1].indexOf("年龄"));
                }else if(strArray2[1].contains("性别")){
                    strArray2[1] = strArray2[1].substring(0,strArray2[1].indexOf("性别"));
                }
                log.info("-------strArray2[1]--------"+strArray2[1]);
                // TODO 法人
                if(strArray2[1].length()>5 && strArray2[0].trim().equalsIgnoreCase("当事人")){
                    busiPersonFlag = true;
                    k=i;
                    punishToOrg = strArray2[1];
                    log.info("punishToOrg---"+punishToOrg);
                }
                if(busiPersonFlag == true  && strArray2[0].trim().equalsIgnoreCase("地址")){
                    punishToOrgAddress = strArray2[1];
                    k=i;
                    log.info("punishToOrgAddress---"+punishToOrgAddress);
                }

                if(busiPersonFlag == true  && strArray2[0].trim().equalsIgnoreCase("负责人")){
                    busiPersonFlag = false;
                    k=i;
                    punishToOrgHolder = strArray2[1];
                    log.info("punishToOrgHolder---"+punishToOrgHolder);
                }else if(k!=i){//存在负责人缺失的情况，此时将标志位置为 false，便于后面解析自然人数据
                    busiPersonFlag = false;
                }

                // TODO 自然人
                if(busiPersonFlag==false&&strArray2[0].trim().equals("当事人")){
                    priPerson.append(strArray2[1]).append("，");
                }
                if(busiPersonFlag==false&&strArray2[0].trim().equals("地址")){
                    priAddress.append(strArray2[1]).append("，");
                }
                if(busiPersonFlag==false&&strArray2[0].trim().equals("身份证号")){
                    priPersonCert.append(strArray2[1]).append("，");
                }
                if(busiPersonFlag==false&&strArray2[0].trim().equals("职务")){
                    priJob.append(strArray2[1]).append("，");
                }

            }
            if(strArray[i].contains("年") && strArray[i].contains("月") && strArray[i].contains("日") && strArray[i].endsWith("日")){
                punishDate = getPunishDate(strArray[i]);
            }
        }

        resMap.put("punishDate",punishDate);
        resMap.put("punishToOrg",punishToOrg);
        resMap.put("punishToOrgAddress",punishToOrgAddress);
        resMap.put("punishToOrgHolder",punishToOrgHolder);
        resMap.put("priPerson",priPerson);
        resMap.put("priPersonCert",priPersonCert);
        resMap.put("priJob",priJob);
        resMap.put("priAddress",priAddress);

        return resMap;

    }
    /**
     * 获取处罚时间
     * */
    private String getPunishDate(String dateInfo){
        int yearIndex = dateInfo.lastIndexOf("年");
        String date = dateInfo.substring(yearIndex-4);
        return date;
    }
}
