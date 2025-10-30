package com.lw.swing.view.system.menu;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.lw.swing.components.*;
import com.lw.swing.components.notice.WMessage;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.http.PayLoad;
import com.lw.swing.http.RetrofitServiceManager;
import com.lw.swing.store.AppStore;
import com.lw.swing.view.frame.MainFrame;
import com.lw.ui.api.system.MenuApi;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import static javax.swing.JOptionPane.*;

/**
 * 菜单管理面板
 *
 * @author liwen
 * @date 2022/07/17
 */
public class MenuManagementPanel extends JPanel implements Observer {
    private final static String[] COLUMN_ID = {"菜单名称", "图标", "排序", "权限标识", "组件路径", "组件名称", "状态", "操作"};

    private JXTreeTable treeTable;

    private MenuEditPane menuEditPane;


    private JTextField nameTextField;
    private JComboBox statusCombo;
    private JToggleButton exButton;

    private WaitPane waitPane;

    public MenuManagementPanel() {

        initComponents();
        updateData();
    }

    private void initComponents() {

        JPanel toolBar = new WPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolBar.add(new JLabel("菜单名称"));
        toolBar.add(nameTextField = createTextField("请输入菜单名称"));

        nameTextField.setColumns(20);
        toolBar.add(new JLabel("状态"));
        toolBar.add(statusCombo = new JComboBox(new String[]{"全部", "正常", "停用"}));


        JButton restButton = new WButton("重置");
        restButton.setBackground(new Color(0xf35857));
        toolBar.add(restButton);
        restButton.addActionListener(e -> {
            statusCombo.setSelectedIndex(0);
        });
        JButton searchButton = new WButton("搜索");
        searchButton.setBackground(new Color(0xff9c4b));
        searchButton.addActionListener(e -> updateData());
        toolBar.add(searchButton);

        JButton addButton = new WButton("新增");
        toolBar.add(addButton);
        addButton.addActionListener(e -> {
            showMenuAddDialog(null);
        });
        addButton.setBackground(new Color(0x1c7dfa));

        exButton = new JToggleButton("展开/折叠");
        toolBar.add(exButton);
        exButton.addActionListener(e -> {
            if (exButton.isSelected()) {
                treeTable.expandAll();
            } else {
                treeTable.collapseAll();
            }
        });


        treeTable = new JXTreeTable(new MenuTreeTableModel(null));
        treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        treeTable.setRowHeight(50);
        treeTable.setLeafIcon(null);
        treeTable.setClosedIcon(null);
        treeTable.setOpenIcon(null);
        treeTable.setShowHorizontalLines(true);
        treeTable.setShowVerticalLines(false);
        treeTable.setIntercellSpacing(new Dimension(0, 1));
        treeTable.setOpaque(false);
        treeTable.setHighlighters();
        // JXTable
        ColorHighlighter rollover = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, UIManager.getColor("App.hoverBackground"), null);
        treeTable.setHighlighters(rollover);
        treeTable.putClientProperty(JXTreeTable.USE_DTCR_COLORMEMORY_HACK, Boolean.FALSE);
//        treeTable.setSelectionBackground(UIManager.getColor("Table.selectionBackground"));
        JScrollPane tsp = new WScrollPane(treeTable);
        JPanel panel = new WPanel();
        panel.setLayout(new BorderLayout());
        panel.add(tsp);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(0, 10));
        this.add(toolBar, BorderLayout.NORTH);
        this.add(panel);


    }

    @Override
    public void updateUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ColorHighlighter rollover = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, UIManager.getColor("App.hoverBackground"), null);
                treeTable.setHighlighters(rollover);
                treeTable.setIntercellSpacing(new Dimension(0, 1));


            }
        });
        super.updateUI();

    }

    private JToolBar creatBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);
        JButton edit = new JButton("修改");
        edit.setForeground(UIManager.getColor("App.accentColor"));
        edit.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));

        edit.addActionListener(e -> showMenuEditDialog());
        edit.setForeground(UIManager.getColor("App.accentColor"));

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> delMenu());
        del.setForeground(UIManager.getColor("app-error-color-5"));

        JButton add = new JButton("新增");
        add.addActionListener(e -> {
            int selRow = treeTable.getSelectedRow();
            Long menuId = null;
            if (selRow != -1) {
                Object obj = treeTable.getPathForRow(selRow).getLastPathComponent();
                if (obj instanceof Tree) {
                    Tree tree = (Tree) obj;
                    menuId = (Long) tree.get("id");
                }
            }
            showMenuAddDialog(menuId);
        });
        add.setForeground(UIManager.getColor("App.accentColor"));
        add.setIcon(new FlatSVGIcon("icons/xinzeng.svg", 15, 15));
        optBar.add(Box.createGlue());
        optBar.add(edit);
        optBar.add(add);
        optBar.add(del);
        optBar.add(Box.createGlue());
        return optBar;

    }


    private void showMenuAddDialog(Long id) {
        menuEditPane = new MenuEditPane();
        menuEditPane.updateData(id, true);
        int opt = WOptionPane.showOptionDialog(null, menuEditPane, "添加菜单", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            addMenu();
        }
    }


    private void showMenuEditDialog() {

        int selRow = treeTable.getSelectedRow();
        Long menuId = null;
        if (selRow != -1) {
            Object obj = treeTable.getPathForRow(selRow).getLastPathComponent();
            if (obj instanceof Tree) {
                Tree tree = (Tree) obj;
                menuId = (Long) tree.get("id");
            }
        }
        menuEditPane = new MenuEditPane();
        menuEditPane.updateData(menuId, false);
        int opt = WOptionPane.showOptionDialog(null, menuEditPane, "修改菜单", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            editMenu();
        }
    }

    private void updateData() {
        MenuListReqVO sysMenuModel = new MenuListReqVO();
        sysMenuModel.setName(nameTextField.getText());
        int selectIndex = statusCombo.getSelectedIndex();
        if (selectIndex != 0) {
            sysMenuModel.setStatus(selectIndex == 1 ? 0 : 1);
        }
        Map<String, Object> queryMap= BeanUtil.beanToMap(sysMenuModel, false, true);

        RetrofitServiceManager.getInstance().create(MenuApi.class).getMenuList(queryMap).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(data -> {
                    long min = data.stream().mapToLong(value -> value.getParentId()).min().orElse(0L);
                    TreeNodeConfig config = new TreeNodeConfig();
                    config.setWeightKey("orderNum");
                    config.setParentIdKey("parentId");
                    config.setNameKey("menuName");
                    Tree<Long> treeList = TreeUtil.buildSingle(data, min, config, ((object, treeNode) -> {
                        treeNode.setId(object.getId());
                        treeNode.setParentId(object.getParentId());
                        treeNode.setName(object.getName());
                        treeNode.putExtra("icon", object.getIcon());
                        treeNode.putExtra("component", object.getComponentSwing());
                        treeNode.putExtra("orderNum", object.getSort());
                        treeNode.putExtra("status", object.getStatus());
                        treeNode.putExtra("perms", object.getPermission());
                        treeNode.putExtra("menuType", object.getType());
                        treeNode.putExtra("componentName", object.getComponentName());
                    }));
                    updateTreeTableRoot(treeList);
                    if (exButton.isSelected()) {
                        treeTable.expandAll();
                    }
                }, throwable -> throwable.printStackTrace());
    }

    private void updateTreeTableRoot(Object root) {

        treeTable.setTreeTableModel(new MenuTreeTableModel(root));
        treeTable.getColumnExt("操作").setMinWidth(240);
        treeTable.getColumnExt("操作").setMaxWidth(240);
        treeTable.getColumnExt("图标").setMinWidth(60);
        treeTable.getColumnExt("图标").setMaxWidth(60);
        treeTable.getColumnExt("排序").setWidth(80);
        treeTable.getColumnExt("排序").setMaxWidth(80);

        treeTable.getColumnExt("组件路径").setMinWidth(300);

        treeTable.getColumnExt("图标").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = new JLabel("", JLabel.CENTER);

                String icon = (String) value;
                if (StrUtil.contains(icon, ":")) {
                    icon = "icons/menu/" + icon.split(":")[1] + ".svg";
                    label.setIcon(new FlatSVGIcon(icon, 25, 25));
                } else {
                    label.setText(value.toString());
                }

                label.setHorizontalTextPosition(CENTER);
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(label);
                panel.setBackground(component.getBackground());
                return panel;
            }
        });

        treeTable.getColumnExt("状态").setCellRenderer(new DefaultTableCellRenderer() {
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
                return panel;
            }
        });
        treeTable.getColumnExt("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
        treeTable.getColumnExt("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));
        treeTable.getColumn("排序").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                JLabel label = new JLabel(value + "", JLabel.CENTER);
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(label);
                panel.setBackground(component.getBackground());
                return panel;
            }
        });
    }


    private JTextField createTextField(String placeholderText) {
        JTextField textField = new JTextField();
        textField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholderText);
        textField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        return textField;
    }


    /**
     * 添加菜单
     */
    private void addMenu() {

        MenuSaveVO menuSaveVO = menuEditPane.getMenuRespVO();

        RetrofitServiceManager.getInstance().create(MenuApi.class).createMenu(menuSaveVO).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(data -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "添加成功！");
                    updateData();
                    AppStore.getMenuRefreshObservable().refresh();
                }, throwable -> throwable.printStackTrace());


    }

    private void editMenu() {

        MenuSaveVO menuSaveVO = menuEditPane.getMenuRespVO();

        RetrofitServiceManager.getInstance().create(MenuApi.class).updateMenu(menuSaveVO).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(data -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功！");
                    updateData();
                    AppStore.getMenuRefreshObservable().refresh();
                }, throwable -> throwable.printStackTrace());

    }

    private void delMenu() {
        Long menuId = null;
        String menuName = null;

        int selRow = treeTable.getSelectedRow();
        if (selRow != -1) {
            Object obj = treeTable.getPathForRow(selRow).getLastPathComponent();
            if (obj instanceof Tree) {
                Tree tree = (Tree) obj;
                menuId = (Long) tree.get("id");
                menuName = tree.getName().toString();
            }
        }

        int opt = WOptionPane.showOptionDialog(this, "是否确定删除[" + menuName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        RetrofitServiceManager.getInstance().create(MenuApi.class).deleteMenu(menuId).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(data -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");
                    updateData();
                    AppStore.getMenuRefreshObservable().refresh();
                }, throwable -> throwable.printStackTrace());


    }


    @Override
    public void update(Observable o, Object arg) {
        if (this.isDisplayable()) {
            updateData();
        }
    }

    class MenuTreeTableModel extends AbstractTreeTableModel {


        public MenuTreeTableModel() {
        }

        public MenuTreeTableModel(Object root) {
            super(root);
        }


        public void setRoot(Object root) {
            this.root = root;
            modelSupport.fireNewRoot();
        }

        @Override
        public boolean isCellEditable(Object node, int column) {
            if (column == 7) {
                return true;
            }
            return super.isCellEditable(node, column);
        }

        @Override
        public Object getChild(Object parent, int index) {

            Tree parentTree = (Tree) parent;

            return parentTree.getChildren().get(index);
        }

        @Override
        public int getChildCount(Object parent) {
            if (parent instanceof Tree) {
                List<Tree> children = ((Tree) parent).getChildren();

                if (children != null) {
                    return children.size();
                }
            }
            return 0;
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            if (parent instanceof Tree && child instanceof Tree) {
                Tree parentFile = (Tree) parent;
                List<Tree> treeList = parentFile.getChildren();

                for (int i = 0, len = treeList.size(); i < len; i++) {
                    if (treeList.get(i).equals(child)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return String.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return String.class;
                case 4:
                    return String.class;
                default:
                    return super.getColumnClass(column);
            }
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_ID[column];
        }


        @Override
        public int getColumnCount() {
            return COLUMN_ID.length;
        }

        @Override
        public Object getValueAt(Object node, int column) {
            if (node instanceof Tree) {
                Tree tree = (Tree) node;
                switch (column) {
                    case 0:
                        return tree.getName();
                    case 1:
                        return tree.get("icon");
                    case 2:
                        return tree.get("orderNum");
                    case 3:
                        return tree.get("perms");
                    case 4:
                        return tree.get("component");
                    case 5:
                        return tree.get("componentName");
                    case 6:
                        return tree.get("status");


                }
            }

            return null;
        }

    }


}
