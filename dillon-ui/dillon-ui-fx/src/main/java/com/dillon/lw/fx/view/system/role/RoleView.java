package com.dillon.lw.fx.view.system.role;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.PagingControl;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
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

public class RoleView extends BaseView<RoleViewModel> implements Initializable {

    @FXML
    private Button addBut;

    @FXML
    private TableColumn<RoleRespVO, String> codeCol;

    @FXML
    private TextField codeField;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<RoleRespVO, LocalDateTime> createTimeCol;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<RoleRespVO, Long> idCol;

    @FXML
    private TableColumn<RoleRespVO, String> nameCol;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<RoleRespVO, Long> optCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private TableColumn<RoleRespVO, Integer> sortCol;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<RoleRespVO, Integer> statusCol;

    @FXML
    private ComboBox<?> statusCombo;

    @FXML
    private TableView<RoleRespVO> tableView;

    @FXML
    private TableColumn<RoleRespVO, Integer> typeCol;

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
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER");

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeCol.setStyle("-fx-alignment: CENTER");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle("-fx-alignment: CENTER");

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER");

        sortCol.setCellValueFactory(new PropertyValueFactory<>("sort"));
        sortCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellFactory(new Callback<TableColumn<RoleRespVO, LocalDateTime>, TableCell<RoleRespVO, LocalDateTime>>() {
            @Override
            public TableCell<RoleRespVO, LocalDateTime> call(TableColumn<RoleRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<RoleRespVO, Long>, TableCell<RoleRespVO, Long>>() {
            @Override
            public TableCell<RoleRespVO, Long> call(TableColumn<RoleRespVO, Long> param) {

                TableCell cell = new TableCell<RoleRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Button editBut = new Button("修改");
                            editBut.setOnAction(event -> showRoleFormView(getTableRow().getItem().getId()));
                            editBut.setGraphic(FontIcon.of(Feather.EDIT));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button menuBut = new Button("菜单权限");
                            menuBut.setOnAction(actionEvent -> showRoleAssignMenuForm(getTableRow().getItem()));
                            menuBut.setGraphic(FontIcon.of(Feather.TRASH));
                            menuBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button dataBut = new Button("数据权限");
                            dataBut.setOnAction(actionEvent -> showRoleDataPermissionForm(getTableRow().getItem()));
                            dataBut.setGraphic(FontIcon.of(Feather.TRASH));
                            dataBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button delBut = new Button("删除");
                            delBut.setOnAction(actionEvent -> showDelDialog(getTableRow().getItem()));
                            delBut.setGraphic(FontIcon.of(Feather.TRASH));
                            delBut.getStyleClass().addAll(FLAT, DANGER);
                            HBox box = new HBox(editBut, menuBut, dataBut, delBut);
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

        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        codeField.textProperty().bindBidirectional(viewModel.codeProperty());

        statusCombo.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> viewModel.statusProperty().set(t1.intValue()));

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            nameField.setText(null);
            codeField.setText(null);
            statusCombo.getSelectionModel().select(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

        addBut.setOnAction(actionEvent -> showRoleFormView(null));

    }


    /**
     * 显示编辑对话框
     */
    private void showRoleFormView(Long id) {

        boolean isAdd = (id == null);

        RoleFormView view = ViewLoader.load(RoleFormView.class);
        view.getViewModel().query(id);

        new ConfirmDialog.Builder(modalPane)
                .title(isAdd ? "编辑用户" : "添加用户")
                .content(view.getNode())
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    if (isAdd) {
                        view.getViewModel().addRole(d);
                    } else {
                        view.getViewModel().updateRole(d);
                    }
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }

    private void showRoleAssignMenuForm(RoleRespVO respVO) {
        RoleAssignMenuFormView view = ViewLoader.load(RoleAssignMenuFormView.class);
        view.getViewModel().getSimpleMenuList(respVO);

        new ConfirmDialog.Builder(modalPane)
                .title("菜单权限")
                .content(view.getNode())
                .width(450)
                .height(650)
                .onConfirm(d -> {
                   view.getViewModel().assignRoleMenu(d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }


    private void showRoleDataPermissionForm(RoleRespVO respVO) {


        RoleDataPermissionFormView view = ViewLoader.load(RoleDataPermissionFormView.class);
        view.getViewModel().getSimpleDeptList(respVO);

        new ConfirmDialog.Builder(modalPane)
                .title("数据权限")
                .content(view.getNode())
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    view.getViewModel().assignRoleDataScope(d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();


    }


    private void showDelDialog(RoleRespVO respVO) {


        new ConfirmDialog.Builder(modalPane)
                .title("删除角色")
                .message("是否确认删除名称为【" + respVO.getName() + "】吗？")
                .width(400)
                .height(100)
                .onConfirm(d -> {
                    viewModel.delRole(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }
}
