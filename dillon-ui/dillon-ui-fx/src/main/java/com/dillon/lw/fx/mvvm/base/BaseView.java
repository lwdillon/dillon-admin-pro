package com.dillon.lw.fx.mvvm.base;

import com.dillon.lw.fx.mvvm.i18n.I18n;
import javafx.beans.value.ChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseView<T extends BaseViewModel> implements Initializable {

    protected final T viewModel;
    protected Parent node;
    protected ResourceBundle bundle = I18n.getBundle();
    private static final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();


    private ChangeListener<? super Scene> sceneListener;

    /**
     * 通过构造器传入 ViewModel 类型，避免反射不稳
     */
    public BaseView() {
        this.viewModel = createViewModelByGeneric();
        this.viewModel.init();
    }

    @SuppressWarnings("unchecked")
    private T createViewModelByGeneric() {
        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                Type actualType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
                if (actualType instanceof Class) {
                    Class<T> clazz = (Class<T>) actualType;
                    Constructor<T> ctor = (Constructor<T>) constructorCache.computeIfAbsent(clazz, c -> {
                        try {
                            return c.getDeclaredConstructor();
                        } catch (Exception e) {
                            throw new RuntimeException("构造器缓存失败", e);
                        }
                    });
                    return ctor.newInstance();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("无法通过反射创建 ViewModel 实例", e);
        }

        throw new RuntimeException("无法获取 ViewModel 类型，请确保使用了泛型");
    }


    /**
     * 自动监听 node 是否从 Scene 中移除，自动清理
     */
    private void setupAutoCleanup() {
        if (node == null) {
            return;
        }

        // 如果之前有监听器，先移除
        if (sceneListener != null) {
            node.sceneProperty().removeListener( sceneListener);
            sceneListener=null;
        }

        sceneListener = (obsScene, oldScene, newScene) -> {
            if (oldScene != null && newScene == null) {
                onRemove();
            }
        };

        node.sceneProperty().addListener( sceneListener);
    }

    public Parent getNode() {
        return node;
    }

    public void setNode(Parent node) {
        this.node = node;
        setupAutoCleanup();
    }

    public T getViewModel() {
        return viewModel;
    }

    /**
     * 视图被移除时回调，子类可覆盖
     */
    public void onRemove() {
        try {
            if (viewModel != null) {
                viewModel.dispose();
            }
        } catch (Exception e) {
            // 这里建议记录异常日志，避免影响流程
            e.printStackTrace();
        }
    }
}