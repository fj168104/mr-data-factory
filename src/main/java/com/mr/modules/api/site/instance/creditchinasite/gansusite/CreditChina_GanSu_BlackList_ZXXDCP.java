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
            map.put("source","信用中国（甘肃）");
            map.put("subject","注销消毒产品生产企业卫生许可证的通告");

            insertDiscreditBlacklist(map);
        }
    }


}
