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
* 深圳保监局处罚 提取所需要的信息
* 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
* 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
*/
@Slf4j
@Component("shenzhen")
@Scope("prototype")
public class SiteTaskImpl_BOIS_ShenZhen extends SiteTaskExtend{
    @Override
    protected String execute() throws Throwable {
        String url = "http://www.circ.gov.cn/web/site0/tab5241/info186612.htm";
        extractContent(getData(url));
        return null;
    }
    public Map extractContent(String fullTxt) {
        //发布机构
        String publishOrg = "中国保监会深圳保监局行政处";
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
        //正文
        String stringDetail ="";
        Document doc = Jsoup.parse(fullTxt.replaceAll("、","，")
                .replace("(","（")
                .replace(")","）")
                .replace(":","：")
                .replace("&nbsp;","")
                .replace(" ","")
                .replace("简称：","简称:")//避免简称被替换掉：TODO 如：以下简称：国华人寿滨州中支
                .replace("当 事 人：","当事人：")
                .replace("受处罚人姓名：","当事人：")
                .replace("受处罚人名称：","当事人：")
                .replace("拟被处罚人：","当事人")
                .replace("受处罚人：","当事人")
                .replace("被处罚人：","当事人：")
                .replace("拟被处罚机构名称：","当事人：")
                .replace("被处罚机构名称：","当事人：")
                .replace("被处罚机构：","当事人：")
                .replace("住址：","地址：")
                .replace("营业地址：","地址：")
                .replace("住址：","地址：")
                .replace("住 址：","地址：")
                .replace("地 址：","地址：")
                .replace("职　务：","职务：")
                .replace("职 务：","职务：")
                .replace("主要负责人：","负责人：")
                .replace("法定代表人：","负责人：")
                .replace("主要负责人姓名：","负责人：")
                .replace("单位负责人：","负责人：")
                .replace("身份证号码：","身份证号：")
                .replace("身份证号码","身份证号")
                .replace("台胞证号：","身份证号：")
                .replace("证件号：","身份证号：")
                .replace("护照号：","身份证号：")
                .replace("当事人：","当事人")//解除部分当事人中没有“：”的情况
                .replace("当事人","当事人：")


        );
        //TODO 全文
        Element elementsTxt = doc.getElementById("tab_content");
        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Elements elementsSpan = elementsTxt.getElementsByClass("xilanwb");
        Elements elementsSpanTR = elementsSpan.select("TR");
        log.info("elementsSpanTR:"+elementsSpanTR.size());
        Elements elementsSpanP = elementsSpan.select("P");
        log.info("elementsSpanP:"+elementsSpanP.size());
        Elements elementsSpanChild = elementsSpan.select("span");
        Element elementsSpanChildStr = elementsSpanChild.get(0);//获取SPAN标签中存储了当事人信息的情况数据
        Elements elementsP = elementsTxt.getElementsByTag("P");
        Elements elementsA = elementsTxt.getElementsByTag("A");
        //TODO 正文
        stringDetail =elementsSpan.text();
//        log.info("stringDetail:"+stringDetail);
        /*TODO 通用型*/
        //TODO 提取主题
        Element elementsTitle = elementsTD.first();
        String titleStr = elementsTitle.text();
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+5,publishDateStr.indexOf("分享到："));

        //Span 标签 ClassName：xilanwb  中存在P标签，不存在TR标签
        if(elementsSpanP.size()>0&&elementsSpanTR.size()==0){
            //TODO 正文中没有文号
            if(elementsSpan.get(0).text().indexOf("深保监罚")>-1){
                punishNo=elementsP.get(0).text().replaceAll("　","").trim();
            }else{
                String[] punishNoStr = titleStr.split("（");
                if(punishNoStr.length==2){
                    punishNo ="（"+punishNoStr[1].replaceAll("　","").trim();
                }
            }
            /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/
            if(stringDetail.indexOf("当事人：")>-1||elementsSpanChildStr.text().indexOf("当事人：")>-1){
                //TODO 默认值
                punishOrg = "深圳监管局";
                List<String> listStr = new ArrayList();
                listStr.add(elementsSpanChildStr.text());
                //TODO 判断是否为法人
                for(Element elementP : elementsP){
                    String elementPStr =  elementP.text().replaceAll("　","").trim();
                    if(elementP.text().indexOf("：")>-1&&elementP.text().trim().split("：").length>1){
                        listStr.add(elementP.text().replaceAll("　","").trim());
                    }
                    if(elementPStr.indexOf("年")>-1 && elementPStr.indexOf("月")>-1&&elementPStr.indexOf("日")>-1){
                        punishDate = elementPStr.replaceAll(" ","").trim();
                    }
                    if(elementP.text().indexOf("深保监罚〔")>-1){
                        punishNo = elementP.text().replaceAll(" ","").trim();
                    }
                }
                //如果P标签中没有事件，事件在A标签中，需要获取A标签中的时间
                for(Element elementA : elementsA){
                    String elementAStr =  elementA.text().replaceAll("　","").trim();
                    if(elementAStr.indexOf("年")>-1 && elementAStr.indexOf("月")>-1&&elementAStr.indexOf("日")>-1){
                        punishDate = elementAStr.replaceAll(" ","").trim();
                    }
                }
                //TODO 需要判断是法人还是自然人
                boolean busiPersonFlag = false;
                log.info("listStr:-------"+listStr.toString());
                for(int i=0;i<listStr.size();i++ ){
                    String[] currentPersonStr  = listStr.get(i).split("：");

                    if(currentPersonStr[1].length()>5&&currentPersonStr[0].equals("当事人")){
                        busiPersonFlag =true;
                        punishToOrg = currentPersonStr[1];
                    }
                    if(currentPersonStr[1].length()<=5&&currentPersonStr[0].equals("当事人")){
                        busiPersonFlag =false;
                        priAddress.append(currentPersonStr[1]);
                    }
                    // TODO 法人
                    if(busiPersonFlag==true&&currentPersonStr[0].trim().equals("地址")){
                        punishToOrgAddress = currentPersonStr[1];
                    }
                    if(busiPersonFlag==true&&currentPersonStr[0].trim().equals("负责人")){
                        punishToOrgHolder = currentPersonStr[1];
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
        }
        //Span 标签 ClassName：xilanwb  中存在TR标签，不存在TR标签
        if(elementsSpanP.size()==0&&elementsSpanTR.size()>0){
            int countTD = 0;
            for(Element elementTR :elementsSpanTR){
                Elements elementsTRTD = elementTR.getElementsByTag("TD");
                if(countTD>0 && elementsTRTD.size()==6){
                    punishNo = elementsTRTD.get(0).text();
                    if(elementsTRTD.get(1).text().length()<=5){
                        priPerson.append(elementsTRTD.get(1).text());
                    }
                    if(elementsTRTD.get(1).text().length()>5){
                        punishOrg = elementsTRTD.get(1).text();
                    }
                    punishDate = elementsTRTD.get(5).text();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Element elementTD : elementsTRTD){
                        stringBuffer.append(elementTD).append("\n");
                    }
                }
                countTD++;
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
