package com.mr.common.base.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * auther zjxu
 * time 201803
 * 存储整合的数据实体对象
 */
@Data
@Getter
@Setter
public class ConformityEntity {
    @Id
    @Column(name = "Primary_Key")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /*private String seqNo;
    private String punishNo;
    private String orgPerson;
    private String orgAddress;
    private String orgHolderName;
    private String priPerson;
    private String priCert;
    private String priJob;
    private String priAddress;
    private String stringBufferDetail;
    private String releaseOrg;
    private String releaseDate;
    private String punishOrg;
    private String punishDate;
    private String fileType;
    private String belongArea;*/
    // TODO 主键
    private String Primary_Key             ;         //punish_no+punish_title+punish_institution+punish_date
    // TODO 处罚文号=函号
    private String Punish_No               ;         //地方证监局、深交所、保监会
    // TODO 标题名称=函件标题
    private String Punish_Title            ;         //地方证监局、深交所、保监会、上交所、深交所、证监会
    // TODO 当事人（公司）=处罚对象=机构当事人名称=涉及对象=中介机构名称=处分对象
    private String Party_Institution       ;         //全国中小企业股转系统、地方证监局、保监会、深交所、证监会
    // TODO 当事人（个人）=处罚对象=当事人集合(当事人姓名)=涉及对象=处分对象
    private String Party_Person            ;         //全国中小企业股转系统、地方证监局、保监会、上交所、深交所
    // TODO 当事人集合(当事人身份证号)
    private String Party_Person_Id         ;         //保监会
    // TODO 当事人集合(当事人职务)
    private String Party_Person_Title      ;         //保监会
    // TODO 当事人集合(当事人住址)-机构所在地（保险公司分公司）
    private String Party_Person_Domi       ;         //保监会
    // TODO 一码通代码（当事人为个人）
    private String Unicode                 ;         //全国中小企业股转系统
    // TODO 处分对象类型
    private String Party_Category          ;         //深交所
    // TODO 住所地=机构当事人住所
    private String Domicile                ;         //全国中小企业股转系统
    // TODO 法定代表人=机构负责人姓名
    private String Legal_Representative    ;         //全国中小企业股转系统、保监会
    // TODO 当事人补充情况
    private String Party_Supplement        ;         //全国中小企业股转系统
    // TODO 公司全称
    private String Company_Full_Name       ;         //深交所、全国中小企业股转系统
    // TODO 中介机构类别
    private String Intermediary_Category   ;         //深交所
    // TODO 公司简称=涉及公司简称
    private String Company_Short_Name      ;         //深交所
    // TODO 公司代码=涉及公司代码
    private String Company_Code            ;         //深交所
    // TODO 证券代码
    private String Stock_Code              ;         //上交所
    // TODO 证券简称
    private String Stock_Short_Name        ;         //上交所
    // TODO 处分类别
    private String Punish_Category         ;         //深交所
    // TODO 违规情况=处理事由
    private String Irregularities          ;         //全国中小企业股转系统、上交所、证监会
    // TODO 相关法规=违反条例
    private String Related_Law             ;         //全国中小企业股转系统、证监会
    // TODO 涉及债券
    private String Related_Bond            ;         //深交所
    // TODO 处罚结果
    private String Punish_Result           ;         //全国中小企业股转系统、证监会
    // TODO 处罚结果补充情况
    private String Punish_Result_Supplement;         //全国中小企业股转系统
    // TODO 处罚机关=处罚机构
    private String Punish_Institution      ;         //保监会、证监会
    // TODO 处罚日期=处理日期=处分日期
    private String Punish_Date             ;         //地方证监局、保监会、上交所、深交所、证监会
    // TODO 整改时限
    private String Remedial_Limit_Time     ;         //证监会
    // TODO 发布机构
    private String Publisher               ;         //地方证监局、保监会
    // TODO 发布日期=发函日期
    private String Publish_Date            ;         //地方证监局、保监会
    // TODO 名单分类
    private String List_Classification     ;         //地方证监局
    // TODO 监管类型
    private String Supervision_Type        ;         //上交所
    // TODO 详情=行政处罚详情=全文
    private String Details                 ;         //地方证监局、保监会、深交所
    // TODO 来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）
    private String Source                  ;
    // TODO 主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录
    private String Object                  ;
    // TODO 创建时间
    private String Create_Time             ;
    // TODO 更新时间
    private String Update_Time             ;
}
