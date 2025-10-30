package com.dillon.lw.fx.eventbus.event;


public class RefreshEvent {
    private String data;

    public RefreshEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
