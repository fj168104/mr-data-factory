package com.mr.modules.api.service;

import com.mr.modules.api.model.FinanceMonitorPunish;
import org.springframework.util.StringUtils;

/**
 * Created by feng on 18-3-17
 */
public interface SiteService {
	String start(String groupIndex, String callId) throws Exception;

	Boolean isFinish(String callId) throws Exception;

	String getResultCode(String callId) throws Exception;

	String getThrowableInfo(String callId) throws Exception;

	Boolean delSiteTaskInstance(String callId) throws Exception;

	public int deleteByObject(String object);

	public int deleteByBizKey(String primaryKey);

	public FinanceMonitorPunish selectByBizKey(String primaryKey);
}
