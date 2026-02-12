package com.dillon.lw.websocket;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * WebSocket 客户端服务（主备容灾版）。
 * <p>
 * 设计目标：
 * 1. 使用 application.properties 中的主备地址；
 * 2. 自动重连，失败后在主备之间切换；
 * 3. 暴露统一消息回调给 UI 层。
 * </p>
 */
@Slf4j
public final class WebSocketNoticeService {

    private static final WebSocketNoticeService INSTANCE = new WebSocketNoticeService();

    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "swing-ws-reconnect");
        t.setDaemon(true);
        return t;
    });

    private final AtomicReference<WebSocketClient> clientRef = new AtomicReference<>();
    private volatile ScheduledFuture<?> reconnectFuture;
    private volatile boolean running = false;
    private volatile int endpointIndex = 0;
    private volatile String token;
    private volatile MessageListener messageListener;

    private WebSocketNoticeService() {
    }

    public static WebSocketNoticeService getInstance() {
        return INSTANCE;
    }

    /**
     * 启动连接（幂等）。
     */
    public synchronized void start(String accessToken) {
        if (!isEnabled()) {
            log.info("WebSocket 已禁用，跳过启动");
            return;
        }
        this.token = accessToken;
        this.running = true;
        connectCurrentEndpoint();
    }

    /**
     * 停止连接并取消重连任务。
     */
    public synchronized void stop() {
        running = false;
        cancelReconnectTask();
        closeClient();
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    private void connectCurrentEndpoint() {
        if (!running) {
            return;
        }
        List<String> endpoints = resolveEndpoints();
        if (endpoints.isEmpty()) {
            log.warn("未配置可用的 WebSocket 地址，跳过连接");
            return;
        }
        if (endpointIndex < 0 || endpointIndex >= endpoints.size()) {
            endpointIndex = 0;
        }
        String endpoint = endpoints.get(endpointIndex);
        String wsUrl = appendToken(endpoint, token);

        closeClient();
        try {
            WebSocketClient client = new WebSocketClient(URI.create(wsUrl)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("WebSocket 连接成功: {}", wsUrl);
                }

                @Override
                public void onMessage(String message) {
                    dispatchMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.warn("WebSocket 连接关闭: code={}, reason={}, remote={}", code, reason, remote);
                    scheduleReconnect(true);
                }

                @Override
                public void onError(Exception ex) {
                    log.error("WebSocket 连接异常: {}", wsUrl, ex);
                    scheduleReconnect(true);
                }
            };
            clientRef.set(client);
            client.connect();
        } catch (Exception ex) {
            log.error("WebSocket 连接创建失败: {}", wsUrl, ex);
            scheduleReconnect(true);
        }
    }

    private void dispatchMessage(String rawMessage) {
        MessageListener listener = this.messageListener;
        if (listener != null) {
            listener.onRawMessage(rawMessage);
        }
    }

    private synchronized void scheduleReconnect(boolean switchEndpoint) {
        if (!running) {
            return;
        }
        if (switchEndpoint) {
            List<String> endpoints = resolveEndpoints();
            if (endpoints.size() > 1) {
                endpointIndex = (endpointIndex + 1) % endpoints.size();
            }
        }
        if (reconnectFuture != null && !reconnectFuture.isDone()) {
            return;
        }
        long delayMs = Convert.toLong(System.getProperty("app.websocket.reconnect.delay.ms"), 5000L);
        reconnectFuture = reconnectExecutor.schedule(this::connectCurrentEndpoint, delayMs, TimeUnit.MILLISECONDS);
    }

    private synchronized void cancelReconnectTask() {
        if (reconnectFuture != null && !reconnectFuture.isDone()) {
            reconnectFuture.cancel(true);
        }
        reconnectFuture = null;
    }

    private void closeClient() {
        WebSocketClient oldClient = clientRef.getAndSet(null);
        if (oldClient != null) {
            try {
                oldClient.close();
            } catch (Exception ignored) {
            }
        }
    }

    private boolean isEnabled() {
        return Convert.toBool(System.getProperty("app.websocket.enable"), true);
    }

    private List<String> resolveEndpoints() {
        String master = StrUtil.trim(System.getProperty("app.websocket.master.url"));
        String slave = StrUtil.trim(System.getProperty("app.websocket.slave.url"));
        List<String> endpoints = new ArrayList<>();
        if (StrUtil.isNotBlank(master)) {
            endpoints.add(master);
        }
        if (StrUtil.isNotBlank(slave) && !Objects.equals(slave, master)) {
            endpoints.add(slave);
        }
        // 兜底：若未配置 master/slave url，则基于现有主备 IP + path 自动拼接
        if (endpoints.isEmpty()) {
            String path = StrUtil.blankToDefault(System.getProperty("app.websocket.path"), "/infra/ws");
            String scheme = normalizeWsScheme(System.getProperty("app.server.scheme"));
            String masterIp = StrUtil.blankToDefault(System.getProperty("app.server.master.ip"), "127.0.0.1");
            String slaveIp = StrUtil.blankToDefault(System.getProperty("app.server.slave.ip"), "127.0.0.1");
            int port = Convert.toInt(System.getProperty("app.server.port"), 48080);
            endpoints.add(String.format("%s://%s:%d%s", scheme, masterIp, port, path));
            if (!Objects.equals(masterIp, slaveIp)) {
                endpoints.add(String.format("%s://%s:%d%s", scheme, slaveIp, port, path));
            }
        }
        return endpoints;
    }

    private String normalizeWsScheme(String scheme) {
        if ("https".equalsIgnoreCase(scheme)) {
            return "wss";
        }
        return "ws";
    }

    private String appendToken(String endpoint, String accessToken) {
        if (StrUtil.isBlank(accessToken)) {
            return endpoint;
        }
        return endpoint.contains("?")
                ? endpoint + "&token=" + accessToken
                : endpoint + "?token=" + accessToken;
    }

    public interface MessageListener {
        void onRawMessage(String rawMessage);
    }
}

