package com.lw.fx.view.system.notice;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.fx.vo.NotifyMessageVo;
import com.lw.ui.request.api.system.OperateLogFeign;
import com.lw.ui.utils.DictTypeEnum;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import io.datafx.core.concurrent.ProcessChain;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
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
import static com.lw.ui.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;
import static com.lw.ui.utils.DictTypeEnum.SYSTEM_NOTIFY_TEMPLATE_TYPE;

public class MyNotifyMessageView implements FxmlView<MyNotifyMessageViewModel>, Initializable {

    @InjectViewModel
    private MyNotifyMessageViewModel viewModel;
    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<NotifyMessageVo, LocalDateTime> createTimeCol;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<NotifyMessageVo, Long> optCol;
    @FXML
    private TableColumn<NotifyMessageVo, Boolean> selCol;

    @FXML
    private TableColumn<NotifyMessageVo, Boolean> readStatusCol;

    @FXML
    private ComboBox<DictDataSimpleRespVO> readStatusComboBox;

    @FXML
    private TableColumn<NotifyMessageVo, LocalDateTime> readTimeCol;

    @FXML
    private Button resetBut;
    @FXML
    private Button handleUpdateListBut;
    @FXML
    private Button handleUpdateAllBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableView<NotifyMessageVo> tableView;

    @FXML
    private TableColumn<NotifyMessageVo, String> templateContentCol;

    @FXML
    private TableColumn<NotifyMessageVo, String> templateNicknameCol;

    @FXML
    private TableColumn<NotifyMessageVo, Integer> templateTypeCol;
    @FXML
    private CheckBox selAllCheckBox;

    private PagingControl pagingControl;

    private DialogPane dialogPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        readStatusComboBox.setItems(viewModel.getStautsItems());
        readStatusComboBox.valueProperty().addListener((observableValue, dictDataSimpleRespVO, t1) -> {
            Boolean statsu = null;
            if (t1 != null) {
                statsu = Convert.toBool(t1.getValue());
            }
            viewModel.statusProperty().set(statsu);
        });
        tableView.setItems(viewModel.getTableItems());
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
        templateNicknameCol.setCellValueFactory(new PropertyValueFactory<>("templateNickname"));
        templateNicknameCol.setStyle("-fx-alignment: CENTER");

        templateTypeCol.setCellValueFactory(new PropertyValueFactory<>("templateType"));
        templateTypeCol.setStyle("-fx-alignment: CENTER");

        templateContentCol.setCellValueFactory(new PropertyValueFactory<>("templateContent"));
        templateContentCol.setStyle("-fx-alignment: CENTER");

        readStatusCol.setCellValueFactory(new PropertyValueFactory<>("readStatus"));
        readStatusCol.setStyle("-fx-alignment: CENTER");

        readTimeCol.setCellValueFactory(new PropertyValueFactory<>("readTime"));
        readTimeCol.setStyle("-fx-alignment: CENTER");


        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");
        createTimeCol.setCellFactory(new Callback<TableColumn<NotifyMessageVo, LocalDateTime>, TableCell<NotifyMessageVo, LocalDateTime>>() {
            @Override
            public TableCell<NotifyMessageVo, LocalDateTime> call(TableColumn<NotifyMessageVo, LocalDateTime> param) {
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
        readTimeCol.setCellFactory(new Callback<TableColumn<NotifyMessageVo, LocalDateTime>, TableCell<NotifyMessageVo, LocalDateTime>>() {
            @Override
            public TableCell<NotifyMessageVo, LocalDateTime> call(TableColumn<NotifyMessageVo, LocalDateTime> param) {
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

        templateTypeCol.setCellFactory(new Callback<TableColumn<NotifyMessageVo, Integer>, TableCell<NotifyMessageVo, Integer>>() {
            @Override
            public TableCell<NotifyMessageVo, Integer> call(TableColumn<NotifyMessageVo, Integer> param) {
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

        readStatusCol.setCellFactory(new Callback<TableColumn<NotifyMessageVo, Boolean>, TableCell<NotifyMessageVo, Boolean>>() {
            @Override
            public TableCell<NotifyMessageVo, Boolean> call(TableColumn<NotifyMessageVo, Boolean> param) {
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

        optCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        optCol.setCellFactory(new Callback<TableColumn<NotifyMessageVo, Long>, TableCell<NotifyMessageVo, Long>>() {
            @Override
            public TableCell<NotifyMessageVo, Long> call(TableColumn<NotifyMessageVo, Long> param) {

                TableCell cell = new TableCell<NotifyMessageVo, Long>() {
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
        selCol.setCellValueFactory(c -> c.getValue().selectdProperty());
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        searchBut.setOnAction(actionEvent -> {
            viewModel.loadTableData();
        });
        resetBut.setOnAction(actionEvent -> {
            readStatusComboBox.setValue(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

        selAllCheckBox.setOnAction(actionEvent -> viewModel.selAll(selAllCheckBox.isSelected()));
        handleUpdateAllBut.setOnAction(actionEvent -> viewModel.updateAllNotifyMessageRead());
        handleUpdateListBut.setOnAction(actionEvent -> viewModel.updateNotifyMessageRead());
    }


    /**
     * 显示编辑对话框
     */
    private void showFormView(NotifyMessageVo respVo) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        HBox templateContentBox = createTextAreaField("内容", respVo.getTemplateContent());
        VBox.setVgrow(templateContentBox, Priority.ALWAYS);
        VBox vBox = new VBox(10, createTextField("发送人", respVo.getTemplateNickname())
                , createTextField("发送时间", DateUtil.format(respVo.getCreateTime(),"yyyy-MM-dd HH:mm:ss"))
                , createDictBut("消息类型", respVo.getTemplateType(), SYSTEM_NOTIFY_TEMPLATE_TYPE)
                , createDictBut("是否已读", respVo.getReadStatus(), INFRA_BOOLEAN_STRING)
                , createTextField("阅读时间", DateUtil.format(respVo.getReadTime(),"yyyy-MM-dd HH:mm:ss"))
                , templateContentBox

        );

        dialog.addActions(
                Map.entry(new Button("确定"), event -> {
                    dialog.close();
                })
        );

        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("详情");
        dialog.setContent(vBox);
        dialog.show(rootPane.getScene());

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

    private void showDelDialog(NotifyMessageVo respVO) {
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

        dialog.setContent(new Label("是否确认删除名称为" + respVO.getTemplateNickname() + "-" + respVO.getTemplateContent() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }
}
