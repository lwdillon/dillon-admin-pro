package com.dillon.lw.fx.eventbus.event;

public class OperateEvent {
    private String data;
    private Integer code;

    public OperateEvent(Integer code,String message) {
        this.data = data;
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
