package com.lw.ui.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;


public interface DictTypeApi extends BaseFeignApi {


    @POST("system/dict-type/create")
    Observable<CommonResult<Long>> createDictType(@Body DictTypeSaveReqVO createReqVO);

    @PUT("system/dict-type/update")
    Observable<CommonResult<Boolean>> updateDictType(@Body DictTypeSaveReqVO updateReqVO);

    @DELETE("system/dict-type/delete")
    Observable<CommonResult<Boolean>> deleteDictType(@Query("id") Long id);

    @GET("system/dict-type/page")
    Observable<CommonResult<PageResult<DictTypeRespVO>>> pageDictTypes(@QueryMap Map<String, Object> map);

    @GET("system/dict-type/get")
    Observable<CommonResult<DictTypeRespVO>> getDictType(@Query("id") Long id);

    @GET("system/dict-type/simple-list")
        // 无需添加权限认证，因为前端全局都需要
    Observable<CommonResult<List<DictTypeSimpleRespVO>>> getSimpleDictTypeList();


}
