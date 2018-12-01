package com.mr.modules.api.site.instance.creditchinasite.guizhousite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.colligationsite.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * @auther 1.信用中国（贵州）
 * 1、2018年第一批安全生产失信联合惩戒“黑名单”单位及其人员名单
 * 2、https://www.creditchina.gov.cn/home/lianhejiangchegn/201804/W020180403628062321524.xls
 */
@Slf4j
@Component("guizhou_11239")
@Scope("prototype")
public class Guizhou_11239 extends SiteTaskExtend_CreditChina {
	String url = "https://www.creditchina.gov.cn/home/lianhejiangchegn/201804/W020180403628062321524.xls";

	@Value("${download-dir}")
	private String downloadDir;

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

	/**
	 * 获取网页内容
	 * 行政处罚决定书文号、案件名称、处罚类别、处罚事由、处罚依据、行政相对人名称、组织机构代码、工商登记码、税务登记号、
	 * 法定代表人居民身份证号、法定代表人姓名、处罚结果、处罚生效期、处罚机关、当前状态、地方编码、备注、信息提供部门、数据报送时间
	 */
	public void extractContext(String url) throws Exception {
		String fName = "W020180403628062321524.xls";
		WebClient wc = new WebClient(BrowserVersion.CHROME);
		wc.getOptions().setUseInsecureSSL(true);
		Page pageXLS = wc.getPage(url);
		String fileName = saveFile(pageXLS, fName);

		Workbook workbook = new HSSFWorkbook(new FileInputStream(downloadDir + File.separator + fileName));
		Sheet sheet = workbook.getSheetAt(0);
		int phyRow = sheet.getPhysicalNumberOfRows();
		int startRow = 5;
		for (int t = startRow; t < phyRow; t++) {
			Row row = sheet.getRow(t);
			DiscreditBlacklist discreditBlacklist = createDefaultDiscreditBlacklist();
			discreditBlacklist.setEnterpriseName(row.getCell(1).getStringCellValue());
			discreditBlacklist.setEnterpriseCode1(row.getCell(3).getStringCellValue());
			discreditBlacklist.setPersonName(row.getCell(4).getStringCellValue());
			discreditBlacklist.setPersonId(row.getCell(5).getStringCellValue());
			discreditBlacklist.setPunishReason(row.getCell(8).getStringCellValue());
			discreditBlacklist.setJudgeAuth(row.getCell(7).getStringCellValue());
			try {
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
		discreditBlacklist.setSource("信用中国（贵州）");
		discreditBlacklist.setUrl(url);
		discreditBlacklist.setSubject("2018年第一批安全生产失信联合惩戒“黑名单”单位及其人员名单");
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
