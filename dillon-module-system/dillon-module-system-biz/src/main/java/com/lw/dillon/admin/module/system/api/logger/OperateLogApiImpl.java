package com.lw.dillon.admin.module.system.api.logger;

import com.fhs.core.trans.anno.TransMethodResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogCreateReqDTO;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogRespDTO;
import com.lw.dillon.admin.module.system.dal.dataobject.logger.OperateLogDO;
import com.lw.dillon.admin.module.system.service.logger.OperateLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 操作日志 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class OperateLogApiImpl implements OperateLogApi {

    @Resource
    private OperateLogService operateLogService;

    @Override
    @Async
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        operateLogService.createOperateLog(createReqDTO);
    }

    @Override
    @TransMethodResult
    public PageResult<OperateLogRespDTO> getOperateLogPage(OperateLogPageReqDTO pageReqVO) {
        PageResult<OperateLogDO> operateLogPage = operateLogService.getOperateLogPage(pageReqVO);
        return BeanUtils.toBean(operateLogPage, OperateLogRespDTO.class);
    }

}
