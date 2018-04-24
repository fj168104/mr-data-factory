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
        if(!oneFinanceMonitorPunish.getUrl().contains("bxjg")&&!oneFinanceMonitorPunish.getUrl().equalsIgnoreCase("")&&(oneFinanceMonitorPunish.getRegion().equals("")||oneFinanceMonitorPunish.getRegion()==null)){
            extractByAreaUrl(oneFinanceMonitorPunish.getUrl());
        }else if(oneFinanceMonitorPunish.getUrl().contains("bxjg")&&!oneFinanceMonitorPunish.getUrl().equalsIgnoreCase("")&&!oneFinanceMonitorPunish.getRegion().equals("")&&oneFinanceMonitorPunish.getRegion()!=null){
            extractByMasterStationUrl(oneFinanceMonitorPunish.getUrl(),oneFinanceMonitorPunish.getRegion());
        }else{
            extract();
        }
        return null;
    }




    /**
     * 总站
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
                mapInfo = new SiteTaskImpl_BOIS_AnHui().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("北京")>-1){//解析北京信息
                mapInfo = new SiteTaskImpl_BOIS_BeiJing().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("重庆")>-1){//解析重庆信息
                mapInfo = new SiteTaskImpl_BOIS_ChongQing().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("大连")>-1){//解析大连信息
                SiteTaskImpl_BOIS_DaLian stbd = new SiteTaskImpl_BOIS_DaLian();
                List<Map<String,String>> mapList = stbd.extractContent(getData(lh.get("href").toString()));
                for(Map<String,String> map : mapList){
                    punishInfos.add(getObj(map,lh.get("href").toString()));
                }
            }else if(lh.get("provinceCity").toString().indexOf("福建")>-1){//解析福建信息
                mapInfo = new SiteTaskImpl_BOIS_FuJian().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("甘肃")>-1){//解析甘肃信息
                mapInfo = new SiteTaskImpl_BOIS_GanSu().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("广东")>-1){//解析广东信息
                mapInfo = new SiteTaskImpl_BOIS_GuangDong().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("广西")>-1){//解析广西信息
                mapInfo = new SiteTaskImpl_BOIS_GuangXi().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("贵州")>-1){//解析贵州
                SiteTaskImpl_BOIS_GuiZhou stbg = new SiteTaskImpl_BOIS_GuiZhou();
                List<Map<String,String>> mapList = stbg.extractContent(getData(lh.get("href").toString()));
                for(Map<String,String> map : mapList){
                    punishInfos.add(getObj(map,lh.get("href").toString()));
                }
            }else if(lh.get("provinceCity").toString().indexOf("海南")>-1){//解析海南
                mapInfo = new SiteTaskImpl_BOIS_HaiNan().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("河南")>-1){//解析河南
                mapInfo = new SiteTaskImpl_BOIS_HeNan().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("湖北")>-1){//解析湖北
                mapInfo = new SiteTaskImpl_BOIS_HuBei().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("湖南")>-1){//解析湖南
                SiteTaskImpl_BOIS_HuNan stbh = new SiteTaskImpl_BOIS_HuNan();
                List<Map<String,String>> mapList = stbh.extractContent(getData(lh.get("href").toString()));
                for(Map<String,String> map : mapList){
                    punishInfos.add(getObj(map,lh.get("href").toString()));
                }
            }else if(lh.get("provinceCity").toString().indexOf("江苏")>-1){//解析江苏
                mapInfo = new SiteTaskImpl_BOIS_JiangSu().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("江西")>-1){//解析江西
                mapInfo = new SiteTaskImpl_BOIS_JiangXi().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("宁波")>-1){//解析宁波信息
                mapInfo = new SiteTaskImpl_BOIS_NingBo().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("青海")>-1){//解析青海信息
                mapInfo = new SiteTaskImpl_BOIS_QingHai().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("青岛")>-1){//解析青岛信息
                mapInfo = new SiteTaskImpl_BOIS_QingDao().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("陕西")>-1){//解析陕西信息
                mapInfo = new SiteTaskImpl_BOIS_ShaanXi().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("山东")>-1){//解析山东信息
                mapInfo = new SiteTaskImpl_BOIS_ShanDong().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("上海")>-1){//解析上海信息
                mapInfo = new SiteTaskImpl_BOIS_ShangHai().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("山西")>-1){//解析山西信息
                mapInfo = new SiteTaskImpl_BOIS_ShanXi().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("深圳")>-1){//解析深圳信息
                mapInfo = new SiteTaskImpl_BOIS_ShenZhen().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("四川")>-1){//解析四川信息
                mapInfo = new SiteTaskImpl_BOIS_SiChuan().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("苏州")>-1){//解析苏州信息
                mapInfo = new SiteTaskImpl_BOIS_SuZhou().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("唐山")>-1){//解析唐山信息
                mapInfo = new SiteTaskImpl_BOIS_TangShan().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("温州")>-1){//解析温州信息
                mapInfo = new SiteTaskImpl_BOIS_WenZhou().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("厦门")>-1){//解析厦门信息
                mapInfo = new SiteTaskImpl_BOIS_XiaMen().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("新疆")>-1){//解析新疆信息
                mapInfo = new SiteTaskImpl_BOIS_XinJiang().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("西藏")>-1){//解析西藏信息
                mapInfo = new SiteTaskImpl_BOIS_XiZang().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("烟台")>-1){//解析烟台信息
                mapInfo = new SiteTaskImpl_BOIS_YanTai().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("云南")>-1){//解析云南信息
                mapInfo = new SiteTaskImpl_BOIS_YunNan().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("浙江")>-1){//解析浙江信息
                mapInfo = new SiteTaskImpl_BOIS_ZheJiang().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("黑龙江")>-1){//解析黑龙江信息
                mapInfo = new SiteTaskImpl_BOIS_HeiLongJiang().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("吉林")>-1){//解析吉林信息
                mapInfo = new SiteTaskImpl_BOIS_JiLin().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("辽宁")>-1){//解析辽宁信息
                mapInfo = new SiteTaskImpl_BOIS_LiaoNing().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("内蒙古")>-1){//解析内蒙古信息
                mapInfo = new SiteTaskImpl_BOIS_NeiMengGu().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }else if(lh.get("provinceCity").toString().indexOf("汕头")>-1){//解析汕头信息
                mapInfo = new SiteTaskImpl_BOIS_ShanTou().extractContent(getData(lh.get("href").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("href").toString()));
            }


        }

        log.info("-------------保监局处罚抓取完成-------------");
        return punishInfos;
    }

    /**
     * 通过主站获取
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

    /**
     * 重主站获取
     * 获取总页数下的所有连接url，所属省份 provinceCity，主题title，编号Id
     * @param pageAll
     * @return
     */
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
        financeMonitorPunish.setCompanyFullName(mapInfo.get("companyFullName"));//公司全称
        financeMonitorPunish.setDomicile(mapInfo.get("punishToOrgAddress"));//机构住址
        financeMonitorPunish.setLegalRepresentative(mapInfo.get("punishToOrgHolder"));//机构负责人
        financeMonitorPunish.setPartyPerson(mapInfo.get("priPerson"));//受处罚人
        financeMonitorPunish.setPartyPersonId(mapInfo.get("priPersonCert"));//受处罚人证件号码
        financeMonitorPunish.setPartyPersonTitle(mapInfo.get("priJob"));//职务
        financeMonitorPunish.setPartyPersonDomi(mapInfo.get("priAddress"));//自然人住址
        financeMonitorPunish.setDetails(mapInfo.get("stringDetail"));//详情
        financeMonitorPunish.setUrl(href);
        financeMonitorPunish.setSource("保监局");
        financeMonitorPunish.setObject("行政处罚决定");

        //保存入库
        saveOne(financeMonitorPunish,false);

        return financeMonitorPunish;
    }

    /**
     * @param pageAll
     * @param area
     * @return
     */
    //根据地方名获取总页数下的所有连接url
    // TODO http://bxjg.circ.gov.cn/web/site0/tab5241/module14458/page1.htm
    public List<LinkedHashMap> extractUrlByArea(int pageAll,String area){

        List<LinkedHashMap> listUrl = new ArrayList<>();
        String urlfullTxt = "http://bxjg.circ.gov.cn/web/site0/tab5241/module14458/page1.htm";
        for(int i=1 ; i<=pageAll;i++){
            urlfullTxt   = getData("http://bxjg.circ.gov.cn/web/site0/tab5241/module14458/page"+i+".htm");
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
     * 保监局，时间增量获取数据
     * 根据发布日期获取总页数下的所有连接url，获取指定日期的数据时格式为yyyy-mm-dd,获取某年某一个月内的数据时格式为yyyy-mm
     * */
    // TODO http://bxjg.circ.gov.cn/web/site0/tab5241/module14458/page1.htm
    public List<LinkedHashMap> extractUrlByDate(int pageAll,String date){

        List<LinkedHashMap> listUrl = new ArrayList<>();
        String urlfullTxt = "http://bxjg.circ.gov.cn/web/site0/tab5241/module14458/page1.htm";
        for(int i=1 ; i<=pageAll;i++){
            urlfullTxt   = getData("http://bxjg.circ.gov.cn/web/site0/tab5241/module14458/page"+i+".htm");
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

    /**
     * 区域单笔 处理
     * @param url
     * @return
     */
    public List<FinanceMonitorPunish> extractByAreaUrl(String url){
        List<FinanceMonitorPunish> punishInfos = new ArrayList<FinanceMonitorPunish>();
        Map<String,String> mapInfo = new HashMap<>();
        // url=http://shanxi.circ.gov.cn/web/site31/tab3452/info4063099.htm
        if(url.contains("anhui")){ //解析安徽信息
            mapInfo = new SiteTaskImpl_BOIS_AnHui().extractContent(getData(url));
        }else if(url.contains("chongqing")){//解析重庆信息
            mapInfo = new SiteTaskImpl_BOIS_ChongQing().extractContent(getData(url));
        }else if(url.contains("dalian")){//解析大连信息
            SiteTaskImpl_BOIS_DaLian stbd = new SiteTaskImpl_BOIS_DaLian();
            List<Map<String,String>> mapList = stbd.extractContent(getData(url));
            for(Map<String,String> map : mapList){
                punishInfos.add(getObj(map,url));
            }
        }else if(url.contains("fujian")){//解析福建信息
            mapInfo = new SiteTaskImpl_BOIS_FuJian().extractContent(getData(url));
        }else if(url.contains("gansu")){//解析甘肃信息
            mapInfo = new SiteTaskImpl_BOIS_GanSu().extractContent(getData(url));
        }else if(url.contains("guangdong")){//解析广东信息
            mapInfo = new SiteTaskImpl_BOIS_GuangDong().extractContent(getData(url));
        }else if(url.contains("guangxi")){//解析广西信息
            mapInfo = new SiteTaskImpl_BOIS_GuangXi().extractContent(getData(url));
        }else if(url.contains("guizhou")){//解析贵州
            SiteTaskImpl_BOIS_GuiZhou stbg = new SiteTaskImpl_BOIS_GuiZhou();
            List<Map<String,String>> mapList = stbg.extractContent(getData(url));
            for(Map<String,String> map : mapList){
                punishInfos.add(getObj(map,url));
            }
        }else if(url.contains("hainan")){//解析海南
            mapInfo = new SiteTaskImpl_BOIS_HaiNan().extractContent(getData(url));
        }else if(url.contains("heilongjiang")){//解析黑龙江
            mapInfo = new SiteTaskImpl_BOIS_HaiNan().extractContent(getData(url));
        }else if(url.contains("henan")){//解析河南
            mapInfo = new SiteTaskImpl_BOIS_HeNan().extractContent(getData(url));
        }else if(url.contains("hubei")){//解析湖北
            mapInfo = new SiteTaskImpl_BOIS_HuBei().extractContent(getData(url));
        }else if(url.contains("jiangsu")){//解析江苏
            mapInfo = new SiteTaskImpl_BOIS_JiangSu().extractContent(getData(url));
        }else if(url.contains("jiangxi")){//解析江西
            mapInfo = new SiteTaskImpl_BOIS_JiangXi().extractContent(getData(url));
        }else if(url.contains("jilin")){//解析吉林信息
            mapInfo = new SiteTaskImpl_BOIS_JiLin().extractContent(getData(url));
        }else if(url.contains("liaoning")){//解析辽宁信息
            mapInfo = new SiteTaskImpl_BOIS_LiaoNing().extractContent(getData(url));
        }else if(url.contains("neimenggu")){//解析内蒙古信息
            mapInfo = new SiteTaskImpl_BOIS_NeiMengGu().extractContent(getData(url));
        }else if(url.contains("ningbo")){//解析宁波信息
            mapInfo = new SiteTaskImpl_BOIS_NingBo().extractContent(getData(url));
        }else if(url.contains("ningxia")){//解析宁夏信息
            mapInfo = new SiteTaskImpl_BOIS_NingXia().extractContent(getData(url));
        }else if(url.contains("qinghai")){//解析青海信息
            mapInfo = new SiteTaskImpl_BOIS_QingHai().extractContent(getData(url));
        }else if(url.contains("qingdao")){//解析青岛信息
            mapInfo = new SiteTaskImpl_BOIS_QingDao().extractContent(getData(url));
        }else if(url.contains("shaanxi")){//解析陕西信息
            mapInfo = new SiteTaskImpl_BOIS_ShaanXi().extractContent(getData(url));
        }else if(url.contains("shandong")){//解析山东信息
            mapInfo = new SiteTaskImpl_BOIS_ShanDong().extractContent(getData(url));
        }else if(url.contains("shanghai")){//解析上海信息
            mapInfo = new SiteTaskImpl_BOIS_ShangHai().extractContent(getData(url));
        }else if(url.contains("shantou")){//解析汕头信息
            mapInfo = new SiteTaskImpl_BOIS_ShanTou().extractContent(getData(url));
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
    /**
     * 主站单笔 处理
     * @param url
     * @return
     */
    public List<FinanceMonitorPunish> extractByMasterStationUrl(String region,String url){
        List<FinanceMonitorPunish> punishInfos = new ArrayList<FinanceMonitorPunish>();
        Map<String,String> mapInfo = new HashMap<>();
        // url=http://shanxi.circ.gov.cn/web/site31/tab3452/info4063099.htm
        if(!url.contains("anhui")&&region.contains("安徽")){ //解析安徽信息
            mapInfo = new SiteTaskImpl_BOIS_AnHui().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("chongqing")&&region.contains("重庆")){//解析重庆信息
            mapInfo = new SiteTaskImpl_BOIS_ChongQing().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("dalian")&&region.contains("大连")){//解析大连信息
            SiteTaskImpl_BOIS_DaLian stbd = new SiteTaskImpl_BOIS_DaLian();
            List<Map<String,String>> mapList = stbd.extractContent(getData(url));
            for(Map<String,String> map : mapList){
                punishInfos.add(getObj(map,url));
            }
        }else if(!url.contains("fujian")&&region.contains("福建")){//解析福建信息
            mapInfo = new SiteTaskImpl_BOIS_FuJian().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("gansu")&&region.contains("甘肃")){//解析甘肃信息
            mapInfo = new SiteTaskImpl_BOIS_GanSu().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("guangdong")&&region.contains("广东")){//解析广东信息
            mapInfo = new SiteTaskImpl_BOIS_GuangDong().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("guangxi")&&region.contains("广西")){//解析广西信息
            mapInfo = new SiteTaskImpl_BOIS_GuangXi().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("guizhou")&&region.contains("贵州")){//解析贵州
            SiteTaskImpl_BOIS_GuiZhou stbg = new SiteTaskImpl_BOIS_GuiZhou();
            List<Map<String,String>> mapList = stbg.extractContent(getData(url));
            for(Map<String,String> map : mapList){
                punishInfos.add(getObj(map,url));
            }
        }else if(!url.contains("hainan")&&region.contains("海南")){//解析海南
            mapInfo = new SiteTaskImpl_BOIS_HaiNan().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("heilongjiang")&&region.contains("黑龙江")){//解析黑龙江
            mapInfo = new SiteTaskImpl_BOIS_HeiLongJiang().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("henan")&&region.contains("河南")){//解析河南
            mapInfo = new SiteTaskImpl_BOIS_HeNan().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("hubei")&&region.contains("湖北")){//解析湖北
            mapInfo = new SiteTaskImpl_BOIS_HuBei().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("jiangsu")&&region.contains("江苏")){//解析江苏
            mapInfo = new SiteTaskImpl_BOIS_JiangSu().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("jiangxi")&&region.contains("江西")){//解析江西
            mapInfo = new SiteTaskImpl_BOIS_JiangXi().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("jilin")&&region.contains("吉林")){//解析吉林信息
            mapInfo = new SiteTaskImpl_BOIS_JiLin().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("liaoning")&&region.contains("辽宁")){//解析辽宁信息
            mapInfo = new SiteTaskImpl_BOIS_LiaoNing().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("neimenggu")&&region.contains("内蒙古")){//解析内蒙古信息
            mapInfo = new SiteTaskImpl_BOIS_NeiMengGu().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("ningbo")&&region.contains("宁波")){//解析宁波信息
            mapInfo = new SiteTaskImpl_BOIS_NingBo().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("ningxia")&&region.contains("宁夏")){//解析宁夏信息
            mapInfo = new SiteTaskImpl_BOIS_NingXia().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("qinghai")&&region.contains("青海")){//解析青海信息
            mapInfo = new SiteTaskImpl_BOIS_QingHai().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("qingdao")&&region.contains("青岛")){//解析青岛信息
            mapInfo = new SiteTaskImpl_BOIS_QingDao().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("shaanxi")&&region.contains("陕西")){//解析陕西信息
            mapInfo = new SiteTaskImpl_BOIS_ShaanXi().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("shandong")&&region.contains("山东")){//解析山东信息
            mapInfo = new SiteTaskImpl_BOIS_ShanDong().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("shanghai")&&region.contains("上海")){//解析上海信息
            mapInfo = new SiteTaskImpl_BOIS_ShangHai().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("shantou")&&region.contains("汕头")){//解析汕头信息
            mapInfo = new SiteTaskImpl_BOIS_ShanTou().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("shanxi")&&region.contains("山西")){//解析山西信息
            mapInfo = new SiteTaskImpl_BOIS_ShanXi().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("shenzhen")&&region.contains("深圳")){//解析深圳信息
            mapInfo = new SiteTaskImpl_BOIS_ShenZhen().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("sichuan")&&region.contains("四川")){//解析四川信息
            mapInfo = new SiteTaskImpl_BOIS_SiChuan().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("suzhou")&&region.contains("苏州")){//解析苏州信息
            mapInfo = new SiteTaskImpl_BOIS_SuZhou().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("tangshan")&&region.contains("唐山")){//解析唐山信息
            mapInfo = new SiteTaskImpl_BOIS_TangShan().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("wenzhou")&&region.contains("温州")){//解析温州信息
            mapInfo = new SiteTaskImpl_BOIS_WenZhou().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("xiamen")&&region.contains("厦门")){//解析厦门信息
            mapInfo = new SiteTaskImpl_BOIS_XiaMen().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("xinjiang")&&region.contains("新疆")){//解析新疆信息
            mapInfo = new SiteTaskImpl_BOIS_XinJiang().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("xizang")&&region.contains("西藏")){//解析西藏信息
            mapInfo = new SiteTaskImpl_BOIS_XiZang().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("yantai")&&region.contains("烟台")){//解析烟台信息
            mapInfo = new SiteTaskImpl_BOIS_YanTai().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("yunnan")&&region.contains("云南")){//解析云南信息
            mapInfo = new SiteTaskImpl_BOIS_YunNan().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }else if(!url.contains("zhejiang")&&region.contains("浙江")){//解析浙江信息
            mapInfo = new SiteTaskImpl_BOIS_ZheJiang().extractContent(getData(url));
            punishInfos.add(getObj(mapInfo,url));
        }

        return punishInfos;
    }

}
