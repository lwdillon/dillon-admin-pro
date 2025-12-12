package com.dillon.lw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class WebViewWithURLInput extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建 WebView
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // URL 输入框和按钮
        TextField urlField = new TextField("https://www.baidu.com");
        urlField.setPrefWidth(600);
        Button loadButton = new Button("加载网页");
        HBox topBar = new HBox(5, urlField, loadButton);

        // 加载网页按钮事件
        loadButton.setOnAction(e -> {
            String url = urlField.getText();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            webEngine.load(url);
        });

        // JavaScript 调用 Java
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
            }
        });

        // Java 调用 JS 示例按钮
        Button jsCallButton = new Button("调用网页 JS alert()");
        jsCallButton.setOnAction(e -> webEngine.executeScript("alert('Hello from Java!')"));

        // 布局
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(webView);
        root.setBottom(jsCallButton);

        // Scene & Stage
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("JavaFX WebView 输入网址示例");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Java 对象供 JS 调用
    public static class JavaConnector {
        public void showMessage(String msg) {
            System.out.println("JS 发送消息：" + msg);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}