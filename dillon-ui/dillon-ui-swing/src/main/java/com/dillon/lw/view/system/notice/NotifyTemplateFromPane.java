/*
 * Created by JFormDesigner on Thu Jul 25 16:52:21 CST 2024
 */

package com.dillon.lw.view.system.notice;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.api.system.NotifyTemplateApi;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.DictTypeEnum;
import com.dtflys.forest.Forest;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author wenli
 */
public class NotifyTemplateFromPane extends JPanel {
    private Long id;

    public NotifyTemplateFromPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        codeTield = new JTextField();
        label2 = new JLabel();
        nameField = new JTextField();
        label3 = new JLabel();
        nicknameField = new JTextField();
        label4 = new JLabel();
        typeComboBox = new JComboBox();
        label5 = new JLabel();
        statusComboBox = new JComboBox();
        label6 = new JLabel();
        scrollPane1 = new JScrollPane();
        contentArea = new JTextArea();
        label7 = new JLabel();
        remarkField = new JTextField();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[400:480,grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[320,grow,fill]" +
            "[]"));

        //---- label1 ----
        label1.setText("\u6a21\u7248\u7f16\u7801");
        add(label1, "cell 0 0");
        add(codeTield, "cell 1 0");

        //---- label2 ----
        label2.setText("\u6a21\u677f\u540d\u79f0");
        add(label2, "cell 0 1");
        add(nameField, "cell 1 1");

        //---- label3 ----
        label3.setText("\u53d1\u4ef6\u4eba\u540d\u79f0");
        add(label3, "cell 0 2");
        add(nicknameField, "cell 1 2");

        //---- label4 ----
        label4.setText("\u7c7b\u578b");
        add(label4, "cell 0 3");
        add(typeComboBox, "cell 1 3");

        //---- label5 ----
        label5.setText("\u5f00\u542f\u72b6\u6001");
        add(label5, "cell 0 4");
        add(statusComboBox, "cell 1 4");

        //---- label6 ----
        label6.setText("\u6a21\u677f\u5185\u5bb9");
        add(label6, "cell 0 5,aligny top,growy 0");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(contentArea);
        }
        add(scrollPane1, "cell 1 5,grow");

        //---- label7 ----
        label7.setText("\u5907\u6ce8");
        add(label7, "cell 0 6");
        add(remarkField, "cell 1 6");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        statusComboBox.addItem("开启");
        statusComboBox.addItem("关闭");

        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.SYSTEM_NOTICE_TYPE);
        typeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof DictDataSimpleRespVO) {
                    value = ((DictDataSimpleRespVO) value).getLabel();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        for (DictDataSimpleRespVO dictDataSimpleRespVO : dictDataSimpleRespVOList) {
            typeComboBox.addItem(dictDataSimpleRespVO);
        }
    }

    private void setValue(NotifyTemplateRespVO respVO) {
        nameField.setText(respVO.getName());
        nicknameField.setText(respVO.getNickname());
        remarkField.setText(respVO.getRemark());
        codeTield.setText(respVO.getCode());
        statusComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(respVO.getStatus(), 0));
        typeComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(respVO.getType(), 0));
        contentArea.setText(respVO.getContent());


    }

    public NotifyTemplateSaveReqVO getValue() {
        NotifyTemplateSaveReqVO reqVO = new NotifyTemplateSaveReqVO();
        reqVO.setId(id);
        reqVO.setName(nameField.getText());
        reqVO.setNickname(nicknameField.getText());
        reqVO.setCode(codeTield.getText());
        reqVO.setRemark(remarkField.getText());
        reqVO.setContent(contentArea.getText());
        reqVO.setStatus(statusComboBox.getSelectedIndex());
        reqVO.setType(typeComboBox.getSelectedIndex());
        return reqVO;
    }


    public void updateData(NotifyTemplateRespVO respVO) {
        this.id = respVO.getId();
        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.SYSTEM_NOTICE_TYPE);

        for (DictDataSimpleRespVO dictDataSimpleRespVO : dictDataSimpleRespVOList) {
            if (StrUtil.equals(dictDataSimpleRespVO.getValue(), respVO.getType() + "")) {
                typeComboBox.setSelectedItem(dictDataSimpleRespVO);
                break;
            }

        }

        if (id == null) {
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(NotifyTemplateApi.class).getNotifyTemplate(id).getCheckedData();
        }).thenAcceptAsync(resp -> {
            setValue(resp);
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField codeTield;
    private JLabel label2;
    private JTextField nameField;
    private JLabel label3;
    private JTextField nicknameField;
    private JLabel label4;
    private JComboBox typeComboBox;
    private JLabel label5;
    private JComboBox statusComboBox;
    private JLabel label6;
    private JScrollPane scrollPane1;
    private JTextArea contentArea;
    private JLabel label7;
    private JTextField remarkField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
