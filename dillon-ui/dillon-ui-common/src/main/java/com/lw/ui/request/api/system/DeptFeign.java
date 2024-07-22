package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;

public interface DeptFeign extends BaseFeignApi {


    @RequestLine("POST /admin-api/system/dept/create")
    CommonResult<Long> createDept( DeptSaveReqVO createReqVO);

    @RequestLine("PUT /admin-api/system/dept/update")
    CommonResult<Boolean> updateDept( DeptSaveReqVO updateReqVO);

    @RequestLine("DELETE /admin-api/system/dept/delete?id={id}")
    CommonResult<Boolean> deleteDept(@Param("id") Long id);

    @RequestLine("GET /admin-api/system/dept/list")
    CommonResult<List<DeptRespVO>> getDeptList(@QueryMap DeptListReqVO reqVO);

    @RequestLine("GET /admin-api/system/dept/simple-list")
    CommonResult<List<DeptSimpleRespVO>> getSimpleDeptList();

    @RequestLine("GET /admin-api/system/dept/get?id={id}")
    CommonResult<DeptRespVO> getDept(@Param("id") Long id);

}
