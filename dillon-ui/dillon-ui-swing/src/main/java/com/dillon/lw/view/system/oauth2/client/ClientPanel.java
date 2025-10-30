/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.system.oauth2.client;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.dillon.lw.module.system.controller.admin.oauth2.vo.client.OAuth2ClientRespVO;
import com.dillon.lw.module.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.http.PayLoad;
import com.dillon.lw.http.RetrofitServiceManager;
import com.dillon.lw.view.frame.MainFrame;
import com.dillon.lw.api.system.OAuth2ClientApi;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class ClientPanel extends JPanel {
    private String[] COLUMN_ID = {"客户端编号", "客户端密钥","应用名","应用图标", "状态", "访问令牌的有效期","刷新令牌的有效期","授权类型", "创建时间", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public ClientPanel() {
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
                label7.setText("岗位名称");
                toolPane.add(label7, "cell 0 0");

                //---- postNameTextField ----
                nameTextField.setColumns(15);
                toolPane.add(nameTextField, "cell 0 0");

                //---- label8 ----
                label8.setText("岗位编码");
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

        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());

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
        del.addActionListener(e -> del());
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
        codeTextField.setText("");
        stautsComboBox.setSelectedIndex(0);
    }

    private void showAddDialog(Long id) {
        ClientFormPane clientFormPane = new ClientFormPane();
        clientFormPane.updateData(id);
        int opt = WOptionPane.showOptionDialog(null, clientFormPane, "添加", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            add(clientFormPane.getValue());
        }
    }

    private void showEditDialog() {


        int selRow = table.getSelectedRow();
        Long id = null;
        if (selRow != -1) {
            OAuth2ClientRespVO auth2ClientRespVO= (OAuth2ClientRespVO) table.getValueAt(selRow, COLUMN_ID.length-1);
            id = auth2ClientRespVO.getId();
        }else {
            return;
        }


        ClientFormPane clientFormPane = new ClientFormPane();
        clientFormPane.updateData(id);
        int opt = WOptionPane.showOptionDialog(null, clientFormPane, "修改", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            edit(clientFormPane.getValue());
        }
    }



    /**
     * 添加
     */
    private void add(OAuth2ClientSaveReqVO saveReqVO) {

        RetrofitServiceManager.getInstance().create(OAuth2ClientApi.class).createOAuth2Client(saveReqVO).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(),"添加成功！");
                    updateData();
                },throwable -> {
                    throwable.printStackTrace();
                });


    }

    private void edit(OAuth2ClientSaveReqVO saveReqVO) {


        RetrofitServiceManager.getInstance().create(OAuth2ClientApi.class).updateOAuth2Client(saveReqVO).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(),"修改成功！");
                    updateData();
                },throwable -> {
                    throwable.printStackTrace();
                });


    }

    private void del() {
        Long id = null;
        String name = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            OAuth2ClientRespVO auth2ClientRespVO= (OAuth2ClientRespVO) table.getValueAt(selRow, COLUMN_ID.length-1);
            id = auth2ClientRespVO.getId();
            name = auth2ClientRespVO.getName();
        }

        int opt = WOptionPane.showOptionDialog(this, "是否确定删除[" + name + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }
        RetrofitServiceManager.getInstance().create(OAuth2ClientApi.class).deleteOAuth2Client(id).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(),"删除成功！");
                    updateData();
                },throwable -> {
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
        if (StrUtil.isNotBlank(codeTextField.getText())) {
            queryMap.put("code", codeTextField.getText());
        }
        queryMap.put("status", stautsComboBox.getSelectedIndex() == 0 ? null : (stautsComboBox.getSelectedIndex() == 1 ? 0 : 1));


        queryMap.values().removeIf(Objects::isNull);
        RetrofitServiceManager.getInstance().create(OAuth2ClientApi.class).getOAuth2ClientPage(queryMap).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    Vector<Vector> tableData = new Vector<>();
                    result.getList().forEach(roleRespVO -> {
                        Vector rowV = new Vector();
                        rowV.add(roleRespVO.getClientId());
                        rowV.add(roleRespVO.getSecret());
                        rowV.add(roleRespVO.getName());
                        rowV.add(roleRespVO.getLogo());
                        rowV.add(roleRespVO.getStatus());
                        rowV.add(roleRespVO.getAccessTokenValiditySeconds());
                        rowV.add(roleRespVO.getRefreshTokenValiditySeconds());
                        rowV.add(roleRespVO.getAuthorizedGrantTypes());
                        rowV.add(DateUtil.format(roleRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(roleRespVO);
                        tableData.add(rowV);
                    });
                    tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

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
    private JTextField codeTextField;
    private JLabel label9;
    private JComboBox stautsComboBox;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
