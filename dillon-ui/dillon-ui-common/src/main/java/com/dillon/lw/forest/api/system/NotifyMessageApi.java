package com.dillon.lw.forest.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.List;
import java.util.Map;

public interface NotifyMessageApi {


    // ========== 管理所有的站内信 ==========

    //获得站内信")
    @GET("system/notify-message/get")
    Observable<CommonResult<NotifyMessageRespVO>> getNotifyMessage(@Query("id") Long id);

    //获得站内信分页")
    @GET("system/notify-message/page")
    Observable<CommonResult<PageResult<NotifyMessageRespVO>>> getNotifyMessagePage(@QueryMap Map<String, Object> pageVO);

    // ========== 查看自己的站内信 ==========
    //获得我的站内信分页"
    @GET("system/notify-message/my-page")
    Observable<CommonResult<PageResult<NotifyMessageRespVO>>> getMyMyNotifyMessagePage(@QueryMap Map<String, Object> pageVO);

    //标记站内信为已读")
    @PUT("system/notify-message/update-read")
    Observable<CommonResult<Boolean>> updateNotifyMessageRead( @Query("ids") List<Long> ids);

    //标记所有站内信为已读")
    @PUT("system/notify-message/update-all-read")
    Observable<CommonResult<Boolean>> updateAllNotifyMessageRead();

    @GET("system/notify-message/get-unread-list")
    Observable<CommonResult<List<NotifyMessageRespVO>>> getUnreadNotifyMessageList(@Query("size") Integer size);

    //获得当前用户的未读站内信数量")
    @GET("system/notify-message/get-unread-count")
    Observable<CommonResult<Long>> getUnreadNotifyMessageCount();

}
