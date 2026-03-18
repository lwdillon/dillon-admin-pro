package com.dillon.lw.fx.mvvm.base;

import com.dillon.lw.fx.eventbus.EventBusCenter;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseViewModel {

    /**
     * 当前 ViewModel 持有的所有 RxJava 订阅。
     * <p>
     * 这样页面销毁时可以统一释放订阅，避免旧请求在 ViewModel 已经失效后继续回调 UI。
     * </p>
     */
    private final CompositeDisposable disposables = new CompositeDisposable();

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

    /**
     * 记录一个订阅，并在 ViewModel 销毁时统一释放。
     */
    protected final <T extends Disposable> T track(T disposable) {
        disposables.add(disposable);
        return disposable;
    }

    /**
     * 暴露给 Rx 生命周期绑定工具使用。
     */
    public final <T extends Disposable> T bindDisposable(T disposable) {
        return track(disposable);
    }

    public void dispose() {
        disposables.clear();
        if (isRegisterEventBus()) {
            EventBusCenter.get().unregister(this);
        }
    }
}
