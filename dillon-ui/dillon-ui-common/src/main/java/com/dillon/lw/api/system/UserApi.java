package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.user.vo.user.*;
import com.dtflys.forest.annotation.*;

import java.util.List;
import java.util.Map;


public interface UserApi extends BaseApi {

    @Post("system/user/create")
    CommonResult<Long> createUser(@Body UserSaveReqVO reqVO);

    @Put("system/user/update")
    CommonResult<Boolean> updateUser(@Body UserSaveReqVO reqVO);

    @Delete("system/user/delete")
    CommonResult<Boolean> deleteUser(@Query("id") Long id);

    @Put("system/user/update-password")
    CommonResult<Boolean> updateUserPassword(@Body UserUpdatePasswordReqVO reqVO);

    @Put("system/user/update-status")
    CommonResult<Boolean> updateUserStatus(@Body UserUpdateStatusReqVO reqVO);

    @Get("system/user/page")
    CommonResult<PageResult<UserRespVO>> getUserPage(@Query Map<String, Object> queryMap);

    @Get("system/user/simple-list")
    CommonResult<List<UserSimpleRespVO>> getSimpleUserList();

    @Get("system/user/get")
    CommonResult<UserRespVO> getUser(@Query("id") Long id);


}
