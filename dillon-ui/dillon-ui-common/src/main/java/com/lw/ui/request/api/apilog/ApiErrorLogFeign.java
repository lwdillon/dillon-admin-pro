package com.lw.ui.request.api.apilog;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface ApiErrorLogFeign extends BaseFeignApi {


    // "更新 API 错误日志的状态")
  
    @RequestLine("PUT /admin-api/infra/api-error-log/update-status?id={id}&processStatus={processStatus}")
    CommonResult<Boolean> updateApiErrorLogProcess(@Param("id") Long id,
                                                   @Param("processStatus") Integer processStatus);

   // "获得 API 错误日志分页")
    @RequestLine("GET /admin-api/infra/api-error-log/page")
    CommonResult<PageResult<ApiErrorLogRespVO>> getApiErrorLogPage(@QueryMap Map<String,Object> pageReqVO);


}
