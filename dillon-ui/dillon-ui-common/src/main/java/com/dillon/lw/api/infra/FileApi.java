package com.dillon.lw.api.infra;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import com.dillon.lw.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import com.dillon.lw.module.infra.controller.admin.file.vo.file.FileRespVO;
import com.dtflys.forest.annotation.*;

import java.io.File;
import java.util.Map;

public interface FileApi extends BaseApi {


    // "上传文件", description = "模式一：后端上传文件")
    @Post("infra/file/upload")
    CommonResult<String> uploadFile(@Query("path") String path, @DataFile("file") File file);


    // "获取文件预签名地址", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @Get("infra/file/presigned-url")
    CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(@Query("path") String path);

    // "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    @Post("infra/file/create")
    CommonResult<Long> createFile(@Body FileCreateReqVO createReqVO);

    // "删除文件")
    @Delete("infra/file/delete")
    CommonResult<Boolean> deleteFile(@Query("id") Long id);

    // "下载文件")
    @Get("infra/file/{configId}/get/**")
    Object getFileContent(@Var("configId") Long configId) throws Exception;

    // "获得文件分页")
    @Get("infra/file/page")
    CommonResult<PageResult<FileRespVO>> getFilePage(@Query Map<String, Object> pageVO);

}
