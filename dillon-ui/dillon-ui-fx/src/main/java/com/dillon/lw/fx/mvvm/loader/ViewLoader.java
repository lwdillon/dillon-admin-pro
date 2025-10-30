package com.dillon.lw.fx.mvvm.loader;

import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.i18n.I18n;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ViewLoader {
    public static <T extends BaseView<?>> T load(Class<T> viewClass) {
        try {
            String fxmlPath = getFxmlPath(viewClass);
            URL fxmlUrl = viewClass.getClassLoader().getResource(fxmlPath);

            T view;
            if (fxmlUrl != null) {
                FXMLLoader loader = new FXMLLoader(fxmlUrl, I18n.getBundle());
                if (!hasFxController(fxmlUrl)) {
                    view = viewClass.getDeclaredConstructor().newInstance();
                    loader.setController(view);
                    Parent root = loader.load();
                    view.setNode(root);
                } else {
                    Parent root = loader.load();
                    view = loader.getController();
                    view.setNode(root);
                }
            } else {
                view = viewClass.getDeclaredConstructor().newInstance();
            }

            return view;
        } catch (Exception e) {
            throw new RuntimeException("无法加载视图: " + viewClass.getName(), e);
        }
    }

    private static String getFxmlPath(Class<?> clazz) {
        return clazz.getPackage().getName().replace('.', '/') + "/" + clazz.getSimpleName() + ".fxml";
    }


    private static boolean hasFxController(URL fxmlUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fxmlUrl.openStream()))) {
            return reader.lines().anyMatch(line -> line.contains("fx:controller"));
        } catch (IOException e) {
            throw new RuntimeException("检测 fx:controller 时出错", e);
        }
    }
}