package com.lw.fx.view.infra.file;

import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.store.AppStore;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.lw.ui.utils.DictTypeEnum.INFRA_FILE_STORAGE;

public class FileConfigFormView implements FxmlView<FileConfigFormViewModel>, Initializable {

    @InjectViewModel
    private FileConfigFormViewModel viewModel;
    @FXML
    private HBox accessKeyBox;

    @FXML
    private TextField accessKeyField;

    @FXML
    private HBox accessSecretBox;

    @FXML
    private TextField accessSecretField;

    @FXML
    private HBox basePathBox;

    @FXML
    private TextField basePathField;

    @FXML
    private HBox bucketBox;

    @FXML
    private TextField bucketField;

    @FXML
    private TextField domainField;

    @FXML
    private HBox endpointBox;

    @FXML
    private TextField endpointField;

    @FXML
    private ToggleGroup group;

    @FXML
    private HBox hostBox;

    @FXML
    private TextField hostField;

    @FXML
    private HBox modeBox;

    @FXML
    private RadioButton modeRadioBut1;

    @FXML
    private RadioButton modeRadioBut2;

    @FXML
    private TextField nameField;

    @FXML
    private TextField remarkField;

    @FXML
    private HBox passwordBox;

    @FXML
    private TextField passwordField;

    @FXML
    private HBox portBox;

    @FXML
    private TextField portField;

    @FXML
    private VBox rootBox;
    @FXML
    private HBox domainBox;

    @FXML
    private ComboBox<DictDataSimpleRespVO> storageComboBox;

    @FXML
    private HBox usernameBox;

    @FXML
    private TextField usernameField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        storageComboBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(INFRA_FILE_STORAGE)));
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        remarkField.textProperty().bindBidirectional(viewModel.remarkProperty());
        storageComboBox.valueProperty().bindBidirectional(viewModel.selStorageProperty());
        storageComboBox.disableProperty().bind(viewModel.editProperty());
        domainField.textProperty().bindBidirectional(viewModel.domainProperty());
        basePathField.textProperty().bindBidirectional(viewModel.basePathProperty());
        hostField.textProperty().bindBidirectional(viewModel.hostProperty());
        portField.textProperty().bindBidirectional(viewModel.portProperty());
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        endpointField.textProperty().bindBidirectional(viewModel.endpointProperty());
        bucketField.textProperty().bindBidirectional(viewModel.bucketProperty());
        accessKeyField.textProperty().bindBidirectional(viewModel.accessKeyProperty());
        accessSecretField.textProperty().bindBidirectional(viewModel.accessSecretProperty());
        storageComboBox.valueProperty().addListener((observableValue, dictDataSimpleRespVO, t1) -> showStorage(Optional.of(t1).map(DictDataSimpleRespVO::getLabel).orElse("")));

        domainBox.managedProperty().bind(domainBox.visibleProperty());
        basePathBox.managedProperty().bind(basePathBox.visibleProperty());
        hostBox.managedProperty().bind(hostBox.visibleProperty());
        portBox.managedProperty().bind(portBox.visibleProperty());
        usernameBox.managedProperty().bind(usernameBox.visibleProperty());
        passwordBox.managedProperty().bind(passwordBox.visibleProperty());
        endpointBox.managedProperty().bind(endpointBox.visibleProperty());
        bucketBox.managedProperty().bind(bucketBox.visibleProperty());
        accessKeyBox.managedProperty().bind(accessKeyBox.visibleProperty());
        accessSecretBox.managedProperty().bind(accessSecretBox.visibleProperty());
        modeBox.managedProperty().bind(modeBox.visibleProperty());

        showStorage("");
    }


    private void showStorage(String type) {

        domainBox.setVisible(false);
        basePathBox.setVisible(false);
        hostBox.setVisible(false);
        portBox.setVisible(false);
        usernameBox.setVisible(false);
        passwordBox.setVisible(false);
        endpointBox.setVisible(false);
        bucketBox.setVisible(false);
        accessKeyBox.setVisible(false);
        accessSecretBox.setVisible(false);
        modeBox.setVisible(false);

        switch (type) {
            case "数据库": {
                domainBox.setVisible(true);
                break;
            }
            case "本地磁盘": {
                domainBox.setVisible(true);
                basePathBox.setVisible(true);
                break;
            }
            case "FTP 服务器": {
                domainBox.setVisible(true);
                basePathBox.setVisible(true);
                hostBox.setVisible(true);
                portBox.setVisible(true);
                usernameBox.setVisible(true);
                passwordBox.setVisible(true);
                break;
            }
            case "SFTP 服务器": {
                domainBox.setVisible(true);
                basePathBox.setVisible(true);
                hostBox.setVisible(true);
                portBox.setVisible(true);
                usernameBox.setVisible(true);
                passwordBox.setVisible(true);
                modeBox.setVisible(true);
                break;
            }
            case "S3 对象存储": {

                endpointBox.setVisible(true);
                bucketBox.setVisible(true);
                accessKeyBox.setVisible(true);
                accessSecretBox.setVisible(true);
                break;
            }
            default: {

            }
        }
    }
}
