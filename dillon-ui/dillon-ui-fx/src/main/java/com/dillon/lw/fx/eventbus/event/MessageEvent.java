package com.dillon.lw.fx.eventbus.event;

import com.dillon.lw.fx.utils.MessageType;

public class MessageEvent {
    private String message;
    private MessageType type;

    public MessageEvent(String message, MessageType type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
