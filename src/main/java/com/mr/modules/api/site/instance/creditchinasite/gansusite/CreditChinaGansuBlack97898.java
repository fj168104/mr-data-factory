package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;

import lombok.extern.slf4j.Slf4j;

/**
 * 信用中国（甘肃）-安监总局公告2017年第三批安全生产失信联合惩戒“黑名单”
 * 
 * http://www.gscredit.gov.cn/blackList/97898.jhtml
 * 
 * 
 * @author pxu 2018年7月4日
 */
@Slf4j
@Component("creditchina-gansu-black-97898")
@Scope("prototype")
public class CreditChinaGansuBlack97898 extends SiteTaskExtend_CreditChina {

	private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/97898.jhtml";

	/**
	 * 抓取页面数据
	 */
	@Override
	protected String execute() throws Throwable {
		discreditBlacklistMapper.deleteAllByUrl(url);// 删除该URL下的全部数据
		log.info("开始抓取url={}", url);

		String fileName = downLoadFile("http://upload.xh08.cn/2017/1207/1512613488863.xls");
		log.debug("下载完成，fileName：{}", fileName);
		String[] columeNames = { "序号", "单位名称", "注册地址", "统一社会信用代码", "主要责任人", "身份证号", "失信行为简况", "信息报送机关", "纳入理由" };
		List<Map<String, Object>> list = importFromXls(fileName, columeNames);
		log.debug("xls文件转换完成，list size:{}", list.size());

		DiscreditBlacklist discreditBlacklist = null;
		boolean bStart = false;
		int iCount = 0;
		forList: for (Map<String, Object> map : list) {
			forMap: for (String key : map.keySet()) {
				Object obj = map.get(key);
				String value = Objects.isNull(obj) ? "" : String.valueOf(obj);
				log.debug("key=" + key + "|value=" + value);
				if (value != null) {
					value = value.trim();// 去除两端空格
				}
				if ("序号".equals(key) && "序号".equals(value)) {// 判断标题行
					bStart = true;
					continue forList;// 直接跳出本循环，继续到LIST中的下个循环
				}
				if (bStart) {
					if ("序号".equals(key)) {// 每条记录的开始
						discreditBlacklist = createDefaultDiscreditBlacklist();
						discreditBlacklist.setSubject("安监总局安全生产黑名单");
						discreditBlacklist.setDiscreditType("2017年第三批安全生产失信联合惩戒“黑名单”单位及其人员名单");
						continue forMap;
					}
					if (discreditBlacklist != null) {
						if ("单位名称".equals(key)) {
							discreditBlacklist.setEnterpriseName(value);
							continue forMap;
						}
						if ("注册地址".equals(key)) {
							continue forMap;
						}
						if ("统一社会信用代码".equals(key)) {
							if (value.contains("组织机构代码：") || value.contains("注册号：")) {
								String[] values = value.replace("组织机构代码：", "@!@组织机构代码：").replace("注册号：", "@!@注册号：").split("@!@");
								for (String v : values) {
									if (v.contains("组织机构代码：")) {
										discreditBlacklist.setEnterpriseCode3(v.replace("组织机构代码：", "").trim());
									}
									if (v.contains("注册号：")) {
										discreditBlacklist.setEnterpriseCode2(v.replace("注册号：", "").trim());
									}
								}
							} else {
								discreditBlacklist.setEnterpriseCode1(value);
							}
							continue forMap;
						}
						if ("主要责任人".equals(key)) {
							discreditBlacklist.setPersonName(value);
							continue forMap;
						}
						if ("身份证号".equals(key)) {
							discreditBlacklist.setPersonId(value);
							continue forMap;
						}
						if ("失信行为简况".equals(key)) {
							discreditBlacklist.setDiscreditAction(value);
							continue forMap;
						}
						if ("信息报送机关".equals(key)) {
							discreditBlacklist.setJudgeAuth(value);
							continue forMap;
						}
						if ("纳入理由".equals(key)) {
							discreditBlacklist.setPunishReason(value);
							continue forMap;
						}
					}
				}
				continue forMap;
			}
			if (discreditBlacklist != null) {
				iCount++;
				discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl() + "@" + discreditBlacklist.getEnterpriseName() + "@" + discreditBlacklist.getPersonName() + "@" + discreditBlacklist.getJudgeNo() + "@" + discreditBlacklist.getJudgeAuth());
				discreditBlacklistMapper.insert(discreditBlacklist);
			}
		}
		log.info("抓取url={}结束！共插入{}条记录", url, iCount);
		return null;
	}

	private DiscreditBlacklist createDefaultDiscreditBlacklist() {
		Date nowDate = new Date();
		DiscreditBlacklist blackList = new DiscreditBlacklist();
		blackList.setCreatedAt(nowDate);// 本条记录创建时间
		blackList.setUpdatedAt(nowDate);// 本条记录最后更新时间
		blackList.setSource(CreditChinaSite.GANSU.getSiteName());// 数据来源
		blackList.setSubject("");// 主题
		blackList.setUrl(url);// url
		blackList.setObjectType("01");// 主体类型: 01-企业 02-个人。默认为企业
		blackList.setEnterpriseName("");// 企业名称
		blackList.setEnterpriseCode1("");// 统一社会信用代码
		blackList.setEnterpriseCode2("");// 营业执照注册号
		blackList.setEnterpriseCode3("");// 组织机构代码
		blackList.setEnterpriseCode4("");// 税务登记号
		blackList.setPersonName("");// 法定代表人/负责人姓名|负责人姓名
		blackList.setPersonId("");// 法定代表人身份证号|负责人身份证号
		blackList.setDiscreditType("");// 失信类型
		blackList.setDiscreditAction("");// 失信行为
		blackList.setPunishReason("");// 列入原因
		blackList.setPunishResult("");// 处罚结果
		blackList.setJudgeNo("国家安全生产监督管理总局公告2017年第16号");// 执行文号
		blackList.setJudgeDate("");// 执行时间
		blackList.setJudgeAuth("");// 判决机关
		blackList.setPublishDate("2017/12/04");// 发布日期
		blackList.setStatus("");// 当前状态
		return blackList;
	}
}
