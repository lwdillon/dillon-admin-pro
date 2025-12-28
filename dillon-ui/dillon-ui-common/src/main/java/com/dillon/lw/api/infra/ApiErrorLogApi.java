package com.dillon.lw.api.infra;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Put;
import com.dtflys.forest.annotation.Query;

import java.util.Map;

public interface ApiErrorLogApi extends BaseApi {


    // "更新 API 错误日志的状态")

    @Put("infra/api-error-log/update-status")
    CommonResult<Boolean> updateApiErrorLogProcess(@Query("id") Long id,
                                                               @Query("processStatus") Integer processStatus);

    // "获得 API 错误日志分页")
    @Get("infra/api-error-log/page")
    CommonResult<PageResult<ApiErrorLogRespVO>> getApiErrorLogPage(@Query Map<String, Object> pageReqVO);


}
