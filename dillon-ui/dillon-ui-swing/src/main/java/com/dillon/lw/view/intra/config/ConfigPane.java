/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.intra.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.infra.ConfigApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.BadgeLabelUtil;
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

import static com.dillon.lw.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;
import static com.dillon.lw.utils.DictTypeEnum.INFRA_CONFIG_TYPE;
import static javax.swing.JOptionPane.*;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.swing.rx.SwingRx;

/**
 * @author wenli
 */
public class ConfigPane extends com.dillon.lw.components.AbstractDisposablePanel {
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

        DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setHorizontalAlignment(SwingConstants.LEFT);

                if (value instanceof DictDataSimpleRespVO) {
                    label.setText(((DictDataSimpleRespVO) value).getLabel());
                }

                return label;

            }
        };
        typeComboBox.setRenderer(defaultListCellRenderer);
    }

    private JToolBar creatBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);
        JButton edit = new JButton("修改");
        edit.setForeground(UIManager.getColor("App.accent.color"));
        edit.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));
        edit.addActionListener(e -> showEditDialog());
        edit.setForeground(UIManager.getColor("App.accent.color"));

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> delMenu());
        del.setForeground(UIManager.getColor("App.danger.color"));

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
        ConfigFormPane formPane = new ConfigFormPane();
        formPane.updateData(id);

        WFormDialog<ConfigSaveReqVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "添加", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getValue,
                data -> Forest.client(ConfigApi.class).createConfig(data).getCheckedData(),
                this::updateData,
                "添加成功！"
        );
    }

    private void showEditDialog() {
        int selRow = table.getSelectedRow();
        Long postId = null;
        if (selRow != -1) {
            postId = Convert.toLong(table.getValueAt(selRow, 0));
        }

        ConfigFormPane formPane = new ConfigFormPane();
        formPane.updateData(postId);

        WFormDialog<ConfigSaveReqVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "修改", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getValue,
                data -> Forest.client(ConfigApi.class).updateConfig(data).getCheckedData(),
                this::updateData,
                "修改成功！"
        );
    }

    private void delMenu() {
        Long id = null;
        String name = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
            name = Convert.toStr(table.getValueAt(selRow, 1));
        }

        int opt = JOptionPane.showOptionDialog(this, "是否确定删除[" + name + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        final Long finalId = id;
        Single
                /*
                 * 删除操作本质上也是一次同步请求，这里仍然统一包成 Single，
                 * 让删除和后续的成功提示都进入同一条响应式链中管理。
                 */
                .fromCallable(() -> Forest.client(ConfigApi.class).deleteConfig(finalId).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");
                    updateData();
                }, SwingExceptionHandler::handle);
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

        Single
                /*
                 * 列表查询继续沿用“后台拉数据、前台绘表格”的固定模式：
                 * 查询条件在当前线程组装，请求放到 IO，表格模型和渲染器回到 EDT。
                 */
                .fromCallable(() -> Forest.client(ConfigApi.class).getConfigPage(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .doOnSubscribe(disposable -> {
                    /*
                     * 查询一发起就先禁用“搜索”按钮，避免用户在请求返回前重复点击。
                     * doOnSubscribe 不保证运行在 EDT，所以按钮状态切换要显式投递回 Swing 线程。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(false));
                })
                .doFinally(() -> {
                    /*
                     * 无论查询成功、失败还是订阅提前结束，都要恢复按钮可用态。
                     * doFinally 同样不保证运行在 EDT，这里继续统一切回 Swing 线程。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(true));
                })
                .compose(SwingRx.bindTo(this))
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
                }, SwingExceptionHandler::handle);


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
    private JTextField keyField;
    private JLabel label9;
    private JComboBox typeComboBox;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
