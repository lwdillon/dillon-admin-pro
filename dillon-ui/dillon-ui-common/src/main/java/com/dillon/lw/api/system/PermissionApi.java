package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;
import com.dtflys.forest.annotation.*;

import java.util.Set;

/**
 * 权限 Controller，提供赋予用户、角色的权限的 API 接口
 *
 * @permissionor dillon
 */

public interface PermissionApi extends BaseApi {
    @Get("system/permission/list-role-menus")
    CommonResult<Set<Long>> getRoleMenuList(@Query("roleId") Long roleId);

    @Post("system/permission/assign-role-menu")
    CommonResult<Boolean> assignRoleMenu(@Body PermissionAssignRoleMenuReqVO reqVO);

    @Post("system/permission/assign-role-data-scope")
    CommonResult<Boolean> assignRoleDataScope(@Body PermissionAssignRoleDataScopeReqVO reqVO);

    @Get("system/permission/list-user-roles")
    CommonResult<Set<Long>> listAdminRoles(@Query("userId") Long userId);

    @Post("system/permission/assign-user-role")
    CommonResult<Boolean> assignUserRole(@Body PermissionAssignUserRoleReqVO reqVO);
}
