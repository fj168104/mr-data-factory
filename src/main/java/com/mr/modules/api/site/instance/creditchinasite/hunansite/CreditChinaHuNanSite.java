package com.mr.modules.api.site.instance.creditchinasite.hunansite;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Proxypool> proxypoolList = proxypoolMapper.selectProxyPool();
        if(proxypoolList.size()>0){
             ip = proxypoolList.get(0).getIpaddress();
             port = proxypoolList.get(0).getIpport();
        }

        boolean conntionNetFlag = true;
        while (conntionNetFlag){

            try {
                webClient = createWebClient(ip,port);
                HtmlPage htmlPage = webClient.getPage("http://www.credithunan.gov.cn/info/dishonestyInfoList.do");
                List<HtmlAnchor>  htmlAnchors = htmlPage.getByXPath("//body//table[@class='round3']//tbody//tr//td[@align='right']//table//tbody//tr//td//a");
                for(HtmlAnchor htmlAnchor:htmlAnchors){
                    boolean insertFlag = true;
                    String textStr = htmlAnchor.asText();
                    //主题
                    String subjectName ="";
                    //结果地址
                    String esultUrl ="http://www.credithunan.gov.cn"+htmlAnchor.getAttribute("href");
                    //省水利厅黑榜 2
                    if(textStr.contains("省水利厅黑榜")){
                        subjectName ="省水利厅黑榜";
                    }
                    //省内产品质量监督抽查不合格记录信息 382
                    else if(textStr.contains("产品质量监督抽查不合格记录信息")){
                        subjectName ="省内产品质量监督抽查不合格记录信息";
                    }
                    //省国税重大税收违法案件信息 0
                    else if(textStr.contains("重大税收违法案件信息")){
                        subjectName ="省国税重大税收违法案件信息";
                    }
                    //省地税重大税收违法案件公告信息 2
                    else if(textStr.contains("重大税收违法案件公告信息")){
                        subjectName ="省地税重大税收违法案件公告信息";
                    }
                    //省住建部黑榜 13
                    else if(textStr.contains("省住建厅黑榜")){
                        subjectName ="省住建部黑榜";
                    }
                    //省文化厅主体黑名单查询 1
                    else if(textStr.contains("主体黑名单查询信息")){
                        subjectName ="省文化厅主体黑名单查询";
                    }
                    //省安全生产黑名单 3
                    else if(textStr.contains("安全生产不良记录“黑名单”")){
                        subjectName ="省安全生产黑名单";
                    }
                    //省企业环境信用评价不良 350
                    else if(htmlAnchor.asText().contains("年度企业环境信用评价风险")){
                        subjectName ="省企业环境信用评价不良";
                    }
                    //省食品药品抽检不合格 283
                    else if(textStr.contains("药品抽检不合格")||textStr.contains("食品抽检不合格")){
                        subjectName ="省食品药品抽检不合格";
                    }else{
                        insertFlag = false;
                    }

                    //设置超时最多重试次数3次
                    int count =1;
                    while (count<4 && insertFlag){
                        try {

                            htmlPage =  htmlAnchor.click();

                            webClient.waitForBackgroundJavaScript(5000);
                            BlackList(webClient,htmlPage,esultUrl,subjectName);
                            count=4;
                        }catch (SocketTimeoutException e){
                            count++;
                            log.error("网络连接超时···"+e.getMessage());
                            log.error("网络连接超时···从试中···第"+(count-1)+"从试");
                        }
                    }
                }
                conntionNetFlag = false;
            }catch (IOException e){
                conntionNetFlag = true;
                if(proxypoolList.size()>0){
                    proxypoolList.remove(0);
                }
                log.error("网络连接异常，请检查···"+e.getMessage());
                ip = proxypoolList.get(0).getIpaddress();
                port =proxypoolList.get(0).getIpport();
                log.info("ip地址："+ip+"------------port端口："+port);
            }catch (Throwable e){
                conntionNetFlag = false;
                log.error("Throwable异常，请检查···"+e.getMessage());
            }finally {
                webClient.close();
            }

        }

    }

    /**
     * 获取列表清单
     * @param htmlPage
     * @return
     */
    public HtmlPage BlackList(WebClient webClient,HtmlPage htmlPage,String urlResult,String subject){
        //翻页标识
        boolean nextPageFlag = true;
        //获取列表信息
        List<HtmlElement> htmlElementTrs = htmlPage.getByXPath("//body//table[@class='round3']//tbody//tr//td//table[@class='listf3']//tbody//tr//td//div[@id='promptspage']//div[@class='default_pgContainer']//table//tbody//tr");
        for(int i=1;i<htmlElementTrs.size();i++){
            Map map = new HashMap();
            List<HtmlElement> htmlElementTds = htmlElementTrs.get(i).getElementsByTagName("td");
            if(htmlElementTds.size()==2){
                map.put("source","信用中国（湖南）");
                log.info(" 0企业名称:"+htmlElementTds.get(0).asText()+"------0数据最后更新时间:"+htmlElementTds.get(1).asText());
                map.put("subject",subject);
                map.put("sourceUrl",urlResult);
                if(htmlElementTds.get(0).asText().trim().length()<6) {
                    map.put("objectType", "02");
                    map.put("enterpriseName", "");
                    map.put("personName", htmlElementTds.get(0).asText());
                }else{
                    map.put("objectType", "01");
                    map.put("enterpriseName", htmlElementTds.get(0).asText());
                    map.put("personName","");
                }
                map.put("publishDate",htmlElementTds.get(1).asText());

            }
            if(map.size()>0){
                discreditBlacklistInsert(map);
            }

        }
        DomElement htmlElementDivPage = htmlPage.getElementById("promptspage");
        List<DomElement> htmlElements = htmlElementDivPage.getByXPath("//div//table//tbody//tr//td//table//tbody//tr//td//div[@class='default_pgBtn default_pgNext']");
        //获取下一页事件，TODO 如果：class=default_pgBtn default_pgNext 标识存在下一页，需要执行下一页操作
        log.info("-----------第 1 页------------\n");
        if(htmlElements.size()>0){
            nextPageFlag = true;
            log.info("-----------存在下一页------------\n");
        }else {
            nextPageFlag = false;
            log.info("-----------不存在下一页------------\n");
        }
        //递归翻页
        while(nextPageFlag){
            int pageSize = 1;
            if(htmlElements.size()>0){
                try {
                    DomElement htmlElementDiv = htmlElements.get(0);
                    htmlPage = htmlElementDiv.click();
                    webClient.waitForBackgroundJavaScript(5000);
                    List<HtmlElement>  htmlElementTrs1 = htmlPage.getByXPath("//body//table[@class='round3']//tbody//tr//td//table[@class='listf3']//tbody//tr//td//div[@id='promptspage']//div[@class='default_pgContainer']//table//tbody//tr");

                    for(int j=1;j<htmlElementTrs1.size();j++){
                        Map map = new HashMap();
                        List<HtmlElement> htmlElementTds = htmlElementTrs1.get(j).getElementsByTagName("td");
                        if(htmlElementTds.size()==3){
                            map.put("source","信用中国（湖南）");
                            log.info("1企业名称:"+htmlElementTds.get(0).asText()+"------1数据最后更新时间:"+htmlElementTds.get(1).asText());
                            map.put("subject",subject);
                            map.put("sourceUrl",urlResult);
                            if(htmlElementTds.get(0).asText().trim().length()<6) {
                                map.put("objectType", "02");
                                map.put("enterpriseName", "");
                                map.put("personName", htmlElementTds.get(0).asText());
                            }else{
                                map.put("objectType", "01");
                                map.put("enterpriseName", htmlElementTds.get(0).asText());
                                map.put("personName","");
                            }
                            map.put("publishDate",htmlElementTds.get(1).asText());
                        }
                        if(map.size()>0){
                            discreditBlacklistInsert(map);
                        }

                    }
                    htmlElementDivPage = htmlPage.getElementById("promptspage");
                    htmlElements = htmlElementDivPage.getByXPath("//div//table//tbody//tr//td//table//tbody//tr//td//div[@class='default_pgBtn default_pgNext']");
                    //获取下一页事件，TODO 如果：class=default_pgBtn default_pgNext 标识存在下一页，需要执行下一页操作
                    if(htmlElements.size()>0){
                        nextPageFlag = true;
                        log.info("-----------第"+ ++pageSize +"页------------\n");
                    }else {
                        nextPageFlag = false;
                        log.info("-----------最后一页------------\n");
                    }
                }catch (IOException e){
                    nextPageFlag = false;
                    log.error("网络连接异常，请检查···"+e.getMessage());
                }

            }else {
                nextPageFlag = false;
            }
        }

        return htmlPage;
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
        discreditBlacklist.setEnterpriseName(map.get("commpanyName"));
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
        discreditBlacklist.setJudgeAuth("");
        //publish_date	发布日期
        discreditBlacklist.setPublishDate(map.get("publishDate"));
        //status	当前状态
        discreditBlacklist.setStatus("");
        saveDisneycreditBlackListOne(discreditBlacklist,false);
        return discreditBlacklist;
    }
}
