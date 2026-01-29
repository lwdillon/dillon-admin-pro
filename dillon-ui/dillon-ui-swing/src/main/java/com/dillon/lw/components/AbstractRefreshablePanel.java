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
public abstract class AbstractRefreshablePanel extends JPanel {

    private boolean dirty = false; // 标记是否需要刷新
    /**
     * 构造方法
     */
    public AbstractRefreshablePanel() {
        // 1. 自动管理生命周期：当组件显示在屏幕上时注册，移除时注销
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
     * 当组件显示在屏幕上时调用
     */
    private void onPanelShow() {
        EventBusCenter.get().register(this);
        // 如果在后台期间收到了刷新指令，切换回来时立即刷新
        if (dirty) {
            refreshWithCheck();
        }
    }

    /**
     * 当组件从屏幕移除时调用
     */
    private void onPanelHide() {
        EventBusCenter.get().unregister(this);
    }

    /**
     * 刷新数据事件处理方法
     *
     * @param event 刷新数据事件
     */
    @Subscribe
    public final void onRefreshEvent(RefreshDataEvent event) {
        // 2. 性能优化：如果当前 Tab 不可见，先标记，不执行耗时刷新
        if (!isShowing()) {
            dirty = true;
            return;
        }
        refreshWithCheck();
    }

    /**
     * 刷新数据方法，检查是否需要刷新
     */
    private void refreshWithCheck() {
        dirty = false;
        // 3. 线程安全：强制切换到 EDT
        SwingUtilities.invokeLater(this::doRefresh);
    }

    /**
     * 子类实现具体的刷新逻辑（如调 API、更新表格）
     */
    protected abstract void doRefresh();
}