package com.dillon.lw.module.infra.controller.admin.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Swing 客户端更新信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdateRespVO {

    @Schema(description = "最新版本号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0.1")
    private String version;


    @Schema(description = "更新说明", example = "修复若干问题")
    private String notes;
}