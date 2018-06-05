package com.mr.modules.api.service.impl;

import com.google.common.collect.Lists;
import com.mr.common.util.EhCacheUtils;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.date.DateUtil;
import com.mr.framework.core.io.FileUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.TaskStatus;
import com.mr.modules.api.mapper.FinanceMonitorPunishMapper;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.service.SiteService;
import com.mr.modules.api.site.ResourceGroup;
import com.mr.modules.api.site.SiteTask;
import com.mr.modules.api.site.SiteTaskExtend;
import com.mr.modules.api.xls.importfile.FileImportExecutor;
import com.mr.modules.api.xls.importfile.domain.MapResult;
import com.mr.modules.api.xls.importfile.domain.common.Configuration;
import com.mr.modules.api.xls.importfile.domain.common.ImportCell;
import com.mr.modules.api.xls.importfile.exception.FileImportException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by feng on 18-3-16
 */

@Service
@Slf4j
public class SiteServiceImpl implements SiteService {

	@Value("${download-dir}")
	private String downloadDir;

	@Autowired
	protected FinanceMonitorPunishMapper financeMonitorPunishMapper;

	/**
	 * @param groupIndex SiteTask enum index 信息
	 * @param callId     调用ID,系统唯一
	 * @return
	 */
	@Override
	public String startByParams(String groupIndex, String callId, Map mapParams) throws Exception {
		ResourceGroup task = null;

		log.info(String.valueOf(task));
		if (!Objects.isNull(getTask(callId))) {
			log.warn("task exists...");
			return "task exists...";
		}
		String region = (String) mapParams.get("region");
		String date = (String) mapParams.get("publishDate");
		String url = (String) mapParams.get("url");

		FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();

		financeMonitorPunish.setRegion(region);

		financeMonitorPunish.setPublishDate(date);

		financeMonitorPunish.setUrl(url);

		try {
			task = (ResourceGroup) SpringUtils.getBean(groupIndex);
			task.setFinanceMonitorPunish(financeMonitorPunish);
		} catch (Exception e) {
			log.warn(e.getMessage());
			return "SiteTask object instance not found";
		}

		task.setSaveErrKeyWords(String.format("%s/%s occur Data_Saving_Error", groupIndex, callId));
		EhCacheUtils.put(callId, task);
		return TaskStatus.getName(task.start());
	}

	@Override
	public String start(String groupIndex, String callId) throws Exception {
		ResourceGroup task = null;

		log.info(String.valueOf(task));
		if (!Objects.isNull(getTask(callId))) {
			log.warn("task exists...");
			return "task exists...";
		}

		try {
			task = (ResourceGroup) SpringUtils.getBean(groupIndex);
		} catch (Exception e) {
			log.warn(e.getMessage());
			return "SiteTask object instance not found";
		}

		task.setSaveErrKeyWords(String.format("%s/%s occur Data_Saving_Error", groupIndex, callId));
		EhCacheUtils.put(callId, task);
		return TaskStatus.getName(task.start());
	}

	public Boolean isFinish(String callId) throws Exception {
		ResourceGroup task = getTask(callId);
		if (Objects.isNull(getTask(callId))) {
			log.warn("task not exists...");
			return false;
		}

		if (task.isFinish()) {
			SiteTask.putFinishQueue(callId);
			return true;
		}

		return false;
	}

	@Override
	public String getResultCode(String callId) throws Exception {
		if (Objects.isNull(getTask(callId))) {
			log.warn("task not exists...");
			return "task not exists...";
		}

		if (!isFinish(callId)) {
			return TaskStatus.CALL_ING.name;
		}
		return TaskStatus.getName(getTask(callId).getResultCode());
	}

	@Override
	public String getThrowableInfo(String callId) throws Exception {
		if (Objects.isNull(getTask(callId))) {
			log.warn("task not exists...");
			return "task not exists...";
		}

		if (!isFinish(callId)) {
			return "executing...";
		}
		return getTask(callId).getThrowableInfo();
	}

	@Override
	public Boolean delSiteTaskInstance(String callId) throws Exception {
		try {
			if (Objects.isNull(getTask(callId))) {
				log.warn("task not exists...");
				return false;
			}
			SiteTask.delSiteTaskInstance(callId);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private ResourceGroup getTask(String callId) throws Exception {
		return ((ResourceGroup) EhCacheUtils.get(callId));
	}

	public int deleteBySource(String source) {
		if (StringUtils.isEmpty(source))
			return 0;
		return financeMonitorPunishMapper.deleteBySource(source);
	}

	public int deleteByBizKey(String primaryKey) {
		if (StringUtils.isEmpty(primaryKey))
			return 0;
		return financeMonitorPunishMapper.deleteByBizKey(primaryKey);
	}

	public int deleteByUrl(String url) {
		if (StringUtils.isEmpty(url))
			return 0;
		return financeMonitorPunishMapper.deleteByUrl(url);
	}

	public FinanceMonitorPunish selectByBizKey(String primaryKey) {
		if (StringUtils.isEmpty(primaryKey)) {
			return null;
		}
		FinanceMonitorPunish financeMonitorPunish = financeMonitorPunishMapper.selectByBizKey(primaryKey);
		if (StringUtils.isEmpty(financeMonitorPunish)) {
			return null;
		} else {
			return financeMonitorPunish;
		}
	}

	@Override
	public FinanceMonitorPunish fetchOneRecord(String groupIndex, FinanceMonitorPunish financeMonitorPunish) throws Exception {
		ResourceGroup task = null;

		log.info(String.valueOf(task));

		try {
			task = (ResourceGroup) SpringUtils.getBean(groupIndex);
			if (Objects.isNull(task)) {
				return null;
			}
			task.setFinanceMonitorPunish(financeMonitorPunish);
			task.start();
		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}

		return financeMonitorPunish;
	}


	@Override
	public String importData(FileInputStream fis, String uploadFilePath) throws Exception {

		/******接受上传的文件并临时保存*****/

		log.info("uploadFlePath:" + uploadFilePath);
		// 截取上传文件的文件名
		String uploadFileName = uploadFilePath.substring(
				uploadFilePath.lastIndexOf('\\') + 1, uploadFilePath.indexOf('.'));
		log.info("multiReq.getFile():" + uploadFileName);
		// 截取上传文件的后缀
		String uploadFileSuffix = uploadFilePath.substring(
				uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
		log.info("uploadFileSuffix:" + uploadFileSuffix);
		FileOutputStream fos = null;
		//文件全路径名
		String fullPath = downloadDir + File.separator + uploadFilePath;
		try {

			fos = new FileOutputStream(new File(downloadDir + File.separator + uploadFilePath));
			byte[] temp = new byte[1024];
			int i = fis.read(temp);
			while (i != -1) {
				fos.write(temp, 0, temp.length);
				fos.flush();
				i = fis.read(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.warn(uploadFilePath + "upload fail.");
			return "fail";
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		log.info(uploadFilePath + "upload success.");
		/******解析uploadFilePath并导入*****/
		int count = importXlsByNoConfig(fullPath);
		log.info("导入成功条数:{}", count);
		//删除导入的文件
		FileUtil.del(fullPath);
		if (count <= 0) return "fail";
		return "success " + count;
	}

	/**
	 * 把excel导入，变成map
	 *
	 * @throws FileImportException
	 * @throws FileNotFoundException
	 * @throws URISyntaxException
	 */
	private int importXlsByNoConfig(String filePath) throws FileImportException,
			FileNotFoundException,
			URISyntaxException {
		int count = 0;
		File importFile = new File(filePath);
		Configuration configuration = new Configuration();
		try {
			configuration.setStartRowNo(1);
			int i = 1;
			List<ImportCell> importCells = Lists.newArrayList(
//					new ImportCell(i++ ,"PRIMARY_KEY"),
					new ImportCell(i++, "PUNISH_NO"),
					new ImportCell(i++, "PUNISH_TITLE"),
					new ImportCell(i++, "PARTY_INSTITUTION"),
					new ImportCell(i++, "PARTY_PERSON"),
					new ImportCell(i++, "PARTY_PERSON_ID"),
					new ImportCell(i++, "PARTY_PERSON_TITLE"),
					new ImportCell(i++, "PARTY_PERSON_DOMI"),
					new ImportCell(i++, "UNICODE"),
					new ImportCell(i++, "PARTY_CATEGORY"),
					new ImportCell(i++, "DOMICILE"),
					new ImportCell(i++, "LEGAL_REPRESENTATIVE"),
					new ImportCell(i++, "PARTY_SUPPLEMENT"),
					new ImportCell(i++, "COMPANY_FULL_NAME"),
					new ImportCell(i++, "INTERMEDIARY_CATEGORY"),
					new ImportCell(i++, "COMPANY_SHORT_NAME"),
					new ImportCell(i++, "COMPANY_CODE"),
					new ImportCell(i++, "STOCK_CODE"),
					new ImportCell(i++, "STOCK_SHORT_NAME"),
					new ImportCell(i++, "PUNISH_CATEGORY"),
					new ImportCell(i++, "IRREGULARITIES"),
					new ImportCell(i++, "RELATED_LAW"),
					new ImportCell(i++, "RELATED_BOND"),
					new ImportCell(i++, "PUNISH_RESULT"),
					new ImportCell(i++, "PUNISH_RESULT_SUPPLEMENT"),
					new ImportCell(i++, "PUNISH_INSTITUTION"),
					new ImportCell(i++, "PUNISH_DATE"),
					new ImportCell(i++, "REMEDIAL_LIMIT_TIME"),
					new ImportCell(i++, "PUBLISHER"),
					new ImportCell(i++, "PUBLISH_DATE"),
					new ImportCell(i++, "LIST_CLASSIFICATION"),
					new ImportCell(i++, "SUPERVISION_TYPE"),
					new ImportCell(i++, "DETAILS"),
					new ImportCell(i++, "SOURCE"),
					new ImportCell(i++, "URL"),
					new ImportCell(i++, "OBJECT")
//					new ImportCell(i++, "CREATE_TIME"),
//					new ImportCell(i++, "UPDATE_TIME")
			);
			configuration.setImportCells(importCells);
			configuration.setImportFileType(Configuration.ImportFileType.EXCEL);

			MapResult mapResult = (MapResult) FileImportExecutor.importFile(configuration, importFile, importFile.getName());
			List<Map> maps = mapResult.getResult();
			for (Map<String, Object> map : maps) {

				FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
				financeMonitorPunish.setPunishNo(String.valueOf(map.get("PUNISH_NO")));
				financeMonitorPunish.setPunishTitle(String.valueOf(map.get("PUNISH_TITLE")));
				financeMonitorPunish.setPartyInstitution(String.valueOf(map.get("PARTY_INSTITUTION")));
				financeMonitorPunish.setPartyPerson(String.valueOf(map.get("PARTY_PERSON")));
				financeMonitorPunish.setPartyPersonId(String.valueOf(map.get("PARTY_PERSON_ID")));
				financeMonitorPunish.setPartyPersonTitle(String.valueOf(map.get("PARTY_PERSON_TITLE")));
				financeMonitorPunish.setPartyPersonDomi(String.valueOf(map.get("PARTY_PERSON_DOMI")));
				financeMonitorPunish.setUnicode(String.valueOf(map.get("UNICODE")));
				financeMonitorPunish.setPartyCategory(String.valueOf(map.get("PARTY_CATEGORY")));
				financeMonitorPunish.setDomicile(String.valueOf(map.get("DOMICILE")));
				financeMonitorPunish.setLegalRepresentative(String.valueOf(map.get("LEGAL_REPRESENTATIVE")));
				financeMonitorPunish.setPartySupplement(String.valueOf(map.get("PARTY_SUPPLEMENT")));
				financeMonitorPunish.setCompanyFullName(String.valueOf(map.get("COMPANY_FULL_NAME")));
				financeMonitorPunish.setIntermediaryCategory(String.valueOf(map.get("INTERMEDIARY_CATEGORY")));
				financeMonitorPunish.setCompanyShortName(String.valueOf(map.get("COMPANY_SHORT_NAME")));
				financeMonitorPunish.setCompanyCode(String.valueOf(map.get("COMPANY_CODE")));
				financeMonitorPunish.setStockCode(String.valueOf(map.get("STOCK_CODE")));
				financeMonitorPunish.setStockShortName(String.valueOf(map.get("STOCK_SHORT_NAME")));
				financeMonitorPunish.setPunishCategory(String.valueOf(map.get("PUNISH_CATEGORY")));
				financeMonitorPunish.setIrregularities(String.valueOf(map.get("IRREGULARITIES")));
				financeMonitorPunish.setRelatedLaw(String.valueOf(map.get("RELATED_LAW")));
				financeMonitorPunish.setRelatedBond(String.valueOf(map.get("RELATED_BOND")));
				financeMonitorPunish.setPunishResult(String.valueOf(map.get("PUNISH_RESULT")));
				financeMonitorPunish.setPunishResultSupplement(String.valueOf(map.get("PUNISH_RESULT_SUPPLEMENT")));
				financeMonitorPunish.setPunishInstitution(String.valueOf(map.get("PUNISH_INSTITUTION")));
				financeMonitorPunish.setPunishDate(String.valueOf(map.get("PUNISH_DATE")));
				financeMonitorPunish.setRemedialLimitTime(String.valueOf(map.get("REMEDIAL_LIMIT_TIME")));
				financeMonitorPunish.setPublisher(String.valueOf(map.get("PUBLISHER")));
				financeMonitorPunish.setPublishDate(String.valueOf(map.get("PUBLISH_DATE")));
				financeMonitorPunish.setListClassification(String.valueOf(map.get("LIST_CLASSIFICATION")));
				financeMonitorPunish.setSupervisionType(String.valueOf(map.get("SUPERVISION_TYPE")));
				financeMonitorPunish.setDetails(String.valueOf(map.get("DETAILS")));
				financeMonitorPunish.setSource(String.valueOf(map.get("SOURCE")));
				financeMonitorPunish.setUrl(String.valueOf(map.get("URL")));
				financeMonitorPunish.setObject(String.valueOf(map.get("OBJECT")));

				//保存单条数据
				// 通过source查找
				FinanceMonitorPunish originFinanceMonitorPunish = financeMonitorPunishMapper
						.selectByUrl(financeMonitorPunish.getUrl());
				if (Objects.isNull(originFinanceMonitorPunish)) {
					financeMonitorPunish.setCreateTime(new Date());
					financeMonitorPunish.setUpdateTime(new Date());
				} else {
					financeMonitorPunish.setCreateTime(originFinanceMonitorPunish.getCreateTime());
					financeMonitorPunish.setUpdateTime(new Date());
				}

				//通过url先删除，确保不产生多余数据
				financeMonitorPunishMapper.deleteByUrl(financeMonitorPunish.getUrl());

				//保存到数据库
				log.info(SiteTaskExtend.buildFinanceMonitorPunishBizKey(financeMonitorPunish));
				financeMonitorPunishMapper.insert(financeMonitorPunish);
				count++;
			}
		} catch (FileImportException e) {
			log.warn(e.getMessage());
		}
		return count;
	}

	@Override
	public String importICName(FileInputStream fis, String uploadFilePath) throws Exception {

		/******接受上传的文件并临时保存*****/

		log.info("uploadFlePath:" + uploadFilePath);
		// 截取上传文件的文件名
		String uploadFileName = uploadFilePath.substring(
				uploadFilePath.lastIndexOf('\\') + 1, uploadFilePath.indexOf('.'));
		log.info("multiReq.getFile():" + uploadFileName);
		// 截取上传文件的后缀
		String uploadFileSuffix = uploadFilePath.substring(
				uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
		log.info("uploadFileSuffix:" + uploadFileSuffix);
		FileOutputStream fos = null;
		//文件全路径名
		String fullPath = downloadDir + File.separator + uploadFilePath;
		try {

			fos = new FileOutputStream(new File(downloadDir + File.separator + uploadFilePath));
			byte[] temp = new byte[1024];
			int i = fis.read(temp);
			while (i != -1) {
				fos.write(temp, 0, temp.length);
				fos.flush();
				i = fis.read(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.warn(uploadFilePath + "upload fail.");
			return "fail";
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		log.info(uploadFilePath + "upload success.");
		/******解析uploadFilePath并导入*****/
		int count = updateIcNameByXls(fullPath);
		log.info("导入成功条数:{}", count);
		//删除导入的文件
		FileUtil.del(fullPath);
		if (count <= 0) return "fail";
		return "success " + count;
	}

	/**
	 * 把excel导入，变成map, 更新icName
	 *
	 * @throws FileImportException
	 * @throws FileNotFoundException
	 * @throws URISyntaxException
	 */
	private int updateIcNameByXls(String filePath) throws FileImportException,
			FileNotFoundException,
			URISyntaxException {
		int count = 0;
		File importFile = new File(filePath);
		Configuration configuration = new Configuration();
		try {
			configuration.setStartRowNo(1);
			int i = 0;
			List<ImportCell> importCells = Lists.newArrayList(
					new ImportCell(i++, "url"),
					new ImportCell(i++, "icName")
			);
			configuration.setImportCells(importCells);
			configuration.setImportFileType(Configuration.ImportFileType.EXCEL);
			MapResult mapResult = (MapResult) FileImportExecutor.importFile(configuration, importFile, importFile.getName());
			List<Map> maps = mapResult.getResult();
			for (Map<String, Object> map : maps) {
				if(StrUtil.isEmpty(String.valueOf(map.get("url")))) continue;
				FinanceMonitorPunish financeMonitorPunish = financeMonitorPunishMapper.selectByUrl(String.valueOf(map.get("url")));
				financeMonitorPunish.setCompanyFullName(String.valueOf(map.get("icName")));

				Example example = new Example(FinanceMonitorPunish.class);
				example.createCriteria().andEqualTo("url", financeMonitorPunish.getUrl());
				financeMonitorPunishMapper.updateByExample(financeMonitorPunish, example);

				count++;
			}
		} catch (FileImportException e) {
			log.warn(e.getMessage());
		}
		return count;
	}


	public List<FinanceMonitorPunish> selectYesterday() {
		Date begin = DateUtil.beginOfDay(DateUtil.yesterday());
		Date end = DateUtil.beginOfDay(new Date());
		return financeMonitorPunishMapper.selectYesterday(begin, end);
	}

}
