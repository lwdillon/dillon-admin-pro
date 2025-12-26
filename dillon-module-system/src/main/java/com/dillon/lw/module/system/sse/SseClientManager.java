package com.dillon.lw.module.system.sse;

import com.dillon.lw.module.system.api.sse.SseMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseClientManager {

    // taskId -> roleId -> SseEmitter（共享 emitter）
    private final Map<String, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();

    // taskId -> roleId -> clientId 列表（用于判断新客户端）
    private final Map<String, Map<String, Set<String>>> roleClients = new ConcurrentHashMap<>();

//    @Resource
//    private TaskPermissionService permissionService;
    @Resource
    private TaskSnapshotHolder snapshotHolder;

    public SseEmitter connect(String taskId, String roleId, String clientId) {

//        if (!permissionService.hasPermission(taskId, roleId)) {
//            throw new RuntimeException("Role not permitted for task " + taskId);
//        }
//       String clientId= Convert.toStr(SecurityFrameworkUtils.getLoginUserId(),"") ;

        // 获取或创建 emitter
        Map<String, SseEmitter> roleMap = emitters.computeIfAbsent(taskId, k -> new ConcurrentHashMap<>());
        SseEmitter emitter = roleMap.computeIfAbsent(roleId, k -> {
            SseEmitter e = new SseEmitter(0L);
            e.onCompletion(() -> remove(taskId, roleId, clientId));
            e.onTimeout(() -> remove(taskId, roleId, clientId));
            e.onError(err -> remove(taskId, roleId, clientId));
            return e;
        });

        // 获取角色下客户端列表
        Map<String, Set<String>> clientMap = roleClients.computeIfAbsent(taskId, k -> new ConcurrentHashMap<>());
        Set<String> clients = clientMap.computeIfAbsent(roleId, k -> ConcurrentHashMap.newKeySet());

        boolean isNewClient = clients.add(clientId); // true 表示新客户端

        // ⭐ 新客户端发送快照
        if (isNewClient) {
            snapshotHolder.getAll(taskId).forEach((type, payload) -> {
                try {
                    emitter.send(build(taskId, type, payload));
                } catch (Exception ignored) {}
            });
        }

        return emitter;
    }

    public void broadcast(String taskId, SseMessage<?> msg) {
        Map<String, SseEmitter> roleMap = emitters.get(taskId);
        if (roleMap == null) return;

        roleMap.values().forEach(emitter -> {
            try {
                emitter.send(msg);
            } catch (Exception e) {
                emitter.complete();
            }
        });
    }

    private void remove(String taskId, String roleId, String clientId) {
        // 移除客户端
        Map<String, Set<String>> clientMap = roleClients.get(taskId);
        if (clientMap != null) {
            Set<String> clients = clientMap.get(roleId);
            if (clients != null) clients.remove(clientId);
        }

        // 如果没有客户端了，可以选择关闭 emitter
        Map<String, SseEmitter> roleMap = emitters.get(taskId);
        if (roleMap != null) {
            if (clientMap == null || clientMap.get(roleId) == null || clientMap.get(roleId).isEmpty()) {
                roleMap.remove(roleId);
            }
        }
    }

    private SseMessage<Object> build(String taskId, String type, Object payload) {
        SseMessage<Object> msg = new SseMessage<>();
        msg.setTaskId(taskId);
        msg.setType(type);
        msg.setPayload(payload);
        msg.setTimestamp(System.currentTimeMillis());
        return msg;
    }
}