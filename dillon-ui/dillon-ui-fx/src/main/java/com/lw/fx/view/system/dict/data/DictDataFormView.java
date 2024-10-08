package com.lw.fx.view.system.dict.data;

import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.util.NumberUtil;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class DictDataFormView implements FxmlView<DictDataFormViewModel>, Initializable {
    @InjectViewModel
    private DictDataFormViewModel viewModel;
    @FXML
    private ComboBox<DictDataSimpleRespVO> colorTypeComboBox;

    @FXML
    private TextField cssClassField;

    @FXML
    private TextField labelField;

    @FXML
    private TextArea remarksTextArea;

    @FXML
    private Spinner<Integer> sortSpinner;

    @FXML
    private ToggleSwitch statusBut;

    @FXML
    private TextField typeField;

    @FXML
    private TextField valueField;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeField.disableProperty().bind(viewModel.editProperty());

        labelField.textProperty().bindBidirectional(viewModel.labelProperty());
        typeField.textProperty().bindBidirectional(viewModel.dictTypeProperty());
        valueField.textProperty().bindBidirectional(viewModel.valueProperty());
        cssClassField.textProperty().bindBidirectional(viewModel.cssClassProperty());
        remarksTextArea.textProperty().bindBidirectional(viewModel.remartProperty());
        sortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        viewModel.sortProperty().addListener((observable, oldValue, newValue) -> {
            sortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, newValue.intValue()));
        });
        sortSpinner.valueProperty().addListener((observableValue, integer, t1) -> viewModel.sortProperty().set(t1.intValue()));
        viewModel.sortProperty().addListener((observable, oldValue, newValue) -> viewModel.sortProperty().setValue(newValue));
        viewModel.statusProperty().addListener((observableValue, number, t1) -> statusBut.setSelected(NumberUtil.equals(t1, 0)));
        statusBut.selectedProperty().addListener((observableValue, aBoolean, t1) -> viewModel.statusProperty().set(t1 ? 0 : 1));
        colorTypeComboBox.valueProperty().bindBidirectional(viewModel.selDictDataSimpleRespVOProperty());
        viewModel.colorTypeProperty().addListener((observableValue, s, t1) -> {

            // 设置选中项为 name 等于 "abc" 的对象
            for (DictDataSimpleRespVO item : colorTypeComboBox.getItems()) {
                if (t1.equals(item.getValue())) {
                   viewModel.selDictDataSimpleRespVOProperty().set(item);
                    break;
                }
            }

        });
    }

}
