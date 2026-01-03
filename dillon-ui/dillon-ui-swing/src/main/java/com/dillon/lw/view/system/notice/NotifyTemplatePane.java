/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.system.notice;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.api.system.NotifyTemplateApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO;
import com.dillon.lw.module.system.controller.admin.notify.vo.template.NotifyTemplateSendReqVO;
import com.dillon.lw.utils.BadgeLabelUtil;
import com.dillon.lw.view.frame.MainFrame;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.dillon.lw.utils.DictTypeEnum.COMMON_STATUS;
import static com.dillon.lw.utils.DictTypeEnum.SYSTEM_NOTIFY_TEMPLATE_TYPE;
import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class NotifyTemplatePane extends JPanel {
    private final static String[] COLUMN_ID = {"模板编码", "模板名称", "类型", "发送人名称", "模板内容", "开启状态", "备注", "创建时间", "操作"};


    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public NotifyTemplatePane() {
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
        label8 = new JLabel();
        nameTextField = new JTextField();
        codeTextField = new JTextField();
        label9 = new JLabel();
        stautsComboBox = new JComboBox();
        searchBut = new JButton();
        reseBut = new JButton();
        newBut = new JButton();

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
                label7.setText("模板名称");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                nameTextField.setColumns(15);
                toolPane.add(nameTextField, "cell 0 0");

                //---- label7 ----
                label8.setText("模板编号");
                toolPane.add(label8, "cell 0 0");

                //---- userNameTextField ----
                codeTextField.setColumns(15);
                toolPane.add(codeTextField, "cell 0 0");


                //---- label9 ----
                label9.setText("公告状态");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(stautsComboBox, "cell 0 0");

                //---- button1 ----
                searchBut.setText("\u641c\u7d22");
                toolPane.add(searchBut, "cell 0 0");

                //---- reseBut ----
                reseBut.setText("\u91cd\u7f6e");
                toolPane.add(reseBut, "cell 0 0");

                //---- newBut ----
                newBut.setText("\u65b0\u589e");
                toolPane.add(newBut, "cell 0 0");
            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);


        stautsComboBox.addItem("全部");
        stautsComboBox.addItem("开启");
        stautsComboBox.addItem("关闭");
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
        JButton edit = new JButton("修改");
        edit.setForeground(UIManager.getColor("App.accent.color"));
        edit.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));
        edit.addActionListener(e -> showEditDialog());

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("App.danger.color"));
        JButton push = new JButton("推送");
        push.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        push.addActionListener(e -> showSendDialog());
        push.setForeground(UIManager.getColor("App.accent.color"));


        optBar.add(Box.createGlue());
        optBar.add(edit);
        optBar.add(del);
        optBar.add(push);
        optBar.add(Box.createGlue());
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> updateData());
        newBut.addActionListener(e -> showAddDialog(null));
    }

    private void reset() {
        nameTextField.setText("");
        stautsComboBox.setSelectedIndex(0);
    }

    private void showAddDialog(Long id) {
        NotifyTemplateFromPane noticeFormPane = new NotifyTemplateFromPane();
        noticeFormPane.updateData(new NotifyTemplateRespVO());
        int opt = WOptionPane.showOptionDialog(null, noticeFormPane, "添加", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            add(noticeFormPane.getValue());
        }
    }

    private void showEditDialog() {


        int selRow = table.getSelectedRow();
        NotifyTemplateRespVO noticeRespVO = null;
        if (selRow != -1) {
            noticeRespVO = (NotifyTemplateRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }

        NotifyTemplateFromPane noticeFormPane = new NotifyTemplateFromPane();
        noticeFormPane.updateData(noticeRespVO);
        int opt = WOptionPane.showOptionDialog(null, noticeFormPane, "修改", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            edit(noticeFormPane.getValue());
        }
    }


    private void showSendDialog() {


        int selRow = table.getSelectedRow();
        NotifyTemplateRespVO noticeRespVO = null;
        if (selRow != -1) {
            noticeRespVO = (NotifyTemplateRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }

        NotifyTemplateSendPane noticeFormPane = new NotifyTemplateSendPane();
        noticeFormPane.updateData(noticeRespVO);
        int opt = WOptionPane.showOptionDialog(null, noticeFormPane, "消息发送", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            send(noticeFormPane.getValue());
        }
    }


    private void add(NotifyTemplateSaveReqVO saveReqVO) {
        CompletableFuture.supplyAsync(() -> {
            return Forest.client(NotifyTemplateApi.class).createNotifyTemplate(saveReqVO).getCheckedData();
        }).thenAcceptAsync(result -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "添加成功！");
            updateData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    private void edit(NotifyTemplateSaveReqVO saveReqVO) {
        CompletableFuture.supplyAsync(() -> {
            return Forest.client(NotifyTemplateApi.class).updateNotifyTemplate(saveReqVO).getCheckedData();
        }).thenAcceptAsync(result -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功！");
            updateData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    private void del() {
        Long id = null;
        String name = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
            name = Convert.toStr(table.getValueAt(selRow, 1));
        }

        int opt = WOptionPane.showOptionDialog(this, "是否确定删除[" + name + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        Long finalId = id;
        CompletableFuture.supplyAsync(() -> {
            return Forest.client(NotifyTemplateApi.class).deleteNotifyTemplate(finalId).getCheckedData();
        }).thenAcceptAsync(result -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");
            updateData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    private void send(NotifyTemplateSendReqVO sendReqVO) {
        CompletableFuture.supplyAsync(() -> {
            return Forest.client(NotifyTemplateApi.class).sendNotify(sendReqVO).getCheckedData();
        }).thenAcceptAsync(result -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "发送成功！");
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

        if (StrUtil.isNotBlank(codeTextField.getText())) {
            queryMap.put("code", codeTextField.getText());
        }
        if (StrUtil.isNotBlank(nameTextField.getText())) {
            queryMap.put("name", nameTextField.getText());
        }

        queryMap.put("status", stautsComboBox.getSelectedIndex() == 0 ? null : (stautsComboBox.getSelectedIndex() == 1 ? 0 : 1));
        queryMap.values().removeIf(Objects::isNull);

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(NotifyTemplateApi.class).getNotifyTemplatePage(queryMap).getCheckedData();
        }).thenAcceptAsync(result -> {
            Vector<Vector> tableData = new Vector<>();

            result.getList().forEach(respVO -> {
                Vector rowV = new Vector();
                rowV.add(respVO.getId());
                rowV.add(respVO.getName());
                rowV.add(respVO.getType());
                rowV.add(respVO.getNickname());
                rowV.add(respVO.getContent());
                rowV.add(respVO.getStatus());
                rowV.add(respVO.getRemark());
                rowV.add(DateUtil.format(respVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                rowV.add(respVO);
                tableData.add(rowV);
            });
            tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
            table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
            table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

            table.getColumn("开启状态").setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                    JLabel label = BadgeLabelUtil.getBadgeLabel(COMMON_STATUS, value);
                    panel.add(label);
                    panel.setBackground(component.getBackground());
                    panel.setOpaque(isSelected);
                    return panel;
                }
            });
            table.getColumn("类型").setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                    JLabel label = BadgeLabelUtil.getBadgeLabel(SYSTEM_NOTIFY_TEMPLATE_TYPE, value);
                    panel.add(label);
                    panel.setBackground(component.getBackground());
                    panel.setOpaque(isSelected);
                    return panel;
                }
            });
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
    private JLabel label8;
    private JTextField nameTextField;
    private JTextField codeTextField;
    private JLabel label9;
    private JComboBox stautsComboBox;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
