package com.dillon.lw.fx;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessPane;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.view.window.WindowView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MainApp extends Application {


    @Override
    public void init() throws Exception {

    }

    @Override
    public void start(Stage stage) throws IOException {
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        loadApplicationProperties();
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
        scene.getStylesheets().addAll(Resources.resolve("/styles/index.css"));

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
        stage.setOnCloseRequest(t -> Platform.exit());


        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
        });
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
}