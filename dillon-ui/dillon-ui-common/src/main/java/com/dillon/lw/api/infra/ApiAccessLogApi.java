package com.dillon.lw.api.infra;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;

import java.util.Map;

public interface ApiAccessLogApi extends BaseApi {


    // "获得API 访问日志分页")
    @Get("infra/api-access-log/page")
    CommonResult<PageResult<ApiAccessLogRespVO>> getApiAccessLogPage(@Query Map<String, Object> pageReqVO);


}
