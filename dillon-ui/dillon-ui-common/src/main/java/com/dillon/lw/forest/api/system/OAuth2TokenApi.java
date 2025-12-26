package com.dillon.lw.forest.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface OAuth2TokenApi {


    @GET("system/oauth2-token/page")
    Observable<CommonResult<PageResult<OAuth2AccessTokenRespVO>>> getAccessTokenPage(@QueryMap Map<String, Object> reqVO);

    @DELETE("system/oauth2-token/delete")
    Observable<CommonResult<Boolean>> deleteAccessToken(@Query("accessToken") String accessToken);

}