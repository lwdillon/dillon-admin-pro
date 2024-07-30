package com.lw.fx.view.infra.job;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class JobFormView implements FxmlView<JobFormViewModel>, Initializable {

    @InjectViewModel
    private JobFormViewModel viewModel;
    @FXML
    private HBox basePathBox;

    @FXML
    private TextField cronExpressionField;

    @FXML
    private TextField handlerNameField;

    @FXML
    private TextField handlerParamField;

    @FXML
    private HBox hostBox;

    @FXML
    private TextField monitorTimeoutFiled;

    @FXML
    private TextField nameField;

    @FXML
    private HBox passwordBox;

    @FXML
    private HBox portBox;

    @FXML
    private TextField retryCountField;

    @FXML
    private TextField retryIntervalFiled;

    @FXML
    private VBox rootBox;

    @FXML
    private HBox usernameBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        cronExpressionField.textProperty().bindBidirectional(viewModel.cronExpressionProperty());
        handlerNameField.textProperty().bindBidirectional(viewModel.handlerNameProperty());
        handlerParamField.textProperty().bindBidirectional(viewModel.handlerParamProperty());
        StringConverter stringConverter=   new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public Number fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return 0;
                }
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0; // Default value in case of parsing error
                }
            }
        };
        Bindings.bindBidirectional(retryIntervalFiled.textProperty(), viewModel.retryIntervalProperty(),stringConverter );
        Bindings.bindBidirectional(monitorTimeoutFiled.textProperty(), viewModel.monitorTimeoutProperty(),stringConverter );
        Bindings.bindBidirectional(retryCountField.textProperty(), viewModel.retryCountProperty(),stringConverter );

    }



}
