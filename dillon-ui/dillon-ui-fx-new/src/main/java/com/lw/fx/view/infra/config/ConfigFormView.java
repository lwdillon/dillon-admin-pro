package com.lw.fx.view.infra.config;

import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.store.AppStore;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static com.lw.ui.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;

public class ConfigFormView implements FxmlView<ConfigFormViewModel>, Initializable {
    @InjectViewModel
    private ConfigFormViewModel viewModel;
    @FXML
    private TextField categoryField;

    @FXML
    private TextField keyField;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea remarksTextArea;

    @FXML
    private TextField valueField;

    @FXML
    private ComboBox<DictDataSimpleRespVO> visibleComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        categoryField.textProperty().bindBidirectional(viewModel.categoryProperty());
        keyField.textProperty().bindBidirectional(viewModel.keyProperty());
        valueField.textProperty().bindBidirectional(viewModel.valueProperty());
        remarksTextArea.textProperty().bindBidirectional(viewModel.remartProperty());

        visibleComboBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(INFRA_BOOLEAN_STRING)));
        visibleComboBox.valueProperty().bindBidirectional(viewModel.visbleSelProperty());

    }
}
