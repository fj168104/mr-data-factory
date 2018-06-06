package com.mr.modules.api.site;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.io.FileUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.modules.api.mapper.FinanceMonitorPunishMapper;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.xls.export.FileExportor;
import com.mr.modules.api.xls.export.domain.common.ExportCell;
import com.mr.modules.api.xls.export.domain.common.ExportConfig;
import com.mr.modules.api.xls.export.domain.common.ExportResult;
import com.mr.modules.api.xls.importfile.FileImportExecutor;
import com.mr.modules.api.xls.importfile.domain.MapResult;
import com.mr.modules.api.xls.importfile.domain.common.Configuration;
import com.mr.modules.api.xls.importfile.domain.common.ImportCell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by zjxu on 18-4-16
 */

@Slf4j
@Component
public abstract class SiteTaskExtendSub extends SiteTaskExtend {


	/**
	 * 保存抓取结果
	 *
	 * @return ture 保存成功		false 保存失败（如系统中已存在该记录）
	 * 一主键为条件，筛选数据
	 */
	@Override
	protected Boolean saveOne(FinanceMonitorPunish financeMonitorPunish, Boolean isForce) {
		String primaryKey = buildFinanceMonitorPunishBizKey(financeMonitorPunish);
		log.debug("primaryKey:" + primaryKey);
		if (isForce || Objects.isNull(financeMonitorPunishMapper.selectByBizKey(primaryKey))) {
			setICName(financeMonitorPunish);
			insertOrUpdate(financeMonitorPunish);
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 保存单条记录
	 *
	 * @param financeMonitorPunish
	 * @return
	 */
	private FinanceMonitorPunish insertOrUpdate(FinanceMonitorPunish financeMonitorPunish) {
		if (StringUtils.isEmpty(financeMonitorPunish.getPrimaryKey())) {
			buildFinanceMonitorPunishBizKey(financeMonitorPunish);
		}

		financeMonitorPunishMapper.deleteByBizKey(financeMonitorPunish.getPrimaryKey());
		//设置createTime
		if (StringUtils.isEmpty(financeMonitorPunish.getCreateTime())) {
			financeMonitorPunish.setCreateTime(new Date());
			financeMonitorPunish.setUpdateTime(new Date());
		}
		try {
			if (StrUtil.isEmpty(financeMonitorPunish.getCompanyFullName())) {
				financeMonitorPunish.setCompanyFullName(financeMonitorPunish.getPartyInstitution());
			}
			financeMonitorPunishMapper.insert(filterPlace(financeMonitorPunish));
		} catch (Exception e) {
			log.error(keyWords + ">>>" + e.getMessage());
		}
		return financeMonitorPunish;
	}
	protected String downLoadFile(String targetUri, String fName) throws URISyntaxException, IOException {
		String fileName = targetUri.substring(targetUri.lastIndexOf("/") + 1);
		if (!Objects.isNull(fName))
			fileName = fName;
//		String targetUri = "http://www.neeq.com.cn/uploads/1/file/public/201802/20180226182405_lc6vjyqntd.pdf";
		// 小文件
		RequestEntity requestEntity = RequestEntity.get(new URI(targetUri)).build();
		ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
		byte[] downloadContent = responseEntity.getBody();
		OutputStream out = new FileOutputStream(OCRUtil.DOWNLOAD_DIR + File.separator + fileName);
		out.write(downloadContent);
		out.close();
		return fileName;

		// 大文件
		//		FileOutputStream f=new FileOutputStream()
		//		ResponseExtractor<ResponseEntity<File>> responseExtractor = new ResponseExtractor<ResponseEntity<File>>() {
		//			@Override
		//			public ResponseEntity<File> extractData(ClientHttpResponse response) throws IOException {
		//				File rcvFile = File.createTempFile("rcvFile", "zip");
		//				FileCopyUtils.copy(response.getBody(), new FileOutputStream(rcvFile));
		//				return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(rcvFile);
		//			}
		//		};
		//		ResponseEntity<File> getFile = restTemplate.execute(targetUri, HttpMethod.GET, (RequestCallback) null, responseExtractor);

	}
	/**
	 * 去除字符串中最后一个指定的字符串
	 */
	public String delFinallyString(String strOld,String delString){
		String strNew = strOld;
		if(strOld.length()>0&&strOld.trim().endsWith(delString)){
			strNew = strOld.substring(0,strOld.length()-1);
		}
		return strNew.toString();
	}

}
