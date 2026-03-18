package com.dillon.lw.view.intra.db;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.infra.DataSourceConfigApi;
import com.dillon.lw.components.CenterTableCellRenderer;
import com.dillon.lw.components.WPanel;
import com.dillon.lw.components.WScrollPane;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.infra.controller.admin.db.vo.DataSourceConfigRespVO;
import com.dillon.lw.module.infra.controller.admin.db.vo.DataSourceConfigSaveReqVO;
import com.dillon.lw.view.frame.MainFrame;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.swing.rx.SwingRx;

/**
 * 数据库监控 - 数据源配置管理面板。
 * <p>
 * 对接后端 DataSourceConfigController：
 * - 列表：/infra/data-source-config/list
 * - 新增：/infra/data-source-config/create
 * - 修改：/infra/data-source-config/update
 * - 删除：/infra/data-source-config/delete
 * </p>
 */
public class DataSourceConfigPane extends com.dillon.lw.components.AbstractDisposablePanel {

    private static final String[] COLUMN_ID = {"主键", "数据源名称", "数据源连接", "用户名", "创建时间", "操作"};
    private static final int COL_OBJECT = 5;
    private static final Long MASTER_ID = 0L;
    private static final String MASTER_TIP = "主数据源不允许修改或删除";
    private static final String WARN_SELECT_FIRST = "请先选择数据源配置！";

    private DefaultTableModel tableModel;
    private JXTable table;
    private JTextField nameTextField;
    private JTextField urlTextField;
    private JButton searchBut;
    private JButton resetBut;
    private JButton addBut;

    public DataSourceConfigPane() {
        initComponents();
        initListeners();
        updateData();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        JPanel centerPane = new JPanel(new BorderLayout(10, 10));
        centerPane.setOpaque(false);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return "操作".equals(getColumnName(column));
            }
        };
        tableModel.setColumnIdentifiers(COLUMN_ID);
        table = new JXTable(tableModel);
        table.setRowHeight(40);
        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());

        WScrollPane scrollPane = new WScrollPane(table);
        centerPane.add(scrollPane, BorderLayout.CENTER);

        JPanel toolPane = new WPanel();
        toolPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                "[left]",
                "[]"));
        toolPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        toolPane.add(new JLabel("数据源名称"), "cell 0 0");
        nameTextField = new JTextField();
        nameTextField.setColumns(15);
        toolPane.add(nameTextField, "cell 0 0");

        toolPane.add(new JLabel("数据源连接"), "cell 0 0");
        urlTextField = new JTextField();
        urlTextField.setColumns(20);
        toolPane.add(urlTextField, "cell 0 0");

        searchBut = new JButton("搜索");
        resetBut = new JButton("重置");
        addBut = new JButton("新增");
        toolPane.add(searchBut, "cell 0 0");
        toolPane.add(resetBut, "cell 0 0");
        toolPane.add(addBut, "cell 0 0");

        centerPane.add(toolPane, BorderLayout.NORTH);
        add(centerPane, BorderLayout.CENTER);
    }

    /**
     * @deprecated 历史命名兼容，建议使用 {@link #createActionBar()}。
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

        JButton del = new JButton("删除");
        del.setForeground(UIManager.getColor("App.danger.color"));
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());

        optBar.add(Box.createGlue());
        optBar.add(edit);
        optBar.add(del);
        optBar.add(Box.createGlue());
        optBar.setPreferredSize(new Dimension(210, 45));
        return optBar;
    }

    private void initListeners() {
        resetBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> updateData());
        addBut.addActionListener(e -> showAddDialog());
    }

    private void reset() {
        nameTextField.setText("");
        urlTextField.setText("");
    }

    private void showAddDialog() {
        DataSourceConfigFormPane formPane = new DataSourceConfigFormPane();
        int opt = JOptionPane.showOptionDialog(
                MainFrame.getInstance(), formPane, "新增数据源",
                OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt != 0 || !formPane.validates()) {
            return;
        }
        DataSourceConfigSaveReqVO saveReqVO = formPane.getValue();
        Single
                /*
                 * 新增动作对应一次同步 HTTP 请求，这里显式切到 IO 线程执行，
                 * 成功后再回到 EDT 弹提示并刷新表格，避免对话框确认时阻塞界面。
                 */
                .fromCallable(() -> Forest.client(DataSourceConfigApi.class).createDataSourceConfig(saveReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "新增成功！");
                    updateData();
                }, SwingExceptionHandler::handle);
    }

    private void showEditDialog() {
        DataSourceConfigRespVO selected = getSelectedConfig();
        if (selected == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_FIRST);
            return;
        }
        if (isMaster(selected)) {
            WMessage.showMessageWarning(MainFrame.getInstance(), MASTER_TIP);
            return;
        }

        DataSourceConfigFormPane formPane = new DataSourceConfigFormPane();
        formPane.updateData(selected.getId());
        int opt = JOptionPane.showOptionDialog(
                MainFrame.getInstance(), formPane, "修改数据源",
                OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt != 0 || !formPane.validates()) {
            return;
        }
        DataSourceConfigSaveReqVO saveReqVO = formPane.getValue();
        Single
                /*
                 * 修改数据源仍然是阻塞式接口调用，因此保持同样的线程语义：
                 * 请求在线程池执行，UI 提示和列表刷新在 EDT 完成。
                 */
                .fromCallable(() -> Forest.client(DataSourceConfigApi.class).updateDataSourceConfig(saveReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功！");
                    updateData();
                }, SwingExceptionHandler::handle);
    }

    private void del() {
        DataSourceConfigRespVO selected = getSelectedConfig();
        if (selected == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_FIRST);
            return;
        }
        if (isMaster(selected)) {
            WMessage.showMessageWarning(MainFrame.getInstance(), MASTER_TIP);
            return;
        }

        int opt = JOptionPane.showOptionDialog(
                this, "是否确定删除[" + selected.getName() + "]？",
                "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);
        if (opt != 0) {
            return;
        }
        Single
                /*
                 * 删除成功后页面要立即重新查询列表，所以把成功回调切回 EDT，
                 * 统一处理消息提示和刷新动作。
                 */
                .fromCallable(() -> Forest.client(DataSourceConfigApi.class).deleteDataSourceConfig(selected.getId()).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");
                    updateData();
                }, SwingExceptionHandler::handle);
    }

    /**
     * 查询并刷新列表。
     * <p>
     * 后端只提供 list 接口，这里在前端做轻量过滤，避免改动后端。
     * </p>
     */
    public void updateData() {
        Single
                /*
                 * 列表查询只负责后台取数；真正的过滤和表格模型更新都在 EDT 中执行，
                 * 保证下游所有 Swing 组件访问都线程安全。
                 */
                .fromCallable(() -> Forest.client(DataSourceConfigApi.class).getDataSourceConfigList().getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .doOnSubscribe(disposable -> {
                    /*
                     * 查询开始后先禁用搜索按钮，避免同一批筛选条件被连续点击触发多次请求。
                     * 这里显式回到 EDT，是为了保证 Swing 组件状态修改线程安全。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(false));
                })
                .doFinally(() -> {
                    /*
                     * 请求结束后统一恢复按钮，不区分成功、失败或取消场景。
                     * doFinally 可能运行在后台线程，因此恢复动作也交给 EDT。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(true));
                })
                .compose(SwingRx.bindTo(this))
                .subscribe(this::updateTable, SwingExceptionHandler::handle);
    }

    private void updateTable(List<DataSourceConfigRespVO> list) {
        String nameKeyword = StrUtil.trim(nameTextField.getText());
        String urlKeyword = StrUtil.trim(urlTextField.getText());

        Vector<Vector> tableData = new Vector<>();
        list.stream()
                .filter(item -> StrUtil.isBlank(nameKeyword) || StrUtil.containsIgnoreCase(item.getName(), nameKeyword))
                .filter(item -> StrUtil.isBlank(urlKeyword) || StrUtil.containsIgnoreCase(item.getUrl(), urlKeyword))
                .forEach(item -> {
                    Vector rowV = new Vector();
                    rowV.add(item.getId());
                    rowV.add(item.getName());
                    rowV.add(item.getUrl());
                    rowV.add(item.getUsername());
                    rowV.add(item.getCreateTime() == null ? "-" : DateUtil.format(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                    rowV.add(item);
                    tableData.add(rowV);
                });

        tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
        table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(createActionBar()));
        table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(createActionBar()));
    }

    private DataSourceConfigRespVO getSelectedConfig() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return (DataSourceConfigRespVO) table.getValueAt(selectedRow, COL_OBJECT);
    }

    private boolean isMaster(DataSourceConfigRespVO selected) {
        return ObjectUtil.equals(selected.getId(), MASTER_ID);
    }

}
