package com.dillon.lw.fx.view.system.role;

import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.convert.Convert;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class RoleDataPermissionFormView extends BaseView<RoleDataPermissionFormViewModel> implements Initializable {
    @FXML
    private Button codeBut;

    @FXML
    private ComboBox<DictDataSimpleRespVO> dataScopeCombox;

    @FXML
    private ToggleSwitch expandedBut;

    @FXML
    private Button nameBut;

    @FXML
    private ToggleSwitch selAllBut;

    @FXML
    private TreeView<DeptSimpleRespVO> treeView;

    @FXML
    private HBox treeBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        treeBox.managedProperty().bind(treeBox.visibleProperty());

        treeView.setCellFactory(new Callback<TreeView<DeptSimpleRespVO>, TreeCell<DeptSimpleRespVO>>() {
            @Override
            public TreeCell<DeptSimpleRespVO> call(TreeView<DeptSimpleRespVO> param) {
                return new CheckBoxTreeCell<DeptSimpleRespVO>() {
                    @Override
                    public void updateItem(DeptSimpleRespVO item, boolean empty) {
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
        treeView.rootProperty().bind(viewModel.deptTreeRootProperty());
        dataScopeCombox.setItems(viewModel.getDataScopeList());
        dataScopeCombox.setCellFactory(call -> new ListCell<>() {
            @Override
            protected void updateItem(DictDataSimpleRespVO dictDataSimpleRespVO, boolean b) {
                super.updateItem(dictDataSimpleRespVO, b);
                if (b) {
                    setText(null);
                } else {
                    setText(dictDataSimpleRespVO.getLabel());
                }
            }
        });
        // 设置按钮上显示的文字（当前选中项）
        dataScopeCombox.setButtonCell(new ListCell<DictDataSimpleRespVO>() {
            @Override
            protected void updateItem(DictDataSimpleRespVO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });
        selAllBut.selectedProperty().addListener((observableValue, aBoolean, newV) -> {
            viewModel.getDeptTreeRoot().setSelected(newV);
        });
        expandedBut.selectedProperty().addListener((observableValue, aBoolean, newV) -> {
            treeExpandedAll(treeView.getRoot(), newV);

        });

        dataScopeCombox.getSelectionModel().selectedItemProperty().addListener((observableValue, dictDataSimpleRespVO, t1) -> {
            viewModel.dataScopeProperty().set(Convert.toInt(t1.getValue()));
        });
        viewModel.dataScopeProperty().addListener((observableValue, number, t1) -> {

            treeBox.setVisible(t1.intValue() == 2);
            dataScopeCombox.getItems().forEach(dictDataSimpleRespVO -> {
                if (t1.intValue() == Convert.toInt(dictDataSimpleRespVO.getValue())) {
                    dataScopeCombox.getSelectionModel().select(dictDataSimpleRespVO);
                }
            });
        });
    }

    /**
     * 树扩展所有
     *
     * @param root     根
     * @param expanded 扩大
     */
    private void treeExpandedAll(TreeItem<DeptSimpleRespVO> root, boolean expanded) {
        for (TreeItem<DeptSimpleRespVO> child : root.getChildren()) {
            child.setExpanded(expanded);
            if (!child.getChildren().isEmpty()) {
                treeExpandedAll(child, expanded);
            }
        }
    }


}
