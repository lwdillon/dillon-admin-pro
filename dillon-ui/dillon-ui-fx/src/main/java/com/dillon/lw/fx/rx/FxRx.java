package com.dillon.lw.fx.rx;

import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.CompletableTransformer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.core.SingleTransformer;

/**
 * JavaFX ViewModel 生命周期与 RxJava 链的绑定工具。
 */
public final class FxRx {

    private FxRx() {
    }

    public static <T> LifecycleTransformer<T> bindTo(BaseViewModel owner) {
        return new LifecycleTransformer<T>(owner);
    }

    public static final class LifecycleTransformer<T> implements SingleTransformer<T, T>, CompletableTransformer {

        private final BaseViewModel owner;

        private LifecycleTransformer(BaseViewModel owner) {
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
