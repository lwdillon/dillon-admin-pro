package com.dillon.lw.subject;

import java.util.Observable;

/**
 * 自动刷新主题
 */
public class AppRefreshObservable extends Observable {

    /**
     * 界面刷新
     */
    public void refresh() {
        setChanged();
        notifyObservers();
    }

}
