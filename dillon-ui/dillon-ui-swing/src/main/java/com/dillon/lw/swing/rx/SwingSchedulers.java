package com.dillon.lw.swing.rx;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.SwingUtilities;
import java.util.concurrent.Executor;

/**
 * Swing 线程相关的 RxJava 调度工具。
 * <p>
 * 这层封装的目的有两个：
 * 1. 统一项目里“切回 EDT”的写法，避免每个页面重复 new `Schedulers.from(...)`；
 * 2. 让业务代码表达成固定模式：后台请求走 `Schedulers.io()`，界面回调走 `SwingSchedulers.edt()`。
 * </p>
 * <p>
 * 这样后续排查线程问题时，只要看到 `observeOn(SwingSchedulers.edt())`，
 * 就能明确知道下面的代码运行在 Swing 的 EDT 上。
 * </p>
 */
public final class SwingSchedulers {

    /**
     * 统一的 EDT 执行器。
     * <p>
     * 如果当前已经在 EDT，直接执行；
     * 否则通过 `SwingUtilities.invokeLater(...)` 异步切回 UI 线程。
     * </p>
     */
    private static final Executor EDT_EXECUTOR = new Executor() {
        @Override
        public void execute(Runnable command) {
            if (SwingUtilities.isEventDispatchThread()) {
                command.run();
            } else {
                SwingUtilities.invokeLater(command);
            }
        }
    };

    /**
     * 全局复用的 EDT Scheduler。
     * <p>
     * Scheduler 本身是无状态的，做成单例可以避免页面层重复创建。
     * </p>
     */
    private static final Scheduler EDT_SCHEDULER = Schedulers.from(EDT_EXECUTOR);

    private SwingSchedulers() {
    }

    /**
     * 返回 Swing EDT 对应的 RxJava Scheduler。
     */
    public static Scheduler edt() {
        return EDT_SCHEDULER;
    }

    /**
     * 直接把一个动作投递到 EDT。
     * <p>
     * 这个方法适合少量非 Rx 场景，例如页面销毁、遮罩层显隐等零散 UI 动作。
     * </p>
     */
    public static void runOnEdt(Runnable action) {
        EDT_EXECUTOR.execute(action);
    }

    /**
     * 始终在下一轮 EDT 事件循环中执行动作。
     * <p>
     * 这个方法适合处理“当前回调链结束后再做下一步 UI 动作”的场景，
     * 例如先让本次请求的 `doFinally` 完成，再启动下一次请求。
     * </p>
     */
    public static void postOnEdt(Runnable action) {
        SwingUtilities.invokeLater(action);
    }
}
