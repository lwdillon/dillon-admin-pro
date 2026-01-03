package com.dillon.lw.fx.view.login;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import com.dillon.lw.fx.AppStart;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dlsc.gemsfx.SVGImageView;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginView extends BaseView<LoginViewModel> {


    @FXML
    private SVGImageView svgImageView;
    @FXML
    private Button closeBut;

    @FXML
    private Button loginButton;

    @FXML
    private Label logoLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private PasswordTextField passwordTextField;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private CustomTextField usernameTextField;
    @FXML
    private StackPane rootPane;
    @FXML
    private SVGImageView logoSvgView;

    private FontIcon clearUserNmaeIcon = new FontIcon(Feather.X);
    private FontIcon clearPasswordIcon = new FontIcon(Feather.X);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logoSvgView.setSvgUrl("/icons/dillon.svg");
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#6a11cb")),
                new Stop(0.25, Color.web("#56ccf2")),
                new Stop(0.5, Color.web("#f6d02f")),
                new Stop(0.75, Color.web("#feb47b")),
                new Stop(1, Color.web("#ff7e5f"))

        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        logoLabel.setTextFill(gradient);
        // 设置 clip，实现圆角裁剪
        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        rootPane.setClip(clip);

        rootPane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.getWidth());
            clip.setHeight(newVal.getHeight());
        });

        svgImageView.setSvgUrl(AppStart.class.getResource("/icons/user-manage.svg").toExternalForm());
        closeBut.setGraphic(new FontIcon(Feather.X));
        closeBut.getStyleClass().addAll(
                Styles.BUTTON_ICON, Styles.FLAT, Styles.DANGER
        );
        closeBut.setOnAction(event -> System.exit(0));

        messageLabel.getStyleClass().add(Styles.DANGER);

        loginButton.setOnAction(event -> viewModel.login());


        clearUserNmaeIcon.setOnMouseClicked(event -> usernameTextField.setText(""));
        clearPasswordIcon.setOnMouseClicked(event -> passwordTextField.setText(""));
        usernameTextField.setLeft(new FontIcon(Feather.USER));
        usernameTextField.setRight(clearUserNmaeIcon);
        passwordTextField.setLeft(new FontIcon(Feather.LOCK));
        passwordTextField.setRight(clearPasswordIcon);



        usernameTextField.textProperty().addListener((obs, oldText, newText) -> {
            boolean hasText = newText != null && !newText.trim().isEmpty();

            if (!hasText) {
                viewModel.msgProperty().set("请输入用户名");
                usernameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            } else {
                usernameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                viewModel.msgProperty().set("");
            }

        });

        passwordTextField.textProperty().addListener((obs, oldText, newText) -> {
            boolean hasText = newText != null && !newText.trim().isEmpty();

            if (!hasText) {
                viewModel.msgProperty().set("请输入密码");
                passwordTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            } else {
                passwordTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                viewModel.msgProperty().set("");
            }

        });

        binds();
    }

    private void binds() {

        // 绑定视图模型
        viewModel.usernameProperty().bind(usernameTextField.textProperty());
        viewModel.passwordProperty().bind(passwordTextField.passwordProperty());
        loginButton.disableProperty().bind(viewModel.loginButDisableProperty());
        messageLabel.textProperty().bind(viewModel.msgProperty());
// 当 text 为 null 或空字符串时，不显示并不占位置
        messageLabel.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> messageLabel.getText() != null && !messageLabel.getText().isEmpty(),
                messageLabel.textProperty()
        ));

        messageLabel.managedProperty().bind(messageLabel.visibleProperty());

        progressbar.progressProperty().bind(Bindings.createDoubleBinding(
                () -> loginButton.isDisable() ? -1d : 0d,
                loginButton.disabledProperty()
        ));
        progressbar.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> loginButton.isDisable(),
                loginButton.disabledProperty()
        ));
        clearUserNmaeIcon.visibleProperty().bind(
                Bindings.createBooleanBinding(() ->
                                usernameTextField.getText() != null && !usernameTextField.getText().isEmpty() ,
                        usernameTextField.textProperty()
                )
        );
        clearPasswordIcon.visibleProperty().bind(
                Bindings.createBooleanBinding(() ->
                                passwordTextField.getText() != null && !passwordTextField.getText().isEmpty() ,
                        passwordTextField.textProperty()
                )
        );

    }


}
