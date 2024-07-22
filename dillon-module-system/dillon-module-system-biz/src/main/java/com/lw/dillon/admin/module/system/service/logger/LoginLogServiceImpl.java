package com.lw.dillon.admin.module.system.service.logger;

import com.google.common.annotations.VisibleForTesting;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.lw.dillon.admin.module.system.dal.dataobject.logger.LoginLogDO;
import com.lw.dillon.admin.module.system.dal.mysql.logger.LoginLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static com.lw.dillon.admin.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.lw.dillon.admin.module.system.enums.ErrorCodeConstants.NOTICE_NOT_FOUND;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void deleteLoginLog(Long id) {
        // 校验是否存在
        validateNoticeExists(id);
        // 删除通知公告
        loginLogMapper.deleteById(id);
    }

    @Override
    public void clearLoginLog() {
        loginLogMapper.delete(null);
    }


    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        loginLogMapper.insert(loginLog);
    }

    @VisibleForTesting
    public void validateNoticeExists(Long id) {
        if (id == null) {
            return;
        }
        LoginLogDO loginLogDO = loginLogMapper.selectById(id);
        if (loginLogDO == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
    }


}
