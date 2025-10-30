package com.dillon.lw.fx.view.system.menu;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.RingProgressIndicator;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
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
import static com.dillon.lw.fx.utils.NodeUtils.getIcon;

/**
 * 菜单管理视图
 *
 * @author wenli
 * @date 2023/02/15
 */
public class MenuManageView extends BaseView<MenuManageViewModel> {

    @FXML
    private VBox content;

    @FXML
    private StackPane root;
    private RingProgressIndicator load;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusCombo;
    @FXML
    private Button addBut;
    @FXML
    private Button restBut;
    @FXML
    private TreeTableView<MenuRespVO> treeTableView;
    @FXML
    private TreeTableColumn<MenuRespVO, String> nameCol;
    @FXML
    private TreeTableColumn<MenuRespVO, String> iconCol;
    @FXML
    private TreeTableColumn<MenuRespVO, String> sortCol;
    @FXML
    private TreeTableColumn<MenuRespVO, String> authCol;
    @FXML
    private TreeTableColumn<MenuRespVO, String> comPathCol;
    @FXML
    private TreeTableColumn<MenuRespVO, Integer> stateCol;
    @FXML
    private TreeTableColumn<MenuRespVO, Integer> typeCol;
    @FXML
    private TreeTableColumn<MenuRespVO, LocalDateTime> createTime;
    @FXML
    private TreeTableColumn<MenuRespVO, String> optCol;

    @FXML
    private Button searchBut;
    @FXML
    private ToggleButton expansionBut;

    @FXML
    private ModalPane modalPane;

    /**
     * 初始化
     *
     * @param url            url
     * @param resourceBundle 资源包
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initView();
        initColumns();
        initBindings();
        initEventListeners();
        initData();
    }

    private void initView() {
        load = new RingProgressIndicator();
        root.getChildren().add(load);
        treeTableView.setShowRoot(false);
        addBut.getStyleClass().addAll(ROUNDED, ACCENT);
        searchBut.getStyleClass().addAll(ROUNDED);
        restBut.getStyleClass().addAll(ROUNDED);
        toggleStyleClass(treeTableView, Tweaks.ALT_ICON);
        expansionBut.getStyleClass().addAll(ROUNDED);


    }

    private void initBindings() {
        load.disableProperty().bind(load.visibleProperty().not());
        load.visibleProperty().bindBidirectional(content.disableProperty());
        treeTableView.rootProperty().bind(viewModel.treeItemObjectPropertyProperty());
        searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
        statusCombo.valueProperty().bindBidirectional(viewModel.statusTextProperty());

    }

    private void initEventListeners() {
        addBut.setOnAction(event -> showEditDialog(new MenuRespVO(), false));
        searchBut.setOnAction(event -> viewModel.query());
        restBut.setOnAction(event -> viewModel.rest());
        expansionBut.selectedProperty().addListener((observable, oldValue, newValue) -> treeExpandedAll(treeTableView.getRoot(), newValue));

    }

    private void initColumns() {
        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        iconCol.setStyle("-fx-alignment: CENTER");
        iconCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("icon"));
        iconCol.setCellFactory(new Callback<TreeTableColumn<MenuRespVO, String>, TreeTableCell<MenuRespVO, String>>() {
            @Override
            public TreeTableCell<MenuRespVO, String> call(TreeTableColumn<MenuRespVO, String> param) {
                return new TreeTableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (StrUtil.isEmpty(item)) {
                            setGraphic(null);
                            return;
                        }

                        Label label = new Label();
                        if (StrUtil.equals("#", item)) {
                            label.setText(item);
                        } else {
                            label.setGraphic(getIcon(item));
                        }

                        setGraphic(label);
                    }
                };
            }
        });

        typeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        typeCol.setStyle("-fx-alignment: CENTER");
        typeCol.setCellFactory(col -> {
            return new TreeTableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button state = new Button();
                        if (item != null) {
                            if (item == 1) {
                                state.setText("目录");
                                state.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
                            } else if (item == 2) {
                                state.setText("菜单");
                                state.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
                            } else if (item == 3) {
                                state.setText("按钮");
                                state.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
                            } else {
                                state.setText("未知");
                                state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                            }
                        } else {
                            state.setText("未知");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                            state.setText("正常");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                        }
                        HBox box = new HBox(state);
                        box.setPadding(new Insets(7, 7, 7, 7));
                        box.setAlignment(Pos.CENTER);
                        setGraphic(box);
                    }
                }
            };
        });
        sortCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        sortCol.setStyle("-fx-alignment: CENTER");
        authCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("permission"));
        authCol.setStyle("-fx-alignment: CENTER");
        comPathCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("path"));
        comPathCol.setStyle("-fx-alignment: CENTER");
        stateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("status"));
        stateCol.setStyle("-fx-alignment: CENTER");
        stateCol.setCellFactory(col -> {
            return new TreeTableCell<>() {
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
                            state.setText("停用");
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
        createTime.setCellValueFactory(new TreeItemPropertyValueFactory<>("createTime"));
        createTime.setStyle("-fx-alignment: CENTER");
        createTime.setCellFactory(new Callback<TreeTableColumn<MenuRespVO, LocalDateTime>, TreeTableCell<MenuRespVO, LocalDateTime>>() {
            @Override
            public TreeTableCell<MenuRespVO, LocalDateTime> call(TreeTableColumn<MenuRespVO, LocalDateTime> param) {
                return new TreeTableCell<>() {
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
        optCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        optCol.setCellFactory(new Callback<TreeTableColumn<MenuRespVO, String>, TreeTableCell<MenuRespVO, String>>() {
            @Override
            public TreeTableCell<MenuRespVO, String> call(TreeTableColumn<MenuRespVO, String> param) {

                TreeTableCell cell = new TreeTableCell<MenuRespVO, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Button addBut = new Button("新增");
                            addBut.setOnAction(event -> showEditDialog(getTableRow().getItem(), false));
                            addBut.setGraphic(FontIcon.of(Feather.PLUS));
                            addBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button editBut = new Button("修改");
                            editBut.setOnAction(event -> showEditDialog(getTableRow().getItem(), true));
                            editBut.setGraphic(FontIcon.of(Feather.EDIT));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button remBut = new Button("删除");
                            remBut.setOnAction(event -> showDelDialog(getTableRow().getItem()));
                            remBut.setGraphic(FontIcon.of(Feather.TRASH));
                            remBut.getStyleClass().addAll(FLAT, ACCENT);
                            HBox box = new HBox(addBut, editBut, remBut);
                            box.setAlignment(Pos.CENTER);
//                            box.setSpacing(7);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        });
    }


    /**
     * 树扩展所有
     *
     * @param root     根
     * @param expanded 扩大
     */
    private void treeExpandedAll(TreeItem<MenuRespVO> root, boolean expanded) {
        for (TreeItem<MenuRespVO> child : root.getChildren()) {
            child.setExpanded(expanded);
            if (!child.getChildren().isEmpty()) {
                treeExpandedAll(child, expanded);
            }
        }
    }


    /**
     * 显示编辑对话框
     *
     * @param sysMenu 系统菜单
     * @param isEdit  是编辑
     */
    private void showEditDialog(MenuRespVO sysMenu, boolean isEdit) {


        MenuFromView load = ViewLoader.load(MenuFromView.class);
        MenuRespVO menuRespVO = null;
        if (isEdit) {
            menuRespVO = sysMenu;
        } else {
            menuRespVO = new MenuRespVO();
            menuRespVO.setParentId(sysMenu.getId());
            menuRespVO.setType(1);
        }
        load.getViewModel().updateData(menuRespVO);

        Node centNode = load.getNode();

        new ConfirmDialog.Builder(modalPane)
                .title(isEdit ? "编辑菜单" : "添加菜单")
                .content(centNode)
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    if (isEdit) {
                        load.getViewModel().updateMenu(d);
                    } else {
                        load.getViewModel().createMenu(d);
                    }
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }


    /**
     * 显示del对话框
     *
     * @param sysMenu 系统菜单
     */
    private void showDelDialog(MenuRespVO sysMenu) {


        new ConfirmDialog.Builder(modalPane)
                .title("删除菜单")
                .message("确定要删除菜单【 " + sysMenu.getName() + " 】吗?")
                .width(450)
                .height(150)
                .onConfirm(d -> {
                    viewModel.deleteMenu(sysMenu.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }

    private void initData() {
        viewModel.query();
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }
}
