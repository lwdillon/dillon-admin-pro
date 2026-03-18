package com.dillon.lw.components;

/**
 * Swing 组件资源释放约定。
 */
public interface SwingDisposable {

    /**
     * 释放组件持有的外部资源，例如 RxJava 订阅。
     */
    void disposeResources();
}
