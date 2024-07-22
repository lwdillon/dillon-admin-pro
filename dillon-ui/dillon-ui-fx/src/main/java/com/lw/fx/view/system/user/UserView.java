package com.lw.fx.view.system.user;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.dlsc.gemsfx.DialogPane;
import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangePicker;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserUpdatePasswordReqVO;
import com.lw.fx.request.Request;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.TreeItemPredicate;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.system.UserFeign;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.binding.Bindings;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;

public class UserView implements FxmlView<UserViewModel>, Initializable {

    @InjectViewModel
    private UserViewModel viewModel;
    @FXML
    private Button addBut;

    @FXML
    private HBox contentPane;

    @FXML
    private TableColumn<UserRespVO, LocalDateTime> createTimeCol;

    @FXML
    private DateRangePicker createTimePicker;

    @FXML
    private TableColumn<?, ?> deptCol;

    @FXML
    private TextField deptNameField;

    @FXML
    private TableColumn<UserRespVO, Long> idCol;

    @FXML
    private TextField mobileField;

    @FXML
    private TableColumn<UserRespVO, String> nickNameCol;

    @FXML
    private TableColumn<UserRespVO, Long> optCol;

    @FXML
    private TableColumn<UserRespVO, String> phonenumberCol;

    @FXML
    private Button resetBut;

    @FXML
    private VBox rightPane;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private CheckBox selAllCheckBox;


    @FXML
    private TableColumn<UserRespVO, Integer> statusCol;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private TableView<UserRespVO> tableView;

    @FXML
    private TreeView<DeptSimpleRespVO> treeView;

    @FXML
    private TableColumn<UserRespVO, String> userNameCol;

    @FXML
    private TextField usernameField;

    private PagingControl pagingControl;

    private com.dlsc.gemsfx.DialogPane dialogPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dialogPane = new DialogPane();
        rootPane.getChildren().add(dialogPane);
        pagingControl = new PagingControl();
        rightPane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bind(viewModel.totalProperty());
        viewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        viewModel.pageSizeProperty().bind(pagingControl.pageSizeProperty());
        pagingControl.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.loadTableData();
        });

        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.loadTableData();
        });
        createTimePicker.setValue(new DateRange("创建时间", LocalDate.MIN));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("deptName"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nickNameCol.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        phonenumberCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        createTimeCol.setCellFactory(new Callback<TableColumn<UserRespVO, LocalDateTime>, TableCell<UserRespVO, LocalDateTime>>() {
            @Override
            public TableCell<UserRespVO, LocalDateTime> call(TableColumn<UserRespVO, LocalDateTime> param) {
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
                        if (item!=null&&item==0) {
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
        optCol.setCellFactory(new Callback<TableColumn<UserRespVO, Long>, TableCell<UserRespVO, Long>>() {
            @Override
            public TableCell<UserRespVO, Long> call(TableColumn<UserRespVO, Long> param) {

                TableCell cell = new TableCell<UserRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Button editBut = new Button("修改");
                            editBut.setOnAction(event -> showUserFormView(getTableRow().getItem().getId()));
                            editBut.setGraphic(FontIcon.of(Feather.EDIT));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button remBut = new Button("删除");
                            remBut.setOnAction(actionEvent -> showDelDialog(getTableRow().getItem()));
                            remBut.setGraphic(FontIcon.of(Feather.TRASH));
                            remBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button restPwdBut = new Button("重置密码");
                            restPwdBut.setOnAction(actionEvent -> showPwdDialog(getTableRow().getItem()));
                            restPwdBut.setGraphic(FontIcon.of(Feather.TRASH));
                            restPwdBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button roleBut = new Button("分配角色");
                            roleBut.setOnAction(actionEvent -> showRoleDialog(getTableRow().getItem()));
                            roleBut.setGraphic(FontIcon.of(Feather.TRASH));
                            roleBut.getStyleClass().addAll(FLAT, ACCENT);
                            HBox box = new HBox(editBut, remBut, restPwdBut, roleBut);
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
        treeView.setCellFactory(cell -> new TreeCell<>() {
            @Override
            protected void updateItem(DeptSimpleRespVO deptSimpleRespVO, boolean empty) {
                super.updateItem(deptSimpleRespVO, empty);
                if (deptSimpleRespVO == null || empty) {
                    setText(null);
                } else {
                    setText(deptSimpleRespVO.getName());
                }
            }
        });
        treeView.rootProperty().bind(viewModel.deptTreeRootProperty());
        treeView.getSelectionModel().selectedItemProperty().addListener((observableValue, deptSimpleRespVOTreeItem, t1) -> {
            viewModel.selectDeptProperty().set(t1.getValue());
            viewModel.loadTableData();
        });
        viewModel.deptTreeRootProperty().addListener((observableValue, deptSimpleRespVOFilterableTreeItem, t1) -> {
            if (t1 != null) {
                t1.predicateProperty().bind(Bindings.createObjectBinding(() -> {
                    if (deptNameField.getText() == null || deptNameField.getText().isEmpty()) {
                        return null;
                    }
                    return TreeItemPredicate.create(actor -> {
                        if (StrUtil.contains(PinyinUtil.getFirstLetter(actor.getName(), ""), PinyinUtil.getFirstLetter(deptNameField.getText(), ""))) {
                            return true;
                        } else {
                            return false;
                        }
                    });
                }, deptNameField.textProperty()));
            }
        });
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        mobileField.textProperty().bindBidirectional(viewModel.mobileProperty());

        statusComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> viewModel.statusProperty().set(t1.intValue()));

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            usernameField.setText(null);
            mobileField.setText(null);
            statusComboBox.getSelectionModel().select(null);
            createTimePicker.setValue(new DateRange("创建时间", LocalDate.MIN));
        });
        createTimePicker.valueProperty().bindBidirectional(viewModel.dateRangeProperty());

        addBut.setOnAction(actionEvent -> showUserFormView(null));
    }


    /**
     * 显示编辑对话框
     */
    private void showUserFormView(Long userId) {
        WFXGenericDialog dialog = new WFXGenericDialog();

        boolean isAdd = (userId == null);
        ViewTuple<UserFormView, UserFormViewModel> load = FluentViewLoader.fxmlView(UserFormView.class).load();
        load.getViewModel().createProperty().set(isAdd);
        load.getViewModel().query(userId);

        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return load.getViewModel().saveUser(isAdd);
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    MvvmFX.getNotificationCenter().publish("message", "保存成功", MessageType.SUCCESS);

                                    dialog.close();
                                    viewModel.loadTableData();

                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText(userId != null ? "编辑菜单" : "添加菜单");
        dialog.setContent(load.getView());
        dialog.show(rootPane.getScene());

    }

    private void showRoleDialog(UserRespVO respVO) {

        ViewTuple<UserAssignRoleFormView, UserAssignRoleFormViewModel> load = FluentViewLoader.fxmlView(UserAssignRoleFormView.class).load();
        load.getViewModel().listUserRoles(respVO);
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("分配角色");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return load.getViewModel().assignUserRole();
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    MvvmFX.getNotificationCenter().publish("message", "操作成功", MessageType.SUCCESS);

                                    dialog.close();
                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setContent(load.getView());
        dialog.show(rootPane.getScene());
    }

    private void showPwdDialog(UserRespVO respVO) {
        Label label = new Label("请输入[" + respVO.getUsername() + "]的新密码");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(340);
        VBox vBox = new VBox(label, passwordField);
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("重置密码");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    UserUpdatePasswordReqVO passwordReqVO = new UserUpdatePasswordReqVO();
                    passwordReqVO.setId(respVO.getId());
                    passwordReqVO.setPassword(passwordField.getText());
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return viewModel.updateUserPassword(passwordReqVO);
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    MvvmFX.getNotificationCenter().publish("message", "操作成功", MessageType.SUCCESS);
                                    dialog.close();
                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setContent(vBox);
        dialog.show(rootPane.getScene());
    }

    private void showDelDialog(UserRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("删除用户");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(UserFeign.class).deleteUser(respVO.getId());
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

        dialog.setContent(new Label("是否确认删除名称为" + respVO.getUsername() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }
}
