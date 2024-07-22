package com.lw.dillon.admin.module.bpm.convert.task;

import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.collection.MapUtils;
import com.lw.dillon.admin.framework.common.util.number.NumberUtils;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.process.BpmProcessDefinitionRespVO;
import com.lw.dillon.admin.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceRespVO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmCategoryDO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmProcessDefinitionInfoDO;
import com.lw.dillon.admin.module.bpm.event.BpmProcessInstanceStatusEvent;
import com.lw.dillon.admin.module.bpm.framework.flowable.core.util.FlowableUtils;
import com.lw.dillon.admin.module.bpm.service.message.dto.BpmMessageSendWhenProcessInstanceApproveReqDTO;
import com.lw.dillon.admin.module.bpm.service.message.dto.BpmMessageSendWhenProcessInstanceRejectReqDTO;
import com.lw.dillon.admin.module.system.api.dept.dto.DeptRespDTO;
import com.lw.dillon.admin.module.system.api.user.dto.AdminUserRespDTO;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 流程实例 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface BpmProcessInstanceConvert {

    BpmProcessInstanceConvert INSTANCE = Mappers.getMapper(BpmProcessInstanceConvert.class);

    default PageResult<BpmProcessInstanceRespVO> buildProcessInstancePage(PageResult<HistoricProcessInstance> pageResult,
                                                                          Map<String, ProcessDefinition> processDefinitionMap,
                                                                          Map<String, BpmCategoryDO> categoryMap,
                                                                          Map<String, List<Task>> taskMap,
                                                                          Map<Long, AdminUserRespDTO> userMap,
                                                                          Map<Long, DeptRespDTO> deptMap) {
        PageResult<BpmProcessInstanceRespVO> vpPageResult = BeanUtils.toBean(pageResult, BpmProcessInstanceRespVO.class);
        for (int i = 0; i < pageResult.getList().size(); i++) {
            BpmProcessInstanceRespVO respVO = vpPageResult.getList().get(i);
            respVO.setStatus(FlowableUtils.getProcessInstanceStatus(pageResult.getList().get(i)));
            MapUtils.findAndThen(processDefinitionMap, respVO.getProcessDefinitionId(),
                    processDefinition -> respVO.setCategory(processDefinition.getCategory()));
            MapUtils.findAndThen(categoryMap, respVO.getCategory(), category -> respVO.setCategoryName(category.getName()));
            respVO.setTasks(BeanUtils.toBean(taskMap.get(respVO.getId()), BpmProcessInstanceRespVO.Task.class));
            // user
            if (userMap != null) {
                AdminUserRespDTO startUser = userMap.get(NumberUtils.parseLong(pageResult.getList().get(i).getStartUserId()));
                if (startUser != null) {
                    respVO.setStartUser(BeanUtils.toBean(startUser, BpmProcessInstanceRespVO.User.class));
                    MapUtils.findAndThen(deptMap, startUser.getDeptId(), dept -> respVO.getStartUser().setDeptName(dept.getName()));
                }
            }
        }
        return vpPageResult;
    }

    default BpmProcessInstanceRespVO buildProcessInstance(HistoricProcessInstance processInstance,
                                                          ProcessDefinition processDefinition,
                                                          BpmProcessDefinitionInfoDO processDefinitionExt,
                                                          String bpmnXml,
                                                          AdminUserRespDTO startUser,
                                                          DeptRespDTO dept) {
        BpmProcessInstanceRespVO respVO = BeanUtils.toBean(processInstance, BpmProcessInstanceRespVO.class);
        respVO.setStatus(FlowableUtils.getProcessInstanceStatus(processInstance));
        respVO.setFormVariables(FlowableUtils.getProcessInstanceFormVariable(processInstance));
        // definition
        respVO.setProcessDefinition(BeanUtils.toBean(processDefinition, BpmProcessDefinitionRespVO.class));
        copyTo(processDefinitionExt, respVO.getProcessDefinition());
        respVO.getProcessDefinition().setBpmnXml(bpmnXml);
        // user
        if (startUser != null) {
            respVO.setStartUser(BeanUtils.toBean(startUser, BpmProcessInstanceRespVO.User.class));
            if (dept != null) {
                respVO.getStartUser().setDeptName(dept.getName());
            }
        }
        return respVO;
    }

    @Mapping(source = "from.id", target = "to.id", ignore = true)
    void copyTo(BpmProcessDefinitionInfoDO from, @MappingTarget BpmProcessDefinitionRespVO to);

    default BpmProcessInstanceStatusEvent buildProcessInstanceStatusEvent(Object source, HistoricProcessInstance instance, Integer status) {
        return new BpmProcessInstanceStatusEvent(source).setId(instance.getId()).setStatus(status)
                .setProcessDefinitionKey(instance.getProcessDefinitionKey()).setBusinessKey(instance.getBusinessKey());
    }

    default BpmProcessInstanceStatusEvent buildProcessInstanceStatusEvent(Object source, ProcessInstance instance, Integer status) {;
        return new BpmProcessInstanceStatusEvent(source).setId(instance.getId()).setStatus(status)
                .setProcessDefinitionKey(instance.getProcessDefinitionKey()).setBusinessKey(instance.getBusinessKey());
    }

    default BpmMessageSendWhenProcessInstanceApproveReqDTO buildProcessInstanceApproveMessage(ProcessInstance instance) {
        return new BpmMessageSendWhenProcessInstanceApproveReqDTO()
                .setStartUserId(NumberUtils.parseLong(instance.getStartUserId()))
                .setProcessInstanceId(instance.getId())
                .setProcessInstanceName(instance.getName());
    }

    default BpmMessageSendWhenProcessInstanceRejectReqDTO buildProcessInstanceRejectMessage(ProcessInstance instance, String reason) {
        return new BpmMessageSendWhenProcessInstanceRejectReqDTO()
            .setProcessInstanceName(instance.getName())
            .setProcessInstanceId(instance.getId())
            .setReason(reason)
            .setStartUserId(NumberUtils.parseLong(instance.getStartUserId()));
    }

}
