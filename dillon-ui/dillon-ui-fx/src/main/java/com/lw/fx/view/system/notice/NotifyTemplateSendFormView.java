package com.lw.fx.view.system.notice;

import atlantafx.base.controls.CustomTextField;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.store.AppStore;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

import static com.lw.ui.utils.DictTypeEnum.*;

public class NotifyTemplateSendFormView implements FxmlView<NotifyTemplateSendFormViewModel>, Initializable {
    @InjectViewModel
    private NotifyTemplateSendFormViewModel viewModel;
    @FXML
    private TextArea contentArea;

    @FXML
    private ComboBox userComboBox;

    @FXML
    private ComboBox<DictDataSimpleRespVO> userTypeComboBox;

    @FXML
    private VBox paramsBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userComboBox.setItems(viewModel.getUserItems());
        userComboBox.valueProperty().bindBidirectional(viewModel.selUserProperty());
        userTypeComboBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(USER_TYPE)));
        userTypeComboBox.valueProperty().bindBidirectional(viewModel.userTypeProperty());
        contentArea.textProperty().bindBidirectional(viewModel.contentProperty());
        viewModel.subscribe("params",(key, payload) -> {
            for (String params : viewModel.paramsProperty()) {
                paramsBox.getChildren().add(createTextField(params));
            }
        });


    }

    private HBox createTextField(String param) {
        Label label = new Label("*参数{"+param+"}");
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPrefWidth(120);
        TextField textField = new TextField();
        textField.textProperty().addListener((observableValue, s, t1) -> viewModel.putParams(param,t1));
        HBox.setHgrow(textField, Priority.ALWAYS);
        HBox hBox = new HBox(7, label, textField);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }


}
