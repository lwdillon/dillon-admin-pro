package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface OAuth2TokenFeign extends BaseFeignApi {


    @RequestLine("GET /admin-api/system/oauth2-token/page")
    CommonResult<PageResult<OAuth2AccessTokenRespVO>> getAccessTokenPage(@QueryMap Map<String,Object> reqVO);

    @RequestLine("DELETE /admin-api/system/oauth2-token/delete?accessToken={accessToken}")
    CommonResult<Boolean> deleteAccessToken(@Param("accessToken") String accessToken);

}