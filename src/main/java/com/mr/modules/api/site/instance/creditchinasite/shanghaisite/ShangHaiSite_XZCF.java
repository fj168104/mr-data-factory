package com.mr.modules.api.site.instance.creditchinasite.shanghaisite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Auther zjxu to 201806
 *提取主题：行政处罚
 *提取属性：处罚文书号、处罚名称、处罚类型、处罚事由、处罚依据、行政相对人、处罚决定日期、处罚机关
 * 提取行政许可列表清单：resultXKUrl:http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/xklist/newxkgrid.action?search=false&nd=1530099417839&rows=10&page=1&sidx=&sord=asc&xzxdr=%E9%9F%A9%E6%B8%85%E4%BA%AE
 *
 * 提取行政许可列表详情：detailXKResult:  http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/sgsinfo/getxkinfo.action?xkid=add42068e4f74f8eb5b37d08f1adf0f5
 *
 * 无关键字提取行政处罚列表清单：resultCFUrl:  http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/cflist/newcfgrid.action?search=false&nd=1530099417844&rows=10&page=1&sidx=&sord=asc&xzxdr=%E9%9F%A9%E6%B8%85%E4%BA%AE
 * 有关键字提取行政处罚列表清单：http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/cflist/newSgsCFSearchgrid.action?search=false&nd=&rows=10&page=1&sidx=&sord=asc&xzxdr=%E5%BE%90%E5%81%A5
 *
 * 提取行政许可列表详情：detailCFResult:  http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/sgsinfo/getcfinfo.action?cfid=57632CEDE8FB7F95E0530100007F4755
 */
@Slf4j
@Component("shanghaisite_xzcf")
@Scope("prototype")
public class ShangHaiSite_XZCF extends SiteTaskExtend_CreditChina {
    @Autowired
    AdminPunishMapper adminPunishMapper;
    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    @Override
    protected String execute() throws Throwable {
        String keyWord = SiteParams.map.get("keyWord");
        SiteParams.map.clear();
        extractContext(keyWord);
        return null;
    }
    /**
     * 获取网页内容
     */
    public void extractContext(String keyWord ) throws Throwable{
        result(5,keyWord);;
    }


    @Autowired
    SiteParams siteParams;
    //处罚编号
    String cfid = "561FE6943287636AE0530100007F0A4F";
    //查询关键中 TODO 请输入行政相对人名称（法人名称或者自然人姓名）
    String keyWord =siteParams.map.get("keyWord");
    int sizePage = 1;
    //提取行政处罚列表清单
    //String resultCFUrl  = "http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/cflist/newcfgrid.action?search=false&nd=&rows=10&page="+sizePage+"&sidx=&sord=asc&xzxdr="+keyWord;
    //提取行政许可列表详情
    //String detailCFResult = "http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/sgsinfo/getcfinfo.action?cfid="+cfid;

    /**
     * 获取api接口相应结果清单
     * @param keyWord       关键字
     * @param sizePage  遍历次数
     * @param webClient  HTMLUnit 客户端
     * @return
     */
    public List<String> publicityInfoSearch(String keyWord , int sizePage, WebClient webClient) {
        //用于存放处罚cfid
        List<String> cfidList = new ArrayList<>();
        try {
            //浏览器的中文需要转码  如：URLEncoder.encode("绩溪县汇通汽车运输有限公司", "utf-8");
            String name = URLEncoder.encode(keyWord, "utf-8");

            //提取行政处罚列表清单
            String resultCFUrl  ="";
            if("".equals(name)||name == null){
                resultCFUrl =  "http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/cflist/newcfgrid.action?search=false&nd=&rows=10&page="+sizePage+"&sidx=&sord=asc";
            }else{
                resultCFUrl = "http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/cflist/newSgsCFSearchgrid.action?search=false&nd=&rows=10&page="+sizePage+"&sidx=&sord=asc&xzxdr="+name;
            }


            log.info("--------------------------resultCFUrl-----------------\n"+resultCFUrl);
            WebRequest request = new WebRequest(new URL(resultCFUrl), HttpMethod.GET);
            Map<String, String> additionalHeaders = new HashMap<String, String>();
            additionalHeaders.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
            additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8");
            additionalHeaders.put("Accept", "application/json;charset=UTF-8, text/javascript, */*; q=0.01");
            request.setAdditionalHeaders(additionalHeaders);
            // 获取某网站页面
            Page page = webClient.getPage(request);
            // System.out.println(Page.getWebResponse().getContentAsString());
            ObjectMapper mapper = new ObjectMapper();
            WebResponse response = page.getWebResponse();

            if (response.getContentType().equals("application/json")) {
                String jsonString = response.getContentAsString();
                //log.info("----------------sizePage:"+sizePage+"\n"+jsonString);
                Map mapJson = mapper.readValue(jsonString, Map.class);
                List<Map> listResults = (List)mapJson.get("gridModel");
                for(int i =0;i<listResults.size();i++){
                    Map mapResult = listResults.get(i);
//                    log.info("--------------------------mapResult-----------------\n"+mapResult);
                    String nameResult = (String)mapResult.get("cfid");
//                    log.info("--------------------------nameResult-----------------\n"+nameResult);
                    cfidList.add(nameResult);
                }
            }else{
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return cfidList;
    }
    /**
     * 获取处罚的详情形象
     * @param cfid  要查询的名称
     * @param webClient HTMLUnit客户端
     * @return
     */
    public  Map pubPenaltyDetail(String cfid, WebClient webClient) {
        Map<String,String> detailMap = new HashMap<>();
        try {
            //浏览器的中文需要转码  如：URLEncoder.encode("绩溪县汇通汽车运输有限公司", "utf-8");
            //String name = URLEncoder.encode(researchName, "utf-8");
            //提取行政许可列表详情
            String detailCFResult = "http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/sgsinfo/getcfinfo.action?cfid="+cfid;
            WebRequest request = new WebRequest(new URL(detailCFResult), HttpMethod.GET);
            Map<String, String> additionalHeaders = new HashMap<String, String>();
            additionalHeaders.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
            additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8");
            additionalHeaders.put("Accept", "application/json;charset=UTF-8, text/javascript, */*; q=0.01");
            request.setAdditionalHeaders(additionalHeaders);
            // 获取某网站页面
            Page page = webClient.getPage(request);
            ObjectMapper mapper = new ObjectMapper();
            WebResponse response = page.getWebResponse();
            String jsonString = new String(response.getContentAsString().getBytes("ISO-8859-1"),"UTF-8");
            if (!response.getContentType().equals("application/json")) {
                List<Map> listJson = mapper.readValue(jsonString, List.class);
                for(Object strJson :listJson ){
                    detailMap = (Map)strJson;
                    String sourceUrl = "http://cxw.shcredit.gov.cn:8081/sh_xyxxzc/cflist/newSgsCFSearchgrid.action?search=false&nd=&rows=10&page=1&sidx=&sord=asc&xzxdr="+detailMap.get("xzxdr");
                    detailMap.put("sourceUrl",sourceUrl);

                }
                /*
                处罚文书号 2011706255

                处罚名称 韩清亮占道设摊一案
                处罚类别 罚款
                处罚事由 占道设摊
                处罚依据 《上海市市容环境卫生管理条例》第二十五条第二款
                行政相对人 韩清亮
                处罚决定日期 2017/12/20
                处罚机关 上海市静安区城市管理行政执法局
                */
            }else{
                return null;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return detailMap;
    }
    public void result(int pageSize,String keyWord)throws Throwable{

        List<String> cfidList = new ArrayList<>();
        Map punishDetail = new HashMap();
        //1.获取处罚清单
        if(("".equals(keyWord)||keyWord==null)&&pageSize<=0){
            for(int i=1;i<=pageSize;i++){
                cfidList.addAll(publicityInfoSearch(keyWord,i,createWebClient("",""))) ;
            }
            log.info("关键字为空或者为null并且页数小于等于零的情况···");
        }
        if(("".equals(keyWord)||keyWord==null)&& pageSize>0){
            for(int i=1;i<=pageSize;i++){
                cfidList.addAll(publicityInfoSearch(keyWord,i,createWebClient("",""))) ;
            }

            log.info("关键字为空或者为null并且页数大于零的情况···");
        }
        if(!"".equals(keyWord)&&keyWord!=null){
            cfidList =  publicityInfoSearch(keyWord,1,createWebClient("",""));
            log.info("关键字不为空并且不为null的情况···");
        }

        //2.获取处罚的详情//入库
        //log.info("-----------------------\n"+nameList);
        for(int i=0;i<cfidList.size();i++){
            punishDetail = pubPenaltyDetail(cfidList.get(i),createWebClient("",""));
            adminPunishInsert(punishDetail);
        }
    }
    public AdminPunish adminPunishInsert(Map<String,String> map){
        AdminPunish adminPunish = new AdminPunish();
        //created_at	本条记录创建时间
        adminPunish.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        adminPunish.setUpdatedAt(new Date());
        //source	数据来源
        adminPunish.setSource("信用中国（上海）");
        //subject	主题
        adminPunish.setSubject("行政处罚");
        //url	url
        adminPunish.setUrl(map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        adminPunish.setObjectType(map.get("xzxdr").toString().trim().length()<6?"02":"01");
        //enterprise_name	企业名称
        adminPunish.setEnterpriseName(map.get("xzxdr").trim().length()<6?"":map.get("xzxdr"));
        //enterprise_code1	统一社会信用代码--cfXdrShxym
        adminPunish.setEnterpriseCode1("");
        //enterprise_code2	营业执照注册号
        adminPunish.setEnterpriseCode2("");
        //enterprise_code3	组织机构代码
        adminPunish.setEnterpriseCode3("");
        //person_name	法定代表人/负责人姓名|负责人姓名
        adminPunish.setPersonName(map.get("xzxdr").toString().trim().length()<6?map.get("xzxdr"):"");
        //person_id	法定代表人身份证号|负责人身份证号
        adminPunish.setPersonId(map.get(""));
        //punish_type	处罚类型
        adminPunish.setPunishType(map.get("cflb"));
        //punish_reason	处罚事由
        adminPunish.setPunishReason(map.get("cfsy"));
        //punish_according	处罚依据
        adminPunish.setPunishAccording(map.get("cfyj"));
        //punish_result	处罚结果
        adminPunish.setPunishResult("");
        //judge_no	执行文号
        adminPunish.setJudgeNo(map.get("cfwsh"));
        //judge_date	执行时间
        adminPunish.setJudgeDate(map.get("cfjdrq"));
        //judge_auth	判决机关
        adminPunish.setJudgeAuth(map.get("cfjguan"));
        //publish_date	发布日期
        adminPunish.setPublishDate(map.get("cfjdrq"));

        saveAdminPunishOne(adminPunish,false);
        return adminPunish;
    }

}
