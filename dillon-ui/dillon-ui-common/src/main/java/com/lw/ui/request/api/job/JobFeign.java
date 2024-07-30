package com.lw.ui.request.api.job;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface JobFeign extends BaseFeignApi {


    // "创建定时任务")
    @RequestLine("POST /admin-api/infra/job/create")
    CommonResult<Long> createJob(JobSaveReqVO createReqVO);

    // "更新定时任务")
    @RequestLine("PUT /admin-api/infra/job/update")
    CommonResult<Boolean> updateJob(JobSaveReqVO updateReqVO);

    // "更新定时任务的状态")
    @RequestLine("PUT /admin-api/infra/job/update-status?id={id}&status={status}")
    CommonResult<Boolean> updateJobStatus(@Param("id") Long id, @Param("status") Integer status);

    // "删除定时任务")
    @RequestLine("DELETE /admin-api/infra/job/delete?id={id}")
    CommonResult<Boolean> deleteJob(@Param("id") Long id);

    // "触发定时任务")
    @RequestLine("PUT /admin-api/infra/job/trigger?id={id}")
    CommonResult<Boolean> triggerJob(@Param("id") Long id);

    // "同步定时任务")
    @RequestLine("POST /admin-api/infra/job/sync")
    CommonResult<Boolean> syncJob();

    // "获得定时任务")
    @RequestLine("GET /admin-api/infra/job/get?id={id}")
    CommonResult<JobRespVO> getJob(@Param("id") Long id);

    // "获得定时任务分页")
    @RequestLine("GET /admin-api/infra/job/page")
    CommonResult<PageResult<JobRespVO>> getJobPage(@QueryMap Map<String,Object> pageVO);



    // "获得定时任务的下 n 次执行时间")
    @RequestLine("GET /admin-api/infra/job/get_next_times?id={id}&count={count}")
    CommonResult<List<LocalDateTime>> getJobNextTimes(
            @Param("id") Long id,
            @Param("count") Integer count);

}
