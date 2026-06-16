package com.dillon.lw.components.notice;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * InfoCenterPane 中的一条通知数据。
 */
public class InfoCenterItem {

    public static final String DEFAULT_GROUP_ID = "default";
    public static final String DEFAULT_GROUP_TITLE = "通知";

    private final String id;
    private final String groupId;
    private final String groupTitle;
    private final String title;
    private final String message;
    private final int type;
    private final LocalDateTime time;
    private final Runnable action;
    private boolean read;

    public InfoCenterItem(String title, String message, int type) {
        this(title, message, type, LocalDateTime.now(), false, null);
    }

    public InfoCenterItem(String title, String message, int type, LocalDateTime time) {
        this(title, message, type, time, false, null);
    }

    public InfoCenterItem(String title, String message, int type, LocalDateTime time, boolean read, Runnable action) {
        this(DEFAULT_GROUP_ID, DEFAULT_GROUP_TITLE, title, message, type, time, read, action);
    }

    public InfoCenterItem(String groupId, String groupTitle, String title, String message, int type) {
        this(groupId, groupTitle, title, message, type, LocalDateTime.now(), false, null);
    }

    public InfoCenterItem(String groupId, String groupTitle, String title, String message, int type, LocalDateTime time, boolean read, Runnable action) {
        this.id = UUID.randomUUID().toString();
        this.groupId = Objects.requireNonNullElse(groupId, DEFAULT_GROUP_ID);
        this.groupTitle = Objects.requireNonNullElse(groupTitle, DEFAULT_GROUP_TITLE);
        this.title = Objects.requireNonNullElse(title, "");
        this.message = Objects.requireNonNullElse(message, "");
        this.type = type;
        this.time = time == null ? LocalDateTime.now() : time;
        this.read = read;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean hasAction() {
        return action != null;
    }

    public void runAction() {
        if (action != null) {
            action.run();
        }
    }
}
