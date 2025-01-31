package com.lw.ui.request.api.system;


import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;

public interface MenuFeign extends BaseFeignApi {

    @RequestLine("GET /system/menu/list")
    CommonResult<List<MenuRespVO>> getMenuList(@QueryMap MenuListReqVO reqVO);


    @RequestLine("POST /system/menu/create")
    CommonResult<Long> createMenu( MenuSaveVO createReqVO);

    @RequestLine("PUT /system/menu/update")
    CommonResult<Boolean> updateMenu(MenuSaveVO updateReqVO);

    @RequestLine("DELETE /system/menu/delete?id={id}")
    CommonResult<Boolean> deleteMenu(@Param("id") Long id);

    @RequestLine("GET /system/menu/simple-list")
    CommonResult<List<MenuSimpleRespVO>> getSimpleMenuList();

    @RequestLine("GET /system/menu/get?id={id}")
    CommonResult<MenuRespVO> getMenu(@Param("id") Long id);
}
