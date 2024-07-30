/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.lw.swing.view.intra.job;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.log.JobLogRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.swing.components.*;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.swing.utils.BadgeLabelUtil;
import com.lw.ui.request.api.job.JobLogFeign;
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
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

/**
 * @author wenli
 */
public class JobLogPane extends JPanel {
    private String[] COLUMN_ID = {"日志编号", "任务编号", "处理器的名字", "处理器的参数", "第几次执行", "执行时间", "执行时长", "任务状态", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public JobLogPane() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        textField = new JTextField();
        scrollPane1 = new WScrollPane();
        centerPane = new JPanel();
        scrollPane2 = new WScrollPane();
        table = new JXTable(tableModel = new DefaultTableModel());
        toolPane = new WPanel();
        label7 = new JLabel();
        handlerNameField = new JTextField();
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
                    loadTableData("");
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
                label7.setText("处理器的名字");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                handlerNameField.setColumns(15);
                toolPane.add(handlerNameField, "cell 0 0");

                //---- label9 ----
                label9.setText("任务状态");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(stautsComboBox, "cell 0 0");

                //---- label10 ----
                label10.setText("执行时间");
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

            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);

        AppStore.getDictDataList(INFRA_JOB_STATUS).forEach(dictDataSimpleRespVO -> {
            stautsComboBox.addItem(dictDataSimpleRespVO);
        });

        stautsComboBox.setSelectedItem(null);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);


        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());

        centerPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    }

    private JToolBar creatBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);
        JButton detailsBut = new JButton("详细");
        detailsBut.setForeground(UIManager.getColor("App.accentColor"));
        detailsBut.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));
        detailsBut.addActionListener(e -> showDetails());
        detailsBut.setForeground(UIManager.getColor("App.accentColor"));

        optBar.add(Box.createGlue());
        optBar.add(detailsBut);
        optBar.add(Box.createGlue());
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> loadTableData(""));
    }

    private void showDetails() {

        int selRow = table.getSelectedRow();
        JobLogRespVO finalRespVO = null;
        if (selRow != -1) {
            finalRespVO = (JobLogRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill][grow,fill]",
                // rows
                "[][][][][][][][]"));
        panel.setPreferredSize(new Dimension(450, 500));
        addMessageInfo("日志编号", finalRespVO.getId(), panel, 0);
        addMessageInfo("任务编号", finalRespVO.getJobId(), panel, 1);
        addMessageInfo("处理器的名字", finalRespVO.getHandlerName(), panel, 2);
        addMessageInfo("处理器的参数", finalRespVO.getHandlerParam(), panel, 3);
        addMessageInfo("第几次执行", finalRespVO.getExecuteIndex(), panel, 4);
        addMessageInfo("执行时间", DateUtil.format(Convert.toLocalDateTime(finalRespVO.getBeginTime()), "yyyy-MM-dd HH:mm:ss") + " ~ " + DateUtil.format(Convert.toLocalDateTime(finalRespVO.getEndTime()), "yyyy-MM-dd HH:mm:ss"), panel, 5);
        addMessageInfo("执行时长", finalRespVO.getDuration() + " 毫秒", panel, 6);
        addMessageInfo("任务状态", INFRA_JOB_LOG_STATUS, finalRespVO.getStatus(), panel, 7);
        addMessageInfo("执行结果", finalRespVO.getResult(), panel, 8);
        WOptionPane.showOptionDialog(null, panel, "任务详细", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");

    }

    private void reset() {
        handlerNameField.setText("");
        stautsComboBox.setSelectedItem(null);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
        loadTableData("");
    }


    private void addMessageInfo(String text, Object value, JPanel panel, int row) {
        JLabel label = new JLabel(text,JLabel.RIGHT);
        JTextField textField = new JTextField(Convert.toStr(value));
        textField.setEditable(false);

        panel.add(label, "cell 0 " + row);
        panel.add(textField, "cell 1 " + row);
    }

    private void addMessageInfo(String text, DictTypeEnum dictType, Object value, JPanel panel, int row) {


        JLabel label = new JLabel(text,JLabel.RIGHT);
        JLabel badge = BadgeLabelUtil.getBadgeLabel(dictType, value);


        panel.add(label, "cell 0 " + row);
        panel.add(badge, "cell 1 " + row + ",alignx left,growx 0");
    }


    @Override
    public void updateUI() {
        super.updateUI();
        if (table != null) {
            table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        }
    }


    public void loadTableData(String handlerName) {
        handlerNameField.setText(handlerName);
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (StrUtil.isNotBlank(handlerNameField.getText())) {
            handlerName = handlerNameField.getText();
        }

        Integer stauts = null;
        if (stautsComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO dictDataSimpleRespVO = (DictDataSimpleRespVO) stautsComboBox.getSelectedItem();
            stauts = Convert.toInt(dictDataSimpleRespVO.getValue(), null);
        }


        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            String[] dateTimes = new String[2];
            dateTimes[0] = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            dateTimes[1] = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("beginTime", dateTimes[0]);
            queryMap.put("endTime", dateTimes[1]);
        }

        queryMap.put("handlerName", handlerName);
        queryMap.put("stauts", stauts);


        SwingWorker<Vector<Vector>, Long> swingWorker = new SwingWorker<Vector<Vector>, Long>() {
            @Override
            protected Vector<Vector> doInBackground() throws Exception {
                CommonResult<PageResult<JobLogRespVO>> result = Request.connector(JobLogFeign.class).getJobLogPage(queryMap);

                Vector<Vector> tableData = new Vector<>();


                if (result.isSuccess()) {

                    result.getData().getList().forEach(respVO -> {
                        Vector rowV = new Vector();
                        rowV.add(respVO.getId());
                        rowV.add(respVO.getJobId());
                        rowV.add(respVO.getHandlerName());
                        rowV.add(respVO.getHandlerParam());
                        rowV.add(respVO.getExecuteIndex());
                        rowV.add(DateUtil.format(Convert.toLocalDateTime(respVO.getBeginTime()), "yyyy-MM-dd HH:mm:ss") + " ~ " + DateUtil.format(Convert.toLocalDateTime(respVO.getEndTime()), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(respVO.getDuration() + " 毫秒");
                        rowV.add(respVO.getStatus());
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
                    table.getColumn("执行时间").setMinWidth(300);
                    table.getColumn("操作").setMinWidth(80);
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

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JTextField textField;
    private JScrollPane scrollPane1;
    private JPanel centerPane;
    private JScrollPane scrollPane2;
    private JTable table;
    private JPanel toolPane;
    private JLabel label7;
    private JTextField handlerNameField;
    private JLabel label9;
    private JComboBox<DictDataSimpleRespVO> stautsComboBox;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
