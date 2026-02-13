package com.dillon.lw.fx.eventbus.event;

public class UpdateDataEvent {
    private String message;
    private Object data;

    public UpdateDataEvent(String message) {
        this.message = message;
    }

    public UpdateDataEvent(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
