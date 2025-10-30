import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class FrostedGlassEffect extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 1. 创建背景层
        Rectangle background = new Rectangle(400, 300, Color.DARKGRAY);
        
        // 2. 创建半透明毛玻璃层
        Rectangle glassPane = new Rectangle(200, 150, Color.rgb(255, 255, 255, 0.2));
        glassPane.setArcWidth(20);  // 圆角
        glassPane.setArcHeight(20);
        
        // 3. 添加模糊效果
        GaussianBlur blur = new GaussianBlur(15);
        glassPane.setEffect(blur);
        
        // 4. 使用 StackPane 叠加
        StackPane root = new StackPane();
        root.getChildren().addAll(background, glassPane);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("JavaFX 毛玻璃效果");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}