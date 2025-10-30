package com.dillon.lw.module.system.controller.admin.logger;

import com.dillon.lw.framework.apilog.core.annotation.ApiAccessLog;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageParam;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.framework.common.util.object.BeanUtils;
import com.dillon.lw.framework.excel.core.util.ExcelUtils;
import com.dillon.lw.framework.translate.core.TranslateUtils;
import com.dillon.lw.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.dillon.lw.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.dillon.lw.module.system.dal.dataobject.logger.OperateLogDO;
import com.dillon.lw.module.system.service.logger.OperateLogService;
import com.fhs.core.trans.anno.TransMethodResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.dillon.lw.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.dillon.lw.framework.common.pojo.CommonResult.success;

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
    @TransMethodResult
    public CommonResult<PageResult<OperateLogRespVO>> pageOperateLog(@Valid OperateLogPageReqVO pageReqVO) {
        PageResult<OperateLogDO> pageResult = operateLogService.getOperateLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, OperateLogRespVO.class));
    }

    @Operation(summary = "导出操作日志")
    @GetMapping("/export-excel")
    @PreAuthorize("@ss.hasPermission('system:operate-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOperateLog(HttpServletResponse response, @Valid OperateLogPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OperateLogDO> list = operateLogService.getOperateLogPage(exportReqVO).getList();
        ExcelUtils.write(response, "操作日志.xls", "数据列表", OperateLogRespVO.class,
                TranslateUtils.translate(BeanUtils.toBean(list, OperateLogRespVO.class)));
    }

}
