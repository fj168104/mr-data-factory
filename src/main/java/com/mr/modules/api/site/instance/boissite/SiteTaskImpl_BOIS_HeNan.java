package com.mr.modules.api.site.instance.boissite;

import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtendSub;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component("bois_henan")
@Scope("prototype")
public class SiteTaskImpl_BOIS_HeNan extends SiteTaskExtendSub {
    /**
     * 获取：全量、增量
     * 通过发布时间：yyyy-mm-dd格式进行增量处理
     * 注：请求参数：publishDate
     */
    @Override
    protected String execute() throws Throwable {
//        String url = "http://anhui.circ.gov.cn/web/site11/tab3388/module8940/page1.htm";
        List<String> urlList = extractPageUrlList();
        for(String urlResult : urlList){
            log.info("urlResult:"+urlResult);
            Map map = extractContent(getData(urlResult));
            try{
                getObj(map,urlResult);
            }catch (Exception e){
                writeBizErrorLog(urlResult,"请检查此条url："+"\n"+e.getMessage());
                continue;
            }
        }
        return null;
    }
    /**
     * 获取：单笔
     * 注：请求参数传入：url
     */
    @Override
    protected String executeOne() throws Throwable {
        if(oneFinanceMonitorPunish.getUrl()!=null){
            log.info("oneUrl:"+oneFinanceMonitorPunish.getUrl());
            Map map = extractContent(getData(oneFinanceMonitorPunish.getUrl()));

            try{
                getObj(map,oneFinanceMonitorPunish.getUrl());
            }catch (Exception e){
                writeBizErrorLog(oneFinanceMonitorPunish.getUrl(),"请检查此条url："+"\n"+e.getMessage());
            }
        }
        if(oneFinanceMonitorPunish.getPublishDate()!=null){
            List<String> urlList = extractPageUrlListAdd(oneFinanceMonitorPunish.getPublishDate());
            for(String urlResult : urlList){
                log.info("urlResult:"+urlResult);
                Map map = extractContent(getData(urlResult));
                try{
                    getObj(map,urlResult);
                }catch (Exception e){
                    writeBizErrorLog(urlResult,"请检查此条url："+"\n"+e.getMessage());
                    continue;
                }
            }
        }
        return null;
    }

    /**  xtractPageAll,URl集合
     * @return*/

    public List extractPageUrlList(){
        List<String> urlList = new ArrayList<>();
        //第一个页面，用于获取总页数
        String baseUrl = "http://henan.circ.gov.cn/web/site10/tab3426/module9897/page2.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        ok:for(int i=1;i<=pageAll;i++){
            String url ="http://henan.circ.gov.cn/web/site10/tab3426/module9897/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                String resultUrl = "http://henan.circ.gov.cn"+elementUrl.attr("href");
                log.info("编号："+i+"==resultUrl:"+resultUrl);
                if(Objects.isNull(financeMonitorPunishMapper.selectByUrl(resultUrl))){
                    urlList.add(resultUrl);
                }else{
                    break ok;
                }
            }
        }
        return urlList;
    }
    /**  xtractPageAll,URl集合
     * @return*/

    public List extractPageUrlListAdd(String date)throws Throwable{
        List<String> urlList = new ArrayList<>();
        //第一个页面，用于获取总页数
        String baseUrl = "http://henan.circ.gov.cn/web/site10/tab3426/module9897/page2.htm";
        //解析第一个页面，获取这个页面上下文
        String fullTxt = getData(baseUrl);
        //获取页数
        int  pageAll= extractPage(fullTxt);
        ok:for(int i=1;i<=pageAll;i++){
            String url ="http://henan.circ.gov.cn/web/site10/tab3426/module9897/page"+i+".htm";
            String resultTxt = getData(url);
            Document doc = Jsoup.parse(resultTxt);
            Elements elementsHerf = doc.getElementsByClass("hui14");
            for(Element element : elementsHerf){
                //发布时间
                Element element_td = element.nextElementSibling();
                String extract_Date = "20" + element_td.text().replace("(","").replace(")","");
                if(new SimpleDateFormat("yyyy-MM-dd").parse(extract_Date).compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(date))>=0){
                    Element elementUrl = element.getElementById("hui1").getElementsByTag("A").get(0);
                    String resultUrl = "http://henan.circ.gov.cn"+elementUrl.attr("href");
                    log.info("编号："+i+"==resultUrl:"+resultUrl);
                    if(Objects.isNull(financeMonitorPunishMapper.selectByUrl(resultUrl))){
                        urlList.add(resultUrl);
                    }else{
                        break ok;
                    }
                }
            }
        }
        return urlList;
    }
    /** 获取保监会处罚列表所有页数
     * @param fullTxt
     * @return*/

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

    public Map extractContent(String fullTxt) {
        //发布机构
        String publishOrg = "中国保监会河南保监局行政处";
        //发布时间
        String publishDate = "";
        //TODO 处罚机关（由于有些页面没有，所以暂且给予默认值）
        String punishOrg = "河南保监局";
        //TODO 处罚时间
        String punishDate = "";
        //TODO 处罚文号
        String punishNo = "";
        //TODO 受处罚机构
        StringBuffer punishToOrg = new StringBuffer();
        //TODO 受处罚机构地址
        StringBuffer punishToOrgAddress = new StringBuffer();
        //TODO 法定代表人或主要负责人
        StringBuffer punishToOrgHolder = new StringBuffer();
        //TODO 受处罚当时人名称（自然人）
        StringBuffer priPerson = new StringBuffer();
        //TODO 受处罚当时人证件号码（自然人）
        StringBuffer priPersonCert = new StringBuffer();
        //TODO 受处罚当时人职位（自然人）
        StringBuffer priJob = new StringBuffer();
        //TODO 受处罚当时人地址（自然人）
        StringBuffer priAddress = new StringBuffer();
        //TODO 判断处罚的是法人，还是自然人
        String priBusiType = "";
        String stringDetail = "";
        //数据来源  TODO 来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）
        String source = "保监局";
        //主题 TODO 主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录
        String object = "行政处罚决定";
        String titleStr = "";

        Document doc = Jsoup.parse(fullTxt.replace("、","，")
                .replace("(","（")
                .replace(")","）")
                .replace(":","：")
                .replace("&nbps;","")
                .replace(" ","")
        );
        Element elementsTxt = doc.getElementById("tab_content");

        Elements elementsTD = elementsTxt.getElementsByTag("TD");
        Elements elementsSpan = elementsTxt.getElementsByClass("xilanwb");
        stringDetail =elementsSpan.text();
        /*TODO 通用型*/
        //TODO 提取主题
        Element elementsTitle = elementsTD.first();
        titleStr = elementsTitle.text();
        //TODO 获取包含发布时间的元素
        Element elementsPublishDate = elementsTD.get(1);
        String publishDateStr = elementsPublishDate.text();
        publishDate = publishDateStr.substring(publishDateStr.indexOf("发布时间：")+5,publishDateStr.indexOf("分享到："));

        //全文提取
        String txtAll = elementsTxt.text()
                .replace("(","（")
                .replace(")","）")
                .replace("开户名称：","户名：")
                .replace("账户名称：","户名：")
                .replace("住    所：","，地址：")
                .replace("住    所：","，地址：")
                .replace("住      所：","，地址：")
                .replaceAll("住所： ","，地址：")
                .replace("地  址：","，地址：")
                .replaceAll("地 址: ","，地址：")
                .replace("当 事 人： 　　姓 名： ","，当事人：")
                .replace("当事人： 　　名称： ","，当事人：")
                .replace("名 称：","，当事人：")
                .replace("名称：","，当事人：")
                .replace("姓   名： ","，当事人：")
                .replace("姓名：","，当事人：")
                .replace("当事人： ","，当事人：")
                .replace("当  事  人：","，当事人：")
                .replace("当  事  人：","，当事人：")
                .replace("当 事 人：","，当事人：")
                .replace("负责人： ","，负责人：")
                .replace("负 责 人：","，负责人：")
                .replace("负 责 人：","，负责人：")
                .replace("负  责  人：","，负责人：")
                .replace("负  责  人：","，负责人：")
                .replace("负  责  人： ","，负责人：")
                .replace("地址： ","，地址：")
                .replace("证件号码： ","，证件号码：")
                .replace("身份证号：","，证件号码：")
                .replace("职务： ","，职务：")
                .replace("职   务：","，职务：")
                .replace("住所：","，地址：")
                .replace("住      所：","，地址：")
                .replace("住     所：","，地址：")
                .replace("住 所：","，地址：")
                .replace("地，址：，","，地址：")
                .replace("当  事  人：","，当事人：")
                .replace("当 事 人：","，当事人：")
                .replace("姓名：","，当事人：")
                .replace("受处罚人：","，当事人：")
                .replace("工作单位：","，当事人：")
                .replace("所在单位：","，当事人：")
                .replace("受处罚机构：","，当事人：")
                .replace("受处罚单位：","，当事人：")
                .replace("法定代表人（主要负责人）： ","，负责人：")
                .replace("受处罚人姓名：","，当事人：")
                .replace("受处罚人名称：","，当事人：")
                .replace("受处罚机构名称：","，当事人：")
                .replace("负  责  人：","，负责人：")
                .replace("处罚人姓名：","，当事人：")
                .replace("法定代表人姓名：","，负责人：")
                .replace("主要负责人姓名：","，负责人：")
                .replace("法定代表人或负责人：","，负责人：")
                .replace("法定代表人或主要负责人：","，负责人：")
                .replace("法定代表人或主要负责人姓名：","，负责人：")
                .replace("负责人：，","，负责人：")
                .replace("职     务：","，职务：")
                .replace("身份证号码：","，证件号码：")
                .replace("身份证号码","，证件号码")
                .replace("地  址：","，地址：")
                .replace("住      所：","，地址：")
                .replace("单位地址：","，地址：")
                .replace("住址：","，地址：")
                .replace("经查，","，经查")
                .replace(" ","，")
                .replace("。","，")
                .replace(" ","，")
                .replace("　","，")
                .replace("当，事人：","，当事人：")
                .replace("职，务：","，职务：")
                .replace("名，称：","，当事人：")
                .replace("姓，名：","，当事人：")
                .replace("当事人：，","，当事人：")
                .replace("当事人：姓名","，当事人：")
                .replace("住，所：","，地址：")
                .replace(" 号","号")
                .replace("姓，，，，名：，","，当事人：")
                .replace("名，，称：","，当事人：")
                .replace("职，，，，务：，","，职务：")
                .replace("住，，所：，","，地址：")
                .replace("王，磊","王磊")
                .replace("褚，红","褚红")
                .replace("乔，萍","乔萍")
                .replace("肖，宏","肖宏")
                .replace("马，琴","马琴")
                .replace("袁，琳","袁琳")
                .replace("张，倩","张倩")
                .replace("耿，超","耿超")
                .replace("李，霞","李霞")
                .replace("]，","]")
                .replace("〕，","〕")
                ;
        log.info("txtAll:"+txtAll);
        String[] txtAllArr = txtAll.split("，");

        //判断是法人还是自然人true为自然人，false为法人
        boolean personFlag = true;
        if(txtAll.contains("当事人：")){
            for(String arrStr : txtAllArr){
                String[] str = arrStr.split("：");
                if(arrStr.contains("当事人：")&&str.length>=2){
                    if(str[1].length()<6){
                        //TODO 受处罚当时人名称（自然人）
                        priPerson.append(str[1]).append("，");
                        //TODO 判断处罚的是法人，还是自然人
                        personFlag=true;
                    }else{
                        //TODO 受处罚机构
                        punishToOrg.append(str[1]).append("，");
                        //TODO 判断处罚的是法人，还是自然人
                        personFlag=false;
                    }
                }
                if(personFlag==false&&arrStr.contains("地址：")&&str.length>=2){
                    //TODO 受处罚机构地址
                    punishToOrgAddress.append(str[1]).append("，");
                }
                if(personFlag==false&&arrStr.contains("负责人：")&&str.length>=2){
                    //TODO 法定代表人或主要负责人
                    punishToOrgHolder.append(str[1]).append("，");
                }

                if(personFlag==true&&arrStr.contains("证件号码：")&&str.length>=2){
                    //TODO 受处罚当时人证件号码（自然人）
                    priPersonCert.append(str[1]).append("，");
                }
                if(personFlag==true&&arrStr.contains("职务：")&&str.length>=2){
                    //TODO 受处罚当时人职位（自然人）
                    priJob.append(str[1]).append("，");
                }
                if(personFlag==true&&arrStr.contains("地址：")&&str.length>=2){
                    //TODO 受处罚当时人地址（自然人）
                    priAddress.append(str[1]).append("，");
                }

                if(arrStr.contains("年")&&arrStr.endsWith("日")){
                    //TODO 处罚时间
                    punishDate=arrStr.trim();
                }
                if(arrStr.contains("保监罚")&&arrStr.endsWith("号")){
                    //TODO 处罚文号
                    punishNo=arrStr.trim();
                }
            }
            if(punishOrg.equals("")){
                punishOrg ="豫保监局";
            }
        }
        if(txtAll.contains("豫保监罚〔2007〕4号")){
            punishNo ="豫保监罚〔2007〕4号";
            punishToOrg.append("中国人民财产保险股份有限公司安阳市分公司");
            punishToOrgHolder.append("杨玉祥");
            punishDate="二○○七年三月六日";
            priPerson.append("杨玉祥").append("，").append("杨军生");
            priJob.append("总经理").append("，").append("副总经理");
        }
        if(txtAll.contains("豫保监罚〔2007〕1号")){
            punishNo ="豫保监罚〔2007〕1号";
            punishToOrg.append("中华联合财产保险公司漯河中心支公司舞阳营销服务部");
            punishDate="二○○七年一月八日";
        }
        if((punishNo.equals("")||punishNo.equals("null")||punishNo.equals("NULL")||punishNo==null)&&titleStr.contains("保监罚")&&titleStr.contains("号")){
            //TODO 处罚文号
            punishNo=titleStr.substring(titleStr.indexOf("保监罚")-1   ,titleStr.indexOf("号")+1);
        }
        if(punishNo.equals("")||punishNo.equals("null")||punishNo.equals("NULL")||punishNo==null){
            punishNo = "无文号"+new Date().getTime();
        }
        /*log.info("发布主题：" + titleStr);
        log.info("发布机构：" + publishOrg);
        log.info("发布时间：" + publishDate);
        log.info("处罚机关：" + punishOrg);
        log.info("处罚时间：" + punishDate);
        log.info("处罚文号：" + punishNo);
        log.info("受处罚机构：" + punishToOrg);
        log.info("受处罚机构地址：" + punishToOrgAddress);
        log.info("受处罚机构负责人：" + punishToOrgHolder);
        log.info("受处罚人：" + priPerson);
        log.info("受处罚人证件：" + priPersonCert);
        log.info("受处罚人职位：" + priJob);
        log.info("受处罚人地址：" + priAddress);
        log.info("来源："+source);
        log.info("主题："+object);
        log.info("正文：" + stringDetail);*/

        Map<String,String> map = new HashMap<String,String>();
        map.put("titleStr",titleStr);
        map.put("publishOrg",publishOrg);
        map.put("publishDate",publishDate);
        map.put("punishOrg",punishOrg);
        map.put("punishDate",punishDate);
        map.put("punishNo",punishNo);
        map.put("punishToOrg",punishToOrg.toString());
        map.put("punishToOrgAddress",punishToOrgAddress.toString());
        map.put("punishToOrgHolder",punishToOrgHolder.toString());
        map.put("priPerson",priPerson.toString());
        map.put("priPersonCert",priPersonCert.toString());
        map.put("priJob",priJob.toString());
        map.put("priAddress",priAddress.toString());
        map.put("source",source);
        map.put("object",object);
        map.put("stringDetail",stringDetail);

        return map;
    }
    /**
     * 获取Obj,并入库
     * */
    public FinanceMonitorPunish getObj(Map<String,String> mapInfo, String href){

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
        financeMonitorPunish.setSource(mapInfo.get("source"));
        financeMonitorPunish.setObject(mapInfo.get("object"));

        //保存入库
        saveOne(financeMonitorPunish,false);

        return financeMonitorPunish;
    }
}
