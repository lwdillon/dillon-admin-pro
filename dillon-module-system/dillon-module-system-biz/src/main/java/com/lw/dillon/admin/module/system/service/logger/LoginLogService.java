package com.lw.dillon.admin.module.system.service.logger;

import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.lw.dillon.admin.module.system.dal.dataobject.logger.LoginLogDO;

import javax.validation.Valid;

/**
 * 登录日志 Service 接口
 */
public interface LoginLogService {

    /**
     * 获得登录日志分页
     *
     * @param pageReqVO 分页条件
     * @return 登录日志分页
     */
    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO);

    /**
     * 删除登录日志
     *
     * @param id 编号
     */
    void deleteLoginLog(Long id);

    /**
     * 清空登录日志
     */
    void clearLoginLog();

    /**
     * 创建登录日志
     *
     * @param reqDTO 日志信息
     */
    void createLoginLog(@Valid LoginLogCreateReqDTO reqDTO);


}
