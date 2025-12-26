package com.dillon.lw.forest.interceptors;

import com.dillon.lw.forest.config.HAStatusManager;
import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestRequest;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HASmartSource implements AddressSource {

    private static final ScheduledExecutorService HEALTH_CHECKER =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "ha-master-health-check");
                t.setDaemon(true);
                return t;
            });

    public HASmartSource() {
        HEALTH_CHECKER.scheduleAtFixedRate(
                this::checkMasterHealth,
                5, 5, TimeUnit.SECONDS
        );
    }

    @Override
    public ForestAddress getAddress(ForestRequest request) {
        if (HAStatusManager.isMasterAlive()) {
            return master();
        }
        return slave();
    }

    private ForestAddress master() {
        return new ForestAddress(
                HAStatusManager.SCHEME,
                HAStatusManager.MASTER_IP,
                HAStatusManager.PORT,
                HAStatusManager.BASE_PATH
        );
    }

    private ForestAddress slave() {
        return new ForestAddress(
                HAStatusManager.SCHEME,
                HAStatusManager.SLAVE_IP,
                HAStatusManager.PORT,
                HAStatusManager.BASE_PATH
        );
    }

    /**
     * 主机健康探测（仅在失败状态 + 冷却期后执行）
     */
    private void checkMasterHealth() {
        if (!HAStatusManager.canProbeRecover()) {
            return;
        }

        try (Socket socket = new Socket()) {
            socket.connect(
                    new InetSocketAddress(
                            HAStatusManager.MASTER_IP,
                            HAStatusManager.PORT
                    ),
                    2000
            );
            HAStatusManager.markMasterRecover();
            System.out.println("【生产通知】主机健康恢复，流量已切回主机");
        } catch (Exception ignored) {
            // 保持备机
        }
    }
}