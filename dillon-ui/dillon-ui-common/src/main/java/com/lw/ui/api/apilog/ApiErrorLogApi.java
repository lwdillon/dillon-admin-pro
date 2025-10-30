package com.lw.ui.api.apilog;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface ApiErrorLogApi extends BaseFeignApi {


    // "更新 API 错误日志的状态")

    @PUT("infra/api-error-log/update-status")
    Observable<CommonResult<Boolean>> updateApiErrorLogProcess(@Query("id") Long id,
                                                               @Query("processStatus") Integer processStatus);

    // "获得 API 错误日志分页")
    @GET("infra/api-error-log/page")
    Observable<CommonResult<PageResult<ApiErrorLogRespVO>>> getApiErrorLogPage(@QueryMap Map<String, Object> pageReqVO);


}
