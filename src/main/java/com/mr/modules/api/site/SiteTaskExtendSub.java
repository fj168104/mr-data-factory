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


}
