package com.dillon.lw.components.notice;

import javax.swing.*;

/**
 * 通知视图创建工厂。不同通知组可以提供自己的通知卡片样式。
 */
@FunctionalInterface
public interface InfoCenterNotificationViewFactory {

    JComponent createView(InfoCenterItem item, Runnable onClose, Runnable onChanged);
}
