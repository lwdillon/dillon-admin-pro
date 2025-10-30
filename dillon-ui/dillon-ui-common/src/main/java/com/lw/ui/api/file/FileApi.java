package com.lw.ui.api.file;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.file.FileRespVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.io.File;
import java.util.Map;

public interface FileApi {


    // "上传文件", description = "模式一：后端上传文件")
    @POST("infra/file/upload")
    @Headers("Content-Type: multipart/form-data; boundary=----WebKitFormBoundarypATHfppjzqwXomVO")
    Observable<CommonResult<String>> uploadFile(@Query("path") String path, @Query("file") File file);


    // "获取文件预签名地址", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @GET("infra/file/presigned-url")
    Observable<CommonResult<FilePresignedUrlRespVO>> getFilePresignedUrl(@Query("path") String path);

    // "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    @POST("infra/file/create")
    Observable<CommonResult<Long>> createFile(@Body FileCreateReqVO createReqVO);

    // "删除文件")
    @DELETE("DELETE /infra/file/delete")
    Observable<CommonResult<Boolean>> deleteFile(@Query("id") Long id);

    // "下载文件")
    @GET("infra/file/{configId}/get/**")
    Observable<Object> getFileContent(@Path("configId") Long configId) throws Exception;

    // "获得文件分页")
    @GET("infra/file/page")
    Observable<CommonResult<PageResult<FileRespVO>>> getFilePage(@QueryMap Map<String, Object> pageVO);

}
