/*
 * Created by JFormDesigner on Fri Jun 14 09:37:13 CST 2024
 */

package com.dillon.lw.view.system.role;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.api.system.RoleApi;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.dtflys.forest.Forest;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author wenli
 */
public class RoleEditPane extends JPanel {

    private Long id;

    public RoleEditPane() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        contentPanel = new JPanel();
        label1 = new JLabel();
        nameTextField = new JTextField();
        label3 = new JLabel();
        codeTextField = new JTextField();
        label2 = new JLabel();
        typeComboBox = new JComboBox();
        label5 = new JLabel();
        sortSpinner = new JSpinner();
        label7 = new JLabel();
        statusComboBox = new JComboBox();
        label9 = new JLabel();
        scrollPane1 = new JScrollPane();
        remarkTextArea = new JTextArea();

        //======== this ========
        setLayout(new BorderLayout());

        //======== contentPanel ========
        {
            contentPanel.setLayout(new MigLayout(
                "fill,insets dialog,hidemode 3",
                // columns
                "[right]" +
                "[240:n,grow,shrink 0,sizegroup 1,fill]15",
                // rows
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[45!]" +
                "[180,grow,shrink 0]"));

            //---- label1 ----
            label1.setText("*\u89d2\u8272\u540d\u79f0");
            contentPanel.add(label1, "cell 0 0");
            contentPanel.add(nameTextField, "cell 1 0");

            //---- label3 ----
            label3.setText("*\u89d2\u8272\u6807\u8bc6");
            contentPanel.add(label3, "cell 0 1");
            contentPanel.add(codeTextField, "cell 1 1");

            //---- label2 ----
            label2.setText("\u89d2\u8272\u7c7b\u578b");
            contentPanel.add(label2, "cell 0 2");
            contentPanel.add(typeComboBox, "cell 1 2");

            //---- label5 ----
            label5.setText("*\u663e\u793a\u987a\u5e8f");
            contentPanel.add(label5, "cell 0 3");
            contentPanel.add(sortSpinner, "cell 1 3");

            //---- label7 ----
            label7.setText("*\u72b6\u6001");
            contentPanel.add(label7, "cell 0 4");
            contentPanel.add(statusComboBox, "cell 1 4");

            //---- label9 ----
            label9.setText("\u5907\u6ce8");
            contentPanel.add(label9, "cell 0 5,aligny top,growy 0");

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(remarkTextArea);
            }
            contentPanel.add(scrollPane1, "cell 1 5,growy");
        }
        add(contentPanel, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        SpinnerModel model = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        sortSpinner.setModel(model);
        statusComboBox.addItem("开启");
        statusComboBox.addItem("关闭");

        typeComboBox.addItem("内置");
        typeComboBox.addItem("自定义");


    }

    private void initListeners() {


    }


    public void updateData(Long id) {

        this.id = id;

        if (id == null) {
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(RoleApi.class).getRole(id).getCheckedData();
        }).thenAcceptAsync(roleRespVO -> {
            setValue(roleRespVO);
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    private void setValue(RoleRespVO roleRespVO) {
        nameTextField.setText(roleRespVO.getName());
        codeTextField.setText(roleRespVO.getCode());
        sortSpinner.setValue(roleRespVO.getSort() == null ? 0 : roleRespVO.getSort());
        statusComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(roleRespVO.getStatus(), 0));
        typeComboBox.setSelectedIndex(roleRespVO.getType() == null ? 1 : roleRespVO.getType() - 1);
        remarkTextArea.setText(roleRespVO.getRemark());

    }

    public RoleSaveReqVO getValue() {
        RoleSaveReqVO reqVO = new RoleSaveReqVO();
        reqVO.setId(id);
        reqVO.setName(nameTextField.getText());
        reqVO.setCode(codeTextField.getText());
        reqVO.setRemark(remarkTextArea.getText());
        reqVO.setSort(Convert.toInt(sortSpinner.getValue()));
        reqVO.setStatus(statusComboBox.getSelectedIndex());
        reqVO.setType(typeComboBox.getSelectedIndex() + 1);
        return reqVO;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField nameTextField;
    private JLabel label3;
    private JTextField codeTextField;
    private JLabel label2;
    private JComboBox typeComboBox;
    private JLabel label5;
    private JSpinner sortSpinner;
    private JLabel label7;
    private JComboBox statusComboBox;
    private JLabel label9;
    private JScrollPane scrollPane1;
    private JTextArea remarkTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
