package com.mr.modules.api.site.instance.creditchinasite.xizangsite;

import com.mr.common.IdempotentOperator;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.colligationsite.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * @auther 1.信用中国（西藏）
 * 1、拉萨关区进出口失信企业名单
 * 2.http://www.credithebei.gov.cn:8082/was5/web/detail?record=%d&channelid=284249
 */
@Slf4j
@Component("xizang_jck")
@Scope("prototype")
public class Xizang_jck extends SiteTaskExtend_CreditChina {
	String url = "http://www.creditxizang.gov.cn/xyxz/ueditor/jsp/upload/file/20161128/1480314185755050447.pdf";

	protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);

	@Override
	protected String executeOne() throws Throwable {
		return super.executeOne();
	}

	@Override
	protected String execute() throws Throwable {
		try {
			extractContext(url);
		} catch (Exception e) {
			writeBizErrorLog(url, e.getMessage());
		}
		return null;
	}

	private String getPdfText(String fileName) {
		return new IdempotentOperator<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return ocrUtil.getTextFromPdf(fileName);
			}
		}).execute();
	}

	/**
	 * 获取网页内容
	 * 海关注册编码、企业注册名称、失信原因、备注
	 */
	public void extractContext(String url) throws Exception {
		String fileName = downLoadFile(url);
		String content = getPdfText(fileName);
		String s1 = content.substring(content.indexOf("备注") + 2).trim();
		String[] infos = s1.split("\\s+");
		DiscreditBlacklist discreditBlacklist = null;
		int i = 1;
		int j = 0;
		for (String info : infos) {
			if (info.trim().equals(String.valueOf(i))) {
				discreditBlacklist = createDefaultDiscreditBlacklist();
				i++;
				j = 1;
				continue;
			}
			if(j == 1){
				discreditBlacklist.setEnterpriseCode1(info.trim());
				j++;
				continue;
			}

			if(j == 2){
				discreditBlacklist.setEnterpriseName(info);
				j++;
				continue;
			}

			if(j == 3){
				discreditBlacklist.setPunishReason(info);
				if("公司".equals(discreditBlacklist.getPunishReason())){
					discreditBlacklist.setEnterpriseName(discreditBlacklist.getEnterpriseName() + "公司");
					discreditBlacklist.setPunishReason("");
				}
				try{
					discreditBlacklist.setUniqueKey(MD5Util.encode(discreditBlacklist.getUrl()+"@"+discreditBlacklist.getEnterpriseName()+"@"+discreditBlacklist.getPersonName()+"@"+discreditBlacklist.getJudgeNo()+"@"+discreditBlacklist.getJudgeAuth()));
					saveDisneycreditBlackListOne(discreditBlacklist, false);
				}catch (Exception e){
					writeBizErrorLog(url, e.getMessage());
				}
				j++;
				continue;
			}

		}
	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist() {
		DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();

		discreditBlacklist.setCreatedAt(new Date());
		discreditBlacklist.setUpdatedAt(new Date());
		discreditBlacklist.setSource("信用中国（西藏）");
		discreditBlacklist.setUrl(url);
		discreditBlacklist.setSubject("拉萨关区进出口失信企业名单");
		discreditBlacklist.setObjectType("01");
		discreditBlacklist.setEnterpriseCode1("");
		discreditBlacklist.setEnterpriseCode2("");
		discreditBlacklist.setEnterpriseCode3("");
		discreditBlacklist.setEnterpriseName("");
		discreditBlacklist.setPersonName("");
		discreditBlacklist.setPersonId("");
		discreditBlacklist.setJudgeNo("");
		discreditBlacklist.setJudgeAuth("");
		return discreditBlacklist;
	}

}
