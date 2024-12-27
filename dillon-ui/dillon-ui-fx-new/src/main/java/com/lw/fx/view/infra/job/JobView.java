package com.lw.fx.view.infra.job;

import cn.hutool.core.date.DateUtil;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.job.JobFeign;
import com.lw.ui.utils.DictTypeEnum;
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
import static com.lw.ui.utils.DictTypeEnum.INFRA_JOB_STATUS;

public class JobView implements FxmlView<JobViewModel>, Initializable {
    @InjectViewModel
    private JobViewModel viewModel;
    @FXML
    private Button addBut;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<JobRespVO, ?> cronExpressionCol;

    @FXML
    private TableColumn<JobRespVO, ?> handlerNameCol;

    @FXML
    private TextField handlerNameField;

    @FXML
    private TableColumn<JobRespVO, ?> handlerParamCol;

    @FXML
    private TableColumn<JobRespVO, ?> idCol;

    @FXML
    private Button infoBut;

    @FXML
    private TableColumn<JobRespVO, ?> nameCol;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<JobRespVO, Long> optCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private TableColumn<JobRespVO, Integer> statusCol;

    @FXML
    private ComboBox<DictDataSimpleRespVO> statusComboBox;

    @FXML
    private TableView<JobRespVO> tableView;


    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

        statusComboBox.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(INFRA_JOB_STATUS)));
        statusComboBox.valueProperty().bindBidirectional(viewModel.statusProperty());
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        handlerNameField.textProperty().bindBidirectional(viewModel.handlerNameProperty());


        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER");

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER");
        statusCol.setCellFactory(new Callback<TableColumn<JobRespVO, Integer>, TableCell<JobRespVO, Integer>>() {
            @Override
            public TableCell<JobRespVO, Integer> call(TableColumn<JobRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(INFRA_JOB_STATUS).get(item + "");
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

        handlerNameCol.setCellValueFactory(new PropertyValueFactory<>("handlerName"));
        handlerNameCol.setStyle("-fx-alignment: CENTER");

        handlerParamCol.setCellValueFactory(new PropertyValueFactory<>("handlerParam"));
        handlerParamCol.setStyle("-fx-alignment: CENTER");


        cronExpressionCol.setCellValueFactory(new PropertyValueFactory<>("cronExpression"));
        cronExpressionCol.setStyle("-fx-alignment: CENTER");

        optCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        optCol.setCellFactory(new Callback<TableColumn<JobRespVO, Long>, TableCell<JobRespVO, Long>>() {
            @Override
            public TableCell<JobRespVO, Long> call(TableColumn<JobRespVO, Long> param) {

                TableCell cell = new TableCell<JobRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            int status=getTableRow().getItem().getStatus();

                            Button editBut = new Button("编辑");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem().getId()));
                            editBut.setGraphic(FontIcon.of(Feather.EDIT));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button onBut = new Button(status == 2 ? "开启" : "暂停");
                            onBut.setOnAction(event -> showOnDialog(getTableRow().getItem(), status == 2 ? 1 : 2));
                            onBut.setGraphic(FontIcon.of(Feather.ACTIVITY));
                            onBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button handleRunBut = new Button("执行一次");
                            handleRunBut.setOnAction(event -> showTriggerJobDialog(getTableRow().getItem()));
                            handleRunBut.setGraphic(FontIcon.of(Feather.PLAY));
                            handleRunBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button openDetailBut = new Button("任务详细");
                            openDetailBut.setOnAction(event -> showDetailView(getTableRow().getItem()));
                            openDetailBut.setGraphic(FontIcon.of(Feather.EYE));
                            openDetailBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button handleJobLogBut = new Button("调度日志");
                            handleJobLogBut.setOnAction(event -> {
                                openJobLogTab(getTableRow().getItem().getHandlerName());
                            });
                            handleJobLogBut.setGraphic(FontIcon.of(Feather.DATABASE));
                            handleJobLogBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button delBut = new Button("删除");
                            delBut.setOnAction(actionEvent -> showDelDialog(getTableRow().getItem()));
                            delBut.setGraphic(FontIcon.of(Feather.TRASH));
                            delBut.getStyleClass().addAll(FLAT, DANGER);
                            HBox box = new HBox(editBut, onBut, handleRunBut, openDetailBut, handleJobLogBut, delBut);
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
        infoBut.setOnAction(actionEvent -> openJobLogTab(null));
        resetBut.setOnAction(actionEvent -> {
            nameField.setText(null);
            statusComboBox.getSelectionModel().select(null);
            handlerNameField.setText(null);
            viewModel.loadTableData();
        });
    }

    private void openJobLogTab(String handlerName) {
        ViewTuple<JobLogView, JobLogViewModel> viewTuple = FluentViewLoader.fxmlView(JobLogView.class).load();
        viewTuple.getViewModel().handlerNameProperty().set(handlerName);
        viewTuple.getViewModel().loadTableData();
        MvvmFX.getNotificationCenter().publish("addTab", "调度日志", Feather.DATABASE, viewTuple.getView());
    }

    /**
     * 显示编辑对话框
     */
    private void showFormView(Long id) {
        WFXGenericDialog dialog = new WFXGenericDialog();

        boolean isAdd = (id == null);
        ViewTuple<JobFormView, JobFormViewModel> load = FluentViewLoader.fxmlView(JobFormView.class).load();
        load.getViewModel().updateData(id, isAdd);
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
        dialog.setHeaderText(id != null ? "编辑Job" : "添加Job");
        dialog.setContent(load.getView());
        dialog.show(rootPane.getScene());

    }

    private void showOnDialog(JobRespVO respVO, int status) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("温馨提示");
        String text = status == 1 ? "开启" : "关闭";
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(JobFeign.class).updateJobStatus(respVO.getId(), status);
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    dialog.close();
                                    MvvmFX.getNotificationCenter().publish("message", text + "成功", MessageType.SUCCESS);

                                    viewModel.loadTableData();
                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setContent(new Label("确认要" + text + "定时任务编号为" + respVO.getName() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }

    private void showTriggerJobDialog(JobRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("温馨提示");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(JobFeign.class).triggerJob(respVO.getId());
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    dialog.close();
                                    MvvmFX.getNotificationCenter().publish("message", "执行成功", MessageType.SUCCESS);

                                    viewModel.loadTableData();
                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setContent(new Label("确认要立即执行一次" + respVO.getName() + "的任务？"));
        dialog.show(rootPane.getScene());
    }

    private void showDelDialog(JobRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("温馨提示");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(JobFeign.class).deleteJob(respVO.getId());
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


    /**
     * 显示编辑对话框
     */
    private void showDetailView(JobRespVO respVo) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return Request.connector(JobFeign.class).getJobNextTimes(respVo.getId(), 5);
                })
                .addConsumerInPlatformThread(r -> {
                    if (r.isSuccess()) {

                        WFXGenericDialog dialog = new WFXGenericDialog();
                        dialog.addActions(

                                Map.entry(new Button("确定"), event -> {
                                    dialog.close();
                                })
                        );

                        ListView<LocalDateTime> listView = new ListView(FXCollections.observableArrayList(r.getCheckedData()));
                        listView.setCellFactory(cell -> new ListCell<>() {
                            @Override
                            protected void updateItem(LocalDateTime localDateTime, boolean b) {
                                super.updateItem(localDateTime, b);
                                if (b) {
                                    setText(null);
                                    setGraphic(null);
                                } else {
                                    Label numLabel = new Label("第" + (getIndex() + 1) + "次");
                                    numLabel.getStyleClass().addAll("accent", "title-4");
                                    FontIcon fontIcon = new FontIcon();
                                    fontIcon.setIconLiteral("mdal-label");
                                    numLabel.setGraphic(fontIcon);
                                    Label timeLabel = new Label(DateUtil.format(getItem(), "yyyy-MM-dd HH:mm:ss"));
                                    timeLabel.getStyleClass().addAll("text-caption", "text-muted");
                                    VBox vBox = new VBox(10, numLabel, timeLabel);
                                    HBox box = new HBox(vBox);

                                    box.setPadding(new Insets(10));
                                    setGraphic(box);
                                }
                            }
                        });
                        Label label = new Label("后续执行时间");
                        label.setAlignment(Pos.TOP_RIGHT);
                        label.setPrefWidth(100);
                        HBox.setHgrow(listView, Priority.ALWAYS);
                        HBox netTiemBox = new HBox(7, label, listView);
                        netTiemBox.setAlignment(Pos.TOP_CENTER);
                        VBox.setVgrow(netTiemBox, Priority.ALWAYS);
                        VBox vBox = new VBox(10, createTextField("任务编号", respVo.getId() + "")
                                , createTextField("任务名称", respVo.getName() + "")
                                , createDictBut("任务状态", respVo.getStatus(), INFRA_JOB_STATUS)
                                , createTextField("处理器的名字", respVo.getHandlerName())
                                , createTextField("处理器的参数", respVo.getHandlerParam())
                                , createTextField("Cron 表达式", respVo.getCronExpression())
                                , createTextField("重试次数", respVo.getRetryCount() + "")
                                , createTextField("重试间隔", respVo.getRetryInterval() + "毫秒")
                                , createTextField("监控超时时间", respVo.getMonitorTimeout() > 0 ? respVo.getMonitorTimeout() + "毫秒" : "未开启")
                                , netTiemBox

                        );

                        vBox.setPrefWidth(500);
                        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
                        dialog.setHeaderText("详情");
                        dialog.setContent(vBox);
                        dialog.show(rootPane.getScene());
                    }
                }).onException(e -> e.printStackTrace())
                .run();


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
