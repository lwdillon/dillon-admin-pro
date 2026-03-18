package com.dillon.lw.components;

import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.RefreshDataEvent;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;
import java.awt.event.HierarchyEvent;

/**
 * 可刷新的面板基类
 * <p>
 * 1. 自动管理生命周期：当组件显示在屏幕上时注册，移除时注销
 * 2. 性能优化：如果当前 Tab 不可见，先标记，不执行耗时刷新
 * 3. 线程安全：强制切换到 EDT 执行刷新逻辑
 *
 * @author liwen
 * @date 2022/07/08
 */
public abstract class AbstractRefreshablePanel extends AbstractDisposablePanel {

    /**
     * 是否需要刷新
     */
    private boolean dirty = false;

    /**
     * 是否已经注册到 EventBus
     */
    private boolean registered = false;

    /**
     * 构造方法
     */
    public AbstractRefreshablePanel() {
        // 监听显示/隐藏事件
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) {
                    onPanelShow();
                } else {
                    onPanelHide();
                }
            }
        });
    }

    /**
     * 当面板显示时调用
     */
    private void onPanelShow() {
        // 注册 EventBus
        if (!registered) {
            EventBusCenter.get().register(this);
            registered = true;
        }

        // 如果在后台期间收到刷新指令，切换回来时立即刷新
        if (dirty) {
            refreshWithCheck();
        }
    }

    /**
     * 当面板隐藏时调用
     */
    private void onPanelHide() {
        // 注销 EventBus
        if (registered) {
            EventBusCenter.get().unregister(this);
            registered = false;
        }
    }

    /**
     * 处理刷新事件
     *
     * @param event 刷新数据事件
     */
    @Subscribe
    public final void onRefreshEvent(RefreshDataEvent event) {
        // 面板不可见时，只标记 dirty
        if (!isShowing()) {
            dirty = true;
            return;
        }
        refreshWithCheck();
    }

    /**
     * 刷新方法检查
     */
    private void refreshWithCheck() {
        dirty = false;
        SwingUtilities.invokeLater(this::doRefresh);
    }

    /**
     * 子类实现具体刷新逻辑
     */
    protected abstract void doRefresh();
}
