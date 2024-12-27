package com.lw.fx.themes;

import atlantafx.base.theme.*;
import com.lw.fx.Resources;
import de.saxsys.mvvmfx.MvvmFX;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.Set;

/**
 * 皮肤管理类，用于切换应用程序的皮肤
 */
public class ThemeManager {

    private Scene scene; // 应用的主场景
    private Theme defaultTheme=new NordLight();
    private final Set<Theme> themes = Set.of(
            new PrimerLight(),
            new PrimerDark(),
            new NordLight(),
            new NordDark(),
            new CupertinoLight(),
            new CupertinoDark(),
            new Dracula()
    ); // 可用的皮肤列表
    private Theme currentTheme; // 当前应用的皮肤

    /**
     * 构造函数
     */
    public ThemeManager() {


    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * 设置皮肤
     *
     * @param theme 要应用的皮肤
     */
    public void setTheme(Theme theme) {
        if (theme == null || theme.equals(currentTheme)) {
            return; // 如果皮肤未变化，则不操作
        }
        animateThemeChange(Duration.millis(750));
        // 清空现有样式表
        scene.getStylesheets().clear();
        // 添加新皮肤的样式表
        scene.getStylesheets().addAll(Resources.resolve("assets/styles/index.css"));
        Application.setUserAgentStylesheet("themes/" + theme.getName().toLowerCase().replace(" ", "-") + ".css");
        // 更新当前皮肤
        currentTheme = theme;
        MvvmFX.getNotificationCenter().publish("themeUpdate","更新皮肤");

    }

    private void animateThemeChange(Duration duration) {
        Image snapshot = scene.snapshot(null);
        Pane root = (Pane) scene.getRoot();

        ImageView imageView = new ImageView(snapshot);
        root.getChildren().add(imageView); // add snapshot on top

        var transition = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(imageView.opacityProperty(), 1, Interpolator.EASE_OUT)),
                new KeyFrame(duration, new KeyValue(imageView.opacityProperty(), 0, Interpolator.EASE_OUT))
        );
        transition.setOnFinished(e -> root.getChildren().remove(imageView));
        transition.play();
    }

    /**
     * 获取当前皮肤
     *
     * @return 当前皮肤
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * 获取所有可用皮肤
     *
     * @return 皮肤列表
     */

    public Set<Theme> getThemes() {
        return themes;
    }

    public Theme getDefaultTheme() {
        return defaultTheme;
    }

    private static class InstanceHolder {

        private static final ThemeManager INSTANCE = new ThemeManager();

    }


    public static ThemeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }
}