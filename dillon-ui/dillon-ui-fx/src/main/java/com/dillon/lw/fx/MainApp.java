package com.dillon.lw.fx;

import com.dillon.lw.fx.http.forest.ForestConfig;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.updater.ClientAutoUpdater;
import com.dillon.lw.fx.view.window.WindowView;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessPane;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MainApp extends Application {
    private static final AtomicBoolean EXIT_CONFIRMED = new AtomicBoolean(false);


    @Override
    public void init() throws Exception {

    }

    @Override
    public void start(Stage stage) throws IOException {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> DefaultExceptionHandler.handle(e));
        loadApplicationProperties();
        ForestConfig.init();
        MainApp.setUserAgentStylesheet(Resources.resolve("/styles/primer-light.css"));

        int insets = 40;
        WindowView mainView = ViewLoader.load(WindowView.class);
        Parent mainNoae = mainView.getNode();
        BorderlessScene scene = new BorderlessScene(stage, StageStyle.TRANSPARENT, mainNoae, 950, 650);
        BorderlessPane rootPane = (BorderlessPane) scene.getRoot();
        rootPane.setPadding(new Insets(insets));

        scene.removeDefaultCSS();
        scene.setMoveControl(mainNoae);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().addAll(Resources.resolve("/styles/index.css"), Resources.resolve("/styles/gemsfx-atlantafx.css"));

        scene.maximizedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal) {
                rootPane.setPadding(new Insets(0, 0, 0, 0));
            } else {
                rootPane.setPadding(new Insets(insets));
            }

        });


        stage.setScene(scene);
        stage.setTitle(System.getProperty("app.name"));
        stage.setResizable(true);
        stage.setOnCloseRequest(event -> {
            if (EXIT_CONFIRMED.get()) {
                return;
            }
            if (!confirmAndMarkExit(stage)) {
                event.consume();
            }
        });


        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
            startAutoUpdateCheckAsync();
        });
    }

    private void startAutoUpdateCheckAsync() {
        Thread updateThread = new Thread(() -> {
            try {
                ClientAutoUpdater.checkAndUpdateIfNeeded();
            } catch (Exception e) {
                DefaultExceptionHandler.handle(e);
            }
        }, "fx-client-auto-updater");
        updateThread.setDaemon(true);
        updateThread.start();
    }


    private void loadApplicationProperties() {
        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(Resources.getResourceAsStream("/application.properties"),
                UTF_8)) {
            properties.load(in);
            properties.forEach((key, value) -> System.setProperty(
                    String.valueOf(key),
                    String.valueOf(value)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 弹出退出确认框；确认后写入全局退出标记，避免重复弹窗。
     */
    public static boolean confirmAndMarkExit(Window owner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("退出确认");
        alert.setHeaderText("确认关闭应用吗？");
        alert.setContentText("关闭后将退出当前客户端。");
        if (owner != null) {
            alert.initOwner(owner);
        }
        boolean confirmed = alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
        if (confirmed) {
            EXIT_CONFIRMED.set(true);
        }
        return confirmed;
    }
}
