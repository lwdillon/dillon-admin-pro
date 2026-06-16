package com.dillon.lw.components.notice;

import java.util.Objects;

/**
 * InfoCenterPane 的通知分组配置。
 */
public class InfoCenterGroup {

    private final String id;
    private String title;
    private boolean pinned;
    private boolean expanded;
    private boolean stacked = true;
    private boolean listView;
    private int displayThreshold = 3;
    private InfoCenterNotificationViewFactory viewFactory;

    public InfoCenterGroup(String id, String title) {
        this.id = Objects.requireNonNullElse(id, InfoCenterItem.DEFAULT_GROUP_ID);
        this.title = Objects.requireNonNullElse(title, "通知");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNullElse(title, "通知");
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isStacked() {
        return stacked;
    }

    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    public boolean isListView() {
        return listView;
    }

    public void setListView(boolean listView) {
        this.listView = listView;
    }

    public int getDisplayThreshold() {
        return displayThreshold;
    }

    public void setDisplayThreshold(int displayThreshold) {
        this.displayThreshold = Math.max(1, displayThreshold);
    }

    public InfoCenterNotificationViewFactory getViewFactory() {
        return viewFactory;
    }

    public void setViewFactory(InfoCenterNotificationViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }
}
