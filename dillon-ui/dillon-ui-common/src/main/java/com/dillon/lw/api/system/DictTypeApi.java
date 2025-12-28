package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.dtflys.forest.annotation.*;

import java.util.List;
import java.util.Map;


public interface DictTypeApi extends BaseApi {


    @Post("system/dict-type/create")
    CommonResult<Long> createDictType(@Body DictTypeSaveReqVO createReqVO);

    @Put("system/dict-type/update")
    CommonResult<Boolean> updateDictType(@Body DictTypeSaveReqVO updateReqVO);

    @Delete("system/dict-type/delete")
    CommonResult<Boolean> deleteDictType(@Query("id") Long id);

    @Get("system/dict-type/page")
    CommonResult<PageResult<DictTypeRespVO>> pageDictTypes(@Query Map<String, Object> map);

    @Get("system/dict-type/get")
    CommonResult<DictTypeRespVO> getDictType(@Query("id") Long id);

    @Get("system/dict-type/simple-list")
        // 无需添加权限认证，因为前端全局都需要
    CommonResult<List<DictTypeSimpleRespVO>> getSimpleDictTypeList();


}
