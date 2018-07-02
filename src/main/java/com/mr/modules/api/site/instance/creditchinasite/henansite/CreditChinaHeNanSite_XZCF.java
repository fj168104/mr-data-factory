package com.mr.modules.api.site.instance.creditchinasite.henansite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 信用中国（河南）
 * 主题：行政处罚
 * 属性：处罚号 案件名称 行政相对人名称 处罚机关
 * url：http://www.xyhn.gov.cn/ca/20160419000001.htm
 * 全量查询 urlAllResult:
 * http://www.xyhn.gov.cn/CMSInterface/cms/xzcflist?pagesize=200&page=1
 * 精确查询 urlSelectResult:
 * http://www.xyhn.gov.cn/CMSInterface/cms/cmsSelectListxzcf?pagesize=20&content=%E4%BF%A1%E9%98%B3%E5%B8%82%E6%B5%89%E6%B2%B3%E5%8C%BA%E6%9D%8E%E6%96%B9%E8%94%AC%E8%8F%9C%E9%85%8D%E9%80%81%E9%83%A8
 */

@Slf4j
@Component("creditchinahenansite_xzcf")
@Scope("prototype")
public class CreditChinaHeNanSite_XZCF extends SiteTaskExtend_CreditChina {
    @Autowired
    SiteParams siteParams;
    int pagesize = 10, start = 1, type = 2;
    String ip = "", port = "";
    //作为上下文关键字 content="";
    String keyWord = "";

    @Override
    protected String execute() throws Throwable {
        keyWord = siteParams.map.get("keyWord");
        webContext(keyWord, ip, port);
        return super.execute();
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    /**
     * 获取页码列表信息
     *
     * @param keyWord
     * @param ip
     * @param port
     */
    public void webContext(String keyWord, String ip, String port) {
        //pagesize=200&page=1
        String baseAllUrl = "http://www.xyhn.gov.cn/CMSInterface/cms/xzcflist?pagesize=200&page=1";
        //pagesize=20&content=keyWord
        String baseSelectUrl = "http://www.xyhn.gov.cn/CMSInterface/cms/cmsSelectListxzcf?pagesize=200&content=";
        try {
            String urlMain = "";
            if(keyWord!=null&&!"".equals(keyWord)){
                urlMain = baseSelectUrl+ URLEncoder.encode(keyWord, "UTF-8");
            }else{
                urlMain = baseAllUrl;
            }

            WebClient webClient = createWebClient(ip, port);
            WebRequest request = new WebRequest(new URL(urlMain), HttpMethod.POST);
            Map<String, String> additionalHeaders = new HashMap<String, String>();
            additionalHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
            additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8");
            additionalHeaders.put("Accept", "application/json, text/javascript, */*; q=0.01");
            request.setAdditionalHeaders(additionalHeaders);
            // 获取某网站页面
            Page page = webClient.getPage(request);
            // System.out.println(Page.getWebResponse().getContentAsString());
            ObjectMapper mapper = new ObjectMapper();
            WebResponse response = page.getWebResponse();
            String mapStr = new String(response.getContentAsString().getBytes("ISO-8859-1"), "UTF-8");
            Map map = mapper.readValue(mapStr, Map.class);
            if (map.size() > 0) {
                List<Map> listMap = (List) map.get("data");
                //解析第一页数据
                for (Map mapResult : listMap) {
                    mapResult.put("sourceUrl", baseSelectUrl + (String) mapResult.get("cf_xdr_mc"));
                    adminPunishInsert(mapResult);
                    log.info("对象名称：" + mapResult.get("cf_xdr_mc") + "=============对象代码" + mapResult.get("cf_wsh"));
                }
                int totalPage = (int) map.get("totalPage");
                if (totalPage > 1) {
                    for (int i = 2; i <= totalPage; i++) {
                        if(keyWord!=null&&!"".equals(keyWord)){
                            urlMain = baseSelectUrl+URLEncoder.encode(keyWord, "UTF-8")+"&page="+i;
                        }else{
                            urlMain = "http://www.xyhn.gov.cn/CMSInterface/cms/xzcflist?pagesize=200&page="+i;
                        }

                        WebRequest request0 = new WebRequest(new URL(urlMain), HttpMethod.POST);
                        Map<String, String> additionalHeaders0 = new HashMap<String, String>();
                        additionalHeaders0.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
                        additionalHeaders0.put("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8");
                        additionalHeaders0.put("Accept", "application/json, text/javascript, */*; q=0.01");
                        request0.setAdditionalHeaders(additionalHeaders0);
                        // 获取某网站页面
                        Page page0 = webClient.getPage(request0);
                        // System.out.println(Page.getWebResponse().getContentAsString());
                        ObjectMapper mapper0 = new ObjectMapper();
                        WebResponse response0 = page0.getWebResponse();
                        String mapStr0 = new String(response0.getContentAsString().getBytes("ISO-8859-1"), "UTF-8");
                        Map map0 = mapper0.readValue(mapStr0, Map.class);
                        List<Map> listMap0 = (List) map0.get("data");
                        //解析第一页数据
                        for (Map mapResult : listMap0) {
                            mapResult.put("sourceUrl", baseSelectUrl + (String) mapResult.get("cf_xdr_mc"));
                            adminPunishInsert(mapResult);
                            log.info("对象名称：" + mapResult.get("cf_xdr_mc") + "=============对象代码" + mapResult.get("cf_wsh"));
                        }
                    }
                }
            }
            webClient.close();
        } catch (IOException e) {
            log.error("获取网页异常···" + e.getMessage());
        }


    }
    public AdminPunish adminPunishInsert(Map<String,String> map){
        AdminPunish adminPunish = new AdminPunish();
        //created_at	本条记录创建时间
        adminPunish.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        adminPunish.setUpdatedAt(new Date());
        //source	数据来源
        adminPunish.setSource("信用中国（河南）");
        //subject	主题
        adminPunish.setSubject("行政处罚");
        //url	url
        adminPunish.setUrl(map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        adminPunish.setObjectType(map.get("cf_xdr_mc").toString().trim().length()<6?"02":"01");
        //enterprise_name	企业名称
        adminPunish.setEnterpriseName(map.get("cf_xdr_mc").trim().length()<6?"":map.get("cf_xdr_mc"));
        //enterprise_code1	统一社会信用代码--cfXdrShxym
        adminPunish.setEnterpriseCode1("");
        //enterprise_code2	营业执照注册号
        adminPunish.setEnterpriseCode2("");
        //enterprise_code3	组织机构代码
        adminPunish.setEnterpriseCode3("");
        //person_name	法定代表人/负责人姓名|负责人姓名
        adminPunish.setPersonName(map.get("cf_xdr_mc").trim().length()<6?map.get("cf_xdr_mc"):"");
        //person_id	法定代表人身份证号|负责人身份证号
        adminPunish.setPersonId("");
        //punish_type	处罚类型
        adminPunish.setPunishType("");
        //punish_reason	处罚事由
        adminPunish.setPunishReason(map.get("cf_cfmc"));
        //punish_according	处罚依据
        adminPunish.setPunishAccording("");
        //punish_result	处罚结果
        adminPunish.setPunishResult("");
        //judge_no	执行文号
        adminPunish.setJudgeNo(map.get("cf_wsh"));
        //judge_date	执行时间
        adminPunish.setJudgeDate("");
        //judge_auth	判决机关
        adminPunish.setJudgeAuth("");
        //publish_date	发布日期
        adminPunish.setPublishDate("");

        saveAdminPunishOne(adminPunish,false);
        return adminPunish;
    }
}