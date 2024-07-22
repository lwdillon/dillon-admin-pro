package com.lw.fx.view.window;

import animatefx.animation.BounceInDown;
import atlantafx.base.controls.Message;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.login.LoginView;
import com.lw.fx.view.login.LoginViewModel;
import com.lw.fx.view.main.MainView;
import com.lw.fx.view.main.MainViewModel;
import de.saxsys.mvvmfx.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowView implements FxmlView<WindowViewModel>, Initializable {

    @InjectViewModel
    private WindowViewModel windowViewModel;



    @FXML
    private StackPane rootPane;
    @FXML
    private StackPane contentPane;
    public static final String MAIN_MODAL_ID = "modal-pane";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        var modalPane = new ModalPane();
        modalPane.setId(MAIN_MODAL_ID);
        rootPane.getChildren().add(0, modalPane);
        MvvmFX.getNotificationCenter().subscribe("showMainView", (key, payload) -> {
            // trigger some actions
            Platform.runLater(() -> {
                showMainView();

            });

        });
        MvvmFX.getNotificationCenter().subscribe("showLoginRegister", (key, payload) -> {
            // trigger some actions
            Platform.runLater(() -> {
                showLoginView();

            });
        });


        MvvmFX.getNotificationCenter().subscribe("message", (key, payload) -> {
            // trigger some actions

            Platform.runLater(() -> {
                showMessage((String) payload[0], (MessageType) payload[1]);

            });

        });
        MvvmFX.getNotificationCenter().subscribe("exit", (key, payload) -> {
            // trigger some actions

            Platform.runLater(() -> {
                Stage stage = (Stage) rootPane.getScene().getWindow();

                stage.close();

            });

        });

        showLoginView();

    }

    private void showMainView() {

        contentPane.getChildren().clear();
        ViewTuple<MainView, MainViewModel> load = FluentViewLoader.fxmlView(MainView.class).load();
        contentPane.getChildren().add(load.getView());
        windowViewModel.mainViewVisbleProperty().setValue(true);
        if (contentPane.getScene() instanceof BorderlessScene) {

            ((BorderlessScene) contentPane.getScene()).maximizeStage();
        }
    }

    private void showLoginView() {
        contentPane.getChildren().clear();
        ViewTuple<LoginView, LoginViewModel> viewTuple = FluentViewLoader.fxmlView(LoginView.class).load();
        contentPane.getChildren().add(viewTuple.getView());
        windowViewModel.mainViewVisbleProperty().setValue(false);
    }

    private void showMessage(String msg, MessageType msgType) {

        if (windowViewModel.isMainViewVisble()) {
            Platform.runLater(() -> {


                Message message = null;

                switch (msgType) {
                    case REGULAR:
                        message = new Message(msg, "", new FontIcon(Material2OutlinedAL.CHAT_BUBBLE_OUTLINE));
                        break;
                    case INFO:
                        message = new Message(msg, null, new FontIcon(Material2OutlinedAL.HELP_OUTLINE));
                        message.getStyleClass().add(Styles.ACCENT);
                        break;
                    case SUCCESS:
                        message = new Message(msg, "", new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE));
                        message.getStyleClass().add(Styles.SUCCESS);
                        break;
                    case WARNING:
                        message = new Message(msg, "", new FontIcon(Material2OutlinedMZ.OUTLINED_FLAG));
                        message.getStyleClass().add(Styles.WARNING);
                        break;
                    case DANGER:
                        message = new Message(msg, "", new FontIcon(Material2OutlinedAL.ERROR_OUTLINE));
                        message.getStyleClass().add(Styles.DANGER);
                        break;
                    default:
                        message = new Message(msg, "", new FontIcon(Material2OutlinedAL.CHAT_BUBBLE_OUTLINE));

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
}
