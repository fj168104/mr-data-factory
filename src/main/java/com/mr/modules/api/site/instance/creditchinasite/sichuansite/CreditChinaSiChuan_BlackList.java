package com.mr.modules.api.site.instance.creditchinasite.sichuansite;

import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 来源：信用中国（四川）
 *
 * 属性：企业名称  失信类型
 * 主题：失信黑名单
 * 1.  6家企业均被拉入 诚信系统“黑名单” [2016-12-23 ] url:http://www.creditsc.gov.cn/SCMH/staticPage/5B3312204FEE4A2AA1753E3A13DD78E1.html
 * 2.  四川处理236家建筑施工企业 11家被列入黑名单 [2016-12-23 ] TODO （没有名单信息）url:http://www.creditsc.gov.cn/SCMH/staticPage/63337122A63E46519B180A5FB1C99960.html
 * 3.  这四家企业太失信 [2016-12-23 ] url:http://www.creditsc.gov.cn/SCMH/staticPage/1933301B64A842C29A3AA3E9BF095417.html
 * 4.  四川省蓬溪县发布食品药品安全“黑名单 ”信息 [2018-07-04 ] url: http://www.creditsc.gov.cn/SCMH/staticPage/51694367-7f64-11e8-9786-fa163e4075c3.html
 * 5.  关于向社会公布2018年第一批重大劳动保障违法行为的公告 [2018-05-30 ]  http://www.creditsc.gov.cn/SCMH/staticPage/de09ab11-63b2-11e8-96fd-fa163e4075c3.html
 * 6. 国家体育总局：严重失信者将被列入“黑名单” [2018-05-04 ]  http://www.creditsc.gov.cn/SCMH/staticPage/c8aaaacd-4f4b-11e8-ba4b-fa163e4075c3.html
 * 7. 遂宁市2017第一期诚信“黑名单” [2018-01-16 ]  http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html
 * 8. 关于发布四川省九州工程勘测设计有限公司资质申报过程中失信行为信用处理结果的通知 [2017-12-18 ]  http://www.creditsc.gov.cn/SCMH/staticPage/95e00cc1-351d-1036-9d08-5f8ca080a0a3.html
 * 9. 四川失信企业集中公示名单（第一批） [2017-03-01 ]  http://www.creditsc.gov.cn/SCMH/staticPage/397BE86C8FD540779EDEB7FC725E3D99.html
 * 10.四川成都“贵锋老妈蹄花”店加亚硝酸钠 厨师获刑6个月 [2016-12-23 ]  http://www.creditsc.gov.cn/SCMH/staticPage/D47DE63089D542208A5533C1187847AA.html
 * 11.四川成都5家房地产评估机构被罚 [2016-12-23 ]     http://www.creditsc.gov.cn/SCMH/staticPage/B223E0836FA94D3BA73712F2945E6AF3.html
 * 12.四川成都餐饮不合格名单公布 滋味烤鱼查出致癌物 [2016-12-23 ]     http://www.creditsc.gov.cn/SCMH/staticPage/A491AEDCF3364661BEB5AC25A8A663AB.html
 * 13:四川处理236家建筑施工企业 [2016-12-23 ]  http://www.creditsc.gov.cn/SCMH/staticPage/BFC35F4822AE4E6BB15F7D00EB3C9C3E.html
 * 14:四川成都市工商行政管理局违法广告公告 [2016-12-23 ]  http://www.creditsc.gov.cn/SCMH/staticPage/DDC078B36A03437F88968F2331E99BD7.html
 * 15:四川国家检查出8批次不合格化妆品 成都要求就地封存 [2016-12-23 ]  http://www.creditsc.gov.cn/SCMH/staticPage/CC9A0491F044482A8A73470A5772AD7A.html
 * 16:四川成都工商发质监报告 雷士国星光电灯具登质量黑榜 [2016-12-23 ]  http://www.creditsc.gov.cn/SCMH/staticPage/E5A606E61132439885EC5F657234C1E0.html
 * 17:四川成都市扬尘办第三次通报黑名单 [2016-12-23 ]        http://www.creditsc.gov.cn/SCMH/staticPage/D0EF9FD24AE04BA9835A4E631A1DE561.html
 * 18:四川3家A级景区被摘牌 优胜劣汰提升旅游“含金量” [2016-12-23 ] http://www.creditsc.gov.cn/SCMH/staticPage/F19D7496568243AEA7007746199BA5A3.html
 */
@Slf4j
@Component("creditchinasichuan_blacklist")
@Scope("prototype")
public class CreditChinaSiChuan_BlackList extends SiteTaskExtend_CreditChina{
    @Override
    protected String execute() throws Throwable {
        webContext();
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    public void webContext(){
        List<Map> listMap = new ArrayList<>();
        List<String> listDetail = new ArrayList<>();


//         *1.  6家企业均被拉入 诚信系统“黑名单” [2016-12-23 ] url:http://www.creditsc.gov.cn/SCMH/staticPage/5B3312204FEE4A2AA1753E3A13DD78E1.html
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/5B3312204FEE4A2AA1753E3A13DD78E1.html||punishType@重大劳动保障违法||publishDate@2016-12-23||enterpriseName@裕达建工集团有限公司");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/5B3312204FEE4A2AA1753E3A13DD78E1.html||punishType@重大劳动保障违法||publishDate@2016-12-23||enterpriseName@东莞市湘粤建筑劳务有限公司");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/5B3312204FEE4A2AA1753E3A13DD78E1.html||punishType@重大劳动保障违法||publishDate@2016-12-23||enterpriseName@浙江新东方建设工程有限公司");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/5B3312204FEE4A2AA1753E3A13DD78E1.html||punishType@重大劳动保障违法||publishDate@2016-12-23||enterpriseName@联日（清远）纺织服装制衣有限公司");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/5B3312204FEE4A2AA1753E3A13DD78E1.html||punishType@重大劳动保障违法||publishDate@2016-12-23||enterpriseName@福建七建集团有限公司深圳分公司");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/5B3312204FEE4A2AA1753E3A13DD78E1.html||punishType@重大劳动保障违法||publishDate@2016-12-23||enterpriseName@清远市力创实业有限公司");
//         * 2.  四川处理236家建筑施工企业 11家被列入黑名单 [2016-12-23 ] TODO （没有名单信息）url:http://www.creditsc.gov.cn/SCMH/staticPage/63337122A63E46519B180A5FB1C99960.html

//         * 3.  这四家企业太失信 [2016-12-23 ] TODO 与（ 6家企业均被拉入 诚信系统“黑名单”）一致 url:http://www.creditsc.gov.cn/SCMH/staticPage/1933301B64A842C29A3AA3E9BF095417.html
//         *
//         * 4.  四川省蓬溪县发布食品药品安全“黑名单 ”信息 [2018-07-04 ] TODO （没有名单信息） url: http://www.creditsc.gov.cn/SCMH/staticPage/51694367-7f64-11e8-9786-fa163e4075c3.html

//         * 5.  关于向社会公布2018年第一批重大劳动保障违法行为的公告 [2018-05-30 ]  http://www.creditsc.gov.cn/SCMH/staticPage/de09ab11-63b2-11e8-96fd-fa163e4075c3.html
        listDetail.add("objectType@01||enterpriseName@宜宾市鑫海建筑工程有限责任公司||personName@李明||address@宜宾市长宁县长宁镇竹都大道三段鑫空间住宅小区6号楼||enterpriseCode1@91511524209153817R||discreditAction@拖欠61名劳动者工资134.66万元。||punishResult@2018年1月29日，珙县人力资源和社会保障局收到县住建城管局移交的案件材料，1月30日进行立案调查。2018年2月6日，珙县人力资源和社会保障局依法对宜宾市鑫海建筑工程有限责任公司下达了限期改正指令书，要求该公司限期支付工资。该公司逾期未履行。2018年2月11日，珙县人力资源社会保障局以涉嫌拒不支付劳动报酬罪将此案移交公安机关。||judgeAuth@珙县人力资源社会保障局||publishDate@2018-05-30||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/de09ab11-63b2-11e8-96fd-fa163e4075c3.html");

        listDetail.add("objectType@01||enterpriseName@四川省合江县三江建筑工程公司||personName@穆仕泉||address@泸州市合江县成教中心||enterpriseCode1@91510522204916571F||discreditAction@拖欠31名劳动者工资1061765元。||punishResult@2018年1月4日, 古蔺县人力资源社会保障局接到劳动者投诉，1月5日,立案调查。2018年1月16日，古蔺县人力资源社会保障局向四川省合江县三江建筑工程公司发出《古蔺县人力资源和社会保障局限期支付指令书》，责令该公司限期支付工资。但该公司逾期未履行。3月9日古蔺县人力资源社会保障局以涉嫌拒不支付劳动报酬罪将案件移送公安机关立案处理。||judgeAuth@古蔺县人力资源社会保障局||publishDate@2018-05-30||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/de09ab11-63b2-11e8-96fd-fa163e4075c3.html");

        listDetail.add("objectType@01||enterpriseName@四川天国香餐饮管理有限公司||personName@李永||address@巴中市巴州区江北新区江北广场东侧1幢2-1号||enterpriseCode1@91511902MA65Q6UM1D||discreditAction@" +
                "拖欠13名劳动者工资60200元。||punishResult@2018年1月25日,巴中市巴州区人力资源和社会保障局接到劳动者投诉，立即立案调查。2018年1月26日，巴州区人力资源和社会保障局向四川天国香餐饮管理有限公司下达《劳动保障监察限期改正指令书》，责令该公司限期支付劳动者工资。该公司逾期未履行。2018年2月11日，巴州区人力资源社会保障局依法以涉嫌拒不支付劳动报酬罪将案件移送公安机关查处。||judgeAuth@巴中市巴州区人力资源社会保障局||publishDate@2018-05-30||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/de09ab11-63b2-11e8-96fd-fa163e4075c3.html");
        listDetail.add("objectType@01||enterpriseName@资阳市雁江区亚泰装饰经营部||personName@代永富||address@资阳市雁江区英博大道国际商贸城营销中心四楼||enterpriseCode1@512002600441028||discreditAction@" +
                "拖欠60名劳动者2017年10 至12月工资100余万元。||punishResult2017年12月21日，资阳市人力资源社会保障局接到劳动者投诉，12月28日，进行立案调查。2018年1月15日，资阳市人力资源社会保障局向该公司留置送达了《劳动保障监察限期改正指令书》，要求该公司限期支付劳动者工资。该公司逾期未履行。1月26日资阳市人力资源社会保障局依法将该案移送资阳市公安局立案处理。该公司法定代表人代某及部分股东已被依法拘留。||judgeAuth@资阳市人力资源社会保障局||publishDate@2018-05-30||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/de09ab11-63b2-11e8-96fd-fa163e4075c3.html");

        listDetail.add("objectType@01||enterpriseName@四川旭升建筑安装工程有限公司||personName@李林||address@成都市武侯区浆洗街120号10-4号||enterpriseCode1@915100006674263097||discreditAction@拖欠7名员工2016年1至6月工资282417元，拖欠金额大、时间长，劳动者反映强烈，造成严重社会影响。||punishResult2017年11月28日，仁寿县人力资源社会保障局接到劳动者投诉，12月1日进行立案调查。2017年12月14日，仁寿县人力资源社会保障局作出劳动保障监察限期改正指令书，要求该公司限期支付劳动者工资。该公司逾期未履行。2018年1月4日，仁寿县人力资源社会保障局依法将该案移交仁寿县公安局。||judgeAuth@仁寿县人力资源社会保障局||publishDate@2018-05-30||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/de09ab11-63b2-11e8-96fd-fa163e4075c3.html");
//         * 6. 国家体育总局：严重失信者将被列入“黑名单” [2018-05-04 ]  TODO （没有名单信息） http://www.creditsc.gov.cn/SCMH/staticPage/c8aaaacd-4f4b-11e8-ba4b-fa163e4075c3.html

//         * 7. 遂宁市2017第一期诚信“黑名单” [2018-01-16 ]  http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@房地产秩序类||publishDate@2018-01-16||enterpriseName@四川湘涵房地产开发有限公司");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@房地产秩序类||publishDate@2018-01-16||enterpriseName@四川辰基置业有限公司");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@失信被执行人||publishDate@2018-01-16||enterpriseName@四川立菱星月轩家具有限公司||judgeNo@（2014）遂中民初字第47号民事判决书");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@失信被执行人||publishDate@2018-01-16||enterpriseName@四川送宁市财贸房地产开发有限公司||judgeNo@（2009）川民终字第598号民事判决书");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@失信被执行人||publishDate@2018-01-16||enterpriseName@广元驰天房地产开发有限责任公司||judgeNo@（2015）遂中民初字第69号民事判决书");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@失信被执行人||publishDate@2018-01-16||enterpriseName@重庆嘉华建设开发有限公司||judgeNo@（2015）遂中民初字第8、9号民事调解书");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@失信被执行人||publishDate@2018-01-16||enterpriseName@||judgeNo@（2014）遂中民初字第47号民事判决书）||personName@唐啶蜇||personId@512926190********17");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@失信被执行人||publishDate@2018-01-16||enterpriseName@||judgeNo@（2014）遂中民初字第47号民事判决书||personName@鞠碧珍||personId@512926196********68");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@失信被执行人||publishDate@2018-01-16||enterpriseName@||judgeNo@（2015)遂中民初字第3号民事调解书||personName@程宗政||personId@513027197******36");
        listDetail.add("sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@沁果园||discreditAction@占道经营");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@阳光水业||discreditAction@占道经营");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@阅家茶园||discreditAction@占道经营");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@好家乡水果店||discreditAction@占道经营");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@广德自由麻将馆||discreditAction@占道经营");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@勤慧副食||discreditAction@占道经营");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@多源书店||discreditAction@占道摆放烟摊、冰柜");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@鸿程烟酒 ||discreditAction@占道摆放烟摊、冰柜");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@迷宁土特产 ||discreditAction@占道摆放香蜡等物品");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@柏正 ||discreditAction@占道摆放物品");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@淘宝屋  ||discreditAction@占道摆放物品");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@老坝头串串香  ||discreditAction@严重占道经营");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@店小二串串香  ||discreditAction@严重占道经营");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@烙锅烧烤  ||discreditAction@严重占道经营");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@美家家居  ||discreditAction@长期占道堆放家具");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@万里长城  ||discreditAction@长期占道堆放床垫");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@圣者通讯  ||discreditAction@长期摆放宣传展架");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@祥林家电  ||discreditAction@长期摆放宣传展架");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@王者通讯  ||discreditAction@长期摆放宣传展架");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@九九龙翔  ||discreditAction@长期摆放宣传展架");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@千百汇  ||discreditAction@长期摆放家具");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@刘木匠  ||discreditAction@长期摆放家具");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@鸿瑞王朝  ||discreditAction@长期摆放家具");
        listDetail.add("objectType@01||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/f215e418-fa59-11e7-bfd7-fa163e4075c3.html||punishType@城市管理类||publishDate@2018-01-16||enterpriseName@万禾超市  ||discreditAction@长期食品，奶制品店外促销（占道）");
//         * 8. 关于发布四川省九州工程勘测设计有限公司资质申报过程中失信行为信用处理结果的通知 [2017-12-18 ] TODO 无具体黑名单信息  http://www.creditsc.gov.cn/SCMH/staticPage/95e00cc1-351d-1036-9d08-5f8ca080a0a3.html
//         * 9. 四川失信企业集中公示名单（第一批） [2017-03-01 ]   TODO 文档无法打开 http://www.creditsc.gov.cn/SCMH/staticPage/397BE86C8FD540779EDEB7FC725E3D99.html
//         * 10.四川成都“贵锋老妈蹄花”店加亚硝酸钠 厨师获刑6个月 [2016-12-23 ]  http://www.creditsc.gov.cn/SCMH/staticPage/D47DE63089D542208A5533C1187847AA.html
        listDetail.add("objectType@01||enterpriseName@四川成都“贵锋老妈蹄花”店||personName@||address@成都市武侯区浆洗街120号10-4号||discreditAction@贵锋老妈蹄花”因添加亚硝酸盐||punishResult@“贵锋老妈蹄花”因添加亚硝酸盐厨师被判刑6个月||judgeAuth@青羊区食药监局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/D47DE63089D542208A5533C1187847AA.html");
//         * 11.四川成都5家房地产评估机构被罚 [2016-12-23 ]     http://www.creditsc.gov.cn/SCMH/staticPage/B223E0836FA94D3BA73712F2945E6AF3.html
        listDetail.add("objectType@01||enterpriseName@四川永道合房地产土地评估有限公司||discreditAction@地产评估报告抽检不合格||punishResult@暂停其资质升级||judgeAuth@成都市房管局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/B223E0836FA94D3BA73712F2945E6AF3.html");
        listDetail.add("objectType@01||enterpriseName@四川蜀地房地产土地评估事务所有限责任公司||address@成都市武侯区浆洗街120号10-4号||discreditAction@地产评估报告抽检不合格||punishResult@暂停其资质升级||judgeAuth@成都市房管局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/B223E0836FA94D3BA73712F2945E6AF3.html");
        listDetail.add("objectType@01||enterpriseName@四川天道房地产评估有限公司||discreditAction@地产评估报告抽检不合格||punishResult@暂停其资质升级||judgeAuth@成都市房管局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/B223E0836FA94D3BA73712F2945E6AF3.html");
        listDetail.add("objectType@01||enterpriseName@四川华昊房地产评估有限责任公司||discreditAction@地产评估报告抽检不合格||punishResult@暂停其资质升级||judgeAuth@成都市房管局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/B223E0836FA94D3BA73712F2945E6AF3.html");
        listDetail.add("objectType@01||enterpriseName@成都濯锦房地产评估有限公司||discreditAction@地产评估报告抽检不合格||punishResult@暂停其资质升级||judgeAuth@成都市房管局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/B223E0836FA94D3BA73712F2945E6AF3.html");

//         * 12.四川成都餐饮不合格名单公布 滋味烤鱼查出致癌物 [2016-12-23 ]     http://www.creditsc.gov.cn/SCMH/staticPage/A491AEDCF3364661BEB5AC25A8A663AB.html
        listDetail.add("objectType@01||enterpriseName@蜀风园||discreditAction@餐饮不合格||punishResult@整改||judgeAuth@成都市食品药品监督管理局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/A491AEDCF3364661BEB5AC25A8A663AB.html");
        listDetail.add("objectType@01||enterpriseName@滋味烤鱼等名店||discreditAction@餐饮不合格||punishResult@整改||judgeAuth@成都市食品药品监督管理局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/A491AEDCF3364661BEB5AC25A8A663AB.html");
        listDetail.add("objectType@01||enterpriseName@高新区天泰路南堂馆||discreditAction@餐饮不合格||punishResult@整改||judgeAuth@成都市食品药品监督管理局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/A491AEDCF3364661BEB5AC25A8A663AB.html");
        listDetail.add("objectType@01||enterpriseName@蒲江县朝阳湖镇静丽庄农家乐||discreditAction@餐饮不合格||punishResult@整改||judgeAuth@成都市食品药品监督管理局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/A491AEDCF3364661BEB5AC25A8A663AB.html");
//         * 13:四川处理236家建筑施工企业 [2016-12-23 ] TODO 没有具体的黑名单信息 http://www.creditsc.gov.cn/SCMH/staticPage/BFC35F4822AE4E6BB15F7D00EB3C9C3E.html

//         * 14:四川成都市工商行政管理局违法广告公告 [2016-12-23 ] TODO 没有具体的黑名单信息 http://www.creditsc.gov.cn/SCMH/staticPage/DDC078B36A03437F88968F2331E99BD7.html
//         * 15:四川国家检查出8批次不合格化妆品 成都要求就地封存 [2016-12-23 ]  http://www.creditsc.gov.cn/SCMH/staticPage/CC9A0491F044482A8A73470A5772AD7A.html
        listDetail.add("objectType@01||enterpriseName@朗曜日化（上海）有限公司||discreditAction@化妆品不合格||punishResult@不合格产品须就地封存||judgeAuth@成都市食品药品监督管理局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/CC9A0491F044482A8A73470A5772AD7A.html");
        listDetail.add("objectType@01||enterpriseName@上海臻美高科技发展有限公司||discreditAction@化妆品不合格||punishResult@不合格产品须就地封存||judgeAuth@成都市食品药品监督管理局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/CC9A0491F044482A8A73470A5772AD7A.html");
        listDetail.add("objectType@01||enterpriseName@广州澳谷生物科技有限公司||discreditAction@化妆品不合格||punishResult@不合格产品须就地封存||judgeAuth@成都市食品药品监督管理局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/CC9A0491F044482A8A73470A5772AD7A.html");
        listDetail.add("objectType@01||enterpriseName@广州天姿丽化妆品有限公司||discreditAction@化妆品不合格||punishResult@不合格产品须就地封存||judgeAuth@成都市食品药品监督管理局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/CC9A0491F044482A8A73470A5772AD7A.html");
        listDetail.add("objectType@01||enterpriseName@广州市白云区美莲葆化妆品厂||discreditAction@化妆品不合格||punishResult@不合格产品须就地封存||judgeAuth@成都市食品药品监督管理局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/CC9A0491F044482A8A73470A5772AD7A.html");
//         * 16:四川成都工商发质监报告 雷士国星光电灯具登质量黑榜 [2016-12-23 ] TODO 没有具体的黑名单信息 http://www.creditsc.gov.cn/SCMH/staticPage/E5A606E61132439885EC5F657234C1E0.html
//         * 17:四川成都市扬尘办第三次通报黑名单 [2016-12-23 ]        http://www.creditsc.gov.cn/SCMH/staticPage/D0EF9FD24AE04BA9835A4E631A1DE561.html
        listDetail.add("objectType@01||enterpriseName@成都路桥工程股份有限公司||discreditAction@扬尘污染||punishResult@通报批评||judgeAuth@四川成都市扬尘办||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/D0EF9FD24AE04BA9835A4E631A1DE561.html");
        listDetail.add("objectType@01||enterpriseName@成都路桥工程股份有限公司||discreditAction@扬尘污染||punishResult@通报批评||judgeAuth@四川成都市扬尘办||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/D0EF9FD24AE04BA9835A4E631A1DE561.html");
//         * 18:四川3家A级景区被摘牌 优胜劣汰提升旅游“含金量” [2016-12-23 ] http://www.creditsc.gov.cn/SCMH/staticPage/F19D7496568243AEA7007746199BA5A3.html
        listDetail.add("objectType@01||enterpriseName@成都市中国枇杷博览园（3A）||discreditAction@景区质量合格||punishResult@摘牌||judgeAuth@四川省旅游局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/D0EF9FD24AE04BA9835A4E631A1DE561.html");
        listDetail.add("objectType@01||enterpriseName@绵阳市凤凰山旅游景区（2A）||discreditAction@景区质量合格||punishResult@摘牌||judgeAuth@四川省旅游局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/D0EF9FD24AE04BA9835A4E631A1DE561.html");
        listDetail.add("objectType@01||enterpriseName@安华蓥市仙鹤洞景区（2A）||discreditAction@景区质量合格||punishResult@摘牌||judgeAuth@四川省旅游局||publishDate@2016-12-23||sourceUrl@http://www.creditsc.gov.cn/SCMH/staticPage/D0EF9FD24AE04BA9835A4E631A1DE561.html");
        for(String recordList : listDetail){
            Map map = new HashMap();
            String[] records = recordList.split("\\|\\|");
            if(records.length>0){
                for(String strRecord : records){
                    String[] record = strRecord.split("@");
                    if(record.length==2){
                        map.put(record[0],record[1]);
                    }
                }
                map.put("source","信用中国（四川）");
                map.put("subject","失信黑名单");
            }
            insertDiscreditBlacklist(map);
            log.info("--------------------------"+map);
        }
    }
}
