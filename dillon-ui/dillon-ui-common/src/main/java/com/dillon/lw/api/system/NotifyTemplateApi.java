package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateSendReqVO;
import com.dtflys.forest.annotation.*;

import java.util.Map;

public interface NotifyTemplateApi extends BaseApi {
    //"创建站内信模版")
    @Post("system/notify-template/create")
    CommonResult<Long> createNotifyTemplate(@Body NotifyTemplateSaveReqVO createReqVO);

    //"更新站内信模版")
    @Put("system/notify-template/update")
    CommonResult<Boolean> updateNotifyTemplate(@Body NotifyTemplateSaveReqVO updateReqVO);

    //"删除站内信模版")
    @Delete("system/notify-template/delete")
    CommonResult<Boolean> deleteNotifyTemplate(@Query("id") Long id);

    //"获得站内信模版")
    @Get("system/notify-template/get")
    CommonResult<NotifyTemplateRespVO> getNotifyTemplate(@Query("id") Long id);

    //"获得站内信模版分页")
    @Get("system/notify-template/page")
    CommonResult<PageResult<NotifyTemplateRespVO>> getNotifyTemplatePage(@Query Map<String, Object> pageVO);

    //"发送站内信")
    @Post("system/notify-template/send-notify")
    CommonResult<Long> sendNotify(@Body NotifyTemplateSendReqVO sendReqVO);
}
