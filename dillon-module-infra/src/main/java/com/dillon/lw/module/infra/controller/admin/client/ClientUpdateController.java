package com.dillon.lw.module.infra.controller.admin.client;

import cn.hutool.core.util.StrUtil;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.infra.controller.admin.client.vo.ClientUpdateRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.StandardCharsets;

import static com.dillon.lw.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 客户端更新")
@RestController
@RequestMapping("/infra/client/update/")
public class ClientUpdateController {

    @Value("${dillon.client-update.enabled:true}")
    private Boolean enabled;

    @Value("${dillon.client-update.swing.version}")
    private String versionSwing;

    @Value("${dillon.client-update.swing.notes:}")
    private String notesSwing;


    @Value("${dillon.client-update.swing.jar-file}")
    private String jarFileSwing;


    @Value("${dillon.client-update.javafx.version}")
    private String versionJavaFx;

    @Value("${dillon.client-update.javafx.notes:}")
    private String notesJavaFx;


    @Value("${dillon.client-update.javafx.jar-file}")
    private String jarFileJavaFx;


    @GetMapping("/latest/swing")
    @PermitAll
    @Operation(summary = "获取客户端最新版本信息")
    public CommonResult<ClientUpdateRespVO> latestSwing(HttpServletRequest request) {
        if (!Boolean.TRUE.equals(enabled)) {
        }
        return success(new ClientUpdateRespVO(versionSwing, notesSwing));
    }

    @GetMapping("/latest/javafx")
    @PermitAll
    @Operation(summary = "获取客户端最新版本信息")
    public CommonResult<ClientUpdateRespVO> latestJavaFX(HttpServletRequest request) {
        if (!Boolean.TRUE.equals(enabled)) {

        }
        return success(new ClientUpdateRespVO(versionJavaFx, notesJavaFx));
    }

    @GetMapping("/download/swing")
    @PermitAll
    @Operation(summary = "下载 Swing 客户端安装包")
    public ResponseEntity<Resource> downloadSwing() {
        if (!Boolean.TRUE.equals(enabled) || StrUtil.isBlank(jarFileSwing)) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(jarFileSwing);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String filename = file.getName();
        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/java-archive"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"")
                .body(resource);
    }

    @GetMapping("/download/javafx")
    @PermitAll
    @Operation(summary = "下载 JavaFx 客户端安装包")
    public ResponseEntity<Resource> downloadJavaFx() {
        if (!Boolean.TRUE.equals(enabled) || StrUtil.isBlank(jarFileJavaFx)) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(jarFileJavaFx);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String filename = file.getName();
        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/java-archive"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"")
                .body(resource);
    }


}
