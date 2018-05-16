package com.mr.modules.api.mapper;

import com.mr.common.base.mapper.BaseMapper;
import com.mr.modules.api.model.FinanceMonitorPunish;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FinanceMonitorPunishMapper extends BaseMapper<FinanceMonitorPunish> {
	/**
	 * 通过业务主键删除
	 *
	 * @param primaryKey
	 * @return
	 */
	int deleteByBizKey(String primaryKey);

	/**
	 * 通过业务主键查找
	 */
	FinanceMonitorPunish selectByBizKey(String primaryKey);

	/**
	 * 通过链接地址删除
	 * @param url
	 * @return
	 */
	int deleteByUrl(String url);

	/**
	 * 通过链接地址查找
	 * @param url
	 * @return
	 */
	FinanceMonitorPunish selectByUrl(String url);

	/**
	 * 通过业务来源删除
	 * @return
	 */
	int deleteBySource(String source);


	List<FinanceMonitorPunish> selectYesterday(@Param("begin") Date begin, @Param("end") Date end);
}