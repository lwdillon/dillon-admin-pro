package com.dillon.lw.api.infra;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import com.dtflys.forest.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface JobApi extends BaseApi {


    // "创建定时任务")
    @Post("infra/job/create")
    CommonResult<Long> createJob(@Body JobSaveReqVO createReqVO);

    // "更新定时任务")
    @Put("infra/job/update")
    CommonResult<Boolean> updateJob(@Body JobSaveReqVO updateReqVO);

    // "更新定时任务的状态")
    @Put("infra/job/update-status")
    CommonResult<Boolean> updateJobStatus(@Query("id") Long id, @Query("status") Integer status);

    // "删除定时任务")
    @Delete("infra/job/delete")
    CommonResult<Boolean> deleteJob(@Query("id") Long id);

    // "触发定时任务")
    @Put("infra/job/trigger")
    CommonResult<Boolean> triggerJob(@Query("id") Long id);

    // "同步定时任务")
    @Post("infra/job/sync")
    CommonResult<Boolean> syncJob();

    // "获得定时任务")
    @Get("infra/job/get")
    CommonResult<JobRespVO> getJob(@Query("id") Long id);

    // "获得定时任务分页")
    @Get("infra/job/page")
    CommonResult<PageResult<JobRespVO>> getJobPage(@Query Map<String, Object> pageVO);


    // "获得定时任务的下 n 次执行时间")
    @Get("infra/job/get_next_times")
    CommonResult<List<LocalDateTime>> getJobNextTimes(
            @Query("id") Long id,
            @Query("count") Integer count);

}
