package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import com.mr.framework.core.collection.CollectionUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
import com.mr.modules.api.model.DiscreditBlacklist;
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
 * @auther
 * 1.信用中国（甘肃）
 * 2.url:http://www.gscredit.gov.cn/blackList/94084.jhtml
 */
@Slf4j
@Component("gansu_94084")
@Scope("prototype")
public class Gansu_94084 extends SiteTaskExtend_CreditChina{
    String url ="http://www.gscredit.gov.cn/blackList/94084.jhtml";

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    @Override
    protected String execute() throws Throwable {
        try {
            extractContext(url);
        }catch (Exception e){
            writeBizErrorLog(url, e.getMessage());
        }
        return null;
    }
    /**
     * 获取网页内容
     * 发布单位投诉电话、新闻发布日期、企业名称、企业所在地、上榜原因、惩戒措施、
     */
    public void extractContext(String url){
        DiscreditBlacklist dcbl = null;
        Document document  = Jsoup.parse(getData(url));
        Elements elementDivs = document.getElementsByTag("div");
        String subject = "";

        for(Element elementDiv : elementDivs){

            Elements elelementSpans = elementDiv.getElementsByTag("span");
            if(CollectionUtil.isEmpty(elelementSpans)) continue;

            if(StrUtil.isEmpty(subject)) {
                subject = elelementSpans.first().text();
                if(subject.contains("甘肃省高级人民法院失信被执行人黑榜名单及惩戒措施")){
                    subject = "省高级人民法院诚信黑榜";
                    continue;
                }
                if(subject.contains("甘肃省地方税务局地方税收诚信纳税黑榜名单及惩戒措施")){
                    subject = "省地方税务局诚信黑榜";
                    continue;
                }
                if(subject.contains("甘肃省工商行政管理局黑榜名单及惩戒措施")){
                    subject = "省工商行政管理局黑榜";
                    continue;
                }
                if(subject.contains("甘肃省质量技术监督局质量违法企业黑榜名单及惩戒措施")){
                    subject = "省质监局诚信黑榜";
                    continue;
                }
                if(subject.contains("甘肃省食品药品监督管理局食品药品黑榜名单及惩戒措施")){
                    subject = "省食药监管局诚信黑榜";
                    continue;
                }
                if(subject.contains("甘肃省国家税务局纳税诚信黑榜企业及惩戒措施")){
                    subject = "省国税局诚信黑榜";
                    continue;
                }

                subject = "";
                continue;
            }

            //省高级人民法院诚信黑榜
            if(subject.equals("省高级人民法院诚信黑榜")){
                String text = elelementSpans.first().text();
                //省高级人民法院诚信黑榜 处理结束
                if(text.contains("原因：")){
                    subject = "";
                    continue;
                }
                DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
                discreditBlacklist.setSubject(subject);
                if(text.contains(".")){
                    discreditBlacklist.setEnterpriseName(text.substring(text.indexOf(".") + 1));
                }else {
                    discreditBlacklist.setEnterpriseName(text);
                }

                discreditBlacklist.setPunishReason("均为拒不履行生效法律文书确定的义务。");
                String punishResult ="1.对上述单位及其法定代表人、主要负责人、影响债务履行的直接责任人员、实际控制人限制高消费，包括禁止乘坐飞机、列车软卧以上座位；禁止在星级以上酒店、餐馆、娱乐场所消费；禁止购买不动产或者新建、扩建、高档装修房屋；禁止租赁高档写字楼、宾馆、公寓等场所办公；禁止购买非经营必需车辆；禁止旅游、度假；禁止子女就读高收费私立学校；禁止支付高额保费购买保险理财产品；禁止乘坐G字头动车组列车全部座位、其他动车组列车一等以上座位等其他非生活和工作必需的消费行为。" +
                        "2.对上述单位及其法定代表人、主要负责人、影响债务履行的直接责任人员、实际控制人的失信行为，作为不良记录推送到人民银行征信系统、工商信用系统以及发改、财政、税务、国土、房产、海关等部门，联合实施信用惩戒。包括限制其在金融机构贷款和办理信用卡，限制其开办新公司和投资入股，限制参与政府采购、招标投标，限制行政审批、政府扶持、市场准入、资质认证等。" +
                        "3.对违反禁令高消费、有能力履行而拒不履行生效裁判的失信被执行人，人民法院将依法对单位处以100万元以下、对个人10万元以下罚款，对法定代表人和实际控制人采取司法拘留强制措施。情节严重构成犯罪的，根据《刑法》第三百一十三条和全国人大关于追究拒不执行法院判决罪立法解释的规定，严肃追究刑事责任。";
                discreditBlacklist.setPunishResult(punishResult);
                saveDisneycreditBlackListOne(discreditBlacklist, false);
                continue;
            }

            //省地方税务局诚信黑榜
            if(subject.equals("省地方税务局诚信黑榜")){
                String text = elelementSpans.first().text();
                //省地方税务局诚信黑榜 处理结束
                if(text.contains("惩戒措施：")){
                    subject = "";
                    continue;
                }

                if(text.contains("原因：")){
                    dcbl.setPunishReason(text.replace("原因：", ""));
                    saveDisneycreditBlackListOne(dcbl, false);
                    continue;
                }
                dcbl = createDefaultDiscreditBlacklist();
                dcbl.setSubject(subject);
                if(text.contains(".")){
                    dcbl.setEnterpriseName(text.substring(text.indexOf(".") + 1));
                }else {
                    dcbl.setEnterpriseName(text);
                }

                String punishResult ="1.纳税人未按照规定的期限办理纳税申报和报送纳税资料的，或者扣缴义务人未按照规定的期限向税务机关报送代扣代缴、代收代缴税款报告表和有关资料的，由税务机关责令限期改正，可以处二千元以下的罚款；情节严重的，可以处二千元以上一万元以下的罚款。" +
                        "2.纳税人出现伪造、变造、隐匿、擅自销毁账簿、记账凭证，或者在账簿上多列支出或者不列、少列收入，或者经税务机关通知申报而拒不申报或者进行虚假的纳税申报，不缴或者少缴应纳税款等偷税行为的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款；构成犯罪的移交司法机关追究刑事责任。" +
                        "3.纳税人、扣缴义务人编造虚假计税依据的，由税务机关责令限期改正，并处五万元以下的罚款。" +
                        "4.纳税人不进行纳税申报，不缴或者少缴应纳税款的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款。";
                dcbl.setPunishResult(punishResult);
                continue;
            }

            //甘肃省工商行政管理局黑榜名单及惩戒措施
            if(subject.equals("省工商行政管理局黑榜")){
                String text = elelementSpans.first().text();
                //省工商行政管理局黑榜 处理结束
                if(text.contains("惩戒措施：")){
                    subject = "";
                    continue;
                }

                if(text.contains("原因：")){
                    dcbl.setPunishReason(text.replace("原因：", ""));
                    saveDisneycreditBlackListOne(dcbl, false);
                    continue;
                }
                dcbl = createDefaultDiscreditBlacklist();
                dcbl.setSubject(subject);
                if(text.contains(".")){
                    dcbl.setEnterpriseName(text.substring(text.indexOf(".") + 1));
                }else {
                    dcbl.setEnterpriseName(text);
                }

                String punishResult ="1.充分利用全国企业信用信息公示系统（甘肃），加大对黑榜企业信息曝光力度，加强对黑榜企业经营活动重点监控。" +
                        "2.进一步强化信息共享、协同监管，及时将黑榜企业的违法信息推送到相关部门实施联合惩戒，真正使违法企业一处受罚、处处受限。" +
                        "3.列入经营异常名录的黑榜企业，按照《企业信息公示暂行条例》第十八条规定，在政府采购、工程招投标、国有土地出让等工作中，依法予以限制和禁止" +
                        "4.全省工商系统不受理纳入黑榜名单企业及其经营管理人员任何评先选优等表彰申请。";
                dcbl.setPunishResult(punishResult);
                continue;
            }

            //甘肃省质量技术监督局质量违法企业黑榜名单及惩戒措施
            if(subject.equals("省质监局诚信黑榜")){
                String text = elelementSpans.first().text();
                //省质监局诚信黑榜 处理结束
                if(text.contains("惩戒措施：")){
                    subject = "";
                    continue;
                }

                if(text.contains("原因：")){
                    dcbl.setPunishReason(text.replace("原因：", ""));
                    saveDisneycreditBlackListOne(dcbl, false);
                    continue;
                }
                dcbl = createDefaultDiscreditBlacklist();
                dcbl.setSubject(subject);
                if(text.contains(".")){
                    dcbl.setEnterpriseName(text.substring(text.indexOf(".") + 1));
                }else {
                    dcbl.setEnterpriseName(text);
                }

                String punishResult ="责令企业停止生产，并对违法行为进行整改。" +
                        "2.采取罚款、停业整顿、没收违法所得等行政处罚。" +
                        "3.根据违法事实和情节，触犯刑法的一律移送司法机关。" +
                        "4.不受理纳入黑榜名单企业及其经营管理人员的任何评先选优等表彰申请。";
                dcbl.setPunishResult(punishResult);
                continue;
            }

            //甘肃省食品药品监督管理局食品药品黑榜名单及惩戒措施
            if(subject.equals("省食药监管局诚信黑榜")){
                String text = elelementSpans.first().text();
                //省食药监管局诚信黑榜 处理结束
                if(text.contains("惩戒措施：")){
                    subject = "";
                    continue;
                }

                if(text.contains("原因：")){
                    dcbl.setPunishReason(text.replace("原因：", ""));
                    saveDisneycreditBlackListOne(dcbl, false);
                    continue;
                }
                dcbl = createDefaultDiscreditBlacklist();
                dcbl.setSubject(subject);
                if(text.contains(".")){
                    dcbl.setEnterpriseName(text.substring(text.indexOf(".") + 1));
                }else {
                    dcbl.setEnterpriseName(text);
                }

                String punishResult ="1.各级食品药品监督管理部门要加大黑榜信息的公开曝光力度。对辖区内列入食品药品黑榜企业和单位要在其政务网站和当地媒体公开曝光，并将黑榜名单向同级卫生、工商、金融等部门通报。" +
                        "2.各级食品药品监督管理部门要加大黑榜企业和单位的监管力度。把列入食品药品黑榜名单的生产经营企业和使用单位作为重点监管对象，通过增加监督检查和抽验频次等方式加强监管。列入食品药品黑榜名单的生产经营者、责任人员，再次发生违法违规行为的，依法从重处罚。" +
                        "3.充分依靠人民群众，营造食品药品安全社会共治格局。畅通12331食品药品安全投诉举报渠道，落实举报奖励制度，鼓励社会组织或个人对食品药品黑榜企业和单位进行监督，举报列入食品药品黑榜名单的生产经营者和责任人员的违法行为。";
                dcbl.setPunishResult(punishResult);
                continue;
            }


            //甘肃省国家税务局纳税诚信黑榜企业及惩戒措施
            if(subject.equals("省国税局诚信黑榜")){
                String text = elelementSpans.first().text();
                //省国税局诚信黑榜 处理结束
                if(text.contains("惩戒措施：")){
                    subject = "";
                    break;
                }

                if(text.contains("原因：")){
                    dcbl.setPunishReason(text.replace("原因：", ""));
                    saveDisneycreditBlackListOne(dcbl, false);
                    log.info(dcbl.toString());
                    continue;
                }
                dcbl = createDefaultDiscreditBlacklist();
                dcbl.setSubject(subject);
                if(text.contains(".")){
                    dcbl.setEnterpriseName(text.substring(text.indexOf(".") + 1));
                }else {
                    dcbl.setEnterpriseName(text);
                }

                String punishResult ="1.对于达到重大税收违法案件信息公布标准的案件，依据《关于对重大税收违法案件当事人实施联合惩戒措施的合作备忘录》的惩戒措施及操作程序，与有关部门共同进行联合惩戒。" +
                        "2.纳税人不进行纳税申报，不缴或者少缴应纳税款的，由税务机关追缴其不缴或者少缴的税款、滞纳金，并处不缴或者少缴的税款百分之五十以上五倍以下的罚款。" +
                        "3.纳税人、扣缴义务人有《税收征管法》第六十三条、第六十五条、第六十六条、第六十七条、第七十一条规定的行为涉嫌犯罪的，税务机关应当依法移交司法机关追究刑事责任。";
                dcbl.setPunishResult(punishResult);
                continue;
            }
        }

    }

    protected boolean saveDisneycreditBlackListOne(DiscreditBlacklist discreditBlacklist, Boolean isForce) {
        try{
            discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
            return super.saveDisneycreditBlackListOne(discreditBlacklist, false);
        }catch (Exception e){
            writeBizErrorLog(url, e.getMessage());
        }
        return true;
    }

    private DiscreditBlacklist createDefaultDiscreditBlacklist(){
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();

        discreditBlacklist.setCreatedAt(new Date());
        discreditBlacklist.setUpdatedAt(new Date());
        discreditBlacklist.setSource("信用中国（甘肃）");
        discreditBlacklist.setUrl(url);
        discreditBlacklist.setObjectType("01");
        discreditBlacklist.setEnterpriseName("");
        discreditBlacklist.setEnterpriseCode1("");
        discreditBlacklist.setEnterpriseCode2("");
        discreditBlacklist.setEnterpriseCode3("");
        discreditBlacklist.setPersonName("");
        discreditBlacklist.setPersonId("");
        discreditBlacklist.setDiscreditType("");
        discreditBlacklist.setDiscreditAction("");
        discreditBlacklist.setJudgeNo("");
        discreditBlacklist.setJudgeDate("");
        discreditBlacklist.setJudgeAuth("");
        discreditBlacklist.setStatus("");
        discreditBlacklist.setPublishDate("2015/12/09");
        return discreditBlacklist;
    }

}
