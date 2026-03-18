package com.dillon.lw.fx.view.system.dept;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Popover;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.util.NumberUtil;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.toggleStyleClass;

/**
 * 视图菜单对话框
 *
 * @author wenli
 * @date 2023/02/15
 */
public class DeptFromView extends BaseView<DeptFromViewModel> implements Initializable {


    private Popover popover;
    private TreeView<DeptSimpleRespVO> deptTree;
    @FXML
    private CustomTextField deptTreeTextField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<UserSimpleRespVO> leaderUserIdComBox;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneField;

    @FXML
    private VBox rootBox;

    @FXML
    private Spinner<Integer> sortNumFeild;

    @FXML
    private ToggleSwitch statusToggleBut;

    /**
     * 初始化
     *
     * @param url            url
     * @param resourceBundle 资源包
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deptTreeTextField.setRight(FontIcon.of(Feather.CHEVRON_DOWN));
        popover = new Popover(deptTree = new TreeView<>());
        popover.setHeaderAlwaysVisible(false);
        popover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);
        deptTree.setCellFactory(cell -> new TreeCell<>() {
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
        deptTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.selectTreeItemProperty().setValue(newValue);
                viewModel.parentIdProperty().set(newValue.getValue().getId());
                deptTreeTextField.setText(newValue.getValue().getName());
            }
            if (popover != null) {
                popover.hide();
            }

        });
        viewModel.selectTreeItemProperty().addListener((observableValue, deptSimpleRespVOTreeItem, t1) -> deptTree.getSelectionModel().select(t1));

        deptTreeTextField.setEditable(false);
        deptTreeTextField.setOnMouseClicked(actionEvent -> {
            deptTree.setPrefWidth(deptTreeTextField.getWidth() - 50);
            popover.show(deptTreeTextField);
            TreeItem<DeptSimpleRespVO> selectedTreeItem = viewModel.selectTreeItemProperty().get();
            if (selectedTreeItem != null) {
                selectedTreeItem.setExpanded(true);
            }
        });
        nameTextField.textProperty().bindBidirectional(viewModel.nameProperty());
        deptTree.rootProperty().bind(viewModel.deptTreeRootProperty());

        int initSort = viewModel.sortProperty().getValue() == null ? 0 : viewModel.sortProperty().getValue().intValue();
        SpinnerValueFactory.IntegerSpinnerValueFactory sortFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, initSort);
        sortNumFeild.setValueFactory(sortFactory);
        sortNumFeild.setEditable(true);
        sortNumFeild.valueProperty().addListener((observableValue, integer, t1) -> {
            if (t1 != null) {
                viewModel.sortProperty().set(t1);
            }
        });
        viewModel.sortProperty().addListener((observable, oldValue, newValue) -> {
            int value = newValue == null ? 0 : newValue.intValue();
            Integer currentValue = sortFactory.getValue();
            if (currentValue == null || currentValue.intValue() != value) {
                sortFactory.setValue(value);
            }
        });
        viewModel.statusProperty().addListener((observableValue, number, t1) -> statusToggleBut.setSelected(NumberUtil.equals(t1, 0)));
        statusToggleBut.selectedProperty().addListener((observableValue, aBoolean, t1) -> viewModel.statusProperty().set(t1 ? 0 : 1));
        phoneField.textProperty().bindBidirectional(viewModel.phoneProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        nameTextField.textProperty().bindBidirectional(viewModel.nameProperty());

        leaderUserIdComBox.itemsProperty().bind(viewModel.leaderUserListProperty());
        leaderUserIdComBox.getSelectionModel().selectedItemProperty().addListener((observableValue, userSimpleRespVO, t1) -> viewModel.selectLeaderUserProperty().set(t1));
        viewModel.selectLeaderUserProperty().addListener((observableValue, userSimpleRespVO, t1) -> leaderUserIdComBox.getSelectionModel().select(t1));
        leaderUserIdComBox.setCellFactory(cell -> new TextFieldListCell<>() {
            @Override
            public void updateItem(UserSimpleRespVO userSimpleRespVO, boolean empty) {
                super.updateItem(userSimpleRespVO, empty);
                if (userSimpleRespVO == null || empty) {
                    setText(null);
                } else {
                    setText(userSimpleRespVO.getNickname());
                }
            }
        });
        // 设置按钮上显示的文字（当前选中项）
        leaderUserIdComBox.setButtonCell(new ListCell<UserSimpleRespVO>() {
            @Override
            protected void updateItem(UserSimpleRespVO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNickname());
            }
        });
        toggleStyleClass(deptTree, Tweaks.ALT_ICON);
    }


}
