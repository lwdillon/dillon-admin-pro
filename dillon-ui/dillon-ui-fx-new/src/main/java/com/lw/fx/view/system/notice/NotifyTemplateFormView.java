package com.lw.fx.view.system.notice;

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

import static com.lw.ui.utils.DictTypeEnum.COMMON_STATUS;
import static com.lw.ui.utils.DictTypeEnum.SYSTEM_NOTIFY_TEMPLATE_TYPE;

public class NotifyTemplateFormView implements FxmlView<NotifyTemplateFormViewModel>, Initializable {
    @InjectViewModel
    private NotifyTemplateFormViewModel viewModel;
    @FXML
    private TextField codeTield;

    @FXML
    private TextArea contentArea;

    @FXML
    private TextField nameField;

    @FXML
    private TextField nicknameField;

    @FXML
    private TextField remarkField;

    @FXML
    private ComboBox<DictDataSimpleRespVO> statusComboBox;

    @FXML
    private ComboBox<DictDataSimpleRespVO> typeComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        typeComboBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(SYSTEM_NOTIFY_TEMPLATE_TYPE)));
        typeComboBox.valueProperty().bindBidirectional(viewModel.selTypeProperty());

        statusComboBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(COMMON_STATUS)));
        statusComboBox.valueProperty().bindBidirectional(viewModel.selStatusProperty());

        codeTield.textProperty().bindBidirectional(viewModel.codeProperty());
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        nicknameField.textProperty().bindBidirectional(viewModel.nicknameProperty());
        contentArea.textProperty().bindBidirectional(viewModel.contentProperty());
        remarkField.textProperty().bindBidirectional(viewModel.remarkProperty());



    }




}
