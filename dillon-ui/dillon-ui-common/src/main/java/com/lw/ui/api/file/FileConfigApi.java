package com.lw.ui.api.file;

import com.google.gson.JsonObject;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.Map;

public interface FileConfigApi {

    //"创建文件配置")
    @POST("infra/file-config/create")
    Observable<CommonResult<Long>> createFileConfig(@Body FileConfigSaveReqVO createReqVO);

    //"更新文件配置")
    @PUT("infra/file-config/update")
    Observable<CommonResult<Boolean>> updateFileConfig(@Body FileConfigSaveReqVO updateReqVO);

    //"更新文件配置为 Master")
    @PUT("infra/file-config/update-master")
    Observable<CommonResult<Boolean>> updateFileConfigMaster(@Query("id") Long id);

    //"删除文件配置")
    @DELETE("infra/file-config/delete")
    Observable<CommonResult<Boolean>> deleteFileConfig(@Query("id") Long id);

    //"获得文件配置")
    @GET("infra/file-config/get")
    Observable<CommonResult<JsonObject>> getFileConfig(@Query("id") Long id);

    //"获得文件配置分页")
    @GET("infra/file-config/page")
    Observable<CommonResult<PageResult<JsonObject>>> getFileConfigPage(@QueryMap Map<String, Object> map);

    //"测试文件配置是否正确")
    @GET("infra/file-config/test")
    Observable<CommonResult<String>> testFileConfig(@Query("id") Long id) throws Exception;
}
