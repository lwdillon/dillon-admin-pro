package com.dillon.lw.forest.api.infra;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface ApiAccessLogApi {


    // "获得API 访问日志分页")
    @GET("infra/api-access-log/page")
    Observable<CommonResult<PageResult<ApiAccessLogRespVO>>> getApiAccessLogPage(@QueryMap Map<String, Object> pageReqVO);


}
