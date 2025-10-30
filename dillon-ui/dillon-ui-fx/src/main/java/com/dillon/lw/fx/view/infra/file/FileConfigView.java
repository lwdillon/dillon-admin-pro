package com.dillon.lw.fx.view.infra.file;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.module.infra.controller.admin.file.vo.config.FileConfigRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.store.AppStore;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.PagingControl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;
import static com.dillon.lw.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;
import static com.dillon.lw.utils.DictTypeEnum.INFRA_FILE_STORAGE;

public class FileConfigView extends BaseView<FileConfigViewModel> implements Initializable {
    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<FileConfigRespVO, LocalDateTime> createTimeCol;

    @FXML
    private Button addBut;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<FileConfigRespVO, ?> idCol;

    @FXML
    private TableColumn<FileConfigRespVO, ?> nameCol;

    @FXML
    private TableColumn<FileConfigRespVO, Long> optCol;

    @FXML
    private TableColumn<FileConfigRespVO, Boolean> primaryCol;

    @FXML
    private TableColumn<FileConfigRespVO, ?> remarkCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<FileConfigRespVO, Integer> storageCol;

    @FXML
    private TableView<FileConfigRespVO> tableView;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<DictDataSimpleRespVO> storageComboBox;

    private PagingControl pagingControl;

    private ModalPane modalPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        modalPane = new ModalPane();
        rootPane.getChildren().add(modalPane);
        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);

        pagingControl.totalProperty().bind(viewModel.totalProperty());
        viewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        viewModel.pageSizeProperty().bind(pagingControl.pageSizeProperty());
        pagingControl.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.loadTableData();
        });

        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.loadTableData();
        });

        storageComboBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(INFRA_FILE_STORAGE)));
        storageComboBox.valueProperty().bindBidirectional(viewModel.storageProperty());
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());


        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER");

        storageCol.setCellValueFactory(new PropertyValueFactory<>("storage"));
        storageCol.setStyle("-fx-alignment: CENTER");
        storageCol.setCellFactory(new Callback<TableColumn<FileConfigRespVO, Integer>, TableCell<FileConfigRespVO, Integer>>() {
            @Override
            public TableCell<FileConfigRespVO, Integer> call(TableColumn<FileConfigRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(INFRA_FILE_STORAGE).get(item + "");
                            Button state = new Button(dict.getLabel());
                            switch (dict.getColorType()) {
                                case "primary":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
                                    break;
                                case "success":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                                    break;
                                case "info":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED);
                                    break;
                                case "warning":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, WARNING);
                                    break;
                                case "danger":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                                    break;
                                default:
                                    state.getStyleClass().addAll(BUTTON_OUTLINED);
                            }

                            HBox box = new HBox(state);
                            box.setPadding(new Insets(7, 7, 7, 7));
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);


                        }

                    }
                };
            }
        });

        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
        remarkCol.setStyle("-fx-alignment: CENTER");


        primaryCol.setCellValueFactory(new PropertyValueFactory<>("master"));
        primaryCol.setStyle("-fx-alignment: CENTER");
        primaryCol.setCellFactory(new Callback<TableColumn<FileConfigRespVO, Boolean>, TableCell<FileConfigRespVO, Boolean>>() {
            @Override
            public TableCell<FileConfigRespVO, Boolean> call(TableColumn<FileConfigRespVO, Boolean> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(INFRA_BOOLEAN_STRING).get(item + "");
                            Button state = new Button(dict.getLabel());
                            switch (dict.getColorType()) {
                                case "primary":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
                                    break;
                                case "success":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                                    break;
                                case "info":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED);
                                    break;
                                case "warning":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, WARNING);
                                    break;
                                case "danger":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                                    break;
                                default:
                                    state.getStyleClass().addAll(BUTTON_OUTLINED);
                            }

                            HBox box = new HBox(state);
                            box.setPadding(new Insets(7, 7, 7, 7));
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);

                        }

                    }
                };
            }
        });

        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");
        createTimeCol.setCellFactory(new Callback<TableColumn<FileConfigRespVO, LocalDateTime>, TableCell<FileConfigRespVO, LocalDateTime>>() {
            @Override
            public TableCell<FileConfigRespVO, LocalDateTime> call(TableColumn<FileConfigRespVO, LocalDateTime> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {


                            if (item != null) {
                                this.setText(DateUtil.format(item, "yyyy-MM-dd HH:mm:ss"));
                            }
                        }

                    }
                };
            }
        });

        optCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        optCol.setCellFactory(new Callback<TableColumn<FileConfigRespVO, Long>, TableCell<FileConfigRespVO, Long>>() {
            @Override
            public TableCell<FileConfigRespVO, Long> call(TableColumn<FileConfigRespVO, Long> param) {

                TableCell cell = new TableCell<FileConfigRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            Button editBut = new Button("编辑");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem().getId()));
                            editBut.setGraphic(FontIcon.of(Feather.EDIT));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button masterBut = new Button("主配置");
                            masterBut.setOnAction(event -> showMasterDialog(getTableRow().getItem()));
                            masterBut.setGraphic(FontIcon.of(Feather.EDIT));
                            masterBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button delBut = new Button("删除");
                            delBut.setOnAction(actionEvent -> showDelDialog(getTableRow().getItem()));
                            delBut.setGraphic(FontIcon.of(Feather.TRASH));
                            delBut.getStyleClass().addAll(FLAT, DANGER);
                            HBox box = new HBox(editBut, masterBut, delBut);
                            box.setAlignment(Pos.CENTER);
//                            box.setSpacing(7);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        });

        tableView.setItems(viewModel.getTableItems());

        addBut.setOnAction(actionEvent -> showFormView(null));
        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            nameField.setText(null);
            storageComboBox.getSelectionModel().select(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            viewModel.loadTableData();
        });

        storageComboBox.setCellFactory(param -> new ListCell<>() {

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
        storageComboBox.setButtonCell(new ListCell<>() {
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


    /**
     * 显示编辑对话框
     */
    private void showFormView(Long id) {

        boolean isAdd = (id == null);
        FileConfigFormView load = ViewLoader.load(FileConfigFormView.class);
        load.getViewModel().updateData(id, isAdd);
        new ConfirmDialog.Builder(modalPane)
                .title(isAdd ? "添加配置" : "编辑配置")
                .content(load.getNode())
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    if (isAdd) {
                        load.getViewModel().createFileConfig(d);
                    } else {
                        load.getViewModel().updateFileConfig(d);
                    }
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }

    private void showDelDialog(FileConfigRespVO respVO) {
        new ConfirmDialog.Builder(modalPane)
                .title("删除")
                .message("是否确认删除名称为【 " + respVO.getName() + " 】的数据项?")
                .width(450)
                .height(150)
                .onConfirm(d -> {
                    viewModel.deleteFileConfig(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }

    private void showMasterDialog(FileConfigRespVO respVO) {

        new ConfirmDialog.Builder(modalPane)
                .title("系统提示")
                .message("是否确认修改配置名称为【 " + respVO.getName() + " 】的数据项为主配置?")
                .width(450)
                .height(150)
                .onConfirm(d -> {
                    viewModel.updateFileConfigMaster(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }
}
