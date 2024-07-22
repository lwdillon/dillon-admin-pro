package com.lw.dillon.admin.module.system.controller.admin.logger;

import com.lw.dillon.admin.framework.apilog.core.annotation.ApiAccessLog;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageParam;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.framework.excel.core.util.ExcelUtils;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.lw.dillon.admin.module.system.dal.dataobject.logger.LoginLogDO;
import com.lw.dillon.admin.module.system.service.logger.LoginLogService;
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

@Tag(name = "管理后台 - 登录日志")
@RestController
@RequestMapping("/system/login-log")
@Validated
public class LoginLogController {

    @Resource
    private LoginLogService loginLogService;

    @GetMapping("/page")
    @Operation(summary = "获得登录日志分页列表")
    @PreAuthorize("@ss.hasPermission('system:login-log:query')")
    public CommonResult<PageResult<LoginLogRespVO>> getLoginLogPage(@Valid LoginLogPageReqVO pageReqVO) {
        PageResult<LoginLogDO> pageResult = loginLogService.getLoginLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LoginLogRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除登录日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:login-log:delete')")
    public CommonResult<Boolean> deleteLoginLog(@RequestParam("id") Long id) {
        loginLogService.deleteLoginLog(id);
        return success(true);
    }
    @DeleteMapping("/clear")
    @Operation(summary = "清空登录日志")
    @PreAuthorize("@ss.hasPermission('system:login-log:clear')")
    public CommonResult<Boolean> clearLoginLog() {
        loginLogService.clearLoginLog();
        return success(true);
    }


    @GetMapping("/export")
    @Operation(summary = "导出登录日志 Excel")
    @PreAuthorize("@ss.hasPermission('system:login-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportLoginLog(HttpServletResponse response, @Valid LoginLogPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<LoginLogDO> list = loginLogService.getLoginLogPage(exportReqVO).getList();
        // 输出
        ExcelUtils.write(response, "登录日志.xls", "数据列表", LoginLogRespVO.class,
                BeanUtils.toBean(list, LoginLogRespVO.class));
    }

}
