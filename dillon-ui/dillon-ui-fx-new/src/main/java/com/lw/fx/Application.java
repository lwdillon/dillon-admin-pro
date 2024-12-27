package com.lw.fx;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessPane;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.lw.fx.themes.ThemeManager;
import com.lw.fx.view.window.WindowView;
import com.lw.fx.view.window.WindowViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import fr.brouillard.oss.cssfx.CSSFX;
import fr.brouillard.oss.cssfx.api.URIToPathConverter;
import fr.brouillard.oss.cssfx.impl.log.CSSFXLogger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Application extends javafx.application.Application {
    public static final boolean IS_DEV_MODE = "DEV".equalsIgnoreCase(
            Resources.getPropertyOrEnv("dillonfx.mode", "dillonfx_mode")
    );

    @Override
    public void start(Stage stage) throws IOException {
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        loadApplicationProperties();


        ViewTuple<WindowView, WindowViewModel> viewTuple = FluentViewLoader.fxmlView(WindowView.class).load();
        Parent mainView = viewTuple.getView();
        BorderlessScene scene = new BorderlessScene(stage, StageStyle.TRANSPARENT, mainView, 950, 650);
        BorderlessPane rootPane = (BorderlessPane) scene.getRoot();
        rootPane.setPadding(new Insets(15, 15, 15, 15));

        scene.removeDefaultCSS();
        scene.setMoveControl(mainView);
        scene.setFill(Color.TRANSPARENT);

        scene.getStylesheets().addAll(Resources.resolve("assets/styles/index.css"));
        var tm = ThemeManager.getInstance();
        tm.setScene(scene);
        tm.setTheme(tm.getDefaultTheme());

        scene.maximizedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal) {
                rootPane.setPadding(new Insets(0, 0, 0, 0));
            } else {
                rootPane.setPadding(new Insets(15, 15, 15, 15));
            }

        });
//
        if (IS_DEV_MODE) {
            startCssFX(scene);
        }


        stage.setScene(scene);
        stage.setTitle(System.getProperty("app.name"));
        stage.setResizable(true);
        stage.setOnCloseRequest(t -> Platform.exit());


        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
        });
    }
    @SuppressWarnings("CatchAndPrintStackTrace")
    private void startCssFX(Scene scene) {
        URIToPathConverter fileUrlConverter = uri -> {
            try {
                if (uri != null && uri.startsWith("file:")) {
                    return Paths.get(URI.create(uri));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };

        CSSFX.addConverter(fileUrlConverter).start();
        CSSFXLogger.setLoggerFactory(loggerName -> (level, message, args) -> {
            if (level.ordinal() <= CSSFXLogger.LogLevel.INFO.ordinal()) {
                System.out.println("[" + level + "] CSSFX: " + String.format(message, args));
            }
        });
        CSSFX.start(scene);
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
        launch();
    }
}