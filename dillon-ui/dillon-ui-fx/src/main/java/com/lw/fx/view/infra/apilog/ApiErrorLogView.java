package com.lw.fx.view.infra.apilog;

import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.apilog.ApiErrorLogFeign;
import com.lw.ui.utils.DictTypeEnum;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import io.datafx.core.concurrent.ProcessChain;
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
import java.util.Map;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;
import static com.lw.ui.utils.DictTypeEnum.INFRA_API_ERROR_LOG_PROCESS_STATUS;
import static com.lw.ui.utils.DictTypeEnum.USER_TYPE;

public class ApiErrorLogView implements FxmlView<ApiErrorLogViewModel>, Initializable {

    @InjectViewModel
    private ApiErrorLogViewModel viewModel;
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
    private TableColumn<ApiErrorLogRespVO, Integer> optCol;

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

    private DialogPane dialogPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dialogPane = new DialogPane();
        rootPane.getChildren().add(dialogPane);
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
        optCol.setCellFactory(new Callback<TableColumn<ApiErrorLogRespVO, Integer>, TableCell<ApiErrorLogRespVO, Integer>>() {
            @Override
            public TableCell<ApiErrorLogRespVO, Integer> call(TableColumn<ApiErrorLogRespVO, Integer> param) {

                TableCell cell = new TableCell<ApiErrorLogRespVO, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            Button editBut = new Button("详情");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem()));
                            editBut.setGraphic(FontIcon.of(Feather.EYE));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button processedBut = new Button("已处理");
                            processedBut.setOnAction(event -> showDelDialog(getTableRow().getItem().getId(),1));
                            processedBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button ignoredBut = new Button("已忽略");
                            ignoredBut.setOnAction(event -> showDelDialog(getTableRow().getItem().getId(),2));
                            ignoredBut.getStyleClass().addAll(FLAT, ACCENT);

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

        processStatusComBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(INFRA_API_ERROR_LOG_PROCESS_STATUS)));
        processStatusComBox.valueProperty().bindBidirectional(viewModel.processStatusProperty());
    }


    /**
     * 显示编辑对话框
     */
    private void showFormView(ApiErrorLogRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();


        dialog.addActions(

                Map.entry(new Button("确定"), event -> {
                    dialog.close();
                })
        );

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

        vBox.setPrefWidth(500);

        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("详情");
        dialog.setContent(vBox);
        dialog.show(rootPane.getScene());

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


    private void showDelDialog(Integer id, Integer processStatus) {

        String type = processStatus == 1 ? "已处理" : "已忽略";
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("系统提示");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(ApiErrorLogFeign.class).updateApiErrorLogProcess(id.longValue(), processStatus);
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    MvvmFX.getNotificationCenter().publish("message", "删除成功", MessageType.SUCCESS);

                                    dialog.close();
                                    viewModel.loadTableData();
                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setContent(new Label("确认标记为" + type + "？"));
        dialog.show(rootPane.getScene());
    }

}
