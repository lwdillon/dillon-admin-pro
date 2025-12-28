package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.dtflys.forest.annotation.*;

import java.util.List;
import java.util.Map;


public interface RoleApi extends BaseApi {

    @Post("system/role/create")
    CommonResult<Long> createRole(@Body RoleSaveReqVO createReqVO);

    @Put("system/role/update")
    CommonResult<Boolean> updateRole(@Body RoleSaveReqVO updateReqVO);

    @Delete("system/role/delete")
    CommonResult<Boolean> deleteRole(@Query("id") Long id);

    @Get("system/role/get")
    CommonResult<RoleRespVO> getRole(@Query("id") Long id);

    @Get("system/role/page")
    CommonResult<PageResult<RoleRespVO>> getRolePage(@Query Map<String, Object> queryMay);

    @Get("system/role/simple-list")
    CommonResult<List<RoleRespVO>> getSimpleRoleList();


}
