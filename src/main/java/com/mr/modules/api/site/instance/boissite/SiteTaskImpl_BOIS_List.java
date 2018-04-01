package com.mr.modules.api.site.instance.boissite;

import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("bois")
@Scope("prototype")
public class SiteTaskImpl_BOIS_List extends SiteTaskExtend {
    @Override
    protected String execute() throws Throwable {
        //页数
        int pageAll = 1;
        String targetUri1 = "http://www.circ.gov.cn/web/site0/tab5241/";
        String fullTxt = getData(targetUri1);
//        log.info("\n"+fullTxt);
        pageAll = extractPage(fullTxt);
        List listMap = new ArrayList<>();
        listMap =extractUrl(pageAll);
        for(int i =0;i<listMap.size();i++){
            LinkedHashMap lh = (LinkedHashMap)listMap.get(i);
            //解析安徽信息
            /*if(lh.get("provinceCity").toString().indexOf("安徽")>-1){
                new SiteTaskImpl_BOIS_AnHui().extractContent(getData(getData(lh.get("herf").toString())));
            }*/
            //解析宁波信息
            /*if(lh.get("provinceCity").toString().indexOf("宁波")>-1){
                new SiteTaskImpl_BOIS_NingBo().extractContent(getData(getData(lh.get("herf").toString())));
            }*/
        }

//        exportToXls("bois.xlsx",listMap);
        log.info("-------------保监局处罚抓取完成-------------");
        return null;
    }
    /**
     * 获取保监会处罚列表所有页数
     * @param fullTxt
     * @return
     */
    public int extractPage(String fullTxt){
        int pageAll = 1;
        Document doc = Jsoup.parse(fullTxt);
        Elements td = doc.getElementsByClass("Normal");
        //记录元素的数量
        int serialNo = td.size();
        pageAll = Integer.valueOf(td.get(serialNo-1).text().split("/")[1]);
        log.info("-------------********---------------");
        log.info("处罚列表清单总页数为："+pageAll);
        log.info("-------------********---------------");
        return  pageAll;
    }
    //获取总页数下的所有连接url，所属省份 provinceCity，主题title，编号Id
    // TODO http://www.circ.gov.cn/web/site0/tab5241/module14458/page454.htm
    public List<LinkedHashMap> extractUrl(int pageAll){

        List<LinkedHashMap> listUrl = new ArrayList<>();
        String urlfullTxt = "http://www.circ.gov.cn/web/site0/tab5241/module14458/page1.htm";
        for(int i=1 ; i<=pageAll;i++){
             urlfullTxt   = getData("http://www.circ.gov.cn/web/site0/tab5241/module14458/page"+i+".htm");
             Document doc = Jsoup.parse(urlfullTxt);
             Elements elements = doc.getElementsByClass("hui14");
             elements.size();
             for (Element element : elements){
                 LinkedHashMap map = new LinkedHashMap();
                 String[] provinceCityStr = element.text().replace(":","：").split("：");
                 //所属省份
                 String provinceCity = provinceCityStr[0];
                 Element elementSpan = element.getElementById("lan1");
                 Elements element1A = elementSpan.getElementsByTag("A");
                 //Url地址
                 String href = "http://www.circ.gov.cn"+element1A.attr("href");
                 //正文获取
                 String textContext = getData(href);
                 Document docText = Jsoup.parse(textContext);
                 String text = docText.getElementsByClass("xilanwb").text();
                 //文档名称
                 String title = element1A.attr("title");
                 //文档编号
                 String id = element1A.attr("id");
                 map.put("id",id);
                 map.put("href",href);
                 map.put("provinceCity",provinceCity);
                 map.put("title",title);
                 map.put("text",text);

                 /*if(provinceCity.indexOf("安徽")>-1){
                     SiteTaskImpl_BOIS_AnHui siteTaskImpl_bois_anHui = new SiteTaskImpl_BOIS_AnHui();
                     siteTaskImpl_bois_anHui.extractContent(getData(href));
                 }*/
                 //解析宁波信息
                if(provinceCity.indexOf("宁波")>-1){
                    log.info("url-------"+href);
                    new SiteTaskImpl_BOIS_NingBo().extractContent(getData(href));
                }
                 listUrl.add(map);
//                 log.info(i+"----id："+id+"----href:"+href+"----provinceCity:"+provinceCity+"----title:"+title);
             }

        }
    return listUrl;
    }
}
