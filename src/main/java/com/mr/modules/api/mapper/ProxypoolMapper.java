package com.mr.modules.api.mapper;

import com.mr.common.base.mapper.BaseMapper;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.model.Proxypool;

import java.util.List;

/**
 * @Auther 18-4-10. zjxu
 */
public interface ProxypoolMapper extends BaseMapper<Proxypool> {
    /**
     * 通过业务主键删除
     *
     * @param ipaddress
     * @return
     */
    int deleteByBizKey(String ipaddress);

    /**
     * 查询所有IP池记录
     * @return
     */
    List<Proxypool> selectProxyPool();
}