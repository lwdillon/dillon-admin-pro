package com.dillon.lw.module.system.sse;

import com.dillon.lw.module.system.api.sse.SseMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TaskMessagePublisher {

    @Resource
    private TaskSnapshotHolder snapshotHolder;
    @Resource
    private SseClientManager sseClientManager;

    public void publish(String taskId, String type, Object payload) {

        snapshotHolder.update(taskId, type, payload);

        SseMessage<Object> msg = new SseMessage<>();
        msg.setTaskId(taskId);
        msg.setType(type);
        msg.setPayload(payload);
        msg.setTimestamp(System.currentTimeMillis());

        sseClientManager.broadcast(taskId, msg);
    }
}