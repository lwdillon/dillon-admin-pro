package com.lw.ui.request.api.system;


import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.RequestLine;

public interface AuthFeign extends BaseFeignApi {

    @RequestLine("POST /admin-api/system/auth/login")
    CommonResult<AuthLoginRespVO> login(AuthLoginReqVO authLoginReqVO);

    @RequestLine("POST /admin-api/system/auth/logout")
    CommonResult<Boolean> logout();

    /**
     * 获取登录用户的权限信息
     *
     * @return {@link CommonResult }<{@link AuthPermissionInfoRespVO }>
     */
    @RequestLine("GET /admin-api/system/auth/get-permission-info")
    CommonResult<AuthPermissionInfoRespVO> getPermissionInfo();
}
