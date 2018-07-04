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
        String environDiscussPerson = "";
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
                //行政处理方式 String punishMethod = "";
                personObjectMap.put("punishMethod",punishMethod);
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
            adminPunishInsert(map);
            log.info("-----------------------------------------------------------------------------------");
        }
        log.info(document.text());
    }

    public AdminPunish adminPunishInsert(Map<String,String> map){
        AdminPunish adminPunish = new AdminPunish();
        //created_at	本条记录创建时间
        adminPunish.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        adminPunish.setUpdatedAt(new Date());
        //source	数据来源
        adminPunish.setSource(map.get("source"));
        //subject	主题
        adminPunish.setSubject(map.get("subject"));
        //url	url
        adminPunish.setUrl(map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        adminPunish.setObjectType("02");
        //enterprise_name	企业名称
        adminPunish.setEnterpriseName("");
        //enterprise_code1	统一社会信用代码
        adminPunish.setEnterpriseCode1("");
        //enterprise_code2	营业执照注册号
        adminPunish.setEnterpriseCode2("");
        //enterprise_code3	组织机构代码
        adminPunish.setEnterpriseCode3("");
        //person_name	法定代表人/负责人姓名|负责人姓名
        adminPunish.setPersonName(map.get("environDiscussPerson"));
        //person_id	法定代表人身份证号|负责人身份证号
        adminPunish.setPersonId("");
        //punish_type	处罚类型
        adminPunish.setPunishType(map.get("punishType"));
        //punish_reason	处罚事由
        adminPunish.setPunishReason(map.get("punishReason"));
        //punish_according	处罚依据
        adminPunish.setPunishAccording("");
        //punish_result	处罚结果
        adminPunish.setPunishResult("");
        //judge_no	执行文号
        adminPunish.setJudgeNo(map.get("aptitudeNo"));
        //judge_date	执行时间
        adminPunish.setJudgeDate(map.get("punishDate"));
        //judge_auth	判决机关
        adminPunish.setJudgeAuth(map.get("executeOrg"));
        //publish_date	发布日期
        adminPunish.setPublishDate(map.get("punishDate"));

        saveAdminPunishOne(adminPunish,false);
        return adminPunish;
    }

}
