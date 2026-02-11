package com.dillon.lw.eventbus.event;

import javax.swing.*;

/**
 * 请求主界面新增一个 Tab 的事件。
 */
public class AddMainTabEvent {
    /** Tab 标题 */
    private String tabName;
    /** Tab 图标路径 */
    private String icon;
    /** Tab 内容组件 */
    private JComponent tabContent;

    /**
     * @param icon 图标路径
     * @param tabName 标题
     * @param tabContent 内容组件
     */
    public AddMainTabEvent(String icon, String tabName, JComponent tabContent) {
        this.tabName = tabName;
        this.icon = icon;
        this.tabContent = tabContent;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public JComponent getTabContent() {
        return tabContent;
    }

    public void setTabContent(JComponent tabContent) {
        this.tabContent = tabContent;
    }
}
