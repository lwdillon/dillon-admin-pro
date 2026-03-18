package com.dillon.lw.components;

import java.awt.*;

/**
 * Swing 组件树生命周期辅助方法。
 */
public final class SwingLifecycleUtils {

    private SwingLifecycleUtils() {
    }

    /**
     * 递归释放组件树上声明了 {@link SwingDisposable} 的节点。
     */
    public static void disposeComponentTree(Component component) {
        if (component == null) {
            return;
        }

        if (component instanceof SwingDisposable) {
            ((SwingDisposable) component).disposeResources();
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                disposeComponentTree(child);
            }
        }
    }
}
