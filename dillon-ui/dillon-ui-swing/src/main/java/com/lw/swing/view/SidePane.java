package com.lw.swing.view;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.swing.components.WPanel;
import com.lw.swing.components.WScrollPane;
import com.lw.swing.store.AppStore;
import com.lw.swing.utils.IconLoader;
import lombok.extern.slf4j.Slf4j;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author wenli
 * @date 2024/05/09
 */
@Slf4j
public class SidePane extends WPanel {


    private JXTreeTable treeTable;

    public SidePane() {
        initComponents();
        initListeners();
        initData();

    }

    private void initComponents() {

        // JXTable

        treeTable = new JXTreeTable(new MenuModel());
        treeTable.setShowHorizontalLines(true);
        treeTable.setOpaque(false);
        treeTable.setIntercellSpacing(new Dimension(1, 1));
        // 设置表头高度
        JTableHeader header = new JTableHeader() {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(super.getMaximumSize().width, 30);
            }
        };
        treeTable.setTableHeader(header);
        treeTable.setRowHeight(45);
        treeTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, UIManager.getColor("App.hoverBackground"), null));

        treeTable.setTreeCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (component instanceof JLabel) {
                    if (value instanceof AuthPermissionInfoRespVO.MenuVO) {
                        AuthPermissionInfoRespVO.MenuVO menuVO= (AuthPermissionInfoRespVO.MenuVO) value;
                        ((JLabel) component).setText(menuVO.getName());
                        String icon = menuVO.getIcon();
                        if (StrUtil.isBlank(menuVO.getIcon())) {
                            icon="icons/item.svg";
                        }else if (StrUtil.contains(icon,":")){
                            icon="icons/menu/"+icon.split(":")[1]+".svg";
                        }
                        FlatSVGIcon svgIcon =  IconLoader.getSvgIcon(icon,25,25);


                        ((JLabel) component).setIcon(svgIcon);
                        ((JLabel) component).setIconTextGap(7);

                    }


                }
                return component;
            }
        });
        treeTable.getColumnExt(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (component instanceof JLabel) {
                    ((JLabel) component).setIcon(new FlatSVGIcon("icons/item.svg", 25, 25));
                }
                return component;
            }
        });
        WScrollPane jScrollPane = new WScrollPane(treeTable);
        jScrollPane.setPreferredSize(new Dimension(260, 0));
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.add(jScrollPane);

    }

    private void initListeners() {
        treeTable.addTreeSelectionListener(e -> {
            Object object = e.getNewLeadSelectionPath().getLastPathComponent();

            if (object instanceof AuthPermissionInfoRespVO.MenuVO) {
                if (StrUtil.isNotBlank(((AuthPermissionInfoRespVO.MenuVO) object).getComponentSwing())) {
                    MainFrame.getInstance().addTab((AuthPermissionInfoRespVO.MenuVO) object);
                }
            }
        });
    }

    private void initData() {
        SwingWorker<AuthPermissionInfoRespVO.MenuVO, Object> swingWorker = new SwingWorker<AuthPermissionInfoRespVO.MenuVO, Object>() {
            @Override
            protected AuthPermissionInfoRespVO.MenuVO doInBackground() throws Exception {
                AuthPermissionInfoRespVO.MenuVO rootNode = new AuthPermissionInfoRespVO.MenuVO();
                rootNode.setName("root");
                rootNode.setChildren(AppStore.getMenus());
                return rootNode;
            }

            @Override
            protected void done() {
                try {
                    updateTreeTableRoot(get());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();
    }

    private void updateTreeTableRoot(Object root) {

        treeTable.setTreeTableModel(new MenuModel(root));

    }

    @Override
    public void updateUI() {

        super.updateUI();

        if (treeTable != null) {

            treeTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, UIManager.getColor("App.hoverBackground"), null));

        }
    }

    /**
     * 添加功能面板
     *
     * @param node
     */
    private void addFeaturePane(JsonNode node) {
        String classPath = node.get("classPath").asText();

    }

    /**
     * 获取功能面板
     *
     * @param className
     */
    public static Container getNavigatonPanel(String className) {

        JComponent container = null;
        if (StrUtil.isNotBlank(className)) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
                container = (JComponent) clazz.newInstance();
                container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                container.setOpaque(false);
            } catch (Exception e1) {
                container = new JLabel("暂无投运", JLabel.CENTER);

                log.error("获取功能面板出错:[" + className + "] as:" + e1);
                e1.printStackTrace();
            }
        }
        return container;
    }


    class MenuModel extends AbstractTreeTableModel {

        public MenuModel() {

        }


        public MenuModel(Object root) {
            super(root);
        }

        public Object getChild(Object parent, int index) {
            AuthPermissionInfoRespVO.MenuVO parentFile = (AuthPermissionInfoRespVO.MenuVO) parent;
            List<AuthPermissionInfoRespVO.MenuVO> children = parentFile.getChildren();
            return children != null ? children.get(index) : null;

        }

        public int getChildCount(Object parent) {
            if (parent instanceof AuthPermissionInfoRespVO.MenuVO) {
                List<AuthPermissionInfoRespVO.MenuVO> children = ((AuthPermissionInfoRespVO.MenuVO) parent).getChildren();
                if (children != null) {
                    return children.size();
                }
            }

            return 0;
        }

        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return String.class;
                default:
                    return super.getColumnClass(column);
            }
        }

        public int getColumnCount() {
            return 1;
        }

        public String getColumnName(int column) {
            return "Name";
        }

        public Object getValueAt(Object node, int column) {
            if (node instanceof AuthPermissionInfoRespVO.MenuVO) {
                AuthPermissionInfoRespVO.MenuVO menuVO = (AuthPermissionInfoRespVO.MenuVO) node;
                switch (column) {
                    case 0:
                        return menuVO.getName();

                }
            }

            return null;
        }


        public void setRoot(Object root) {
            this.root = root;
            this.modelSupport.fireNewRoot();
        }

        public boolean isLeaf(Object node) {
            if (node instanceof AuthPermissionInfoRespVO.MenuVO) {
                return ((AuthPermissionInfoRespVO.MenuVO) node).getChildren() == null;
            } else {
                return true;
            }
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            if (parent instanceof AuthPermissionInfoRespVO.MenuVO && child instanceof AuthPermissionInfoRespVO.MenuVO) {
                AuthPermissionInfoRespVO.MenuVO parentFile = (AuthPermissionInfoRespVO.MenuVO) parent;
                List<AuthPermissionInfoRespVO.MenuVO> files = parentFile.getChildren();
                int i = 0;

                for (int len = files.size(); i < len; ++i) {
                    if (files.get(i).equals(child)) {
                        return i;
                    }
                }
            }

            return -1;
        }
    }

}
