package com.dillon.lw.fx.view.infra.config;

import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.store.AppStore;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

import static com.dillon.lw.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;


public class ConfigFormView extends BaseView<ConfigFormViewModel> implements Initializable {
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


        visibleComboBox.setCellFactory(new Callback<ListView<DictDataSimpleRespVO>, ListCell<DictDataSimpleRespVO>>() {
            @Override
            public ListCell<DictDataSimpleRespVO> call(ListView<DictDataSimpleRespVO> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(DictDataSimpleRespVO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getLabel());
                        }
                    }
                };
            }
        });
        visibleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DictDataSimpleRespVO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLabel());
                }
            }
        });

    }
}
