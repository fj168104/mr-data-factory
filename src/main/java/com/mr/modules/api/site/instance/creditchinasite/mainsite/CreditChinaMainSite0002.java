package com.mr.modules.api.site.instance.creditchinasite.mainsite;

import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * @auther
 * 1.信用中国主站
 * 2.url:http://www.creditchina.gov.cn/xinxigongshi/?navPage=4
 * 3.需求：环保部公布的环评工程师不良行为记录名单
 * 4.提取内容：姓名、职业资格证书号、惩罚时间 、奖惩部门、惩罚类型、处理文号、惩罚原因
 */
@Slf4j
@Component("creditChinaMainSite0002")
public class CreditChinaMainSite0002 extends SiteTaskExtend_CreditChina{
    String url ="https://www.creditchina.gov.cn/xinxigongshi/huanbaolingyu/201804/t20180419_113582.html";
    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    @Override
    protected String execute() throws Throwable {
        extractContext(url);
        return null;
    }
    /**
     * 获取网页内容
     */
    public void extractContext(String url){
        //来源
        String source = "信用中国";
        //来源地址
        String sourceUrl = url;

        //处理文号
        String punishNo = "";
        //姓名、
        String environDiscussPerson = "";
        // 资质证号、
        String aptitudeNo = "";
        // 惩罚时间、
        String punishDate = "";
        // 奖惩部门、
        String executeOrg = "";
        // 惩罚类型、
        String punishType = "环保部公布的环评机构不良行为记录";
        // 惩罚原因
        String punishReason = "";
        //行政处理方式
        String punishMethod = "";

        Document document  = Jsoup.parse(getHtmlPage(url,1000));
        Element elementTable = document.getElementsByTag("table").first();
        Elements elementTr = elementTable.getElementsByTag("tr");
        for(Element elementTd : elementTr){
            Elements elementsTdList = elementTd.getElementsByTag("td");
            if(elementsTdList.size()==7){
                int count = Integer.valueOf(elementsTdList.get(6).text().trim());
                for(int i =0 ; i<count;i++){

                }
            }
        }
        log.info(document.text());
    }

}
