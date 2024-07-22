package com.lw.fx.view.system.user;

import atlantafx.base.controls.Popover;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class UserFormView implements FxmlView<UserFormViewModel>, Initializable {

    @InjectViewModel
    private UserFormViewModel viewModel;
    @FXML
    private TextField deptField;
    @FXML
    private TextField postField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nickNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phonenumberField;

    @FXML
    private TextArea remarkArea;

    @FXML
    private VBox rootPane;

    @FXML
    private ComboBox<String> sexCombo;

    @FXML
    private TextField userNameField;
    @FXML
    private HBox pwdBox;


    private Popover deptTreeViewPopover;
    private Popover postListViewPopover;
    private TreeView<DeptSimpleRespVO> deptTreeView;
    private ListView<PostSimpleRespVO> postListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        deptTreeViewPopover = new Popover(deptTreeView = new TreeView<>());
        deptTreeViewPopover.setHeaderAlwaysVisible(false);
        deptTreeViewPopover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);

        postListViewPopover = new Popover(postListView = new ListView<>());
        postListViewPopover.setHeaderAlwaysVisible(false);
        postListViewPopover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);


        deptField.setEditable(false);
        deptField.setOnMouseClicked(actionEvent -> {
            deptTreeView.setPrefWidth(deptField.getWidth() - 50);
            deptTreeViewPopover.show(deptField);
        });
        deptTreeView.setCellFactory(cell -> new TreeCell<>() {
            @Override
            protected void updateItem(DeptSimpleRespVO menuSimpleRespVO, boolean empty) {
                super.updateItem(menuSimpleRespVO, empty);
                if (menuSimpleRespVO == null || empty) {
                    setText(null);
                } else {
                    setText(menuSimpleRespVO.getName());
                }
            }
        });
        deptTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.selectTreeItemProperty().setValue(newValue);
                viewModel.deptIdProperty().set(newValue.getValue().getId());
                deptField.setText(newValue.getValue().getName());
            }
            if (deptTreeViewPopover != null) {
                deptTreeViewPopover.hide();
            }

        });
        viewModel.selectTreeItemProperty().addListener((observableValue, menuSimpleRespVOTreeItem, t1) -> deptTreeView.getSelectionModel().select(t1));

        postListView.setCellFactory(postSimpleRespVOListView -> new CheckBoxListCell());
        postField.setEditable(false);
        postField.setOnMouseClicked(actionEvent -> {
            postListView.setPrefWidth(postField.getWidth() - 50);
            postListViewPopover.show(postField);
        });

        viewModel.getSelectPostItems().addListener((ListChangeListener<PostSimpleRespVO>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (PostSimpleRespVO item : change.getAddedSubList()) {
                        postField.setText(viewModel.getSelectPostItems() + "");
                    }
                }
                if (change.wasRemoved()) {
                    for (PostSimpleRespVO item : change.getRemoved()) {
                        postField.setText(viewModel.getSelectPostItems() + "");
                    }
                }
            }
        });
        postListView.itemsProperty().bind(viewModel.postItemsProperty());
        deptTreeView.rootProperty().bind(viewModel.deptTreeRootProperty());
        pwdBox.visibleProperty().bind(viewModel.createProperty());
        pwdBox.managedProperty().bind(pwdBox.visibleProperty());
        nickNameField.textProperty().bindBidirectional(viewModel.nicknameProperty());
        userNameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        phonenumberField.textProperty().bindBidirectional(viewModel.mobileProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        remarkArea.textProperty().bindBidirectional(viewModel.remarkProperty());


        sexCombo.valueProperty().addListener((observableValue, o, t1) -> {
            int sex=3;
            if ("男".equals(t1)) {
                sex=1;
            } else if ("女".equals(t1)) {
                sex=2;
            }
            viewModel.sexProperty().set(sex);
        });
        viewModel.sexProperty().addListener((observableValue, number, t1) -> {
            String sex="未知";
            if (t1.intValue()==1) {
                sex="男";
            } else if (t1.intValue()==2) {
                sex="女";
            }
            sexCombo.setValue(sex);
        });
    }


    // 自定义ListCell类
    class CheckBoxListCell extends ListCell<PostSimpleRespVO> {
        private final CheckBox checkBox;

        public CheckBoxListCell() {
            checkBox = new CheckBox();
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    viewModel.getSelectPostItems().add(getItem());
                } else {
                    viewModel.getSelectPostItems().remove(getItem());
                }
            });
        }

        @Override
        protected void updateItem(PostSimpleRespVO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                setText(null);

                checkBox.setSelected(viewModel.getSelectPostItems().contains(item));
                checkBox.setText(item.getName());
                setGraphic(checkBox);
            }
        }
    }
}
