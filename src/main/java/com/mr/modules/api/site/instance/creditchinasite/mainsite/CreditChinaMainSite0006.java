package com.mr.modules.api.site.instance.creditchinasite.mainsite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther
 * 1.信用中国主站
 * 2.url:http://www.creditchina.gov.cn/xinxigongshi/xinxishuanggongshi/
 * 3.需求：选择“行政处罚”+“全国”
 * 4.提取内容：处罚主体、决定书文号、处罚名称、法定代表人、处罚类别、处罚结果、处罚事由、处罚机关、处罚决定日期、处罚期限、数据更新日期
 */
@Slf4j
@Component("creditChinaMainSite0006")
@Scope("prototype")
public class CreditChinaMainSite0006 /*extends SiteTaskExtend_CreditChina*/{
    //行政处罚与许可证清单
    String url ="http://www.creditchina.gov.cn/xinxigongshi/xinxishuanggongshi/";
    /********************************************************************************************************************/
    //许可证对象清单地址 json
     String  urlZM = "https://www.creditchina.gov.cn/api/publicity_info_search?keyword=&dataType=1&areaCode=&page=1&pageSize=10";
    //许可证对象明细地址 json
    //String urlZMDetail = "https://www.creditchina.gov.cn/api/pub_permissions_name?name="+new String("绩溪县汇通汽车运输有限公司".getBytes("iso8859-1"),"UTF-8")+"&page=1&pageSize=10";

    /********************************************************************************************************************/

    //处罚对象清单地址 json  keyword\areaCode\pageSize
    String urlPunishObjectAPI = "https://www.creditchina.gov.cn/api/publicity_info_search?keyword=&dataType=0&areaCode=&page=1&pageSize=10";
    //处罚对象明细地址json
    String urlPunishDetailResult = "https://www.creditchina.gov.cn/api/pub_penalty_name?name=绩溪县汇通汽车运输有限公司&page=1&pageSize=10";


    /*@Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    @Override
    protected String execute() throws Throwable {
        extractContext(url);
        return null;
    }*/
    /**
     * 获取网页内容
     */
    public void extractContext(String url) throws Throwable{

    }

    /**
     * 创建一个htmlUnit webClient 客户端
     * @param ip
     * @param port
     * @return
     */
    public  WebClient createWebClient(String ip, String port) {
        WebClient client = null;
        try {
            if ("".equals(ip) || "".equals(port)) {
                client = new WebClient(BrowserVersion.getDefault());
            } else {
                client = new WebClient(BrowserVersion.getDefault(), ip,
                        Integer.valueOf(port));
            }
            client.getOptions().setUseInsecureSSL(true);
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return client;
    }

    /**
     * 获取api接口相应结果清单
     * @param url       遍历的地址
     * @param sizePage  遍历次数
     * @param webClient  HTMLUnit 客户端
     * @return
     */
    public  List<String> publicityInfoSearch(String url,int sizePage, WebClient webClient) {
        List<String> nameList = new ArrayList<>();
        try {
            for(int j=1;j<=sizePage;j++){
                int pageSize = j;
                WebRequest request = new WebRequest(new URL(url), HttpMethod.GET);
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
                    log.info("----------------\n"+jsonString);
                    Map mapJson = mapper.readValue(jsonString, Map.class);
                    List<Map> listResults = (List)mapJson.get("results");
                    for(int i =0;i<listResults.size();i++){
                        Map mapResult = listResults.get(i);

                        String name = (String)mapResult.get("name");
                        nameList.add(name);
                        log.info("-------dddddd-name--------\n"+name);
                    }

                    return nameList;
                }else{
                    return null;
                }
            }


        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    public  List<Map> pubPenaltyName(String url,List<String> listName, WebClient webClient) {
        List<Map> nameList = new ArrayList<>();
        try {
            for(int i=0;i<listName.size();i++){
                //浏览器的中文需要转码  如：URLEncoder.encode("绩溪县汇通汽车运输有限公司", "utf-8");
                String name = URLEncoder.encode(listName.get(i), "utf-8");
                WebRequest request = new WebRequest(new URL(url), HttpMethod.GET);
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
                    log.info("----------------\n"+jsonString);
                    Map mapJson = mapper.readValue(jsonString, Map.class);
                    Map listResult = (Map)mapJson.get("result");
                    List<Map> listResults = (List)listResult.get("results");
                    for(int j =0;j<listResults.size();j++){
                        Map mapResult = listResults.get(j);
                        nameList.add(mapResult);
                        log.info("------ccccc--name--------\n"+mapResult);
                    }

                    return nameList;
                }else{
                    return null;
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    public void result(int pageSize,String keywordy)throws Throwable{
        //浏览器的中文需要转码  如：URLEncoder.encode("绩溪县汇通汽车运输有限公司", "utf-8");
        String name = URLEncoder.encode(keywordy, "utf-8");

        //获取处罚的列表清单的名称
        String urlPunishObjectAPI = "https://www.creditchina.gov.cn/api/publicity_info_search?keyword="+name+"&dataType=0&areaCode=&page="+pageSize+"&pageSize=10";
        List<String> nameList = new ArrayList<>();
        if((keywordy.equals("")||keywordy==null)&&pageSize<=0){
            publicityInfoSearch(urlPunishObjectAPI,5,createWebClient("",""));
        }
        if((keywordy.equals("")||keywordy==null)&& pageSize>0){
            publicityInfoSearch(urlPunishObjectAPI,pageSize,createWebClient("",""));
        }
        if(!keywordy.equals("")&&keywordy!=null){
            publicityInfoSearch(urlPunishObjectAPI,1,createWebClient("",""));
        }

        //获取处罚的详情

        String urlZMDetail = "https://www.creditchina.gov.cn/api/pub_penalty_name?name="+name+"&page=1&pageSize=10";
        pubPenaltyName(urlZMDetail,nameList,createWebClient("",""));

    }
    @Test
    public void test() throws Throwable{

        result(5,"");
    }
}
