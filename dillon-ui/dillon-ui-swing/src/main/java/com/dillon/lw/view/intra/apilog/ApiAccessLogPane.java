/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.intra.apilog;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.dillon.lw.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.components.*;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.http.PayLoad;
import com.dillon.lw.http.RetrofitServiceManager;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.BadgeLabelUtil;
import com.dillon.lw.api.infra.ApiAccessLogApi;
import com.dillon.lw.utils.DictTypeEnum;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

import static com.dillon.lw.utils.DictTypeEnum.INFRA_OPERATE_TYPE;
import static com.dillon.lw.utils.DictTypeEnum.USER_TYPE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

/**
 * @author wenli
 */
public class ApiAccessLogPane extends JPanel {
    private String[] COLUMN_ID = {"日志编号", "用户编号", "用户类型", "应用名", "请求方法", "请求地址", "请求时间", "执行时长", "操作结果", "操作模块", "操作名", "操作类型", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public ApiAccessLogPane() {
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
        durationField = new JTextField();
        resultCodeField = new JTextField();
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
                label9.setText("执行时长");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(durationField, "cell 0 0");
                toolPane.add(new JLabel("结果码"), "cell 0 0");
                resultCodeField.setColumns(15);
                toolPane.add(resultCodeField, "cell 0 0");


                //---- label10 ----
                label10.setText("请求时间");
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

        userTypeComboBox.setSelectedItem(null);
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


        optBar.add(Box.createGlue());
        optBar.add(viewBut);
        optBar.add(Box.createGlue());
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> updateData());
    }

    private void showDetailsDialog() {


        int selRow = table.getSelectedRow();
        ApiAccessLogRespVO respVO = null;
        if (selRow != -1) {
            respVO = (ApiAccessLogRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill][grow,fill]",
                // rows
                "[][][][][][][][][][][][][][][]"));
        panel.setPreferredSize(new Dimension(450, 600));
        addMessageInfo("日志主键", respVO.getId(), panel, 0);
        addMessageInfo("链路追踪", respVO.getTraceId(), panel, 1);
        addMessageInfo("应用名", respVO.getApplicationName(), panel, 2);
        addMessageInfo("用户信息", USER_TYPE, respVO.getUserType(), panel, 3);
        addMessageInfo("用户 IP", respVO.getUserIp(), panel, 4);
        addMessageInfo("用户 UA", respVO.getUserAgent(), panel, 5);
        addMessageInfo("请求信息", respVO.getRequestMethod() + " " + respVO.getRequestUrl(), panel, 6);
        addMessageInfo("请求参数", respVO.getRequestParams(), panel, 7);
        addMessageInfo("请求结果", respVO.getResponseBody(), panel, 8);
        addMessageInfo("请求时间", DateUtil.format(respVO.getBeginTime(), "yyyy-MM-dd HH:mm:ss") + " ~ " + DateUtil.format(respVO.getEndTime(), "yyyy-MM-dd HH:mm:ss"), panel, 9);
        addMessageInfo("请求耗时", respVO.getDuration() + " ms", panel, 10);
        addMessageInfoArea("操作结果", respVO.getResultCode() == 0 ? "正常" : "失败 | " + respVO.getResultCode() + " | " + respVO.getResultMsg(), panel, 11);
        addMessageInfo("操作模块", respVO.getOperateModule(), panel, 12);
        addMessageInfo("操作名", respVO.getOperateName(), panel, 13);
        addMessageInfo("操作名类型", INFRA_OPERATE_TYPE, respVO.getOperateType(), panel, 14);


        WOptionPane.showOptionDialog(null, panel, "详情", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");

    }

    private void addMessageInfoArea(String text, Object value, JPanel panel, int row) {


        JLabel label = new JLabel(text);
        JTextArea textField = new JTextArea(value + "");
        textField.setEditable(false);
        textField.setLineWrap(true);

        panel.add(label, "cell 0 " + row);
        panel.add(new JScrollPane(textField), "cell 1 " + row + ",grow");
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
        durationField.setText("");
        resultCodeField.setText("");
        applicationNameFiele.setText("");
        userTypeComboBox.setSelectedItem(null);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
        updateData();
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
        if (StrUtil.isNotBlank(durationField.getText())) {
            queryMap.put("duration", durationField.getText());
        }
        if (StrUtil.isNotBlank(resultCodeField.getText())) {
            queryMap.put("resultCode", resultCodeField.getText());
        }

        if (userTypeComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO selectedItem = (DictDataSimpleRespVO) userTypeComboBox.getSelectedItem();
            queryMap.put("userType", selectedItem.getValue());
        }


        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            String[] dateTimes = new String[2];
            dateTimes[0] = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            dateTimes[1] = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("beginTime", dateTimes);
        }
        queryMap.values().removeIf(Objects::isNull);


        RetrofitServiceManager.getInstance().create(ApiAccessLogApi.class).getApiAccessLogPage(queryMap)
                .map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    Vector<Vector> tableData = new Vector<>();

                    result.getList().forEach(respVO -> {
                        Vector rowV = new Vector();
                        rowV.add(respVO.getId());
                        rowV.add(respVO.getUserId());
                        rowV.add(respVO.getUserType());
                        rowV.add(respVO.getApplicationName());
                        rowV.add(respVO.getRequestMethod());
                        rowV.add(respVO.getRequestUrl());
                        rowV.add(DateUtil.format(respVO.getBeginTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(respVO.getDuration() + " ms");
                        rowV.add(respVO.getResultCode());
                        rowV.add(respVO.getOperateModule());
                        rowV.add(respVO.getOperateName());
                        rowV.add(respVO.getOperateType());
                        rowV.add(respVO);
                        tableData.add(rowV);
                    });
                    paginationPane.setTotal(result.getTotal());
                    tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
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
                    table.getColumn("操作类型").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = BadgeLabelUtil.getBadgeLabel(INFRA_OPERATE_TYPE, value);

                            panel.add(label);
                            panel.setBackground(component.getBackground());
                            panel.setOpaque(isSelected);
                            return panel;
                        }
                    });
                    table.getColumn("操作结果").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = null;
                            if (Convert.toInt(value, 1) == 0) {
                                label = BadgeLabelUtil.getBadgeLabel("成功", "primary");
                            } else {
                                label = BadgeLabelUtil.getBadgeLabel("失败", "danger");
                            }

                            panel.add(label);
                            panel.setBackground(component.getBackground());
                            panel.setOpaque(isSelected);
                            return panel;
                        }
                    });
                }, throwable -> {
                    throwable.printStackTrace();
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
    private JTextField userIdField;
    private JLabel label8;
    private JTextField applicationNameFiele;
    private JLabel label9;
    private JTextField durationField;
    private JTextField resultCodeField;
    private JComboBox userTypeComboBox;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
