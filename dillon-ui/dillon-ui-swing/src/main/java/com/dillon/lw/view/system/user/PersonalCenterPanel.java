package com.dillon.lw.view.system.user;

import cn.hutool.core.date.DateUtil;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.api.system.UserProfileApi;
import com.dillon.lw.components.WPanel;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.dillon.lw.view.frame.MainFrame;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CompletableFuture;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT;

/**
 * 个人中心面板
 *
 * @author liwen
 * @date 2022/07/08
 */
public class PersonalCenterPanel extends JPanel implements Observer {

    /**
     * 人小组
     */
    private JPanel personPanel;
    /**
     * 选项卡式窗格
     */
    private JTabbedPane tabbedPane;
    /**
     * 基本信息面板
     */
    private JPanel basicInfoPanel;
    /**
     * pwd面板
     */
    private JPanel pwdPanel;

    /**
     * 《阿凡达》标签
     */
    private JLabel avatarLabel;
    /**
     * 用户名称标签
     */
    private JLabel userNameLabel;
    /**
     * 电话号码标签
     */
    private JLabel phoneNumberLabel;
    /**
     * 邮件标签
     */
    private JLabel emailLabel;
    /**
     * 部门标签
     */
    private JLabel deptLabel;
    /**
     * 部门标签
     */
    private JLabel postLabel;
    /**
     * 标签作用
     */
    private JLabel roleLabel;
    /**
     * 创建时间标签
     */
    private JLabel createTimeLabel;

    /**
     * 尼克名字文本字段
     */
    private JTextField nickNameTextField;
    /**
     * 手机文本字段
     */
    private JTextField phoneTextField;
    /**
     * 电子邮件文本字段
     */
    private JTextField emailTextField;
    /**
     * 性组合框
     */
    private JComboBox<String> sexComboBox;


    /**
     * 老pwd领域
     */
    private JPasswordField oldPwdField;
    /**
     * 新pwd字段
     */
    private JPasswordField newPwdField;
    /**
     * 新pwd field2
     */
    private JPasswordField newPwdField2;

    /**
     * 保存信息,但
     */
    private JButton saveInfoBut;
    /**
     * 拯救pwd但
     */
    private JButton savePwdBut;

    /**
     * 个人中心面板
     */
    public PersonalCenterPanel() {

        initComponents();
    }

    /**
     * 初始化组件
     */
    private void initComponents() {

        this.setOpaque(false);
        this.setLayout(new BorderLayout(10, 1));
        this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        initPersonPanel();
        initTabbedPane();
        updateData();
    }

    /**
     * init人小组
     */
    private void initPersonPanel() {
        personPanel = new WPanel();
        personPanel.setLayout(new MigLayout("aligny top,wrap 1,insets 10", "[300!]", "[][][][][][][][][][][]"));
        personPanel.add(new JLabel("个人信息"));
        personPanel.add(new JSeparator(), "growx");
        personPanel.add(avatarLabel = new JLabel(), "h 150!,center");
        avatarLabel.setIcon(new FlatSVGIcon("icons/user-filling.svg", 120, 120));
        personPanel.add(new JSeparator(), "growx");
        personPanel.add(createLabelPanel("用户名称", "icons/user.svg", userNameLabel = new JLabel()), "growx,h 40!");
        personPanel.add(createLabelPanel("手机号码", "icons/shoujihao.svg", phoneNumberLabel = new JLabel()), "growx,h 40!");
        personPanel.add(createLabelPanel("用户邮箱", "icons/email.svg", emailLabel = new JLabel()), "growx,h 40!");
        personPanel.add(createLabelPanel("所属部门", "icons/bumenguanli.svg", deptLabel = new JLabel()), "growx,h 40!");
        personPanel.add(createLabelPanel("所属岗位", "icons/bumenguanli.svg", postLabel = new JLabel()), "growx,h 40!");
        personPanel.add(createLabelPanel("所属角色", "icons/jiaoseguanli.svg", roleLabel = new JLabel()), "growx,h 40!");
        personPanel.add(createLabelPanel("创建日期", "icons/calendar.svg", createTimeLabel = new JLabel()), "growx,h 40!");

        this.add(personPanel, BorderLayout.WEST);
    }

    /**
     * 初始化选项卡式窗格
     */
    private void initTabbedPane() {
        basicInfoPanel = new JPanel();
        basicInfoPanel.setLayout(new MigLayout("top,center,wrap 2,insets 10", "[right,60!][grow]", "[][][][][]"));
        basicInfoPanel.add(new JLabel("*用户昵称"));
        basicInfoPanel.add(nickNameTextField = new JTextField(), "growx,h 40!");

        basicInfoPanel.add(new JLabel("*手机号码"));
        basicInfoPanel.add(phoneTextField = new JTextField(), "growx,h 40!");

        basicInfoPanel.add(new JLabel("*邮箱"));
        basicInfoPanel.add(emailTextField = new JTextField(), "growx,h 40!");

        basicInfoPanel.add(new JLabel("*性别"));
        basicInfoPanel.add(sexComboBox = new JComboBox(new Object[]{"男", "女", "未知"}), "growx,h 40!");

        basicInfoPanel.add(saveInfoBut = new JButton("保存"), "span 2,right");
        saveInfoBut.addActionListener(e -> updateProfile());

        pwdPanel = new JPanel();
        pwdPanel.setLayout(new MigLayout("top,center,wrap 2,insets 10", "[right,60!][grow]", "[][][][]"));

        pwdPanel.add(new JLabel("*旧密码"));
        pwdPanel.add(oldPwdField = new JPasswordField(), "growx,h 40!");

        pwdPanel.add(new JLabel("*新密码"));
        pwdPanel.add(newPwdField = new JPasswordField(), "growx,h 40!");

        pwdPanel.add(new JLabel("*确认密码"));
        pwdPanel.add(newPwdField2 = new JPasswordField(), "growx,h 40!");


        oldPwdField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "请输入旧密码");
        newPwdField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "请输入新密码");
        newPwdField2.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "请确认密码");
        oldPwdField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        newPwdField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        newPwdField2.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");

        pwdPanel.add(savePwdBut = new JButton("保存"), "span 2,right");
        savePwdBut.addActionListener(e -> updatePwd());
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("基础信息", basicInfoPanel);
        tabbedPane.addTab("修改密码", pwdPanel);
        tabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, new JToolBar());

        this.add(tabbedPane, BorderLayout.CENTER);

    }


    /**
     * 创建标签面板
     *
     * @param tile  瓷砖
     * @param icon  图标
     * @param label 标签
     * @return {@link JPanel}
     */
    private JPanel createLabelPanel(String tile, String icon, JLabel label) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel tileLabel = new JLabel(tile);
        tileLabel.setIcon(new FlatSVGIcon(icon, 20, 20));
        label.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(tileLabel, BorderLayout.WEST);
        panel.add(label, BorderLayout.CENTER);
        panel.add(new JSeparator(), BorderLayout.SOUTH);
        return panel;
    }


    /**
     * 更新数据
     */
    public void updateData() {

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(UserProfileApi.class).getUserProfile().getCheckedData();
        }).thenAcceptAsync(userProfileRespVO -> {
            userNameLabel.setText(userProfileRespVO.getUsername());
            phoneNumberLabel.setText(userProfileRespVO.getMobile());
            emailLabel.setText(userProfileRespVO.getEmail());
            deptLabel.setText(userProfileRespVO.getDept().getName());
            postLabel.setText(userProfileRespVO.getPosts() + "");
            roleLabel.setText(userProfileRespVO.getRoles() + "");
            createTimeLabel.setText(DateUtil.format(userProfileRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));

            nickNameTextField.setText(userProfileRespVO.getNickname());
            phoneTextField.setText(userProfileRespVO.getMobile());
            emailTextField.setText(userProfileRespVO.getEmail());
            sexComboBox.setSelectedItem(userProfileRespVO.getSex());
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });


    }


    /**
     * 更新配置文件
     * 更新个人信息
     */
    private void updateProfile() {
        UserProfileUpdateReqVO sysUser = new UserProfileUpdateReqVO();
        sysUser.setNickname(nickNameTextField.getText());
        sysUser.setMobile(phoneTextField.getText());
        sysUser.setEmail(emailTextField.getText());
        sysUser.setSex(sexComboBox.getSelectedIndex());

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(UserProfileApi.class).updateUserProfile(sysUser).getCheckedData();
        }).thenAcceptAsync(result -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功");
            updateData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });

    }

    /**
     * 更新pwd
     */
    private void updatePwd() {
        String oldPwd = oldPwdField.getText();
        String newPwd = newPwdField.getText();
        String newPwd2 = newPwdField2.getText();


        if (updateFieldValidity(oldPwdField, StringUtils.isEmpty(oldPwd))) {
            oldPwdField.requestFocusInWindow();
            return;
        }
        if (updateFieldValidity(newPwdField, StringUtils.isEmpty(newPwd))) {
            newPwdField.requestFocusInWindow();
            return;
        }
        if (updateFieldValidity(newPwdField2, StringUtils.isEmpty(newPwd2))) {
            newPwdField2.requestFocusInWindow();
            return;
        }

        if (!newPwd2.equals(newPwd)) {
            updateFieldValidity(newPwdField, true);
            updateFieldValidity(newPwdField2, true);
            return;
        }

        UserProfileUpdatePasswordReqVO reqVO = new UserProfileUpdatePasswordReqVO();
        reqVO.setNewPassword(newPwd);
        reqVO.setOldPassword(oldPwd);

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(UserProfileApi.class).updateUserProfilePassword(reqVO).getCheckedData();
        }).thenAcceptAsync(result -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功");
            updateData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });


    }

    private boolean updateFieldValidity(JTextField textField, boolean valid) {
        textField.putClientProperty(FlatClientProperties.OUTLINE, valid ? FlatClientProperties.OUTLINE_ERROR : null);
        return valid;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (this.isDisplayable()) {
            updateData();
        }
    }
}
