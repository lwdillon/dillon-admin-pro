package com.dillon.lw.fx.view.system.role;

import atlantafx.base.controls.ToggleSwitch;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class RoleAssignMenuFormView extends BaseView<RoleAssignMenuFormViewModel> implements Initializable {

    @FXML
    private Button codeBut;

    @FXML
    private Button nameBut;

    @FXML
    private TreeView<MenuSimpleRespVO> treeView;

    @FXML
    private ToggleSwitch expandedBut;

    @FXML
    private ToggleSwitch selAllBut;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        treeView.setCellFactory(javafx.scene.control.cell.CheckBoxTreeCell.forTreeView());
        // 设置自定义 TreeCell 工厂，实现复选框和自定义文本
        treeView.setCellFactory(new Callback<TreeView<MenuSimpleRespVO>, TreeCell<MenuSimpleRespVO>>() {
            @Override
            public TreeCell<MenuSimpleRespVO> call(TreeView<MenuSimpleRespVO> param) {
                return new CheckBoxTreeCell<MenuSimpleRespVO>() {
                    @Override
                    public void updateItem(MenuSimpleRespVO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        });
        nameBut.textProperty().bindBidirectional(viewModel.nameProperty());
        codeBut.textProperty().bindBidirectional(viewModel.codeProperty());
        treeView.rootProperty().bind(viewModel.menuTreeRootProperty());

        selAllBut.selectedProperty().addListener((observableValue, aBoolean, newV) -> {
            viewModel.getMenuTreeRoot().setSelected(newV);
        });
        expandedBut.selectedProperty().addListener((observableValue, aBoolean, newV) -> {
            treeExpandedAll(treeView.getRoot(), newV);


        });

    }

    /**
     * 树扩展所有
     *
     * @param root     根
     * @param expanded 扩大
     */
    private void treeExpandedAll(TreeItem<MenuSimpleRespVO> root, boolean expanded) {
        for (TreeItem<MenuSimpleRespVO> child : root.getChildren()) {
            child.setExpanded(expanded);
            if (!child.getChildren().isEmpty()) {
                treeExpandedAll(child, expanded);
            }
        }
    }

}
