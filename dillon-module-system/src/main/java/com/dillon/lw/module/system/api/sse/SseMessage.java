package com.dillon.lw.module.system.api.sse;

import lombok.Data;

@Data
public class SseMessage<T> {
    private String taskId;
    private String type;        // STATUS / STEP / LOG
    private T payload;
    private long timestamp;
}