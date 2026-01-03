/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.system.log;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.api.system.OperateLogApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class OperatelogManagementPanel extends JPanel {
    private String[] COLUMN_ID = {"日志编号", "操作人", "操作模块", "操作名", "操作内容", "操作时间", "业务编号", "ip", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public OperatelogManagementPanel() {
        initComponents();
        initListeners();
        updateData();
    }

    private void initComponents() {
        textField = new JTextField();
        scrollPane1 = new WScrollPane();
        centerPane = new JPanel();
        scrollPane2 = new WScrollPane();
        table = new JXTable(tableModel = new DefaultTableModel());
        toolPane = new WPanel();
        label7 = new JLabel();
        userNameTextField = new JTextField();
        label8 = new JLabel();
        typeTextField = new JTextField();
        label9 = new JLabel();
        subTypeTextField = new JTextField();
        label10 = new JLabel();
        startDateTextField = new WLocalDateCombo();
        label11 = new JLabel();
        endDateTextField = new WLocalDateCombo();
        label12 = new JLabel();
        actionTextField = new JTextField();
        label13 = new JLabel();
        bizIdTextField = new JTextField();
        searchBut = new JButton();
        reseBut = new JButton();
        clearBut = new JButton();

        //======== this ========
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));


        //======== centerPane ========
        {
            centerPane.setOpaque(false);
            centerPane.setLayout(new BorderLayout(10, 10));

            //======== scrollPane2 ========
            {
                tableModel.setColumnIdentifiers(COLUMN_ID);
                scrollPane2.setViewportView(table);
            }

            JPanel panel = new WPanel();
            panel.setLayout(new BorderLayout());
            panel.add(scrollPane2);
            paginationPane = new WPaginationPane() {
                @Override
                public void setPageIndex(long pageIndex) {
                    super.setPageIndex(pageIndex);
                    updateData();
                }
            };
            paginationPane.setOpaque(false);
            panel.add(paginationPane, BorderLayout.SOUTH);
            centerPane.add(panel, BorderLayout.CENTER);

            //======== toolPane ========
            {
                toolPane.setLayout(new MigLayout(
                        "fill,insets 0,hidemode 3",
                        // columns
                        "[left]",
                        // rows
                        "[]"));
                toolPane.setBorder(new EmptyBorder(10, 10, 10, 10));
                //---- label7 ----
                label7.setText("操作人");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                toolPane.add(userNameTextField, "cell 0 0");

                //---- label8 ----
                label8.setText("操作模块");
                toolPane.add(label8, "cell 0 0");

                //---- phoneTextField ----
                toolPane.add(typeTextField, "cell 0 0");

                //---- label9 ----
                label9.setText("操作名");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(subTypeTextField, "cell 0 0");

                //---- label12----
                label12.setText("操作内容");
                toolPane.add(label12, "cell 0 0");
                toolPane.add(actionTextField, "cell 0 0");

                //---- label13 ----
                label13.setText("业务编号");
                toolPane.add(label13, "cell 0 0");
                toolPane.add(bizIdTextField, "cell 0 0");


                //---- label10 ----
                label10.setText("创建时间");
                toolPane.add(label10, "cell 0 0");

                //---- startDateTextField ----
                toolPane.add(startDateTextField, "cell 0 0");

                //---- label11 ----
                label11.setText("-");
                toolPane.add(label11, "cell 0 0");

                //---- endDateTextField ----
                toolPane.add(endDateTextField, "cell 0 0");

                //---- button1 ----
                searchBut.setText("\u641c\u7d22");
                toolPane.add(searchBut, "cell 0 0");

                //---- reseBut ----
                reseBut.setText("\u91cd\u7f6e");
                toolPane.add(reseBut, "cell 0 0");

                //---- newBut ----
                clearBut.setText("清空日志");
                toolPane.add(clearBut, "cell 0 0");
            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);


        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());

    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (table != null) {
            table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        }
    }

    private JToolBar creatBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);


        JButton viewBut = new JButton("详情");
        viewBut.setForeground(UIManager.getColor("App.accent.color"));

        viewBut.setIcon(new FlatSVGIcon("icons/chakan.svg", 15, 15));
        viewBut.addActionListener(e -> showDetailsDialog());

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("App.danger.color"));
        optBar.add(Box.createGlue());
        optBar.add(viewBut);
        optBar.add(del);
        optBar.add(Box.createGlue());
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> updateData());
        clearBut.addActionListener(e -> clear());
    }


    private void showDetailsDialog() {


        int selRow = table.getSelectedRow();
        OperateLogRespVO logRespVO = null;
        if (selRow != -1) {
            logRespVO = (OperateLogRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill][grow,fill]",
                // rows
                "[][][][][][][][][][][][][]"));
        panel.setPreferredSize(new Dimension(450, 600));
        addMessageInfo("日志主键", logRespVO.getId(), panel, 0);
        addMessageInfo("链路追踪", logRespVO.getTraceId(), panel, 1);
        addMessageInfo("操作人编号", logRespVO.getUserId(), panel, 2);
        addMessageInfo("操作人名字", logRespVO.getUserName(), panel, 3);
        addMessageInfo("操作人 IP", logRespVO.getUserIp(), panel, 4);
        addMessageInfo("操作人 UA", logRespVO.getUserAgent(), panel, 5);
        addMessageInfo("操作模块", logRespVO.getType(), panel, 6);
        addMessageInfo("操作名", logRespVO.getSubType(), panel, 7);
        addMessageInfoArea("操作内容", logRespVO.getAction(), panel, 8);
        addMessageInfo("操作拓展参数", logRespVO.getExtra(), panel, 9);
        addMessageInfo("请求 URL", logRespVO.getRequestMethod() + " " + logRespVO.getRequestUrl(), panel, 10);
        addMessageInfo("操作时间", DateUtil.format(logRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"), panel, 11);
        addMessageInfo("业务编号", logRespVO.getBizId(), panel, 12);
        WOptionPane.showOptionDialog(null, panel, "详情", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");

    }

    private void addMessageInfo(String text, Object value, JPanel panel, int row) {
        JLabel label = new JLabel(text);
        JTextField textField = new JTextField(Convert.toStr(value));
        textField.setEditable(false);

        panel.add(label, "cell 0 " + row);
        panel.add(textField, "cell 1 " + row);
    }

    private void addMessageInfoArea(String text, Object value, JPanel panel, int row) {


        JLabel label = new JLabel(text);
        JTextArea textField = new JTextArea(value + "");
        textField.setEditable(false);
        textField.setLineWrap(true);

        panel.add(label, "cell 0 " + row);
        panel.add(new JScrollPane(textField), "cell 1 " + row + ",grow");
    }

    private void reset() {
        userNameTextField.setText("");
        typeTextField.setText("");
        actionTextField.setText("");
        bizIdTextField.setText("");
        subTypeTextField.setText("");
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
    }

    private void clear() {

        int opt = WOptionPane.showOptionDialog(this, "确定要清空所有操作日志吗？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);
        if (opt != 0) {
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(OperateLogApi.class).clearOperateLog().getCheckedData();
        }).thenAcceptAsync(result -> {
            updateData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });

    }


    private void del() {
        Long userId = null;
        String userName = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            userId = Convert.toLong(table.getValueAt(selRow, 0));
            userName = Convert.toStr(table.getValueAt(selRow, 3));
        }

        int opt = WOptionPane.showOptionDialog(this, "是否确定删除[" + userName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }
        Long finalUserId = userId;
        CompletableFuture.supplyAsync(() -> {
             return Forest.client(OperateLogApi.class).deleteOperateLog(finalUserId).getCheckedData();
        }).thenAcceptAsync(result -> {
            updateData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });


    }


    public void updateData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (StrUtil.isNotBlank(userNameTextField.getText())) {
            queryMap.put("userId", userNameTextField.getText());
        }
        if (StrUtil.isNotBlank(typeTextField.getText())) {
            queryMap.put("type", typeTextField.getText());
        }
        if (StrUtil.isNotBlank(subTypeTextField.getText())) {
            queryMap.put("subType", subTypeTextField.getText());
        }
        if (StrUtil.isNotBlank(actionTextField.getText())) {
            queryMap.put("action", actionTextField.getText());
        }
        if (StrUtil.isNotBlank(bizIdTextField.getText())) {
            queryMap.put("bizId", bizIdTextField.getText());
        }

        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            String[] dateTimes = new String[2];
            dateTimes[0] = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            dateTimes[1] = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("createTime", dateTimes);
        }

        queryMap.values().removeIf(Objects::isNull);
        CompletableFuture.supplyAsync(() -> {
            return Forest.client(OperateLogApi.class).pageOperateLog(queryMap).getCheckedData();
        }).thenAcceptAsync(result -> {
            Vector<Vector> tableData = new Vector<>();
            result.getList().forEach(roleRespVO -> {
                Vector rowV = new Vector();
                rowV.add(roleRespVO.getId());
                rowV.add(roleRespVO.getUserName());
                rowV.add(roleRespVO.getType());
                rowV.add(roleRespVO.getSubType());
                rowV.add(roleRespVO.getAction());
                rowV.add(DateUtil.format(roleRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                rowV.add(roleRespVO.getBizId());
                rowV.add(roleRespVO.getUserIp());
                rowV.add(roleRespVO);
                tableData.add(rowV);
            });
            tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
            table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
            table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

            paginationPane.setTotal(result.getTotal());

        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JTextField textField;
    private JScrollPane scrollPane1;
    private JPanel centerPane;
    private JScrollPane scrollPane2;
    private JTable table;
    private JPanel toolPane;
    private JLabel label7;
    private JTextField userNameTextField;
    private JLabel label8;
    private JTextField typeTextField;
    private JLabel label9;
    private JTextField subTypeTextField;
    private JLabel label12;
    private JTextField actionTextField;
    private JLabel label13;
    private JTextField bizIdTextField;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    private JButton clearBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
