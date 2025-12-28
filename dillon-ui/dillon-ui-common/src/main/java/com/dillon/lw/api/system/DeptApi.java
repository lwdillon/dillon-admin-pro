package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dtflys.forest.annotation.*;

import java.util.List;
import java.util.Map;

public interface DeptApi extends BaseApi {


    @Post("system/dept/create")
    CommonResult<Long> createDept(@Body DeptSaveReqVO createReqVO);

    @Put("system/dept/update")
    CommonResult<Boolean> updateDept(@Body DeptSaveReqVO updateReqVO);

    @Delete("system/dept/delete")
    CommonResult<Boolean> deleteDept(@Query("id") Long id);

    @Get("system/dept/list")
    CommonResult<List<DeptRespVO>> getDeptList(@Query Map<String, Object> reqVO);

    @Get("system/dept/simple-list")
    CommonResult<List<DeptSimpleRespVO>> getSimpleDeptList();

    @Get("system/dept/get")
    CommonResult<DeptRespVO> getDept(@Query("id") Long id);

}
