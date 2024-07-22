package com.lw.dillon.admin.module.system.api.logger;

import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogCreateReqDTO;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogRespDTO;

import javax.validation.Valid;

/**
 * 操作日志 API 接口
 *
 * @author 芋道源码
 */
public interface OperateLogApi {

    /**
     * 创建操作日志
     *
     * @param createReqDTO 请求
     */
    void createOperateLog(@Valid OperateLogCreateReqDTO createReqDTO);

    /**
     * 获取指定模块的指定数据的操作日志分页
     *
     * @param pageReqVO 请求
     * @return 操作日志分页
     */
    PageResult<OperateLogRespDTO> getOperateLogPage(OperateLogPageReqDTO pageReqVO);

}
