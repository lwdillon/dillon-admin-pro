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
import com.lw.swing.components.notice.WMessage;
import com.lw.swing.components.table.renderer.CheckHeaderCellRenderer;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.request.Request;
import com.lw.swing.utils.BadgeLabelUtil;
import com.lw.swing.view.MainFrame;
import com.lw.ui.request.api.system.NotifyMessageFeign;
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

import static com.lw.ui.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;
import static com.lw.ui.utils.DictTypeEnum.SYSTEM_NOTIFY_TEMPLATE_TYPE;
import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class MyNotifyMessagePane extends JPanel {
    private String[] COLUMN_ID = {"", "发送人", "发送时间", "类型", "消息内容", "是否已读", "阅读时间", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public MyNotifyMessagePane() {
        initComponents();
        initListeners();
        updateData();
    }

    private void initComponents() {
        textField = new JTextField();
        scrollPane1 = new WScrollPane();
        centerPane = new JPanel();
        scrollPane2 = new WScrollPane();
        table = new JXTable(tableModel = new DefaultTableModel()) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        toolPane = new WPanel();
        label7 = new JLabel();
        readStatusComboBox = new JComboBox();
        label10 = new JLabel();
        startDateTextField = new WLocalDateCombo();
        label11 = new JLabel();
        endDateTextField = new WLocalDateCombo();
        searchBut = new JButton();
        reseBut = new JButton();
        readingBut = new JButton();
        readingAllBut = new JButton();

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
                label7.setText("是否已读");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                toolPane.add(readStatusComboBox, "cell 0 0");


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
                readingBut.setText("标记已读");
                toolPane.add(readingBut, "cell 0 0");

                readingAllBut.setText("全部已读");
                toolPane.add(readingAllBut, "cell 0 0");
            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);


        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
        readingBut.addActionListener(e -> updateNotifyMessageRead());
        readingAllBut.addActionListener(e -> updateAllNotifyMessageRead());
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
                "[][][][][][]"));
        panel.setPreferredSize(new Dimension(450, 400));
        addMessageInfo("发送人", noticeRespVO.getTemplateNickname(), panel, 0);
        addMessageInfo("发送时间", DateUtil.format(noticeRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"), panel, 1);
        addMessageInfo("消息类型", SYSTEM_NOTIFY_TEMPLATE_TYPE, noticeRespVO.getTemplateType(), panel, 2);
        addMessageInfo("是否已读", INFRA_BOOLEAN_STRING, noticeRespVO.getReadStatus(), panel, 3);
        addMessageInfo("阅读时间", DateUtil.format(noticeRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"), panel, 4);
        addMessageInfo("内容", noticeRespVO.getTemplateContent(), panel, 5);
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
        JLabel badge = BadgeLabelUtil.getBadgeLabel(dictType, value);


        panel.add(label, "cell 0 " + row);
        panel.add(badge, "cell 1 " + row + ",alignx left,growx 0");
    }

    private void reset() {
        readStatusComboBox.setSelectedItem(null);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
    }

    private void updateNotifyMessageRead() {


        List<Long> ids = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean b = (Boolean) tableModel.getValueAt(i, 0);
            if (b) {
                NotifyMessageRespVO notifyMessageRespVO = (NotifyMessageRespVO) tableModel.getValueAt(i, COLUMN_ID.length - 1);

                ids.add(notifyMessageRespVO.getId());
            }
        }

        if (ids.isEmpty()) {
            WMessage.showMessageWarning(MainFrame.getInstance(), "请选择要删除的数据！");
            return;
        }


        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(NotifyMessageFeign.class).updateNotifyMessageRead(ids);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "批量已读成功！");

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

    private void updateAllNotifyMessageRead() {


        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(NotifyMessageFeign.class).updateAllNotifyMessageRead();
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "全部已读成功！");

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


    private void del() {


    }


    public void updateData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());


        if (readStatusComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO simpleRespVO = (DictDataSimpleRespVO) readStatusComboBox.getSelectedItem();
            queryMap.put("readStatus", simpleRespVO.getValue());
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
                CommonResult<PageResult<NotifyMessageRespVO>> result = Request.connector(NotifyMessageFeign.class).getMyMyNotifyMessagePage(queryMap);

                Vector<Vector> tableData = new Vector<>();


                if (result.isSuccess()) {

                    result.getData().getList().forEach(roleRespVO -> {
                        Vector rowV = new Vector();
                        rowV.add(false);
                        rowV.add(roleRespVO.getTemplateNickname());
                        rowV.add(DateUtil.format(roleRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(roleRespVO.getTemplateType());
                        rowV.add(roleRespVO.getTemplateContent());
                        rowV.add(roleRespVO.getReadStatus());
                        rowV.add(DateUtil.format(roleRespVO.getReadTime(), "yyyy-MM-dd HH:mm:ss"));
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
                    table.getColumn("").setMinWidth(40);
                    table.getColumn("").setMaxWidth(40);
                    table.getColumn("").setHeaderRenderer(new CheckHeaderCellRenderer(table));
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
    private JComboBox readStatusComboBox;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    private JButton readingBut;
    private JButton readingAllBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
