package com.dillon.lw.eventbus.event;

import javax.swing.*;

public class AddMainTabEvent {
    private String tabName;
    private String icon;
    private JComponent tabContent;

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
