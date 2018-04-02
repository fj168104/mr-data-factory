package com.mr.modules.api.site;

import com.mr.modules.api.model.FinanceMonitorPunish;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by feng on 18-3-16
 */
public interface ResourceGroup{
	/**
	 * 返回调度结果
	 */
	 Integer start();

	/**
	 * 是否执行结束
	 */
	Boolean isFinish();

	/**
	 * 执行结果
	 */
	Integer getResultCode();

	/**
	 * 返回错误信息
	 */
	String getThrowableInfo();

	/**
	 *单条抓取处理
	 */
	void setFinanceMonitorPunish(FinanceMonitorPunish oneFinanceMonitorPunish);

}
