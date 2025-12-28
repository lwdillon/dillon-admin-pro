package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import com.dtflys.forest.annotation.Delete;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;

import java.util.Map;

public interface OAuth2TokenApi extends BaseApi {


    @Get("system/oauth2-token/page")
    CommonResult<PageResult<OAuth2AccessTokenRespVO>> getAccessTokenPage(@Query Map<String, Object> reqVO);

    @Delete("system/oauth2-token/delete")
    CommonResult<Boolean> deleteAccessToken(@Query("accessToken") String accessToken);

}