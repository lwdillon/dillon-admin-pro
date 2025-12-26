package com.dillon.lw.forest.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.user.vo.user.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;


public interface UserApi {

    @POST("system/user/create")
    Observable<CommonResult<Long>> createUser(@Body UserSaveReqVO reqVO);

    @PUT("system/user/update")
    Observable<CommonResult<Boolean>> updateUser(@Body UserSaveReqVO reqVO);

    @DELETE("system/user/delete")
    Observable<CommonResult<Boolean>> deleteUser(@Query("id") Long id);

    @PUT("system/user/update-password")
    Observable<CommonResult<Boolean>> updateUserPassword(@Body UserUpdatePasswordReqVO reqVO);

    @PUT("system/user/update-status")
    Observable<CommonResult<Boolean>> updateUserStatus(@Body UserUpdateStatusReqVO reqVO);

    @GET("system/user/page")
    Observable<CommonResult<PageResult<UserRespVO>>>getUserPage(@QueryMap Map<String,Object> queryMap);

    @GET("system/user/simple-list")
    Observable<CommonResult<List<UserSimpleRespVO>>>getSimpleUserList();

    @GET("system/user/get")
    Observable<CommonResult<UserRespVO>> getUser(@Query("id") Long id);


}
