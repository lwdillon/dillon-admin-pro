package com.dillon.lw.api.system;


import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.dtflys.forest.annotation.*;

import java.util.List;
import java.util.Map;

public interface MenuApi extends BaseApi {

    @Get("system/menu/list")
    CommonResult<List<MenuRespVO>> getMenuList(@Query Map<String, Object> reqVO);


    @Post("system/menu/create")
    CommonResult<Long> createMenu(@Body MenuSaveVO createReqVO);

    @Put("system/menu/update")
    CommonResult<Boolean> updateMenu(@Body MenuSaveVO updateReqVO);

    @Delete("system/menu/delete")
    CommonResult<Boolean> deleteMenu(@Query("id") Long id);

    @Get("system/menu/simple-list")
    CommonResult<List<MenuSimpleRespVO>> getSimpleMenuList();

    @Get("system/menu/get")
    CommonResult<MenuRespVO> getMenu(@Query("id") Long id);
}
