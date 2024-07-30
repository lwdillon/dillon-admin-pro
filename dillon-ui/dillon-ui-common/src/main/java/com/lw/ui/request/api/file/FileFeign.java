package com.lw.ui.request.api.file;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.file.FileRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.io.File;
import java.util.Map;

public interface FileFeign extends BaseFeignApi {


    // "上传文件", description = "模式一：后端上传文件")
    @RequestLine("POST /admin-api/infra/file/upload")
    @Headers("Content-Type: multipart/form-data; boundary=----WebKitFormBoundarypATHfppjzqwXomVO")
    CommonResult<String> uploadFile(@Param("path") String path, @Param("file") File file);


    // "获取文件预签名地址", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @RequestLine("GET /admin-api/infra/file/presigned-url?path={path}")
    CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(@Param("path") String path);

    // "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    @RequestLine("POST /admin-api/infra/file/create")
    CommonResult<Long> createFile(FileCreateReqVO createReqVO);

    // "删除文件")
    @RequestLine("DELETE /admin-api/infra/file/delete?id={id}")
    CommonResult<Boolean> deleteFile(@Param("id") Long id);

    // "下载文件")
    @RequestLine("GET /admin-api/infra/file/{configId}/get/**")
    void getFileContent(@Param("configId") Long configId) throws Exception;

    // "获得文件分页")
    @RequestLine("GET /admin-api/infra/file/page")
    CommonResult<PageResult<FileRespVO>> getFilePage(@QueryMap Map<String, Object> pageVO);

}
