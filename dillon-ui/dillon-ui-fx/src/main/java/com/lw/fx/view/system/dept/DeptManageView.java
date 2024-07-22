package com.lw.fx.view.system.dept;

import atlantafx.base.controls.RingProgressIndicator;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.WFXGenericDialog;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
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
import java.util.Map;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;

/**
 * 菜单管理视图
 *
 * @author wenli
 * @date 2023/02/15
 */
public class DeptManageView implements FxmlView<DeptManageViewModel>, Initializable {

    @InjectViewModel
    private DeptManageViewModel viewModel;



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
    private TreeTableColumn<DeptRespVO, String> iconCol;
    @FXML
    private TreeTableColumn<DeptRespVO, String> sortCol;
    @FXML
    private TreeTableColumn<DeptRespVO, String> authCol;
    @FXML
    private TreeTableColumn<DeptRespVO, String> comPathCol;
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


    /**
     * 初始化
     *
     * @param url            url
     * @param resourceBundle 资源包
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


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
        iconCol.setStyle("-fx-alignment: CENTER");
        iconCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("icon"));
        iconCol.setCellFactory(new Callback<TreeTableColumn<DeptRespVO, String>, TreeTableCell<DeptRespVO, String>>() {
            @Override
            public TreeTableCell<DeptRespVO, String> call(TreeTableColumn<DeptRespVO, String> param) {
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
                            label.setGraphic(FontIcon.of(Feather.INFO));
                        }

                        setGraphic(label);
                    }
                };
            }
        });
        sortCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("sort"));
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
     * @param isEdit  是编辑
     */
    private void showEditDialog(DeptRespVO deptRespVo, boolean isEdit) {

        WFXGenericDialog dialog = new WFXGenericDialog();

        ViewTuple<DeptFromView, DeptFromViewModel> load = FluentViewLoader.fxmlView(DeptFromView.class).load();
        DeptRespVO deptRespVO=null;
        if (isEdit) {
            deptRespVO = deptRespVo;
        }else {
            deptRespVO = new DeptRespVO();
            deptRespVO.setParentId(deptRespVo.getId());
        }
        load.getViewModel().updateData(deptRespVO,true);
        dialog.clearActions();
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> load.getViewModel().save(isEdit))
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    dialog.close();
                                    MvvmFX.getNotificationCenter().publish("message", "保存成功", MessageType.SUCCESS);
                                    query();
                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );


        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText(isEdit ? "编辑菜单" : "添加菜单");
        dialog.setContent(load.getView());
        dialog.show(root.getScene());


    }



    /**
     * 显示del对话框
     *
     * @param deptRespVo 系统菜单
     */
    private void showDelDialog(DeptRespVO deptRespVo) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.clearActions();
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addRunnableInExecutor(() -> viewModel.remove(deptRespVo.getId()))
                            .addRunnableInPlatformThread(() -> {
                                dialog.close();
                                MvvmFX.getNotificationCenter().publish("message", "保存成功", MessageType.SUCCESS);
                                query();
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );


        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("系统揭示");
        dialog.setContent(new Label("是否确认删除名称为" + deptRespVo.getName() + "的数据项？"));
        dialog.show(root.getScene());
    }

    /**
     * 查询
     */
    private void query() {
        viewModel.query();
    }






}
