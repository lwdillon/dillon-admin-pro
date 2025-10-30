package com.dillon.lw.fx.eventbus;

import com.google.common.eventbus.AsyncEventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBusCenter {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final AsyncEventBus eventBus = new AsyncEventBus(executor);

    public static AsyncEventBus get() {
        return eventBus;
    }
}