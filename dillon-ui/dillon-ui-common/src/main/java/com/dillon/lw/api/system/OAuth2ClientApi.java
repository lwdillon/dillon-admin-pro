package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.oauth2.vo.client.OAuth2ClientRespVO;
import com.dillon.lw.module.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import com.dtflys.forest.annotation.*;

import java.util.Map;

public interface OAuth2ClientApi extends BaseApi {


    //(summary = "创建 OAuth2 客户端")
    @Post("system/oauth2-client/create")
    CommonResult<Long> createOAuth2Client(@Body OAuth2ClientSaveReqVO createReqVO);

    //(summary = "更新 OAuth2 客户端")
    @Put("system/oauth2-client/update")
    CommonResult<Boolean> updateOAuth2Client(@Body OAuth2ClientSaveReqVO updateReqVO);

    //(summary = "删除 OAuth2 客户端")
    @Delete("system/oauth2-client/delete")
    CommonResult<Boolean> deleteOAuth2Client(@Query("id") Long id);

    //(summary = "获得 OAuth2 客户端")
    @Get("system/oauth2-client/get")
    CommonResult<OAuth2ClientRespVO> getOAuth2Client(@Query("id") Long id);

    //(summary = "获得 OAuth2 客户端分页")
    @Get("system/oauth2-client/page")
    CommonResult<PageResult<OAuth2ClientRespVO>> getOAuth2ClientPage(@Query Map<String, Object> params);

}
