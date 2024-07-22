package com.lw.dillon.admin.module.bpm.convert.definition;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.collection.CollectionUtils;
import com.lw.dillon.admin.framework.common.util.date.DateUtils;
import com.lw.dillon.admin.framework.common.util.json.JsonUtils;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.model.BpmModelCreateReqVO;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.model.BpmModelRespVO;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.model.BpmModelUpdateReqVO;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.process.BpmProcessDefinitionRespVO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmCategoryDO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmFormDO;
import com.lw.dillon.admin.module.bpm.service.definition.dto.BpmModelMetaInfoRespDTO;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 流程模型 Convert
 *
 * @author yunlongn
 */
@Mapper
public interface BpmModelConvert {

    BpmModelConvert INSTANCE = Mappers.getMapper(BpmModelConvert.class);

    default PageResult<BpmModelRespVO> buildModelPage(PageResult<Model> pageResult,
                                                      Map<Long, BpmFormDO> formMap,
                                                      Map<String, BpmCategoryDO> categoryMap, Map<String, Deployment> deploymentMap,
                                                      Map<String, ProcessDefinition> processDefinitionMap) {
        List<BpmModelRespVO> list = CollectionUtils.convertList(pageResult.getList(), model -> {
            BpmModelMetaInfoRespDTO metaInfo = buildMetaInfo(model);
            BpmFormDO form = metaInfo != null ? formMap.get(metaInfo.getFormId()) : null;
            BpmCategoryDO category = categoryMap.get(model.getCategory());
            Deployment deployment = model.getDeploymentId() != null ? deploymentMap.get(model.getDeploymentId()) : null;
            ProcessDefinition processDefinition = model.getDeploymentId() != null ? processDefinitionMap.get(model.getDeploymentId()) : null;
            return buildModel0(model, metaInfo, form, category, deployment, processDefinition);
        });
        return new PageResult<>(list, pageResult.getTotal());
    }

    default BpmModelRespVO buildModel(Model model,
                                     byte[] bpmnBytes) {
        BpmModelMetaInfoRespDTO metaInfo = buildMetaInfo(model);
        BpmModelRespVO modelVO = buildModel0(model, metaInfo, null, null, null, null);
        if (ArrayUtil.isNotEmpty(bpmnBytes)) {
            modelVO.setBpmnXml(new String(bpmnBytes));
        }
        return modelVO;
    }

    default BpmModelRespVO buildModel0(Model model,
                                      BpmModelMetaInfoRespDTO metaInfo, BpmFormDO form, BpmCategoryDO category,
                                      Deployment deployment, ProcessDefinition processDefinition) {
        BpmModelRespVO modelRespVO = new BpmModelRespVO().setId(model.getId()).setName(model.getName())
                .setKey(model.getKey()).setCategory(model.getCategory())
                .setCreateTime(DateUtils.of(model.getCreateTime()));
        // Form
        if (metaInfo != null) {
            modelRespVO.setFormType(metaInfo.getFormType()).setFormId(metaInfo.getFormId())
                    .setFormCustomCreatePath(metaInfo.getFormCustomCreatePath())
                    .setFormCustomViewPath(metaInfo.getFormCustomViewPath());
            modelRespVO.setIcon(metaInfo.getIcon()).setDescription(metaInfo.getDescription());
        }
        if (form != null) {
            modelRespVO.setFormId(form.getId()).setFormName(form.getName());
        }
        // Category
        if (category != null) {
            modelRespVO.setCategoryName(category.getName());
        }
        // ProcessDefinition
        if (processDefinition != null) {
            modelRespVO.setProcessDefinition(BeanUtils.toBean(processDefinition, BpmProcessDefinitionRespVO.class));
            modelRespVO.getProcessDefinition().setSuspensionState(processDefinition.isSuspended() ?
                    SuspensionState.SUSPENDED.getStateCode() : SuspensionState.ACTIVE.getStateCode());
            if (deployment != null) {
                modelRespVO.getProcessDefinition().setDeploymentTime(DateUtils.of(deployment.getDeploymentTime()));
            }
        }
        return modelRespVO;
    }

    default void copyToCreateModel(Model model, BpmModelCreateReqVO bean) {
        model.setName(bean.getName());
        model.setKey(bean.getKey());
        model.setMetaInfo(buildMetaInfoStr(null,
                null, bean.getDescription(),
                null, null, null, null));
    }

    default void copyToUpdateModel(Model model, BpmModelUpdateReqVO bean) {
        model.setName(bean.getName());
        model.setCategory(bean.getCategory());
        model.setMetaInfo(buildMetaInfoStr(buildMetaInfo(model),
                bean.getIcon(), bean.getDescription(),
                bean.getFormType(), bean.getFormId(), bean.getFormCustomCreatePath(), bean.getFormCustomViewPath()));
    }

    default String buildMetaInfoStr(BpmModelMetaInfoRespDTO metaInfo,
                                    String icon, String description,
                                    Integer formType, Long formId, String formCustomCreatePath, String formCustomViewPath) {
        if (metaInfo == null) {
            metaInfo = new BpmModelMetaInfoRespDTO();
        }
        // 只有非空，才进行设置，避免更新时的覆盖
        if (StrUtil.isNotEmpty(icon)) {
            metaInfo.setIcon(icon);
        }
        if (StrUtil.isNotEmpty(description)) {
            metaInfo.setDescription(description);
        }
        if (Objects.nonNull(formType)) {
            metaInfo.setFormType(formType);
            metaInfo.setFormId(formId);
            metaInfo.setFormCustomCreatePath(formCustomCreatePath);
            metaInfo.setFormCustomViewPath(formCustomViewPath);
        }
        return JsonUtils.toJsonString(metaInfo);
    }

    default BpmModelMetaInfoRespDTO buildMetaInfo(Model model) {
        return JsonUtils.parseObject(model.getMetaInfo(), BpmModelMetaInfoRespDTO.class);
    }

}
