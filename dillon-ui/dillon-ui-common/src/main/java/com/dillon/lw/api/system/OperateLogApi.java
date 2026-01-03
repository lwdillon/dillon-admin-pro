package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.dtflys.forest.annotation.Delete;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;

import java.util.Map;


public interface OperateLogApi extends BaseApi {

    @Get("system/operate-log/page")
    CommonResult<PageResult<OperateLogRespVO>> pageOperateLog(@Query Map<String, Object> map);

    @Delete("system/operate-log/delete")
    CommonResult<Boolean> deleteOperateLog(@Query("id") Long id);

    @Delete("system/operate-log/clear")
    CommonResult<Boolean> clearOperateLog();

}
