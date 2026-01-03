package com.dillon.lw.fx.view.infra.apilog;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.store.AppStore;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.PagingControl;
import com.dillon.lw.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.utils.DictTypeEnum;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;
import static com.dillon.lw.utils.DictTypeEnum.INFRA_API_ERROR_LOG_PROCESS_STATUS;
import static com.dillon.lw.utils.DictTypeEnum.USER_TYPE;

public class ApiErrorLogView extends BaseView<ApiErrorLogViewModel> implements Initializable {

    @FXML
    private TableColumn<ApiErrorLogRespVO, ?> applicationNameCol;

    @FXML
    private TextField applicationNameField;

    @FXML
    private TableColumn<ApiErrorLogRespVO, LocalDateTime> exceptionTimeCol;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<ApiErrorLogRespVO, ?> exceptionNameCol;


    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<ApiErrorLogRespVO, ?> idCol;


    @FXML
    private TableColumn<ApiErrorLogRespVO, Long> optCol;

    @FXML
    private TableColumn<ApiErrorLogRespVO, ?> requestMethodCol;

    @FXML
    private TableColumn<ApiErrorLogRespVO, ?> requestUrlCol;

    @FXML
    private Button resetBut;

    @FXML
    private ComboBox<DictDataSimpleRespVO> processStatusComBox;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<ApiErrorLogRespVO, Integer> processStatusCol;

    @FXML
    private TableView<ApiErrorLogRespVO> tableView;

    @FXML
    private TableColumn<ApiErrorLogRespVO, ?> userIdCol;

    @FXML
    private TextField userIdField;

    @FXML
    private TableColumn<ApiErrorLogRespVO, Integer> userTypeCol;

    @FXML
    private ComboBox<DictDataSimpleRespVO> userTypeComboBox;

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

        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setStyle("-fx-alignment: CENTER");

        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
        userTypeCol.setStyle("-fx-alignment: CENTER");
        userTypeCol.setCellFactory(new Callback<TableColumn<ApiErrorLogRespVO, Integer>, TableCell<ApiErrorLogRespVO, Integer>>() {
            @Override
            public TableCell<ApiErrorLogRespVO, Integer> call(TableColumn<ApiErrorLogRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(USER_TYPE).get(item + "");
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
        processStatusCol.setCellValueFactory(new PropertyValueFactory<>("processStatus"));
        processStatusCol.setStyle("-fx-alignment: CENTER");
        processStatusCol.setCellFactory(new Callback<TableColumn<ApiErrorLogRespVO, Integer>, TableCell<ApiErrorLogRespVO, Integer>>() {
            @Override
            public TableCell<ApiErrorLogRespVO, Integer> call(TableColumn<ApiErrorLogRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(INFRA_API_ERROR_LOG_PROCESS_STATUS).get(item + "");
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

        applicationNameCol.setCellValueFactory(new PropertyValueFactory<>("applicationName"));
        applicationNameCol.setStyle("-fx-alignment: CENTER");

        requestMethodCol.setCellValueFactory(new PropertyValueFactory<>("requestMethod"));
        requestMethodCol.setStyle("-fx-alignment: CENTER");

        requestUrlCol.setCellValueFactory(new PropertyValueFactory<>("requestUrl"));
        requestUrlCol.setStyle("-fx-alignment: CENTER");

        exceptionNameCol.setCellValueFactory(new PropertyValueFactory<>("exceptionName"));
        exceptionNameCol.setStyle("-fx-alignment: CENTER");


        exceptionTimeCol.setCellValueFactory(new PropertyValueFactory<>("exceptionTime"));
        exceptionTimeCol.setStyle("-fx-alignment: CENTER");
        exceptionTimeCol.setCellFactory(new Callback<TableColumn<ApiErrorLogRespVO, LocalDateTime>, TableCell<ApiErrorLogRespVO, LocalDateTime>>() {
            @Override
            public TableCell<ApiErrorLogRespVO, LocalDateTime> call(TableColumn<ApiErrorLogRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<ApiErrorLogRespVO, Long>, TableCell<ApiErrorLogRespVO, Long>>() {
            @Override
            public TableCell<ApiErrorLogRespVO, Long> call(TableColumn<ApiErrorLogRespVO, Long> param) {

                TableCell cell = new TableCell<ApiErrorLogRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            ApiErrorLogRespVO respVO = getTableRow().getItem();
                            Button editBut = new Button("详情");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem()));
                            editBut.setGraphic(FontIcon.of(Feather.EYE));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button processedBut = new Button("已处理");
                            processedBut.setOnAction(event -> showDelDialog(getTableRow().getItem().getId(), 1));
                            processedBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button ignoredBut = new Button("已忽略");
                            ignoredBut.setOnAction(event -> showDelDialog(getTableRow().getItem().getId(), 2));
                            ignoredBut.getStyleClass().addAll(FLAT, ACCENT);

                            if (respVO.getProcessStatus() != 0) {
                                processedBut.setVisible(false);
                                ignoredBut.setVisible(false);
                            }

                            HBox box = new HBox(editBut, processedBut, ignoredBut);
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

        userIdField.textProperty().bindBidirectional(viewModel.userIdProperty());
        applicationNameField.textProperty().bindBidirectional(viewModel.applicationNameProperty());

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            userIdField.setText(null);
            applicationNameField.setText(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            userTypeComboBox.getSelectionModel().select(null);
            processStatusComBox.getSelectionModel().select(null);
            viewModel.loadTableData();
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());
        userTypeComboBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(USER_TYPE)));
        userTypeComboBox.valueProperty().bindBidirectional(viewModel.selUserTypeProperty());
        userTypeComboBox.setButtonCell(new ListCell<>() {
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
        userTypeComboBox.setCellFactory(new Callback<ListView<DictDataSimpleRespVO>, ListCell<DictDataSimpleRespVO>>() {
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

        processStatusComBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(INFRA_API_ERROR_LOG_PROCESS_STATUS)));
        processStatusComBox.valueProperty().bindBidirectional(viewModel.processStatusProperty());
        processStatusComBox.setCellFactory(new Callback<ListView<DictDataSimpleRespVO>, ListCell<DictDataSimpleRespVO>>() {
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
        processStatusComBox.setButtonCell(new ListCell<>() {
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
    private void showFormView(ApiErrorLogRespVO respVO) {

        VBox vBox = new VBox(10,
                createTextField("日志编号", respVO.getId() + "")
                , createTextField("链路追踪", respVO.getTraceId())
                , createTextField("应用名", respVO.getApplicationName())
                , createDictBut("用户信息", respVO.getUserType(), USER_TYPE)
                , createTextField("用户 IP", respVO.getUserIp())
                , createTextField("用户 UA", respVO.getUserAgent())
                , createTextField("请求信息", respVO.getRequestMethod() + " " + respVO.getRequestUrl())
                , createTextField("请求参数", respVO.getRequestParams())
                , createTextField("异常时间", DateUtil.format(respVO.getExceptionTime(), "yyyy-MM-dd HH:mm:ss"))
                , createTextField("异常名", respVO.getExceptionName())
                , createTextField("异常堆栈", respVO.getExceptionStackTrace())
                , createDictBut("处理状态", respVO.getProcessStatus(), INFRA_API_ERROR_LOG_PROCESS_STATUS)
                , createTextField("处理人", respVO.getProcessUserId() + "")
                , createTextField("处理时间", DateUtil.format(respVO.getProcessTime(), "yyyy-MM-dd HH:mm:ss"))

        );

        new ConfirmDialog.Builder(modalPane)
                .title("详情")
                .content(vBox)
                .width(450)
                .height(450)
                .onConfirm(ConfirmDialog::close)
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }


    private HBox createTextField(String text, String value) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPrefWidth(100);
        TextField textField = new TextField(value);
        textField.setEditable(false);
        HBox.setHgrow(textField, Priority.ALWAYS);
        HBox hBox = new HBox(7, label, textField);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private HBox createDictBut(String text, Object value, DictTypeEnum dictTypeEnum) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPrefWidth(100);

        DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(dictTypeEnum).get(value + "");
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
        HBox hBox = new HBox(7, label, state);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }


    private void showDelDialog(Long id, Integer processStatus) {

        String type = processStatus == 1 ? "已处理" : "已忽略";

        new ConfirmDialog.Builder(modalPane)
                .title("标记")
                .message("确认标记为【" + type + "】吗？")
                .width(400)
                .height(100)
                .onConfirm(d -> {
                    viewModel.updateApiErrorLogProcess(id, processStatus, d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }

}
