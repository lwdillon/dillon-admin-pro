import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SimpleJavaFXApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {




        // 创建一个标签并设置文本
        Label label = new Label("Hello, JavaFX!");

        // 创建一个根布局
        StackPane root = new StackPane();
        root.getChildren().add(label);

        // 创建一个场景，设置根布局和窗口大小
        Scene scene = new Scene(root, 300, 200);

        // 设置窗口标题
        stage.setTitle("Simple JavaFX App");

        // 设置场景到窗口
        stage.setScene(scene);

//
//        Method   tkStageGetter = Window.class.getDeclaredMethod("getPeer");;
//
//        tkStageGetter.setAccessible(true);
//        Object tkStage = tkStageGetter.invoke(stage);
//        Method getPlatformWindow = tkStage.getClass().getDeclaredMethod("getPlatformWindow");
////        getPlatformWindow.setAccessible(true);
////        Object platformWindow = getPlatformWindow.invoke(tkStage);
//        Method getNativeHandle = platformWindow.getClass().getMethod("getNativeHandle");
//        getNativeHandle.setAccessible(true);
//        Object nativeHandle = getNativeHandle.invoke(platformWindow);
//        getNativeHandle.setAccessible(true);

        var seamlessFrameApplied = false;
        stage.getScene()
                .getRoot()
                .pseudoClassStateChanged(PseudoClass.getPseudoClass("seamless-frame"), seamlessFrameApplied);
        stage.getScene()
                .getRoot()
                .pseudoClassStateChanged(PseudoClass.getPseudoClass("separate-frame"), !seamlessFrameApplied);


        // 显示窗口
        stage.show();
    }

    public static void main(String[] args) {
        // 启动 JavaFX 应用
        launch(args);
    }
}