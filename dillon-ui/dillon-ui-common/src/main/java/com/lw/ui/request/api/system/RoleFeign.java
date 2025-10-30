package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;
import java.util.Map;


public interface RoleFeign extends BaseFeignApi {

    @RequestLine("POST /system/role/create")
    CommonResult<Long> createRole( RoleSaveReqVO createReqVO);

    @RequestLine("PUT /system/role/update")
    CommonResult<Boolean> updateRole(RoleSaveReqVO updateReqVO);

    @RequestLine("DELETE /system/role/delete?id={id}")
    CommonResult<Boolean> deleteRole(@Param("id") Long id);

    @RequestLine("GET /system/role/get?id={id}")
    CommonResult<RoleRespVO> getRole(@Param("id") Long id);

    @RequestLine("GET /system/role/page")
    CommonResult<PageResult<RoleRespVO>> getRolePage(@QueryMap Map<String,Object> queryMay);

    @RequestLine("GET /system/role/simple-list")
    CommonResult<List<RoleRespVO>> getSimpleRoleList();


}
