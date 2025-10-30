package com.dillon.lw.fx.eventbus.event;

import javafx.scene.Node;

public class MainTabEvent {
    private String tabName;
    private String icon;
    private Node tabContent;

    public MainTabEvent(String icon, String tabName, Node tabContent) {
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

    public Node getTabContent() {
        return tabContent;
    }

    public void setTabContent(Node tabContent) {
        this.tabContent = tabContent;
    }
}
