package com.mr.modules.api.model;

import com.mr.common.base.model.BaseEntity;
import java.util.Date;
import javax.persistence.*;

@Table(name = "credit_level")
public class CreditLevel extends BaseEntity {
   /* @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)*//*
    private Integer id;*/

    /**
     * 本条记录创建时间
     */
    //@Column(name = "created_at")
    private Date createdAt;

    /**
     * 本条记录最后更新时间
     */
    //@Column(name = "updated_at")
    private Date updatedAt;

    /**
     * url
     */
    private String url;

    /**
     * 数据来源{全国行业信用公共服务平台, 中国电力企业联合会}
     */
    private String source;

    /**
     * 企业名称
     */
    //@Column(name = "enterprise_name")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    //@Column(name = "enterprise_code1")
    private String enterpriseCode1;

    /**
     * 营业执照注册号
     */
    //@Column(name = "enterprise_code2")
    private String enterpriseCode2;

    /**
     * 组织机构代码
     */
    //@Column(name = "enterprise_code3")
    private String enterpriseCode3;

    /**
     * 信用等级
     */
    //@Column(name = "credit_level")
    private String creditLevel;

    /**
     * 证书编号
     */
    //@Column(name = "certificate_no")
    private String certificateNo;

    /**
     * 颁发日期
     */
    //@Column(name = "award_date")
    private Date awardDate;

    /**
     * 有效期至
     */
    //@Column(name = "valid_date")
    private Date validDate;

    /**
     * @return id
     */
   /* public Integer getId() {
        return id;
    }*/

    /**
     * @param id
     */
    /*public void setId(Integer id) {
        this.id = id;
    }*/

    /**
     * 获取本条记录创建时间
     *
     * @return created_at - 本条记录创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置本条记录创建时间
     *
     * @param createdAt 本条记录创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取本条记录最后更新时间
     *
     * @return updated_at - 本条记录最后更新时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置本条记录最后更新时间
     *
     * @param updatedAt 本条记录最后更新时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取url
     *
     * @return url - url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置url
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取数据来源{全国行业信用公共服务平台, 中国电力企业联合会}
     *
     * @return source - 数据来源{全国行业信用公共服务平台, 中国电力企业联合会}
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置数据来源{全国行业信用公共服务平台, 中国电力企业联合会}
     *
     * @param source 数据来源{全国行业信用公共服务平台, 中国电力企业联合会}
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 获取企业名称
     *
     * @return enterprise_name - 企业名称
     */
    public String getEnterpriseName() {
        return enterpriseName;
    }

    /**
     * 设置企业名称
     *
     * @param enterpriseName 企业名称
     */
    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    /**
     * 获取统一社会信用代码
     *
     * @return enterprise_code1 - 统一社会信用代码
     */
    public String getEnterpriseCode1() {
        return enterpriseCode1;
    }

    /**
     * 设置统一社会信用代码
     *
     * @param enterpriseCode1 统一社会信用代码
     */
    public void setEnterpriseCode1(String enterpriseCode1) {
        this.enterpriseCode1 = enterpriseCode1;
    }

    /**
     * 获取营业执照注册号
     *
     * @return enterprise_code2 - 营业执照注册号
     */
    public String getEnterpriseCode2() {
        return enterpriseCode2;
    }

    /**
     * 设置营业执照注册号
     *
     * @param enterpriseCode2 营业执照注册号
     */
    public void setEnterpriseCode2(String enterpriseCode2) {
        this.enterpriseCode2 = enterpriseCode2;
    }

    /**
     * 获取组织机构代码
     *
     * @return enterprise_code3 - 组织机构代码
     */
    public String getEnterpriseCode3() {
        return enterpriseCode3;
    }

    /**
     * 设置组织机构代码
     *
     * @param enterpriseCode3 组织机构代码
     */
    public void setEnterpriseCode3(String enterpriseCode3) {
        this.enterpriseCode3 = enterpriseCode3;
    }

    /**
     * 获取信用等级
     *
     * @return credit_level - 信用等级
     */
    public String getCreditLevel() {
        return creditLevel;
    }

    /**
     * 设置信用等级
     *
     * @param creditLevel 信用等级
     */
    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }

    /**
     * 获取证书编号
     *
     * @return certificate_no - 证书编号
     */
    public String getCertificateNo() {
        return certificateNo;
    }

    /**
     * 设置证书编号
     *
     * @param certificateNo 证书编号
     */
    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    /**
     * 获取颁发日期
     *
     * @return award_date - 颁发日期
     */
    public Date getAwardDate() {
        return awardDate;
    }

    /**
     * 设置颁发日期
     *
     * @param awardDate 颁发日期
     */
    public void setAwardDate(Date awardDate) {
        this.awardDate = awardDate;
    }

    /**
     * 获取有效期至
     *
     * @return valid_date - 有效期至
     */
    public Date getValidDate() {
        return validDate;
    }

    /**
     * 设置有效期至
     *
     * @param validDate 有效期至
     */
    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }
}