package com.dillon.lw.fx.view.system.user;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserUpdatePasswordReqVO;
import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangePicker;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.PagingControl;
import com.dillon.lw.fx.view.layout.TreeItemPredicate;
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
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;

public class UserView extends BaseView<UserViewModel> implements Initializable {

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


    private ModalPane modalPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modalPane = new ModalPane();
        rootPane.getChildren().add(modalPane);
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

        boolean isAdd = (userId == null);

        UserFormView view = ViewLoader.load(UserFormView.class);
        view.getViewModel().query(userId);
        new ConfirmDialog.Builder(modalPane)
                .title(isAdd ? "编辑用户" : "添加用户")
                .content(view.getNode())
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    if (isAdd) {
                        view.getViewModel().addUser(d);
                    } else {
                        view.getViewModel().updateUser(d);
                    }
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }

    private void showRoleDialog(UserRespVO respVO) {

        UserAssignRoleFormView view = ViewLoader.load(UserAssignRoleFormView.class);
        view.getViewModel().listUserRoles(respVO);
        new ConfirmDialog.Builder(modalPane)
                .title("分配角色")
                .content(view.getNode())
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    view.getViewModel().assignUserRole(d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }

    private void showPwdDialog(UserRespVO respVO) {
        Label label = new Label("请输入[" + respVO.getUsername() + "]的新密码");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(340);
        VBox vBox = new VBox(label, passwordField);

        new ConfirmDialog.Builder(modalPane)
                .title("重置密码")
                .content(vBox)
                .width(450)
                .height(150)
                .onConfirm(d -> {
                    UserUpdatePasswordReqVO passwordReqVO = new UserUpdatePasswordReqVO();
                    passwordReqVO.setId(respVO.getId());
                    passwordReqVO.setPassword(passwordField.getText());
                    viewModel.updateUserPassword(passwordReqVO, d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }

    private void showDelDialog(UserRespVO respVO) {

        new ConfirmDialog.Builder(modalPane)
                .title("删除用户")
                .message("是否确认删除名称为【" + respVO.getUsername() + "】吗？")
                .width(400)
                .height(100)
                .onConfirm(d -> {
                    viewModel.delUser(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }
}
