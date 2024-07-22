package com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.category;

import com.lw.dillon.admin.framework.common.enums.CommonStatusEnum;
import com.lw.dillon.admin.framework.common.pojo.PageParam;
import com.lw.dillon.admin.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.lw.dillon.admin.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - BPM 流程分类分页 Request VO")
@Data
public class BpmCategoryPageReqVO extends PageParam {

    @Schema(description = "分类名", example = "王五")
    private String name;

    @Schema(description = "分类标志", example = "OA")
    private String code;

    @Schema(description = "分类状态", example = "1")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}