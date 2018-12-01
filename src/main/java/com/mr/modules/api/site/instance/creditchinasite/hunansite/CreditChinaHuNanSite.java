package com.mr.modules.api.site.instance.creditchinasite.hunansite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.mr.modules.api.mapper.ProxypoolMapper;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.model.Proxypool;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;

/**
 * @Auther zjxu
 * @DateTime 201806
 * 来源：信用中国（湖南）
 * 主题：
 * 省水利厅黑磅
 * 省内产品质量监督抽查不合格记录信息
 * 省国税重大税收违法案件信息
 * 省地税重大税收违法案件公告信息
 * 省住建部黑榜
 * 省文化厅主体黑名单查询
 * 省安全生产黑名单
 * 省企业环境信用评价不良
 * 省食品药品抽检不合格
 * 属性：企业名称  发布时间
 * url:http://www.credithunan.gov.cn/info/dishonestyInfoList.do
 */
@Slf4j
@Component("creditchinahunansite")
@Scope("prototype")
public class CreditChinaHuNanSite extends SiteTaskExtend_CreditChina{
    @Override
    protected String execute() throws Throwable {
        webContext();
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    @Autowired
    ProxypoolMapper proxypoolMapper;

    public void webContext(){
        WebClient webClient =null;
        String ip ="", port="";
        /*List<Proxypool> proxypoolList = proxypoolMapper.selectProxyPool();
        if(proxypoolList.size()>0){
             ip = proxypoolList.get(0).getIpaddress();
             port = proxypoolList.get(0).getIpport();
        }*/

        boolean conntionNetFlag = true;
        while (conntionNetFlag){

            try {
                webClient = createWebClient(ip,port);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
                webClient.getOptions().setJavaScriptEnabled(false);
                HtmlPage htmlPage = webClient.getPage("http://www.credithunan.gov.cn/info/dishonestyInfoList.do");
                //获取连接
                List<HtmlAnchor>  htmlAnchors = htmlPage.getByXPath("//body//table[@class='round3']//tbody//tr//td[@align='right']//table//tbody//tr//td//a");
                //获取总数量
                List<HtmlElement>  htmlCountStrs = htmlPage.getByXPath("//body//table[@class='round3']//tbody//tr//td[@align='right']//table//tbody//tr//td//nobr//span");
                //获取数据来源
                List<HtmlElement>  htmlSources = htmlPage.getByXPath("//body//table[@class='round3']//tbody//tr//td[@align='right']//table//tbody//tr//td[@width='25%']");
                for(int i=0;i<htmlAnchors.size();i++){
                    HtmlAnchor htmlAnchor = htmlAnchors.get(i);
                    HtmlElement htmlCountStr = htmlCountStrs.get(i);
                    String textStr = htmlAnchor.asText();
                    //主题
                    String subjectName ="";
                    //结果地址
                    String esultUrl ="http://www.credithunan.gov.cn"+htmlAnchor.getAttribute("href");
                    //编号
                    String id  = htmlAnchor.getAttribute("href").split("\\?")[1];
                    //记录数据
                    String countStr =htmlCountStr.asText().replace(",","");
                    //处罚机构
                    String judgeAuth = htmlSources.get(i).asText().replace("数据来源：","");
                    //String judgeAuth ="";
                    subjectName = textStr.replaceAll("[0-9]+","").replace(".","").replace(" ","");

                    try {
                        BlackList(webClient,judgeAuth,esultUrl,subjectName,id,countStr);
                    }catch (SocketTimeoutException e){
                        log.warn("网络连接超时···"+e.getMessage());
                    }

                }
                conntionNetFlag = false;
            }catch (IOException e){
                log.warn("网络连接异常，请检查···"+e.getMessage());
            }catch (Throwable e){
                conntionNetFlag = false;
                log.warn("Throwable异常，请检查···"+e.getMessage());
            }finally {
                webClient.close();
            }

        }

    }

    /**
     * 获取列表清单
     * @param judgeAuth
     * @return
     */
    public void BlackList(WebClient webClient,String judgeAuth,String urlResult,String subject,String id,String countStr) throws Exception{
        //http://www.credithunan.gov.cn/page/info/promptsProxy.jsp?startrecord=1&endrecord=5268&perpage=5268&totalRecord=5268&id=9FEBBC60E2B677A9856C6D384C6E39C74C4C2B28B963A80D
        if(!countStr.equals("0")){
            String urlMain = "http://www.credithunan.gov.cn/page/info/promptsProxy.jsp?startrecord=1&endrecord="+countStr+"&perpage="+countStr+"&totalRecord="+countStr+"&"+id;
            WebRequest request = new WebRequest(new URL(urlMain), HttpMethod.GET);
            Map<String, String> additionalHeaders = new HashMap<String, String>();
            additionalHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
            additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
            additionalHeaders.put("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
            request.setAdditionalHeaders(additionalHeaders);
            // 获取某网站页面
            Page page = webClient.getPage(request);
            // System.out.println(Page.getWebResponse().getContentAsString());
            ObjectMapper mapper = new ObjectMapper();
            WebResponse response = page.getWebResponse();
            String resultStr = response.getContentAsString().split("dataStore =")[1].replace("[","").replace("]","");
            String[] list = resultStr.split(",");
            for (String str : list){
                Map<String, String> map = new HashMap<>();
                String [] finalResult = str.replace("\"","").split("\\$");
                if(finalResult.length==4){
                    map.put("source","信用中国（湖南）");
                    String name = finalResult[2];
                    String time = finalResult[3];
                    map.put("subject",subject);
                    map.put("sourceUrl",urlResult);
                    if(name.length()<4) {
                        map.put("objectType", "02");
                        map.put("enterpriseName", "");
                        map.put("personName", name);
                    }else{
                        map.put("objectType", "01");
                        map.put("enterpriseName", name);
                        map.put("personName","");
                    }
                    map.put("publishDate",time);
                    map.put("judgeAuth",judgeAuth);
                    discreditBlacklistInsert(map);
                } else if(finalResult.length==5) {
                    map.put("source","信用中国（湖南）");
                    String name = finalResult[2];
                    String time = finalResult[4];
                    map.put("subject",subject);
                    map.put("sourceUrl",urlResult);
                    if(name.length()<4) {
                        map.put("objectType", "02");
                        map.put("enterpriseName", "");
                        map.put("personName", name);
                    }else{
                        map.put("objectType", "01");
                        map.put("enterpriseName", name);
                        map.put("personName","");
                    }
                    map.put("judgeAuth",judgeAuth);
                    map.put("publishDate",time);
                    discreditBlacklistInsert(map);
                }
            }
        }

    }
    public DiscreditBlacklist discreditBlacklistInsert(Map<String,String> map){
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();
        //created_at	本条记录创建时间
        discreditBlacklist.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        discreditBlacklist.setUpdatedAt(new Date());
        //source	数据来源
        discreditBlacklist.setSource("信用中国（湖南）");
        //subject	主题
        discreditBlacklist.setSubject(map.get("subject"));
        //url	url
        discreditBlacklist.setUrl(map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        discreditBlacklist.setObjectType(map.get("objectType"));
        //enterprise_name	企业名称
        discreditBlacklist.setEnterpriseName(map.get("enterpriseName"));
        //enterprise_code1	统一社会信用代码
        discreditBlacklist.setEnterpriseCode1("");
        //enterprise_code2	营业执照注册号
        discreditBlacklist.setEnterpriseCode2("");
        //enterprise_code3	组织机构代码
        discreditBlacklist.setEnterpriseCode3("");
        //person_name	法定代表人/负责人姓名|负责人姓名
        discreditBlacklist.setPersonName(map.get("personName"));
        //person_id	法定代表人身份证号|负责人身份证号
        discreditBlacklist.setPersonId("");
        //discredit_type	失信类型
        discreditBlacklist.setDiscreditType("");
        //discredit_action	失信行为
        discreditBlacklist.setDiscreditAction("");
        //punish_reason	列入原因
        discreditBlacklist.setPunishReason("");
        //punish_result	处罚结果
        discreditBlacklist.setPunishReason("");
        //judge_no	执行文号
        discreditBlacklist.setJudgeNo("");
        //judge_date	执行时间
        discreditBlacklist.setJudgeDate("");
        //judge_auth	判决机关
        discreditBlacklist.setJudgeAuth(map.get("judgeAuth"));
        //publish_date	发布日期
        discreditBlacklist.setPublishDate(map.get("publishDate"));
        //status	当前状态
        discreditBlacklist.setStatus("");
        saveDisneycreditBlackListOne(discreditBlacklist,false);
        return discreditBlacklist;
    }
}
