package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;


public interface LoginLogFeign extends BaseFeignApi {


    @RequestLine("GET /admin-api/system/login-log/page")
    CommonResult<PageResult<LoginLogRespVO>> getLoginLogPage(@QueryMap Map<String, Object> map);

    @RequestLine("DELETE /admin-api/system/login-log/delete?id={id}")
    CommonResult<Boolean> deleteLoginLog(@Param("id") Long id);

    @RequestLine("DELETE /admin-api/system/login-log/clear")
    CommonResult<Boolean> clearLoginLog();

}
