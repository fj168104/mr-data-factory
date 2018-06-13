package com.mr.modules.api.site.instance.creditchinasite;

/**
 * 信用中国站点
 * 
 * @author pxu 2018年6月11日
 */
public enum CreditChinaSite {
	ANHUI("http://www.creditah.gov.cn", "信用中国（安徽）"), //
	SHANDONG("http://www.creditsd.gov.cn", "信用中国（山东）"), //
	GUANGDONG("http://www.gdcredit.gov.cn", "信用中国（广东）") //
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
