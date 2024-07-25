/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.lw.swing.view.system.notice;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import com.lw.swing.components.*;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.swing.utils.BadgeLabelUtil;
import com.lw.ui.request.api.system.NotifyMessageFeign;
import com.lw.ui.request.api.system.OperateLogFeign;
import com.lw.ui.utils.DictTypeEnum;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.lw.ui.utils.DictTypeEnum.*;
import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class NotifyMessagePane extends JPanel {
    private String[] COLUMN_ID = {"编号", "用户类型", "用户编号", "模板编码", "发送人名称", "模版内容", "模版类型", "是否已读", "阅读时间", "创建时间", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public NotifyMessagePane() {
        initComponents();
        initListeners();
        updateData();
    }

    private void initComponents() {
        centerPane = new JPanel();
        scrollPane2 = new WScrollPane();
        table = new JXTable(tableModel = new DefaultTableModel());
        toolPane = new WPanel();
        label7 = new JLabel();
        userIdTextField = new JTextField(10);
        label8 = new JLabel();
        userTypeComboBox = new JComboBox();
        label9 = new JLabel();
        templateCodeTextField = new JTextField(10);
        label10 = new JLabel();
        startDateTextField = new WLocalDateCombo();
        label11 = new JLabel();
        endDateTextField = new WLocalDateCombo();
        label12 = new JLabel();
        templateTypeComboBox = new JComboBox();
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
                label7.setText("用户编号");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                toolPane.add(userIdTextField, "cell 0 0");

                //---- label8 ----
                label8.setText("用户类型");
                toolPane.add(label8, "cell 0 0");

                //---- phoneTextField ----
                toolPane.add(userTypeComboBox, "cell 0 0");

                //---- label9 ----
                label9.setText("模板编码");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(templateCodeTextField, "cell 0 0");

                //---- label12----
                label12.setText("模版类型");
                toolPane.add(label12, "cell 0 0");
                toolPane.add(templateTypeComboBox, "cell 0 0");

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
                clearBut.setText("清空");
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


        AppStore.getDictDataList(USER_TYPE).forEach(dictDataSimpleRespVO -> {
            userTypeComboBox.addItem(dictDataSimpleRespVO);
        });
        AppStore.getDictDataList(SYSTEM_NOTIFY_TEMPLATE_TYPE).forEach(dictDataSimpleRespVO -> {
            templateTypeComboBox.addItem(dictDataSimpleRespVO);
        });
        userTypeComboBox.setSelectedItem(null);
        templateTypeComboBox.setSelectedItem(null);

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
        viewBut.setForeground(UIManager.getColor("App.accentColor"));

        viewBut.setIcon(new FlatSVGIcon("icons/chakan.svg", 15, 15));
        viewBut.addActionListener(e -> showDetailsDialog());


        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("app-error-color-5"));

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

    private void reset() {
        userIdTextField.setText("");
        userTypeComboBox.setSelectedItem(null);
        templateTypeComboBox.setSelectedItem(null);
        templateCodeTextField.setText("");
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
    }

    private void showDetailsDialog() {


        int selRow = table.getSelectedRow();
        NotifyMessageRespVO noticeRespVO = null;
        if (selRow != -1) {
            noticeRespVO = (NotifyMessageRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill][grow,fill]",
                // rows
                "[][][][][][][][][][][][]"));
        panel.setPreferredSize(new Dimension(450,600));
        addMessageInfo("编号", noticeRespVO.getId(), panel, 0);
        addMessageInfo("用户类型", USER_TYPE,noticeRespVO.getUserType(), panel, 1);
        addMessageInfo("用户编号", noticeRespVO.getUserId(), panel, 2);
        addMessageInfo("模版编号", noticeRespVO.getTemplateId(), panel, 3);
        addMessageInfo("模板编码", noticeRespVO.getTemplateCode(), panel, 4);
        addMessageInfo("发送人名称", noticeRespVO.getTemplateNickname(), panel, 5);
        addMessageInfo("模版内容", noticeRespVO.getTemplateContent(), panel, 6);
        addMessageInfo("模版参数", noticeRespVO.getTemplateParams(), panel, 7);
        addMessageInfo("模版类型", SYSTEM_NOTIFY_TEMPLATE_TYPE,noticeRespVO.getTemplateType(), panel, 8);
        addMessageInfo("是否已读", INFRA_BOOLEAN_STRING,noticeRespVO.getReadStatus(), panel, 9);
        addMessageInfo("阅读时间", DateUtil.format(noticeRespVO.getReadTime(), "yyyy-MM-dd HH:mm:ss"), panel, 10);
        addMessageInfo("创建时间", DateUtil.format(noticeRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"), panel, 11);
        WOptionPane.showOptionDialog(null, panel, "详情", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");

    }

    private void addMessageInfo(String text, Object value, JPanel panel, int row) {
        JLabel label = new JLabel(text);
        JTextField textField = new JTextField(Convert.toStr(value));
        textField.setEditable(false);

        panel.add(label, "cell 0 " + row);
        panel.add(textField, "cell 1 " + row);
    }

    private void addMessageInfo(String text, DictTypeEnum dictType, Object value, JPanel panel, int row) {


        JLabel label = new JLabel(text);
        JLabel badge = BadgeLabelUtil.getBadgeLabel(dictType,value);


        panel.add(label, "cell 0 " + row);
        panel.add(badge, "cell 1 " + row+",alignx left,growx 0");
    }


    private void clear() {



    }


    private void del() {


    }


    public void updateData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        queryMap.put("userId", userIdTextField.getText());
        queryMap.put("templateCode", templateCodeTextField.getText());

        if (userTypeComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO userTypeComboBoxSelectedItem = (DictDataSimpleRespVO) userTypeComboBox.getSelectedItem();
            queryMap.put("userType", userTypeComboBoxSelectedItem.getValue());
        }
        if (templateTypeComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO templateTypeComboBoxSelectedItem = (DictDataSimpleRespVO) templateTypeComboBox.getSelectedItem();

            queryMap.put("templateType", templateTypeComboBoxSelectedItem.getValue());

        }


        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            String[] dateTimes = new String[2];
            dateTimes[0] = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            dateTimes[1] = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("createTime", dateTimes);
        }


        SwingWorker<Vector<Vector>, Long> swingWorker = new SwingWorker<Vector<Vector>, Long>() {
            @Override
            protected Vector<Vector> doInBackground() throws Exception {
                CommonResult<PageResult<NotifyMessageRespVO>> result = Request.connector(NotifyMessageFeign.class).getNotifyMessagePage(queryMap);

                Vector<Vector> tableData = new Vector<>();


                if (result.isSuccess()) {

                    result.getData().getList().forEach(roleRespVO -> {
                        Vector rowV = new Vector();
                        rowV.add(roleRespVO.getId());
                        rowV.add(roleRespVO.getUserType());
                        rowV.add(roleRespVO.getUserId());
                        rowV.add(roleRespVO.getTemplateCode());
                        rowV.add(roleRespVO.getTemplateNickname());
                        rowV.add(roleRespVO.getTemplateContent());
                        rowV.add(roleRespVO.getTemplateType());
                        rowV.add(roleRespVO.getReadStatus());
                        rowV.add(DateUtil.format(roleRespVO.getReadTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(DateUtil.format(roleRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(roleRespVO);
                        tableData.add(rowV);
                    });

                    publish(result.getData().getTotal());
                }
                return tableData;
            }


            @Override
            protected void process(List<Long> chunks) {
                chunks.forEach(total -> paginationPane.setTotal(total));
            }

            @Override
            protected void done() {
                try {


                    tableModel.setDataVector(get(), new Vector<>(Arrays.asList(COLUMN_ID)));
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

                    table.getColumn("是否已读").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = BadgeLabelUtil.getBadgeLabel(INFRA_BOOLEAN_STRING, value);
                            panel.add(label);
                            panel.setBackground(component.getBackground());
                            panel.setOpaque(isSelected);
                            return panel;
                        }
                    });

                    table.getColumn("用户类型").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = BadgeLabelUtil.getBadgeLabel(USER_TYPE, value);
                            panel.add(label);
                            panel.setBackground(component.getBackground());
                            panel.setOpaque(isSelected);
                            return panel;
                        }
                    });
                    table.getColumn("模版类型").setCellRenderer(new DefaultTableCellRenderer() {
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
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        swingWorker.execute();

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel centerPane;
    private JScrollPane scrollPane2;
    private JTable table;
    private JPanel toolPane;
    private JLabel label7;
    private JTextField userIdTextField;
    private JLabel label8;
    private JComboBox userTypeComboBox;
    private JLabel label9;
    private JTextField templateCodeTextField;
    private JLabel label12;
    private JComboBox templateTypeComboBox;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    private JButton clearBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
