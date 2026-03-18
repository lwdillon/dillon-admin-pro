package com.dillon.lw.swing.rx;

import com.dillon.lw.components.AbstractDisposablePanel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.CompletableTransformer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.core.SingleTransformer;

/**
 * Swing 组件生命周期与 RxJava 链的绑定工具。
 */
public final class SwingRx {

    private SwingRx() {
    }

    public static <T> LifecycleTransformer<T> bindTo(AbstractDisposablePanel owner) {
        return new LifecycleTransformer<T>(owner);
    }

    public static final class LifecycleTransformer<T> implements SingleTransformer<T, T>, CompletableTransformer {

        private final AbstractDisposablePanel owner;

        private LifecycleTransformer(AbstractDisposablePanel owner) {
            this.owner = owner;
        }

        @Override
        public SingleSource<T> apply(Single<T> upstream) {
            return upstream.doOnSubscribe(owner::bindDisposable);
        }

        @Override
        public CompletableSource apply(Completable upstream) {
            return upstream.doOnSubscribe(owner::bindDisposable);
        }
    }
}
