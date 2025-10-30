package com.dillon.lw.fx.eventbus.event;

public class UpdateDataEvent {
    private String message;

    public UpdateDataEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
