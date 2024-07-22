package com.lw.dillon.admin.module.bpm.controller.admin.definition;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.form.BpmFormPageReqVO;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.form.BpmFormRespVO;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.form.BpmFormSaveReqVO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmFormDO;
import com.lw.dillon.admin.module.bpm.service.definition.BpmFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static com.lw.dillon.admin.framework.common.pojo.CommonResult.success;
import static com.lw.dillon.admin.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - 动态表单")
@RestController
@RequestMapping("/bpm/form")
@Validated
public class BpmFormController {

    @Resource
    private BpmFormService formService;

    @PostMapping("/create")
    @Operation(summary = "创建动态表单")
    @PreAuthorize("@ss.hasPermission('bpm:form:create')")
    public CommonResult<Long> createForm(@Valid @RequestBody BpmFormSaveReqVO createReqVO) {
        return success(formService.createForm(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新动态表单")
    @PreAuthorize("@ss.hasPermission('bpm:form:update')")
    public CommonResult<Boolean> updateForm(@Valid @RequestBody BpmFormSaveReqVO updateReqVO) {
        formService.updateForm(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除动态表单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('bpm:form:delete')")
    public CommonResult<Boolean> deleteForm(@RequestParam("id") Long id) {
        formService.deleteForm(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得动态表单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('bpm:form:query')")
    public CommonResult<BpmFormRespVO> getForm(@RequestParam("id") Long id) {
        BpmFormDO form = formService.getForm(id);
        return success(BeanUtils.toBean(form, BpmFormRespVO.class));
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "获得动态表单的精简列表", description = "用于表单下拉框")
    public CommonResult<List<BpmFormRespVO>> getFormSimpleList() {
        List<BpmFormDO> list = formService.getFormList();
        return success(convertList(list, formDO -> // 只返回 id、name 字段
                new BpmFormRespVO().setId(formDO.getId()).setName(formDO.getName())));
    }

    @GetMapping("/page")
    @Operation(summary = "获得动态表单分页")
    @PreAuthorize("@ss.hasPermission('bpm:form:query')")
    public CommonResult<PageResult<BpmFormRespVO>> getFormPage(@Valid BpmFormPageReqVO pageVO) {
        PageResult<BpmFormDO> pageResult = formService.getFormPage(pageVO);
        return success(BeanUtils.toBean(pageResult, BpmFormRespVO.class));
    }

}
