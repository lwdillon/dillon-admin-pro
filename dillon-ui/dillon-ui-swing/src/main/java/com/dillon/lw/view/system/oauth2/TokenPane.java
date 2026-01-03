/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.system.oauth2;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.api.system.OAuth2TokenApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.BadgeLabelUtil;
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
import java.util.concurrent.CompletableFuture;

import static com.dillon.lw.utils.DictTypeEnum.USER_TYPE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.WARNING_MESSAGE;

/**
 * @author wenli
 */
public class TokenPane extends JPanel {
    private String[] COLUMN_ID = {"访问令牌", "刷新令牌", "用户编号", "用户类型", "过期时间", "创建时间", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public TokenPane() {
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
        userIdField = new JTextField();
        label8 = new JLabel();
        clientIdFiled = new JTextField();
        label9 = new JLabel();
        userTypeComboBox = new JComboBox();
        searchBut = new JButton();
        reseBut = new JButton();

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
                label7.setText("用户编号");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                userIdField.setColumns(15);
                toolPane.add(userIdField, "cell 0 0");

                //---- label8 ----
                label8.setText("客户端编号");
                toolPane.add(label8, "cell 0 0");

                //---- phoneTextField ----
                clientIdFiled.setColumns(15);
                toolPane.add(clientIdFiled, "cell 0 0");

                //---- label9 ----
                label9.setText("用户类型");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(userTypeComboBox, "cell 0 0");


                //---- button1 ----
                searchBut.setText("搜索");
                toolPane.add(searchBut, "cell 0 0");

                //---- reseBut ----
                reseBut.setText("重置");
                toolPane.add(reseBut, "cell 0 0");


            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);

        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());


        AppStore.getDictDataList(USER_TYPE).forEach(dictDataSimpleRespVO -> {
            userTypeComboBox.addItem(dictDataSimpleRespVO);
        });
        userTypeComboBox.setSelectedItem(null);
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


        JButton del = new JButton("强退");
        del.setIcon(new FlatSVGIcon("icons/logout.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("App.danger.color"));        optBar.add(Box.createGlue());
        optBar.add(del);
        optBar.add(Box.createGlue());
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> updateData());
    }


    private void reset() {
        userIdField.setText("");
        clientIdFiled.setText("");
        userTypeComboBox.setSelectedItem(null);
        updateData();
    }


    private void del() {
        String accessToken = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            accessToken = Convert.toStr(table.getValueAt(selRow, 0));
        }

        int opt = WOptionPane.showOptionDialog(this, "是否要强制退出用户？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        String finalAccessToken = accessToken;
        CompletableFuture.supplyAsync(() -> {
            return Forest.client(OAuth2TokenApi.class).deleteAccessToken(finalAccessToken).getCheckedData();
        }).thenAcceptAsync(result -> {
            updateData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }


    public void updateData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (StrUtil.isNotBlank(userIdField.getText())) {
            queryMap.put("userId", userIdField.getText());
        }
        if (StrUtil.isNotBlank(clientIdFiled.getText())) {
            queryMap.put("clientId", clientIdFiled.getText());
        }
        if (userTypeComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO userTypeDict = (DictDataSimpleRespVO) userTypeComboBox.getSelectedItem();
            queryMap.put("userType", userTypeDict.getValue());
        }

        queryMap.values().removeIf(Objects::isNull);
        CompletableFuture.supplyAsync(() -> {
            return Forest.client(OAuth2TokenApi.class).getAccessTokenPage(queryMap).getCheckedData();
        }).thenAcceptAsync(result -> {
            Vector<Vector> tableData = new Vector<>();
            result.getList().forEach(roleRespVO -> {
                Vector rowV = new Vector();
                rowV.add(roleRespVO.getAccessToken());
                rowV.add(roleRespVO.getRefreshToken());
                rowV.add(roleRespVO.getUserId());
                rowV.add(roleRespVO.getUserType());
                rowV.add(DateUtil.format(roleRespVO.getExpiresTime(), "yyyy-MM-dd HH:mm:ss"));
                rowV.add(DateUtil.format(roleRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                rowV.add(roleRespVO);
                tableData.add(rowV);
            });

            tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
            table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
            table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

            table.getColumn("用户类型").setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                    JLabel label = BadgeLabelUtil.getBadgeLabel(USER_TYPE, value);

                    panel.add(label);
                    panel.setBackground(component.getBackground());
                    panel.setOpaque(isSelected);
                    return panel;
                }
            });

            paginationPane.setTotal(result.getTotal());
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
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
    private JTextField userIdField;
    private JLabel label8;
    private JTextField clientIdFiled;
    private JLabel label9;
    private JComboBox userTypeComboBox;
    private JButton searchBut;
    private JButton reseBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
