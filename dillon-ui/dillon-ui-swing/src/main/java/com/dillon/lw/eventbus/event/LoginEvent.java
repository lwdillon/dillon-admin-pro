package com.dillon.lw.eventbus.event;

import lombok.Data;

@Data
public class LoginEvent {

    private Integer code = 0;

    public LoginEvent(Integer code) {
        this.code = code;
    }
}
