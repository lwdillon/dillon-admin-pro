package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.notice.vo.NoticeRespVO;
import com.dillon.lw.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.dtflys.forest.annotation.*;

import java.util.Map;


public interface NoticeApi extends BaseApi {


    @Post("system/notice/create")
    CommonResult<Long> createNotice(@Body NoticeSaveReqVO createReqVO);

    @Put("system/notice/update")
    CommonResult<Boolean> updateNotice(@Body NoticeSaveReqVO updateReqVO);

    @Delete("system/notice/delete")
    CommonResult<Boolean> deleteNotice(@Query("id") Long id);

    @Get("system/notice/page")
    CommonResult<PageResult<NoticeRespVO>> getNoticePage(@Query Map<String, Object> map);

    @Get("system/notice/get")
    CommonResult<NoticeRespVO> getNotice(@Query("id") Long id);

    @Post("system/notice/push")
    CommonResult<Boolean> push(@Body("id") Long id);

}
