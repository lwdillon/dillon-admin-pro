package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import com.dtflys.forest.annotation.*;

import java.util.List;
import java.util.Map;

public interface NotifyMessageApi extends BaseApi {


    // ========== 管理所有的站内信 ==========

    //获得站内信")
    @Get("system/notify-message/get")
    CommonResult<NotifyMessageRespVO> getNotifyMessage(@Query("id") Long id);

    //获得站内信分页")
    @Get("system/notify-message/page")
    CommonResult<PageResult<NotifyMessageRespVO>> getNotifyMessagePage(@Query Map<String, Object> pageVO);

    // ========== 查看自己的站内信 ==========
    //获得我的站内信分页"
    @Get("system/notify-message/my-page")
    CommonResult<PageResult<NotifyMessageRespVO>> getMyMyNotifyMessagePage(@Query Map<String, Object> pageVO);

    //标记站内信为已读")
    @Put("system/notify-message/update-read")
    CommonResult<Boolean> updateNotifyMessageRead(@Query("ids") List<Long> ids);

    //标记所有站内信为已读")
    @Put("system/notify-message/update-all-read")
    CommonResult<Boolean> updateAllNotifyMessageRead();

    @Get("system/notify-message/get-unread-list")
    CommonResult<List<NotifyMessageRespVO>> getUnreadNotifyMessageList(@Query("size") Integer size);

    //获得当前用户的未读站内信数量")
    @Get("system/notify-message/get-unread-count")
    CommonResult<Long> getUnreadNotifyMessageCount();

}
