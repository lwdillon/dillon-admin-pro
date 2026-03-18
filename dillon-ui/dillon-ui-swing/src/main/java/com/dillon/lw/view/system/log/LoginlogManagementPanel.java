/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.system.log;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.system.LoginLogApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.dillon.lw.utils.BadgeLabelUtil;
import com.dillon.lw.utils.DictTypeEnum;
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

import static com.dillon.lw.utils.DictTypeEnum.SYSTEM_LOGIN_RESULT;
import static com.dillon.lw.utils.DictTypeEnum.SYSTEM_LOGIN_TYPE;
import static javax.swing.JOptionPane.*;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.swing.rx.SwingRx;

/**
 * @author wenli
 */
public class LoginlogManagementPanel extends com.dillon.lw.components.AbstractDisposablePanel {
    private static final String[] COLUMN_ID = {"日志编号", "操作类型", "用户名称", "登录地址", "浏览器", "登录结果", "登录日期", "操作"};
    private static final int COL_LOG_ID = 0;
    private static final int COL_LOG_OBJECT = COLUMN_ID.length - 1;
    private static final String WARN_SELECT_LOG_FIRST = "请先选择登录日志！";

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public LoginlogManagementPanel() {
        initComponents();
        initListeners();
        updateData();
    }

    private void initComponents() {
        textField = new JTextField();
        scrollPane1 = new WScrollPane();
        centerPane = new JPanel();
        scrollPane2 = new WScrollPane();
        table = new JXTable(tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return "操作".equals(getColumnName(column));
            }
        });
        toolPane = new WPanel();
        label7 = new JLabel();
        nameTextField = new JTextField();
        label8 = new JLabel();
        ipTextField = new JTextField();
        label9 = new JLabel();
        stautsComboBox = new JComboBox();
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
                label7.setText("用户名称");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                nameTextField.setColumns(15);
                toolPane.add(nameTextField, "cell 0 0");

                //---- label8 ----
                label8.setText("登录地址");
                toolPane.add(label8, "cell 0 0");

                //---- phoneTextField ----
                ipTextField.setColumns(15);
                toolPane.add(ipTextField, "cell 0 0");

                //---- label9 ----
                label9.setText("状态");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(stautsComboBox, "cell 0 0");

                //---- label10 ----
                label10.setText("登录时间");
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

                clearBut = new JButton("清空");
                clearBut.setBackground(UIManager.getColor("app-error-color-7"));
                toolPane.add(clearBut, "cell 0 0");


            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);


        stautsComboBox.addItem("全部");
        stautsComboBox.addItem("成功");
        stautsComboBox.addItem("失败");

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

    /**
     * @deprecated 历史命名，建议改用 {@link #createActionBar()}。
     */
    @Deprecated
    private JToolBar creatBar() {
        return createActionBar();
    }

    /**
     * 创建“操作列”工具栏。
     */
    private JToolBar createActionBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);
        JButton viewBut = new JButton("详情");
        viewBut.setForeground(UIManager.getColor("App.accent.color"));
        viewBut.setIcon(new FlatSVGIcon("icons/chakan.svg", 15, 15));
        viewBut.addActionListener(e -> showDetailsDialog());

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> delMenu());
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
        clearBut.addActionListener(e -> clearLoginLog());
    }

    private void showDetailsDialog() {
        LoginLogRespVO logRespVO = getSelectedLog();
        if (logRespVO == null) {
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill][grow,fill]",
                // rows
                "[][][][][][][]"));
        panel.setPreferredSize(new Dimension(450, 300));
        addMessageInfo("日志编号", logRespVO.getId(), panel, 0);
        addMessageInfo("操作类型", SYSTEM_LOGIN_TYPE, logRespVO.getLogType(), panel, 1);
        addMessageInfo("用户名称", logRespVO.getUsername(), panel, 2);
        addMessageInfo("登录地址", logRespVO.getUserIp(), panel, 3);
        addMessageInfo("浏览器", logRespVO.getUserAgent(), panel, 4);
        addMessageInfo("登陆结果", SYSTEM_LOGIN_RESULT, logRespVO.getResult(), panel, 5);
        addMessageInfo("登录日期", DateUtil.format(logRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"), panel, 6);
        JOptionPane.showOptionDialog(null, panel, "详情", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");

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
        nameTextField.setText("");
        ipTextField.setText("");
        stautsComboBox.setSelectedIndex(0);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
    }


    private void delMenu() {
        Long logId = getSelectedLogId();
        String userName = getSelectedUsername();
        if (logId == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_LOG_FIRST);
            return;
        }

        int opt = JOptionPane.showOptionDialog(this, "是否确定删除[" + userName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        Single
                /*
                 * 同步接口先通过 fromCallable 包装成懒执行的 RxJava 任务，
                 * 请求放到 IO 线程执行，成功结果再切回 EDT 更新 Swing 组件。
                 */
                .fromCallable(() -> Forest.client(LoginLogApi.class).deleteLoginLog(logId).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> updateData(), SwingExceptionHandler::handle);


    }

    private void clearLoginLog() {

        int opt = JOptionPane.showOptionDialog(this, "确定要清空所有登录日志吗？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }
        Single
                /*
                 * 同步接口先通过 fromCallable 包装成懒执行的 RxJava 任务，
                 * 请求放到 IO 线程执行，成功结果再切回 EDT 更新 Swing 组件。
                 */
                .fromCallable(() -> Forest.client(LoginLogApi.class).clearLoginLog().getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> updateData(), SwingExceptionHandler::handle);


    }


    public void updateData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (StrUtil.isNotBlank(nameTextField.getText())) {
            queryMap.put("username", nameTextField.getText());
        }
        if (StrUtil.isNotBlank(ipTextField.getText())) {
            queryMap.put("userIp", ipTextField.getText());
        }
        queryMap.put("status", stautsComboBox.getSelectedIndex() == 0 ? null : (stautsComboBox.getSelectedIndex() == 1 ? true : false));

        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            java.lang.String sd = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            java.lang.String ed = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("createTime", ListUtil.of(sd, ed));
        }
        queryMap.values().removeIf(Objects::isNull);
        Single
                /*
                 * 同步接口先通过 fromCallable 包装成懒执行的 RxJava 任务，
                 * 请求放到 IO 线程执行，成功结果再切回 EDT 更新 Swing 组件。
                 */
                .fromCallable(() -> Forest.client(LoginLogApi.class).getLoginLogPage(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .doOnSubscribe(disposable -> {
                    /*
                     * 日志查询期间先禁用搜索按钮，避免重复点击叠加多个相同请求。
                     * doOnSubscribe 的线程不固定，因此通过 EDT 再去修改 Swing 按钮状态。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(false));
                })
                .doFinally(() -> {
                    /*
                     * 查询完成后无条件恢复按钮，确保异常场景下页面也能继续操作。
                     * doFinally 可能跑在后台线程，所以恢复动作同样放到 EDT。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(true));
                })
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    Vector<Vector> tableData = new Vector<>();
                    result.getList().forEach(roleRespVO -> {
                        Vector rowV = new Vector();
                        rowV.add(roleRespVO.getId());
                        rowV.add(roleRespVO.getLogType());
                        rowV.add(roleRespVO.getUsername());
                        rowV.add(roleRespVO.getUserIp());
                        rowV.add(roleRespVO.getUserAgent());
                        rowV.add(roleRespVO.getResult());
                        rowV.add(DateUtil.format(roleRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(roleRespVO);
                        tableData.add(rowV);
                    });

                    tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(createActionBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(createActionBar()));

                    table.getColumn("登录结果").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = BadgeLabelUtil.getBadgeLabel(SYSTEM_LOGIN_RESULT, value);

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
                            JLabel label = BadgeLabelUtil.getBadgeLabel(SYSTEM_LOGIN_TYPE, value);

                            panel.add(label);
                            panel.setBackground(component.getBackground());
                            panel.setOpaque(isSelected);
                            return panel;
                        }
                    });

                    paginationPane.setTotal(result.getTotal());
                }, SwingExceptionHandler::handle);


    }

    private LoginLogRespVO getSelectedLog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_LOG_FIRST);
            return null;
        }
        return (LoginLogRespVO) table.getValueAt(selectedRow, COL_LOG_OBJECT);
    }

    private Long getSelectedLogId() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return Convert.toLong(table.getValueAt(selectedRow, COL_LOG_ID));
    }

    private String getSelectedUsername() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return Convert.toStr(table.getValueAt(selectedRow, 2));
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JTextField textField;
    private JScrollPane scrollPane1;
    private JPanel centerPane;
    private JScrollPane scrollPane2;
    private JXTable table;
    private JPanel toolPane;
    private JLabel label7;
    private JTextField nameTextField;
    private JLabel label8;
    private JTextField ipTextField;
    private JLabel label9;
    private JComboBox stautsComboBox;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    private JButton clearBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
