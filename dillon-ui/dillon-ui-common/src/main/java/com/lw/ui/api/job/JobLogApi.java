package com.lw.ui.api.job;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.log.JobLogRespVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface JobLogApi {


    //"获得定时任务日志")
    @GET("infra/job-log/get")
    Observable<CommonResult<JobLogRespVO>> getJobLog(@Query("id") Long id);

    //"获得定时任务日志分页")
    @GET("infra/job-log/page")
    Observable<CommonResult<PageResult<JobLogRespVO>>> getJobLogPage(@QueryMap Map<String, Object> pageVO);


}