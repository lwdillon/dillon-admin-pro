package com.dillon.lw.api.infra;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.infra.controller.admin.db.vo.DataSourceConfigRespVO;
import com.dillon.lw.module.infra.controller.admin.db.vo.DataSourceConfigSaveReqVO;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Delete;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Put;
import com.dtflys.forest.annotation.Query;

import java.util.List;

/**
 * 数据源配置管理 API（对应 DataSourceConfigController）。
 */
public interface DataSourceConfigApi extends BaseApi {

    @Post("/infra/data-source-config/create")
    CommonResult<Long> createDataSourceConfig(@Body DataSourceConfigSaveReqVO createReqVO);

    @Put("/infra/data-source-config/update")
    CommonResult<Boolean> updateDataSourceConfig(@Body DataSourceConfigSaveReqVO updateReqVO);

    @Delete("/infra/data-source-config/delete")
    CommonResult<Boolean> deleteDataSourceConfig(@Query("id") Long id);

    @Get("/infra/data-source-config/get")
    CommonResult<DataSourceConfigRespVO> getDataSourceConfig(@Query("id") Long id);

    @Get("/infra/data-source-config/list")
    CommonResult<List<DataSourceConfigRespVO>> getDataSourceConfigList();
}

