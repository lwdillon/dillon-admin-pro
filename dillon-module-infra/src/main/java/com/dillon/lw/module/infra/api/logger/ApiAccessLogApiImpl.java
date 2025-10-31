package com.dillon.lw.module.infra.api.logger;

import com.dillon.lw.framework.common.biz.infra.logger.ApiAccessLogCommonApi;
import com.dillon.lw.framework.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import com.dillon.lw.module.infra.service.logger.ApiAccessLogService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * API 访问日志的 API 实现类
 *
 * @author liwen
 */
@Service
@Validated
public class ApiAccessLogApiImpl implements ApiAccessLogCommonApi {

    @Resource
    private ApiAccessLogService apiAccessLogService;

    @Override
    public void createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
        apiAccessLogService.createApiAccessLog(createDTO);
    }

}
