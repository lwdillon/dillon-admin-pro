package com.dillon.lw.fx.view.infra.job;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.module.infra.controller.admin.job.vo.log.JobLogRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.utils.DictTypeEnum;
import com.dillon.lw.fx.mvvm.base.BaseView;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;
import static com.dillon.lw.utils.DictTypeEnum.INFRA_JOB_LOG_STATUS;
import static com.dillon.lw.utils.DictTypeEnum.INFRA_JOB_STATUS;

public class JobLogView extends BaseView<JobLogViewModel> implements Initializable {

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<JobLogRespVO, Integer> durationCol;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<JobLogRespVO, ?> executeIndexCol;

    @FXML
    private TableColumn<JobLogRespVO, Long> executeTimeCol;

    @FXML
    private TextField handlerNameField;

    @FXML
    private TableColumn<JobLogRespVO, ?> handlerNamecol;

    @FXML
    private TableColumn<JobLogRespVO, ?> handlerParamCol;

    @FXML
    private TableColumn<JobLogRespVO, ?> idCol;


    @FXML
    private TableColumn<JobLogRespVO, ?> jobIdCol;

    @FXML
    private TableColumn<JobLogRespVO, Long> optCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<JobLogRespVO, Integer> statusCol;

    @FXML
    private ComboBox<DictDataSimpleRespVO> statusComboBox;

    @FXML
    private TableView<JobLogRespVO> tableView;


    private PagingControl pagingControl;

    private ModalPane modalPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rootPane.getChildren().add(modalPane = new ModalPane());
        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);

        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());
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
        statusComboBox.setCellFactory(new Callback<ListView<DictDataSimpleRespVO>, ListCell<DictDataSimpleRespVO>>() {
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
        statusComboBox.setButtonCell(new ListCell<>() {
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
        handlerNameField.textProperty().bindBidirectional(viewModel.handlerNameProperty());


        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");

        jobIdCol.setCellValueFactory(new PropertyValueFactory<>("jobId"));
        jobIdCol.setStyle("-fx-alignment: CENTER");

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER");
        statusCol.setCellFactory(new Callback<TableColumn<JobLogRespVO, Integer>, TableCell<JobLogRespVO, Integer>>() {
            @Override
            public TableCell<JobLogRespVO, Integer> call(TableColumn<JobLogRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(INFRA_JOB_LOG_STATUS).get(item + "");
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

        handlerNamecol.setCellValueFactory(new PropertyValueFactory<>("handlerName"));
        handlerNamecol.setStyle("-fx-alignment: CENTER");


        handlerParamCol.setCellValueFactory(new PropertyValueFactory<>("handlerParam"));
        handlerParamCol.setStyle("-fx-alignment: CENTER");


        executeIndexCol.setCellValueFactory(new PropertyValueFactory<>("executeIndex"));
        executeIndexCol.setStyle("-fx-alignment: CENTER");

        executeTimeCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        executeTimeCol.setStyle("-fx-alignment: CENTER");
        executeTimeCol.setCellFactory(new Callback<TableColumn<JobLogRespVO, Long>, TableCell<JobLogRespVO, Long>>() {
            @Override
            public TableCell<JobLogRespVO, Long> call(TableColumn<JobLogRespVO, Long> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if (item != null) {
                                JobLogRespVO respVO = getTableRow().getItem();
                                this.setText(DateUtil.format(respVO.getBeginTime(), "yyyy-MM-dd HH:mm:ss") + " ~ " + DateUtil.format(respVO.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
                            }
                        }

                    }
                };
            }
        });

        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationCol.setStyle("-fx-alignment: CENTER");
        durationCol.setCellFactory(new Callback<TableColumn<JobLogRespVO, Integer>, TableCell<JobLogRespVO, Integer>>() {
            @Override
            public TableCell<JobLogRespVO, Integer> call(TableColumn<JobLogRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if (item != null) {
                                this.setText(item + " 毫秒");
                            }
                        }

                    }
                };
            }
        });


        optCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        optCol.setCellFactory(new Callback<TableColumn<JobLogRespVO, Long>, TableCell<JobLogRespVO, Long>>() {
            @Override
            public TableCell<JobLogRespVO, Long> call(TableColumn<JobLogRespVO, Long> param) {

                TableCell cell = new TableCell<JobLogRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Button openDetailBut = new Button("详细");
                            openDetailBut.setOnAction(event -> showDetailView(getTableRow().getItem()));
                            openDetailBut.setGraphic(FontIcon.of(Feather.EDIT));
                            openDetailBut.getStyleClass().addAll(FLAT, ACCENT);

                            HBox box = new HBox(openDetailBut);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        });

        tableView.setItems(viewModel.getTableItems());

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            jobIdCol.setText(null);
            statusComboBox.getSelectionModel().select(null);
            handlerNameField.setText(null);
            endDatePicker.setValue(null);
            startDatePicker.setValue(null);
            viewModel.loadTableData();

        });
    }


    /**
     * 显示编辑对话框
     */
    private void showDetailView(JobLogRespVO respVo) {


        VBox vBox = new VBox(10, createTextField("日志编号", respVo.getId() + "")
                , createTextField("任务编号", respVo.getJobId() + "")
                , createTextField("处理器的名字", respVo.getHandlerName())
                , createTextField("处理器的参数", respVo.getHandlerParam())
                , createTextField("第几次执行", respVo.getExecuteIndex() + "")
                , createTextField("执行时间", DateUtil.format(respVo.getBeginTime(), "yyyy-MM-dd HH:mm:ss") + " ~ " + DateUtil.format(respVo.getEndTime(), "yyyy-MM-dd HH:mm:ss"))
                , createTextField("执行时长", respVo.getDuration() + "毫秒")
                , createDictBut("任务状态", respVo.getStatus(), INFRA_JOB_LOG_STATUS)
                , createTextField("执行结果", respVo.getResult())

        );

        new ConfirmDialog.Builder(modalPane)
                .title("详情")
                .content(vBox)
                .width(450)
                .height(150)
                .onConfirm(d -> {
                    d.close();
                })
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
}
