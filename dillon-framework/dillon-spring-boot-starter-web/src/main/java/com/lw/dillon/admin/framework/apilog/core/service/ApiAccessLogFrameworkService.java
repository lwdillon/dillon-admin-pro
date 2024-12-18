package com.lw.dillon.admin.framework.apilog.core.service;

import com.lw.dillon.admin.module.infra.api.logger.dto.ApiAccessLogCreateReqDTO;

/**
 * API 访问日志 Framework Service 接口
 *
 * @author 芋道源码
 */
public interface ApiAccessLogFrameworkService {

    /**
     * 创建 API 访问日志
     *
     * @param reqDTO API 访问日志
     */
    void createApiAccessLog(ApiAccessLogCreateReqDTO reqDTO);

}
