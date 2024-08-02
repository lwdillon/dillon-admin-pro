/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.lw.swing.view.intra.apilog;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.swing.components.*;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.swing.utils.BadgeLabelUtil;
import com.lw.ui.request.api.apilog.ApiErrorLogFeign;
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

import static com.lw.ui.utils.DictTypeEnum.INFRA_API_ERROR_LOG_PROCESS_STATUS;
import static com.lw.ui.utils.DictTypeEnum.USER_TYPE;
import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class ApiErrorLogPane extends JPanel {
    private String[] COLUMN_ID = {"日志编号", "用户编号", "用户类型", "应用名", "请求方法", "请求地址", "异常发生时间", "异常名", "处理状态", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public ApiErrorLogPane() {
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
        userIdField = new JTextField();
        label8 = new JLabel();
        applicationNameFiele = new JTextField();
        label9 = new JLabel();
        processStatusComboBox = new JComboBox();
        userTypeComboBox = new JComboBox();
        label10 = new JLabel();
        startDateTextField = new WLocalDateCombo();
        label11 = new JLabel();
        endDateTextField = new WLocalDateCombo();
        searchBut = new JButton();
        reseBut = new JButton();

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
                userIdField.setColumns(15);
                toolPane.add(userIdField, "cell 0 0");

                toolPane.add(new JLabel("用户类型"), "cell 0 0");
                toolPane.add(userTypeComboBox, "cell 0 0");

                //---- label8 ----
                label8.setText("应用名");
                toolPane.add(label8, "cell 0 0");

                //---- phoneTextField ----
                applicationNameFiele.setColumns(15);
                toolPane.add(applicationNameFiele, "cell 0 0");


                //---- label9 ----
                label9.setText("处理状态");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(processStatusComboBox, "cell 0 0");

                //---- label10 ----
                label10.setText("异常时间");
                toolPane.add(label10, "cell 0 0");

                //---- startDateTextField ----
                toolPane.add(startDateTextField, "cell 0 0");

                //---- label11 ----
                label11.setText("-");
                toolPane.add(label11, "cell 0 0");

                //---- endDateTextField ----
                toolPane.add(endDateTextField, "cell 0 0");

                //---- button1 ----
                searchBut.setText("搜索");
                toolPane.add(searchBut, "cell 0 0");

                //---- reseBut ----
                reseBut.setText("重置");
                toolPane.add(reseBut, "cell 0 0");


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
        AppStore.getDictDataList(INFRA_API_ERROR_LOG_PROCESS_STATUS).forEach(dictDataSimpleRespVO -> {
            processStatusComboBox.addItem(dictDataSimpleRespVO);
        });
        userTypeComboBox.setSelectedItem(null);
        processStatusComboBox.setSelectedItem(null);
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

        JButton processedBut = new JButton("已处理");
        processedBut.addActionListener(e -> processed(1));
        processedBut.setForeground(UIManager.getColor("App.accentColor"));

        JButton ignoredBut = new JButton("已忽略");
        ignoredBut.addActionListener(e -> processed(2));
        ignoredBut.setForeground(UIManager.getColor("App.accentColor"));

        optBar.add(Box.createGlue());
        optBar.add(viewBut);
        optBar.add(processedBut);
        optBar.add(ignoredBut);
        optBar.add(Box.createGlue());
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> updateData());
    }

    private void showDetailsDialog() {


        int selRow = table.getSelectedRow();
        ApiErrorLogRespVO respVO = null;
        if (selRow != -1) {
            respVO = (ApiErrorLogRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill][grow,fill]",
                // rows
                "[][][][][][][]"));
        panel.setPreferredSize(new Dimension(450, 600));
        addMessageInfo("日志主键", respVO.getId(), panel, 0);
        addMessageInfo("链路追踪", respVO.getTraceId(), panel, 1);
        addMessageInfo("应用名", respVO.getApplicationName(), panel, 2);
        addMessageInfo("用户编号", respVO.getUserId(), panel, 3);
        addMessageInfo("用户 IP", respVO.getUserIp(), panel, 4);
        addMessageInfo("用户 UA", respVO.getUserAgent(), panel, 5);
        addMessageInfo("请求信息", respVO.getRequestMethod()+" "+respVO.getRequestUrl(), panel, 6);
        addMessageInfo("请求参数", respVO.getRequestParams(), panel, 7);
        addMessageInfo("异常时间", DateUtil.format(respVO.getExceptionTime(), "yyyy-MM-dd HH:mm:ss"), panel, 8);
        addMessageInfo("异常名", respVO.getExceptionName(), panel, 9);
        addMessageInfoArea("异常堆栈", respVO.getExceptionStackTrace(), panel, 10);
        addMessageInfo("处理状态", INFRA_API_ERROR_LOG_PROCESS_STATUS, respVO.getProcessStatus(), panel, 11);
        addMessageInfo("处理人", respVO.getProcessUserId(), panel, 12);
        addMessageInfo("处理时间", DateUtil.format(respVO.getProcessTime(), "yyyy-MM-dd HH:mm:ss"), panel, 13);


        WOptionPane.showOptionDialog(null, panel, "详情", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");

    }
    private void addMessageInfoArea(String text, Object value, JPanel panel, int row) {


        JLabel label = new JLabel(text);
        JTextArea textField = new JTextArea(value+"");
        textField.setEditable(false);
        textField.setLineWrap(true);

        panel.add(label, "cell 0 " + row);
        panel.add(new JScrollPane(textField), "cell 1 " + row+",grow");
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
        JLabel badge = BadgeLabelUtil.getBadgeLabel(dictType, value);


        panel.add(label, "cell 0 " + row);
        panel.add(badge, "cell 1 " + row + ",alignx left,growx 0");
    }

    private void reset() {
        userIdField.setText("");
        applicationNameFiele.setText("");
        processStatusComboBox.setSelectedItem(null);
        userTypeComboBox.setSelectedItem(null);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
        updateData();
    }


    private void processed(int processStatus) {
        Long userId = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            userId = Convert.toLong(table.getValueAt(selRow, 0));
        }

        int opt = WOptionPane.showOptionDialog(this, "确认标记为" + (processStatus == 1 ? "已处理" : "已忽略") + "？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }
        Long finalUserId = userId;
        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(ApiErrorLogFeign.class).updateApiErrorLogProcess(finalUserId, processStatus);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        updateData();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();

    }


    public void updateData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (StrUtil.isNotBlank(userIdField.getText())) {
            queryMap.put("userId", userIdField.getText());
        }
        if (StrUtil.isNotBlank(applicationNameFiele.getText())) {
            queryMap.put("applicationName", applicationNameFiele.getText());
        }

        if (userTypeComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO selectedItem = (DictDataSimpleRespVO) userTypeComboBox.getSelectedItem();
            queryMap.put("userType", selectedItem.getValue());
        }
        if (processStatusComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO selectedItem = (DictDataSimpleRespVO) processStatusComboBox.getSelectedItem();
            queryMap.put("processStatus", selectedItem.getValue());
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
                CommonResult<PageResult<ApiErrorLogRespVO>> result = Request.connector(ApiErrorLogFeign.class).getApiErrorLogPage(queryMap);

                Vector<Vector> tableData = new Vector<>();


                if (result.isSuccess()) {

                    result.getData().getList().forEach(respVO -> {
                        Vector rowV = new Vector();
                        rowV.add(respVO.getId());
                        rowV.add(respVO.getUserId());
                        rowV.add(respVO.getUserType());
                        rowV.add(respVO.getApplicationName());
                        rowV.add(respVO.getRequestMethod());
                        rowV.add(respVO.getRequestUrl());
                        rowV.add(DateUtil.format(respVO.getExceptionTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(respVO.getExceptionName());
                        rowV.add(respVO.getProcessStatus());
                        rowV.add(respVO);
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
                    table.getColumn("处理状态").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = BadgeLabelUtil.getBadgeLabel(INFRA_API_ERROR_LOG_PROCESS_STATUS, value);

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
    private JTextField textField;
    private JScrollPane scrollPane1;
    private JPanel centerPane;
    private JScrollPane scrollPane2;
    private JTable table;
    private JPanel toolPane;
    private JLabel label7;
    private JTextField userIdField;
    private JLabel label8;
    private JTextField applicationNameFiele;
    private JLabel label9;
    private JComboBox processStatusComboBox;
    private JComboBox userTypeComboBox;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
