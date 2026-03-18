package com.dillon.lw.fx.rx;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;

import java.util.concurrent.Executor;

/**
 * JavaFX 线程相关的 RxJava 调度工具。
 * <p>
 * 这层封装和 Swing 模块里的 {@code SwingSchedulers} 保持一致，
 * 目的是把“后台请求跑 IO 线程，界面回调回到 FX Application Thread”写成固定模式。
 * </p>
 * <p>
 * 这样页面层只要看到 {@code observeOn(FxSchedulers.fx())}，
 * 就能立刻知道后续代码运行在 JavaFX UI 线程上。
 * </p>
 */
public final class FxSchedulers {

    /**
     * 统一的 JavaFX UI 线程执行器。
     * <p>
     * 如果当前已经在 FX Application Thread，直接执行；
     * 否则通过 {@link Platform#runLater(Runnable)} 切回 UI 线程。
     * </p>
     */
    private static final Executor FX_EXECUTOR = new Executor() {
        @Override
        public void execute(Runnable command) {
            if (Platform.isFxApplicationThread()) {
                command.run();
            } else {
                Platform.runLater(command);
            }
        }
    };

    /**
     * 全局复用的 JavaFX Scheduler。
     */
    private static final Scheduler FX_SCHEDULER = Schedulers.from(FX_EXECUTOR);

    private FxSchedulers() {
    }

    /**
     * 返回 JavaFX UI 线程对应的 RxJava Scheduler。
     */
    public static Scheduler fx() {
        return FX_SCHEDULER;
    }

    /**
     * 把一个动作投递到 JavaFX UI 线程。
     */
    public static void runOnFx(Runnable action) {
        FX_EXECUTOR.execute(action);
    }

    /**
     * 始终在下一轮 JavaFX 事件循环中执行动作。
     */
    public static void postOnFx(Runnable action) {
        Platform.runLater(action);
    }
}
