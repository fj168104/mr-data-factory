package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Auther zjxu
 * @DateTime 2018-07
 * 主题：注销消毒产品生产企业卫生许可证的通告
 * 来源：信用中国（甘肃）
 * 属性：发布时间、发布机关、法律依据、生产企业卫生许可证号、许可项目、企业名称、法定代表人/负责、地址、有效期限、注销原因
 */
@Scope("prototype")
@Component("creditchina_gansu_blacklist_zxxdcp")
@Slf4j
public class CreditChina_GanSu_BlackList_ZXXDCP extends SiteTaskExtend_CreditChina{
    @Override
    protected String execute() throws Throwable {
        webContext();
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    private void webContext(){

        List<String> filedList = new ArrayList();
        String publishDate = "2017/12/14";
        String judgeAuth = "兰州市卫生和计划生育委员会";
        String discreditAction = "消毒产品生产企业存在卫生许可证有效期届满未申请延续、原生产场地已不存在等问题根据《中华人民共和国行政许可法》《卫生行政许可管理办法》和《消毒管理办法》";
        String enterpriseName = "";
        String personName = "";
        String punishReason = "";
        String sourceUrl = "http://www.gscredit.gov.cn/shiXin/99197.jhtml";
        String objectType = "01";
        filedList.add("兰州奇浩纸业有限公司@苟伍代@卫生许可证有效期满未申请延续");
        filedList.add("兰州双文工贸有限公司@孙风阁@卫生许可证有效期满未申请延续");
        filedList.add("阡陌惠纸品加工厂@张俊杰@卫生许可证有效期满未申请延续");
        filedList.add("兰州国兴纸业有限公司@王兴闪@卫生许可证有效期满未申请延续，原生产场地已经不在");
        filedList.add("兰州鼎东纸业有限公司@高向东@卫生许可证有效期满未申请延续");
        filedList.add("城关区伏龙坪诗芬兰纸品加工厂@马淑兰@卫生许可证有效期满未申请延续");
        for(String filed : filedList){
            String[] strings = filed.split("@");
            enterpriseName = strings[0];
            personName = strings[1];
            punishReason = strings[2];
            Map<String,String> map = new HashMap();
            map.put("publishDate",publishDate);
            map.put("judgeAuth",judgeAuth);
            map.put("discreditAction",discreditAction);
            map.put("sourceUrl",sourceUrl);
            map.put("objectType",objectType);
            map.put("enterpriseName",enterpriseName);
            map.put("personName",personName);
            map.put("punishReason",punishReason);

            insertDiscreditBlacklist(map);
        }
    }

    public DiscreditBlacklist insertDiscreditBlacklist(Map map){
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();
        /**
         * 本条记录创建时间
         */
        //@Column(name = "created_at")
        discreditBlacklist.setCreatedAt(new Date());

        /**
         * 本条记录最后更新时间
         */
        //@Column(name = "updated_at")
        discreditBlacklist.setUpdatedAt(new Date());

        /**
         * 数据来源
         */
        discreditBlacklist.setSource("信用中国（甘肃）");
        /**
         * 主题
         */
        discreditBlacklist.setSubject("注销消毒产品生产企业卫生许可证的通告");
        /**
         * url
         */
        discreditBlacklist.setUrl(map.get("sourceUrl")==null?"":map.get("sourceUrl").toString());
        /**
         * 主体类型: 01-企业 02-个人
         */
        //@Column(name = "object_type")
        discreditBlacklist.setObjectType(map.get("objectType")==null?"":map.get("objectType").toString());

        /**
         * 企业名称
         */
        //@Column(name = "enterprise_name")
        discreditBlacklist.setEnterpriseName(map.get("enterpriseName")==null?"":map.get("enterpriseName").toString());

        /**
         * 统一社会信用代码
         */
        //@Column(name = "enterprise_code1")
        discreditBlacklist.setEnterpriseCode1(map.get("enterpriseCode1")==null?"":map.get("enterpriseCode1").toString());
        /**
         * 营业执照注册号
         */
        //@Column(name = "enterprise_code2")
        discreditBlacklist.setEnterpriseCode2(map.get("enterpriseCode2")==null?"":map.get("enterpriseCode2").toString());

        /**
         * 组织机构代码
         */
        //@Column(name = "enterprise_code3")
        discreditBlacklist.setEnterpriseCode3(map.get("enterpriseCode3")==null?"":map.get("enterpriseCode3").toString());
        /**
         * 法定代表人/负责人姓名|负责人姓名
         */
        //@Column(name = "person_name")
        discreditBlacklist.setPersonName(map.get("personName")==null?"":map.get("personName").toString());
        /**
         * 法定代表人身份证号|负责人身份证号
         */
        //@Column(name = "person_id")
        discreditBlacklist.setPersonId(map.get("personId")==null?"":map.get("personId").toString());
        /**
         * 失信类型
         */
        //@Column(name = "discredit_type")
        discreditBlacklist.setDiscreditType(map.get("discreditType")==null?"":map.get("discreditType").toString());
        /**
         * 失信行为
         */
        //@Column(name = "discredit_action")
        discreditBlacklist.setDiscreditAction(map.get("discreditAction")==null?"":map.get("discreditAction").toString());
        /**
         * 列入原因
         */
        //@Column(name = "punish_reason")
        discreditBlacklist.setPunishReason(map.get("punishReason")==null?"":map.get("punishReason").toString());
        /**
         * 处罚结果
         */
        //@Column(name = "punish_result")
        discreditBlacklist.setPunishResult(map.get("punishResult")==null?"":map.get("punishResult").toString());
        /**
         * 执行文号
         */
        //@Column(name = "judge_no")
        discreditBlacklist.setJudgeNo(map.get("judgeNo")==null?"":map.get("judgeNo").toString());
        /**
         * 执行时间
         */
        //@Column(name = "judge_date")
        discreditBlacklist.setJudgeDate(map.get("judgeDate")==null?"":map.get("judgeDate").toString());
        /**
         * 判决机关
         */
        //@Column(name = "judge_auth")
        discreditBlacklist.setJudgeAuth(map.get("judgeAuth")==null?"":map.get("judgeAuth").toString());
        /**
         * 发布日期
         */
        //@Column(name = "publish_date")
        discreditBlacklist.setPublishDate(map.get("publishDate")==null?"":map.get("publishDate").toString());
        /**
         * 当前状态
         */
        discreditBlacklist.setStatus(map.get("status")==null?"":map.get("status").toString());
        saveDisneycreditBlackListOne(discreditBlacklist,false);

        return null;
    }
}
