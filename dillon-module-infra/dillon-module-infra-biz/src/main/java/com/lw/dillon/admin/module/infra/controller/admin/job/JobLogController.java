package com.lw.dillon.admin.module.infra.controller.admin.job;

import com.lw.dillon.admin.framework.apilog.core.annotation.ApiAccessLog;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageParam;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.framework.excel.core.util.ExcelUtils;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.log.JobLogPageReqVO;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.log.JobLogRespVO;
import com.lw.dillon.admin.module.infra.dal.dataobject.job.JobLogDO;
import com.lw.dillon.admin.module.infra.service.job.JobLogService;
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

@Tag(name = "管理后台 - 定时任务日志")
@RestController
@RequestMapping("/infra/job-log")
@Validated
public class JobLogController {

    @Resource
    private JobLogService jobLogService;

    @GetMapping("/get")
    @Operation(summary = "获得定时任务日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<JobLogRespVO> getJobLog(@RequestParam("id") Long id) {
        JobLogDO jobLog = jobLogService.getJobLog(id);
        return success(BeanUtils.toBean(jobLog, JobLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得定时任务日志分页")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<PageResult<JobLogRespVO>> getJobLogPage(@Valid JobLogPageReqVO pageVO) {
        PageResult<JobLogDO> pageResult = jobLogService.getJobLogPage(pageVO);
        return success(BeanUtils.toBean(pageResult, JobLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出定时任务日志 Excel")
    @PreAuthorize("@ss.hasPermission('infra:job:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportJobLogExcel(@Valid JobLogPageReqVO exportReqVO,
                                  HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<JobLogDO> list = jobLogService.getJobLogPage(exportReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "任务日志.xls", "数据", JobLogRespVO.class,
                BeanUtils.toBean(list, JobLogRespVO.class));
    }

}