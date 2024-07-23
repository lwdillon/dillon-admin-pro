package com.lw.fx.view.system.notice;

import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notice.vo.NoticeRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.system.NoticeFeign;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
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
import static com.lw.ui.utils.DictTypeEnum.SYSTEM_NOTICE_TYPE;

public class NoticeView implements FxmlView<NoticeViewModel>, Initializable {

    @InjectViewModel
    private NoticeViewModel viewModel;

    @FXML
    private Button addBut;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<NoticeRespVO, LocalDateTime> createTimeCol;

    @FXML
    private TableColumn<NoticeRespVO, Long> idCol;

    @FXML
    private TableColumn<NoticeRespVO, Long> optCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private TableColumn<NoticeRespVO, Integer> statusCol;

    @FXML
    private ComboBox<?> statusCombo;

    @FXML
    private TableView<NoticeRespVO> tableView;

    @FXML
    private TableColumn<NoticeRespVO, String> titleCol;

    @FXML
    private TextField titleField;

    @FXML
    private TableColumn<NoticeRespVO, Integer> typeCol;

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
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setStyle("-fx-alignment: CENTER");

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle("-fx-alignment: CENTER");
        typeCol.setCellFactory(new Callback<TableColumn<NoticeRespVO, Integer>, TableCell<NoticeRespVO, Integer>>() {
            @Override
            public TableCell<NoticeRespVO, Integer> call(TableColumn<NoticeRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(SYSTEM_NOTICE_TYPE).get(item + "");
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

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");


        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER");


        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellFactory(new Callback<TableColumn<NoticeRespVO, LocalDateTime>, TableCell<NoticeRespVO, LocalDateTime>>() {
            @Override
            public TableCell<NoticeRespVO, LocalDateTime> call(TableColumn<NoticeRespVO, LocalDateTime> param) {
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

        statusCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button state = new Button();
                        if (item != null && item == 0) {
                            state.setText("正常");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                        } else {
                            state.setText("关闭");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                        }
                        HBox box = new HBox(state);
                        box.setPadding(new Insets(7, 7, 7, 7));
                        box.setAlignment(Pos.CENTER);
                        setGraphic(box);
                    }
                }
            };
        });
        optCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        optCol.setCellFactory(new Callback<TableColumn<NoticeRespVO, Long>, TableCell<NoticeRespVO, Long>>() {
            @Override
            public TableCell<NoticeRespVO, Long> call(TableColumn<NoticeRespVO, Long> param) {

                TableCell cell = new TableCell<NoticeRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Button editBut = new Button("修改");
                            editBut.setOnAction(event -> showPostFormView(getTableRow().getItem().getId()));
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

        titleField.textProperty().bindBidirectional(viewModel.titleProperty());

        statusCombo.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> viewModel.statusProperty().set(t1.intValue()));

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            titleCol.setText(null);
            statusCombo.getSelectionModel().select(null);
        });

        addBut.setOnAction(actionEvent -> showPostFormView(null));
    }


    /**
     * 显示编辑对话框
     */
    private void showPostFormView(Long roleId) {
        WFXGenericDialog dialog = new WFXGenericDialog();

        boolean isAdd = (roleId == null);
        ViewTuple<NoticeFormView, NoticeFormViewModel> load = FluentViewLoader.fxmlView(NoticeFormView.class).load();
        load.getViewModel().query(roleId);
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create().addRunnableInPlatformThread(() -> load.getViewModel().commitHtmText())
                            .addSupplierInExecutor(() -> {
                                return load.getViewModel().saveUser(isAdd);
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
        dialog.setHeaderText(roleId != null ? "编辑角色" : "添加角色");
        dialog.setContent(load.getView());
        dialog.show(rootPane.getScene());

    }


    private void showDelDialog(NoticeRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("删除角色");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(NoticeFeign.class).deleteNotice(respVO.getId());
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

        dialog.setContent(new Label("是否确认删除名称为" + respVO.getTitle() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }

    private void push(Long id) {
        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return Request.connector(NoticeFeign.class).push(id);
                })
                .addConsumerInPlatformThread(r -> {
                    if (r.isSuccess()) {
                        MvvmFX.getNotificationCenter().publish("message", "推送成功", MessageType.SUCCESS);

                        viewModel.loadTableData();
                    }
                }).onException(e -> e.printStackTrace())
                .run();
    }
}
