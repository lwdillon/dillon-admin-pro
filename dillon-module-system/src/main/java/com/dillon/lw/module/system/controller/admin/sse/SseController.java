package com.dillon.lw.module.system.controller.admin.sse;

import com.dillon.lw.module.system.sse.SseClientManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

@RestController
@RequestMapping("/system/sse")
public class SseController {

    @Resource
    private SseClientManager sseClientManager;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @RequestParam String taskId,
            @RequestParam String clientId,
            @RequestParam String userId) {

        return sseClientManager.connect(taskId, userId,clientId);
    }
}