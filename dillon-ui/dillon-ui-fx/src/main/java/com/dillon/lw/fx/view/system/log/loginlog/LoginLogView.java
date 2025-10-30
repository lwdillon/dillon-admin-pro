package com.dillon.lw.fx.view.system.log.loginlog;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.store.AppStore;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.PagingControl;
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
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;
import static com.dillon.lw.utils.DictTypeEnum.SYSTEM_LOGIN_RESULT;
import static com.dillon.lw.utils.DictTypeEnum.SYSTEM_LOGIN_TYPE;

public class LoginLogView extends BaseView<LoginLogViewModel> implements Initializable {

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
    private void showFormView(LoginLogRespVO loginLogRespVO) {


        LoginLogDetailView view = ViewLoader.load(LoginLogDetailView.class);
        view.getViewModel().query(loginLogRespVO);
        new ConfirmDialog.Builder(modalPane)
                .title("详情")
                .content(view.getNode())
                .width(750)
                .height(450)
                .onConfirm(d -> {
                    d.close();
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();


    }


    private void showDelDialog(LoginLogRespVO respVO) {


        new ConfirmDialog.Builder(modalPane)
                .title("删除登录日志")
                .message("确定删除日志【" + respVO.getUsername() + "-" + respVO.getLogType() + "】吗？")
                .width(400)
                .height(100)
                .onConfirm(d -> {
                    viewModel.deleteLoginLog(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }
}
