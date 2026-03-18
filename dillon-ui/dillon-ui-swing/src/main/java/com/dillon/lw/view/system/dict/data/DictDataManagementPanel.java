/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.system.dict.data;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.system.DictDataApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
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

import static javax.swing.JOptionPane.*;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.swing.rx.SwingRx;

/**
 * @author wenli
 */
public class DictDataManagementPanel extends com.dillon.lw.components.AbstractDisposablePanel {
    private static final String[] COLUMN_ID = {"字典编码", "字典标签", "字典键值", "字典顺序", "状态", "颜色类型", "CSS Class", "备注", "创建时间", "操作"};
    private static final int COL_DATA_ID = 0;
    private static final int COL_DATA_NAME = 1;
    private static final String SUCCESS_DELETE = "删除成功！";
    private static final String WARN_SELECT_DATA_FIRST = "请先选择字典数据！";

    private String dictType = "";
    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public DictDataManagementPanel() {
        initComponents();
        initListeners();
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
        codeTextField = new JTextField();
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
            centerPane.setBorder(new EmptyBorder(10, 10, 10, 10));

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
                label7.setText("字典名称");
                toolPane.add(label7, "cell 0 0");

                //---- nameTextField ----
                nameTextField.setColumns(15);
                nameTextField.setEnabled(false);
                toolPane.add(nameTextField, "cell 0 0");

                //---- label8 ----
                label8.setText("字典标签");
                toolPane.add(label8, "cell 0 0");

                //---- phoneTextField ----
                codeTextField.setColumns(15);
                toolPane.add(codeTextField, "cell 0 0");

                //---- label9 ----
                label9.setText("状态");
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
        nameTextField.setText("aaaa");

        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());

    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (table != null) {
            table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        }
    }

    public void setDictTypeRespVO(DictTypeRespVO respVO) {
        // 当前页签由字典类型驱动，只展示选中字典类型下的数据。
        this.dictType = respVO.getType();
        nameTextField.setText(respVO.getName());
        updateData();
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
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> updateData());
        newBut.addActionListener(e -> showAddDialog(null));
    }

    private void reset() {
        codeTextField.setText("");
        stautsComboBox.setSelectedIndex(0);
    }

    private void showAddDialog(Long id) {
        DictDataFormPane formPane = new DictDataFormPane();
        formPane.setDictType(dictType);
        formPane.updateData(id);

        WFormDialog<DictDataSaveReqVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "添加", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getValue,
                data -> Forest.client(DictDataApi.class).createDictData(data).getCheckedData(),
                this::updateData,
                "添加成功！"
        );
    }

    private void showEditDialog() {
        Long id = getSelectedDataId();
        if (id == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_DATA_FIRST);
            return;
        }

        DictDataFormPane formPane = new DictDataFormPane();
        formPane.updateData(id);

        WFormDialog<DictDataSaveReqVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "修改", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getValue,
                data -> Forest.client(DictDataApi.class).updateDictData(data).getCheckedData(),
                this::updateData,
                "修改成功！"
        );
    }

    private void delMenu() {
        Long id = getSelectedDataId();
        String name = getSelectedDataName();
        if (id == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_DATA_FIRST);
            return;
        }

        int opt = JOptionPane.showOptionDialog(this, "是否确定删除[" + name + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        Single
                /*
                 * 同步接口先通过 fromCallable 包装成懒执行的 RxJava 任务，
                 * 请求放到 IO 线程执行，成功结果再切回 EDT 更新 Swing 组件。
                 */
                .fromCallable(() -> Forest.client(DictDataApi.class).deleteDictData(id).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(aBoolean -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), SUCCESS_DELETE);
                    updateData();
                }, SwingExceptionHandler::handle);
    }

    public void updateData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (StrUtil.isNotBlank(codeTextField.getText())) {
            queryMap.put("label", codeTextField.getText());
        }
        if (StrUtil.isNotBlank(dictType)) {
            queryMap.put("dictType", dictType);
        }
        queryMap.put("status", stautsComboBox.getSelectedIndex() == 0 ? null : (stautsComboBox.getSelectedIndex() == 1 ? 0 : 1));

        queryMap.values().removeIf(Objects::isNull);

        Single
                /*
                 * 同步接口先通过 fromCallable 包装成懒执行的 RxJava 任务，
                 * 请求放到 IO 线程执行，成功结果再切回 EDT 更新 Swing 组件。
                 */
                .fromCallable(() -> Forest.client(DictDataApi.class).getDictTypePage(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .doOnSubscribe(disposable -> {
                    /*
                     * 搜索按钮在请求期间保持置灰，防止用户连续点击把同一页查询堆叠起来。
                     * doOnSubscribe 本身没有 EDT 保证，所以这里显式切回 Swing 线程。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(false));
                })
                .doFinally(() -> {
                    /*
                     * 查询链无论以哪种方式结束，都需要把按钮恢复成可点击状态。
                     * doFinally 也要通过 EDT 执行，避免跨线程修改 Swing 组件。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(true));
                })
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    Vector<Vector> tableData = new Vector<>();
                    result.getList().forEach(roleRespVO -> {
                        Vector rowV = new Vector();
                        rowV.add(roleRespVO.getId());
                        rowV.add(roleRespVO.getLabel());
                        rowV.add(roleRespVO.getValue());
                        rowV.add(roleRespVO.getSort());
                        rowV.add(roleRespVO.getStatus());
                        rowV.add(roleRespVO.getColorType());
                        rowV.add(roleRespVO.getCssClass());
                        rowV.add(roleRespVO.getRemark());
                        rowV.add(DateUtil.format(roleRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(roleRespVO);
                        tableData.add(rowV);
                    });
                    tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(createActionBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(createActionBar()));

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
                }, SwingExceptionHandler::handle);
    }

    private Long getSelectedDataId() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return Convert.toLong(table.getValueAt(selectedRow, COL_DATA_ID));
    }

    private String getSelectedDataName() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return Convert.toStr(table.getValueAt(selectedRow, COL_DATA_NAME));
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
    private JTextField codeTextField;
    private JLabel label9;
    private JComboBox stautsComboBox;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
