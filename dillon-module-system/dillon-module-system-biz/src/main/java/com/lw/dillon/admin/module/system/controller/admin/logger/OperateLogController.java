package com.lw.dillon.admin.module.system.controller.admin.logger;

import com.lw.dillon.admin.framework.apilog.core.annotation.ApiAccessLog;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageParam;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.framework.excel.core.util.ExcelUtils;
import com.lw.dillon.admin.framework.translate.core.TranslateUtils;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.lw.dillon.admin.module.system.dal.dataobject.logger.OperateLogDO;
import com.lw.dillon.admin.module.system.service.logger.OperateLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.lw.dillon.admin.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.lw.dillon.admin.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 操作日志")
@RestController
@RequestMapping("/system/operate-log")
@Validated
public class OperateLogController {

    @Resource
    private OperateLogService operateLogService;

    @GetMapping("/page")
    @Operation(summary = "查看操作日志分页列表")
    @PreAuthorize("@ss.hasPermission('system:operate-log:query')")
    public CommonResult<PageResult<OperateLogRespVO>> pageOperateLog(@Valid OperateLogPageReqVO pageReqVO) {
        PageResult<OperateLogDO> pageResult = operateLogService.getOperateLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, OperateLogRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除操作日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:operate-log:delete')")
    public CommonResult<Boolean> deleteLoginLog(@RequestParam("id") Long id) {
        operateLogService.deleteOperateLog(id);
        return success(true);
    }
    @DeleteMapping("/clear")
    @Operation(summary = "清空操作日志")
    @PreAuthorize("@ss.hasPermission('system:operate-log:clear')")
    public CommonResult<Boolean> clearLoginLog() {
        operateLogService.clearOperateLog();
        return success(true);
    }



    @Operation(summary = "导出操作日志")
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:operate-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOperateLog(HttpServletResponse response, @Valid OperateLogPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OperateLogDO> list = operateLogService.getOperateLogPage(exportReqVO).getList();
        ExcelUtils.write(response, "操作日志.xls", "数据列表", OperateLogRespVO.class,
                TranslateUtils.translate(BeanUtils.toBean(list, OperateLogRespVO.class)));
    }

}
