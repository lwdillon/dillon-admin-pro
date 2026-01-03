package com.dillon.lw.fx.view.system.log.loginlog;

import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.store.AppStore;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;
import static com.dillon.lw.utils.DictTypeEnum.SYSTEM_LOGIN_RESULT;
import static com.dillon.lw.utils.DictTypeEnum.SYSTEM_LOGIN_TYPE;

public class LoginLogDetailView extends BaseView<LoginLogDetailViewModel>implements Initializable {
    @FXML
    private TextField createTimeField;

    @FXML
    private TextField idField;

    @FXML
    private Button logTypeBut;

    @FXML
    private Button resultBut;

    @FXML
    private TextField userAgentField;

    @FXML
    private TextField userIpField;

    @FXML
    private TextField usernameField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        logTypeBut.textProperty().bind(Bindings.createStringBinding(
                () -> AppStore.getDictDataValueMap(SYSTEM_LOGIN_TYPE).get(viewModel.logTypeProperty().get()+"").getLabel() + "",
                viewModel.logTypeProperty()));

        logTypeBut.textProperty().addListener((observableValue, s, t1) -> {
            DictDataSimpleRespVO dict=AppStore.getDictDataLabelMap(SYSTEM_LOGIN_TYPE).get(t1);
            switch (dict.getColorType()) {
                case "primary":
                    logTypeBut.getStyleClass().addAll( ACCENT);
                    break;
                case "success":
                    logTypeBut.getStyleClass().addAll( SUCCESS);
                    break;
                case "info":
                    logTypeBut.getStyleClass().addAll(BUTTON_OUTLINED);
                    break;
                case "warning":
                    logTypeBut.getStyleClass().addAll( WARNING);
                    break;
                case "danger":
                    logTypeBut.getStyleClass().addAll( DANGER);
                    break;
                default:
                    logTypeBut.getStyleClass().addAll(BUTTON_OUTLINED);
            }
        });



        resultBut.textProperty().addListener((observableValue, s, t1)  -> {
            DictDataSimpleRespVO dict=AppStore.getDictDataLabelMap(SYSTEM_LOGIN_RESULT).get(resultBut.getText());
            switch (dict.getColorType()) {
                case "primary":
                    resultBut.getStyleClass().addAll( ACCENT);
                    break;
                case "success":
                    resultBut.getStyleClass().addAll( SUCCESS);
                    break;
                case "info":
                    resultBut.getStyleClass().addAll(BUTTON_OUTLINED);
                    break;
                case "warning":
                    resultBut.getStyleClass().addAll( WARNING);
                    break;
                case "danger":
                    resultBut.getStyleClass().addAll( DANGER);
                    break;
                default:
                    resultBut.getStyleClass().addAll(BUTTON_OUTLINED);
            }
        });
        resultBut.textProperty().bind(Bindings.createStringBinding(
                () -> AppStore.getDictDataValueMap(SYSTEM_LOGIN_RESULT).get(viewModel.resultProperty().get()+"").getLabel() + "",
                viewModel.resultProperty()));

        createTimeField.textProperty().bind(Bindings.convert(viewModel.createTimeProperty()));


        idField.textProperty().bind(Bindings.convert(viewModel.idProperty()));


        userAgentField.textProperty().bind(viewModel.userAgentProperty());


        userIpField.textProperty().bind(viewModel.userIpProperty());


        usernameField.textProperty().bind(viewModel.userNameProperty());


    }
}
