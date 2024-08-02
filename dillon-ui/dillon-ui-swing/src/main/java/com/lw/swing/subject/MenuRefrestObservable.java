package com.lw.swing.subject;

import java.util.Observable;

/**
 * r
 */
public class MenuRefrestObservable extends Observable {


    /**
     * 日期切换
     */
    public void refresh() {
        setChanged();
        notifyObservers();
    }


}
