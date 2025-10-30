/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.intra.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.http.PayLoad;
import com.dillon.lw.http.RetrofitServiceManager;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.BadgeLabelUtil;
import com.dillon.lw.view.frame.MainFrame;
import com.dillon.lw.api.infra.ConfigApi;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

import static com.dillon.lw.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;
import static com.dillon.lw.utils.DictTypeEnum.INFRA_CONFIG_TYPE;
import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class ConfigPane extends JPanel {
    private String[] COLUMN_ID = {"参数主键", "参数分类", "参数名称", "参数键名", "参数键值", "是否可见", "系统内置", "备注", "创建时间", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public ConfigPane() {
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
        nameTextField = new JTextField();
        label8 = new JLabel();
        keyField = new JTextField();
        label9 = new JLabel();
        typeComboBox = new JComboBox();
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
                label7.setText("参数名称");
                toolPane.add(label7, "cell 0 0");

                //---- postNameTextField ----
                nameTextField.setColumns(15);
                toolPane.add(nameTextField, "cell 0 0");

                //---- label8 ----
                label8.setText("参数键名");
                toolPane.add(label8, "cell 0 0");

                //---- phoneTextField ----
                keyField.setColumns(15);
                toolPane.add(keyField, "cell 0 0");

                //---- label9 ----
                label9.setText("系统类型");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(typeComboBox, "cell 0 0");

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

        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());

        AppStore.getDictDataList(INFRA_CONFIG_TYPE).forEach(dictDataSimpleRespVO -> {
            typeComboBox.addItem(dictDataSimpleRespVO);
        });
        typeComboBox.setSelectedItem(null);
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
        del.addActionListener(e -> delMenu());
        del.setForeground(UIManager.getColor("app-error-color-5"));


        optBar.add(Box.createGlue());
        optBar.add(edit);
        optBar.add(del);
        optBar.add(Box.createGlue());
        optBar.setPreferredSize(new Dimension(210, 45));
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> updateData());
        newBut.addActionListener(e -> showAddDialog(null));
    }

    private void reset() {
        nameTextField.setText("");
        keyField.setText("");
        typeComboBox.setSelectedIndex(0);
    }

    private void showAddDialog(Long id) {
        ConfigFormPane postFormPane = new ConfigFormPane();
        postFormPane.updateData(id);
        int opt = WOptionPane.showOptionDialog(null, postFormPane, "添加", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            add(postFormPane.getValue());
        }
    }

    private void showEditDialog() {


        int selRow = table.getSelectedRow();
        Long postId = null;
        if (selRow != -1) {
            postId = Convert.toLong(table.getValueAt(selRow, 0));
        }

        ConfigFormPane formPane = new ConfigFormPane();
        formPane.updateData(postId);
        int opt = WOptionPane.showOptionDialog(null, formPane, "修改", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            edit(formPane.getValue());
        }
    }


    /**
     * 添加
     */
    private void add(ConfigSaveReqVO saveReqVO) {

        RetrofitServiceManager.getInstance().create(ConfigApi.class).createConfig(saveReqVO).map(new PayLoad<>())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "添加成功！");
                    updateData();
                }, throwable -> {
                    WMessage.showMessageError(MainFrame.getInstance(), throwable.getMessage());
                    throwable.printStackTrace();
                });


    }

    private void edit(ConfigSaveReqVO saveReqVO) {

        RetrofitServiceManager.getInstance().create(ConfigApi.class).updateConfig(saveReqVO).map(new PayLoad<>())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {

                    WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功！");

                    updateData();
                }, throwable -> {
                    WMessage.showMessageError(MainFrame.getInstance(), throwable.getMessage());
                    throwable.printStackTrace();
                });


    }

    private void delMenu() {
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
        RetrofitServiceManager.getInstance().create(ConfigApi.class).deleteConfig(id).map(new PayLoad<>())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    if (result) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");
                        updateData();
                    }
                }, throwable -> {
                    WMessage.showMessageError(MainFrame.getInstance(), throwable.getMessage());
                    throwable.printStackTrace();
                });


    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (table != null) {
            table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        }
    }


    public void updateData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (StrUtil.isNotBlank(nameTextField.getText())) {
            queryMap.put("name", nameTextField.getText());
        }
        if (StrUtil.isNotBlank(keyField.getText())) {
            queryMap.put("key", keyField.getText());
        }
        if (typeComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO selectedItem = (DictDataSimpleRespVO) typeComboBox.getSelectedItem();
            queryMap.put("type", selectedItem.getValue());
        }
        queryMap.values().removeIf(Objects::isNull);

        RetrofitServiceManager.getInstance().create(ConfigApi.class).getConfigPage(queryMap).map(new PayLoad<>())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    Vector<Vector> tableData = new Vector<>();
                    result.getList().forEach(respVO -> {
                        Vector rowV = new Vector();
                        rowV.add(respVO.getId());
                        rowV.add(respVO.getCategory());
                        rowV.add(respVO.getName());
                        rowV.add(respVO.getKey());
                        rowV.add(respVO.getValue());
                        rowV.add(respVO.getVisible());
                        rowV.add(respVO.getType());
                        rowV.add(respVO.getRemark());
                        rowV.add(DateUtil.format(respVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(respVO);
                        tableData.add(rowV);
                    });
                    tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

                    table.getColumn("是否可见").setCellRenderer(new DefaultTableCellRenderer() {
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
                    table.getColumn("系统内置").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = BadgeLabelUtil.getBadgeLabel(INFRA_CONFIG_TYPE, value);

                            panel.add(label);
                            panel.setBackground(component.getBackground());
                            panel.setOpaque(isSelected);
                            return panel;
                        }
                    });
                    paginationPane.setTotal(result.getTotal());
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
    private JTextField nameTextField;
    private JLabel label8;
    private JTextField keyField;
    private JLabel label9;
    private JComboBox typeComboBox;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
