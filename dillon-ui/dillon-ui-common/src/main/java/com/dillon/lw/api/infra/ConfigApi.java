package com.dillon.lw.api.infra;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.dtflys.forest.annotation.*;

import java.util.Map;

public interface ConfigApi extends BaseApi {

    //"创建参数配置")
    @Post("/infra/config/create")
    CommonResult<Long> createConfig(@Body ConfigSaveReqVO createReqVO);

    //"修改参数配置")
    @Put("/infra/config/update")
    CommonResult<Boolean> updateConfig(@Body ConfigSaveReqVO updateReqVO);


    //"删除参数配置")
    @Delete("/infra/config/delete")
    CommonResult<Boolean> deleteConfig(@Query("id") Long id);

    //"获得参数配置")
    @Get("/infra/config/get")
    CommonResult<ConfigRespVO> getConfig(@Query("id") Long id);

    //"根据参数键名查询参数值", description = "不可见的配置，不允许返回给前端")
    @Get("/infra/config/get-value-by-key")
    CommonResult<String> getConfigKey(@Query("key") String key);

    //"根据参数键名查询参数值", description = "不可见的配置，不允许返回给前端")
    @Get("/infra/config/get-by-key")
    CommonResult<ConfigRespVO> getConfig(@Query("key") String key);


    //"获取参数配置分页")
    @Get("/infra/config/page")
    CommonResult<PageResult<ConfigRespVO>> getConfigPage(@Query Map<String, Object> pageReqVO);


}
