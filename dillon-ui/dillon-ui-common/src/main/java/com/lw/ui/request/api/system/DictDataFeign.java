package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface DictDataFeign extends BaseFeignApi {


    @RequestLine("POST /admin-api/system/dict-data/create")
    CommonResult<Long> createDictData(DictDataSaveReqVO createReqVO);

    @RequestLine("PUT /admin-api/system/dict-data/update")
    CommonResult<Boolean> updateDictData(DictDataSaveReqVO updateReqVO);

    @RequestLine("DELETE /admin-api/system/dict-data/delete?id={id}")
    CommonResult<Boolean> deleteDictData(@Param("id") Long id);

    @RequestLine("GET /admin-api/system/dict-data/simple-list")
    CommonResult<List<DictDataSimpleRespVO>> getSimpleDictDataList();

    @RequestLine("GET /admin-api/system/dict-data/page")
    CommonResult<PageResult<DictDataRespVO>> getDictTypePage(@QueryMap Map<String,Object> map);

    @RequestLine("GET /admin-api/system/dict-data/get?id={id}")
    CommonResult<DictDataRespVO> getDictData(@Param("id") Long id);


}
