/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.lw.swing.view.intra.job;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.swing.components.*;
import com.lw.swing.components.notice.WMessage;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.swing.utils.BadgeLabelUtil;
import com.lw.swing.view.MainFrame;
import com.lw.ui.request.api.job.JobFeign;
import com.lw.ui.utils.DictTypeEnum;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.lw.ui.utils.DictTypeEnum.INFRA_JOB_STATUS;
import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class JobPane extends JPanel {
    private String[] COLUMN_ID = {"任务编号", "任务名称", "任务状态", "处理器的名字", "处理器的参数", "CRON 表达式", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public JobPane() {
        initComponents();
        initListeners();
        loadTableData();
    }

    private void initComponents() {
        textField = new JTextField();
        scrollPane1 = new WScrollPane();
        centerPane = new JPanel();
        scrollPane2 = new WScrollPane();
        table = new JXTable(tableModel = new DefaultTableModel());
        toolPane = new WPanel();
        label7 = new JLabel();
        nameField = new JTextField();
        label9 = new JLabel();
        handlerNameField = new JTextField();
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
                    loadTableData();
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
                label7.setText("任务名称");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                nameField.setColumns(15);
                toolPane.add(nameField, "cell 0 0");

                //---- label7 ----
                toolPane.add(new JLabel("任务状态"), "cell 0 0");
                toolPane.add(statusComboBox = new JComboBox(), "cell 0 0");

                //---- label9 ----
                label9.setText("处理器的名字");
                toolPane.add(label9, "cell 0 0");
                handlerNameField.setColumns(15);
                toolPane.add(handlerNameField, "cell 0 0");

                //---- button1 ----
                searchBut.setText("搜索");
                toolPane.add(searchBut, "cell 0 0");

                //---- reseBut ----
                reseBut.setText("重置");
                toolPane.add(reseBut, "cell 0 0");

                //---- newBut ----
                newBut.setText("新增");
                toolPane.add(newBut, "cell 0 0");

                toolPane.add(logBut = new JButton("调度日志"), "cell 0 0");
            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);

        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());


        AppStore.getDictDataList(INFRA_JOB_STATUS).forEach(dictDataSimpleRespVO -> {
            statusComboBox.addItem(dictDataSimpleRespVO);
        });
        statusComboBox.setSelectedItem(null);
    }

    private JToolBar creatBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("app-error-color-5"));

        JButton editBut = new JButton("修改");
        editBut.setForeground(UIManager.getColor("App.accentColor"));
        editBut.addActionListener(e -> showEditDialog());
        JButton statusBut = new JButton("开启/暂停");
        statusBut.setForeground(UIManager.getColor("App.accentColor"));
        statusBut.addActionListener(e -> showOnDialog());
        JButton executeBut = new JButton("执行一次");
        executeBut.setForeground(UIManager.getColor("App.accentColor"));
        executeBut.addActionListener(e -> triggerJob());
        JButton detailBut = new JButton("任务详细");
        detailBut.setForeground(UIManager.getColor("App.accentColor"));
        detailBut.addActionListener(e -> showDetailsDialog());
        JButton logBut = new JButton("调度日志");
        logBut.addActionListener(e -> showJobLog());
        logBut.setForeground(UIManager.getColor("App.accentColor"));

        optBar.add(Box.createGlue());
        optBar.add(editBut);
        optBar.add(statusBut);
        optBar.add(executeBut);
        optBar.add(detailBut);
        optBar.add(logBut);
        optBar.add(del);
        optBar.add(Box.createGlue());
        return optBar;
    }


    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> loadTableData());
        newBut.addActionListener(e -> showAddDialog(null));
        logBut.addActionListener(e -> showJobLogTab(new JobRespVO()));
    }

    private void reset() {
        nameField.setText("");
        handlerNameField.setText(null);
        statusComboBox.setSelectedItem(null);
        loadTableData();
    }

    private void showEditDialog() {


        int selRow = table.getSelectedRow();
        Long id = null;
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
        }

        JobFormPane roleEditPane = new JobFormPane();
        roleEditPane.updateData(id);
        int opt = WOptionPane.showOptionDialog(null, roleEditPane, "修改", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            edit(roleEditPane.getValue());
        }
    }

    private void showAddDialog(Long id) {
        JobFormPane formPane = new JobFormPane();
        formPane.updateData(id);
        int opt = WOptionPane.showOptionDialog(null, formPane, "添加", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            add(formPane.getValue());
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (table != null) {
            table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        }
    }

    /**
     * 添加
     */
    private void add(JobSaveReqVO saveReqVO) {

        SwingWorker<CommonResult<Long>, Object> swingWorker = new SwingWorker<CommonResult<Long>, Object>() {
            @Override
            protected CommonResult<Long> doInBackground() throws Exception {
                return Request.connector(JobFeign.class).createJob(saveReqVO);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "添加成功！");
                        loadTableData();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();

    }

    private void edit(JobSaveReqVO saveReqVO) {


        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(JobFeign.class).updateJob(saveReqVO);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功！");
                        loadTableData();
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


    private void triggerJob() {
        Long id = null;

        int selRow = table.getSelectedRow();
        JobRespVO jobRespVO = null;
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
            jobRespVO = (JobRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }

        int opt = WOptionPane.showOptionDialog(this, "是否要执行[" + jobRespVO.getHandlerName() + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }
        Long finalId = id;
        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(JobFeign.class).triggerJob(finalId);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "执行成功！");
                        loadTableData();
                    }
                } catch (Exception e) {

                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();
    }

    private void showOnDialog() {
        Long id = null;
        JobRespVO jobRespVO = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
            jobRespVO = (JobRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }
        String text = (jobRespVO.getStatus() == 2) ? "开启" : "暂停";

        int opt = WOptionPane.showOptionDialog(this, "是否" + text + "[" + jobRespVO.getName() + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }
        Long finalId = id;
        int status = jobRespVO.getStatus() == 2 ? 1 : 2;
        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(JobFeign.class).updateJobStatus(finalId, status);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), text + "成功！");
                        loadTableData();
                    }
                } catch (Exception e) {

                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();
    }

    private void del() {
        Long id = null;
        String userName = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
            userName = Convert.toStr(table.getValueAt(selRow, 1));
        }

        int opt = WOptionPane.showOptionDialog(this, "是否确定删除[" + userName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }
        Long finalId = id;
        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(JobFeign.class).deleteJob(finalId);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");
                        loadTableData();
                    }
                } catch (Exception e) {

                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();

    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        String name = null;
        String handlerName = null;
        if (StrUtil.isNotBlank(nameField.getText())) {
            name = nameField.getText();
        }
        if (StrUtil.isNotBlank(handlerNameField.getText())) {
            handlerName = handlerNameField.getText();
        }

        Integer status = null;
        if (statusComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO dictDataSimpleRespVO = (DictDataSimpleRespVO) statusComboBox.getSelectedItem();
            status = Convert.toInt(dictDataSimpleRespVO.getValue(), null);
        }
        queryMap.put("name", name);
        queryMap.put("handlerName", handlerName);
        queryMap.put("status", status);

        SwingWorker<Vector<Vector>, Long> swingWorker = new SwingWorker<Vector<Vector>, Long>() {
            @Override
            protected Vector<Vector> doInBackground() throws Exception {
                CommonResult<PageResult<JobRespVO>> result = Request.connector(JobFeign.class).getJobPage(queryMap);

                Vector<Vector> tableData = new Vector<Vector>();


                if (result.isSuccess()) {

                    result.getData().getList().forEach(respVO -> {
                        Vector rowV = new Vector();
                        rowV.add(respVO.getId());
                        rowV.add(respVO.getName());
                        rowV.add(respVO.getStatus());
                        rowV.add(respVO.getHandlerName());
                        rowV.add(respVO.getHandlerParam());
                        rowV.add(respVO.getCronExpression());
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
                    table.getColumn("操作").setMinWidth(240);
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

                    table.getColumn("任务状态").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = BadgeLabelUtil.getBadgeLabel(INFRA_JOB_STATUS, value);
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

    private void showDetailsDialog() {
        int selRow = table.getSelectedRow();
        JobRespVO respVO = null;
        if (selRow != -1) {
            respVO = (JobRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }


        JobRespVO finalRespVO = respVO;
        SwingWorker<DefaultListModel, Long> swingWorker = new SwingWorker<DefaultListModel, Long>() {
            @Override
            protected DefaultListModel doInBackground() throws Exception {
                CommonResult<List<LocalDateTime>> result = Request.connector(JobFeign.class).getJobNextTimes(finalRespVO.getId(), 5);

                DefaultListModel listModel = new DefaultListModel();
                if (result.isSuccess()) {

                    int index = 1;
                    for (LocalDateTime dateTime : result.getData()) {
                        listModel.addElement("第" + index + "次: " + DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss"));
                        index++;
                    }

                }
                return listModel;
            }


            @Override
            protected void process(List<Long> chunks) {
                chunks.forEach(total -> paginationPane.setTotal(total));
            }

            @Override
            protected void done() {
                try {

                    JList<String> list = new JList<>();
                    list.setModel(get());
                    JPanel panel = new JPanel();
                    panel.setLayout(new MigLayout(
                            "fill,insets 0,hidemode 3",
                            // columns
                            "[fill][grow,fill]",
                            // rows
                            "[][][][][][][][][][]"));
                    panel.setPreferredSize(new Dimension(450, 600));
                    addMessageInfo("任务编号", finalRespVO.getId(), panel, 0);
                    addMessageInfo("任务名称", finalRespVO.getName(), panel, 1);
                    addMessageInfo("任务状态", INFRA_JOB_STATUS, finalRespVO.getStatus(), panel, 2);
                    addMessageInfo("处理器的名字", finalRespVO.getHandlerName(), panel, 3);
                    addMessageInfo("处理器的参数", finalRespVO.getHandlerParam(), panel, 4);
                    addMessageInfo("Cron 表达式", finalRespVO.getCronExpression(), panel, 5);
                    addMessageInfo("重试次数", finalRespVO.getRetryCount(), panel, 6);
                    addMessageInfo("重试间隔", finalRespVO.getRetryInterval(), panel, 7);
                    addMessageInfo("监控超时时间", finalRespVO.getMonitorTimeout(), panel, 8);
                    panel.add(new JLabel("后续执行时间"), "cell 0 9");
                    panel.add(list, "cell 1 9,growy 0");
                    WOptionPane.showOptionDialog(null, panel, "任务详细", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        };
        swingWorker.execute();


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

    private void showJobLog() {

        int selRow = table.getSelectedRow();
        JobRespVO respVO = null;
        if (selRow != -1) {
            respVO = (JobRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
            showJobLogTab(respVO);
        }

    }

    private void showJobLogTab(JobRespVO respVO) {
        int tabIndex = MainFrame.getInstance().getTabbedPane().indexOfTab("调度日志");
        JobLogPane jobLogPane;
        if (tabIndex == -1) {
            jobLogPane = new JobLogPane();
            MainFrame.getInstance().getTabbedPane().addTab("调度日志", jobLogPane);
        } else {
            jobLogPane = (JobLogPane) MainFrame.getInstance().getTabbedPane().getComponentAt(tabIndex);
        }
        MainFrame.getInstance().getTabbedPane().setSelectedIndex(MainFrame.getInstance().getTabbedPane().indexOfTab("调度日志"));
        jobLogPane.loadTableData(respVO.getHandlerName());

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
    private JTextField nameField;
    private JLabel label9;
    private JTextField handlerNameField;
    private JComboBox statusComboBox;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    private JButton logBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
