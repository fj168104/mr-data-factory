package com.mr.modules.api.site.instance.boissite;

import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Component("ningxia")
@Slf4j
@Scope("prototype")
public class SiteTaskImpl_BOIS_NingXia{
    /*@Override
    protected String execute() throws Throwable {
        String url ="http://www.circ.gov.cn/web/site0/tab5241/info4103430.htm";
        extractContent(getData(url));
        return null;
    }*/
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
                .replaceAll("职 务","职务")
                .replaceAll("住 所","地址")
                .replaceAll("地 址","地址")
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
            if(punishNoStr.length==2){
                punishNo ="（"+punishNoStr[1];
            }
            //TODO 判断是否为自然人
            if(stringDetail.indexOf("身份证号：")>-1){
                if(stringDetail.indexOf("职务：")>-1){
                    priPerson.append(stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("身份证号：")));
                    priPersonCert.append(stringDetail.substring(stringDetail.indexOf("身份证号：")+5,stringDetail.indexOf("职务：")));
                    priJob.append(stringDetail.substring(stringDetail.indexOf("职务：")+3,stringDetail.indexOf("地址：")));
                    priAddress.append(stringDetail.substring(stringDetail.indexOf("地址：")+3,stringDetail.indexOf("经查")));
                }
                punishOrg = "宁夏保监局";
                String[] punishDateStr = stringDetail.split("。");
                punishDate= punishDateStr[punishDateStr.length-1].replaceAll(" ","");
                if(punishDate.indexOf("宁夏保监局")>-1){
                    punishDate = punishDate.replaceAll("宁夏保监局","");
                }
            }
            //TODO 判断是否为法人
            else if(stringDetail.indexOf("法定代表人：")>-1){

                punishToOrg = stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("地址："));
                punishToOrgAddress=stringDetail.substring(stringDetail.indexOf("地址：")+3,stringDetail.indexOf("法定代表人："));
                punishToOrgHolder = stringDetail.substring(stringDetail.indexOf("法定代表人：")+6,stringDetail.indexOf("经查"));
                punishOrg = "宁夏保监局";
                String[] punishDateStr = stringDetail.split("。");
                punishDate= punishDateStr[punishDateStr.length-1].replaceAll(" ","");
                if(punishDate.indexOf("宁夏保监局")>-1){
                    punishDate = punishDate.replaceAll("宁夏保监局","");
                }
            }else{
                priPerson.append(stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("经查")));
                punishOrg = "宁夏保监局";
                String[] punishDateStr = stringDetail.split("。");
                punishDate= punishDateStr[punishDateStr.length-1].replaceAll(" ","");
            }
        }else{
            //TODO 从标题中提取当时人
            String CurrentPersonStr = titleStr.replaceAll("行政处罚信息（","").replaceAll("）","");
            String[] CurrentPersonS = CurrentPersonStr.split("，");
            //提取法人与自然人 TODO 有法人与自然人
            if(CurrentPersonS.length>=2&& CurrentPersonS[0].length()>3){
                punishToOrg = CurrentPersonS[0];
                for(String str : CurrentPersonS){
                    priPerson.append(str);
                }
                priPerson.toString().replaceAll(punishToOrg,"");
            }
            //提取法人 TODO 只有法人
            if(CurrentPersonS.length<2&& CurrentPersonS[0].length()>3){
                punishToOrg = CurrentPersonS[0];
            }
            //提取自然人
            if(CurrentPersonS.length<2&& CurrentPersonS[0].length()<=3){
                for(String str : CurrentPersonS){
                    priPerson.append(str);
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
        log.info("正文："+stringDetail);

        return null;
    }
}
