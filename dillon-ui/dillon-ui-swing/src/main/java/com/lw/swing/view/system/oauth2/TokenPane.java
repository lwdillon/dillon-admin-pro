/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.lw.swing.view.system.oauth2;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import com.lw.swing.components.*;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.swing.utils.BadgeLabelUtil;
import com.lw.ui.request.api.system.OAuth2TokenFeign;
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

import static com.lw.ui.utils.DictTypeEnum.USER_TYPE;
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
        del.addActionListener(e -> delMenu());
        del.setForeground(UIManager.getColor("app-error-color-5"));
        optBar.add(Box.createGlue());
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


    private void delMenu() {
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
        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(OAuth2TokenFeign.class).deleteAccessToken(finalAccessToken);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
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
            DictDataSimpleRespVO userTypeDict= (DictDataSimpleRespVO) userTypeComboBox.getSelectedItem();
            queryMap.put("userType", userTypeDict.getValue());
        }




        SwingWorker<Vector<Vector>, Long> swingWorker = new SwingWorker<Vector<Vector>, Long>() {
            @Override
            protected Vector<Vector> doInBackground() throws Exception {
                CommonResult<PageResult<OAuth2AccessTokenRespVO>> result = Request.connector(OAuth2TokenFeign.class).getAccessTokenPage(queryMap);

                Vector<Vector> tableData = new Vector<>();


                if (result.isSuccess()) {

                    result.getData().getList().forEach(roleRespVO -> {
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
    private JTextField userIdField;
    private JLabel label8;
    private JTextField clientIdFiled;
    private JLabel label9;
    private JComboBox userTypeComboBox;
    private JButton searchBut;
    private JButton reseBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
