package com.mr.modules.api.site.instance.creditchinasite.hubeisite;

import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @auther 1.信用中国（湖北）
 * 黑名单
 * 2.http://www.hbcredit.gov.cn/credithb/gkgs/listNew.html
 */
@Slf4j
@Component("hubei_blacklist")
@Scope("prototype")
public class Hubei_Blacklist extends SiteTaskExtend_CreditChina {
    String url = "http://www.hbcredit.gov.cn/credithb/gkgs/listNew.html";

    String[] classNames = {"FY", "DS", "GUOS", "HB", "GongS", "SYJ", "SW"};

    private String dUrlPrefix = "http://www.hbcredit.gov.cn";
    private static final Map<String, String> subjects = new HashMap<String, String>() {{
        put("FY", "法院");
        put("DS", "地税");
        put("GUOS", "国税");
        put("HB", "环保");
        put("GongS", "工商");
        put("SYJ", "食药监");
        put("SW", "商务");
    }};

    @Autowired
    AdminPunishMapper adminPunishMapper;

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    @Override
    protected String execute() throws Throwable {
        for (String className : classNames) {
            try {
                extractContext(url, className);
            } catch (Exception e) {
                writeBizErrorLog(url, e.getMessage());
            }
        }
        return null;
    }

    /**
     * 获取网页内容
     * 行政处罚决定书文号、案件名称、处罚类别、处罚事由、处罚依据、行政相对人名称、组织机构代码、工商登记码、税务登记号、
     * 法定代表人居民身份证号、法定代表人姓名、处罚结果、处罚生效期、处罚机关、当前状态、地方编码、备注、信息提供部门、数据报送时间
     */
    public void extractContext(String url, String className) {

        Map<String, String> tMap = new HashMap<>();
        tMap.put("className", String.valueOf(className));
        tMap.put("type", "BlackList");
        String textList = postData(url, tMap, 3);
        if (textList.indexOf("var totalPages =") < 0) {
            return;
        }
        String s1 = textList.substring(textList.indexOf("var totalPages ="))
                .replace("var totalPages =", "");
        int pages = Integer.parseInt(s1.substring(0, s1.indexOf(";")).trim());

        for (int page = 1; page <= pages; page++) {
            Map<String, String> map = new HashMap<>();
            map.put("pageIndex", String.valueOf(page));
            map.put("className", String.valueOf(className));
            map.put("type", "BlackList");
            Document listDoc = Jsoup.parse(postData(url, map, 3));

            if ("SW".equals(className)) {
                // 分类“商务”
                saveSwListInfo(listDoc, url);
            } else {
                // 其它分类
                saveListInfo(listDoc, url, className);
            }
        }
    }

    /**
     * 其它分类的数据("FY", "DS", "GUOS", "HB", "GongS", "SYJ")
     *
     * @param listDoc
     * @param url
     * @param className
     */
    private void saveListInfo(Document listDoc, String url, String className) {
        Element div = listDoc.getElementsByClass("right_xkgs").first();
        Elements aElements = div.getElementsByTag("a");
        Elements textElement = div.getElementsByTag("td");
        List<String> listSubject = new ArrayList<>();
        for (Element element : textElement) {
            if (element.text().contains("涉及事项：")) {
                listSubject.add(element.attr("title"));
            }
        }

        for (int i = 0; i < aElements.size(); i++) {
            String infoUrl = dUrlPrefix + aElements.get(i).attr("href");
            Document infoDoc = Jsoup.parse(getData(infoUrl));
            Elements trElements = infoDoc.getElementsByTag("tr");
            DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
            //"FY", "DS", "GUOS", "HB", "GongS", "SYJ", "SW"
            discreditBlacklist.setSubject(subjects.get(className) + "-" + listSubject.get(i));
            discreditBlacklist.setUrl(infoUrl);



            String objType = "01";
            String entityName = "";
            String entitiCode = "";
            for (int j = 1; j < trElements.size(); j++) {
                Element trElement = trElements.get(j);
                String keyString = trElement.getElementsByTag("td").get(0).text();
                String valueString = trElement.getElementsByTag("td").get(1).text().trim();

                if (keyString.contains("代码类型") && valueString.contains("身份证")) {
                    objType = "02";
                    discreditBlacklist.setObjectType(objType);
                    continue;
                }

                if (keyString.contains("信用主体姓名/名称")) {
                    // discreditBlacklist.setEnterpriseName(valueString);
                    entityName = valueString;
                    continue;
                }

                if (keyString.trim().equals("代码")) {
                    // discreditBlacklist.setEnterpriseCode1(valueString);
                    entitiCode = valueString;
                    continue;
                }

                if (keyString.contains("法定代表人姓名")) {
                    discreditBlacklist.setPersonName(valueString);
                    continue;
                }

                if (keyString.contains("法定代表人证件号码")) {
                    discreditBlacklist.setPersonId(valueString);
                    continue;
                }

                if (keyString.contains("处罚内容")) {
                    if (StrUtil.isNotEmpty(valueString) && valueString.trim().startsWith("<")) {
                        Element document = Jsoup.parse("<html>" + valueString + "</html>");
                        String s = document.text();
                        discreditBlacklist.setPunishResult(s.replace("\t", "").replace("\n", ""));
                    } else {
                        discreditBlacklist.setPunishResult(valueString.replace("\t", "").replace("\n", ""));
                    }
                    continue;
                }

                // if (keyString.contains("来源单位")) {
                // 	adminPunish.setSource(valueString);
                // 	continue;
                // }

                if (keyString.contains("列入日期")) {
                    discreditBlacklist.setPublishDate(valueString);
                    continue;
                }

                if (keyString.contains("认定（决定）机构")) {
                    discreditBlacklist.setJudgeAuth(valueString);
                    continue;
                }

            }

            // 根据 objType 判断数据对应字段
            if ("02".equals(objType)){
                discreditBlacklist.setPersonName(entityName);
                discreditBlacklist.setPersonId(entitiCode);
            }else{
                discreditBlacklist.setEnterpriseName(entityName);
                discreditBlacklist.setEnterpriseCode1(entitiCode);
            }

            try {
                // discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
                saveDisneycreditBlackListOne(discreditBlacklist, false);
            } catch (Exception e) {
                writeBizErrorLog(infoUrl, e.getMessage());
            }
        }
    }

    /**
     * 保存商务分类数据
     *
     * @param listDoc
     * @param url
     */
    private void saveSwListInfo(Document listDoc, String url) {
        Element div = listDoc.getElementsByClass("SWtable").first();
        Elements trs = div.getElementsByTag("tr");
        for (int i = 0; i < trs.size(); i++) {
            Elements tdElements = trs.get(i).getElementsByTag("td");
            if (tdElements.size() == 0){
                continue;
            }
            DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
            discreditBlacklist.setUrl(url);
            discreditBlacklist.setEnterpriseName(tdElements.get(0).attr("title").trim());
            discreditBlacklist.setEnterpriseCode3(tdElements.get(1).text().trim());
            discreditBlacklist.setSubject(subjects.get("SW") + "-" + tdElements.get(2).text().trim());
            // discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl()
            //         + "@" + discreditBlacklist.getEnterpriseName()
            //         + "@" + discreditBlacklist.getPersonName()
            //         + "@" + discreditBlacklist.getJudgeNo()
            //         + "@" + discreditBlacklist.getJudgeAuth()
            // );
            try {
                saveDisneycreditBlackListOne(discreditBlacklist, false);
            } catch (Exception e) {
                writeBizErrorLog(url, e.getMessage());
            }
        }
    }

    private DiscreditBlacklist createDefaultDiscreditBlacklist() {
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();

        discreditBlacklist.setCreatedAt(new Date());
        discreditBlacklist.setUpdatedAt(new Date());
        discreditBlacklist.setSource("信用中国（湖北）");
        discreditBlacklist.setUrl(url);
        discreditBlacklist.setSubject("黑名单");
        discreditBlacklist.setObjectType("01");
        discreditBlacklist.setEnterpriseCode1("");
        discreditBlacklist.setEnterpriseCode2("");
        discreditBlacklist.setEnterpriseCode3("");
        discreditBlacklist.setEnterpriseName("");
        discreditBlacklist.setPersonName("");
        discreditBlacklist.setPersonId("");
        discreditBlacklist.setJudgeNo("");
        discreditBlacklist.setJudgeAuth("");
        return discreditBlacklist;
    }

}
