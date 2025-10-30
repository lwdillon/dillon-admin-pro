package com.dillon.lw.module.infra.controller.admin.job.vo.job;

import com.dillon.lw.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 定时任务分页 Request VO")
@Data
public class JobPageReqVO extends PageParam {

    @Schema(description = "任务名称，模糊匹配", example = "测试任务")
    private String name;

    @Schema(description = "任务状态，参见 JobStatusEnum 枚举", example = "1")
    private Integer status;

    @Schema(description = "处理器的名字，模糊匹配", example = "sysUserSessionTimeoutJob")
    private String handlerName;

}
