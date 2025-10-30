package com.dillon.lw.api.system;


import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface MenuApi {

    @GET("system/menu/list")
    Observable<CommonResult<List<MenuRespVO>>> getMenuList(@QueryMap Map<String,Object> reqVO);


    @POST("system/menu/create")
    Observable<CommonResult<Long>> createMenu(@Body MenuSaveVO createReqVO);

    @PUT("system/menu/update")
    Observable<CommonResult<Boolean>> updateMenu(@Body MenuSaveVO updateReqVO);

    @DELETE("system/menu/delete")
    Observable<CommonResult<Boolean>> deleteMenu(@Query("id") Long id);

    @GET("system/menu/simple-list")
    Observable<CommonResult<List<MenuSimpleRespVO>>> getSimpleMenuList();

    @GET("system/menu/get")
    Observable<CommonResult<MenuRespVO>> getMenu(@Query("id") Long id);
}
