package com.lw.fx.view.system.post;

import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.util.NumberUtil;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class PostFormView implements FxmlView<PostFormViewModel>, Initializable {
    @InjectViewModel
    private PostFormViewModel viewModel;
    @FXML
    private TextField codeField;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea remarksTextArea;

    @FXML
    private Spinner<Integer> sortSpinner;

    @FXML
    private ToggleSwitch statusBut;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        codeField.textProperty().bindBidirectional(viewModel.codeProperty());
        remarksTextArea.textProperty().bindBidirectional(viewModel.remartProperty());
        sortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        viewModel.sortProperty().addListener((observable, oldValue, newValue) -> {
            sortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, newValue.intValue()));
        });
        sortSpinner.valueProperty().addListener((observableValue, integer, t1) -> viewModel.sortProperty().set(t1.intValue()));
        viewModel.sortProperty().addListener((observable, oldValue, newValue) -> viewModel.sortProperty().setValue(newValue));
        viewModel.statusProperty().addListener((observableValue, number, t1) -> statusBut.setSelected(NumberUtil.equals(t1, 0)));
        statusBut.selectedProperty().addListener((observableValue, aBoolean, t1) -> viewModel.statusProperty().set(t1 ? 0 : 1));

    }
}
