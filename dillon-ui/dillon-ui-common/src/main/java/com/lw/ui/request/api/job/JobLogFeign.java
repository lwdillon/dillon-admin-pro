package com.lw.ui.request.api.job;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.log.JobLogRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface JobLogFeign extends BaseFeignApi {


    //"获得定时任务日志")
    @RequestLine("GET /admin-api/infra/job-log/get?id={id}")
    CommonResult<JobLogRespVO> getJobLog(@Param("id") Long id);

    //"获得定时任务日志分页")
    @RequestLine("GET /admin-api/infra/job-log/page")
    CommonResult<PageResult<JobLogRespVO>> getJobLogPage(@QueryMap Map<String,Object> pageVO);



}