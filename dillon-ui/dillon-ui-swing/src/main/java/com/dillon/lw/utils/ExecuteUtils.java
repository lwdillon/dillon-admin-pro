package com.dillon.lw.utils;

import com.dillon.lw.components.WaitPane;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.view.frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Swing 异步任务执行工具类
 * <p>
 * 核心目标：
 * 1. 自动切换线程：耗时任务在后台线程池执行，UI 更新自动回到 Swing 事件派发线程 (EDT)。
 * 2. 异常闭环管理：自动捕获并解包异步异常，统一交给 {@link SwingExceptionHandler} 处理。
 * 3. 资源自动清理：提供 onComplete 回调，确保无论成功失败都能重置 UI 状态（如按钮恢复点击）。
 * 4. 业务语义增强：支持对 {@link CommonResult} 类型业务结果的自动校验和成功提示。
 * 5. 交互体验增强：支持一键开启 {@link WaitPane} 加载动画，提升用户交互体验。
 * </p>
 */
public class ExecuteUtils {

    /** Swing 事件派发线程执行器 */
    private static final Executor EDT = SwingUtilities::invokeLater;

    /**
     * 执行带完整生命周期管理的异步任务
     *
     * @param <T>             后台任务返回的数据类型
     * @param task            [后台线程] 执行的任务逻辑。注意：此处不应包含任何直接操作 UI 的代码。
     * @param onSuccess       [EDT 线程] 任务成功后的回调。入参为 task 的返回值。
     * @param onComplete      [EDT 线程] 任务结束后的清理逻辑。无论成功还是失败都会执行。
     * @param successMsg      [EDT 线程] 可选的成功提示消息。若传入且业务逻辑判定为成功，则自动弹出提示。
     * @param waitPane        [EDT 线程] 可选的 WaitPane 实例。若传入，执行期间会自动显示/隐藏 Loading。
     */
    public static <T> void execute(
            Supplier<T> task,
            Consumer<T> onSuccess,
            Runnable onComplete,
            String successMsg,
            WaitPane waitPane
    ) {
        toggleWaitPane(waitPane, true);

        CompletableFuture
                .supplyAsync(task)
                .thenAcceptAsync(result -> {
                    try {
                        if (onSuccess != null) {
                            onSuccess.accept(result);
                        }
                        processSuccessMessage(result, successMsg);
                    } catch (Exception uiEx) {
                        SwingExceptionHandler.handle(uiEx);
                    }
                }, EDT)
                .whenCompleteAsync((r, t) -> {
                    try {
                        toggleWaitPane(waitPane, false);

                        if (onComplete != null) {
                            onComplete.run();
                        }

                        if (t != null) {
                            SwingExceptionHandler.handle(unwrap(t));
                        }
                    } catch (Exception e) {
                        SwingExceptionHandler.handle(e);
                    }
                }, EDT);
    }

    /**
     * 统一管理 WaitPane 的显示与隐藏，减少调用方重复代码。
     */
    private static void toggleWaitPane(WaitPane waitPane, boolean show) {
        if (waitPane == null) {
            return;
        }

        runOnEdt(() -> {
            if (show) {
                waitPane.showMessageLayer();
                return;
            }

            waitPane.hideMessageLayer();
            // 兼容历史行为：关闭 loading 后恢复基础输入控件可用态。
            if (waitPane.getComponentCount() > 0 && waitPane.getComponent(0) instanceof Container) {
                setContainerEnabled((Container) waitPane.getComponent(0), true);
            }
        });
    }

    private static void runOnEdt(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        } else {
            SwingUtilities.invokeLater(action);
        }
    }

    /**
     * 递归设置容器及其子组件的启用状态
     */
    private static void setContainerEnabled(Container container, boolean enabled) {
        for (Component child : container.getComponents()) {
            if (child instanceof JButton || child instanceof JTextField || child instanceof JComboBox) {
                child.setEnabled(enabled);
            }
            if (child instanceof Container) {
                setContainerEnabled((Container) child, enabled);
            }
        }
    }

    /** 处理成功提示弹窗。 */
    private static <T> void processSuccessMessage(T result, String successMsg) {
        if (successMsg == null || successMsg.isEmpty()) {
            return;
        }

        boolean shouldShow = true;
        if (result instanceof CommonResult) {
            shouldShow = ((CommonResult<?>) result).isSuccess();
        }

        if (shouldShow) {
            WMessage.showMessageSuccess(MainFrame.getInstance(), successMsg);
        }
    }

    /** 解包 CompletableFuture 包装的异常。 */
    private static Throwable unwrap(Throwable t) {
        return (t instanceof CompletionException && t.getCause() != null)
                ? t.getCause()
                : t;
    }

    // --- 各种便捷重载方法 ---

    /**
     * 极简模式：仅执行任务并处理成功后的数据更新
     */
    public static <T> void execute(Supplier<T> task, Consumer<T> onSuccess) {
        execute(task, onSuccess, null, null, null);
    }

    /**
     * 提示模式：执行任务并在成功后弹出指定的成功消息
     */
    public static <T> void execute(Supplier<T> task, Consumer<T> onSuccess, String successMsg) {
        execute(task, onSuccess, null, successMsg, null);
    }

    /**
     * 加载模式：执行任务并显示 Loading 动画，成功后处理数据
     */
    public static <T> void execute(Supplier<T> task, Consumer<T> onSuccess, WaitPane waitPane) {
        execute(task, onSuccess, null, null, waitPane);
    }

    /**
     * 完整 UI 闭环模式：执行任务、更新 UI，并确保在结束后重置 UI 状态
     */
    public static <T> void execute(Supplier<T> task, Consumer<T> onSuccess, Runnable onComplete) {
        execute(task, onSuccess, onComplete, null, null);
    }

    /**
     * 行为模式：执行一个没有返回值的后台任务，并处理清理逻辑
     */
    public static void execute(Runnable task, Runnable onComplete) {
        execute(() -> {
            task.run();
            return null;
        }, null, onComplete, null, null);
    }
}
