package com.dillon.lw.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Set;

/**
 * 权限 Controller，提供赋予用户、角色的权限的 API 接口
 *
 * @permissionor 芋道源码
 */

public interface PermissionApi {
    @GET("system/permission/list-role-menus")
    Observable<CommonResult<Set<Long>>> getRoleMenuList(@Query("roleId") Long roleId);

    @POST("system/permission/assign-role-menu")
    Observable<CommonResult<Boolean>> assignRoleMenu(@Body PermissionAssignRoleMenuReqVO reqVO);

    @POST("system/permission/assign-role-data-scope")
    Observable<CommonResult<Boolean>> assignRoleDataScope(@Body PermissionAssignRoleDataScopeReqVO reqVO);

    @GET("system/permission/list-user-roles")
    Observable<CommonResult<Set<Long>>> listAdminRoles(@Query("userId") Long userId);

    @POST("system/permission/assign-user-role")
    Observable<CommonResult<Boolean>> assignUserRole(@Body PermissionAssignUserRoleReqVO reqVO);
}
