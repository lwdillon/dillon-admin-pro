package com.dillon.lw.forest.api.infra;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface JobApi {


    // "创建定时任务")
    @POST("infra/job/create")
    Observable<CommonResult<Long>> createJob(@Body JobSaveReqVO createReqVO);

    // "更新定时任务")
    @PUT("infra/job/update")
    Observable<CommonResult<Boolean>> updateJob(@Body JobSaveReqVO updateReqVO);

    // "更新定时任务的状态")
    @PUT("infra/job/update-status")
    Observable<CommonResult<Boolean>> updateJobStatus(@Query("id") Long id, @Query("status") Integer status);

    // "删除定时任务")
    @DELETE("infra/job/delete")
    Observable<CommonResult<Boolean>> deleteJob(@Query("id") Long id);

    // "触发定时任务")
    @PUT("infra/job/trigger")
    Observable<CommonResult<Boolean>> triggerJob(@Query("id") Long id);

    // "同步定时任务")
    @POST("infra/job/sync")
    Observable<CommonResult<Boolean>> syncJob();

    // "获得定时任务")
    @GET("infra/job/get")
    Observable<CommonResult<JobRespVO>> getJob(@Query("id") Long id);

    // "获得定时任务分页")
    @GET("infra/job/page")
    Observable<CommonResult<PageResult<JobRespVO>>> getJobPage(@QueryMap Map<String, Object> pageVO);


    // "获得定时任务的下 n 次执行时间")
    @GET("infra/job/get_next_times")
    Observable<CommonResult<List<LocalDateTime>>> getJobNextTimes(
            @Query("id") Long id,
            @Query("count") Integer count);

}
