package com.mr.modules.api.mapper;

import com.mr.common.base.mapper.BaseMapper;
import com.mr.modules.api.model.ScrapyData;

public interface ScrapyDataMapper extends BaseMapper<ScrapyData> {

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
}