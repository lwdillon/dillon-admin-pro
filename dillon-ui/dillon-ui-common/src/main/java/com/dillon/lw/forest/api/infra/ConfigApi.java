package com.dillon.lw.forest.api.infra;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.Map;

public interface ConfigApi {

    //"创建参数配置")
    @POST("infra/config/create")
    Observable<CommonResult<Long>> createConfig(@Body ConfigSaveReqVO createReqVO);

    //"修改参数配置")
    @PUT("infra/config/update")
    Observable<CommonResult<Boolean>> updateConfig(@Body ConfigSaveReqVO updateReqVO);


    //"删除参数配置")
    @DELETE("infra/config/delete")
    Observable<CommonResult<Boolean>> deleteConfig(@Query("id") Long id);

    //"获得参数配置")
    @GET("infra/config/get")
    Observable<CommonResult<ConfigRespVO>> getConfig(@Query("id") Long id);

    //"根据参数键名查询参数值", description = "不可见的配置，不允许返回给前端")
    @GET("infra/config/get-value-by-key")
    Observable<CommonResult<String>> getConfigKey(@Query("key") String key);

    //"根据参数键名查询参数值", description = "不可见的配置，不允许返回给前端")
    @GET("infra/config/get-by-key")
    Observable<CommonResult<ConfigRespVO>> getConfig(@Query("key") String key);


    //"获取参数配置分页")
    @GET("infra/config/page")
    Observable<CommonResult<PageResult<ConfigRespVO>>> getConfigPage(@QueryMap Map<String, Object> pageReqVO);


}
