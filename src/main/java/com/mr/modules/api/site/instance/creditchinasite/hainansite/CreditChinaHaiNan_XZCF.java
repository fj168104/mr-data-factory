package com.mr.modules.api.site.instance.creditchinasite.hainansite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 来源：信用中国（海南）
 * 地址：http://xyhn.hainan.gov.cn/hnxyweb/p/32/index.html?tabIndex=1
 * 属性：企业名称 文号 案件名称 部门 公示日期
 * 主题：行政处罚
 * apiList:http://xyhn.hainan.gov.cn/JRBWeb/jointCredit/HnZsXzcfxxSjbzMainController.do?reqCode=getXzcfInfo&pageIndex=1&pageSize=10&isIndex=2
 * apiDetail:http://xyhn.hainan.gov.cn/hnxyweb/p/39/index.html?key=%273537C5D3-2A33-401A-AB9B-7CC16E0BF869%27&stype=2
 */
@Slf4j
@Component("creditchinahainan_xzcf")
@Scope("prototype")
public class CreditChinaHaiNan_XZCF extends SiteTaskExtend_CreditChina{
    @Autowired
    SiteParams siteParams;
    @Override
    protected String execute() throws Throwable {
        //keyWord=add 为增量
        String keyWord = siteParams.map.get("keyWord");
        int sizePage = 1;
        webContext(sizePage,keyWord);
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    public void webContext(int sizePage,String keyWord){

        String url = "http://xyhn.hainan.gov.cn/JRBWeb/jointCredit/HnZsXzcfxxSjbzMainController.do?reqCode=getXzcfInfo&pageIndex="+sizePage+"&pageSize=10&isIndex=2";
        //翻页标识
        boolean nextPageFlag = true;
        //总页数
        int pageCount = 1;
        //链接失败 从试次数
        int repeatSize = 0;
        //链接标识
        boolean failFlag = false;
        while(repeatSize<6&&!failFlag){
            failFlag =true;
            try {
                WebClient webClient = createWebClient("","");
                WebRequest request = new WebRequest(new URL(url), HttpMethod.GET);
                Map<String, String> additionalHeaders = new HashMap<String, String>();
                additionalHeaders.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
                additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8");
                additionalHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                request.setAdditionalHeaders(additionalHeaders);
                // 获取某网站页面
                Page page = webClient.getPage(request);

                ObjectMapper mapper = new ObjectMapper();
                WebResponse response = page.getWebResponse();
                //获取状态码
                int statusCode= response.getStatusCode();
                if(statusCode==200){
                    String jsonString = response.getContentAsString();

                    Map mapJson = mapper.readValue(jsonString, Map.class);
                    //获取总页数
                    pageCount = (Integer) mapJson.get("pageLast");

                    //获取数据
                    List<Map> listResults = (List)mapJson.get("data");
                    for(int i =0;i<listResults.size();i++){
                        Map map = new HashMap();
                        Map mapResult = listResults.get(i);
                        //TODO 企业名称 文号 案件名称 部门 公示日期
                        //更新时间 lastupdatedate
                        map.put("publishDate",mapResult.get("lastupdatedate").toString()) ;
                        //处罚机构 CF_XZJG
                        map.put("judgeAuth",mapResult.get("CF_XZJG").toString());
                        //案件名称 CF_AJMC
                        map.put("punishReason",mapResult.get("CF_AJMC").toString());
                        //文号 CF_WSH
                        map.put("judgeNo",mapResult.get("CF_WSH").toString());
                        //企业名称 CF_XDR_MC
                        map.put("enterpriseName",mapResult.get("CF_XDR_MC").toString());
                        //公式时间 CF_SXQ
                        map.put("judgeDate",mapResult.get("CF_SXQ").toString());
                        //TODO 数据入库
                        String baseUrl ="http://xyhn.hainan.gov.cn/hnxyweb/p/39/index.html?key=%27"+mapResult.get("uuid").toString()+"%27&stype=2";
                        map.put("sourceUrl",baseUrl);
                        nextPageFlag = adminPunishInsert(map);
                    }
                }else {
                    continue;
                }
                //翻译标识

                if(pageCount>1){
                    for(int i =2;i<=pageCount;i++){
                        sizePage =i;
                        url = "http://xyhn.hainan.gov.cn/JRBWeb/jointCredit/HnZsXzcfxxSjbzMainController.do?reqCode=getXzcfInfo&pageIndex="+sizePage+"&pageSize=10&isIndex=2";
                        request = new WebRequest(new URL(url), HttpMethod.GET);
                        request.setAdditionalHeaders(additionalHeaders);
                        // 获取某网站页面
                        page = webClient.getPage(request);

                        response = page.getWebResponse();
                        //获取状态码
                        statusCode= response.getStatusCode();
                        if(statusCode==200){
                            String jsonString = response.getContentAsString();

                            Map mapJson = mapper.readValue(jsonString, Map.class);

                            //获取数据
                            List<Map> listResults = (List)mapJson.get("data");
                            for(int j =0;j<listResults.size();j++){
                                Map map = new HashMap();
                                Map mapResult = listResults.get(j);
                                //TODO 企业名称 文号 案件名称 部门 公示日期
                                //更新时间 lastupdatedate
                                map.put("publishDate",mapResult.get("lastupdatedate").toString()) ;
                                //处罚机构 CF_XZJG
                                map.put("judgeAuth",mapResult.get("CF_XZJG").toString());
                                //案件名称 CF_AJMC
                                map.put("punishReason",mapResult.get("CF_AJMC").toString());
                                //文号 CF_WSH
                                map.put("judgeNo",mapResult.get("CF_WSH").toString());
                                //企业名称 CF_XDR_MC
                                map.put("enterpriseName",mapResult.get("CF_XDR_MC").toString());
                                //公式时间 CF_SXQ
                                map.put("judgeDate",mapResult.get("CF_SXQ").toString());
                                //
                                //TODO 数据入库
                                String baseUrl ="http://xyhn.hainan.gov.cn/hnxyweb/p/39/index.html?key=%27"+mapResult.get("uuid").toString()+"%27&stype=2";
                                map.put("sourceUrl",baseUrl);
                                nextPageFlag = adminPunishInsert(map);
                            }
                            if(nextPageFlag==true&&keyWord.equals("add")){
                                break;
                            }
                        }else {
                            continue;
                        }
                    }
                }
            } catch (Throwable throwable) {
                repeatSize++;
                failFlag = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("程序...滴"+repeatSize+"次...........从试中............");
                log.error("网络连接异常···请检查！"+throwable.getMessage());
            }
        }


    }
    public boolean adminPunishInsert(Map<String,String> map){
        boolean isFlag = true;
        AdminPunish adminPunish = new AdminPunish();
        //created_at	本条记录创建时间
        adminPunish.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        adminPunish.setUpdatedAt(new Date());
        //source	数据来源
        adminPunish.setSource("信用中国（海南）");
        //subject	主题
        adminPunish.setSubject("行政处罚");
        //url	url
        adminPunish.setUrl(map.get("sourceUrl")==null?"":map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        adminPunish.setObjectType("01");
        //enterprise_name	企业名称
        adminPunish.setEnterpriseName(map.get("enterpriseName")==null?"":map.get("enterpriseName"));
        //enterprise_code1	统一社会信用代码--cfXdrShxym
        adminPunish.setEnterpriseCode1(map.get("enterpriseCode1")==null?"":map.get("enterpriseCode1"));
        //enterprise_code2	营业执照注册号
        adminPunish.setEnterpriseCode2(map.get("enterpriseCode2")==null?"":map.get("enterpriseCode2"));
        //enterprise_code3	组织机构代码
        adminPunish.setEnterpriseCode3(map.get("enterpriseCode3")==null?"":map.get("enterpriseCode3"));
        //person_name	法定代表人/负责人姓名|负责人姓名
        adminPunish.setPersonName(map.get("personName")==null?"":map.get("personName"));
        //person_id	法定代表人身份证号|负责人身份证号
        adminPunish.setPersonId(map.get("personId")==null?"":map.get("personId"));
        //punish_type	处罚类型
        adminPunish.setPunishType(map.get("punishType")==null?"":map.get("punishType"));
        //punish_reason	处罚事由
        adminPunish.setPunishReason(map.get("punishReason")==null?"":map.get("punishReason"));
        //punish_according	处罚依据
        adminPunish.setPunishAccording(map.get("punishAccording")==null?"":map.get("punishAccording"));
        //punish_result	处罚结果
        adminPunish.setPunishResult(map.get("punishResult")==null?"":map.get("punishResult"));
        //judge_no	执行文号
        adminPunish.setJudgeNo(map.get("judgeNo")==null?"":map.get("judgeNo"));
        //judge_date	执行时间
        adminPunish.setJudgeDate(map.get("judgeDate")==null?"":map.get("judgeDate"));
        //judge_auth	判决机关
        adminPunish.setJudgeAuth(map.get("judgeAuth")==null?"":map.get("judgeAuth"));
        //publish_date	发布日期
        adminPunish.setPublishDate(map.get("publishDate")==null?"":map.get("publishDate"));

        isFlag = saveAdminPunishOne(adminPunish,false);
        return isFlag;
    }

}
