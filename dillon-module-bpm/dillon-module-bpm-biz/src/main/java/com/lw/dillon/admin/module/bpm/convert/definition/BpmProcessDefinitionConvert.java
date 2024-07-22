package com.lw.dillon.admin.module.bpm.convert.definition;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.collection.CollectionUtils;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.process.BpmProcessDefinitionRespVO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmCategoryDO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmFormDO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmProcessDefinitionInfoDO;
import com.lw.dillon.admin.module.bpm.framework.flowable.core.util.BpmnModelUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * Bpm 流程定义的 Convert
 *
 * @author yunlong.li
 */
@Mapper
public interface BpmProcessDefinitionConvert {

    BpmProcessDefinitionConvert INSTANCE = Mappers.getMapper(BpmProcessDefinitionConvert.class);

    default PageResult<BpmProcessDefinitionRespVO> buildProcessDefinitionPage(PageResult<ProcessDefinition> page,
                                                                              Map<String, Deployment> deploymentMap,
                                                                              Map<String, BpmProcessDefinitionInfoDO> processDefinitionInfoMap,
                                                                              Map<Long, BpmFormDO> formMap,
                                                                              Map<String, BpmCategoryDO> categoryMap) {
        List<BpmProcessDefinitionRespVO> list = buildProcessDefinitionList(page.getList(), deploymentMap, processDefinitionInfoMap, formMap, categoryMap);
        return new PageResult<>(list, page.getTotal());
    }

    default List<BpmProcessDefinitionRespVO> buildProcessDefinitionList(List<ProcessDefinition> list,
                                                                        Map<String, Deployment> deploymentMap,
                                                                        Map<String, BpmProcessDefinitionInfoDO> processDefinitionInfoMap,
                                                                        Map<Long, BpmFormDO> formMap,
                                                                        Map<String, BpmCategoryDO> categoryMap) {
        return CollectionUtils.convertList(list, definition -> {
            Deployment deployment = MapUtil.get(deploymentMap, definition.getDeploymentId(), Deployment.class);
            BpmProcessDefinitionInfoDO processDefinitionInfo = MapUtil.get(processDefinitionInfoMap, definition.getId(), BpmProcessDefinitionInfoDO.class);
            BpmFormDO form = null;
            if (processDefinitionInfo != null) {
                form = MapUtil.get(formMap, processDefinitionInfo.getFormId(), BpmFormDO.class);
            }
            BpmCategoryDO category = MapUtil.get(categoryMap, definition.getCategory(), BpmCategoryDO.class);
            return buildProcessDefinition(definition, deployment, processDefinitionInfo, form, category, null, null);
        });
    }

    default BpmProcessDefinitionRespVO buildProcessDefinition(ProcessDefinition definition,
                                                              Deployment deployment,
                                                              BpmProcessDefinitionInfoDO processDefinitionInfo,
                                                              BpmFormDO form,
                                                              BpmCategoryDO category,
                                                              BpmnModel bpmnModel,
                                                              List<UserTask> startUserSelectUserTaskList) {
        BpmProcessDefinitionRespVO respVO = BeanUtils.toBean(definition, BpmProcessDefinitionRespVO.class);
        respVO.setSuspensionState(definition.isSuspended() ? SuspensionState.SUSPENDED.getStateCode() : SuspensionState.ACTIVE.getStateCode());
        // Deployment
        if (deployment != null) {
            respVO.setDeploymentTime(LocalDateTimeUtil.of(deployment.getDeploymentTime()));
        }
        // BpmProcessDefinitionInfoDO
        if (processDefinitionInfo != null) {
            copyTo(processDefinitionInfo, respVO);
            // Form
            if (form != null) {
                respVO.setFormName(form.getName());
            }
        }
        // Category
        if (category != null) {
            respVO.setCategoryName(category.getName());
        }
        // BpmnModel
        if (bpmnModel != null) {
            respVO.setBpmnXml(BpmnModelUtils.getBpmnXml(bpmnModel));
            respVO.setStartUserSelectTasks(BeanUtils.toBean(startUserSelectUserTaskList, BpmProcessDefinitionRespVO.UserTask.class));
        }
        return respVO;
    }

    @Mapping(source = "from.id", target = "to.id", ignore = true)
    void copyTo(BpmProcessDefinitionInfoDO from, @MappingTarget BpmProcessDefinitionRespVO to);

}
