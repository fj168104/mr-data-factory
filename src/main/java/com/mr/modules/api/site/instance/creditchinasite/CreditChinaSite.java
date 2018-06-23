package com.mr.modules.api.site.instance.creditchinasite;

/**
 * 信用中国站点
 * 
 * @author pxu 2018年6月11日
 */
public enum CreditChinaSite {
	ANHUI("http://www.creditah.gov.cn", "信用中国（安徽）"), //
	SHANDONG("http://www.creditsd.gov.cn", "信用中国（山东）"), //
	GUANGDONG("http://www.gdcredit.gov.cn", "信用中国（广东）"), //
	HENAN("http://www.xyhn.gov.cn", "信用中国（河南）"), //
	HUNAN("http://www.credithunan.gov.cn", "信用中国（湖南）"), //
	SHANGHAI("http://www.shcredit.gov.cn", "信用中国（上海）"), //
	SHANANXI("http://www.sxcredit.gov.cn", "信用中国（陕西）"), //
	SHANXI("http://www.creditsx.gov.cn", "信用中国（山西）"), //
	GANSU("http://www.gscredit.gov.cn", "信用中国（甘肃）"), //
	HEBEI("http://www.credithebei.gov.cn", "信用中国（河北）"), //
	NEIMENGGU("http://nmgcredit.gov.cn", "信用中国（内蒙古）"), //
	LIAONING("http://www.lncredit.gov.cn", "信用中国（辽宁）"), //
	JILIN("http://www.jilincredit.gov.cn", "信用中国（吉林）"), //
	JIANGSU("http://www.jscredit.gov.cn", "信用中国（江苏）"), //
	ZHEJIANG("http://www.zjcredit.gov.cn", "信用中国（浙江）"),//
	;

	private CreditChinaSite(String baseUrl, String siteName) {
		this.baseUrl = baseUrl;
		this.siteName = siteName;
	}

	/**
	 * 站点 基础url
	 */
	private String baseUrl;
	/**
	 * 站点 名称
	 */
	private String siteName;

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getSiteName() {
		return siteName;
	}
}
