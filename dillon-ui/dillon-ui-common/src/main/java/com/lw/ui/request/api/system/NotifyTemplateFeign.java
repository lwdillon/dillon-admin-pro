package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.template.NotifyTemplateSendReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface NotifyTemplateFeign extends BaseFeignApi {
    //"创建站内信模版")
    @RequestLine("POST /system/notify-template/create")
    CommonResult<Long> createNotifyTemplate(NotifyTemplateSaveReqVO createReqVO);

    //"更新站内信模版")
    @RequestLine("PUT /system/notify-template/update")
    CommonResult<Boolean> updateNotifyTemplate(NotifyTemplateSaveReqVO updateReqVO);

    //"删除站内信模版")
    @RequestLine("DELETE /system/notify-template/delete?id={id}")
    CommonResult<Boolean> deleteNotifyTemplate(@Param("id") Long id);

    //"获得站内信模版")
    @RequestLine("GET /system/notify-template/get?id={id}")
    CommonResult<NotifyTemplateRespVO> getNotifyTemplate(@Param("id") Long id);

    //"获得站内信模版分页")
    @RequestLine("GET /system/notify-template/page")
    CommonResult<PageResult<NotifyTemplateRespVO>> getNotifyTemplatePage(@QueryMap Map<String,Object> pageVO);

    //"发送站内信")
    @RequestLine("POST /system/notify-template/send-notify")
    CommonResult<Long> sendNotify(NotifyTemplateSendReqVO sendReqVO);
}
