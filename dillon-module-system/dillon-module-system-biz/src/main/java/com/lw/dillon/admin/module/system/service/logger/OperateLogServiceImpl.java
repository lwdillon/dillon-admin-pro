package com.lw.dillon.admin.module.system.service.logger;

import com.google.common.annotations.VisibleForTesting;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogCreateReqDTO;
import com.lw.dillon.admin.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.lw.dillon.admin.module.system.dal.dataobject.logger.OperateLogDO;
import com.lw.dillon.admin.module.system.dal.mysql.logger.OperateLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static com.lw.dillon.admin.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.lw.dillon.admin.module.system.enums.ErrorCodeConstants.NOTICE_NOT_FOUND;

/**
 * 操作日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogMapper operateLogMapper;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO log = BeanUtils.toBean(createReqDTO, OperateLogDO.class);
        operateLogMapper.insert(log);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO) {
        return operateLogMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        return operateLogMapper.selectPage(pageReqDTO);
    }

    @Override
    public void deleteOperateLog(Long id) {
        validateNoticeExists(id);
        operateLogMapper.deleteById(id);
    }

    @Override
    public void clearOperateLog() {
        operateLogMapper.delete(null);
    }

    @VisibleForTesting
    public void validateNoticeExists(Long id) {
        if (id == null) {
            return;
        }
        OperateLogDO operateLogDO = operateLogMapper.selectById(id);
        if (operateLogDO == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
    }


}
