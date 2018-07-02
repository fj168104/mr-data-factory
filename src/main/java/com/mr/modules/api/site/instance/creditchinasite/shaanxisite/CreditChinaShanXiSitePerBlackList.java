package com.mr.modules.api.site.instance.creditchinasite.shaanxisite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.mr.modules.api.SiteParams;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
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
 * @Auther zjxu
 * @DateTime 2018-06
 * 1.主题：黑名单-自然人
 * 2.属性：姓名、身份证号、失信领域、列入文号、列入事由、列入日期、认定机关、公布期限
 *注：此界面有反扒机制，页面最多展示50条记录，建议精确查询
 */
@Slf4j
@Component("creditchinashanxisiteperblacklist")
@Scope("prototype")
public class CreditChinaShanXiSitePerBlackList extends SiteTaskExtend_CreditChina{
    //输入关键字
    String keyWord = "";
    //对象类型 ztType=1 对公；ztType=3 自然人
    String ztType = "3";
    //记录编号ID
    String id = "";
    //行业类型
    String sxly = "[216,999,21,1,2,3,4,5,6,7,8,214,215,213,201,202,203,204,205,206,207,208,209,210,211,212]";
    //列表清单
    /*[{"value":"215","label":"石油天然气行业"},{"value":"214","label":"保险领域"},{"value":"213","label":"电子认证服务行业"},{"value":"201","label":"盐行业"},{"value":"202","label":"电力行业"},{"value":"203","label":"涉金融领域"},{"value":"204","label":"海关领域"},{"value":"205","label":"统计领域"},{"value":"206","label":"财政性资金管理使用领域"},{"value":"207","label":"食品药品监管领域"},{"value":"208","label":"农资领域"},{"value":"209","label":"超限超载运输"},{"value":"210","label":"电子商务及分享经济领域炒信行为相关失信主体"},{"value":"211","label":"质量安全领域"},{"value":"212","label":"环境保护领域"},{"value":"1","label":"工商监管领域"},{"value":"2","label":"税收征管领域"},{"value":"5","label":"上市公司"},{"value":"6","label":"失信被执行人"},{"value":"7","label":"安全生产领域"},{"value":"20","label":"劳动保障黑名单"},{"value":"8","label":"房地产领域"},{"value":"216","label":"运输物流行业"},{"value":"999","label":"未分类"},{"value":"21","label":"公安领域"}]*/
    //1.获取列表清单
    //String urlList = "http://www1.sxcredit.gov.cn/queryCj.jspx?ztType=1&lb=black&keyWord1=&sxly=[216,999,21,1,2,3,4,5,6,7,8,214,215,213,201,202,203,204,205,206,207,208,209,210,211,212]";//ztType=1 对公；ztType=3 自然人
    //2.通过行业字典，遍历查询 type 执行类型 blackinfosxly（黑名单）
    //String urlDic = "http://www1.sxcredit.gov.cn/queryDict.jspx?ztType=3&type=blackinfosxly";//ztType=1 对公；ztType=3 自然人
    //结果明细
    //String urlResult = "http://www1.sxcredit.gov.cn/queryItemData.jspx?id=d799e2aafd8b44dcec3bf0f5dfbbae53&lb=black";

    @Autowired
    DiscreditBlacklistMapper discreditBlacklistMapper;
    @Override
    protected String execute() throws Throwable {
        keyWord = SiteParams.map.get("keyWord");
        SiteParams.map.clear();
        log.info("************************keyWord*********************"+keyWord);
        rusult(keyWord);
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }


    /**
     * 获取正文
     * @param url 需要处理的url
     */
    public String webContent(String url ){
        String jsonResult = "";
        WebClient wc = createWebClient("","");
        try {
            //获取处罚的列表清单的名称
            WebRequest request = new WebRequest(new URL(url), HttpMethod.POST);
            Map<String, String> additionalHeaders = new HashMap<String, String>();
            additionalHeaders.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
            additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8");
            additionalHeaders.put("Accept", "application/json;charset=UTF-8, text/javascript, */*; q=0.01");
            additionalHeaders.put("X-Requested-With","XMLHttpRequest");
            request.setAdditionalHeaders(additionalHeaders);
            // 获取某网站页面

            Page page = wc.getPage(request);
            WebResponse webResponse = page.getWebResponse();
            int status = webResponse.getStatusCode();
            if(status==200){
                jsonResult =  new String(webResponse.getContentAsString().getBytes("iso-8859-1"),"UTF-8");
            }else{
                log.info("没有正常返回，请君监察监察···返回码为："+status);
            }
            wc.close();
        }catch (IOException e){
            log.error("IO处理异常，请注意查看···"+e.getMessage());
        }
        return jsonResult;
    }

    /**
     * 获取源数据详情
     * @param keyWord 接口需要传入的关键字
     */
    public void rusult(String keyWord)throws Throwable{
        //1.获取列表清单
        keyWord = URLEncoder.encode(keyWord, "utf-8");
        String urlList = "http://www1.sxcredit.gov.cn/queryCj.jspx?ztType=3&lb=black&keyWord1="+keyWord+"&sxly=[216,999,21,1,2,3,4,5,6,7,8,214,215,213,201,202,203,204,205,206,207,208,209,210,211,212]";//ztType=1 对公；ztType=3 自然人
        //2.通过行业字典，遍历查询 type 执行类型 blackinfosxly（黑名单）
        //String urlDic = "http://www1.sxcredit.gov.cn/queryDict.jspx?ztType=3&type=blackinfosxly";//ztType=1 对公；ztType=3 自然人
        //sxly=[216,999,21,1,2,3,4,5,6,7,8,214,215,213,201,202,203,204,205,206,207,208,209,210,211,212]"
        //3.结果明细 如：id=d799e2aafd8b44dcec3bf0f5dfbbae53

        ObjectMapper om = new ObjectMapper();
        String jsonList =  webContent(urlList);

        try {
            Map mapJson = om.readValue(jsonList, Map.class);
            List<Map> resultListMap = (List)mapJson.get("list");
            for(Map mapId : resultListMap){
                id = (String)mapId.get("id");
                String urlResult = "http://www1.sxcredit.gov.cn/queryItemData.jspx?id="+id+"&lb=black";
                String jsonResult = webContent(urlResult);
                Map result = om.readValue(jsonResult,Map.class);
                Map detailMap = (Map)result.get("dataList");
                detailMap.put("sourceUrl",urlResult);
                log.info("========================detailMap========================"+detailMap);
                discreditBlacklistInsert(detailMap);
                /*{
                    "zh_name": "法人或其他组织名称",
                        "en_name": "xy010101"
                },
                {
                    "zh_name": "统一社会信用代码",
                        "en_name": "xy010133"
                },
                {
                    "zh_name": "法定代表人姓名",
                        "en_name": "xy010105"
                },
                {
                    "zh_name": "法定代表人身份证号码",
                        "en_name": "fddbrsfzh"
                },
                {
                    "zh_name": "失信领域",
                        "en_name": "sxly_label"
                },
                {
                    "zh_name": "列入文号",
                        "en_name": "lrwh"
                },
                {
                    "zh_name": "列入事由",
                        "en_name": "lryy"
                },
                {
                    "zh_name": "列入日期",
                        "en_name": "lrdate"
                },
                {
                    "zh_name": "认定机关",
                        "en_name": "jdjg"
                },
                {
                    "zh_name": "公布期限",
                        "en_name": "lrqx"
                },
                {
                    "zh_name": "移出日期",
                        "en_name": "ycdate"
                },
                {
                    "zh_name": "信息提供部门 ",
                        "en_name": "dep_name"
                },
                {
                    "zh_name": "信息报送日期",
                        "en_name": "create_date"
                },
                {
                    "zh_name": "最后一次修改日期",
                        "en_name": "mondify_date"
                }*/
            }

        }catch (IOException e){
            log.error("IO处理异常，请注意查看···"+e.getMessage());
        }
    }
    public DiscreditBlacklist discreditBlacklistInsert(Map<String,String> map){
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();
        //created_at	本条记录创建时间
        discreditBlacklist.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        discreditBlacklist.setUpdatedAt(new Date());
        //source	数据来源
        discreditBlacklist.setSource("信用中国(陕西)");
        //subject	主题
        discreditBlacklist.setSubject("黑名单-自然人");
        //url	url
        discreditBlacklist.setUrl(map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        discreditBlacklist.setObjectType("02");
        //enterprise_name	企业名称
        discreditBlacklist.setEnterpriseName("");
        //enterprise_code1	统一社会信用代码
        discreditBlacklist.setEnterpriseCode1("");
        //enterprise_code2	营业执照注册号
        discreditBlacklist.setEnterpriseCode2("");
        //enterprise_code3	组织机构代码
        discreditBlacklist.setEnterpriseCode3("");
        //person_name	法定代表人/负责人姓名|负责人姓名
        discreditBlacklist.setPersonName(map.get("xy010101"));
        //person_id	法定代表人身份证号|负责人身份证号
        discreditBlacklist.setPersonId(map.get("xy010133"));
        //discredit_type	失信类型
        discreditBlacklist.setDiscreditType("");
        //discredit_action	失信行为
        discreditBlacklist.setDiscreditAction(map.get("lryy"));
        //punish_reason	列入原因
        discreditBlacklist.setPunishReason(map.get("lryy"));
        //punish_result	处罚结果
        discreditBlacklist.setPunishResult(map.get("sxly_label"));
        //judge_no	执行文号
        discreditBlacklist.setJudgeNo(map.get("lrwh"));
        //judge_date	执行时间
        discreditBlacklist.setJudgeDate(map.get("lrdate"));
        //judge_auth	判决机关
        discreditBlacklist.setJudgeAuth(map.get("jdjg"));
        //publish_date	发布日期
        discreditBlacklist.setPublishDate(map.get("create_date"));
        //status	当前状态
        discreditBlacklist.setStatus("");
        saveDisneycreditBlackListOne(discreditBlacklist,false);
        return discreditBlacklist;
    }

}
