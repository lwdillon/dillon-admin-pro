/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.lw.swing.view.system.notice;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.notice.vo.NoticeRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.lw.swing.components.*;
import com.lw.swing.components.notice.WMessage;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.request.Request;
import com.lw.swing.view.MainFrame;
import com.lw.ui.request.api.system.NoticeFeign;
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

import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class NoticeManagementPanel extends JPanel {
    private final static String[] COLUMN_ID = {"公告编号", "公告标题", "公告类型", "状态", "创建时间", "操作"};


    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public NoticeManagementPanel() {
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
        titleTextField = new JTextField();
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
                label7.setText("公告标题");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                titleTextField.setColumns(15);
                toolPane.add(titleTextField, "cell 0 0");


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
        edit.setForeground(UIManager.getColor("App.accentColor"));
        edit.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));
        edit.addActionListener(e -> showEditDialog());
        edit.setForeground(UIManager.getColor("App.accentColor"));

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("app-error-color-5"));

        JButton push = new JButton("推送");
        push.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        push.addActionListener(e -> push());
        push.setForeground(UIManager.getColor("App.accentColor"));


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
        titleTextField.setText("");
        stautsComboBox.setSelectedIndex(0);
    }

    private void showAddDialog(Long id) {
        NoticeFormPane noticeFormPane = new NoticeFormPane();
        noticeFormPane.updateData(new NoticeRespVO());
        int opt = WOptionPane.showOptionDialog(null, noticeFormPane, "添加", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            add(noticeFormPane.getValue());
        }
    }

    private void showEditDialog() {


        int selRow = table.getSelectedRow();
        NoticeRespVO noticeRespVO = null;
        if (selRow != -1) {
            noticeRespVO = (NoticeRespVO) table.getValueAt(selRow, 5);
        }

        NoticeFormPane noticeFormPane = new NoticeFormPane();
        noticeFormPane.updateData(noticeRespVO);
        int opt = WOptionPane.showOptionDialog(null, noticeFormPane, "修改", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            edit(noticeFormPane.getValue());
        }
    }


    /**
     * 添加
     */
    private void add(NoticeSaveReqVO saveReqVO) {

        SwingWorker<CommonResult<Long>, Object> swingWorker = new SwingWorker<CommonResult<Long>, Object>() {
            @Override
            protected CommonResult<Long> doInBackground() throws Exception {
                return Request.connector(NoticeFeign.class).createNotice(saveReqVO);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "添加成功！");

                        updateData();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();

    }

    private void edit(NoticeSaveReqVO saveReqVO) {


        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(NoticeFeign.class).updateNotice(saveReqVO);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功！");

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
        Long finaId = id;
        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(NoticeFeign.class).deleteNotice(finaId);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");

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

    private void push() {

        int selRow = table.getSelectedRow();
        Long id = Convert.toLong(table.getValueAt(selRow, 0));


        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(NoticeFeign.class).push(id);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "发布成功！");

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

        if (StrUtil.isNotBlank(titleTextField.getText())) {
            queryMap.put("title", titleTextField.getText());
        }

        queryMap.put("status", stautsComboBox.getSelectedIndex() == 0 ? null : (stautsComboBox.getSelectedIndex() == 1 ? 0 : 1));


        SwingWorker<Vector<Vector>, Long> swingWorker = new SwingWorker<Vector<Vector>, Long>() {
            @Override
            protected Vector<Vector> doInBackground() throws Exception {
                CommonResult<PageResult<NoticeRespVO>> result = Request.connector(NoticeFeign.class).getNoticePage(queryMap);

                Vector<Vector> tableData = new Vector<>();


                if (result.isSuccess()) {

                    result.getData().getList().forEach(respVO -> {
                        Vector rowV = new Vector();
                        rowV.add(respVO.getId());
                        rowV.add(respVO.getTitle());
                        rowV.add(respVO.getType());
                        rowV.add(respVO.getStatus());
                        rowV.add(DateUtil.format(respVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
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

                    table.getColumn("状态").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = new JLabel(ObjectUtil.equals(value, 0) ? "开启" : "停用");
                            label.setForeground(ObjectUtil.equals(value, 0) ? new Color(96, 197, 104) : new Color(0xf56c6c));
                            FlatSVGIcon icon = new FlatSVGIcon("icons/yuan.svg", 10, 10);
                            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
                                return label.getForeground();
                            }));
                            label.setIcon(icon);
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
    private JTextField titleTextField;
    private JLabel label9;
    private JComboBox stautsComboBox;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
