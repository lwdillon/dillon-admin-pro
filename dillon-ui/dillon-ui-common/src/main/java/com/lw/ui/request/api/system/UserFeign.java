package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.*;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;
import java.util.Map;


public interface UserFeign extends BaseFeignApi {

    @RequestLine("POST /admin-api/system/user/create")
    CommonResult<Long> createUser(UserSaveReqVO reqVO);

    @RequestLine("PUT /admin-api/system/user/update")
    CommonResult<Boolean> updateUser(UserSaveReqVO reqVO);

    @RequestLine("DELETE /admin-api/system/user/delete?id={id}")
    CommonResult<Boolean> deleteUser(@Param("id") Long id);

    @RequestLine("PUT /admin-api/system/user/update-password")
    CommonResult<Boolean> updateUserPassword(UserUpdatePasswordReqVO reqVO);

    @RequestLine("PUT /admin-api/system/user/update-status")
    CommonResult<Boolean> updateUserStatus(UserUpdateStatusReqVO reqVO);

    @RequestLine("GET /admin-api/system/user/page")
    CommonResult<PageResult<UserRespVO>> getUserPage(@QueryMap Map<String,Object> queryMap);

    @RequestLine("GET /admin-api/system/user/simple-list")
    CommonResult<List<UserSimpleRespVO>> getSimpleUserList();

    @RequestLine("GET /admin-api/system/user/get?id={id}")
    CommonResult<UserRespVO> getUser(@Param("id") Long id);


}
