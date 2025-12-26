package com.dillon.lw.forest.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.oauth2.vo.client.OAuth2ClientRespVO;
import com.dillon.lw.module.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.Map;

public interface OAuth2ClientApi {


    //(summary = "创建 OAuth2 客户端")
    @POST("system/oauth2-client/create")
    Observable<CommonResult<Long>> createOAuth2Client(@Body OAuth2ClientSaveReqVO createReqVO);

    //(summary = "更新 OAuth2 客户端")
    @PUT("system/oauth2-client/update")
    Observable<CommonResult<Boolean>> updateOAuth2Client(@Body OAuth2ClientSaveReqVO updateReqVO);

    //(summary = "删除 OAuth2 客户端")
    @DELETE("system/oauth2-client/delete")
    Observable<CommonResult<Boolean>> deleteOAuth2Client(@Query("id") Long id);

    //(summary = "获得 OAuth2 客户端")
    @GET("system/oauth2-client/get?id={id}")
    Observable<CommonResult<OAuth2ClientRespVO>> getOAuth2Client(@Query("id") Long id);

    //(summary = "获得 OAuth2 客户端分页")
    @GET("system/oauth2-client/page")
    Observable<CommonResult<PageResult<OAuth2ClientRespVO>>> getOAuth2ClientPage(@QueryMap Map<String, Object> params);

}
