package com.dillon.lw.config;

import lombok.Data;

@Data
public class UserHistory {

    private String userId;
    private String username;
    private String passwrod;
    private long lastLoginTime;
    private boolean lastSuccess;

    public UserHistory() {}

    public UserHistory(String userId, String username, String passwrod) {
        this.userId = userId;
        this.username = username;
        this.passwrod = passwrod;
        this.lastLoginTime = System.currentTimeMillis();
        this.lastSuccess = true;
    }


}