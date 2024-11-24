package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.notice.vo.NoticeRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;


public interface NoticeFeign extends BaseFeignApi {


    @RequestLine("POST /system/notice/create")
    CommonResult<Long> createNotice(NoticeSaveReqVO createReqVO);

    @RequestLine("PUT /system/notice/update")
    CommonResult<Boolean> updateNotice(NoticeSaveReqVO updateReqVO);

    @RequestLine("DELETE /system/notice/delete?id={id}")
    CommonResult<Boolean> deleteNotice(@Param("id") Long id);

    @RequestLine("GET /system/notice/page")
    CommonResult<PageResult<NoticeRespVO>> getNoticePage(@QueryMap Map<String,Object> map);

    @RequestLine("GET /system/notice/get?id={id}")
    CommonResult<NoticeRespVO> getNotice(@Param("id") Long id);

    @RequestLine("POST /system/notice/push?id={id}")
    CommonResult<Boolean> push(@Param("id")Long id);

}
