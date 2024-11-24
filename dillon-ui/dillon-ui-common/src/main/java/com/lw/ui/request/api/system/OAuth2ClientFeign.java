package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.oauth2.vo.client.OAuth2ClientRespVO;
import com.lw.dillon.admin.module.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface OAuth2ClientFeign extends BaseFeignApi {


    //(summary = "创建 OAuth2 客户端")
    @RequestLine("POST /system/oauth2-client/create")
    CommonResult<Long> createOAuth2Client(OAuth2ClientSaveReqVO createReqVO);

    //(summary = "更新 OAuth2 客户端")
    @RequestLine("PUT /system/oauth2-client/update")
    CommonResult<Boolean> updateOAuth2Client(OAuth2ClientSaveReqVO updateReqVO);

    //(summary = "删除 OAuth2 客户端")
    @RequestLine("DELETE /system/oauth2-client/delete")
    CommonResult<Boolean> deleteOAuth2Client(@Param("id") Long id);

    //(summary = "获得 OAuth2 客户端")
    @RequestLine("GET /system/oauth2-client/get?id={id}")
    CommonResult<OAuth2ClientRespVO> getOAuth2Client(@Param("id") Long id);

    //(summary = "获得 OAuth2 客户端分页")
    @RequestLine("GET /system/oauth2-client/page")
    CommonResult<PageResult<OAuth2ClientRespVO>> getOAuth2ClientPage(@QueryMap Map<String, Object> params);

}
