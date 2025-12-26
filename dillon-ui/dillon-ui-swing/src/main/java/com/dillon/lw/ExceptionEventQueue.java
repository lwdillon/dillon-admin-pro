package com.dillon.lw;

import java.awt.*;

public class ExceptionEventQueue extends EventQueue {

    @Override
    protected void dispatchEvent(AWTEvent event) {
        try {
            super.dispatchEvent(event);
        } catch (Throwable e) {
            SwingExceptionHandler.handle(e);
        }
    }
}