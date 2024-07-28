package com.lw.ui.request.api.file;

import com.google.gson.JsonObject;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.config.FileConfigRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface FileConfigFeign extends BaseFeignApi {

        //"创建文件配置")
    @RequestLine("POST /admin-api/infra/file-config/create")
    CommonResult<Long> createFileConfig( FileConfigSaveReqVO createReqVO);

        //"更新文件配置")
    @RequestLine("PUT /admin-api/infra/file-config/update")
    CommonResult<Boolean> updateFileConfig( FileConfigSaveReqVO updateReqVO);

        //"更新文件配置为 Master")
    @RequestLine("PUT /admin-api/infra/file-config/update-master?id={id}")
    CommonResult<Boolean> updateFileConfigMaster(@Param("id") Long id);

        //"删除文件配置")
    @RequestLine("DELETE /admin-api/infra/file-config/delete?id={id}")
    CommonResult<Boolean> deleteFileConfig(@Param("id") Long id);

        //"获得文件配置")
    @RequestLine("GET /admin-api/infra/file-config/get?id={id}")
    CommonResult<JsonObject> getFileConfig(@Param("id") Long id);

        //"获得文件配置分页")
    @RequestLine("GET /admin-api/infra/file-config/page")
    CommonResult<PageResult<JsonObject>> getFileConfigPage(@QueryMap Map<String,Object> map);

        //"测试文件配置是否正确")
    @RequestLine("GET /admin-api/infra/file-config/test?id={id}")
    CommonResult<String> testFileConfig(@Param("id") Long id) throws Exception;
}
