package com.lw.fx.view.system.role;

import atlantafx.base.controls.ToggleSwitch;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class RoleAssignMenuFormView implements FxmlView<RoleAssignMenuFormViewModel>, Initializable {

    @InjectViewModel
    private RoleAssignMenuFormViewModel viewModel;
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

        treeView.setCellFactory(javafx.scene.control.cell.CheckBoxTreeCell.forTreeView());
        nameBut.textProperty().bindBidirectional(viewModel.nameProperty());
        codeBut.textProperty().bindBidirectional(viewModel.codeProperty());
        treeView.rootProperty().bind(viewModel.menuTreeRootProperty());

        selAllBut.selectedProperty().addListener((observableValue, aBoolean, newV) -> {
            viewModel.getMenuTreeRoot().setSelected(newV);
        });
        expandedBut.selectedProperty().addListener((observableValue, aBoolean, newV) -> {
           treeExpandedAll(treeView.getRoot(),newV);


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
