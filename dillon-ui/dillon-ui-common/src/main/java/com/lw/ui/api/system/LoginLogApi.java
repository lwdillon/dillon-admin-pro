package com.lw.ui.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;


public interface LoginLogApi extends BaseFeignApi {


    @GET("system/login-log/page")
    Observable<CommonResult<PageResult<LoginLogRespVO>>> getLoginLogPage(@QueryMap Map<String, Object> map);

    @DELETE("system/login-log/delete")
    Observable<CommonResult<Boolean>> deleteLoginLog(@Query("id") Long id);

    @DELETE("system/login-log/clear")
    Observable<CommonResult<Boolean>> clearLoginLog();

}
