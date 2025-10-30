package com.dillon.lw.fx.utils;

public enum MessageType {

    REGULAR(0, "Regular"), INFO(1, "Info"), SUCCESS(2, "Success"), WARNING(3, "Warning"), DANGER(4, "Danger");

    private int code;
    private String desc;

    MessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
