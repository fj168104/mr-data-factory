package com.mr.modules.api.site.instance.creditchinasite.mainsite;

import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @auther
 * 1.信用中国主站
 * 2.url:http://www.creditchina.gov.cn/xinxigongshi/?navPage=4
 * 3.需求：环保部公布的环评工程师不良行为记录名单
 * 4.提取内容：姓名、职业资格证书号、惩罚时间 、奖惩部门、惩罚类型、处理文号、惩罚原因
 */
@Slf4j
@Component("creditchinamainsite0002")
public class CreditChinaMainSite0002 extends SiteTaskExtend_CreditChina{
    String url ="https://www.creditchina.gov.cn/xinxigongshi/huanbaolingyu/201804/t20180419_113582.html";

    @Autowired
    AdminPunishMapper adminPunishMapper;
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
        //主题
        String subject = "受到环保部门两次及以上行政处理的环评工程师名单记录";
        //来源地址
        String sourceUrl = url;

        //处理文号
        String punishNo = "";
        //姓名、
        String personName = "";
        // 资质证号、
        String aptitudeNo = "";
        // 惩罚时间、
        String punishDate = "2018年3月26日";
        // 奖惩部门、
        String executeOrg = "";
        // 惩罚类型、
        String punishType = "";
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
                    personName = elementsTdList.get(1).text();
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
                personObjectMap.put("judgeNo",punishNo);
                //姓名、String environDiscussPerson = "";
                personObjectMap.put("personName",personName);
                // 资质证号、String aptitudeNo = "";
                personObjectMap.put("aptitudeNo",aptitudeNo);
                // 惩罚时间、String punishDate = "";
                personObjectMap.put("judgeDate",punishDate);
                // 奖惩部门、String executeOrg = "";
                personObjectMap.put("judgeAuth",executeOrg);
                //行政处理方式 String punishMethod = "";
                // 惩罚类型、String punishType = "";
                if(punishMethod.contains("通报")&&!punishMethod.contains("整改")){
                    // 惩罚类型、String punishType = "";
                    personObjectMap.put("punishType","通报");
                }else if(punishMethod.contains("整改")){
                    // 惩罚类型、String punishType = "";
                    personObjectMap.put("punishType","整改");
                }else{
                    personObjectMap.put("punishType","其他");
                }
                // 惩罚原因 String punishReason = "";
                personObjectMap.put("punishReason",punishReason);

                //主题
                personObjectMap.put("subject",subject);
                listPersonObjectMap.add(personObjectMap);
            }
        }
        for(Map<String,String> map : listPersonObjectMap){
            adminPunishInsert(map);
        }
        log.info(document.text());
    }

}
