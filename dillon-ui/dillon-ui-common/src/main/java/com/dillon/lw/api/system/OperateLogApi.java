package com.dillon.lw.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;


public interface OperateLogApi {

    @GET("system/operate-log/page")
    Observable<CommonResult<PageResult<OperateLogRespVO>>> pageOperateLog(@QueryMap Map<String, Object> map);

    @DELETE("system/operate-log/delete?id={id}")
    Observable<CommonResult<Boolean>> deleteOperateLog(@Query("id") Long id);

    @DELETE("system/operate-log/clear")
    Observable<CommonResult<Boolean>> clearOperateLog();

}
