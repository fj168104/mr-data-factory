package com.mr.modules.api.mapper;

import com.mr.common.base.mapper.BaseMapper;
import com.mr.modules.api.model.AdminPunish;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminPunishMapper extends BaseMapper<AdminPunish> {

	/**
	 * 通过链接地址删除全部
	 * 
	 * @param url
	 * @return
	 */
	int deleteAllByUrl(String url);

	/**
	 * 判断记录此url地址是否已经存在
	 * 
	 * @param url 网站
	 * @param enterpriseName
	 *            企业名称
	 * @param personName
	 *            代表人名称
	 * @param judgeNo
	 *            文号
	 * @param judgeAuth
	 *            处罚机构
	 *
	 * @return
	 */
	List<AdminPunish> selectByUrl(@Param("url") String url, @Param("enterpriseName") String enterpriseName, @Param("personName") String personName, @Param("judgeNo") String judgeNo, @Param("judgeAuth") String judgeAuth);

	/**
	 * 判断记录此url地址是否已经存在
	 * 
	 * @param url
	 * @param enterpriseName
	 * @param personName
	 * @param judgeNo
	 * @return
	 */
	int deleteByUrl(@Param("url") String url, @Param("enterpriseName") String enterpriseName, @Param("personName") String personName, @Param("judgeNo") String judgeNo, @Param("judgeAuth") String judgeAuth);

	/**
	 * 通过判决书编号+企业名称(或个人名称)获得记录数
	 * 
	 * @param judgeNo
	 * @return
	 */
	int selectCountByJudgeNoAndName(@Param("objectType") String objectType, @Param("judgeNo") String judgeNo, @Param("name") String name);
}