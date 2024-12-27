package com.lw.fx.view.system.user;

import atlantafx.base.controls.Popover;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UserAssignRoleFormView implements FxmlView<UserAssignRoleFormViewModel>, Initializable {
    @InjectViewModel
    private UserAssignRoleFormViewModel viewModel;
    @FXML
    private TextField nicknameField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField usernameField;

    private Popover roleListViewPopover;
    private ListView<RoleRespVO> roleListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        roleListViewPopover = new Popover(roleListView = new ListView<>());
        roleListViewPopover.setHeaderAlwaysVisible(false);
        roleListViewPopover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);

        roleListView.setCellFactory(postSimpleRespVOListView -> new CheckBoxListCell());
        roleField.setEditable(false);
        roleField.setOnMouseClicked(actionEvent -> {
            roleListView.setPrefWidth(roleField.getWidth() - 50);
            roleListViewPopover.show(roleField);
        });

        viewModel.getSelRoleItems().addListener((ListChangeListener<RoleRespVO>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (RoleRespVO item : change.getAddedSubList()) {
                        roleField.setText(viewModel.getSelRoleItems()+"");
                    }
                }
                if (change.wasRemoved()) {
                    for (RoleRespVO item : change.getRemoved()) {
                        roleField.setText(viewModel.getSelRoleItems()+"");
                    }
                }
            }
        });

        roleField.setOnMouseClicked(actionEvent -> {
            roleListView.setPrefWidth(roleField.getWidth() - 50);
            roleListViewPopover.show(roleField);
        });
        roleListView.itemsProperty().bind(viewModel.roleItemsProperty());
        nicknameField.textProperty().bindBidirectional(viewModel.nicknameProperty());
        usernameField.textProperty().bindBidirectional(viewModel.nicknameProperty());

    }

    // 自定义ListCell类
    class CheckBoxListCell extends ListCell<RoleRespVO> {
        private final CheckBox checkBox;

        public CheckBoxListCell() {
            checkBox = new CheckBox();
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    viewModel.getSelRoleItems().add(getItem());
                } else {
                    viewModel.getSelRoleItems().remove(getItem());
                }
            });
        }

        @Override
        protected void updateItem(RoleRespVO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                setText(null);

                checkBox.setSelected(viewModel.getSelRoleItems().contains(item));
                checkBox.setText(item.getName());
                setGraphic(checkBox);
            }
        }
    }
}
