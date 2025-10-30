package com.lw.ui.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;


public interface RoleApi extends BaseFeignApi {

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
