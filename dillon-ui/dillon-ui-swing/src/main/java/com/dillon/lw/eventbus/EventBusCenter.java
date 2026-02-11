package com.dillon.lw.eventbus;

import com.google.common.eventbus.AsyncEventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全局事件总线单例入口。
 */
public final class EventBusCenter {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final AsyncEventBus EVENT_BUS = new AsyncEventBus(EXECUTOR);

    private EventBusCenter() {
    }

    public static AsyncEventBus get() {
        return EVENT_BUS;
    }
}
