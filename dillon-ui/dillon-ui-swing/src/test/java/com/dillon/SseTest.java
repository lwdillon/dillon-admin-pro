package com.dillon;

import com.dillon.lw.ExceptionEventQueue;
import com.dillon.lw.Resources;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.http.forest.ForestConfig;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SseTest {

    public static void main(String[] args) {


        // 1️⃣ EDT 异常统一兜底（Swing 核心）
        Toolkit.getDefaultToolkit()
                .getSystemEventQueue()
                .push(new ExceptionEventQueue());

        // 2️⃣ 后台线程异常兜底
        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> SwingExceptionHandler.handle(e)
        );

        loadApplicationProperties();
        ForestConfig.init();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(new Dimension(700,300));
        SsePanel testPane = new SsePanel();
        frame.setContentPane(testPane);
        frame.setVisible(true);

    }
    private static void loadApplicationProperties() {
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
}
