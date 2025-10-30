package com.lw.ui.api.system;


import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface MenuApi extends BaseFeignApi {

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
