package com.mr.modules.api.service.impl;

import com.google.common.base.Strings;
import com.mr.common.util.EhCacheUtils;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.TaskStatus;
import com.mr.modules.api.mapper.FinanceMonitorPunishMapper;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.service.SiteService;
import com.mr.modules.api.site.ResourceGroup;
import com.mr.modules.api.site.SiteTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created by feng on 18-3-16
 */

@Service
@Slf4j
public class SiteServiceImpl implements SiteService {

	@Autowired
	protected FinanceMonitorPunishMapper financeMonitorPunishMapper;

	/**
	 * @param groupIndex SiteTask enum index 信息
	 * @param callId     调用ID,系统唯一
	 * @return
	 */
	@Override
	public String start(String groupIndex, String callId) throws Exception {
		ResourceGroup task = null;

		log.info(String.valueOf(task));
		if (!Objects.isNull(getTask(callId))) {
			log.warn("task exists...");
			return "task exists...";
		}

		try {
			task = (ResourceGroup)SpringUtils.getBean(groupIndex);
		} catch (Exception e) {
			log.error(e.getMessage());
			return "SiteTask object instance not found";
		}

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

	public int deleteByObject(String object){
		if(StringUtils.isEmpty(object))
			return 0;
		return financeMonitorPunishMapper.deleteByObject(object);
	}

	public int deleteByBizKey(String primaryKey){
		if(StringUtils.isEmpty(primaryKey))
			return 0;
		return financeMonitorPunishMapper.deleteByBizKey(primaryKey);
	}

	public FinanceMonitorPunish selectByBizKey(String primaryKey){
		if(StringUtils.isEmpty(primaryKey)){
			return null;
		}
		FinanceMonitorPunish financeMonitorPunish = financeMonitorPunishMapper.selectByBizKey(primaryKey);
		if(StringUtils.isEmpty(financeMonitorPunish)){
			return null;
		}else {
			return financeMonitorPunish;
		}
	}

}
