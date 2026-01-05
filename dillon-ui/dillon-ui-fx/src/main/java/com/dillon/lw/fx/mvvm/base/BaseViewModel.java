package com.dillon.lw.fx.mvvm.base;

import com.dillon.lw.fx.eventbus.EventBusCenter;

public abstract class BaseViewModel {

    /**
     * 初始化方法，在View构造完成后调用
     */
    public void init() {
        if (isRegisterEventBus()) {
            EventBusCenter.get().register(this);
        }
    }

    /**
     * 是否注册事件总线，默认返回 true。子类可重写
     */
    protected boolean isRegisterEventBus() {
        return true;
    }

    public void dispose() {
        if (isRegisterEventBus()) {
            EventBusCenter.get().unregister(this);
        }
    }
}