package com.mr.modules.api.site.instance.creditchinasite.xizangsite;

import com.mr.common.IdempotentOperator;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.colligationsite.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * @auther 1.信用中国（西藏）
 * 1、西藏自治区法院失信被执行人名单（企业）
 * 2.http://www.creditxizang.gov.cn/xyxz/ueditor/jsp/upload/file/20161128/1480314111565008355.pdf
 */
@Slf4j
@Component("xizang_fyqy")
@Scope("prototype")
public class Xizang_fyqy extends SiteTaskExtend_CreditChina {
	String url = "http://www.creditxizang.gov.cn/xyxz/ueditor/jsp/upload/file/20161128/1480314111565008355.pdf";

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
			DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
			String sRow = info.replace("\n", "");
			discreditBlacklist.setEnterpriseCode1(sRow.substring(sRow.indexOf("("), sRow.indexOf("号") + 1));
			discreditBlacklist.setEnterpriseName(sRow.substring(sRow.indexOf("号") + 1, sRow.indexOf("公司") + 2));
			if (i == 1) {
				discreditBlacklist.setEnterpriseCode2("74191508—0");
				discreditBlacklist.setPersonName("吴忠和");
				discreditBlacklist.setJudgeAuth("拉萨市堆龙德庆县人民法院");
				try{
					discreditBlacklist.setUniqueKey(MD5Util.encode(discreditBlacklist.getUrl()+"@"+discreditBlacklist.getEnterpriseName()+"@"+discreditBlacklist.getPersonName()+"@"+discreditBlacklist.getJudgeNo()+"@"+discreditBlacklist.getJudgeAuth()));
					saveDisneycreditBlackListOne(discreditBlacklist, false);
				}catch (Exception e){
					writeBizErrorLog(url, e.getMessage());
				}
				continue;
			}
			if (i == 2) {
				discreditBlacklist.setEnterpriseCode2("68684241—2");
				discreditBlacklist.setPersonName("攀维超");
				discreditBlacklist.setJudgeAuth("拉萨市堆龙德庆县人民法院");
				try{
					discreditBlacklist.setUniqueKey(MD5Util.encode(discreditBlacklist.getUrl()+"@"+discreditBlacklist.getEnterpriseName()+"@"+discreditBlacklist.getPersonName()+"@"+discreditBlacklist.getJudgeNo()+"@"+discreditBlacklist.getJudgeAuth()));
					saveDisneycreditBlackListOne(discreditBlacklist, false);
				}catch (Exception e){
					writeBizErrorLog(url, e.getMessage());
				}
				continue;
			}
			if (i == 3) {
				discreditBlacklist.setEnterpriseCode2("78910651—4");
				discreditBlacklist.setPersonName("方相胜");
				discreditBlacklist.setJudgeAuth("拉萨市堆龙德庆县人民法院");
				try{
					discreditBlacklist.setUniqueKey(MD5Util.encode(discreditBlacklist.getUrl()+"@"+discreditBlacklist.getEnterpriseName()+"@"+discreditBlacklist.getPersonName()+"@"+discreditBlacklist.getJudgeNo()+"@"+discreditBlacklist.getJudgeAuth()));
					saveDisneycreditBlackListOne(discreditBlacklist, false);
				}catch (Exception e){
					writeBizErrorLog(url, e.getMessage());
				}
				continue;
			}
			if (i == 4) {
				discreditBlacklist.setEnterpriseCode2("68682020—5");
				discreditBlacklist.setPersonName("万冬");
				discreditBlacklist.setJudgeAuth("拉萨市堆龙德庆县人民法院");
				try{
					discreditBlacklist.setUniqueKey(MD5Util.encode(discreditBlacklist.getUrl()+"@"+discreditBlacklist.getEnterpriseName()+"@"+discreditBlacklist.getPersonName()+"@"+discreditBlacklist.getJudgeNo()+"@"+discreditBlacklist.getJudgeAuth()));
					saveDisneycreditBlackListOne(discreditBlacklist, false);
				}catch (Exception e){
					writeBizErrorLog(url, e.getMessage());
				}
				continue;
			}
			if (i == 22) {
				discreditBlacklist.setEnterpriseCode2("68683480—X");
				discreditBlacklist.setPersonName("陈林");
				discreditBlacklist.setJudgeAuth("昌都市中级人民法院");
				try{
					discreditBlacklist.setUniqueKey(MD5Util.encode(discreditBlacklist.getUrl()+"@"+discreditBlacklist.getEnterpriseName()+"@"+discreditBlacklist.getPersonName()+"@"+discreditBlacklist.getJudgeNo()+"@"+discreditBlacklist.getJudgeAuth()));
					saveDisneycreditBlackListOne(discreditBlacklist, false);
				}catch (Exception e){
					writeBizErrorLog(url, e.getMessage());
				}
				continue;
			}

			String[] sis = sRow.substring(sRow.indexOf("公司") + 2).trim().split("\\s+");
			if(sis.length < 3)  continue;
			discreditBlacklist.setEnterpriseCode2(sis[0]);
			discreditBlacklist.setPersonName(sis[1]);
			discreditBlacklist.setJudgeAuth(sis[2] + "法院");
			try{
				discreditBlacklist.setUniqueKey(MD5Util.encode(discreditBlacklist.getUrl()+"@"+discreditBlacklist.getEnterpriseName()+"@"+discreditBlacklist.getPersonName()+"@"+discreditBlacklist.getJudgeNo()+"@"+discreditBlacklist.getJudgeAuth()));
				saveDisneycreditBlackListOne(discreditBlacklist, false);
			}catch (Exception e){
				writeBizErrorLog(url, e.getMessage());
			}
		}
	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist() {
		DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();

		discreditBlacklist.setCreatedAt(new Date());
		discreditBlacklist.setUpdatedAt(new Date());
		discreditBlacklist.setSource("信用中国（西藏）");
		discreditBlacklist.setUrl(url);
		discreditBlacklist.setSubject("西藏自治区法院失信被执行人名单（企业）");
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
