package com.dillon.lw.view.intra.db;

import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.infra.DataSourceConfigApi;
import com.dillon.lw.module.infra.controller.admin.db.vo.DataSourceConfigRespVO;
import com.dillon.lw.module.infra.controller.admin.db.vo.DataSourceConfigSaveReqVO;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * 数据源配置编辑表单。
 * <p>
 * 说明：
 * 1. 用于新增/编辑；
 * 2. 编辑时后端不会返回密码，界面要求用户重新输入密码后提交；
 * 3. 仅处理表单数据，不直接发起保存请求，交给外层面板统一提交。
 * </p>
 */
public class DataSourceConfigFormPane extends JPanel {

    private Long id;
    private JTextField nameField;
    private JTextField urlField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public DataSourceConfigFormPane() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new MigLayout(
                "insets 15,fillx,hidemode 3",
                "[right][grow,fill]",
                "[][][][]"));

        nameField = createTextField("请输入数据源名称");
        urlField = createTextField("请输入 JDBC URL");
        usernameField = createTextField("请输入数据库用户名");
        passwordField = new JPasswordField();
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "请输入数据库密码（编辑时需重新填写）");

        add(new JLabel("数据源名称"), "cell 0 0");
        add(nameField, "cell 1 0,w 420!");

        add(new JLabel("JDBC URL"), "cell 0 1");
        add(urlField, "cell 1 1,w 420!");

        add(new JLabel("用户名"), "cell 0 2");
        add(usernameField, "cell 1 2,w 420!");

        add(new JLabel("密码"), "cell 0 3");
        add(passwordField, "cell 1 3,w 420!");
    }

    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        textField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        return textField;
    }

    /**
     * 根据 id 回填数据；id 为空时表示新增。
     */
    public void updateData(Long id) {
        this.id = id;
        if (id == null) {
            clearForm();
            return;
        }
        DataSourceConfigRespVO respVO = Forest.client(DataSourceConfigApi.class).getDataSourceConfig(id).getCheckedData();
        nameField.setText(respVO.getName());
        urlField.setText(respVO.getUrl());
        usernameField.setText(respVO.getUsername());
        passwordField.setText("");
    }

    public boolean validates() {
        if (StrUtil.isBlank(nameField.getText())) {
            JOptionPane.showMessageDialog(this, "数据源名称不能为空");
            nameField.requestFocusInWindow();
            return false;
        }
        if (StrUtil.isBlank(urlField.getText())) {
            JOptionPane.showMessageDialog(this, "数据源连接不能为空");
            urlField.requestFocusInWindow();
            return false;
        }
        if (StrUtil.isBlank(usernameField.getText())) {
            JOptionPane.showMessageDialog(this, "用户名不能为空");
            usernameField.requestFocusInWindow();
            return false;
        }
        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "密码不能为空");
            passwordField.requestFocusInWindow();
            return false;
        }
        return true;
    }

    public DataSourceConfigSaveReqVO getValue() {
        DataSourceConfigSaveReqVO reqVO = new DataSourceConfigSaveReqVO();
        reqVO.setId(id);
        reqVO.setName(nameField.getText().trim());
        reqVO.setUrl(urlField.getText().trim());
        reqVO.setUsername(usernameField.getText().trim());
        reqVO.setPassword(new String(passwordField.getPassword()));
        return reqVO;
    }

    private void clearForm() {
        nameField.setText("");
        urlField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }
}

