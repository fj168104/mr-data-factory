package com.mr.modules.api.site.instance.creditchinasite.henansite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.model.DiscreditBlacklist;
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
 * 涉金融领域严重失信债务人名单	文本	主体名称 主体代码
 */
@Slf4j
@Component("creditchinahenansite0006")
@Scope("prototype")
public class CreditChinaHeNanSite0006 extends SiteTaskExtend_CreditChina {
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
    public void webContext(String keyWord, String ip, String port) throws Throwable{
        String baseUrl = "http://www.xyhn.gov.cn/CMSInterface/cms/getMdList?id=294924a8ce6e11e78ce51c3947289257&pagesize=20&start=1&type=2&content=";
        try {
            String urlMain = baseUrl + URLEncoder.encode(keyWord, "UTF-8");

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
                    mapResult.put("sourceUrl",baseUrl+(String)mapResult.get("mc"));
                    mapResult.put("source","信用中国（河南）");
                    mapResult.put("subject","涉金融领域严重失信债务人名单");
                    mapResult.put("enterpriseName",mapResult.remove("mc"));
                    mapResult.put("enterpriseCode1",mapResult.remove("dm"));
                    insertDiscreditBlacklist(mapResult);
                }
                int totalPage = (int) map.get("totalPage");
                if (totalPage > 1) {
                    for (int i = 2; i <= totalPage; i++) {
                        urlMain = "http://www.xyhn.gov.cn/CMSInterface/cms/getMdList?id=294924a8ce6e11e78ce51c3947289257&pagesize=20&start=" + i + "&type=2&content=" + URLEncoder.encode(keyWord, "UTF-8");
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
                            mapResult.put("sourceUrl",baseUrl+(String)mapResult.get("mc"));
                            mapResult.put("source","信用中国（河南）");
                            mapResult.put("subject","涉金融领域严重失信债务人名单");
                            mapResult.put("enterpriseName",mapResult.remove("mc"));
                            mapResult.put("enterpriseCode1",mapResult.remove("dm"));
                            insertDiscreditBlacklist(mapResult);
                        }
                    }
                }
            }
            webClient.close();
        } catch (IOException e) {
            log.error("获取网页异常···" + e.getMessage());
        }
    }
}