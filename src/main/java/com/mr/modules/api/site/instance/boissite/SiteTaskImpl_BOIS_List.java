package com.mr.modules.api.site.instance.boissite;

import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("bois")
@Scope("prototype")
public class SiteTaskImpl_BOIS_List extends SiteTaskExtend {

    @Override
    protected String execute() throws Throwable {
        extract();
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        if(!oneFinanceMonitorPunish.getUrl().equalsIgnoreCase("")){
            extractByUrl(oneFinanceMonitorPunish.getUrl());
        }else{
            extract();
        }
        return null;
    }




    /**
     * 地方保监局 解析
     * */
    public List<FinanceMonitorPunish> extract(){
        //页数
        int pageAll = 1;
        String targetUrl = "http://bxjg.circ.gov.cn/web/site0/tab5241/";
        String fullTxt = getData(targetUrl);
        pageAll = extractPage(fullTxt);
        List listMap = new ArrayList<>();

        List<FinanceMonitorPunish> punishInfos = new ArrayList<FinanceMonitorPunish>();
        if(oneFinanceMonitorPunish!=null){
            if(!oneFinanceMonitorPunish.getPublishDate().equalsIgnoreCase("")){
                listMap = extractUrlByDate(pageAll,oneFinanceMonitorPunish.getPublishDate());
            }else if(!oneFinanceMonitorPunish.getRegion().equalsIgnoreCase("")){
                listMap = extractUrlByArea(pageAll,oneFinanceMonitorPunish.getRegion());
            }
        }else{
            listMap =extractUrl(pageAll);
        }


        for(int i =0;i<listMap.size();i++){
            LinkedHashMap lh = (LinkedHashMap)listMap.get(i);
            Map<String,String> mapInfo = new HashMap<>();
            if(lh.get("provinceCity").toString().indexOf("安徽")>-1){//解析安徽信息
                mapInfo = new SiteTaskImpl_BOIS_AnHui().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("重庆")>-1){//解析重庆信息
                mapInfo = new SiteTaskImpl_BOIS_ChongQing().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("大连")>-1){//解析大连信息
                mapInfo = new SiteTaskImpl_BOIS_DaLian().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("福建")>-1){//解析福建信息
                mapInfo = new SiteTaskImpl_BOIS_FuJian().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("甘肃")>-1){//解析甘肃信息
                mapInfo = new SiteTaskImpl_BOIS_GanSu().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("广东")>-1){//解析广东信息
                mapInfo = new SiteTaskImpl_BOIS_GuangDong().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("广西")>-1){//解析广西信息
                mapInfo = new SiteTaskImpl_BOIS_GuangXi().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("贵州")>-1){//解析贵州
                mapInfo = new SiteTaskImpl_BOIS_GuiZhou().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("海南")>-1){//解析海南
                mapInfo = new SiteTaskImpl_BOIS_HaiNan().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("河南")>-1){//解析河南
                mapInfo = new SiteTaskImpl_BOIS_HeNan().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("湖北")>-1){//解析湖北
                mapInfo = new SiteTaskImpl_BOIS_HuBei().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("江苏")>-1){//解析江苏
                mapInfo = new SiteTaskImpl_BOIS_JiangSu().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("江西")>-1){//解析江西
                mapInfo = new SiteTaskImpl_BOIS_JiangXi().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("宁波")>-1){//解析宁波信息
                mapInfo = new SiteTaskImpl_BOIS_NingBo().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("青海")>-1){//解析青海信息
                mapInfo = new SiteTaskImpl_BOIS_QingHai().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("山东")>-1){//解析山东信息
                mapInfo = new SiteTaskImpl_BOIS_ShanDong().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("上海")>-1){//解析上海信息
                mapInfo = new SiteTaskImpl_BOIS_ShangHai().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("山西")>-1){//解析山西信息
                mapInfo = new SiteTaskImpl_BOIS_ShanXi().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("深圳")>-1){//解析深圳信息
                mapInfo = new SiteTaskImpl_BOIS_ShenZhen().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("四川")>-1){//解析四川信息
                mapInfo = new SiteTaskImpl_BOIS_SiChuan().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("苏州")>-1){//解析苏州信息
                mapInfo = new SiteTaskImpl_BOIS_SuZhou().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("唐山")>-1){//解析唐山信息
                mapInfo = new SiteTaskImpl_BOIS_TangShan().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("温州")>-1){//解析温州信息
                mapInfo = new SiteTaskImpl_BOIS_WenZhou().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("厦门")>-1){//解析厦门信息
                mapInfo = new SiteTaskImpl_BOIS_XiaMen().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("新疆")>-1){//解析新疆信息
                mapInfo = new SiteTaskImpl_BOIS_XinJiang().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("西藏")>-1){//解析西藏信息
                mapInfo = new SiteTaskImpl_BOIS_XiZang().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("烟台")>-1){//解析烟台信息
                mapInfo = new SiteTaskImpl_BOIS_YanTai().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("云南")>-1){//解析云南信息
                mapInfo = new SiteTaskImpl_BOIS_YunNan().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("浙江")>-1){//解析浙江信息
                mapInfo = new SiteTaskImpl_BOIS_ZheJiang().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("黑龙江")>-1){//解析黑龙江信息
                mapInfo = new SiteTaskImpl_BOIS_HeiLongJiang().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("吉林")>-1){//解析吉林信息
                mapInfo = new SiteTaskImpl_BOIS_JiLin().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("辽宁")>-1){//解析辽宁信息
                mapInfo = new SiteTaskImpl_BOIS_LiaoNing().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("内蒙古")>-1){//解析内蒙古信息
                mapInfo = new SiteTaskImpl_BOIS_NeiMengGu().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("陕西")>-1){//解析陕西信息
                mapInfo = new SiteTaskImpl_BOIS_ShaanXi().extractContent(getData(lh.get("herf").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("汕头")>-1){//解析汕头信息
                mapInfo = new SiteTaskImpl_BOIS_ShanTou().extractContent(getData(lh.get("herf").toString()));
            }
            punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));

        }

        log.info("-------------保监局处罚抓取完成-------------");
        return punishInfos;
    }

    /**
     * 获取保监局处罚列表所有页数
     * @param fullTxt
     * @return
     */
    public int extractPage(String fullTxt){
        int pageAll = 1;
        Document doc = Jsoup.parse(fullTxt);
        Elements td = doc.getElementsByClass("Normal");
        //记录元素的数量
        int serialNo = td.size();
        log.info("--------serialNo---------"+serialNo);
        pageAll = Integer.valueOf(td.get(serialNo-1).text().split("/")[1]);
        log.info("-------------********---------------");
        log.info("处罚列表清单总页数为："+pageAll);
        log.info("-------------********---------------");
        return  pageAll;
    }

    //获取总页数下的所有连接url，所属省份 provinceCity，主题title，编号Id
    // TODO http://bxjg.circ.gov.cn/web/site0/tab5241/module14458/page2.htm
    public List<LinkedHashMap> extractUrl(int pageAll){

        List<LinkedHashMap> listUrl = new ArrayList<>();
        String urlfullTxt = "http://bxjg.circ.gov.cn/web/site0/tab5241/module14458/page1.htm";
        int countPage =0;
        int countUrl = 0;
        for(int i=1 ; i<=pageAll;i++){
            urlfullTxt   = getData("http://bxjg.circ.gov.cn/web/site0/tab5241/module14458/page"+i+".htm");

            Document doc = Jsoup.parse(urlfullTxt);
            Elements elements = doc.getElementsByClass("hui14");
            elements.size();
            countPage++;
            for (Element element : elements){
                LinkedHashMap map = new LinkedHashMap();
                String[] provinceCityStr = element.text().replace(":","：").split("：");
                //所属省份
                String provinceCity = provinceCityStr[0];

                Element elementSpan = element.getElementById("lan1");
                Elements element1A = elementSpan.getElementsByTag("A");
                //Url地址
                String href = "http://bxjg.circ.gov.cn"+element1A.attr("href");
                countUrl ++;
                log.info("页序号："+countPage +"==url序号："+countUrl+"======省市："+provinceCity+"-------区域------href:"+href);
                //正文获取
                String textContext = getData(href);
                Document docText = Jsoup.parse(textContext);
                String text = docText.getElementsByClass("xilanwb").text();
                //文档名称
                String title = element1A.attr("title");
                //文档编号
                String id = element1A.attr("id");
                //发布时间
                String publishDate = "20" +element.nextElementSibling().text().replace("(","").replace(")","");

                map.put("id",id);
                map.put("href",href);
                map.put("provinceCity",provinceCity);
                map.put("title",title);
                map.put("text",text);
                map.put("publishDate",publishDate);

                listUrl.add(map);
            }

        }
        return listUrl;
    }


    /**
     * 获取Obj,并入库
     * */
    public FinanceMonitorPunish getObj(Map<String,String> mapInfo,String href){

        FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
        financeMonitorPunish.setPunishNo(mapInfo.get("punishNo"));//处罚文号
        financeMonitorPunish.setPunishTitle(mapInfo.get("titleStr"));//标题
        financeMonitorPunish.setPublisher(mapInfo.get("publishOrg"));//发布机构
        financeMonitorPunish.setPublishDate(mapInfo.get("publishDate"));//发布时间
        financeMonitorPunish.setPunishInstitution(mapInfo.get("punishOrg"));//处罚机关
        financeMonitorPunish.setPunishDate(mapInfo.get("punishDate"));//处罚时间
        financeMonitorPunish.setPartyInstitution(mapInfo.get("punishToOrg"));//当事人（公司）=处罚对象
        financeMonitorPunish.setDomicile(mapInfo.get("punishToOrgAddress"));//机构住址
        financeMonitorPunish.setLegalRepresentative(mapInfo.get("punishToOrgHolder"));//机构负责人
        financeMonitorPunish.setPartyPerson(mapInfo.get("priPerson"));//受处罚人
        financeMonitorPunish.setPartyPersonId(mapInfo.get("priPersonCert"));//受处罚人证件号码
        financeMonitorPunish.setPartyPersonTitle(mapInfo.get("priJob"));//职务
        financeMonitorPunish.setPartyPersonDomi(mapInfo.get("priAddress"));//自然人住址
        financeMonitorPunish.setDetails(mapInfo.get("stringDetail"));//详情
        financeMonitorPunish.setUrl(href);
        financeMonitorPunish.setSource("地方证监局");
        financeMonitorPunish.setObject("行政处罚决定书");

        //保存入库
        saveOne(financeMonitorPunish,false);

        return financeMonitorPunish;
    }

    //根据地方名获取总页数下的所有连接url
    // TODO http://www.circ.gov.cn/web/site0/tab5241/module14458/page454.htm
    public List<LinkedHashMap> extractUrlByArea(int pageAll,String area){

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

                if(provinceCity.contains(area)){
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
                    //发布时间
                    String publishDate = "20" +element.nextElementSibling().text().replace("(","").replace(")","");

                    map.put("id",id);
                    map.put("href",href);
                    map.put("provinceCity",provinceCity);
                    map.put("title",title);
                    map.put("text",text);
                    map.put("publishDate",publishDate);

                    listUrl.add(map);
                }

            }

        }
        return listUrl;
    }


    /**
     * 根据发布日期获取总页数下的所有连接url，获取指定日期的数据时格式为yyyy-mm-dd,获取某年某一个月内的数据时格式为yyyy-mm
     * */
    // TODO http://www.circ.gov.cn/web/site0/tab5241/module14458/page454.htm
    public List<LinkedHashMap> extractUrlByDate(int pageAll,String date){

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

                //发布时间
                String publishDate = "20" +element.nextElementSibling().text().replace("(","").replace(")","");

                if(date.equalsIgnoreCase(publishDate) || publishDate.contains(date)){
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
                    map.put("publishDate",publishDate);

                    listUrl.add(map);
                }
            }
        }

        return listUrl;
    }

    public List<FinanceMonitorPunish> extractByUrl(String url){
        List<FinanceMonitorPunish> punishInfos = new ArrayList<FinanceMonitorPunish>();
        Map<String,String> mapInfo = new HashMap<>();
        // url=http://shanxi.circ.gov.cn/web/site31/tab3452/info4063099.htm
        if(url.contains("anhui")){ //解析安徽信息
            mapInfo = new SiteTaskImpl_BOIS_AnHui().extractContent(getData(url));
        }else if(url.contains("chongqing")){//解析重庆信息
            mapInfo = new SiteTaskImpl_BOIS_ChongQing().extractContent(getData(url));
        }else if(url.contains("dalian")){//解析大连信息
            mapInfo = new SiteTaskImpl_BOIS_DaLian().extractContent(getData(url));
        }else if(url.contains("fujian")){//解析福建信息
            mapInfo = new SiteTaskImpl_BOIS_FuJian().extractContent(getData(url));
        }else if(url.contains("gansu")){//解析甘肃信息
            mapInfo = new SiteTaskImpl_BOIS_GanSu().extractContent(getData(url));
        }else if(url.contains("guangdong")){//解析广东信息
            mapInfo = new SiteTaskImpl_BOIS_GuangDong().extractContent(getData(url));
        }else if(url.contains("guangxi")){//解析广西信息
            mapInfo = new SiteTaskImpl_BOIS_GuangXi().extractContent(getData(url));
        }else if(url.contains("guizhou")){//解析贵州
            mapInfo = new SiteTaskImpl_BOIS_GuiZhou().extractContent(getData(url));
        }else if(url.contains("hainan")){//解析海南
            mapInfo = new SiteTaskImpl_BOIS_HaiNan().extractContent(getData(url));
        }else if(url.contains("henan")){//解析河南
            mapInfo = new SiteTaskImpl_BOIS_HeNan().extractContent(getData(url));
        }else if(url.contains("hubei")){//解析湖北
            mapInfo = new SiteTaskImpl_BOIS_HuBei().extractContent(getData(url));
        }else if(url.contains("jiangsu")){//解析江苏
            mapInfo = new SiteTaskImpl_BOIS_JiangSu().extractContent(getData(url));
        }else if(url.contains("jiangxi")){//解析江西
            mapInfo = new SiteTaskImpl_BOIS_JiangXi().extractContent(getData(url));
        }else if(url.contains("ningbo")){//解析宁波信息
            mapInfo = new SiteTaskImpl_BOIS_NingBo().extractContent(getData(url));
        }else if(url.contains("qinghai")){//解析青海信息
            mapInfo = new SiteTaskImpl_BOIS_QingHai().extractContent(getData(url));
        }else if(url.contains("shandong")){//解析山东信息
            mapInfo = new SiteTaskImpl_BOIS_ShanDong().extractContent(getData(url));
        }else if(url.contains("shanghai")){//解析上海信息
            mapInfo = new SiteTaskImpl_BOIS_ShangHai().extractContent(getData(url));
        }else if(url.contains("shanxi")){//解析山西信息
            mapInfo = new SiteTaskImpl_BOIS_ShanXi().extractContent(getData(url));
        }else if(url.contains("shenzhen")){//解析深圳信息
            mapInfo = new SiteTaskImpl_BOIS_ShenZhen().extractContent(getData(url));
        }else if(url.contains("sichuan")){//解析四川信息
            mapInfo = new SiteTaskImpl_BOIS_SiChuan().extractContent(getData(url));
        }else if(url.contains("suzhou")){//解析苏州信息
            mapInfo = new SiteTaskImpl_BOIS_SuZhou().extractContent(getData(url));
        }else if(url.contains("tangshan")){//解析唐山信息
            mapInfo = new SiteTaskImpl_BOIS_TangShan().extractContent(getData(url));
        }else if(url.contains("wenzhou")){//解析温州信息
            mapInfo = new SiteTaskImpl_BOIS_WenZhou().extractContent(getData(url));
        }else if(url.contains("xiamen")){//解析厦门信息
            mapInfo = new SiteTaskImpl_BOIS_XiaMen().extractContent(getData(url));
        }else if(url.contains("xinjiang")){//解析新疆信息
            mapInfo = new SiteTaskImpl_BOIS_XinJiang().extractContent(getData(url));
        }else if(url.contains("xizang")){//解析西藏信息
            mapInfo = new SiteTaskImpl_BOIS_XiZang().extractContent(getData(url));
        }else if(url.contains("yantai")){//解析烟台信息
            mapInfo = new SiteTaskImpl_BOIS_YanTai().extractContent(getData(url));
        }else if(url.contains("yunnan")){//解析云南信息
            mapInfo = new SiteTaskImpl_BOIS_YunNan().extractContent(getData(url));
        }else if(url.contains("zhejiang")){//解析浙江信息
            mapInfo = new SiteTaskImpl_BOIS_ZheJiang().extractContent(getData(url));
        }
        punishInfos.add(getObj(mapInfo,url));
        return punishInfos;
    }

}
