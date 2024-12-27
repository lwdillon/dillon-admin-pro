package com.lw.fx.view.infra.apilog;

import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.store.AppStore;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.utils.DictTypeEnum;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
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
import static com.lw.ui.utils.DictTypeEnum.INFRA_OPERATE_TYPE;
import static com.lw.ui.utils.DictTypeEnum.USER_TYPE;

public class ApiAccessLogView implements FxmlView<ApiAccessLogViewModel>, Initializable {

    @InjectViewModel
    private ApiAccessLogViewModel viewModel;
    @FXML
    private TableColumn<ApiAccessLogRespVO, ?> applicationNameCol;

    @FXML
    private TextField applicationNameField;

    @FXML
    private TableColumn<ApiAccessLogRespVO, LocalDateTime> beginTimeCol;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<ApiAccessLogRespVO, ?> durationCol;

    @FXML
    private TextField durationField;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<ApiAccessLogRespVO, ?> idCol;

    @FXML
    private TableColumn<ApiAccessLogRespVO, ?> operateModuleCol;

    @FXML
    private TableColumn<ApiAccessLogRespVO, ?> operateNameCol;

    @FXML
    private TableColumn<ApiAccessLogRespVO, Integer> operateTypeCol;

    @FXML
    private TableColumn<ApiAccessLogRespVO, Long> optCol;

    @FXML
    private TableColumn<ApiAccessLogRespVO, ?> requestMethodCol;

    @FXML
    private TableColumn<ApiAccessLogRespVO, ?> requestUrlCol;

    @FXML
    private Button resetBut;

    @FXML
    private TextField resultCodeField;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<ApiAccessLogRespVO, Integer> statusCol;

    @FXML
    private TableView<ApiAccessLogRespVO> tableView;

    @FXML
    private TableColumn<ApiAccessLogRespVO, ?> userIdCol;

    @FXML
    private TextField userIdField;

    @FXML
    private TableColumn<ApiAccessLogRespVO, Integer> userTypeCol;

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
        userTypeCol.setCellFactory(new Callback<TableColumn<ApiAccessLogRespVO, Integer>, TableCell<ApiAccessLogRespVO, Integer>>() {
            @Override
            public TableCell<ApiAccessLogRespVO, Integer> call(TableColumn<ApiAccessLogRespVO, Integer> param) {
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
        operateTypeCol.setCellValueFactory(new PropertyValueFactory<>("operateType"));
        operateTypeCol.setStyle("-fx-alignment: CENTER");
        operateTypeCol.setCellFactory(new Callback<TableColumn<ApiAccessLogRespVO, Integer>, TableCell<ApiAccessLogRespVO, Integer>>() {
            @Override
            public TableCell<ApiAccessLogRespVO, Integer> call(TableColumn<ApiAccessLogRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(INFRA_OPERATE_TYPE).get(item + "");
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

        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationCol.setStyle("-fx-alignment: CENTER");

        operateModuleCol.setCellValueFactory(new PropertyValueFactory<>("operateModule"));
        operateModuleCol.setStyle("-fx-alignment: CENTER");
        operateNameCol.setCellValueFactory(new PropertyValueFactory<>("operateName"));
        operateNameCol.setStyle("-fx-alignment: CENTER");

        statusCol.setCellValueFactory(new PropertyValueFactory<>("resultCode"));
        statusCol.setStyle("-fx-alignment: CENTER");
        statusCol.setCellFactory(new Callback<TableColumn<ApiAccessLogRespVO, Integer>, TableCell<ApiAccessLogRespVO, Integer>>() {
            @Override
            public TableCell<ApiAccessLogRespVO, Integer> call(TableColumn<ApiAccessLogRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            String text = "";
                            String colorType = "";
                            if (item == 0) {
                                text = "成功";
                                colorType = "success";
                            } else {
                                text = "失败(" + getTableRow().getItem().getResultMsg() + ")";
                                colorType = "danger";
                            }
                            Button state = new Button(text);
                            switch (colorType) {
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

        beginTimeCol.setCellValueFactory(new PropertyValueFactory<>("beginTime"));
        beginTimeCol.setStyle("-fx-alignment: CENTER");
        beginTimeCol.setCellFactory(new Callback<TableColumn<ApiAccessLogRespVO, LocalDateTime>, TableCell<ApiAccessLogRespVO, LocalDateTime>>() {
            @Override
            public TableCell<ApiAccessLogRespVO, LocalDateTime> call(TableColumn<ApiAccessLogRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<ApiAccessLogRespVO, Long>, TableCell<ApiAccessLogRespVO, Long>>() {
            @Override
            public TableCell<ApiAccessLogRespVO, Long> call(TableColumn<ApiAccessLogRespVO, Long> param) {

                TableCell cell = new TableCell<ApiAccessLogRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            Button editBut = new Button("详情");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem()));
                            editBut.setGraphic(FontIcon.of(Feather.EYE));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);

                            HBox box = new HBox(editBut);
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
        durationField.textProperty().bindBidirectional(viewModel.durationProperty());
        resultCodeField.textProperty().bindBidirectional(viewModel.resultCodeProperty());

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            userIdField.setText(null);
            applicationNameField.setText(null);
            durationField.setText(null);
            resultCodeField.setText(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            userTypeComboBox.getSelectionModel().select(null);
            viewModel.loadTableData();
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());
        userTypeComboBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(USER_TYPE)));
        userTypeComboBox.valueProperty().bindBidirectional(viewModel.selUserTypeProperty());
    }


    /**
     * 显示编辑对话框
     */
    private void showFormView(ApiAccessLogRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();


        dialog.addActions(

                Map.entry(new Button("确定"), event -> {
                    dialog.close();
                })
        );

        VBox vBox = new VBox(10,
                createTextField("日志编号", respVO.getId()+"")
                , createTextField("链路追踪", respVO.getTraceId() )
                , createTextField("应用名", respVO.getApplicationName())
                , createDictBut("用户信息", respVO.getUserType(),USER_TYPE)
                , createTextField("用户 IP", respVO.getUserIp())
                , createTextField("用户 UA", respVO.getUserAgent())
                , createTextField("请求信息", respVO.getRequestMethod()+" "+respVO.getRequestUrl())
                , createTextField("请求参数", respVO.getRequestParams())
                , createTextField("请求结果", respVO.getResponseBody())
                , createTextField("请求时间", DateUtil.format(respVO.getBeginTime(), "yyyy-MM-dd HH:mm:ss") + " ~ " + DateUtil.format(respVO.getEndTime(), "yyyy-MM-dd HH:mm:ss"))
                , createTextField("请求耗时", respVO.getDuration() + "毫秒")
                , createTextField("操作结果", respVO.getResultCode()==0?"正常":("失败 | "+respVO.getResultCode()+" | "+respVO.getResultMsg()))
                , createTextField("操作模块", respVO.getOperateModule())
                , createTextField("操作名", respVO.getOperateName())
                , createDictBut("操作类型", respVO.getOperateType(),INFRA_OPERATE_TYPE)

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


}
