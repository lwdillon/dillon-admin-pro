package com.dillon.lw.module.system.sse;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskSnapshotHolder {

    // taskId -> (type -> last payload)
    private final Map<String, Map<String, Object>> snapshots = new ConcurrentHashMap<>();

    public void update(String taskId, String type, Object payload) {
        snapshots
            .computeIfAbsent(taskId, k -> new ConcurrentHashMap<>())
            .put(type, payload);
    }

    public Map<String, Object> getAll(String taskId) {
        return snapshots.getOrDefault(taskId,new HashMap<>());
    }

    public void clear(String taskId) {
        snapshots.remove(taskId);
    }
}