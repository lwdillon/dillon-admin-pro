package com.dillon.lw;

import atlantafx.base.theme.PrimerDark;
import com.dillon.lw.fx.Resources;
import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangePicker;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class DateRangePickerDemo extends Application {

    @Override
    public void start(Stage stage) {


        // 1️⃣ 应用 Atlantafx PrimerDark
        Application.setUserAgentStylesheet(
                new PrimerDark().getUserAgentStylesheet()
        );

        // 2️⃣ 创建 DateRangePicker（GemsFX 3.8.2）
        DateRangePicker picker = new DateRangePicker();
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.customRangeTextProperty().setValue("创建日期");
        picker.setCustomRangeText("333");
        picker.setValue(null);/**/
        picker.getDateRangeView().setPresetsLocation(Side.RIGHT);
//        picker.getValue()


        VBox root = new VBox(12, picker);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 420, 200);
        scene.getStylesheets().addAll(Resources.resolve("/styles/gemsfx-atlantafx.css"));

        stage.setTitle("PrimerDark + GemsFX 3.8.2 DateRangePicker");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}