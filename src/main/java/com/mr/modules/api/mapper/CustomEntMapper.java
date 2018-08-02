package com.mr.modules.api.mapper;

import com.mr.common.base.mapper.BaseMapper;
import com.mr.modules.api.model.CustomEnt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomEntMapper extends BaseMapper<CustomEnt> {
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
     * 判断记录此url地址是否已经存在
     * @param url
     * @param enterpriseName 企业名称
     * @param startDate        发布时间
     * @return
     */
    List<CustomEnt> selectByUrl(@Param("url") String url , @Param("enterpriseName") String enterpriseName , @Param("startDate") String startDate);

    /**
     * 判断记录此url地址是否已经存在
     * @param url
     * @param enterpriseName
     * @param startDate
     * @return
     */
    int deleteByUrl(@Param("url") String url , @Param("enterpriseName") String enterpriseName , @Param("startDate") String startDate);
}