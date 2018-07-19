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
 * 1、西藏自治区通报15家拖欠民工工资失信企业
 * 2、http://www.creditxizang.gov.cn/xyxz//staticPage/D56C0CC1ECD247419A499F59A85584A1.html?colId=1
 */
@Slf4j
@Component("xizang_tq")
@Scope("prototype")
public class Xizang_tq extends SiteTaskExtend_CreditChina {
	String url = "http://www.creditxizang.gov.cn/xyxz//staticPage/D56C0CC1ECD247419A499F59A85584A1.html?colId=1";

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
	 * 事件背景、事件调查、受罚企业名称、处罚详情、防范措施
	 */
	public void extractContext(String url) throws Exception {
		String[] qyNames = {"四川亚泰建设有限公司",
				"四川昌达建筑有限公司",
				"湖南湘达路桥建筑有限公司",
				"江西中林建设集团有限公司",
				"陕西联腾建工集团有限公司",
				"陕西建工集团第六建筑工程有限公司",
				"四川川新建筑工程有限公司",
				"四川省科茂建筑工程有限公司",
				"西藏宏绩建设有限公司",
				"西藏嘉煜建设有限公司",
				"西藏智黎工程建设有限公司",
				"绵阳佳成建设有限公司",
				"中太建设集团股份有限",
				"四川众发劳务有限公司",
				"北京城建第七公司"};
		for(String qyName : qyNames){
			AdminPunish adminPunish = createDefaultAdminPunish();
			adminPunish.setEnterpriseName(qyName);
			adminPunish.setPunishReason("拖欠民工工资");
			adminPunish.setPunishResult("对企业和相关从业人员，自治区住建厅要求各地各部门要在政府采购、招投标、履约担保、资质审核、融资贷款、市场准入等方面依法依规予以限制，" +
					"使失信企业或人员在西藏自治区范围内“一处失信、处处受限”，提高企业失信违法成本，从而形成治理欠薪的高压态势，在社会上起到震慑作用。");
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
		adminPunish.setObjectType("01");
		adminPunish.setEnterpriseCode1("");
		adminPunish.setEnterpriseCode2("");
		adminPunish.setEnterpriseCode3("");
		adminPunish.setPersonName("");
		adminPunish.setPersonId("");
		return adminPunish;
	}

}
