package com.dillon.lw.fx.view.system.log.operatelog;

import com.dillon.lw.fx.mvvm.base.BaseView;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class OperateLogDetailView extends BaseView<OperateLogDetailViewModel>implements Initializable {
    @FXML
    private TextArea actionTextArea;

    @FXML
    private TextField bizIdField;

    @FXML
    private TextField createTimeField;

    @FXML
    private TextArea extraArea;

    @FXML
    private TextField idField;

    @FXML
    private TextField requestUrlField;
    @FXML
    private TextField requestMethodField;

    @FXML
    private TextField subTypeField;

    @FXML
    private TextField traceIdField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField userAgentField;

    @FXML
    private TextField userIdField;

    @FXML
    private TextField userIpField;

    @FXML
    private TextField userNameField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        actionTextArea.textProperty().bind(viewModel.actionProperty());


        bizIdField.textProperty().bind(Bindings.convert(viewModel.bizIdProperty()));


        createTimeField.textProperty().bind(Bindings.convert(viewModel.createTimeProperty()));


        extraArea.textProperty().bind(viewModel.extraProperty());


        idField.textProperty().bind(Bindings.convert(viewModel.idProperty()));


        requestUrlField.textProperty().bind(viewModel.requestUrlProperty());
        requestMethodField.textProperty().bind(viewModel.requestMethodProperty());


        subTypeField.textProperty().bind(viewModel.subTypeProperty());


        traceIdField.textProperty().bind(viewModel.traceIdProperty());


        typeField.textProperty().bind(viewModel.typeProperty());


        userAgentField.textProperty().bind(viewModel.userAgentProperty());


        userIdField.textProperty().bind(Bindings.convert(viewModel.userIdProperty()));


        userIpField.textProperty().bind(viewModel.userIpProperty());


        userNameField.textProperty().bind(viewModel.userNameProperty());


    }
}
