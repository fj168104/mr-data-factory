package com.mr.modules.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "t03_tax_punish")
public class T03TaxPunish {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 纳税人名称
     */
    @Column(name = "tax_name")
    private String taxName;

    /**
     * 纳税人识别号
     */
    @Column(name = "tax_id")
    private String taxId;

    /**
     * 统一社会信用代码
     */
    @Column(name = "credit_no")
    private String creditNo;

    /**
     * 组织机构代码
     */
    @Column(name = "org_code")
    private String orgCode;

    /**
     * 工商注册号
     */
    @Column(name = "reg_no")
    private String regNo;

    /**
     * 注册地址
     */
    @Column(name = "reg_address")
    private String regAddress;

    /**
     * 法定代表人或者负责人姓名
     */
    @Column(name = "fr_name")
    private String frName;

    /**
     * 法定代表人或者负责人性别
     */
    @Column(name = "fr_sex")
    private String frSex;

    /**
     * 法定代表人或者负责人证件类型
     */
    @Column(name = "fr_cert_type")
    private String frCertType;

    /**
     * 法定代表人或者负责人证件号码
     */
    @Column(name = "fr_cert_no")
    private String frCertNo;

    /**
     * 责任的财务负责人姓名
     */
    @Column(name = "finance_name")
    private String financeName;

    /**
     * 责任的财务负责人性别
     */
    @Column(name = "finance_sex")
    private String financeSex;

    /**
     * 责任的财务负责人证件类型
     */
    @Column(name = "finance_cert_type")
    private String financeCertType;

    /**
     * 责任的财务负责人证件号码
     */
    @Column(name = "finance_cert_no")
    private String financeCertNo;

    /**
     * 负有直接责任的中介机构
     */
    private String agency;

    /**
     * 案件性质
     */
    @Column(name = "case_nature")
    private String caseNature;

    /**
     * 处罚依据
     */
    @Column(name = "pen_basis")
    private String penBasis;

    /**
     * 处罚结果
     */
    @Column(name = "pen_result")
    private String penResult;

    /**
     * 发布级别
     */
    @Column(name = "publish_level")
    private String publishLevel;

    /**
     * 公布时间
     */
    @Column(name = "publish_date")
    private String publishDate;

    /**
     * 撤销日期
     */
    @Column(name = "cancel_date")
    private String cancelDate;

    /**
     * 注销原因
     */
    @Column(name = "cancel_reason")
    private String cancelReason;

    /**
     * 实施检查单位
     */
    @Column(name = "inspect__org")
    private String inspectOrg;

    /**
     * 罚款
     */
    private String fine;

    /**
     * 税款、滞纳金、罚款是否全部缴清
     */
    @Column(name = "if_pay_off")
    private String ifPayOff;

    /**
     * 是否缴清税款
     */
    @Column(name = "if_pay_tax")
    private String ifPayTax;

    /**
     * 是否缴清滞纳金
     */
    @Column(name = "if_pay_latefee")
    private String ifPayLatefee;

    /**
     * 是否缴清罚款
     */
    @Column(name = "if_pay_fine")
    private String ifPayFine;

    /**
     * 企业经营情况
     */
    @Column(name = "business_state")
    private String businessState;

    /**
     * 信用状态
     */
    @Column(name = "credit_status")
    private String creditStatus;

    /**
     * 信息来源
     */
    @Column(name = "publish_source")
    private String publishSource;

    /**
     * 数据更新时间
     */
    @Column(name = "data_time")
    private String dataTime;

    /**
     * 数据来源
     */
    @Column(name = "data_source")
    private String dataSource;

    /**
     * 数据来源代码
     */
    @Column(name = "data_source_code")
    private String dataSourceCode;

    /**
     * 原文链接
     */
    private String url;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 主要违法事实
     */
    @Column(name = "illeg_fact")
    private String illegFact;

    /**
     * 获取自增主键
     *
     * @return id - 自增主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置自增主键
     *
     * @param id 自增主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取纳税人名称
     *
     * @return tax_name - 纳税人名称
     */
    public String getTaxName() {
        return taxName;
    }

    /**
     * 设置纳税人名称
     *
     * @param taxName 纳税人名称
     */
    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    /**
     * 获取纳税人识别号
     *
     * @return tax_id - 纳税人识别号
     */
    public String getTaxId() {
        return taxId;
    }

    /**
     * 设置纳税人识别号
     *
     * @param taxId 纳税人识别号
     */
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    /**
     * 获取统一社会信用代码
     *
     * @return credit_no - 统一社会信用代码
     */
    public String getCreditNo() {
        return creditNo;
    }

    /**
     * 设置统一社会信用代码
     *
     * @param creditNo 统一社会信用代码
     */
    public void setCreditNo(String creditNo) {
        this.creditNo = creditNo;
    }

    /**
     * 获取组织机构代码
     *
     * @return org_code - 组织机构代码
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * 设置组织机构代码
     *
     * @param orgCode 组织机构代码
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * 获取工商注册号
     *
     * @return reg_no - 工商注册号
     */
    public String getRegNo() {
        return regNo;
    }

    /**
     * 设置工商注册号
     *
     * @param regNo 工商注册号
     */
    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    /**
     * 获取注册地址
     *
     * @return reg_address - 注册地址
     */
    public String getRegAddress() {
        return regAddress;
    }

    /**
     * 设置注册地址
     *
     * @param regAddress 注册地址
     */
    public void setRegAddress(String regAddress) {
        this.regAddress = regAddress;
    }

    /**
     * 获取法定代表人或者负责人姓名
     *
     * @return fr_name - 法定代表人或者负责人姓名
     */
    public String getFrName() {
        return frName;
    }

    /**
     * 设置法定代表人或者负责人姓名
     *
     * @param frName 法定代表人或者负责人姓名
     */
    public void setFrName(String frName) {
        this.frName = frName;
    }

    /**
     * 获取法定代表人或者负责人性别
     *
     * @return fr_sex - 法定代表人或者负责人性别
     */
    public String getFrSex() {
        return frSex;
    }

    /**
     * 设置法定代表人或者负责人性别
     *
     * @param frSex 法定代表人或者负责人性别
     */
    public void setFrSex(String frSex) {
        this.frSex = frSex;
    }

    /**
     * 获取法定代表人或者负责人证件类型
     *
     * @return fr_cert_type - 法定代表人或者负责人证件类型
     */
    public String getFrCertType() {
        return frCertType;
    }

    /**
     * 设置法定代表人或者负责人证件类型
     *
     * @param frCertType 法定代表人或者负责人证件类型
     */
    public void setFrCertType(String frCertType) {
        this.frCertType = frCertType;
    }

    /**
     * 获取法定代表人或者负责人证件号码
     *
     * @return fr_cert_no - 法定代表人或者负责人证件号码
     */
    public String getFrCertNo() {
        return frCertNo;
    }

    /**
     * 设置法定代表人或者负责人证件号码
     *
     * @param frCertNo 法定代表人或者负责人证件号码
     */
    public void setFrCertNo(String frCertNo) {
        this.frCertNo = frCertNo;
    }

    /**
     * 获取责任的财务负责人姓名
     *
     * @return finance_name - 责任的财务负责人姓名
     */
    public String getFinanceName() {
        return financeName;
    }

    /**
     * 设置责任的财务负责人姓名
     *
     * @param financeName 责任的财务负责人姓名
     */
    public void setFinanceName(String financeName) {
        this.financeName = financeName;
    }

    /**
     * 获取责任的财务负责人性别
     *
     * @return finance_sex - 责任的财务负责人性别
     */
    public String getFinanceSex() {
        return financeSex;
    }

    /**
     * 设置责任的财务负责人性别
     *
     * @param financeSex 责任的财务负责人性别
     */
    public void setFinanceSex(String financeSex) {
        this.financeSex = financeSex;
    }

    /**
     * 获取责任的财务负责人证件类型
     *
     * @return finance_cert_type - 责任的财务负责人证件类型
     */
    public String getFinanceCertType() {
        return financeCertType;
    }

    /**
     * 设置责任的财务负责人证件类型
     *
     * @param financeCertType 责任的财务负责人证件类型
     */
    public void setFinanceCertType(String financeCertType) {
        this.financeCertType = financeCertType;
    }

    /**
     * 获取责任的财务负责人证件号码
     *
     * @return finance_cert_no - 责任的财务负责人证件号码
     */
    public String getFinanceCertNo() {
        return financeCertNo;
    }

    /**
     * 设置责任的财务负责人证件号码
     *
     * @param financeCertNo 责任的财务负责人证件号码
     */
    public void setFinanceCertNo(String financeCertNo) {
        this.financeCertNo = financeCertNo;
    }

    /**
     * 获取负有直接责任的中介机构
     *
     * @return agency - 负有直接责任的中介机构
     */
    public String getAgency() {
        return agency;
    }

    /**
     * 设置负有直接责任的中介机构
     *
     * @param agency 负有直接责任的中介机构
     */
    public void setAgency(String agency) {
        this.agency = agency;
    }

    /**
     * 获取案件性质
     *
     * @return case_nature - 案件性质
     */
    public String getCaseNature() {
        return caseNature;
    }

    /**
     * 设置案件性质
     *
     * @param caseNature 案件性质
     */
    public void setCaseNature(String caseNature) {
        this.caseNature = caseNature;
    }

    /**
     * 获取处罚依据
     *
     * @return pen_basis - 处罚依据
     */
    public String getPenBasis() {
        return penBasis;
    }

    /**
     * 设置处罚依据
     *
     * @param penBasis 处罚依据
     */
    public void setPenBasis(String penBasis) {
        this.penBasis = penBasis;
    }

    /**
     * 获取处罚结果
     *
     * @return pen_result - 处罚结果
     */
    public String getPenResult() {
        return penResult;
    }

    /**
     * 设置处罚结果
     *
     * @param penResult 处罚结果
     */
    public void setPenResult(String penResult) {
        this.penResult = penResult;
    }

    /**
     * 获取发布级别
     *
     * @return publish_level - 发布级别
     */
    public String getPublishLevel() {
        return publishLevel;
    }

    /**
     * 设置发布级别
     *
     * @param publishLevel 发布级别
     */
    public void setPublishLevel(String publishLevel) {
        this.publishLevel = publishLevel;
    }

    /**
     * 获取公布时间
     *
     * @return publish_date - 公布时间
     */
    public String getPublishDate() {
        return publishDate;
    }

    /**
     * 设置公布时间
     *
     * @param publishDate 公布时间
     */
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * 获取撤销日期
     *
     * @return cancel_date - 撤销日期
     */
    public String getCancelDate() {
        return cancelDate;
    }

    /**
     * 设置撤销日期
     *
     * @param cancelDate 撤销日期
     */
    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }

    /**
     * 获取注销原因
     *
     * @return cancel_reason - 注销原因
     */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * 设置注销原因
     *
     * @param cancelReason 注销原因
     */
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    /**
     * 获取实施检查单位
     *
     * @return inspect__org - 实施检查单位
     */
    public String getInspectOrg() {
        return inspectOrg;
    }

    /**
     * 设置实施检查单位
     *
     * @param inspectOrg 实施检查单位
     */
    public void setInspectOrg(String inspectOrg) {
        this.inspectOrg = inspectOrg;
    }

    /**
     * 获取罚款
     *
     * @return fine - 罚款
     */
    public String getFine() {
        return fine;
    }

    /**
     * 设置罚款
     *
     * @param fine 罚款
     */
    public void setFine(String fine) {
        this.fine = fine;
    }

    /**
     * 获取税款、滞纳金、罚款是否全部缴清
     *
     * @return if_pay_off - 税款、滞纳金、罚款是否全部缴清
     */
    public String getIfPayOff() {
        return ifPayOff;
    }

    /**
     * 设置税款、滞纳金、罚款是否全部缴清
     *
     * @param ifPayOff 税款、滞纳金、罚款是否全部缴清
     */
    public void setIfPayOff(String ifPayOff) {
        this.ifPayOff = ifPayOff;
    }

    /**
     * 获取是否缴清税款
     *
     * @return if_pay_tax - 是否缴清税款
     */
    public String getIfPayTax() {
        return ifPayTax;
    }

    /**
     * 设置是否缴清税款
     *
     * @param ifPayTax 是否缴清税款
     */
    public void setIfPayTax(String ifPayTax) {
        this.ifPayTax = ifPayTax;
    }

    /**
     * 获取是否缴清滞纳金
     *
     * @return if_pay_latefee - 是否缴清滞纳金
     */
    public String getIfPayLatefee() {
        return ifPayLatefee;
    }

    /**
     * 设置是否缴清滞纳金
     *
     * @param ifPayLatefee 是否缴清滞纳金
     */
    public void setIfPayLatefee(String ifPayLatefee) {
        this.ifPayLatefee = ifPayLatefee;
    }

    /**
     * 获取是否缴清罚款
     *
     * @return if_pay_fine - 是否缴清罚款
     */
    public String getIfPayFine() {
        return ifPayFine;
    }

    /**
     * 设置是否缴清罚款
     *
     * @param ifPayFine 是否缴清罚款
     */
    public void setIfPayFine(String ifPayFine) {
        this.ifPayFine = ifPayFine;
    }

    /**
     * 获取企业经营情况
     *
     * @return business_state - 企业经营情况
     */
    public String getBusinessState() {
        return businessState;
    }

    /**
     * 设置企业经营情况
     *
     * @param businessState 企业经营情况
     */
    public void setBusinessState(String businessState) {
        this.businessState = businessState;
    }

    /**
     * 获取信用状态
     *
     * @return credit_status - 信用状态
     */
    public String getCreditStatus() {
        return creditStatus;
    }

    /**
     * 设置信用状态
     *
     * @param creditStatus 信用状态
     */
    public void setCreditStatus(String creditStatus) {
        this.creditStatus = creditStatus;
    }

    /**
     * 获取信息来源
     *
     * @return publish_source - 信息来源
     */
    public String getPublishSource() {
        return publishSource;
    }

    /**
     * 设置信息来源
     *
     * @param publishSource 信息来源
     */
    public void setPublishSource(String publishSource) {
        this.publishSource = publishSource;
    }

    /**
     * 获取数据更新时间
     *
     * @return data_time - 数据更新时间
     */
    public String getDataTime() {
        return dataTime;
    }

    /**
     * 设置数据更新时间
     *
     * @param dataTime 数据更新时间
     */
    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    /**
     * 获取数据来源
     *
     * @return data_source - 数据来源
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * 设置数据来源
     *
     * @param dataSource 数据来源
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 获取数据来源代码
     *
     * @return data_source_code - 数据来源代码
     */
    public String getDataSourceCode() {
        return dataSourceCode;
    }

    /**
     * 设置数据来源代码
     *
     * @param dataSourceCode 数据来源代码
     */
    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    /**
     * 获取原文链接
     *
     * @return url - 原文链接
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置原文链接
     *
     * @param url 原文链接
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取主要违法事实
     *
     * @return illeg_fact - 主要违法事实
     */
    public String getIllegFact() {
        return illegFact;
    }

    /**
     * 设置主要违法事实
     *
     * @param illegFact 主要违法事实
     */
    public void setIllegFact(String illegFact) {
        this.illegFact = illegFact;
    }

	@Override
	public String toString() {
		return "T03TaxPunish [id=" + id + ", taxName=" + taxName + ", taxId=" + taxId + ", creditNo=" + creditNo + ", orgCode=" + orgCode + ", regNo=" + regNo + ", regAddress=" + regAddress
				+ ", frName=" + frName + ", frSex=" + frSex + ", frCertType=" + frCertType + ", frCertNo=" + frCertNo + ", financeName=" + financeName + ", financeSex=" + financeSex
				+ ", financeCertType=" + financeCertType + ", financeCertNo=" + financeCertNo + ", agency=" + agency + ", caseNature=" + caseNature + ", penBasis=" + penBasis + ", penResult="
				+ penResult + ", publishLevel=" + publishLevel + ", publishDate=" + publishDate + ", cancelDate=" + cancelDate + ", cancelReason=" + cancelReason + ", inspectOrg=" + inspectOrg
				+ ", fine=" + fine + ", ifPayOff=" + ifPayOff + ", ifPayTax=" + ifPayTax + ", ifPayLatefee=" + ifPayLatefee + ", ifPayFine=" + ifPayFine + ", businessState=" + businessState
				+ ", creditStatus=" + creditStatus + ", publishSource=" + publishSource + ", dataTime=" + dataTime + ", dataSource=" + dataSource + ", dataSourceCode=" + dataSourceCode + ", url="
				+ url + ", createTime=" + createTime + ", updateTime=" + updateTime + ", illegFact=" + illegFact + "]";
	}
    
    
}