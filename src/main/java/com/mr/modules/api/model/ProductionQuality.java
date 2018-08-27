package com.mr.modules.api.model;

import com.mr.common.base.model.BaseEntity;
import java.util.Date;
import javax.persistence.*;

@Table(name = "production_quality")
public class ProductionQuality extends BaseEntity {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * 数据来源{工信部，商务部}
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
     * 检查产品
     */
    //@Column(name = "oper_production")
    private String operProduction;

    /**
     * 检查结果
     */
    //@Column(name = "oper_result")
    private String operResult;

    /**
     * 检查机关
     */
    //@Column(name = "oper_org")
    private String operOrg;

    /**
     * 发布日期
     */
    //@Column(name = "publish_date")
    private Date publishDate;

    /**
     * @return id
     */
    /*public Integer getId() {
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
     * 获取数据来源{工信部，商务部}
     *
     * @return source - 数据来源{工信部，商务部}
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置数据来源{工信部，商务部}
     *
     * @param source 数据来源{工信部，商务部}
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
     * 获取检查产品
     *
     * @return oper_production - 检查产品
     */
    public String getOperProduction() {
        return operProduction;
    }

    /**
     * 设置检查产品
     *
     * @param operProduction 检查产品
     */
    public void setOperProduction(String operProduction) {
        this.operProduction = operProduction;
    }

    /**
     * 获取检查结果
     *
     * @return oper_result - 检查结果
     */
    public String getOperResult() {
        return operResult;
    }

    /**
     * 设置检查结果
     *
     * @param operResult 检查结果
     */
    public void setOperResult(String operResult) {
        this.operResult = operResult;
    }

    /**
     * 获取检查机关
     *
     * @return oper_org - 检查机关
     */
    public String getOperOrg() {
        return operOrg;
    }

    /**
     * 设置检查机关
     *
     * @param operOrg 检查机关
     */
    public void setOperOrg(String operOrg) {
        this.operOrg = operOrg;
    }

    /**
     * 获取发布日期
     *
     * @return publish_date - 发布日期
     */
    public Date getPublishDate() {
        return publishDate;
    }

    /**
     * 设置发布日期
     *
     * @param publishDate 发布日期
     */
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
}