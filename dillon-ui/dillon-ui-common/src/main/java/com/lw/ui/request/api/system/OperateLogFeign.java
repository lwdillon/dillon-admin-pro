package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;
import java.util.concurrent.CompletableFuture;


public interface OperateLogFeign extends BaseFeignApi {


    @RequestLine("GET /admin-api/system/operate-log/page")
    CommonResult<PageResult<OperateLogRespVO>> pageOperateLog(@QueryMap Map<String, Object> map);


    @RequestLine("DELETE /admin-api/system/operate-log/delete?id={id}")
    CommonResult<Boolean> deleteOperateLog(@Param("id") Long id);

    @RequestLine("DELETE /admin-api/system/operate-log/clear")
    CommonResult<Boolean> clearOperateLog();

}
