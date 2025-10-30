package com.dillon.lw.fx.mvvm.base;

import com.dillon.lw.fx.eventbus.EventBusCenter;

public abstract class BaseViewModel {
    /**
     * 初始化方法，在View构造完成后调用
     */
    public void init() {
        // 默认实现为空，子类可重写
        EventBusCenter.get().register(this);
    }

    public void dispose() {

        EventBusCenter.get().unregister(this);

    }
}