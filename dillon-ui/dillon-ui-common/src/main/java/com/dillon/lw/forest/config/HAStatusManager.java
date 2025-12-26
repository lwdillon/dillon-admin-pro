package com.dillon.lw.forest.config;

import cn.hutool.core.convert.Convert;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class HAStatusManager {
    public static final int PORT = Convert.toInt(System.getProperty("app.server.port"), 48080);
    public static final String BASE_PATH = Convert.toStr(System.getProperty("app.server.basePath"), null);
    public static final String SCHEME = Convert.toStr(System.getProperty("app.server.scheme"), "http");

    public static final String MASTER_IP = Convert.toStr(System.getProperty("app.server.master.ip"), "127.0.0.1");
    public static final String SLAVE_IP = Convert.toStr(System.getProperty("app.server.slave.ip"), "127.0.0.1");

    /** 主机状态 */

    private static final AtomicBoolean MASTER_ALIVE = new AtomicBoolean(true);

    private static final AtomicLong LAST_FAIL_TIME = new AtomicLong(0);

    /** 主机最小冷却时间（避免抖动） */
    private static final long COOL_DOWN = 30_000;

    public static boolean isMasterAlive() {
        return MASTER_ALIVE.get();
    }

    /** 网络异常触发 */
    public static void markMasterFail() {
        if (MASTER_ALIVE.compareAndSet(true, false)) {
            LAST_FAIL_TIME.set(System.currentTimeMillis());
        }
    }

    /** 健康检查成功触发 */
    public static void markMasterRecover() {
        MASTER_ALIVE.set(true);
    }

    public static boolean canProbeRecover() {
        return !MASTER_ALIVE.get()
                && System.currentTimeMillis() - LAST_FAIL_TIME.get() > COOL_DOWN;
    }
}