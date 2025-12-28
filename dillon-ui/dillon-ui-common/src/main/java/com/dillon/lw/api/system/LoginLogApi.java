package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.dtflys.forest.annotation.Delete;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;

import java.util.Map;


public interface LoginLogApi extends BaseApi {


    @Get("/system/login-log/page")
    CommonResult<PageResult<LoginLogRespVO>> getLoginLogPage(@Query Map<String, Object> map);

    @Delete("/system/login-log/delete")
    CommonResult<Boolean> deleteLoginLog(@Query("id") Long id);

    @Delete("/system/login-log/clear")
    CommonResult<Boolean> clearLoginLog();

}
