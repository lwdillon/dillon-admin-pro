package com.lw.fx.view.system.role;

import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.convert.Convert;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RoleDataPermissionFormView implements FxmlView<RoleDataPermissionFormViewModel>, Initializable {
    @InjectViewModel
    private RoleDataPermissionFormViewModel viewModel;
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
        treeView.setCellFactory(javafx.scene.control.cell.CheckBoxTreeCell.forTreeView());
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
