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
        extract();
        return null;
    }


    /**
     * 地方保监局 解析
     * */
    public List<FinanceMonitorPunish> extract(){
        //页数
        int pageAll = 1;
        // targetUrl = "http://www.circ.gov.cn/web/site0/tab5241/";
        String targetUrl = "http://www.circ.gov.cn/web/site0/tab5241/";
        String fullTxt = getData(targetUrl);
//        log.info("\n"+fullTxt);
        pageAll = extractPage(fullTxt);
        List listMap = new ArrayList<>();

        List<FinanceMonitorPunish> punishInfos = new ArrayList<FinanceMonitorPunish>();

        listMap =extractUrl(pageAll);
        for(int i =0;i<listMap.size();i++){
            LinkedHashMap lh = (LinkedHashMap)listMap.get(i);
            //解析安徽信息
            if(lh.get("provinceCity").toString().indexOf("安徽")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_AnHui().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析重庆信息
            if(lh.get("provinceCity").toString().indexOf("重庆")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_ChongQing().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析大连信息
            if(lh.get("provinceCity").toString().indexOf("大连")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_DaLian().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析福建信息
            if(lh.get("provinceCity").toString().indexOf("福建")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_FuJian().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析甘肃信息
            if(lh.get("provinceCity").toString().indexOf("甘肃")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_GanSu().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析广东信息
            if(lh.get("provinceCity").toString().indexOf("广东")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_GuangDong().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析广西信息
            if(lh.get("provinceCity").toString().indexOf("广西")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_GuangXi().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析贵州
            if(lh.get("provinceCity").toString().indexOf("贵州")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_GuiZhou().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析海南
            if(lh.get("provinceCity").toString().indexOf("海南")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_HaiNan().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析河南
            if(lh.get("provinceCity").toString().indexOf("河南")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_HeNan().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析湖北
            if(lh.get("provinceCity").toString().indexOf("湖北")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_HuBei().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析江苏
            if(lh.get("provinceCity").toString().indexOf("江苏")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_JiangSu().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析江西
            if(lh.get("provinceCity").toString().indexOf("江西")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_JiangXi().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析宁波信息
            if(lh.get("provinceCity").toString().indexOf("宁波")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_NingBo().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析青海信息
            if(lh.get("provinceCity").toString().indexOf("青海")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_QingHai().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析山东信息
            if(lh.get("provinceCity").toString().indexOf("山东")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_ShanDong().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析上海信息
            if(lh.get("provinceCity").toString().indexOf("上海")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_ShangHai().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析山西信息
            if(lh.get("provinceCity").toString().indexOf("山西")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_ShanXi().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析深圳信息
            if(lh.get("provinceCity").toString().indexOf("深圳")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_ShenZhen().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析四川信息
            if(lh.get("provinceCity").toString().indexOf("四川")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_SiChuan().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析苏州信息
            if(lh.get("provinceCity").toString().indexOf("苏州")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_SuZhou().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析唐山信息
            if(lh.get("provinceCity").toString().indexOf("唐山")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_TangShan().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析温州信息
            if(lh.get("provinceCity").toString().indexOf("温州")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_WenZhou().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析厦门信息
            if(lh.get("provinceCity").toString().indexOf("厦门")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_XiaMen().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析新疆信息
            if(lh.get("provinceCity").toString().indexOf("新疆")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_XinJiang().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析西藏信息
            if(lh.get("provinceCity").toString().indexOf("西藏")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_XiZang().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析烟台信息
            if(lh.get("provinceCity").toString().indexOf("烟台")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_YanTai().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析云南信息
            if(lh.get("provinceCity").toString().indexOf("云南")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_YunNan().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }
            //解析浙江信息
            if(lh.get("provinceCity").toString().indexOf("浙江")>-1){
                Map<String,String> mapInfo = new SiteTaskImpl_BOIS_ZheJiang().extractContent(getData(lh.get("herf").toString()));
                punishInfos.add(getObj(mapInfo,lh.get("herf").toString()));
            }

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
}
