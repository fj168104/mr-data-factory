package com.mr.modules.api.site.instance.boissite;

import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.util.ParseDaLian;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("dalian")
@Scope("prototype")
public class SiteTaskImpl_BOIS_DaLian{
    /*@Override
    protected String execute() throws Throwable {
        String url = "http://dalian.circ.gov.cn/web/site12/tab3429/info104927.htm";
        extractContent(getData(url));
        return null;
    }*/

    public Map extractContent(String fullTxt) {
        //发布机构
        String publishOrg = "中国保监会大连保监局行政处";
        //发布时间
        String publishDate = "";
        //TODO 处罚机关（由于有些页面没有，所以暂且给予默认值）
        String punishOrg = "大连保监局";
        //TODO 处罚时间
        String punishDate = "";
        //TODO 处罚文号
        String punishNo = "";
        //TODO 受处罚机构
        String punishToOrg = "";
        //TODO 受处罚机构地址
        String punishToOrgAddress = "";
        //TODO 法定代表人或主要负责人
        String punishToOrgHolder = "";
        //TODO 受处罚当时人名称（自然人）
        StringBuffer priPerson = new StringBuffer();
        //TODO 受处罚当时人证件号码（自然人）
        StringBuffer priPersonCert = new StringBuffer();
        //TODO 受处罚当时人职位（自然人）
        StringBuffer priJob = new StringBuffer();
        //TODO 受处罚当时人地址（自然人）
        StringBuffer priAddress = new StringBuffer();
        //TODO 判断处罚的是法人，还是自然人
        String priBusiType = "";
        String stringDetail = "";

        String titleStr = "";

        Map resMap = new ParseDaLian().parseInfo(fullTxt);
        publishDate = (String) resMap.get("publishDate");
        punishDate = (String) resMap.get("punishDate");
        punishNo = (String) resMap.get("punishNo");
        punishToOrg = (String) resMap.get("punishToOrg");
        punishToOrgAddress = (String) resMap.get("punishToOrgAddress");
        punishToOrgHolder = (String) resMap.get("punishToOrgHolder");
        priPerson = (StringBuffer) resMap.get("priPerson");
        priPersonCert = (StringBuffer) resMap.get("priPersonCert");
        priJob = (StringBuffer) resMap.get("priJob");
        priAddress = (StringBuffer) resMap.get("priAddress");
        stringDetail = (String) resMap.get("stringDetail");
        titleStr = (String) resMap.get("titleStr");

        log.info("发布主题：" + titleStr);
        log.info("发布机构：" + publishOrg);
        log.info("发布时间：" + publishDate);
        log.info("处罚机关：" + punishOrg);
        log.info("处罚时间：" + punishDate);
        log.info("处罚文号：" + punishNo);
        log.info("受处罚机构：" + punishToOrg);
        log.info("受处罚机构地址：" + punishToOrgAddress);
        log.info("受处罚机构负责人：" + punishToOrgHolder);
        log.info("受处罚人：" + priPerson);
        log.info("受处罚人证件：" + priPersonCert);
        log.info("受处罚人职位：" + priJob);
        log.info("受处罚人地址：" + priAddress);
        log.info("正文：" + stringDetail);

        Map<String,String> map = new HashMap<String,String>();
        map.put("titleStr",titleStr);
        map.put("publishOrg",publishOrg);
        map.put("publishDate",publishDate);
        map.put("punishOrg",punishOrg);
        map.put("punishDate",punishDate);
        map.put("punishNo",punishNo);
        map.put("punishToOrg",punishToOrg);
        map.put("punishToOrgAddress",punishToOrgAddress);
        map.put("punishToOrgHolder",punishToOrgHolder);
        map.put("priPerson",priPerson.toString());
        map.put("priPersonCert",priPersonCert.toString());
        map.put("priJob",priJob.toString());
        map.put("priAddress","");
        map.put("stringDetail",stringDetail);

        return map;
    }
}
