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
@Slf4j
@Component("anhui")
@Scope("prototype")
public  class SiteTaskImpl_BOIS_AnHui extends SiteTaskExtend{
    /**
     * 安徽保监局处罚 提取所需要的信息
     * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
     * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
     */
    public Map extractContent(String fullTxt) {
        //TODO 处罚机关
        String punishOrg ="";
        //TODO 处罚时间
        String punishDate = "";
        //TODO 处罚文号
        String  punishNo = "";
        //TODO 受处罚机构
        String punishToOrg = "";
        //TODO 受处罚当时人名称（自然人）
        String priPerson = "";
        //TODO 受处罚当时人证件号码（自然人）
        String priPersonCert = "";
        //TODO 受处罚当时人职位（自然人）
        String priJob = "";
//        log.info("原文："+fullTxt);
        Document doc = Jsoup.parse(fullTxt.replace("、","，").replace("(","（").
                replace(")","）"));
        Element elementsTxt = doc.getElementById("tab_content");
        //全文提取
        String txtAll = elementsTxt.text();
        log.info("全文："+txtAll);
        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        /*TODO 通用型*/
        //TODO 提取主题
        Element elementsTitle = elementsTD.first();
        String titleStr = elementsTitle.text();
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        String  publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+1,publishDateStr.indexOf("分享到："));

        /*TODO 特殊型 只适合没有标明当事人的处罚文案，需要加限制条件*/
        if(txtAll.indexOf("当事人：")>-1){

        }else if(txtAll.indexOf("受处罚人姓名：")>-1){

        }else if(txtAll.indexOf("受处罚机构名称：")>-1){

        }else{
            Elements elementspunishP = elementsTD.get(3).getElementsByTag("P");
            Element elementspunish = elementspunishP.first();
            //TODO 处罚机关
            String[] punishOrgListStr = elementspunish.text().split("于");
            punishOrg = punishOrgListStr[0];
            //TODO 处罚时间
            String[] punishDateListStr = punishOrgListStr[1].split("作出");
            punishDate = punishDateListStr[0];
            //TODO 处罚文号
            String[] punishNoListStr = punishDateListStr[1].split("处罚决定");
            punishNo = punishNoListStr[0];
            //TODO 受处罚机构
            String[] punishToOrgListStr = elementspunishP.get(1).text().split("经查，")[1].split("于");
            punishToOrg = punishToOrgListStr[0];
            //TODO 受处罚当时人名称（自然人）
            String[] priPersonStr = elementspunishP.text().split("（身份证号码");
            String[] priPersonLeft = priPersonStr[0].split("。");
            priPerson = priPersonLeft[priPersonLeft.length-1];
            //TODO 受处罚当时人证件号码（自然人）
            String[] priPersonRight = priPersonStr[1].split("）时任");
            priPersonCert = priPersonRight[0];
            //TODO 受处罚当时人职位（自然人）
            priJob = priPersonRight[1].split("，")[0];
        }

        log.info("发布主题："+titleStr);
        log.info("发布机构："+punishOrg);
        log.info("发布时间："+publishDate);
        log.info("处罚机关："+punishOrg);
        log.info("处罚时间："+punishDate);
        log.info("处罚文号："+punishNo);
        log.info("受处罚机构："+punishToOrg);
        log.info("受处罚机构地址："+"");
        log.info("受处罚机构负责人："+"");
        log.info("受处罚人："+priPerson);
        log.info("受处罚人证件："+priPersonCert);
        log.info("受处罚人职位："+priJob);
        log.info("受处罚人地址："+"");
        return null;
    }

    @Override
    protected String execute() throws Throwable {
        String url = "http://www.circ.gov.cn/web/site0/tab5241/info4101493.htm";
        String fullTxt = getData(url);
        extractContent(fullTxt);
        log.info("----------------------安徽保监局处罚信息提取完成------------------------");
        return null;
    }
}
