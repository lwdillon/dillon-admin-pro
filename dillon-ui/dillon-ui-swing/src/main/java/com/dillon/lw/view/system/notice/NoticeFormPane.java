/*
 * Created by JFormDesigner on Sun Jun 16 17:23:39 CST 2024
 */

package com.dillon.lw.view.system.notice;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.api.system.NoticeApi;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.notice.vo.NoticeRespVO;
import com.dillon.lw.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
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
public class NoticeFormPane extends JPanel {
    private Long id;

    public NoticeFormPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        titleTextFiled = new JTextField();
        label2 = new JLabel();
        typeComboBox = new JComboBox();
        label3 = new JLabel();
        statusComboBox = new JComboBox();
        label4 = new JLabel();
        scrollPane2 = new JScrollPane();
        contentTextPane = new JTextPane();
        label5 = new JLabel();
        scrollPane1 = new JScrollPane();
        remarkTextArea = new JTextArea();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[520:n,grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[480:n,grow]" +
            "[120:n]"));

        //---- label1 ----
        label1.setText("*\u516c\u544a\u6807\u9898");
        add(label1, "cell 0 0");
        add(titleTextFiled, "cell 1 0");

        //---- label2 ----
        label2.setText("*\u516c\u544a\u7c7b\u578b ");
        add(label2, "cell 0 1");
        add(typeComboBox, "cell 1 1,alignx left,growx 0");

        //---- label3 ----
        label3.setText("*\u72b6\u6001");
        add(label3, "cell 0 2");
        add(statusComboBox, "cell 1 2,alignx left,growx 0");

        //---- label4 ----
        label4.setText("*\u516c\u544a\u5185\u5bb9");
        add(label4, "cell 0 3,aligny top,growy 0");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(contentTextPane);
        }
        add(scrollPane2, "cell 1 3,grow");

        //---- label5 ----
        label5.setText("\u5907\u6ce8");
        add(label5, "cell 0 4,aligny top,growy 0");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(remarkTextArea);
        }
        add(scrollPane1, "cell 1 4,grow");
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

    private void setValue(NoticeRespVO respVO) {
        titleTextFiled.setText(respVO.getTitle());
        contentTextPane.setText(respVO.getContent());
        statusComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(respVO.getStatus(), 0));
        typeComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(respVO.getType(), 0));
        remarkTextArea.setText(respVO.getRemark());

    }

    public NoticeSaveReqVO getValue() {
        NoticeSaveReqVO reqVO = new NoticeSaveReqVO();
        reqVO.setId(id);
        reqVO.setTitle(titleTextFiled.getText());
        reqVO.setType(typeComboBox.getSelectedIndex());
        reqVO.setContent(contentTextPane.getText());
        reqVO.setStatus(statusComboBox.getSelectedIndex());
        return reqVO;
    }


    public void updateData(NoticeRespVO respVO) {
        this.id = respVO.getId();

        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.SYSTEM_NOTICE_TYPE);

        for (DictDataSimpleRespVO dictDataSimpleRespVO : dictDataSimpleRespVOList) {
            if (StrUtil.equals(dictDataSimpleRespVO.getValue(), respVO.getType() + "")) {
                typeComboBox.setSelectedItem(dictDataSimpleRespVO);
                break;
            }

        }

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(NoticeApi.class).getNotice(id).getCheckedData();
        }).thenAcceptAsync(respVO1 -> {
            setValue(respVO1);
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
    private JTextField titleTextFiled;
    private JLabel label2;
    private JComboBox typeComboBox;
    private JLabel label3;
    private JComboBox statusComboBox;
    private JLabel label4;
    private JScrollPane scrollPane2;
    private JTextPane contentTextPane;
    private JLabel label5;
    private JScrollPane scrollPane1;
    private JTextArea remarkTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
