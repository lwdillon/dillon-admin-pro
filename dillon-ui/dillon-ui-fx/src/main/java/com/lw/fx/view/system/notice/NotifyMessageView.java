package com.lw.fx.view.system.notice;

import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.system.OperateLogFeign;
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
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;
import static com.lw.ui.utils.DictTypeEnum.*;

public class NotifyMessageView implements FxmlView<NotifyMessageViewModel>, Initializable {

    @InjectViewModel
    private NotifyMessageViewModel viewModel;
    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<NotifyMessageRespVO, LocalDateTime> createTimeCol;

    @FXML
    private Button emptyBut;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<NotifyMessageRespVO, ?> idCol;

    @FXML
    private TableColumn<NotifyMessageRespVO, Long> optCol;

    @FXML
    private TableColumn<NotifyMessageRespVO, Boolean> readStatusCol;

    @FXML
    private TableColumn<NotifyMessageRespVO, LocalDateTime> readTimeCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableView<NotifyMessageRespVO> tableView;

    @FXML
    private TableColumn<NotifyMessageRespVO, ?> templateCodeCol;

    @FXML
    private TextField templateCodeField;

    @FXML
    private TableColumn<NotifyMessageRespVO, ?> templateContentCol;

    @FXML
    private TableColumn<NotifyMessageRespVO, ?> templateNicknameCol;

    @FXML
    private TableColumn<NotifyMessageRespVO, ?> templateParamsCol;

    @FXML
    private TableColumn<NotifyMessageRespVO, Integer> templateTypeCol;

    @FXML
    private ComboBox<DictDataSimpleRespVO> templateTypeComBox;

    @FXML
    private TableColumn<NotifyMessageRespVO, ?> userIdCol;

    @FXML
    private TextField userIdField;

    @FXML
    private TableColumn<NotifyMessageRespVO, Byte> userTypeCol;

    @FXML
    private ComboBox<DictDataSimpleRespVO> userTypeComBox;

    private PagingControl pagingControl;

    private DialogPane dialogPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userTypeComBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(USER_TYPE)));
        userTypeComBox.valueProperty().addListener((observableValue, userSimpleRespVO, t1) -> {
            if (t1 == null) {
                viewModel.userTypeProperty().set(null);
            }else {
                viewModel.userTypeProperty().set(t1.getValue());
            }

        });
        templateTypeComBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(SYSTEM_NOTIFY_TEMPLATE_TYPE)));
        templateTypeComBox.valueProperty().addListener((observableValue, userSimpleRespVO, t1) -> {
            if (t1 == null) {
                viewModel.templateTypeProperty().set(null);
            }else {
                viewModel.templateTypeProperty().set(t1.getValue());
            }
        });

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

        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
        userTypeCol.setStyle("-fx-alignment: CENTER");
        userTypeCol.setCellFactory(new Callback<TableColumn<NotifyMessageRespVO, Byte>, TableCell<NotifyMessageRespVO, Byte>>() {
            @Override
            public TableCell<NotifyMessageRespVO, Byte> call(TableColumn<NotifyMessageRespVO, Byte> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Byte item, boolean empty) {
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

        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setStyle("-fx-alignment: CENTER");

        templateCodeCol.setCellValueFactory(new PropertyValueFactory<>("templateCode"));
        templateCodeCol.setStyle("-fx-alignment: CENTER");

        templateNicknameCol.setCellValueFactory(new PropertyValueFactory<>("templateNickname"));
        templateNicknameCol.setStyle("-fx-alignment: CENTER");

        templateContentCol.setCellValueFactory(new PropertyValueFactory<>("templateContent"));
        templateContentCol.setStyle("-fx-alignment: CENTER");

        templateParamsCol.setCellValueFactory(new PropertyValueFactory<>("templateParams"));
        templateParamsCol.setStyle("-fx-alignment: CENTER");

        templateTypeCol.setCellValueFactory(new PropertyValueFactory<>("templateType"));
        templateTypeCol.setStyle("-fx-alignment: CENTER");
        templateTypeCol.setCellFactory(new Callback<TableColumn<NotifyMessageRespVO, Integer>, TableCell<NotifyMessageRespVO, Integer>>() {
            @Override
            public TableCell<NotifyMessageRespVO, Integer> call(TableColumn<NotifyMessageRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(SYSTEM_NOTIFY_TEMPLATE_TYPE).get(item + "");
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

        readStatusCol.setCellValueFactory(new PropertyValueFactory<>("readStatus"));
        readStatusCol.setStyle("-fx-alignment: CENTER");
        readStatusCol.setCellFactory(new Callback<TableColumn<NotifyMessageRespVO, Boolean>, TableCell<NotifyMessageRespVO, Boolean>>() {
            @Override
            public TableCell<NotifyMessageRespVO, Boolean> call(TableColumn<NotifyMessageRespVO, Boolean> param) {
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

        readTimeCol.setCellValueFactory(new PropertyValueFactory<>("readTime"));
        readTimeCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");
        createTimeCol.setCellFactory(new Callback<TableColumn<NotifyMessageRespVO, LocalDateTime>, TableCell<NotifyMessageRespVO, LocalDateTime>>() {
            @Override
            public TableCell<NotifyMessageRespVO, LocalDateTime> call(TableColumn<NotifyMessageRespVO, LocalDateTime> param) {
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

        readTimeCol.setCellFactory(new Callback<TableColumn<NotifyMessageRespVO, LocalDateTime>, TableCell<NotifyMessageRespVO, LocalDateTime>>() {
            @Override
            public TableCell<NotifyMessageRespVO, LocalDateTime> call(TableColumn<NotifyMessageRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<NotifyMessageRespVO, Long>, TableCell<NotifyMessageRespVO, Long>>() {
            @Override
            public TableCell<NotifyMessageRespVO, Long> call(TableColumn<NotifyMessageRespVO, Long> param) {

                TableCell cell = new TableCell<NotifyMessageRespVO, Long>() {
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

        userIdField.textProperty().bindBidirectional(viewModel.userIdProperty());
        templateCodeField.textProperty().bindBidirectional(viewModel.templateCodeProperty());


        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            userIdField.setText(null);
            templateCodeField.setText(null);
            userTypeComBox.setValue(null);
            templateTypeComBox.setValue(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

    }

    private HBox createTextField(String text, String value) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPrefWidth(80);
        TextField textField = new TextField(value);
        textField.setEditable(false);
        HBox.setHgrow(textField, Priority.ALWAYS);
        HBox hBox = new HBox(7, label, textField);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private HBox createTextAreaField(String text, String value) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPrefWidth(80);
        TextArea textField = new TextArea(value);
        textField.setEditable(false);
        textField.setWrapText(true);
        HBox.setHgrow(textField, Priority.ALWAYS);
        HBox hBox = new HBox(7, label, textField);
        hBox.setAlignment(Pos.TOP_LEFT);
        return hBox;
    }

    private HBox createDictBut(String text, Object value, DictTypeEnum dictTypeEnum) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPrefWidth(80);

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

    /**
     * 显示编辑对话框
     */
    private void showFormView(NotifyMessageRespVO respVo) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.addActions(

                Map.entry(new Button("确定"), event -> {
                    dialog.close();
                })
        );


        HBox templateContentBox = createTextAreaField("模版内容", respVo.getTemplateContent());
        VBox.setVgrow(templateContentBox, Priority.ALWAYS);
        VBox vBox = new VBox(10, createTextField("编号", respVo.getId()+"")
                , createDictBut("用户类型", respVo.getUserType(), USER_TYPE)
                , createTextField("用户编号", respVo.getUserId()+"")
                , createTextField("模版编号", respVo.getTemplateId()+"")
                , createTextField("模板编码", respVo.getTemplateCode()+"")
                , createTextField("发送人名称", respVo.getTemplateNickname()+"")
                , createTextField("模版参数", respVo.getTemplateParams()+"")
                , createDictBut("模版类型", respVo.getTemplateType(), SYSTEM_NOTIFY_TEMPLATE_TYPE)
                , createDictBut("是否已读", respVo.getReadStatus(), INFRA_BOOLEAN_STRING)
                , createTextField("阅读时间",DateUtil.format(respVo.getReadTime(),"yyyy-MM-dd HH:mm:ss"))
                , createTextField("创建时间",DateUtil.format(respVo.getCreateTime(),"yyyy-MM-dd HH:mm:ss"))
                , templateContentBox

        );


        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("详情");
        dialog.setContent(vBox);
        dialog.show(rootPane.getScene());

    }


    private void showDelDialog(NotifyMessageRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("删除");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(OperateLogFeign.class).deleteOperateLog(respVO.getId());
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    dialog.close();
                                    MvvmFX.getNotificationCenter().publish("message", "删除成功", MessageType.SUCCESS);

                                    viewModel.loadTableData();
                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setContent(new Label("是否确认删除名称为" + respVO.getId() + "-" + respVO.getTemplateContent() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }
}
