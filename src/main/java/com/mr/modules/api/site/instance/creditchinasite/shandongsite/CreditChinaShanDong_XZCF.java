package com.mr.modules.api.site.instance.creditchinasite.shandongsite;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.model.Proxypool;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @Auther zjxu to 201806
 * 提取主题：行政处罚
 * 提取出现：行政相对人名称 处罚机关 案件名称 数据接收时间(没有这个属性)
 * 提取地址：http://www.creditsd.gov.cn/creditsearch.publicity.dhtml
 * 注：网页浏览有限制，最多浏览100页
 **/
@Slf4j
@Component("creditchinashandong_xzcf")
@Scope("prototype")
public class CreditChinaShanDong_XZCF extends SiteTaskExtend_CreditChina{

    @Autowired
    SiteParams siteParams;

    @Override
    protected String execute() throws Throwable {
        String keyWord = siteParams.map.get("keyWord");
        if("".equals(keyWord)||keyWord==null){
            keyWord ="";
        }
        webContext(keyWord,"","");
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    /**
     * 提取行政处罚清单
     * @param keyWord
     * @param ip
     * @param port
     */
    public void webContext(String keyWord, String ip, String port)throws Throwable{
        List<Proxypool> listIps = getProxyPool();
        //页码
        int page = 1;
        //新增许可：http://www.creditsd.gov.cn/creditsearch.permissionList.phtml?id=&keyword=%E7%A6%B9%E5%9F%8E%E5%8F%8B%E8%86%B3%E5%9D%8A%E9%A3%9F%E5%93%81%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8
        //行政处罚
        try {
            keyWord = URLEncoder.encode(keyWord, "utf-8");
        } catch (UnsupportedEncodingException ue){
            log.error("不支持代码转换···异常"+ue.getMessage());
        }
        String urlResult = "http://www.creditsd.gov.cn/creditsearch.punishmentList.phtml?id=&keyword="+keyWord+"&page="+page;
        WebClient webClient = createWebClient(ip,port);
        //网络拒绝连接，调用IP池
        Boolean connectFlag = true;
        while (connectFlag){
            try{
                HtmlPage htmlPage = webClient.getPage(urlResult);
                //获取输入框
                /*HtmlInput htmlInput = (HtmlInput)htmlPage.getElementById("keyword");
                htmlInput.setValueAttribute(keyWord);
                //获取按钮事件
                HtmlButton htmlButton = (HtmlButton)htmlPage.getElementById("searchBtn");
                HtmlPage htmlPage1 = htmlButton.click();*/
                //获取页数 pagination
                List<HtmlElement> htmlLi = htmlPage.getByXPath("//body//div//div[@class='mainbody_page']//ul[@class='pagination']//li");
                if(1<htmlLi.size()){
                    page = Integer.valueOf(htmlLi.get(htmlLi.size()-2).asText());
                }
                //获取只有一页的情况
                if(page == 1){
                    //获取列表清单数据
                    List<HtmlElement> htmlTr = htmlPage.getByXPath("//body//div//table[@class='table table-hover table-condensed tab-content']//tbody//tr");

                    for(int i=1;i<htmlTr.size();i++){
                        Map map = new HashMap();
                        List<HtmlElement> htmlTd = htmlTr.get(i).getElementsByTagName("td");
                        HtmlAnchor htmlAnchor = (HtmlAnchor) htmlTd.get(0).getElementsByTagName("a").get(0);
                        htmlPage = htmlAnchor.click();
                        HtmlTableBody htmlTableBody = (HtmlTableBody)htmlPage.getByXPath("//body//div//main//div//div//div//div//div//div//table[@class='table table-hover']//tbody").get(0);
                        log.info("-----------tbody---------"+htmlTableBody.asXml());
                        //提取table中的行与基本属性
                        List<HtmlTableRow> htmlTableRows = htmlTableBody.getRows();
                        for(int h =0;h<htmlTableRows.size();h++){
                            HtmlTableRow htmlTableRow = htmlTableRows.get(h);
                            //log.info("属性："+htmlTableRow.getCell(0).asText()+"------属性值："+htmlTableRow.getCell(1).asText());
                            if(htmlTableRow.getCell(0).asText().equals("行政相对人名称")){
                                map.put("sourceUrl","http://www.creditsd.gov.cn/creditsearch.punishmentList.phtml?id=&keyword="+htmlTableRow.getCell(1).asText());
                                map.put("punishObject",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("处罚机关")){
                                map.put("judgeAuth",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("案件名称")){
                                map.put("judgeName",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("行政处罚决定文书号")){
                                map.put("judgeNo",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("处罚类别")){
                                map.put("punishType",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("处罚事由")){
                                map.put("punishReason",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("处罚依据")){
                                map.put("punishAccording",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("统一社会信用代码")){
                                map.put("enterpriseCode1",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("组织机构代码")){
                                map.put("enterpriseCode3",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("工商登记码")){
                                map.put("enterpriseCode2",htmlTableRow.getCell(1).asText());
                            }if(htmlTableRow.getCell(0).asText().equals("税务登记号")){
                                map.put("taxNo",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("居民身份证号")){
                                map.put("personId",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("法定代表人姓名")){
                                map.put("personName",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("处罚结果")){
                                map.put("punishResult",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("处罚生效期")){
                                map.put("judgeDate",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("数据更新时间")){
                                map.put("updateDate",htmlTableRow.getCell(1).asText());
                            }
                            if(htmlTableRow.getCell(0).asText().equals("备注")){
                                map.put("note",htmlTableRow.getCell(1).asText());
                            }

                        }
                        adminPunishInsert(map);
                    }
                    log.info("只有一页信息需要处理···\n"+htmlPage.asXml());
                }
                if(page>1){
                    for(int j=1;j<page;j++){
                        urlResult ="http://www.creditsd.gov.cn/creditsearch.punishmentList.phtml?id=&keyword="+keyWord+"&page="+j;
                        htmlPage = webClient.getPage(urlResult);
                        List<HtmlElement> htmlTr = htmlPage.getByXPath("//body//div//table[@class='table table-hover table-condensed tab-content']//tbody//tr");

                        for(int i=1;i<htmlTr.size();i++){
                            Map map = new HashMap();

                            List<HtmlElement> htmlTd = htmlTr.get(i).getElementsByTagName("td");
                            HtmlAnchor htmlAnchor = (HtmlAnchor) htmlTd.get(0).getElementsByTagName("a").get(0);
                            htmlPage = htmlAnchor.click();
                            //提取table中的行与基本属性
                            HtmlTableBody htmlTableBody = (HtmlTableBody)htmlPage.getByXPath("//body//div//main//div//div//div//div//div//div//table[@class='table table-hover']//tbody").get(0);
                            //提取table中的行与基本属性
                            List<HtmlTableRow> htmlTableRows = htmlTableBody.getRows();
                            for(int h =0;h<htmlTableRows.size();h++){
                                HtmlTableRow htmlTableRow = htmlTableRows.get(h);
                                log.info("属性："+htmlTableRow.getCell(0).asText()+"------属性值："+htmlTableRow.getCell(1).asText());
                                if(htmlTableRow.getCell(0).asText().equals("行政相对人名称")){

                                    map.put("sourceUrl","http://www.creditsd.gov.cn/creditsearch.punishmentList.phtml?id=&keyword="+htmlTableRow.getCell(1).asText());
                                    map.put("punishObject",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("处罚机关")){
                                    map.put("judgeAuth",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("案件名称")){
                                    map.put("judgeName",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("行政处罚决定文书号")){
                                    map.put("judgeNo",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("处罚类别")){
                                    map.put("punishType",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("处罚事由")){
                                    map.put("punishReason",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("处罚依据")){
                                    map.put("punishAccording",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("统一社会信用代码")){
                                    map.put("enterpriseCode1",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("组织机构代码")){
                                    map.put("enterpriseCode3",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("工商登记码")){
                                    map.put("enterpriseCode2",htmlTableRow.getCell(1).asText());
                                }if(htmlTableRow.getCell(0).asText().equals("税务登记号")){
                                    map.put("taxNo",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("居民身份证号")){
                                    map.put("personId",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("法定代表人姓名")){
                                    map.put("personName",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("处罚结果")){
                                    map.put("punishResult",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("处罚生效期")){
                                    map.put("judgeDate",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("数据更新时间")){
                                    map.put("updateDate",htmlTableRow.getCell(1).asText());
                                }
                                if(htmlTableRow.getCell(0).asText().equals("备注")){
                                    map.put("note",htmlTableRow.getCell(1).asText());
                                }

                            }
                            adminPunishInsert(map);
                        }

                        log.info("有多页信息需要处理···\n"+htmlPage.asXml());
                    }
                }


                connectFlag = false;
            }catch (IOException e){
                log.error("IO异常···异常信息为："+e.getMessage());
                if(listIps.size()<=0){
                    break;
                }
                listIps.remove(0);
                ip = listIps.get(0).getIpaddress();
                port = listIps.get(0).getIpport();
                connectFlag = true;
                log.info("服务决绝连接，切换代理，等待中···"+"ip地址："+ip+" port端口："+port);

            }
        }


        webClient.close();
    }

    public AdminPunish adminPunishInsert(Map<String,String> map){
        AdminPunish adminPunish = new AdminPunish();
        //created_at	本条记录创建时间
        adminPunish.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        adminPunish.setUpdatedAt(new Date());
        //source	数据来源
        adminPunish.setSource("信用中国（山东）");
        //subject	主题
        adminPunish.setSubject("行政处罚");
        //url	url
        adminPunish.setUrl(map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        adminPunish.setObjectType(map.get("punishObject").toString().trim().length()<6?"02":"01");
        //enterprise_name	企业名称
        adminPunish.setEnterpriseName(map.get("punishObject").trim().length()<6?"":map.get("punishObject"));
        //enterprise_code1	统一社会信用代码--cfXdrShxym
        adminPunish.setEnterpriseCode1(map.get("enterpriseCode1"));
        //enterprise_code2	营业执照注册号
        adminPunish.setEnterpriseCode2(map.get("enterpriseCode2"));
        //enterprise_code3	组织机构代码
        adminPunish.setEnterpriseCode3(map.get("enterpriseCode3"));
        //person_name	法定代表人/负责人姓名|负责人姓名
        adminPunish.setPersonName(map.get("personName"));
        //person_id	法定代表人身份证号|负责人身份证号
        adminPunish.setPersonId(map.get("personId"));
        //punish_type	处罚类型
        adminPunish.setPunishType(map.get("punishType"));
        //punish_reason	处罚事由
        adminPunish.setPunishReason(map.get("punishReason"));
        //punish_according	处罚依据
        adminPunish.setPunishAccording(map.get("punishAccording"));
        //punish_result	处罚结果
        adminPunish.setPunishResult(map.get("punishResult"));
        //judge_no	执行文号
        adminPunish.setJudgeNo(map.get("judgeNo"));
        //judge_date	执行时间
        adminPunish.setJudgeDate(map.get("judgeDate"));
        //judge_auth	判决机关
        adminPunish.setJudgeAuth(map.get("judgeAuth"));
        //publish_date	发布日期
        adminPunish.setPublishDate(map.get("updateDate"));

        saveAdminPunishOne(adminPunish,false);
        return adminPunish;
    }
}
