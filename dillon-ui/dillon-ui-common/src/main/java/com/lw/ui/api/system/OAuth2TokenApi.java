package com.lw.ui.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface OAuth2TokenApi extends BaseFeignApi {


    @GET("system/oauth2-token/page")
    Observable<CommonResult<PageResult<OAuth2AccessTokenRespVO>>> getAccessTokenPage(@QueryMap Map<String, Object> reqVO);

    @DELETE("system/oauth2-token/delete")
    Observable<CommonResult<Boolean>> deleteAccessToken(@Query("accessToken") String accessToken);

}