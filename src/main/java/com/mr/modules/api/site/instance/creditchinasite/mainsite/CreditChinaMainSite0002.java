package com.mr.modules.api.site.instance.creditchinasite.mainsite;

import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Map<String,String>> listPersonObjectMap = new ArrayList<>();
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
        String punishDate = "2018年3月26日";
        // 奖惩部门、
        String executeOrg = "";
        // 惩罚类型、
        String punishType = "受到环保部门两次及以上行政处理的环评工程师名单记录";
        // 惩罚原因
        String punishReason = "";
        //行政处理方式
        String punishMethod = "";

        Document document  = Jsoup.parse(getHtmlPage(url,1000));
        Element elementTable = document.getElementsByTag("table").first();
        Elements elementTrs = elementTable.getElementsByTag("tr");
        for(int i =0;i<elementTrs.size();i++){
            if(i>0){
                Elements elementsTdList = elementTrs.get(i).getElementsByTag("td");
                if(elementsTdList.size() ==7){
                    punishNo = elementsTdList.get(2).text();
                    aptitudeNo = elementsTdList.get(2).text();
                    environDiscussPerson = elementsTdList.get(1).text();
                    // 奖惩部门、String executeOrg = "";
                    executeOrg = elementsTdList.get(3).text();
                    punishReason = elementsTdList.get(4).text();
                    punishMethod = elementsTdList.get(5).text();
                }else{
                    executeOrg = elementsTdList.get(0).text();
                    punishReason = elementsTdList.get(1).text();
                    punishMethod = elementsTdList.get(2).text();
                }
                //int count = Integer.valueOf(elementsTdList.get(6).text().trim());
                Map<String,String> personObjectMap = new HashMap<>();
                //来源 String source = "信用中国";
                personObjectMap.put("source",source);
                //来源地址 String sourceUrl = url;
                personObjectMap.put("sourceUrl",sourceUrl);
                //处理文号 String punishNo = "";
                personObjectMap.put("punishNo",punishNo);
                //姓名、String environDiscussPerson = "";
                personObjectMap.put("environDiscussPerson",environDiscussPerson);
                // 资质证号、String aptitudeNo = "";
                personObjectMap.put("aptitudeNo",aptitudeNo);
                // 惩罚时间、String punishDate = "";
                personObjectMap.put("punishDate",punishDate);
                // 奖惩部门、String executeOrg = "";
                personObjectMap.put("executeOrg",executeOrg);
                // 惩罚类型、String punishType = "环保部公布的环评机构不良行为记录";
                personObjectMap.put("punishType",punishType);
                // 惩罚原因 String punishReason = "";
                personObjectMap.put("punishReason",punishReason);
                //行政处理方式 String punishMethod = "";
                personObjectMap.put("punishMethod",punishMethod);
                listPersonObjectMap.add(personObjectMap);
            }
        }
        for(Map<String,String> map : listPersonObjectMap){
            log.info(
                    "来源："+map.get("source")+"\n"
                            +"来源地址："+map.get("sourceUrl")+"\n"
                            +"处罚证书："+ map.get("punishNo")+"\n"
                            +"姓名："+ map.get("environDiscussPerson")+"\n"
                            +"资质证号："+  map.get("aptitudeNo")+"\n"
                            +"惩罚时间："+  map.get("punishDate")+"\n"
                            +"奖惩部门："+  map.get("executeOrg")+"\n"
                            +"惩罚类型："+  map.get("punishType")+"\n"
                            +"惩罚原因："+  map.get("punishReason")+"\n"
                            +"行政处理方式："+  map.get("punishMethod")+"\n" );
            log.info("-----------------------------------------------------------------------------------");
        }
        log.info(document.text());
    }

}
