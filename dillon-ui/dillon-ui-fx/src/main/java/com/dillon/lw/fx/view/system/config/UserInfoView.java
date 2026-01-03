package com.dillon.lw.fx.view.system.config;

import atlantafx.base.controls.PasswordTextField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.BUTTON_CIRCLE;

/**
 * 快速配置菜单视图
 *
 * @author liwen
 * @date 2022/09/16
 */
public class UserInfoView extends BaseView<UserInfoViewModel>implements Initializable {


    @FXML
    private VBox root;
    @FXML
    private Button iconBut;

    @FXML
    private Label userName;

    @FXML
    private Label phonenumber;
    @FXML
    private Label email;
    @FXML
    private Label dept;
    @FXML
    private Label roles;
    @FXML
    private Label post;
    @FXML
    private Label nickName;
    @FXML
    private Label createDate;
    @FXML
    private TextField emailField;
    @FXML
    private TextField mobileField;
    @FXML
    private PasswordTextField newPwdField;
    @FXML
    private TextField nicknameField;
    @FXML
    private PasswordTextField oldPwdTextField;
    @FXML
    private Button restPwdBut;
    @FXML
    private Button restUserProfileBut;
    @FXML
    private Button savePwdBut;
    @FXML
    private Button saveUserProfileBut;
    @FXML
    private ComboBox<String> sexComboBox;
    @FXML
    private PasswordTextField verifyPwdField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nickName.textProperty().bind(viewModel.nicknameProperty());
        userName.textProperty().bind(viewModel.userNameProperty());
        phonenumber.textProperty().bind(viewModel.phonenumberProperty());
        email.textProperty().bind(viewModel.emailProperty());
        roles.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    String rolesStr = "";
                    for (RoleSimpleRespVO role : viewModel.rolesProperty().get()) {
                        rolesStr += role.getName() + ",";
                    }
                    return rolesStr;
                }, viewModel.rolesProperty())
        );
        post.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    String postStr = "";
                    for (PostSimpleRespVO post : viewModel.postsProperty().get()) {
                        postStr += post.getName() + ",";
                    }
                    return postStr;
                }, viewModel.postsProperty())
        );
        dept.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    if (ObjectUtil.isNotEmpty(viewModel.deptProperty().get())) {
                        return viewModel.deptProperty().get().getName();
                    } else {
                        return "";
                    }
                }, viewModel.deptProperty())
        );
        createDate.textProperty().bind(Bindings.createStringBinding(
                () -> DateUtil.format(viewModel.createTimeProperty().getValue(), "yyyy-MM-dd HH:mm:ss"), viewModel.createTimeProperty())
        );

        nicknameField.textProperty().bindBidirectional(viewModel.nicknameProperty());
        mobileField.textProperty().bindBidirectional(viewModel.phonenumberProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());

        iconBut.getStylesheets().addAll(BUTTON_CIRCLE, ACCENT);
        iconBut.setId("userBut");
        iconBut.setShape(new Circle(120));

        restPwdBut.setOnAction(actionEvent -> {
            oldPwdTextField.setText("");
            newPwdField.setText("");
            verifyPwdField.setText("");
        });
        restUserProfileBut.setOnAction(actionEvent -> {
            nicknameField.setText("");
            emailField.setText("");
            mobileField.setText("");
            sexComboBox.setValue(null);
        });

        savePwdBut.setOnAction(actionEvent -> {
            UserProfileUpdatePasswordReqVO reqVO = new UserProfileUpdatePasswordReqVO();
            reqVO.setNewPassword(newPwdField.getPassword());
            reqVO.setOldPassword(oldPwdTextField.getPassword());
            viewModel.updateUserProfilePassword(reqVO);
        });
        saveUserProfileBut.setOnAction(actionEvent -> {
            UserProfileUpdateReqVO reqVO = new UserProfileUpdateReqVO();
            reqVO.setEmail(email.getText());
            reqVO.setNickname(nicknameField.getText());
            reqVO.setMobile(mobileField.getText());
            reqVO.setSex(sexComboBox.getSelectionModel().getSelectedIndex() + 1);
            viewModel.updateUserProfile(reqVO);
        });

        sexComboBox.valueProperty().addListener((observableValue, o, t1) -> {
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
            sexComboBox.setValue(sex);
        });

    }
}
