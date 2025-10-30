package com.dillon.lw.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface DictDataApi {


    @POST("system/dict-data/create")
    Observable<CommonResult<Long>> createDictData(@Body DictDataSaveReqVO createReqVO);

    @PUT("system/dict-data/update")
    Observable<CommonResult<Boolean>> updateDictData(@Body DictDataSaveReqVO updateReqVO);

    @DELETE("system/dict-data/delete")
    Observable<CommonResult<Boolean>> deleteDictData(@Query("id") Long id);

    @GET("system/dict-data/simple-list")
    Observable<CommonResult<List<DictDataSimpleRespVO>>> getSimpleDictDataList();

    @GET("system/dict-data/page")
    Observable<CommonResult<PageResult<DictDataRespVO>>> getDictTypePage(@QueryMap Map<String, Object> map);

    @GET("system/dict-data/get")
    Observable<CommonResult<DictDataRespVO>> getDictData(@Query("id") Long id);


}
