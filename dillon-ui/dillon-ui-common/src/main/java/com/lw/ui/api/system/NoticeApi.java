package com.lw.ui.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.notice.vo.NoticeRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.Map;


public interface NoticeApi extends BaseFeignApi {


    @POST("system/notice/create")
    Observable<CommonResult<Long>> createNotice(@Body NoticeSaveReqVO createReqVO);

    @PUT("system/notice/update")
    Observable<CommonResult<Boolean>> updateNotice(@Body NoticeSaveReqVO updateReqVO);

    @DELETE("system/notice/delete?id={id}")
    Observable<CommonResult<Boolean>> deleteNotice(@Query("id") Long id);

    @GET("system/notice/page")
    Observable<CommonResult<PageResult<NoticeRespVO>>> getNoticePage(@QueryMap Map<String, Object> map);

    @GET("system/notice/get?id={id}")
    Observable<CommonResult<NoticeRespVO>> getNotice(@Query("id") Long id);

    @FormUrlEncoded
    @POST("system/notice/push}")
    Observable<CommonResult<Boolean>> push(@Field("id") Long id);

}
