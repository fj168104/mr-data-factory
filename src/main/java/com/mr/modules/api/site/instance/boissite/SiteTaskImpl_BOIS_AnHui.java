package com.mr.modules.api.site.instance.boissite;

import com.mr.framework.http.HttpRequest;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * auther :zjxu
 * dateTime：201803
 */
@Slf4j
@Component("anhui")
@Scope("prototype")
public  class SiteTaskImpl_BOIS_AnHui{
    /**
     * 安徽保监局处罚 提取所需要的信息
     * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
     * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
     */
    public Map extractContent(String fullTxt) {
        //发布机构
        String publishOrg = "中国保监会安徽监管局行政处";
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
        String priJob = "";
        //TODO 判断处罚的是法人，还是自然人
        String priBusiType = "";
        //TODO 全文
        String stringDetail ="";
        Document doc = Jsoup.parse(fullTxt.replace("、","，").replace("(","（").
                replace(")","）").replace(":","：").replace("&nbps;","").replace(" ",""));
        Element elementsTxt = doc.getElementById("tab_content");
        //全文提取
        String txtAll = elementsTxt.text();
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

        /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/
        if(elementsTD.text().indexOf("当事人：")>-1){
            Elements elementsP = elementsTxt.getElementsByTag("P");
            //发布文号
            punishNo = elementsP.get(0).text();
            //当事人
            String[] partyS = elementsP.get(1).text().split("：");
            String party ="";
            if(partyS.length>2){
                party = partyS[1];
            }
            if(party.length()>3){
                //TODO 处罚机关
                if(elementsTxt.getElementsByTag("pre").text().equals("")){
                    punishOrg = elementsSpan.get(elementsSpan.size()-2).text();
                    punishDate = elementsSpan.get(elementsSpan.size()-1).text();
                }else{
                    punishOrg =elementsTxt.getElementsByTag("pre").text().replaceAll(" ","");
                    //TODO 处罚时间
                    punishDate = elementsSpan.get(0).ownText().replaceAll(" ","");
                }
               //当事人名称换行处理
                int flag = elementsP.get(2).text().split("：").length;
                if(flag == 0){
                    //TODO 受处罚机构
                    punishToOrg = elementsP.get(1).text().split("：")[1]+elementsP.get(2).text();
                    //TODO 受处罚机构地址
                    punishToOrgAddress = elementsP.get(3).text().split("：")[1];
                    //TODO 法定代表人或主要负责人
                    punishToOrgHolder = elementsP.get(4).text().split("：")[1];
                    //TODO 受处罚当时人名称（自然人）
                    priPerson.append(elementsP.get(5).text().split("：")[1]);
                    //TODO 受处罚当时人证件号码（自然人）
                    priPersonCert.append(elementsP.get(6).text().split("：")[1]);
                }else{
                    //TODO 受处罚机构
                    punishToOrg = elementsP.get(1).text().split("：")[1];
                    //TODO 受处罚机构地址
                    punishToOrgAddress = elementsP.get(2).text().split("：")[1];
                    //TODO 法定代表人或主要负责人
                    punishToOrgHolder = elementsP.get(3).text().split("：")[1];
                    //TODO 受处罚当时人名称（自然人）
                    priPerson.append(elementsP.get(4).text().split("：")[1]);
                    //TODO 受处罚当时人证件号码（自然人）
                    priPersonCert.append(elementsP.get(5).text().split("：")[1]);
                }
                //TODO 受处罚当时人职位（自然人）
                priJob = "";
            }else{
                for (Element elementP :elementsP){
                    String[] elementStr = elementP.text().split("：");
                    if(elementStr.length==2 && elementStr[0].equals("当事人") ){
                        //TODO 受处罚当时人名称（自然人）
                        priPerson.append(elementsP.text().split("：")[1]);
                    }
                    if(elementStr.length==2 && elementStr[0].equals("身份证号码") ){
                        //TODO 受处罚当时人证件号码（自然人）
                        priPersonCert.append(elementsP.text().split("：")[1]);
                    }
                }
                if(elementsSpan.size()>1){
                    punishOrg = elementsP.get(elementsSpan.size()-2).text();
                    punishDate = elementsP.get(elementsSpan.size()-1).text();
                }
            }
        }else if(txtAll.indexOf("受处罚人姓名：")>-1){
            Elements elementsP = elementsTxt.getElementsByTag("P");
            for (Element elementP :elementsP){
                String[] elementStr = elementP.text().split("：");

                if(elementStr.length==2&&"受处罚人姓名".equals(elementStr[0].replace("　",""))){
//                    log.info("elementStr[0]----"+elementStr[0]+"---elementStr[1]"+elementStr[1]+"-------"+elementStr.length);
                    //TODO 受处罚当时人名称（自然人）
                    priPerson.append(elementStr[1]).append(",");
                }
                if(elementStr.length==2 && elementStr[0].replace("　","").equals("身份证号码") ){
                    //TODO 受处罚当时人证件号码（自然人）
                    priPersonCert.append(elementStr[1]).append(",");
                }
            }


            punishOrg = "安徽保监局";
            punishDate = elementsSpan.get(0).ownText().replaceAll("　","");;

        }else if(txtAll.indexOf("受处罚机构名称：")>-1){
            Elements elementsP = elementsTxt.getElementsByTag("P");
            List listStr = new ArrayList();
            for(Element elementP : elementsP){
                String[] str = elementP.text().replace(":","：").split("：");
                if(str.length==2){
                    listStr.add(str[1]);
                }
                if(elementP.text().indexOf("〔")>-1){
                    punishNo=elementP.text().replaceAll(" ","");
                }
            }
            if(listStr.size()>=3){
                //TODO 受处罚机构
                punishToOrg = listStr.get(0).toString();
                //TODO 受处罚机构地址
                punishToOrgAddress = listStr.get(1).toString();
                //TODO 法定代表人或主要负责人
                punishToOrgHolder = listStr.get(2).toString();
            }
            punishOrg = "安徽保监局";
            String[] punishDateStr = elementsSpan.text().split("安徽保监局");
            if(punishDateStr.length==2){
                punishDate = punishDateStr[1];
            }

        }else{
            Elements elementspunishP = elementsTD.get(3).getElementsByTag("P");
            Element elementspunish = elementspunishP.first();
            //TODO 处罚机关
            String[] punishOrgListStr = elementspunish.text().split("于");
            if(punishOrgListStr.length==2){
                punishOrg = punishOrgListStr[0];
                //TODO 处罚时间
                String[] punishDateListStr = punishOrgListStr[1].split("作出");
                punishDate = punishDateListStr[0];
                //TODO 处罚文号
                String[] punishNoListStr = punishDateListStr[1].split("处罚决定");
                punishNo = punishNoListStr[0];
            }
            //TODO 受处罚机构
            String[] punishToOrgListStr = elementspunishP.get(1).text().split("经查，");
            if(punishToOrgListStr.length==2){
                punishToOrgListStr[1].split("于");
                punishToOrg = punishToOrgListStr[0];
            }
            //TODO 受处罚当时人名称（自然人）
            String[] priPersonStr = elementspunishP.text().split("（身份证号码");
            String[] priPersonLeft = priPersonStr[0].split("。");
            priPerson.append(priPersonLeft[priPersonLeft.length-1]);
            //TODO 受处罚当时人证件号码（自然人）
            if(priPersonStr.length==2){
                String[] priPersonRight = priPersonStr[1].split("）时任");
                if(priPersonRight.length==2){
                    priPersonCert.append(priPersonRight[0]);
                    //TODO 受处罚当时人职位（自然人）
                    priJob = priPersonRight[1].split("，")[0];
                }
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
        log.info("受处罚人："+priPerson);
        log.info("受处罚人证件："+priPersonCert);
        log.info("受处罚人职位："+priJob);
        log.info("受处罚人地址："+"");
        log.info("正文："+stringDetail);

        return null;
    }

 /*   @Override
    protected String execute() throws Throwable {
        //其他
//        String url = "http://www.circ.gov.cn/web/site0/tab5241/info4101493.htm";
        String url = "http://www.circ.gov.cn/web/site0/tab5241/info2554010.htm";

        String fullTxt = getData(url);
        extractContent(fullTxt);
        log.info("----------------------安徽保监局处罚信息提取完成------------------------");
        return null;
    }*/
}
