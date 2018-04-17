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
public class ParseWenZhou {

    /**
     * 解析法人信息
     * */
    private  Map<String,String> getFaRenInfo(String text){
        log.info("*****getFaRenInfo********"+text);
        Map<String,String> map = new HashMap<String,String>();
        String punishToOrg ="";
        String punishToOrgHolder = "";
        String punishToOrgAddress = "";
        if(text.contains("；")){//有些以"；"间断，统一以"，"
            text = text.replaceAll("；","，");
            log.info("*********text*********"+text);
        }
        String[] infoArray= text.split("，");
        for(int j=0;j<infoArray.length;j++){
            if(infoArray[j].contains("当事人：")){
                punishToOrg = infoArray[j].split("：")[1].replace("。","");
            }
            if(infoArray[j].contains("地址：")){
                punishToOrgAddress = infoArray[j].split("：")[1].replace("。","");
            }
            if(infoArray[j].contains("负责人：")){
                punishToOrgHolder = infoArray[j].split("：")[1].replace("。","");
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
        Map<String,String> map = new HashMap<String,String>();
        String priPerson =  "";
        String priPersonCert = "";
        String priAddress = "";

        if(!text.contains("当事人：")){
            text = text.replace("当事人","当事人：");
        }
        if(!text.contains("身份证号：")){
            text = text.replace("身份证号","身份证号：");
        }

        String[] infoArray =  text.split("，");
        for(int k=0;k<infoArray.length;k++){
            if(infoArray[k].contains("当事人")){
                priPerson = infoArray[k].split("：")[1].replace("。","");
            }
            if(infoArray[k].contains("地址")){
                priAddress = infoArray[k].split("：")[1].replace("。","");
            }
            if(infoArray[k].contains("身份证")){
                priPersonCert = infoArray[k].split("：")[1].replace("。","");
            }

        }
        map.put("priPerson",priPerson);
        map.put("priAddress",priAddress);
        map.put("priPersonCert",priPersonCert);
        return map;
    }

    /**
     * 去除中文里的空格，保留英文里的空格
     * */
    private  String formatText(String fullTxt){
        String resString = fullTxt;
        resString = resString.replaceAll("(\\w) +(\\w)","$1@$2");
        resString = resString.replaceAll(" ","").replaceAll("@"," ");
        return resString.replaceAll("<br>","&&")
                .replaceAll("<br/>","&&")
                .replaceAll("</br>","&&");
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
                .replaceAll("受处罚单位：名称：","当事人：")
                .replaceAll("受处罚单位：","当事人：")
                .replaceAll("、","，")
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
                .replaceAll("受处罚机构：","当事人：")
                .replaceAll("受处罚机构名称：","当事人：")
                .replaceAll("受处罚人名称：","当事人：")
                .replaceAll("机构名称：","当事人：")
                .replaceAll("名称：","当事人：")
                .replace("被告知人：","当事人：")
                .replace("被处罚人：","当事人：")
                .replaceAll("法定代表人或主要负责人姓名：","负责人：")
                .replaceAll("主要负责人姓名：","负责人：")
                .replaceAll("受处罚人姓名：","当事人：")
                .replaceAll("姓名：","当事人：")
                //      .replaceAll("当(.*)事(.*)人：","当事人：")
                .replaceAll("受处罚人：","当事人：")
                .replace("受处理人：","当事人：")
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
                .replaceAll("住所：","地址：")
                .replaceAll("住所:","地址：") //存在有些为英文格式的:
                .replaceFirst("，住所地","，地址：")
                .replaceAll("住所地","地址：")
                .replaceAll("住所地：","地址：")
                .replaceAll("住址：","地址：")
                .replaceFirst("；住所地","，地址：")
                .replaceFirst("机构负责人：","负责人：")
                .replaceAll("单位负责人：","负责人：")
                .replaceFirst("，负责人","，负责人：")
                .replaceAll("，负责人：：","，负责人：")
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
                .replaceAll("，法定代表人","，负责人：")
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
        if(stringDetail.equalsIgnoreCase("")){//有些版本的正文不在p标签里
            stringDetail = textTransfer(elementsSpan.text().trim());
            stringDetailSource = "span";
        }
        log.info("stringDetailSource---"+stringDetailSource+"---stringDetail:"+stringDetail);

        String spantext = textTransfer(elementsSpan.text()).trim();
        if(spantext.lastIndexOf("日")>spantext.lastIndexOf("月") && spantext.lastIndexOf("月")> spantext.lastIndexOf("年")){
            punishDate =spantext.substring(spantext.lastIndexOf("年")-4,spantext.lastIndexOf("日")+1);
        }

        /*TODO 通用型*/
        //TODO 提取主题
        Element elementsTitle = elementsTD.first();
        String titleStr = elementsTitle.text();
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+5,publishDateStr.indexOf("分享到："));




        /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/
        if(stringDetail.indexOf("当事人")>-1){

            List<String> listStr = new ArrayList();

            for(Element elementPre : elementsPre){//存在过信息在pre标签里
                String elementPreStr = elementPre.text().replaceAll("\\s*", "");
                if(elementPreStr.indexOf("：")>-1&&elementPreStr.split("：").length>1){
                    elementPreStr = textTransfer(elementPreStr);
                    if(elementPreStr.contains("当事人：") || elementPreStr.contains("负责人：")|| elementPreStr.contains("地址：") || elementPreStr.endsWith("日")){
                        listStr.add(elementPreStr);
                    }
                }

            }

            //TODO 判断是否为法人
            if(stringDetailSource.equalsIgnoreCase("p")){
                for(Element elementP : elementsP){
                    String elementPStr =  elementP.text().replaceAll("\\s*", "");
                    elementPStr = textTransfer(elementPStr);
                    if(elementPStr.indexOf("：")>-1&&elementPStr.split("：").length>1){
                        log.info("--查看p标签text--"+elementPStr);
                        listStr.add(elementPStr);
                    }
                    /*if(elementPStr.indexOf("年")>-1 && elementPStr.indexOf("月")>-1&&elementPStr.indexOf("日")>-1 &&elementPStr.endsWith("日")){
                        if(elementPStr.contains("&&")){
                            String[] arrayInfo = elementPStr.split("&&");
                            if(arrayInfo.length>0){
                                for(int n = 0;n < arrayInfo.length;n++){
                                    if(arrayInfo[n].contains("年") && arrayInfo[n].contains("月") && arrayInfo[n].contains("日") && arrayInfo[n].endsWith("日")){
                                        String dateInfo = arrayInfo[n];
                                        punishDate = getPunishDate(dateInfo);
                                    }
                                }
                            }
                        }else{
                            punishDate = getPunishDate(elementPStr.replaceAll(" ","").trim());
                        }

                    }*/
                    if(formatText(elementP.text()).indexOf("监罚〔")>-1 || formatText(elementP.text()).indexOf("监罚[")>-1){
                        punishNo = elementP.text().replaceAll(" ","").trim();
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
                    /*if(elementPStr.indexOf("年")>-1 && elementPStr.indexOf("月")>-1&&elementPStr.indexOf("日")>-1 &&elementPStr.endsWith("日")){
                        if(elementPStr.contains("&&")){
                            String[] arrayInfo = elementPStr.split("&&");
                            if(arrayInfo.length>0){
                                for(int n = 0;n < arrayInfo.length;n++){
                                    if(arrayInfo[n].contains("年") && arrayInfo[n].contains("月") && arrayInfo[n].contains("日") && arrayInfo[n].endsWith("日")){
                                        String dateInfo = arrayInfo[n];
                                        punishDate = getPunishDate(dateInfo);
                                    }
                                }
                            }
                        }else{
                            punishDate = getPunishDate(elementPStr.replaceAll(" ","").trim());
                        }

                    }*/
                }
            }



            /*//如果P标签中没有事件，事件在A标签中，需要获取A标签中的时间
            for(Element elementA : elementsA){
                String elementAStr =  elementA.text().replaceAll("　","").trim();
                if(elementAStr.indexOf("年")>-1 && elementAStr.indexOf("月")>-1&&elementAStr.indexOf("日")>-1){
                    punishDate = getPunishDate(elementAStr.replaceAll(" ","").trim());
                }
            }
            //时间有时会在pre标签里
            if(punishDate.equalsIgnoreCase("")){
                if(elementsPre.last()!=null){
                    punishDate = getPunishDate(elementsPre.last().text().replaceAll("　","").trim());
                }
            }*/
            //如果正文里没有处罚文号
            if(punishNo.equalsIgnoreCase("")){
                //TODO 从标题中获取处罚文号
                if(titleStr.startsWith("行政")){
                    punishNo = titleStr.substring(titleStr.indexOf("监罚")-2, titleStr.indexOf("号")+1);
                }else{
                    String wenhao = titleStr.split("行政")[0];
                    if(wenhao.indexOf("〔")>-1 || wenhao.indexOf("[")>-1){
                        punishNo = wenhao;
                    }else{
                        int index = wenhao.indexOf("罚");
                        String year = wenhao.substring(index+1,index+5);
                        punishNo = wenhao.replaceAll(year,"["+year+"]");
                    }
                }


            }else{
                //各个省份的文号格式不一致 暂以浙江的为准
                //浙江：浙保监罚[2010]3号
                //云南：[按照云南保监局《行政处罚决定书》（云保监罚〔2018〕20号）内容公布] 或 云保监罚〔2013〕27号
                punishNo = punishNo.substring(punishNo.indexOf("保监罚")-1, punishNo.indexOf("号")+1);
            }


            //TODO 需要判断是法人还是自然人
            boolean busiPersonFlag = false;
            int m = 0;
            for(int i=0;i<listStr.size();i++ ){


                if(i==0){
                    // 有些版本的处罚决定书，正文的第一行包括当事人，地址及负责人信息；有些版本决定书的正文全在一个p标签里，需特殊处理
                    String firstStr = listStr.get(i);
                    log.info("##################"+firstStr);
                    if((firstStr.contains("当事人") && firstStr.contains("地址") && firstStr.contains("保险法》")) || firstStr.contains("&&")){
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
                if(textInfo.contains("当事人：") && textInfo.contains("地址：") && textInfo.contains("负责人：")){//此为法人
                    Map<String,String> resMap = getFaRenInfo(listStr.get(i));
                    punishToOrg = resMap.get("punishToOrg");
                    punishToOrgAddress = resMap.get("punishToOrgAddress");
                    punishToOrgHolder = resMap.get("punishToOrgHolder");
                }else if((textInfo.contains("当事人") && textInfo.contains("地址：")) || (textInfo.contains("当事人") && textInfo.contains("身份证号"))
                        || (textInfo.contains("当事人") && textInfo.contains("性别")) || (textInfo.contains("当事人") && textInfo.contains("年龄"))){//此为自然人
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
                }else {
                    //对有规律信息的处理
                    String[] currentPersonStr  = listStr.get(i).split("：");
                    log.info(currentPersonStr[0]+"-----------"+currentPersonStr[1]);

                    if(currentPersonStr[1].length()>5&&currentPersonStr[0].trim().equals("当事人")){
                        m = i;
                        busiPersonFlag =true;
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
                        priPerson.append(currentPersonStr[1]).append("，");
                    }
                    if(busiPersonFlag==false&&currentPersonStr[0].trim().equals("地址")){
                        priAddress.append(currentPersonStr[1]).append("，");
                    }
                    if(busiPersonFlag==false&& (currentPersonStr[0].trim().equals("身份证号") || currentPersonStr[0].trim().equals("身份证"))){
                        priPersonCert.append(currentPersonStr[1]).append("，");
                    }
                    if(busiPersonFlag==false&&currentPersonStr[0].trim().equals("职务")){
                        priJob.append(currentPersonStr[1]).append("，");
                    }
                }


            }
        }

        resmap.put("publishDate",publishDate);
        resmap.put("punishDate",punishDate);
        resmap.put("punishNo",punishNo);
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
        //中华联合财产保险股份有限公司黑龙江分公司（以下简称中华联黑龙江分公司）
        if(punishToOrg.contains("以下简称")){
            punishToOrg = punishToOrg.substring(0, punishToOrg.indexOf("以下简称")-1);
        }
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
        int yearIndex = dateInfo.indexOf("年");
        String date = dateInfo.substring(yearIndex-4);
        return date;
    }
}
