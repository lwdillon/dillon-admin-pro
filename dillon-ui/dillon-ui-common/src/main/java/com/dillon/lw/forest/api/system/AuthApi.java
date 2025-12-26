package com.dillon.lw.forest.api.system;


import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("system/auth/login")
    Observable<CommonResult<AuthLoginRespVO>> login(@Body AuthLoginReqVO authLoginReqVO);

    @POST("system/auth/logout")
    Observable<CommonResult<Boolean>> logout();

    /**
     * 获取登录用户的权限信息
     *
     * @return {@link CommonResult }<{@link AuthPermissionInfoRespVO }>
     */
    @GET("system/auth/get-permission-info")
    Observable<CommonResult<AuthPermissionInfoRespVO>> getPermissionInfo();
}
