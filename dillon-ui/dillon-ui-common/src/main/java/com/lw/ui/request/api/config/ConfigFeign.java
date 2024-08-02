package com.lw.ui.request.api.config;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface ConfigFeign extends BaseFeignApi {

    //"创建参数配置")
    @RequestLine("POST /admin-api/infra/config/create")
    public CommonResult<Long> createConfig(ConfigSaveReqVO createReqVO);

    //"修改参数配置")
    @RequestLine("PUT /admin-api/infra/config/update")
    public CommonResult<Boolean> updateConfig(ConfigSaveReqVO updateReqVO);

    //"删除参数配置")
    @RequestLine("DELETE /admin-api/infra/config/delete?id={id}")
    public CommonResult<Boolean> deleteConfig(@Param("id") Long id);

    //"获得参数配置")
    @RequestLine("GET /admin-api/infra/config/get?id={id}")
    public CommonResult<ConfigRespVO> getConfig(@Param("id") Long id);

    //"根据参数键名查询参数值", description = "不可见的配置，不允许返回给前端")
    @RequestLine("GET /admin-api/infra/config/get-value-by-key?key={key}")
    public CommonResult<String> getConfigKey(@Param("key") String key);

    //"获取参数配置分页")
    @RequestLine("GET /admin-api/infra/config/page")
    public CommonResult<PageResult<ConfigRespVO>> getConfigPage(@QueryMap Map<String, Object> pageReqVO);


}
