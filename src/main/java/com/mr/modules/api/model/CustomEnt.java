package com.mr.modules.api.model;

import com.mr.common.base.model.BaseEntity;
import java.util.Date;
import javax.persistence.*;

@Table(name = "custom_ent")
public class CustomEnt extends BaseEntity {
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
     * 数据来源{中国海关总署}
     */
    private String source;

    /**
     * 信用等级
     */
    //@Column(name = "credit_level")
    private String creditLevel;

    /**
     * 所属名录{异常企业名录，失信企业名录}
     */
    //@Column(name = "list_name")
    private String listName;

    /**
     * 注册海关
     */
    //@Column(name = "custom_name")
    private String customName;

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
     * 适用信用等级时间、移入名录时间
     */
    //@Column(name = "start_date")
    private Date startDate;

    /**
     * 移入名录原因
     */
    //@Column(name = "list_reason")
    private Date listReason;

    /**
     * @return id
     */
    /*public Integer getId() {
        return id;
    }*/

    /**
     * @param id
     */
   /* public void setId(Integer id) {
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
     * 获取数据来源{中国海关总署}
     *
     * @return source - 数据来源{中国海关总署}
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置数据来源{中国海关总署}
     *
     * @param source 数据来源{中国海关总署}
     */
    public void setSource(String source) {
        this.source = source;
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
     * 获取所属名录{异常企业名录，失信企业名录}
     *
     * @return list_name - 所属名录{异常企业名录，失信企业名录}
     */
    public String getListName() {
        return listName;
    }

    /**
     * 设置所属名录{异常企业名录，失信企业名录}
     *
     * @param listName 所属名录{异常企业名录，失信企业名录}
     */
    public void setListName(String listName) {
        this.listName = listName;
    }

    /**
     * 获取注册海关
     *
     * @return custom_name - 注册海关
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * 设置注册海关
     *
     * @param customName 注册海关
     */
    public void setCustomName(String customName) {
        this.customName = customName;
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
     * 获取适用信用等级时间、移入名录时间
     *
     * @return start_date - 适用信用等级时间、移入名录时间
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 设置适用信用等级时间、移入名录时间
     *
     * @param startDate 适用信用等级时间、移入名录时间
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 获取移入名录原因
     *
     * @return list_reason - 移入名录原因
     */
    public Date getListReason() {
        return listReason;
    }

    /**
     * 设置移入名录原因
     *
     * @param listReason 移入名录原因
     */
    public void setListReason(Date listReason) {
        this.listReason = listReason;
    }
}