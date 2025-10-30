package com.dillon.lw.fx.view.system.dept;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.RingProgressIndicator;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

/**
 * 菜单管理视图
 *
 * @author wenli
 * @date 2023/02/15
 */
public class DeptManageView extends BaseView<DeptManageViewModel> implements Initializable {


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
    private TreeTableView<DeptRespVO> treeTableView;
    @FXML
    private TreeTableColumn<DeptRespVO, String> nameCol;
    @FXML
    private TreeTableColumn<DeptRespVO, Long> leaderUserCol;
    @FXML
    private TreeTableColumn<DeptRespVO, String> sortCol;
    @FXML
    private TreeTableColumn<DeptRespVO, Integer> stateCol;
    @FXML
    private TreeTableColumn<DeptRespVO, LocalDateTime> createTime;
    @FXML
    private TreeTableColumn<DeptRespVO, String> optCol;

    @FXML
    private Button searchBut;
    @FXML
    private ToggleButton expansionBut;

    private ModalPane modalPane;

    /**
     * 初始化
     *
     * @param url            url
     * @param resourceBundle 资源包
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        root.getChildren().add(modalPane=new ModalPane());
        load = new RingProgressIndicator();
        load.disableProperty().bind(load.visibleProperty().not());
        load.visibleProperty().bindBidirectional(content.disableProperty());
        root.getChildren().add(load);
        treeTableView.setShowRoot(false);
        treeTableView.rootProperty().bind(viewModel.treeItemObjectPropertyProperty());
        addBut.setOnAction(event -> showEditDialog(new DeptRespVO(), false));
        addBut.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
        searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
        statusCombo.valueProperty().bindBidirectional(viewModel.statusTextProperty());
        searchBut.setOnAction(event -> query());
        searchBut.getStyleClass().addAll(ACCENT);

        restBut.setOnAction(event -> {
            viewModel.rest();
            query();
        });
        expansionBut.selectedProperty().addListener((observable, oldValue, newValue) -> treeExpandedAll(treeTableView.getRoot(), newValue));
        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        leaderUserCol.setStyle("-fx-alignment: CENTER");
        leaderUserCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("leaderUserId"));
        leaderUserCol.setCellFactory(new Callback<TreeTableColumn<DeptRespVO, Long>, TreeTableCell<DeptRespVO, Long>>() {
            @Override
            public TreeTableCell<DeptRespVO, Long> call(TreeTableColumn<DeptRespVO, Long> param) {
                return new TreeTableCell<>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item==null) {
                            setGraphic(null);
                            return;
                        }
                        Label label = new Label();
                        UserSimpleRespVO userSimpleRespVO= viewModel.getUserMap().get(item);
                        if (userSimpleRespVO !=null) {
                            label.setText(userSimpleRespVO.getNickname());
                        }



                        setGraphic(label);
                    }
                };
            }
        });
        sortCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("sort"));
        sortCol.setStyle("-fx-alignment: CENTER");
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
        createTime.setCellValueFactory(new TreeItemPropertyValueFactory<>("createTime"));
        createTime.setStyle("-fx-alignment: CENTER");
        createTime.setCellFactory(new Callback<TreeTableColumn<DeptRespVO, LocalDateTime>, TreeTableCell<DeptRespVO, LocalDateTime>>() {
            @Override
            public TreeTableCell<DeptRespVO, LocalDateTime> call(TreeTableColumn<DeptRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TreeTableColumn<DeptRespVO, String>, TreeTableCell<DeptRespVO, String>>() {
            @Override
            public TreeTableCell<DeptRespVO, String> call(TreeTableColumn<DeptRespVO, String> param) {

                TreeTableCell cell = new TreeTableCell<DeptRespVO, String>() {
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

        toggleStyleClass(treeTableView, Tweaks.ALT_ICON);
        query();

    }


    /**
     * 树扩展所有
     *
     * @param root     根
     * @param expanded 扩大
     */
    private void treeExpandedAll(TreeItem<DeptRespVO> root, boolean expanded) {
        for (TreeItem<DeptRespVO> child : root.getChildren()) {
            child.setExpanded(expanded);
            if (!child.getChildren().isEmpty()) {
                treeExpandedAll(child, expanded);
            }
        }
    }


    /**
     * 显示编辑对话框
     *
     * @param deptRespVo 系统菜单
     * @param isEdit     是编辑
     */
    private void showEditDialog(DeptRespVO deptRespVo, boolean isEdit) {

        DeptRespVO deptRespVO = null;
        if (isEdit) {
            deptRespVO = deptRespVo;
        } else {
            deptRespVO = new DeptRespVO();
            deptRespVO.setParentId(deptRespVo.getId());
        }


        DeptFromView view = ViewLoader.load(DeptFromView.class);
        view.getViewModel().initData(deptRespVo);
        new ConfirmDialog.Builder(modalPane)
                .title(isEdit ? "编辑部门" : "添加部门")
                .content(view.getNode())
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    if (isEdit) {
                        view.getViewModel().updateDept(d);
                    } else {
                        view.getViewModel().addDept(d);
                    }
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();


    }


    /**
     * 显示del对话框
     */
    private void showDelDialog(DeptRespVO respVO) {

        new ConfirmDialog.Builder(modalPane)
                .title("删除部门")
                .message("确定删除部门【" + respVO.getName() + "】吗？")
                .width(400)
                .height(100)
                .onConfirm(d -> {
                    viewModel.deleteDept(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }

    /**
     * 查询
     */
    private void query() {
        viewModel.query();
    }


}
