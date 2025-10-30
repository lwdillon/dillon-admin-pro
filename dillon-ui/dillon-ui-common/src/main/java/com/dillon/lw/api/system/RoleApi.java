package com.dillon.lw.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;


public interface RoleApi {

    @POST("system/role/create")
    Observable<CommonResult<Long>> createRole(@Body RoleSaveReqVO createReqVO);

    @PUT("system/role/update")
    Observable<CommonResult<Boolean>> updateRole(@Body RoleSaveReqVO updateReqVO);

    @DELETE("system/role/delete")
    Observable<CommonResult<Boolean>> deleteRole(@Query("id") Long id);

    @GET("system/role/get")
    Observable<CommonResult<RoleRespVO>> getRole(@Query("id") Long id);

    @GET("system/role/page")
    Observable<CommonResult<PageResult<RoleRespVO>>> getRolePage(@QueryMap Map<String, Object> queryMay);

    @GET("system/role/simple-list")
    Observable<CommonResult<List<RoleRespVO>>> getSimpleRoleList();


}
