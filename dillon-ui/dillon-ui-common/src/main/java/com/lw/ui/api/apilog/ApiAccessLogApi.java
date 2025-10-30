package com.lw.ui.api.apilog;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface ApiAccessLogApi {


    // "获得API 访问日志分页")
    @GET("infra/api-access-log/page")
    Observable<CommonResult<PageResult<ApiAccessLogRespVO>>> getApiAccessLogPage(@QueryMap Map<String, Object> pageReqVO);


}
