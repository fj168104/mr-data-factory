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
 * 山西保监局处罚 提取所需要的信息
 * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
 * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
 *
 */
@Slf4j
@Component("shanxi")
@Scope("prototype")
public class SiteTaskImpl_BOIS_ShanXi{
    /*@Override
    protected String execute() throws Throwable {
        String url = "http://www.circ.gov.cn/web/site0/tab5241/info4098544.htm";
        extractContent(getData(url));
        return null;
    }*/
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
        String stringDetail ="";
        Document doc = Jsoup.parse(fullTxt.replaceAll("、","，")
                .replace("(","（")
                .replace(")","）")
                .replace(":","：")
                .replace("&nbsp;","")
                .replace(" ","")
                .replace("受处罚人：","当事人：")
                .replace("处罚人：","当事人：")
                .replace("受处罚人名称：","当事人：")
                .replace("机构名称：","当事人：")
                .replace("受处罚机构：","当事人：")
                .replace("姓名：","当事人：")
                .replace("住址：","地址：")
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

        String[] punishNoStr = titleStr.split("处罚决定书");
        if(punishNoStr.length==2){
            punishNo =punishNoStr[1].replaceAll("　","").trim();
        }
        if(elementsSpan.text().indexOf("晋保监罚字")>-1&&elementsSpan.select("DIV").text().indexOf("晋保监罚字")>-1){
            punishNo =elementsSpan.select("DIV").text().replace("　","").trim();
        }
        /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/
        if(stringDetail.indexOf("当事人：")>-1){
            //TODO 默认值
            punishOrg = "山西监管局";
            List<String> listStr = new ArrayList();
            //TODO 判断是否为法人
            for(Element elementP : elementsP){
                String elementPStr =  elementP.text().replaceAll("　","").replaceAll("职(.*)务","职务").replaceAll("性别：(.*)","").trim();

                if(elementPStr.indexOf("：")>-1&&elementP.text().split("：").length==2){
                    listStr.add(elementP.text().replaceAll("　","").trim());
                }
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

                }
                //处理当事人，地址，负责人在同一个P标签中的情况
                if(elementPStr.indexOf("：")>-1&&elementPStr.split("：").length==4&&elementPStr.indexOf("负责人")>-1){
                    listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("地址") ));
                    listStr.add(elementPStr.substring(elementPStr.indexOf("地址"),elementPStr.indexOf("负责人") ));
                    listStr.add(elementPStr.substring(elementPStr.indexOf("负责人"),elementPStr.length()-1 ));
                }
                //处理当事人，身份证号，职务，地址在同一个P标签中的情况
                if(elementPStr.indexOf("：")>-1&&elementPStr.split("：").length==5&&elementPStr.indexOf("身份证号")>-1){
                    listStr.add(elementPStr.substring(elementPStr.indexOf("当事人"),elementPStr.indexOf("身份证号") ));
                    listStr.add(elementPStr.substring(elementPStr.indexOf("身份证号"),elementPStr.indexOf("职务") ));
                    listStr.add(elementPStr.substring(elementPStr.indexOf("职务"),elementPStr.indexOf("职务") ));
                    listStr.add(elementPStr.substring(elementPStr.indexOf("地址"),elementPStr.length()-1 ));
                }

                if(elementPStr.indexOf("年")>-1 && elementPStr.indexOf("月")>-1&&elementPStr.indexOf("日")>-1){
                    punishDate = elementPStr.replaceAll(" ","").trim();
                    if(punishDate.indexOf("山西保监局")>-1){
                        punishDate = punishDate.replace("山西保监局","");
                    }
                }
                if(elementP.text().indexOf("晋保监罚")>-1){
                    punishNo = elementP.text().replaceAll(" ","").trim();
                }
            }
            //如果P标签中没有时间，事件在A标签中，需要获取A标签中的时间
            for(Element elementA : elementsA){
                String elementAStr =  elementA.text().replaceAll("　","").trim();
                if(elementAStr.indexOf("年")>-1 && elementAStr.indexOf("月")>-1&&elementAStr.indexOf("日")>-1){
                    punishDate = elementAStr.replaceAll(" ","").trim();
                }
            }
            for (Element elementPre : elementsSpanPRE){
                String elementPreStr =  elementPre.text().replaceAll("　","").trim();
                if(elementPreStr.indexOf("年")>-1 && elementPreStr.indexOf("月")>-1&&elementPreStr.indexOf("日")>-1){
                    punishDate = elementPreStr.replaceAll(" ","").trim();
                }
            }

            //TODO 需要判断是法人还是自然人
            boolean busiPersonFlag = false;
            log.info("listStr:-------"+listStr.toString());
            for(int i=0;i<listStr.size();i++ ){
                String[] currentPersonStr  = listStr.get(i).split("：");
                //TODO 法人
                if(currentPersonStr[1].length()>5&&currentPersonStr[0].equals("当事人")){
                    busiPersonFlag =true;
                    punishToOrg.append(currentPersonStr[1]).append("，");
                }
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
