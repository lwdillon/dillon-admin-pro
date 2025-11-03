package com.dillon.lw.fx.view.window;

import animatefx.animation.BounceInDown;
import atlantafx.base.controls.Message;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.LoginSuccessEvent;
import com.dillon.lw.fx.eventbus.event.LogoutEvent;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.login.LoginView;
import com.dillon.lw.fx.view.main.MainView;
import com.google.common.eventbus.Subscribe;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowView extends BaseView<WindowViewModel> {
    public static final String MAIN_MODAL_ID = "modal-pane";

    @FXML
    private StackPane contentPane;

    @FXML
    private StackPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var modalPane = new ModalPane();
        modalPane.setId(MAIN_MODAL_ID);
        rootPane.getChildren().add(0, modalPane);
        showLogin();
        EventBusCenter.get().register(this);

    }


    @Subscribe
    private void onLogin(LoginSuccessEvent event) {

        Platform.runLater(() -> {
            showMainView();
        });
    }

    @Subscribe
    private void onLoginOut(LogoutEvent event) {
        Platform.runLater(() -> {
            showLogin();
        });
    }

    @Subscribe
    private void onMessage(MessageEvent messageEvent) {
        Platform.runLater(() -> {
            showMessage(messageEvent);
        });
    }


    private void showMainView() {

        Node oldNode = contentPane.getChildren().getFirst();

        // 加载新视图但不添加到 contentPane 中
        MainView mainView = ViewLoader.load(MainView.class);
        Node newView = mainView.getNode();
        newView.setOpacity(0); // 设置为全透明，准备淡入
        contentPane.getChildren().add(newView); // 先添加新视图

        // 创建旧节点的淡出动画
        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), oldNode);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            if (rootPane.getScene() instanceof BorderlessScene) {

                if (((BorderlessScene) rootPane.getScene()).isMaximized() == false) {
                    ((BorderlessScene) rootPane.getScene()).maximizeStage();
                }

            }
        });

        // 创建新视图的淡入动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), newView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);


        // 合并为并行动画
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeOut, fadeIn);

        // 动画完成后移除旧节点
        parallelTransition.setOnFinished(e -> {
            contentPane.getChildren().removeAll(oldNode);

        });
        parallelTransition.play();
    }

    private void showLogin() {
        FadeTransition fadeOut = null;
        ParallelTransition parallelTransition = new ParallelTransition();
        if (contentPane.getChildren().size() > 0) {
            Node oldNode = contentPane.getChildren().getFirst();
            // 创建旧节点的淡出动画
            fadeOut = new FadeTransition(Duration.millis(500), oldNode);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            parallelTransition.getChildren().add(fadeOut);
            // 动画完成后移除旧节点
            parallelTransition.setOnFinished(e -> {
                contentPane.getChildren().removeAll(oldNode);
            });
        }
//保存旧节点


        // 加载新视图但不添加到 contentPane 中
        LoginView loginView = ViewLoader.load(LoginView.class);
        Node newView = loginView.getNode();
        newView.setOpacity(0); // 设置为全透明，准备淡入
        contentPane.getChildren().add(newView); // 先添加新视图


        // 创建新视图的淡入动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), newView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // 合并为并行动画
        parallelTransition.getChildren().add(fadeIn);


        parallelTransition.play();

    }


    private void showMessage(MessageEvent messageEvent) {

        String msg = messageEvent.getMessage();
        MessageType msgType = messageEvent.getType();

        if (rootPane.isVisible()) {
            Platform.runLater(() -> {

                Message message = null;

                switch (msgType) {
                    case REGULAR:
                        message = new Message(msg, null, new FontIcon(Material2OutlinedAL.CHAT_BUBBLE_OUTLINE));
                        break;
                    case INFO:
                        message = new Message(msg, null, new FontIcon(Material2OutlinedAL.HELP_OUTLINE));
                        message.getStyleClass().add(Styles.ACCENT);
                        break;
                    case SUCCESS:
                        message = new Message(msg, null, new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE));
                        message.getStyleClass().add(Styles.SUCCESS);
                        break;
                    case WARNING:
                        message = new Message(msg, null, new FontIcon(Material2OutlinedMZ.OUTLINED_FLAG));
                        message.getStyleClass().add(Styles.WARNING);
                        break;
                    case DANGER:
                        message = new Message(msg, null, new FontIcon(Material2OutlinedAL.ERROR_OUTLINE));
                        message.getStyleClass().add(Styles.DANGER);
                        break;
                    default:
                        message = new Message(msg, null, new FontIcon(Material2OutlinedAL.CHAT_BUBBLE_OUTLINE));

                }

                message.setPrefHeight(Region.USE_PREF_SIZE);
                message.setMaxHeight(Region.USE_PREF_SIZE);
                message.setMaxWidth(Region.USE_PREF_SIZE);
                StackPane.setAlignment(message, Pos.TOP_CENTER);
                StackPane.setMargin(message, new Insets(60, 10, 0, 0));

                Message finalMessage = message;
                message.setOnClose(e -> {
                    var out = Animations.slideOutUp(finalMessage, Duration.millis(250));
                    out.setOnFinished(f -> rootPane.getChildren().remove(finalMessage));
                    out.playFromStart();
                });
                if (!rootPane.getChildren().contains(message)) {
                    rootPane.getChildren().add(message);
                }
                new BounceInDown(message).play();

                Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        var out = Animations.slideOutUp(finalMessage, Duration.millis(250));
                        out.setOnFinished(f -> rootPane.getChildren().remove(finalMessage));
                        out.playFromStart();
                    }
                }));
                fiveSecondsWonder.play();
            });
        }


    }
    @Override
    public void onRemove() {
        super.onRemove();
        EventBusCenter.get().unregister(this);
    }
}
