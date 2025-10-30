package com.lw.ui.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface DeptApi extends BaseFeignApi {


    @POST("system/dept/create")
    Observable<CommonResult<Long>> createDept(@Body DeptSaveReqVO createReqVO);

    @PUT("system/dept/update")
    Observable<CommonResult<Boolean>> updateDept(@Body DeptSaveReqVO updateReqVO);

    @DELETE("system/dept/delete")
    Observable<CommonResult<Boolean>> deleteDept(@Query("id") Long id);

    @GET("system/dept/list")
    Observable<CommonResult<List<DeptRespVO>>> getDeptList(@QueryMap Map<String,Object> reqVO);

    @GET("system/dept/simple-list")
    Observable<CommonResult<List<DeptSimpleRespVO>>> getSimpleDeptList();

    @GET("system/dept/get")
    Observable<CommonResult<DeptRespVO>> getDept(@Query("id") Long id);

}
