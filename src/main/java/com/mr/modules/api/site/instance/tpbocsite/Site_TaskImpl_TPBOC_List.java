package com.mr.modules.api.site.instance.tpbocsite;

import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

/**
 * @author zjxu
 * @date 2018-05
 * 获取中国银行业监督管理委员会处罚信息
 */

@Scope("prototype")
@Slf4j
@Component("tpboc")
public class Site_TaskImpl_TPBOC_List extends SiteTaskExtend {
    @Override
    protected String execute() throws Throwable {
        //0.获取保监会处罚列表页码数量
        int pageAll = 1;
        //获取清单列表页数pageAll
//        String fileName = "C:\\Users\\Space\\Desktop\\demo.html";

        String targetUri1 = "http://www.cbrc.gov.cn/chinese/home/docViewPage/110002";
        String fullTxt1 = getData(targetUri1);
        List<List<?>> lists = extractList(fullTxt1);
        //2.获取处罚详情信息
        for(List<?> list : lists) {//其内部实质上还是调用了迭代器遍历方式，这种循环方式还有其他限制，不建议使用。
            for (int i=0;i<list.size();i++){
                String urlStr = list.get(i).toString();
                String[] urlArr = urlStr.split("\\|\\|");
                String id = urlArr[0];
                String url = urlArr[1];
                log.info("excuteOne-----------url:"+url);
                String fileName = urlArr[2];
                //提取正文结构化数据
                Map record = extractContent(getData(url),id,fileName,url);

                try{
                    getObj(record,url);
                }catch (Exception e){
                    writeBizErrorLog(url,"请检查此条url："+"\n"+e.getMessage());
                    continue;
                }
            }
        }
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return null;
    }
    /**
     * 获取中国银行业监督管理委员会处罚列表所有页数
     * @param fullTxt
     * @return
     */
    public int extractPage(String fullTxt){
        //url:http://www.cbrc.gov.cn/chinese/home/docViewPage/110002&current=1
        int pageAll = 1;
        Document doc = Jsoup.parse(fullTxt);
        Elements td = doc.getElementById("testUI").getElementsByTag("td");
        //记录元素的数量
        int serialNo = td.size();
        pageAll = Integer.valueOf(td.get(serialNo-1).text().split("/")[1].replace(" ","").split("首页")[0]);
        log.info("-------------********---------------");
        log.info("处罚列表清单总页数为："+pageAll);
        log.info("-------------********---------------");
        return  pageAll;
    }
    /**
     * 获取保监会处罚里列表清单
     * @param fullTxt
     * @return
     */
    private List<List<?>> extractList(String fullTxt){
        //1.保监会处罚列表清单
        List<List<?>> listList = new ArrayList<>();
        // 使用标识符ok标识，如果解析出的url已经存在库中，就停止继续解析
        ok:for(int i=1;i<=extractPage(fullTxt);i++){
            String targetUri2 = "http://www.cbrc.gov.cn/chinese/home/docViewPage/110002&current="+i;
            String fullTxt2 = getData(targetUri2);
            List<String> list = new ArrayList<>();
            Document doc = Jsoup.parse(fullTxt2);
            Elements span = doc.getElementsByAttributeValue("id","testUI");

            for (Element elementSpan : span){
                Elements elements = elementSpan.getElementsByTag("a");
                for(Element elementA :elements){
                    //抽取编号Id
                    String id = new Date().toString();
                    //抽取连接
                    String href = "http://www.cbrc.gov.cn"+elementA.attr("href");
                    //抽取标题
                    String title = elementA.attr("title").replace("(","（").replace(")","）");
                    //抽取发布的时间
                    //       String extract_Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    Element element_td = elementSpan.parent().nextElementSibling();
                    String extract_Date = "20" + element_td.text().replace("(","").replace(")","");

                    String urlStr = id+"||"+href+"||"+title+"||"+extract_Date;

                    if(Objects.isNull(financeMonitorPunishMapper.selectByUrl(href))){
                        log.info("这是新增的URL："+href);
                        list.add(urlStr);
                    }else{
                        break  ok;
                    }
                }

            }
            listList.add(list);
        }

        return listList;
    }
    private Map extractContent(String fullTxt, String id, String title,String href) throws Throwable{
        Map map = new LinkedHashMap();
        //文件类型
        String fileType = ""; //TODO 对公处罚，个人处罚，处罚情况
        //序号***** TODO 需确认
        String seqNo = "";  //可以提取链接中的ID
        seqNo = id;

        //处罚文号******
        String punishNo = "";//丛链接中提取 TODO 部分在链接中不存在，需要在正文中提取

        //机构当事人名称
        String  orgPerson = "";

        //机构当事人住所
        String  orgAddress = "";

        //机构负责人姓名
        String  orgHolderName = "";

        //当事人
        String priPerson = "";
        //职务
        String  priJob = "";
        //住址
        String priAddress = "";
        //证件号码
        String priCert ="";
        //违规情况，事由
        String  irregularities ="";
        //相关法律 RELATED_LAW
        String relatedLaw = "";
        //处罚结果 PUNISH_RESULT
        String punishResult ="";
        //发布机构******
        String releaseOrg = "中国银行保险监督管理委员会";   //链接中提取

        //发布日期******
        String releaseDate = "";//链接中提取 TODO 链接中的时间格式不全，需要在正文中提取

        //行政处罚详情
        String punishDetail = "";

        //处罚机关******	TODO 需确认
        String punishOrg = "";
        punishOrg = "中国银行保险监督管理委员会";  //TODO 可以从正文中提取

        //处罚日期***** TODO 可以中正文中提取，但是格式非阿拉伯数字类型
        String punishDate = "";
        //数据来源  TODO 来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）
        String source = "银保监";
        //主题 TODO 主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录
        String object = "行政处罚决定";
        //获取正文内容
        Document doc = Jsoup.parse(new File(fullTxt),"utf-8");

        //获取正文主节点
        punishDetail  = doc.getElementsByClass(" f12c").text();
        //获取表格内容
        Elements elementsTxt = doc.getElementsByClass("MsoNormalTable");
       /*1.提取发布时间*/
        releaseDate = doc.getElementById("docTitle").text()
                .replace(" ","").replace(":","：").split("发布时间：")[1].split("文章来源")[0];

        Elements elementsTr = elementsTxt.select("tr");
        if(elementsTxt.text().contains("行政处罚决定书文号")&&elementsTr.text().contains("被处罚当事人姓名或名称")){
            punishNo =elementsTr.get(0).select("td").get(1).text().replace(" ","");
            priPerson=elementsTr.get(1).select("td").get(2).text().replace(" ","");
            orgPerson = elementsTr.get(2).select("td").get(2).text().replace(" ","");
            orgHolderName = elementsTr.get(3).select("td").get(1).text().replace(" ","");
            irregularities = elementsTr.get(4).select("td").get(1).text().replace(" ","");
            relatedLaw = elementsTr.get(5).select("td").get(1).text().replace(" ","");
            punishResult = elementsTr.get(6).select("td").get(1).text().replace(" ","");
            punishOrg = elementsTr.get(7).select("td").get(1).text().replace(" ","");
            punishDate = elementsTr.get(8).select("td").get(1).text().replace(" ","");

        }
        log.info("punishNo:"+punishNo);
        log.info("priPerson:"+priPerson);
        log.info("orgPerson:"+orgPerson);
        log.info("orgHolderName:"+orgHolderName);
        log.info("irregularities:"+irregularities);
        log.info("relatedLaw:"+relatedLaw);
        log.info("punishResult:"+punishResult);
        log.info("punishOrg:"+punishOrg);
        log.info("punishDate:"+punishDate);
        log.info("releaseDate:"+releaseDate);


        map.put("punishNo",punishNo);
        map.put("title",title);
        map.put("releaseOrg",releaseOrg);
        map.put("releaseDate",releaseDate);
        map.put("punishOrg",punishOrg);
        map.put("punishDate",punishDate);
        map.put("orgPerson",orgPerson);
        map.put("orgAddress",orgAddress);
        map.put("orgHolderName",orgHolderName);
        map.put("irregularities",irregularities);
        map.put("relatedLaw",relatedLaw);
        map.put("punishResult",punishResult);
        map.put("priPerson",priPerson);
        map.put("priCert",priCert);
        map.put("priJob",priJob);
        map.put("priAddress",priAddress);
        map.put("stringBufferDetail",punishDetail);

        map.put("href",href);
        map.put("source",source);
        map.put("object",object);
        return  map;
    }
    /**
     * 获取Obj,并入库
     * */
    public FinanceMonitorPunish getObj(Map<String,String> mapInfo, String href){

        FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
        financeMonitorPunish.setPunishNo(mapInfo.get("punishNo"));//处罚文号
        financeMonitorPunish.setPunishTitle(mapInfo.get("title"));//标题
        financeMonitorPunish.setPublisher(mapInfo.get("releaseOrg"));//发布机构
        financeMonitorPunish.setPublishDate(mapInfo.get("releaseDate"));//发布时间
        financeMonitorPunish.setPunishInstitution(mapInfo.get("punishOrg"));//处罚机关
        financeMonitorPunish.setPunishDate(mapInfo.get("punishDate"));//处罚时间
        financeMonitorPunish.setPartyInstitution(mapInfo.get("orgPerson"));//当事人（公司）=处罚对象
        financeMonitorPunish.setCompanyFullName(mapInfo.get("orgPerson"));//当时人（公司）全称
        financeMonitorPunish.setDomicile(mapInfo.get("orgAddress"));//机构住址
        financeMonitorPunish.setLegalRepresentative(mapInfo.get("orgHolderName"));//机构负责人
        financeMonitorPunish.setIrregularities(mapInfo.get("irregularities"));//处理事由
        financeMonitorPunish.setRelatedLaw(mapInfo.get("relatedLaw"));//违反条例
        financeMonitorPunish.setPunishResult(mapInfo.get("punishResult"));//处罚结果
        financeMonitorPunish.setPartyPerson(mapInfo.get("priPerson"));//受处罚人
        financeMonitorPunish.setPartyPersonId(mapInfo.get("priCert"));//受处罚人证件号码
        financeMonitorPunish.setPartyPersonTitle(mapInfo.get("priJob"));//职务
        financeMonitorPunish.setPartyPersonDomi(mapInfo.get("priAddress"));//自然人住址
        financeMonitorPunish.setDetails(mapInfo.get("stringBufferDetail"));//详情
        financeMonitorPunish.setUrl(href);
        financeMonitorPunish.setSource(mapInfo.get("source"));
        financeMonitorPunish.setObject(mapInfo.get("object"));
        log.info("url:"+href);
        //保存入库
        saveOne(financeMonitorPunish,false);

        return financeMonitorPunish;
    }
    /**
     * 设置代理
     * @param url
     * @param host
     * @param port
     * @return
     */
    protected String getDataProxy(String url,String host,String port){
        initProxyIP(host, port);
        String str = getData( url);
        return  str;
    }
    /**
     * 设置代理IP
     * @param host
     * @param port
     */
    public void initProxyIP(String host,String port){
        int portNr = -1;
        try {
            portNr = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            log.error("Unable to parse the proxy port number");
        }
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        InetSocketAddress address = new InetSocketAddress(host,portNr);
        Proxy proxy = new Proxy(Proxy.Type.HTTP,address);
        factory.setProxy(proxy);

        restTemplate.setRequestFactory(factory);
    }

    /**
     * 文件转字符串
     * @param fileName
     * @return
     */
    public String fileNameToString(String fileName){
        File myFile=new File(fileName);
        String str ="";
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(myFile));

            while ((str = in.readLine()) != null)
            {
                System.out.println(str);
            }
            in.close();
        }
        catch (IOException e)
        {
            e.getStackTrace();
        }
        return str;
    }
}
