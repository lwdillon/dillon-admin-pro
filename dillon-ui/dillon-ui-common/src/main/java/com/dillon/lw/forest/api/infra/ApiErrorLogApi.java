package com.dillon.lw.forest.api.infra;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface ApiErrorLogApi {


    // "更新 API 错误日志的状态")

    @PUT("infra/api-error-log/update-status")
    Observable<CommonResult<Boolean>> updateApiErrorLogProcess(@Query("id") Long id,
                                                               @Query("processStatus") Integer processStatus);

    // "获得 API 错误日志分页")
    @GET("infra/api-error-log/page")
    Observable<CommonResult<PageResult<ApiErrorLogRespVO>>> getApiErrorLogPage(@QueryMap Map<String, Object> pageReqVO);


}
