package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.RequestLine;

import java.util.Set;

/**
 * 权限 Controller，提供赋予用户、角色的权限的 API 接口
 *
 * @permissionor 芋道源码
 */

public interface PermissionFeign extends BaseFeignApi {
    @RequestLine("GET /admin-api/system/permission/list-role-menus?roleId={roleId}")
    CommonResult<Set<Long>> getRoleMenuList(@Param("roleId") Long roleId);

    @RequestLine("POST /admin-api/system/permission/assign-role-menu")
    CommonResult<Boolean> assignRoleMenu(PermissionAssignRoleMenuReqVO reqVO);

    @RequestLine("POST /admin-api/system/permission/assign-role-data-scope")
    CommonResult<Boolean> assignRoleDataScope(PermissionAssignRoleDataScopeReqVO reqVO);

    @RequestLine("GET /admin-api/system/permission/list-user-roles?userId={userId}")
    CommonResult<Set<Long>> listAdminRoles(@Param("userId") Long userId);

    @RequestLine("POST /admin-api/system/permission/assign-user-role")
    CommonResult<Boolean> assignUserRole(PermissionAssignUserRoleReqVO reqVO);
}
