package com.dillon.lw.fx.view.infra.config;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigRespVO;
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
import static com.dillon.lw.utils.DictTypeEnum.INFRA_CONFIG_TYPE;

public class ConfigView extends BaseView<ConfigViewModel> implements Initializable {

    @FXML
    private Button addBut;

    @FXML
    private TableColumn<ConfigRespVO, ?> categoryCol;

    @FXML
    private TextField keyField;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<ConfigRespVO, LocalDateTime> createTimeCol;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<ConfigRespVO, ?> idCol;

    @FXML
    private TableColumn<ConfigRespVO, ?> keyCol;

    @FXML
    private TableColumn<ConfigRespVO, ?> nameCol;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<ConfigRespVO, Long> optCol;

    @FXML
    private TableColumn<ConfigRespVO, ?> remarkCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<DictDataSimpleRespVO> typeCombo;

    @FXML
    private TableView<ConfigRespVO> tableView;

    @FXML
    private TableColumn<ConfigRespVO, Integer> typeCol;

    @FXML
    private TableColumn<ConfigRespVO, ?> valueCol;

    @FXML
    private TableColumn<ConfigRespVO, Boolean> visibleCol;

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


        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");

        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setStyle("-fx-alignment: CENTER");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER");

        keyCol.setCellValueFactory(new PropertyValueFactory<>("key"));
        keyCol.setStyle("-fx-alignment: CENTER");

        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.setStyle("-fx-alignment: CENTER");

        visibleCol.setCellValueFactory(new PropertyValueFactory<>("visible"));
        visibleCol.setStyle("-fx-alignment: CENTER");
        visibleCol.setCellFactory(new Callback<TableColumn<ConfigRespVO, Boolean>, TableCell<ConfigRespVO, Boolean>>() {
            @Override
            public TableCell<ConfigRespVO, Boolean> call(TableColumn<ConfigRespVO, Boolean> param) {
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

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle("-fx-alignment: CENTER");
        typeCol.setCellFactory(new Callback<TableColumn<ConfigRespVO, Integer>, TableCell<ConfigRespVO, Integer>>() {
            @Override
            public TableCell<ConfigRespVO, Integer> call(TableColumn<ConfigRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(INFRA_CONFIG_TYPE).get(item + "");
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

        createTimeCol.setCellFactory(new Callback<TableColumn<ConfigRespVO, LocalDateTime>, TableCell<ConfigRespVO, LocalDateTime>>() {
            @Override
            public TableCell<ConfigRespVO, LocalDateTime> call(TableColumn<ConfigRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<ConfigRespVO, Long>, TableCell<ConfigRespVO, Long>>() {
            @Override
            public TableCell<ConfigRespVO, Long> call(TableColumn<ConfigRespVO, Long> param) {

                TableCell cell = new TableCell<ConfigRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Button editBut = new Button("修改");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem().getId()));
                            editBut.setGraphic(FontIcon.of(Feather.EDIT));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button delBut = new Button("删除");
                            delBut.setOnAction(actionEvent -> showDelDialog(getTableRow().getItem()));
                            delBut.setGraphic(FontIcon.of(Feather.TRASH));
                            delBut.getStyleClass().addAll(FLAT, DANGER);
                            HBox box = new HBox(editBut, delBut);
                            box.setAlignment(Pos.CENTER);
//                            box.setSpacing(7);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        });
        tableView.itemsProperty().bind(viewModel.tableItemsProperty());

        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        keyField.textProperty().bindBidirectional(viewModel.keyProperty());


        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            nameField.setText(null);
            keyField.setText(null);
            typeCombo.getSelectionModel().select(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            viewModel.loadTableData();
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

        addBut.setOnAction(actionEvent -> showFormView(null));

        typeCombo.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(INFRA_CONFIG_TYPE)));
        typeCombo.valueProperty().bindBidirectional(viewModel.typeProperty());
        typeCombo.setButtonCell(new ListCell<>() {
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
        typeCombo.setCellFactory(new Callback<ListView<DictDataSimpleRespVO>, ListCell<DictDataSimpleRespVO>>() {
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

    }



    /**
     * 显示编辑对话框
     */
    private void showFormView(Long id) {
        boolean isAdd = (id == null);
        ConfigFormView load = ViewLoader.load(ConfigFormView.class);
        load.getViewModel().query(id);
        new ConfirmDialog.Builder(modalPane)
                .title(isAdd ? "添加配置" : "编辑配置")
                .content(load.getNode())
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    if (isAdd) {
                        load.getViewModel().createConfig(d);
                    } else {
                        load.getViewModel().updateConfig(d);
                    }
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }


    private void showDelDialog(ConfigRespVO respVO) {

        new ConfirmDialog.Builder(modalPane)
                .title("删除配置")
                .message("是否确认删除名称为【 " + respVO.getName() + " 】吗?")
                .width(450)
                .height(150)
                .onConfirm(d -> {
                    viewModel.deleteConfig(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }
}
