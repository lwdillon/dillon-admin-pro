import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;

/**
 * Testing the application to see if it works
 *
 * @author GOXR3PLUS
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {


        CustomTitleBarWindow windowPane = new CustomTitleBarWindow(primaryStage);
        windowPane.setCenter(new Button("我是内容"));

        // Constructor using your primary stage and the root Parent of your content.
        BorderlessScene scene = new BorderlessScene(primaryStage, StageStyle.TRANSPARENT, windowPane, 250, 250);
        primaryStage.setScene(scene); // Set the scene to your stage and you're done!

        //Close Button
        Button removeDefaultCSS = new Button("Remove Default Corners CSS");
        removeDefaultCSS.setOnAction(a -> scene.removeDefaultCSS());

        //remove the default css style
        scene.removeDefaultCSS();

        // Maximise (on/off) and minimise the application:
        //scene.maximizeStage();
        //scene.minimizeStage();

        // To move the window around by pressing a node:
        scene.setMoveControl(windowPane);

        // To disable resize:
        //scene.setResizable(false);

        // To switch the content during runtime:
        //scene.setContent(yourNewParent);

        // Check if maximised:
        //Boolean bool = scene.isMaximised();

        // Get windowed* size and position:
        //scene.getWindowedSize();
        //scene.getWindowedPosition();

        //Show
        primaryStage.setTitle("Draggable and Undecorated JavaFX Window");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
