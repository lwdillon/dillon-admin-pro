package com.lw.fx.view.system.loginlog;

import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.system.LoginLogFeign;
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
import static com.lw.ui.utils.DictTypeEnum.SYSTEM_LOGIN_RESULT;
import static com.lw.ui.utils.DictTypeEnum.SYSTEM_LOGIN_TYPE;

public class LoginLogView implements FxmlView<LoginLogViewModel>, Initializable {

    @InjectViewModel
    private LoginLogViewModel viewModel;
    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<LoginLogRespVO, LocalDateTime> createTimeCol;

    @FXML
    private Button emptyBut;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<LoginLogRespVO, Long> idCol;

    @FXML
    private TableColumn<LoginLogRespVO, Integer> logTypeCol;

    @FXML
    private TableColumn<LoginLogRespVO, Long> optCol;

    @FXML
    private Button resetBut;

    @FXML
    private TableColumn<LoginLogRespVO, Integer> resultCol;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableView<LoginLogRespVO> tableView;

    @FXML
    private TableColumn<LoginLogRespVO, String> userAgentCol;

    @FXML
    private TableColumn<LoginLogRespVO, String> userIpCol;

    @FXML
    private TextField userIpField;

    @FXML
    private TableColumn<LoginLogRespVO, String> usernameCol;

    @FXML
    private TextField usernameField;

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

        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setStyle("-fx-alignment: CENTER");

        logTypeCol.setCellValueFactory(new PropertyValueFactory<>("logType"));
        logTypeCol.setStyle("-fx-alignment: CENTER");
        logTypeCol.setCellFactory(new Callback<TableColumn<LoginLogRespVO, Integer>, TableCell<LoginLogRespVO, Integer>>() {
            @Override
            public TableCell<LoginLogRespVO, Integer> call(TableColumn<LoginLogRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(SYSTEM_LOGIN_TYPE).get(item + "");
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

        userIpCol.setCellValueFactory(new PropertyValueFactory<>("userIp"));
        userIpCol.setStyle("-fx-alignment: CENTER");

        userAgentCol.setCellValueFactory(new PropertyValueFactory<>("userAgent"));
        userAgentCol.setStyle("-fx-alignment: CENTER");

        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));
        resultCol.setStyle("-fx-alignment: CENTER");
        resultCol.setCellFactory(new Callback<TableColumn<LoginLogRespVO, Integer>, TableCell<LoginLogRespVO, Integer>>() {
            @Override
            public TableCell<LoginLogRespVO, Integer> call(TableColumn<LoginLogRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(SYSTEM_LOGIN_RESULT).get(item + "");
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
        createTimeCol.setCellFactory(new Callback<TableColumn<LoginLogRespVO, LocalDateTime>, TableCell<LoginLogRespVO, LocalDateTime>>() {
            @Override
            public TableCell<LoginLogRespVO, LocalDateTime> call(TableColumn<LoginLogRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<LoginLogRespVO, Long>, TableCell<LoginLogRespVO, Long>>() {
            @Override
            public TableCell<LoginLogRespVO, Long> call(TableColumn<LoginLogRespVO, Long> param) {

                TableCell cell = new TableCell<LoginLogRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            Button editBut = new Button("详情");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem()));
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

        usernameField.textProperty().bindBidirectional(viewModel.userNameProperty());
        userIpField.textProperty().bindBidirectional(viewModel.userIpProperty());


        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            usernameField.setText(null);
            userIpField.setText(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            viewModel.loadTableData();
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

    }


    /**
     * 显示编辑对话框
     */
    private void showFormView(LoginLogRespVO operateLogRespVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();

        ViewTuple<LoginLogDetailView, LoginLogDetailViewModel> load = FluentViewLoader.fxmlView(LoginLogDetailView.class).load();
        load.getViewModel().queryDictType(operateLogRespVO);
        dialog.addActions(

                Map.entry(new Button("确定"), event -> {
                    dialog.close();
                })
        );

        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("详情");
        dialog.setContent(load.getView());
        dialog.show(rootPane.getScene());

    }


    private void showDelDialog(LoginLogRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("删除");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(LoginLogFeign.class).deleteLoginLog(respVO.getId());
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

        dialog.setContent(new Label("是否确认删除名称为" + respVO.getUsername() + "-" + respVO.getLogType() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }
}
