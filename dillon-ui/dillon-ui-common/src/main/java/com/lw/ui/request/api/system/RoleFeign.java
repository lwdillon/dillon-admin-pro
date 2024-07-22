package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;


public interface RoleFeign extends BaseFeignApi {

    @PostMapping("/create")
    @RequestLine("POST /admin-api/system/role/create")
    CommonResult<Long> createRole( RoleSaveReqVO createReqVO);

    @PutMapping("/update")
    @RequestLine("PUT /admin-api/system/role/update")
    CommonResult<Boolean> updateRole(RoleSaveReqVO updateReqVO);

    @DeleteMapping("/delete")
    @RequestLine("DELETE /admin-api/system/role/delete?id={id}")
    CommonResult<Boolean> deleteRole(@Param("id") Long id);

    @GetMapping("/get")
    @RequestLine("GET /admin-api/system/role/get?id={id}")
    CommonResult<RoleRespVO> getRole(@Param("id") Long id);

    @RequestLine("GET /admin-api/system/role/page")
    CommonResult<PageResult<RoleRespVO>> getRolePage(@QueryMap Map<String,Object> queryMay);

    @RequestLine("GET /admin-api/system/role/simple-list")
    CommonResult<List<RoleRespVO>> getSimpleRoleList();


}
