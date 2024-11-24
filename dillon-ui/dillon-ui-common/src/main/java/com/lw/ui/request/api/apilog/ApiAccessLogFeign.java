package com.lw.ui.request.api.apilog;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface ApiAccessLogFeign extends BaseFeignApi {


    // "获得API 访问日志分页")
    @RequestLine("GET /infra/api-access-log/page")
     CommonResult<PageResult<ApiAccessLogRespVO>> getApiAccessLogPage(@QueryMap Map<String,Object> pageReqVO);


}
