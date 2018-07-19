package com.mr.modules.api.site.instance.creditchinasite.xizangsite;

import com.mr.common.IdempotentOperator;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * @auther 1.信用中国（西藏）
 * 1、西藏自治区法院失信被执行人名单（自然人）
 * 2.http://www.creditxizang.gov.cn/xyxz/ueditor/jsp/upload/file/20161128/1480314150019004187.pdf
 */
@Slf4j
@Component("xizang_fygr")
@Scope("prototype")
public class Xizang_fygr extends SiteTaskExtend_CreditChina {
	String url = "http://www.creditxizang.gov.cn/xyxz/ueditor/jsp/upload/file/20161128/1480314150019004187.pdf";

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

		String s1 = content.substring(content.indexOf("承办法院") + 4, content.indexOf("西藏自治区高级人民法院")).replace("\n", "").trim();
		String[] infos = s1.split("法院");
		for (int i = 0; i < infos.length; i++) {
			String info = infos[i];
			AdminPunish adminPunish = createDefaultAdminPunish();
			String sRow = info.replace("\n", "、");
			adminPunish.setEnterpriseCode1(sRow.substring(sRow.indexOf("("), sRow.indexOf("号") + 1));

			String[] sis = sRow.substring(sRow.indexOf("号") + 1).trim().split("\\s+");
			if(sis.length < 3)  continue;
			adminPunish.setPersonName(sis[0]);
			adminPunish.setPersonId(sis[1]);
			adminPunish.setJudgeAuth(sis[2] + "法院");
			saveAdminPunishOne(adminPunish, false);
		}
	}

	private AdminPunish createDefaultAdminPunish() {
		AdminPunish adminPunish = new AdminPunish();

		adminPunish.setCreatedAt(new Date());
		adminPunish.setUpdatedAt(new Date());
		adminPunish.setSource("信用西藏");
		adminPunish.setSubject("");
		adminPunish.setUrl(url);
		adminPunish.setObjectType("02");
		adminPunish.setEnterpriseCode1("");
		adminPunish.setEnterpriseCode2("");
		adminPunish.setEnterpriseCode3("");
		adminPunish.setPersonName("");
		adminPunish.setPersonId("");
		return adminPunish;
	}

}
