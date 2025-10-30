package com.lw.ui.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface DictDataApi extends BaseFeignApi {


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
