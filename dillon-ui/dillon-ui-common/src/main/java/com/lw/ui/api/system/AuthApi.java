package com.lw.ui.api.system;


import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
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
