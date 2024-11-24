package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface NotifyMessageFeign extends BaseFeignApi {


    // ========== 管理所有的站内信 ==========

    //获得站内信")
    @RequestLine("GET /system/notify-message/get?id={id}")
    CommonResult<NotifyMessageRespVO> getNotifyMessage(@Param("id") Long id);

    //获得站内信分页")
    @RequestLine("GET /system/notify-message/page")
    CommonResult<PageResult<NotifyMessageRespVO>> getNotifyMessagePage(@QueryMap Map<String, Object> pageVO);

    // ========== 查看自己的站内信 ==========
    //获得我的站内信分页"
    @RequestLine("GET /system/notify-message/my-page")
    CommonResult<PageResult<NotifyMessageRespVO>> getMyMyNotifyMessagePage(@QueryMap Map<String, Object> pageVO);

    //标记站内信为已读")
    @RequestLine("PUT /system/notify-message/update-read?ids={ids}")
    CommonResult<Boolean> updateNotifyMessageRead( @Param("ids")List<Long> ids);

    //标记所有站内信为已读")
    @RequestLine("PUT /system/notify-message/update-all-read")
    CommonResult<Boolean> updateAllNotifyMessageRead();

    @RequestLine("GET /system/notify-message/get-unread-list?size={size}")
    CommonResult<List<NotifyMessageRespVO>> getUnreadNotifyMessageList(@Param("size") Integer size);

    //获得当前用户的未读站内信数量")
    @RequestLine("GET /system/notify-message/get-unread-count")
    CommonResult<Long> getUnreadNotifyMessageCount();

}
