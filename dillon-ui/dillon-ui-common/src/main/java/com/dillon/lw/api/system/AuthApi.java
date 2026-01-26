package com.dillon.lw.api.system;


import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;


public interface AuthApi extends BaseApi {

    @Post("/system/auth/login")
   CommonResult<AuthLoginRespVO> login(@Body AuthLoginReqVO authLoginReqVO);

    @Post("/system/auth/logout")
   CommonResult<Boolean> logout();

    /**
     * 获取登录用户的权限信息
     *
     * @return {@link CommonResult }<{@link AuthPermissionInfoRespVO }>
     */
    @Get("/system/auth/get-permission-info")
    CommonResult<AuthPermissionInfoRespVO> getPermissionInfo();
}
