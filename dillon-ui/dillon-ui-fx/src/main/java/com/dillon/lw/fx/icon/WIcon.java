package com.dillon.lw.fx.icon;

import org.kordamp.ikonli.Ikon;

public enum WIcon implements Ikon {
    HOME("lw-home", '\ue90e'),
    COMPASS("lw-compass", '\ue900'),
    DOCUMENT_COPY("lw-document-copy", '\ue901'),
    STORE("lw-store", '\ue902'),
    DOCUMENT("lw-document", '\ue903'),
    BICYCLE("lw-bicycle", '\ue904'),
    HDD("lw-hdd", '\ue905'),
    MONEY("lw-money", '\ue906'),
    AVATAR("lw-avatar", '\ue907'),
    TOOLS("lw-tools", '\ue908'),
    PIE_CHART("lw-pie-chart", '\ue909'),
    MONITOR("lw-monitor", '\ue90a'),
    SHOP("lw-shop", '\ue90b'),
    APPLE("lw-apple", '\ue90c'),
    MEDIUM("lw-medium", '\ue90d'),
    YUANQUAN("lw-yuanquan", '\ue90f');

    public static WIcon findByDescription(String description) {
        for (WIcon font : values()) {
            if (font.getDescription().equals(description)) {
                return font;
            }
        }
        return YUANQUAN;
//        throw new IllegalArgumentException("Icon description '" + description + "' is invalid!");
    }

    private String description;
    private int code;

    WIcon(String description, int code) {
        this.description = description;
        this.code = code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getCode() {
        return code;
    }
}
