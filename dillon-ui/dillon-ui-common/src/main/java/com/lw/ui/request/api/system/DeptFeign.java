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


    @RequestLine("POST /system/dept/create")
    CommonResult<Long> createDept( DeptSaveReqVO createReqVO);

    @RequestLine("PUT /system/dept/update")
    CommonResult<Boolean> updateDept( DeptSaveReqVO updateReqVO);

    @RequestLine("DELETE /system/dept/delete?id={id}")
    CommonResult<Boolean> deleteDept(@Param("id") Long id);

    @RequestLine("GET /system/dept/list")
    CommonResult<List<DeptRespVO>> getDeptList(@QueryMap DeptListReqVO reqVO);

    @RequestLine("GET /system/dept/simple-list")
    CommonResult<List<DeptSimpleRespVO>> getSimpleDeptList();

    @RequestLine("GET /system/dept/get?id={id}")
    CommonResult<DeptRespVO> getDept(@Param("id") Long id);

}
