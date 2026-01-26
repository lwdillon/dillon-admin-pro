package com.dillon.lw;

import atlantafx.base.theme.PrimerLight;
import com.dillon.lw.fx.Resources;
import com.dlsc.gemsfx.CalendarPicker;
import com.dlsc.gemsfx.daterange.DateRangePicker;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DateRangePickerDemo extends Application {

    @Override
    public void start(Stage stage) {


        // 1️⃣ 应用 Atlantafx PrimerDark
        Application.setUserAgentStylesheet(
                new PrimerLight().getUserAgentStylesheet()
        );

        // 2️⃣ 创建 DateRangePicker（GemsFX 3.8.2）
        DateRangePicker picker = new DateRangePicker();
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.setCustomRangeText("日期");
        picker.setValue(null);/**/
        picker.getDateRangeView().setPresetsLocation(Side.RIGHT);
//        picker.getValue()
        DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false);
        datePicker.setMaxWidth(Double.MAX_VALUE);

        CalendarPicker calendarPicker = new CalendarPicker();
        calendarPicker.setMaxWidth(Double.MAX_VALUE);

        VBox root = new VBox(12, picker,datePicker,calendarPicker );
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 420, 200);
        scene.getStylesheets().addAll(Resources.resolve("/styles/gemsfx-atlantafx.css"));

        stage.setTitle("DateRangePicker");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}