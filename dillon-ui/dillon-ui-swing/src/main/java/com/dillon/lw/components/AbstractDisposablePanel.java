package com.dillon.lw.components;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 支持统一托管 RxJava 订阅的 Swing 面板基类。
 */
public abstract class AbstractDisposablePanel extends JPanel implements SwingDisposable {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final AtomicBoolean disposed = new AtomicBoolean();

    /**
     * 跟踪当前面板持有的订阅，方便在页面关闭时统一释放。
     */
    protected final <T extends Disposable> T track(T disposable) {
        if (disposable == null) {
            return null;
        }
        if (disposed.get()) {
            disposable.dispose();
            return disposable;
        }
        disposables.add(disposable);
        return disposable;
    }

    /**
     * 暴露给 Rx 生命周期绑定工具使用。
     */
    public final <T extends Disposable> T bindDisposable(T disposable) {
        return track(disposable);
    }

    @Override
    public void disposeResources() {
        if (disposed.compareAndSet(false, true)) {
            disposables.dispose();
        }
    }

    @Override
    public void removeNotify() {
        disposeResources();
        super.removeNotify();
    }
}
