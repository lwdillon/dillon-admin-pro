import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class CustomTitleBarWindow extends BorderPane {

    private Stage primaryStage;
    private HBox titleBar;
    private Label titleLabel;


    public CustomTitleBarWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initView();
    }

    private void initView() {
        titleBar=new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setStyle("-fx-background-color: #2C3E50; -fx-padding: 10px;");

        titleLabel=new Label("Hello World");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setStyle("-fx-text-fill: white;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        // 创建最小化按钮
        Button minimizeButton = new Button("_");
        minimizeButton.setOnAction(event -> {
            if (minimizeButton.getScene() instanceof BorderlessScene) {
                ((BorderlessScene) minimizeButton.getScene()).minimizeStage();
            }
        });
        minimizeButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        // 创建最大化按钮
        Button maximizeButton = new Button("⬜");
        maximizeButton.setOnAction(event -> {
            if (maximizeButton.getScene() instanceof BorderlessScene) {

                ((BorderlessScene) maximizeButton.getScene()).maximizeStage();
            }
        });
        maximizeButton.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white;");

        // 创建关闭按钮
        Button closeButton = new Button("X");
        closeButton.setOnAction(event -> primaryStage.close());
        closeButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");

        // 将按钮添加到标题栏
        titleBar.getChildren().addAll(titleLabel,minimizeButton, maximizeButton, closeButton);

        this.setTop(titleBar);
    }




}
