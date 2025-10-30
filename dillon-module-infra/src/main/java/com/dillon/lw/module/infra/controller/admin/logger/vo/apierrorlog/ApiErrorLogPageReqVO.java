package com.dillon.lw.module.infra.controller.admin.logger.vo.apierrorlog;

import com.dillon.lw.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.dillon.lw.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - API 错误日志分页 Request VO")
@Data
public class ApiErrorLogPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "666")
    private Long userId;

    @Schema(description = "用户类型", example = "1")
    private Integer userType;

    @Schema(description = "应用名", example = "dashboard")
    private String applicationName;

    @Schema(description = "请求地址", example = "/xx/yy")
    private String requestUrl;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "异常发生时间")
    private LocalDateTime[] exceptionTime;

    @Schema(description = "处理状态", example = "0")
    private Integer processStatus;

}
