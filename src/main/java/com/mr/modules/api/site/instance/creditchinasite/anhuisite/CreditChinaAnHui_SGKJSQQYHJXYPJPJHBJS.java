package com.mr.modules.api.site.instance.creditchinasite.anhuisite;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther zjxu to 201806
 * 信用中国（安徽）
 * 提取主题：省国控及涉铅企业环境信用评价评价
 * 提取属性：企业名称 统一社会信用代码 统一社会信用代码 企业名称 组织机构代码
 */
@Slf4j
@Component("creditchinaanhui_sgkjsqqyhjxypjpjhbjs")
@Scope("prototype")
public class CreditChinaAnHui_SGKJSQQYHJXYPJPJHBJS extends SiteTaskExtend_CreditChina{
    @Override
    protected String execute() throws Throwable {
        WebContext();
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    String url = "http://www.creditah.gov.cn/remote/1525/";

    public void WebContext(){
        String ip="",  port="";
        WebClient webClient = createWebClient(ip,port);
        try {
            HtmlPage htmlPage = webClient.getPage(url);
            Document documentInit = Jsoup.parse(htmlPage.asXml());
            Element allPage  = documentInit.getElementsByClass("allPage").get(0);
            int pageSize = Integer.valueOf(allPage.ownText());
            if(pageSize>=1){
                for(int i = 1;i<pageSize;i++){
                    String urlResult = url+"index"+i+".htm";
                    HtmlPage htmlPage1 = webClient.getPage(urlResult);
                    Document document = Jsoup.parse(htmlPage1.asXml());
                    Element element = document.getElementsByClass("bordered").get(0);
                    Element elementTbody = element.getElementsByTag("tbody").get(0);
                    Elements elementsTr = elementTbody.getElementsByTag("tr");
                    for(Element elementTR:elementsTr){
                        Elements elementsTd = elementTR.getElementsByTag("td");
                        if(elementsTd.size()==3){
                            Map map = new HashMap<>();
                            map.put("nnifiedSocialCreditCode",elementsTd.get(0).text());
                            if(elementsTd.get(1).text().trim().length()<6){
                                map.put("objectType","02");
                                map.put("personName",elementsTd.get(1).text());
                                map.put("commpanyName","");
                            }else{
                                map.put("objectType","01");
                                map.put("personName","");
                                map.put("commpanyName",elementsTd.get(1).text());
                            }
                            map.put("setEnterpriseCode3",elementsTd.get(2).text());
                            map.put("sourceUrl",urlResult);
                            discreditBlacklistInsert(map);
                        }
                    }
                }
            }
        }catch (IOException e){
            log.error("访问网络有问题，请检查···异常信息如下"+e.getMessage());
        }
    }
    public DiscreditBlacklist discreditBlacklistInsert(Map<String,String> map){
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();
        //created_at	本条记录创建时间
        discreditBlacklist.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        discreditBlacklist.setUpdatedAt(new Date());
        //source	数据来源
        discreditBlacklist.setSource("信用中国（安徽）");
        //subject	主题
        discreditBlacklist.setSubject("省国控及涉铅企业环境信用评价评价");
        //url	url
        discreditBlacklist.setUrl(map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        discreditBlacklist.setObjectType(map.get("objectType"));
        //enterprise_name	企业名称
        discreditBlacklist.setEnterpriseName(map.get("commpanyName"));
        //enterprise_code1	统一社会信用代码
        discreditBlacklist.setEnterpriseCode1(map.get("nnifiedSocialCreditCode"));
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
        discreditBlacklist.setPublishDate("");
        //status	当前状态
        discreditBlacklist.setStatus("");
        saveDisneycreditBlackListOne(discreditBlacklist,false);
        return discreditBlacklist;
    }
}
