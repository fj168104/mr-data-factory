package com.mr.modules.api.site.instance.creditchinasite.guizhousite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
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
			AdminPunish adminPunish = createDefaultAdminPunish();
			adminPunish.setEnterpriseName(row.getCell(1).getStringCellValue());
			adminPunish.setEnterpriseCode1(row.getCell(3).getStringCellValue());
			adminPunish.setPersonName(row.getCell(4).getStringCellValue());
			adminPunish.setPersonId(row.getCell(5).getStringCellValue());
			adminPunish.setPunishReason(row.getCell(8).getStringCellValue());
			adminPunish.setJudgeAuth(row.getCell(7).getStringCellValue());
			try {
				adminPunish.setUniqueKey(adminPunish.getUrl()+"@"+adminPunish.getEnterpriseName()+"@"+adminPunish.getPersonName()+"@"+adminPunish.getJudgeNo()+"@"+adminPunish.getJudgeAuth());
				saveAdminPunishOne(adminPunish, false);
			}catch (Exception e){
				writeBizErrorLog(url, e.getMessage());
			}
		}
	}

	private AdminPunish createDefaultAdminPunish() {
		AdminPunish adminPunish = new AdminPunish();

		adminPunish.setCreatedAt(new Date());
		adminPunish.setUpdatedAt(new Date());
		adminPunish.setSource("信用中国（贵州）");
		adminPunish.setUrl(url);
		adminPunish.setSubject("");
		adminPunish.setObjectType("01");
		adminPunish.setEnterpriseCode1("");
		adminPunish.setEnterpriseCode2("");
		adminPunish.setEnterpriseCode3("");
		adminPunish.setEnterpriseName("");
		adminPunish.setPersonName("");
		adminPunish.setPersonId("");
		adminPunish.setJudgeNo("");
		adminPunish.setJudgeAuth("");
		return adminPunish;
	}

}
