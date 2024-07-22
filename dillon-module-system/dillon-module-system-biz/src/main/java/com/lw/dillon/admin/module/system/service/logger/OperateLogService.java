package com.lw.dillon.admin.module.system.service.logger;

import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogCreateReqDTO;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.lw.dillon.admin.module.system.dal.dataobject.logger.OperateLogDO;

/**
 * 操作日志 Service 接口
 *
 * @author 芋道源码
 */
public interface OperateLogService {

    /**
     * 记录操作日志
     *
     * @param createReqDTO 创建请求
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 获得操作日志分页列表
     *
     * @param pageReqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO);

    /**
     * 获得操作日志分页列表
     *
     * @param pageReqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqVO);


    /**
     * 删除操作日志
     *
     * @param id 编号
     */
    void deleteOperateLog(Long id);

    /**
     * 清空操作日志
     */
    void clearOperateLog();


}
