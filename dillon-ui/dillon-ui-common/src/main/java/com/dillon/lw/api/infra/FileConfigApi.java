package com.dillon.lw.api.infra;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import com.dtflys.forest.annotation.*;

import java.util.Map;

public interface FileConfigApi extends BaseApi {

    //"创建文件配置")
    @Post("infra/file-config/create")
    CommonResult<Long> createFileConfig(@Body FileConfigSaveReqVO createReqVO);

    //"更新文件配置")
    @Put("infra/file-config/update")
    CommonResult<Boolean> updateFileConfig(@Body FileConfigSaveReqVO updateReqVO);

    //"更新文件配置为 Master")
    @Put("infra/file-config/update-master")
    CommonResult<Boolean> updateFileConfigMaster(@Query("id") Long id);

    //"删除文件配置")
    @Delete("infra/file-config/delete")
    CommonResult<Boolean> deleteFileConfig(@Query("id") Long id);

    //"获得文件配置")
    @Get("infra/file-config/get")
    CommonResult<Map<String, Object>> getFileConfig(@Query("id") Long id);

    //"获得文件配置分页")
    @Get("infra/file-config/page")
    CommonResult<PageResult<Map<String, Object>>> getFileConfigPage(@Query Map<String, Object> map);

    //"测试文件配置是否正确")
    @Get("infra/file-config/test")
    CommonResult<String> testFileConfig(@Query("id") Long id) throws Exception;
}
