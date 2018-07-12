package com.mr.modules.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mr.common.base.mapper.BaseMapper;
import com.mr.modules.api.model.DiscreditBlacklist;

public interface DiscreditBlacklistMapper extends BaseMapper<DiscreditBlacklist> {
	
	/**
	 * 通过链接地址删除全部
	 * @param url
	 * @return
	 */
	int deleteAllByUrl(String url);
	
	/**
	 * 通过URL查询记录数
	 * @param url
	 * @return
	 */
	int selectCountByUrl(String url);
	
	/**
	 * 通过主题+对象类型+判决书文号查询记录条数
	 * @param subject
	 * @param objectType
	 * @param judgeNo
	 * @return
	 */
	int selectCountBySubjectAndJudegNo(@Param("subject") String subject, @Param("objectType") String objectType, @Param("judgeNo") String judgeNo);

    /**
     * 判断记录此url地址是否已经存在
     * @param url
     * @param enterpriseName 企业名称
     * @param personName     代表人名称
     * @param judgeNo        文号
     * @return
     */
    List<DiscreditBlacklist> selectByUrl(@Param("url") String url , @Param("enterpriseName") String enterpriseName
            , @Param("personName") String personName
            , @Param("judgeNo") String judgeNo,@Param("judgeAuth")String judgeAuth );

    /**
     * 判断记录此url地址是否已经存在
     * @param url
     * @param enterpriseName
     * @param personName
     * @param judgeNo
     * @return
     */
    int deleteByUrl(@Param("url") String url , @Param("enterpriseName") String enterpriseName
            , @Param("personName") String personName
            , @Param("judgeNo") String judgeNo,@Param("judgeAuth")String judgeAuth);
}