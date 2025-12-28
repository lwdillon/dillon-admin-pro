package com.dillon.lw.api.infra;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.job.vo.log.JobLogRespVO;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;

import java.util.Map;

public interface JobLogApi extends BaseApi {


    //"获得定时任务日志")
    @Get("infra/job-log/get")
    CommonResult<JobLogRespVO> getJobLog(@Query("id") Long id);

    //"获得定时任务日志分页")
    @Get("infra/job-log/page")
    CommonResult<PageResult<JobLogRespVO>> getJobLogPage(@Query Map<String, Object> pageVO);


}