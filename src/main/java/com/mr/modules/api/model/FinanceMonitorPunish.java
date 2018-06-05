package com.mr.modules.api.model;

import com.mr.common.base.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Transient;

@Data
@EqualsAndHashCode(callSuper = true)
public class FinanceMonitorPunish extends BaseEntity {

    /**
     * 业务主键 | punish_no+punish_title+punish_institution+punish_date
     */
    private String primaryKey;

    /**
     * 处罚文号=函号 | 地方证监局、深交所、保监会
     */
    private String punishNo;

    /**
     * 标题名称=函件标题 | 地方证监局、深交所、保监会、上交所、深交所、证监会
     */
    private String punishTitle;

    /**
     * 当事人（公司）=处罚对象=机构当事人名称=涉及对象=中介机构名称=处分对象 | 全国中小企业股转系统、地方证监局、保监会、深交所、证监会
     */
    private String partyInstitution;

    /**
     * 当事人（个人）=处罚对象=当事人集合(当事人姓名)=涉及对象=处分对象 | 全国中小企业股转系统、地方证监局、保监会、上交所、深交所
     */
    private String partyPerson;

    /**
     * 当事人集合(当事人身份证号)|保监会
     */
    private String partyPersonId;

    /**
     * 当事人集合(当事人职务) | 保监会
     */
    private String partyPersonTitle;

    /**
     * 当事人集合(当事人住址)-机构所在地（保险公司分公司）|保监会
     */
    private String partyPersonDomi;

    /**
     * 一码通代码（当事人为个人）| 全国中小企业股转系统
     */
    private String unicode;

    /**
     * 处分对象类型|深交所
     */
    private String partyCategory;

    /**
     * 住所地=机构当事人住所|全国中小企业股转系统
     */
    private String domicile;

    /**
     * 法定代表人=机构负责人姓名|全国中小企业股转系统、保监会
     */
    private String legalRepresentative;

    /**
     * 当事人补充情况|全国中小企业股转系统
     */
    private String partySupplement;

    /**
     * 公司全称|深交所、全国中小企业股转系统
     */
    private String companyFullName;

    /**
     * 中介机构类别|深交所
     */
    private String intermediaryCategory;

    /**
     * 公司简称=涉及公司简称|深交所
     */
    private String companyShortName;

    /**
     * 公司代码=涉及公司代码|深交所
     */
    private String companyCode;

    /**
     * 证券代码|上交所
     */
    private String stockCode;

    /**
     * 证券简称|上交所
     */
    private String stockShortName;

    /**
     * 处分类别|深交所
     */
    private String punishCategory;

    /**
     * 涉及债券|深交所
     */
    private String relatedBond;

    /**
     * 处罚结果|全国中小企业股转系统、证监会
     */
    private String punishResult;

    /**
     * 处罚机关=处罚机构|保监会、证监会
     */
    private String punishInstitution;

    /**
     * 处罚日期=处理日期=处分日期|地方证监局、保监会、上交所、深交所、证监会
     */
    private String punishDate;

    /**
     * 整改时限|证监会
     */
    private String remedialLimitTime;

    /**
     * 发布机构|地方证监局、保监会
     */
    private String publisher;

    /**
     * 发布日期=发函日期|地方证监局、保监会
     */
    private String publishDate;

    /**
     * 监管类型|地方证监局
     */
    private String listClassification;

    /**
     * 名单分类|上交所
     */
    private String supervisionType;

    /**
     * 来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）
     */
    private String source;

    /**
     * 来源url
     */
    private String url;

    /**
     * 主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录
     */
    private String object;

    /**
     * 违规情况=处理事由|全国中小企业股转系统、上交所、证监会
     */
    private String irregularities;

    /**
     * 相关法规=违反条例|全国中小企业股转系统、证监会
     */
    private String relatedLaw;

    /**
     * 处罚结果补充情况|全国中小企业股转系统
     */
    private String punishResultSupplement;

    /**
     * 详情=行政处罚详情=全文|地方证监局、保监会、深交所
     */
    private String details;

    /**
     * 工商名|所有
     */
//    private String icName;

    /**
     * 区域
     */
    @Transient
    private String region;

}