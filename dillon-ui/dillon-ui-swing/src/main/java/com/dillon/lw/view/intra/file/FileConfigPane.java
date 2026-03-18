/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.intra.file;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.infra.FileConfigApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
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
import static com.dillon.lw.utils.DictTypeEnum.INFRA_FILE_STORAGE;
import static javax.swing.JOptionPane.*;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.swing.rx.SwingRx;

/**
 * @author wenli
 */
public class FileConfigPane extends com.dillon.lw.components.AbstractDisposablePanel {
    private String[] COLUMN_ID = {"编号", "配置名", "存储器", "备注", "主配置", "创建时间", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public FileConfigPane() {
        initComponents();
        initListeners();
        loadTableData();
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
        label9 = new JLabel();
        storageComboBox = new JComboBox();
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
                label7.setText("配置名");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                nameTextField.setColumns(15);
                toolPane.add(nameTextField, "cell 0 0");

                //---- label9 ----
                label9.setText("存储器");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(storageComboBox, "cell 0 0");

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
                newBut.setText("\u65b0\u589e");
                toolPane.add(newBut, "cell 0 0");
            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);

        AppStore.getDictDataList(INFRA_FILE_STORAGE).forEach(dictDataSimpleRespVO -> {
            storageComboBox.addItem(dictDataSimpleRespVO);
        });

        storageComboBox.setSelectedItem(null);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);


        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());

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
        storageComboBox.setRenderer(defaultListCellRenderer);
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
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("App.danger.color"));
        JButton primaryBut = new JButton("主配置");
        primaryBut.addActionListener(e -> updateFileConfigMaster());
        primaryBut.setForeground(UIManager.getColor("App.accent.color"));
        primaryBut.setIcon(new FlatSVGIcon("icons/xinzeng.svg", 15, 15));

        optBar.add(Box.createGlue());
        optBar.add(edit);
        optBar.add(primaryBut);
        optBar.add(del);
        optBar.add(Box.createGlue());
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> loadTableData());
        newBut.addActionListener(e -> showAddDialog(null));
    }

    private void reset() {
        nameTextField.setText("");
        storageComboBox.setSelectedItem(null);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
        loadTableData();
    }

    private void showAddDialog(Long id) {
        FileConfigFromPane roleEditPane = new FileConfigFromPane();
        roleEditPane.updateData(id);
        int opt = JOptionPane.showOptionDialog(null, roleEditPane, "添加", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            add(roleEditPane.getValue());
        }
    }

    private void showEditDialog() {


        int selRow = table.getSelectedRow();
        Long id = null;
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
        }

        FileConfigFromPane roleEditPane = new FileConfigFromPane();
        roleEditPane.updateData(id);
        roleEditPane.revalidate();
        int opt = JOptionPane.showOptionDialog(null, roleEditPane, "修改", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            edit(roleEditPane.getValue());
        }
    }


    @Override
    public void updateUI() {
        super.updateUI();
        if (table != null) {
            table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        }
    }


    private void add(FileConfigSaveReqVO roleSaveReqVO) {
        Single
                /*
                 * 新增配置完成后要立刻刷新列表，因此把成功提示和刷新动作统一放在 EDT。
                 */
                .fromCallable(() -> Forest.client(FileConfigApi.class).createFileConfig(roleSaveReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(id -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "添加成功！");
                    loadTableData();
                }, SwingExceptionHandler::handle);
    }

    private void edit(FileConfigSaveReqVO roleSaveReqVO) {
        Single
                /*
                 * 修改配置与新增同样是同步提交请求，统一走 RxJava 链，
                 * 避免页面线程直接等待接口返回。
                 */
                .fromCallable(() -> Forest.client(FileConfigApi.class).updateFileConfig(roleSaveReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功！");
                    loadTableData();
                }, SwingExceptionHandler::handle);
    }

    private void del() {
        Long id = null;
        String userName = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
            userName = Convert.toStr(table.getValueAt(selRow, 1));
        }

        int opt = JOptionPane.showOptionDialog(this, "是否确定删除[" + userName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        Long finalId = id;
        Single
                /*
                 * 删除配置后需要立即回到界面层给出反馈并刷新表格。
                 */
                .fromCallable(() -> Forest.client(FileConfigApi.class).deleteFileConfig(finalId).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");
                    loadTableData();
                }, SwingExceptionHandler::handle);
    }

    private void updateFileConfigMaster() {
        Long id = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
        }

        int opt = JOptionPane.showOptionDialog(this, "是否确认修改配置编号为" + id + "的数据项为主配置?", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        Long finalId = id;
        Single
                /*
                 * 切换主配置也是一次后台写操作，保持和其他按钮一致的线程模型。
                 */
                .fromCallable(() -> Forest.client(FileConfigApi.class).updateFileConfigMaster(finalId).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "主配置更新成功！");
                    loadTableData();
                }, SwingExceptionHandler::handle);
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (StrUtil.isNotBlank(nameTextField.getText())) {
            queryMap.put("name", nameTextField.getText());
        }

        if (storageComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO dictDataSimpleRespVO = (DictDataSimpleRespVO) storageComboBox.getSelectedItem();
            queryMap.put("storage", dictDataSimpleRespVO.getValue());
        }

        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            java.lang.String sd = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            java.lang.String ed = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("createTime", ListUtil.of(sd, ed));
        }

        queryMap.values().removeIf(Objects::isNull);

        Single
                /*
                 * 文件配置列表页要更新分页、表格和徽标渲染器，
                 * 所以请求放到 IO，所有 Swing 更新统一放到 EDT。
                 */
                .fromCallable(() -> Forest.client(FileConfigApi.class).getFileConfigPage(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .doOnSubscribe(disposable -> {
                    /*
                     * 查询进行时先禁用搜索按钮，避免同一批文件配置筛选条件被重复提交。
                     * doOnSubscribe 不一定在 EDT 执行，因此按钮更新显式切回 Swing 线程。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(false));
                })
                .doFinally(() -> {
                    /*
                     * 请求结束后统一恢复按钮，成功、失败和取消场景都保持一致。
                     * doFinally 同样没有 EDT 保证，所以恢复动作继续交给 EDT。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(true));
                })
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    Vector<Vector> tableData = new Vector<>();
                    result.getList().forEach(roleRespVO -> {
                        Vector rowV = new Vector();
                        rowV.add(Convert.toLong(roleRespVO.get("id")));
                        rowV.add(Convert.toStr(roleRespVO.get("name")));
                        rowV.add(Convert.toInt(roleRespVO.get("storage")));
                        rowV.add(Convert.toStr(roleRespVO.get("remark")));
                        rowV.add(Convert.toBool(roleRespVO.get("master")));
                        rowV.add(DateUtil.format(Convert.toLocalDateTime(roleRespVO.get("createTime")), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(roleRespVO);
                        tableData.add(rowV);
                    });
                    paginationPane.setTotal(result.getTotal());
                    tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
                    table.getColumn("操作").setMinWidth(180);
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

                    table.getColumn("存储器").setCellRenderer(new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                            JLabel label = BadgeLabelUtil.getBadgeLabel(INFRA_FILE_STORAGE, value);
                            panel.add(label);
                            panel.setBackground(component.getBackground());
                            panel.setOpaque(isSelected);
                            return panel;
                        }
                    });
                    table.getColumn("主配置").setCellRenderer(new DefaultTableCellRenderer() {
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
    private JLabel label9;
    private JComboBox<DictDataSimpleRespVO> storageComboBox;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
