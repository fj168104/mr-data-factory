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
/**
 *@ auther :zjxu
 *@ dateTime : 201803
 * 宁波保监局处罚 提取所需要的信息
 * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
 * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
 */
@Component("ningbo")
@Slf4j
@Scope("prototype")
public class SiteTaskImpl_BOIS_NingBo{
    /*@Override
    protected String execute() throws Throwable {
//        String url = "http://www.circ.gov.cn/web/site0/tab5241/info4054743.htm";
//        String url = "http://www.circ.gov.cn/web/site0/tab5241/info4056486.htm";
//        String url = "http://www.circ.gov.cn/web/site0/tab5241/info219398.htm";
        String url = "http://www.circ.gov.cn/web/site0/tab5241/info4096833.htm";
        extractContent(getData(url));
        return null;
    }*/

    public Map extractContent(String fullTxt) {
        //发布机构
        String publishOrg = "中国保监会宁波监管局行政处";
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
        stringDetail =elementsSpan.text().replaceAll("住所地","住址").replaceAll("主要负责人","负责人")
                .replaceAll("住所","住址").replaceAll("地址","住址")
                .replaceAll("身份证号：","身份证号码：").replaceAll("：","：").replaceAll(":","：");
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
            if(stringDetail.indexOf("当事人：")>-1){
                if(stringDetail.indexOf("职务：")>-1){
                    priPerson.append(stringDetail.substring(stringDetail.indexOf("当事人：")+4,stringDetail.indexOf("身份证号码：")));
                    priPersonCert.append(stringDetail.substring(stringDetail.indexOf("身份证号码：")+6,stringDetail.indexOf("职务：")));

                    priJob.append(stringDetail.substring(stringDetail.indexOf("职务：")+3,stringDetail.indexOf("住址：")));
                    priAddress.append(stringDetail.substring(stringDetail.indexOf("住址：")+3,stringDetail.indexOf("依据《")));


                }else{

                    punishToOrg = stringDetail.substring(stringDetail.indexOf("当事人：")+3,stringDetail.indexOf("住址："));
                    if(stringDetail.indexOf("负责人：")>-1){
                        punishToOrgAddress=stringDetail.substring(stringDetail.indexOf("住址：")+3,stringDetail.indexOf("负责人："));
                        punishToOrgHolder=stringDetail.substring(stringDetail.indexOf("负责人：")+4,stringDetail.indexOf("依据《"));
                    }else{
                        punishToOrgHolder=stringDetail.substring(stringDetail.indexOf("住址：")+4,stringDetail.indexOf("依据《"));
                    }


                }
            }
            Elements elementsP = elementsTxt.getElementsByTag("P");
            int elementsPSize = elementsP.size();
            punishOrg = elementsP.get(elementsPSize-2).text().replaceAll(" ","").trim();
            punishDate = elementsP.get(elementsPSize-1).text().replaceAll(" ","").trim();
            if(punishDate.equals("")){
                punishOrg = elementsP.get(elementsPSize-3).text().replaceAll(" ","").trim();
                punishDate = elementsP.get(elementsPSize-2).text().replaceAll(" ","").trim();
            }
            if(punishDate.indexOf("宁波保监局")>-1){
                punishOrg = "宁波保监局";
                String[] punishDateStr = punishDate.split("宁波保监局");
                if(punishDateStr.length==2){
                    punishDate = punishDateStr[1];
                }
            }
        }
        if(elementsSpan.text().indexOf("受处罚机构：")>-1){
            if(stringDetail.indexOf("住所：")>-1){
                punishToOrg = stringDetail.substring(stringDetail.indexOf("受处罚机构：")+6,stringDetail.indexOf("住址："));
                punishToOrgAddress=stringDetail.substring(stringDetail.indexOf("住址：")+3,stringDetail.indexOf("负责人："));
            }
            if(stringDetail.indexOf("住址：")>-1){
                punishToOrg = stringDetail.substring(stringDetail.indexOf("受处罚机构：")+6,stringDetail.indexOf("住址："));
                punishToOrgAddress=stringDetail.substring(stringDetail.indexOf("住址：")+3,stringDetail.indexOf("负责人："));
            }
            punishToOrgHolder=stringDetail.substring(stringDetail.indexOf("负责人：")+4,stringDetail.indexOf("   经"));
            Elements elementsP = elementsTxt.getElementsByTag("P");
            punishOrg = "宁波保监局";
            punishDate =elementsP.get(elementsP.size()-1).text().replaceAll(" ","");
        }

        if(elementsSpan.text().indexOf("受处罚人：")>-1){
            if(stringDetail.indexOf("身份证号码：")>-1){
                priPerson.append(stringDetail.substring(stringDetail.indexOf("受处罚人：")+4,stringDetail.indexOf("身份证号码：")));
                priPersonCert.append(stringDetail.substring(stringDetail.indexOf("身份证号码：")+6,stringDetail.indexOf("住址：")));
                priAddress.append(stringDetail.substring(stringDetail.indexOf("住址：")+3,stringDetail.indexOf("　经查")));
            }else{
                priPerson.append(stringDetail.substring(stringDetail.indexOf("受处罚人：")+4,stringDetail.indexOf("住址：")));
                priAddress.append(stringDetail.substring(stringDetail.indexOf("住址：")+3,stringDetail.indexOf("　经查")));
            }
            Elements elementsP = elementsTxt.getElementsByTag("P");
            punishOrg = "宁波保监局";
            punishDate =elementsP.get(elementsP.size()-1).text().replaceAll(" ","");
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
