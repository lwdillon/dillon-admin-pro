/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.system.notice;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.api.system.NotifyMessageApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.CheckHeaderCellRenderer;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
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
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.dillon.lw.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;
import static com.dillon.lw.utils.DictTypeEnum.SYSTEM_NOTIFY_TEMPLATE_TYPE;
import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class MyNotifyMessagePane extends JPanel {
    private static final String[] COLUMN_ID = {"", "发送人", "发送时间", "类型", "消息内容", "是否已读", "阅读时间", "操作"};
    private static final int COL_MESSAGE_OBJECT = COLUMN_ID.length - 1;
    private static final String WARN_SELECT_MESSAGE_FIRST = "请先选择一条消息！";

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


            @Override
            public boolean isCellEditable(int row, int column) {
                return "操作".equals(getColumnName(column));
            }

        };
        tableModel.setColumnIdentifiers(COLUMN_ID);

        // 开启排序
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // 禁止第一列排序
        sorter.setSortable(0, false);
        toolPane = new WPanel();
        label7 = new JLabel();
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
        optBar.add(Box.createGlue());
        optBar.add(viewBut);
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
        NotifyMessageRespVO noticeRespVO = getSelectedMessage();
        if (noticeRespVO == null) {
            return;
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
            WMessage.showMessageWarning(MainFrame.getInstance(), "请选择要已读的数据！");
            return;
        }

        executeAsync(() -> Forest.client(NotifyMessageApi.class).updateNotifyMessageRead(ids).getCheckedData(), result -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "批量已读成功！");
            updateData();
        });


    }

    private void updateAllNotifyMessageRead() {


        executeAsync(() -> Forest.client(NotifyMessageApi.class).updateAllNotifyMessageRead().getCheckedData(), result -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "全部已读成功！");
            updateData();
        });


    }


    public void updateData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            java.lang.String sd = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            java.lang.String ed = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("createTime", ListUtil.of(sd, ed));
        }

        queryMap.values().removeIf(Objects::isNull);

        executeAsync(() -> Forest.client(NotifyMessageApi.class).getMyMyNotifyMessagePage(queryMap).getCheckedData(), result -> {
            paginationPane.setTotal(result.getTotal());
            Vector<Vector> tableData = new Vector<>();
            result.getList().forEach(roleRespVO -> {
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
            tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
            table.getColumn("").setMinWidth(40);
            table.getColumn("").setMaxWidth(40);
            table.getColumn("").setHeaderRenderer(new CheckHeaderCellRenderer(table));
            table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(createActionBar()));
            table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(createActionBar()));

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
        });


    }

    private NotifyMessageRespVO getSelectedMessage() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_MESSAGE_FIRST);
            return null;
        }
        return (NotifyMessageRespVO) table.getValueAt(selectedRow, COL_MESSAGE_OBJECT);
    }

    private <T> void executeAsync(Supplier<T> request, Consumer<T> onSuccess) {
        // 统一异步模板：后台请求 + EDT 回调 + 异常统一处理。
        CompletableFuture
                .supplyAsync(request)
                .thenAcceptAsync(onSuccess, SwingUtilities::invokeLater)
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> SwingExceptionHandler.handle(throwable));
                    return null;
                });
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
