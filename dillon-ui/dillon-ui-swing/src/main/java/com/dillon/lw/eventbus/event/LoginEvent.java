package com.dillon.lw.eventbus.event;

import lombok.Data;

@Data
public class LoginEvent {
    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGOUT_OR_INVALID = 1;

    /**
     * 事件码：
     * 0 = 登录成功，切换主界面；
     * 1 = 退出或登录失效，回到登录页。
     */
    private Integer code = LOGIN_SUCCESS;

    public LoginEvent(Integer code) {
        this.code = code;
    }
}
