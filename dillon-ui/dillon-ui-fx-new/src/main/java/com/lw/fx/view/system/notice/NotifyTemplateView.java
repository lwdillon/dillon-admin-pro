package com.lw.fx.view.system.notice;

import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.system.NotifyTemplateFeign;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
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
import java.util.Map;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;
import static com.lw.ui.utils.DictTypeEnum.COMMON_STATUS;
import static com.lw.ui.utils.DictTypeEnum.SYSTEM_NOTIFY_TEMPLATE_TYPE;

public class NotifyTemplateView implements FxmlView<NotifyTemplateViewModel>, Initializable {

    @InjectViewModel
    private NotifyTemplateViewModel viewModel;

    @FXML
    private Button addBut;

    @FXML
    private TextField codeField;

    @FXML
    private TableColumn<NotifyTemplateRespVO, ?> contentCol;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<NotifyTemplateRespVO, LocalDateTime> createTimeCol;

    @FXML
    private TableColumn<NotifyTemplateRespVO, ?> idCol;

    @FXML
    private TableColumn<NotifyTemplateRespVO, ?> nameCol;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<NotifyTemplateRespVO, ?> nicknameCol;

    @FXML
    private TableColumn<NotifyTemplateRespVO, Long> optCol;

    @FXML
    private TableColumn<NotifyTemplateRespVO, ?> remarkCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private TableColumn<NotifyTemplateRespVO, Integer> statusCol;

    @FXML
    private ComboBox<DictDataSimpleRespVO> statusCombo;

    @FXML
    private TableView<NotifyTemplateRespVO> tableView;

    @FXML
    private TableColumn<NotifyTemplateRespVO, Integer> typeCol;

    private PagingControl pagingControl;

    private DialogPane dialogPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        statusCombo.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(COMMON_STATUS)));
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

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER");

        nicknameCol.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        nicknameCol.setStyle("-fx-alignment: CENTER");

        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        contentCol.setStyle("-fx-alignment: CENTER");

        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
        remarkCol.setStyle("-fx-alignment: CENTER");

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle("-fx-alignment: CENTER");
        typeCol.setCellFactory(new Callback<TableColumn<NotifyTemplateRespVO, Integer>, TableCell<NotifyTemplateRespVO, Integer>>() {
            @Override
            public TableCell<NotifyTemplateRespVO, Integer> call(TableColumn<NotifyTemplateRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
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


        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER");
        statusCol.setCellFactory(new Callback<TableColumn<NotifyTemplateRespVO, Integer>, TableCell<NotifyTemplateRespVO, Integer>>() {
            @Override
            public TableCell<NotifyTemplateRespVO, Integer> call(TableColumn<NotifyTemplateRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(COMMON_STATUS).get(item + "");
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

        createTimeCol.setCellFactory(new Callback<TableColumn<NotifyTemplateRespVO, LocalDateTime>, TableCell<NotifyTemplateRespVO, LocalDateTime>>() {
            @Override
            public TableCell<NotifyTemplateRespVO, LocalDateTime> call(TableColumn<NotifyTemplateRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<NotifyTemplateRespVO, Long>, TableCell<NotifyTemplateRespVO, Long>>() {
            @Override
            public TableCell<NotifyTemplateRespVO, Long> call(TableColumn<NotifyTemplateRespVO, Long> param) {

                TableCell cell = new TableCell<NotifyTemplateRespVO, Long>() {
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

                            Button pushBut = new Button("推送");
                            pushBut.setOnAction(actionEvent -> push(getTableRow().getItem().getId()));
                            pushBut.setGraphic(FontIcon.of(Feather.MESSAGE_SQUARE));
                            pushBut.getStyleClass().addAll(FLAT, ACCENT);
                            HBox box = new HBox(editBut, delBut, pushBut);
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

        codeField.textProperty().bindBidirectional(viewModel.codeProperty());
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());

        statusCombo.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> viewModel.statusProperty().set(t1.intValue()));

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            codeField.setText(null);
            nameField.setText(null);
            statusCombo.getSelectionModel().select(null);
        });

        addBut.setOnAction(actionEvent -> showFormView(null));
    }


    /**
     * 显示编辑对话框
     */
    private void showFormView(Long id) {
        WFXGenericDialog dialog = new WFXGenericDialog();

        boolean isAdd = (id == null);
        ViewTuple<NotifyTemplateFormView, NotifyTemplateFormViewModel> load = FluentViewLoader.fxmlView(NotifyTemplateFormView.class).load();
        load.getViewModel().query(id);
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return load.getViewModel().save(isAdd);
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    dialog.close();
                                    MvvmFX.getNotificationCenter().publish("message", "保存成功", MessageType.SUCCESS);

                                    viewModel.loadTableData();

                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText(id != null ? "编辑" : "添加");
        dialog.setContent(load.getView());
        dialog.show(rootPane.getScene());

    }


    private void showDelDialog(NotifyTemplateRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("删除");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(NotifyTemplateFeign.class).deleteNotifyTemplate(respVO.getId());
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

        dialog.setContent(new Label("是否确认删除名称为" + respVO.getName() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }

    private void push(Long id) {
        WFXGenericDialog dialog = new WFXGenericDialog();

        ViewTuple<NotifyTemplateSendFormView, NotifyTemplateSendFormViewModel> load = FluentViewLoader.fxmlView(NotifyTemplateSendFormView.class).load();
        load.getViewModel().query(id);
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("发送"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return load.getViewModel().sendNotify();
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    dialog.close();
                                    MvvmFX.getNotificationCenter().publish("message", "发送成功", MessageType.SUCCESS);

                                    viewModel.loadTableData();

                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("消息发送");
        dialog.setContent(load.getView());
        dialog.show(rootPane.getScene());
    }
}
