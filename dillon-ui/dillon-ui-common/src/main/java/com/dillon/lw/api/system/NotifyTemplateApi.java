package com.dillon.lw.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateSendReqVO;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.Map;

public interface NotifyTemplateApi {
    //"创建站内信模版")
    @POST("system/notify-template/create")
    Observable<CommonResult<Long>> createNotifyTemplate(@Body NotifyTemplateSaveReqVO createReqVO);

    //"更新站内信模版")
    @PUT("system/notify-template/update")
    Observable<CommonResult<Boolean>> updateNotifyTemplate(@Body NotifyTemplateSaveReqVO updateReqVO);

    //"删除站内信模版")
    @DELETE("system/notify-template/delete?id={id}")
    Observable<CommonResult<Boolean>> deleteNotifyTemplate(@Query("id") Long id);

    //"获得站内信模版")
    @GET("system/notify-template/get?id={id}")
    Observable<CommonResult<NotifyTemplateRespVO>> getNotifyTemplate(@Query("id") Long id);

    //"获得站内信模版分页")
    @GET("system/notify-template/page")
    Observable<CommonResult<PageResult<NotifyTemplateRespVO>>> getNotifyTemplatePage(@QueryMap Map<String, Object> pageVO);

    //"发送站内信")
    @POST("system/notify-template/send-notify")
    Observable<CommonResult<Long>> sendNotify(@Body NotifyTemplateSendReqVO sendReqVO);
}
