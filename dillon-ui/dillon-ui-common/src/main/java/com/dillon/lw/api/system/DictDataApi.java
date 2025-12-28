package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dtflys.forest.annotation.*;

import java.util.List;
import java.util.Map;

public interface DictDataApi extends BaseApi {


    @Post("system/dict-data/create")
    CommonResult<Long> createDictData(@Body DictDataSaveReqVO createReqVO);

    @Put("system/dict-data/update")
    CommonResult<Boolean> updateDictData(@Body DictDataSaveReqVO updateReqVO);

    @Delete("system/dict-data/delete")
    CommonResult<Boolean> deleteDictData(@Query("id") Long id);

    @Get("system/dict-data/simple-list")
    CommonResult<List<DictDataSimpleRespVO>> getSimpleDictDataList();

    @Get("system/dict-data/page")
    CommonResult<PageResult<DictDataRespVO>> getDictTypePage(@Query Map<String, Object> map);

    @Get("system/dict-data/get")
    CommonResult<DictDataRespVO> getDictData(@Query("id") Long id);


}
