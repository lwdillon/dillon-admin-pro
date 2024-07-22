package com.lw.dillon.admin.module.bpm.service.definition;

import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.listener.BpmProcessListenerPageReqVO;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.listener.BpmProcessListenerSaveReqVO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmProcessListenerDO;

import javax.validation.Valid;

/**
 * BPM 流程监听器 Service 接口
 *
 * @author 芋道源码
 */
public interface BpmProcessListenerService {

    /**
     * 创建流程监听器
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProcessListener(@Valid BpmProcessListenerSaveReqVO createReqVO);

    /**
     * 更新流程监听器
     *
     * @param updateReqVO 更新信息
     */
    void updateProcessListener(@Valid BpmProcessListenerSaveReqVO updateReqVO);

    /**
     * 删除流程监听器
     *
     * @param id 编号
     */
    void deleteProcessListener(Long id);

    /**
     * 获得流程监听器
     *
     * @param id 编号
     * @return 流程监听器
     */
    BpmProcessListenerDO getProcessListener(Long id);

    /**
     * 获得流程监听器分页
     *
     * @param pageReqVO 分页查询
     * @return 流程监听器分页
     */
    PageResult<BpmProcessListenerDO> getProcessListenerPage(BpmProcessListenerPageReqVO pageReqVO);

}