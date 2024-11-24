package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;
import java.util.Map;


public interface DictTypeFeign extends BaseFeignApi {


    @RequestLine("POST /system/dict-type/create")
    CommonResult<Long> createDictType( DictTypeSaveReqVO createReqVO);

    @RequestLine("PUT /system/dict-type/update")
    CommonResult<Boolean> updateDictType( DictTypeSaveReqVO updateReqVO);

    @RequestLine("DELETE /system/dict-type/delete?id={id}")
    CommonResult<Boolean> deleteDictType(@Param("id") Long id);

    @RequestLine("GET /system/dict-type/page")
    CommonResult<PageResult<DictTypeRespVO>> pageDictTypes(@QueryMap Map<String,Object> map);

    @RequestLine("GET /system/dict-type/get?id={id}")
    CommonResult<DictTypeRespVO> getDictType(@Param("id") Long id);

    @RequestLine("GET /system/dict-type/simple-list")
        // 无需添加权限认证，因为前端全局都需要
    CommonResult<List<DictTypeSimpleRespVO>> getSimpleDictTypeList();


}
