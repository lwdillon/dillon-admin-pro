/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.lw.swing.view.intra.file;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.file.FileRespVO;
import com.lw.swing.components.*;
import com.lw.swing.components.notice.WMessage;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.request.Request;
import com.lw.swing.view.MainFrame;
import com.lw.ui.request.api.file.FileConfigFeign;
import com.lw.ui.request.api.file.FileFeign;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class FileListPane extends JPanel {
    private String[] COLUMN_ID = {"文件名", "文件路径", "URL", "文件类型", "文件内容", "上传时间", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public FileListPane() {
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
        pathField = new JTextField();
        label9 = new JLabel();
        typeField = new JTextField();
        label10 = new JLabel();
        startDateTextField = new WLocalDateCombo();
        label11 = new JLabel();
        endDateTextField = new WLocalDateCombo();
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
                label7.setText("文件路径");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                pathField.setColumns(15);
                toolPane.add(pathField, "cell 0 0");

                //---- label9 ----
                label9.setText("文件类型");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(typeField, "cell 0 0");

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
                searchBut.setText("搜索");
                toolPane.add(searchBut, "cell 0 0");

                //---- reseBut ----
                reseBut.setText("重置");
                toolPane.add(reseBut, "cell 0 0");

                //---- newBut ----
                newBut.setText("上传文件");
                toolPane.add(newBut, "cell 0 0");
            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);

        startDateTextField.setValue(null);
        endDateTextField.setValue(null);


        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());


    }

    private JToolBar creatBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("app-error-color-5"));

        JButton downLoadBut = new JButton("下载");
        downLoadBut.setIcon(new FlatSVGIcon("icons/xiazai.svg", 15, 15));
        downLoadBut.addActionListener(e -> download());
        del.setForeground(UIManager.getColor("App.accentColor"));

        optBar.add(Box.createGlue());
        optBar.add(downLoadBut);
        optBar.add(del);
        optBar.add(Box.createGlue());
        return optBar;
    }


    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> loadTableData());
        newBut.addActionListener(e -> uploadFile());
    }

    private void reset() {
        pathField.setText("");
        typeField.setText(null);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
        loadTableData();
    }

    private void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            SwingWorker<CommonResult<String>, Object> swingWorker = new SwingWorker<CommonResult<String>, Object>() {
                @Override
                protected CommonResult<String> doInBackground() throws Exception {
                    return Request.fileConnector(FileFeign.class).uploadFile("", selectedFile);
                }

                @Override
                protected void done() {
                    try {
                        if (get().isSuccess()) {
                            WMessage.showMessageSuccess(MainFrame.getInstance(), "上传成功！");
                            loadTableData();
                        }
                    } catch (Exception e) {

                        throw new RuntimeException(e);
                    }
                }
            };
            swingWorker.execute();
        }
    }


    @Override
    public void updateUI() {
        super.updateUI();
        if (table != null) {
            table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        }
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
                return Request.connector(FileConfigFeign.class).deleteFileConfig(finalId);
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


    private void download() {

        FileRespVO respVO=null;
        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            respVO = (FileRespVO) table.getValueAt(selRow, COLUMN_ID.length - 1);
        }


        if (respVO == null) {
            return;
        }
        // Check if Desktop API is supported on the current platform
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(respVO.getUrl()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
//                                            showAlert("Error", "Desktop API is not supported on this platform.");
        }

    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        String path = null;
        String type = null;
        if (StrUtil.isNotBlank(pathField.getText())) {
            path = pathField.getText();
        }
        if (StrUtil.isNotBlank(typeField.getText())) {
            type = typeField.getText();
        }


        queryMap.put("path", path);
        queryMap.put("type", type);

        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            String[] dateTimes = new String[2];
            dateTimes[0] = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            dateTimes[1] = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("createTime", dateTimes);
        }

        SwingWorker<Vector<Vector>, Long> swingWorker = new SwingWorker<Vector<Vector>, Long>() {
            @Override
            protected Vector<Vector> doInBackground() throws Exception {
                CommonResult<PageResult<FileRespVO>> result = Request.connector(FileFeign.class).getFilePage(queryMap);

                Vector<Vector> tableData = new Vector<>();


                if (result.isSuccess()) {

                    result.getData().getList().forEach(respVO -> {
                        Vector rowV = new Vector();
                        rowV.add(respVO.getName());
                        rowV.add(respVO.getPath());
                        rowV.add(respVO.getUrl());
                        rowV.add(respVO.getType());
                        rowV.add(respVO);
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
                    table.getColumn("操作").setMinWidth(180);
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

                    table.getColumn("文件内容").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new BorderLayout());
                            table.setRowHeight(row, 35);
                            if (value instanceof FileRespVO) {

                                if (StrUtil.contains(((FileRespVO) value).getType(), "image")) {
                                    URL url = null;
                                    try {
                                        url = new URL(((FileRespVO) value).getUrl());
                                        ImageIcon imageIcon = new ImageIcon(url);
                                        // Create a label and set the icon
                                        JLabel imageLabel = new JLabel(imageIcon);
                                        panel.add(imageLabel);
                                        table.setRowHeight(row, 180);
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }

                                } else {

                                }
                            }
                            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
    private JTextField pathField;
    private JLabel label9;
    private JTextField typeField;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
