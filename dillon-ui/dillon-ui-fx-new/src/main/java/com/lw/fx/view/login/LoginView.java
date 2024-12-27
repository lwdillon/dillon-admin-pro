package com.lw.fx.view.login;

import atlantafx.base.layout.DeckPane;
import atlantafx.base.theme.Styles;
import com.dlsc.gemsfx.SVGImageView;
import com.lw.fx.AppStart;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class LoginView implements FxmlView<LoginViewModel>, Initializable {
    @InjectViewModel
    private LoginViewModel loginViewModel;
    @FXML
    private Button closeBut;
    @FXML
    private VBox svgBox;

    @FXML
    private DeckPane deckPane;

    @FXML
    private Button loginBut;

    @FXML
    private Label msgLabel;
    @FXML
    private Label logoLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField usernameField;

    private final Random random = new Random();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        createImageView();

        // circularly returns the next item from the deck
        Supplier<Node> nextItem = () -> {
            var next = (deckPane.getChildren().indexOf(deckPane.getTopNode()) + 1)
                    % deckPane.getChildren().size();
            return deckPane.getChildren().get(next);
        };
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            deckPane.slideRight(nextItem.get());
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        SVGImageView logoSvg = new SVGImageView(AppStart.class.getResource("/icons/guanli.svg").toExternalForm());
        logoSvg.setFitWidth(40);
        logoSvg.setFitHeight(40);
        logoLabel.setGraphic(logoSvg);
        closeBut.setGraphic(new FontIcon(Feather.X));
        closeBut.getStyleClass().addAll(Styles.BUTTON_CIRCLE, Styles.FLAT, Styles.DANGER);
        msgLabel.getStyleClass().addAll(Styles.DANGER);

        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(usernameField, Priority.ALWAYS);

        loginBut.getStyleClass().addAll(Styles.ACCENT);

        closeBut.setOnAction(e -> System.exit(0));

        passwordField.textProperty().bindBidirectional(loginViewModel.passwordProperty());
        usernameField.textProperty().bindBidirectional(loginViewModel.usernameProperty());
        progressBar.visibleProperty().bind(loginBut.disabledProperty());
        msgLabel.managedProperty().bind(msgLabel.visibleProperty());
        msgLabel.textProperty().bindBidirectional(loginViewModel.msgPropertyProperty());
        msgLabel.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    String text = msgLabel.getText();
                    return text != null && !text.isEmpty();
                },
                msgLabel.textProperty()
        ));
        progressBar.progressProperty().bind(Bindings.createDoubleBinding(() -> loginBut.isDisable() ? -1D : 0D, loginBut.disableProperty()));
        loginBut.disableProperty().bind(loginViewModel.loginStautsPropertyProperty());
        loginBut.setOnAction(actionEvent -> loginViewModel.login());

        passwordField.setText("admin123");
        usernameField.setText("admin");
    }

    private void createImageView() {

        List<FunctionType> functionTypes = new ArrayList<>();
        // 添加功能
        functionTypes.add(new FunctionType("用户管理", "用户管理功能允许管理员轻松管理系统中的用户", "/icons/user-manage.svg"));
        functionTypes.add(new FunctionType("数据分析与报表", "数据分析与报表功能提供了系统内部数据的分析和可视化工具", "/icons/baobiaofenxi.svg"));
        functionTypes.add(new FunctionType("权限管理", "权限管理功能允许管理员灵活设置系统内各项功能的访问权限", "/icons/quanxian.svg"));
        functionTypes.add(new FunctionType("日志审计与监控", "日志审计与监控功能记录和跟踪系统内的操作日志", "/icons/jiankong.svg"));


        for (FunctionType functionType : functionTypes) {

            SVGImageView svgImageView = new SVGImageView();
            svgImageView.setFitWidth(450);
            svgImageView.setFitHeight(300);
            svgImageView.setSvgUrl(AppStart.class.getResource(functionType.getIcon()).toExternalForm());

            VBox.setVgrow(svgImageView, Priority.ALWAYS);

            Label titleLabel = new Label(functionType.getTitle());
            titleLabel.getStyleClass().add("title-3");
            titleLabel.setStyle("-fx-text-fill: #ffffff");
            titleLabel.setAlignment(Pos.CENTER);

            Label subLabel = new Label(functionType.getDescription());
            subLabel.getStyleClass().add("title-4");
            subLabel.setStyle("-fx-text-fill: #ffffff");
            subLabel.setAlignment(Pos.CENTER);
            subLabel.setWrapText(true);

            VBox vBox = new VBox(svgImageView, titleLabel, subLabel);
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(10);
            vBox.setStyle(getRandomGradientStyle());
            vBox.setPadding(new Insets(10));
            AnchorPane.setBottomAnchor(vBox, 0D);
            AnchorPane.setTopAnchor(vBox, 0D);
            AnchorPane.setLeftAnchor(vBox, 0D);
            AnchorPane.setRightAnchor(vBox, 0D);
            deckPane.getChildren().add(vBox);
        }

    }

    private String getRandomGradientStyle() {
        // 生成随机的颜色
        Stop[] stops = new Stop[]{
                new Stop(0, randomColor()),
                new Stop(1, randomColor())
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        // 将渐变转换为CSS样式
        return String.format("-fx-background-color: linear-gradient(from 0%% 0%% to 100%% 100%%, %s 0%%, %s 100%%);-fx-background-radius:10 0 0 10",
                toHex(stops[0].getColor()), toHex(stops[1].getColor()));
    }

    private String toHex(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private javafx.scene.paint.Color randomColor() {
        return new javafx.scene.paint.Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), 1.0);
    }

    class FunctionType {
        private String title;
        private String description;
        private String icon;

        public FunctionType(String title, String description, String icon) {
            this.title = title;
            this.description = description;
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

}
