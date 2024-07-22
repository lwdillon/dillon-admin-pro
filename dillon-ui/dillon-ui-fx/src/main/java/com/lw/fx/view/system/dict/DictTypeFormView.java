package com.lw.fx.view.system.dict;

import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.util.NumberUtil;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DictTypeFormView implements FxmlView<DictTypeFormViewModel>, Initializable {
    @InjectViewModel
    private DictTypeFormViewModel viewModel;
    @FXML
    private TextField typeField;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea remarksTextArea;


    @FXML
    private ToggleSwitch statusBut;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        typeField.disableProperty().bind(viewModel.editProperty());
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        typeField.textProperty().bindBidirectional(viewModel.typeProperty());
        remarksTextArea.textProperty().bindBidirectional(viewModel.remartProperty());
        viewModel.statusProperty().addListener((observableValue, number, t1) -> statusBut.setSelected(NumberUtil.equals(t1, 0)));
        statusBut.selectedProperty().addListener((observableValue, aBoolean, t1) -> viewModel.statusProperty().set(t1 ? 0 : 1));

    }
}
