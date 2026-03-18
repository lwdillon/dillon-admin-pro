package com.dillon.lw.config;

import lombok.Data;

@Data
public class UserHistory {

    private String userId;
    private String username;
    /**
     * 历史原因导致字段名拼写为 passwrod（而非 password），
     * 为兼容已存储的本地 JSON 数据，保留该字段名不变。
     */
    private String passwrod;
    private long lastLoginTime;
    private boolean lastSuccess;

    public UserHistory() {
    }

    public UserHistory(String userId, String username, String passwrod) {
        this.userId = userId;
        this.username = username;
        this.passwrod = passwrod;
        this.lastLoginTime = System.currentTimeMillis();
        this.lastSuccess = true;
    }
}
