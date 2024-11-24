/*
 * Created by JFormDesigner on Sun Jun 16 15:15:27 CST 2024
 */

package com.lw.swing.view.system.oauth2.client;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jidesoft.swing.CheckBoxList;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.oauth2.vo.client.OAuth2ClientRespVO;
import com.lw.dillon.admin.module.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import com.lw.swing.components.WScrollPane;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.ui.request.api.system.OAuth2ClientFeign;
import com.lw.ui.utils.DictTypeEnum;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author wenli
 */
public class ClientFormPane extends JPanel {
    private Long id = null;
    private JPopupMenu authorizedGrantTypesPopupMenu;
    private CheckBoxList authorizedGrantTypesCheckBoxList;

    public ClientFormPane() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label16 = new JLabel();
        clientIdTextField = new JTextField();
        label17 = new JLabel();
        secretTextField = new JTextField();
        label18 = new JLabel();
        nameTextField = new JTextField();
        label19 = new JLabel();
        logoLabel = new JLabel();
        label20 = new JLabel();
        scrollPane1 = new JScrollPane();
        descriptionTextArea = new JTextArea();
        label21 = new JLabel();
        statusComboBox = new JComboBox();
        label22 = new JLabel();
        accessTokenValiditySecondsSpinner = new JSpinner();
        label23 = new JLabel();
        refreshTokenValiditySecondsSpinner = new JSpinner();
        label24 = new JLabel();
        authorizedGrantTypesComboBox = new JTextField();
        label25 = new JLabel();
        scopesComboBox = new JTextField();
        label26 = new JLabel();
        autoApproveScopesComboBox = new JTextField();
        label27 = new JLabel();
        redirectUrisComboBox = new JTextField();
        label28 = new JLabel();
        authoritiesComboBox = new JTextField();
        label29 = new JLabel();
        resourceIdsComboBox = new JTextField();
        label30 = new JLabel();
        scrollPane2 = new JScrollPane();
        additionalInformationTextArea = new JTextArea();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[320:420,grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]"));

        //---- label16 ----
        label16.setText("\u5ba2\u6237\u7aef\u7f16\u53f7");
        add(label16, "cell 0 0");
        add(clientIdTextField, "cell 1 0");

        //---- label17 ----
        label17.setText("\u5ba2\u6237\u7aef\u5bc6\u94a5");
        add(label17, "cell 0 1");
        add(secretTextField, "cell 1 1");

        //---- label18 ----
        label18.setText("\u5e94\u7528\u540d");
        add(label18, "cell 0 2");
        add(nameTextField, "cell 1 2");

        //---- label19 ----
        label19.setText("\u5e94\u7528\u56fe\u6807");
        add(label19, "cell 0 3");

        //---- logoLabel ----
        logoLabel.setText("text");
        add(logoLabel, "cell 1 3,alignx center,growx 0");

        //---- label20 ----
        label20.setText("\u5e94\u7528\u63cf\u8ff0");
        add(label20, "cell 0 4");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(descriptionTextArea);
        }
        add(scrollPane1, "cell 1 4");

        //---- label21 ----
        label21.setText("\u72b6\u6001");
        add(label21, "cell 0 5");
        add(statusComboBox, "cell 1 5");

        //---- label22 ----
        label22.setText("\u8bbf\u95ee\u4ee4\u724c\u7684\u6709\u6548\u671f");
        add(label22, "cell 0 6");
        add(accessTokenValiditySecondsSpinner, "cell 1 6");

        //---- label23 ----
        label23.setText("\u5237\u65b0\u4ee4\u724c\u7684\u6709\u6548\u671f");
        add(label23, "cell 0 7");
        add(refreshTokenValiditySecondsSpinner, "cell 1 7");

        //---- label24 ----
        label24.setText("\u6388\u6743\u7c7b\u578b");
        add(label24, "cell 0 8");
        add(authorizedGrantTypesComboBox, "cell 1 8");

        //---- label25 ----
        label25.setText("\u6388\u6743\u8303\u56f4");
        add(label25, "cell 0 9");
        add(scopesComboBox, "cell 1 9");

        //---- label26 ----
        label26.setText("\u81ea\u52a8\u6388\u6743\u8303\u56f4");
        add(label26, "cell 0 10");
        add(autoApproveScopesComboBox, "cell 1 10");

        //---- label27 ----
        label27.setText("\u53ef\u91cd\u5b9a\u5411\u7684 URI \u5730\u5740");
        add(label27, "cell 0 11");
        add(redirectUrisComboBox, "cell 1 11");

        //---- label28 ----
        label28.setText("\u6743\u9650");
        add(label28, "cell 0 12");
        add(authoritiesComboBox, "cell 1 12");

        //---- label29 ----
        label29.setText("\u8d44\u6e90");
        add(label29, "cell 0 13");
        add(resourceIdsComboBox, "cell 1 13");

        //---- label30 ----
        label30.setText("\u9644\u52a0\u4fe1\u606f");
        add(label30, "cell 0 14");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(additionalInformationTextArea);
        }
        add(scrollPane2, "cell 1 14");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        authorizedGrantTypesPopupMenu = new JPopupMenu();
        authorizedGrantTypesCheckBoxList = new CheckBoxList();
        authorizedGrantTypesCheckBoxList.setClickInCheckBoxOnly(false);
        authorizedGrantTypesCheckBoxList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof DictDataSimpleRespVO) {
                    value = ((DictDataSimpleRespVO) value).getLabel();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        authorizedGrantTypesCheckBoxList.setFixedCellHeight(35);
        authorizedGrantTypesPopupMenu.add(new WScrollPane(authorizedGrantTypesCheckBoxList));
        authorizedGrantTypesComboBox.setEditable(false);
        statusComboBox.addItem("开启");
        statusComboBox.addItem("关闭");
        refreshTokenValiditySecondsSpinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        accessTokenValiditySecondsSpinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    }

    private void setValue(OAuth2ClientRespVO respVO) {
        clientIdTextField.setText(respVO.getClientId());
        secretTextField.setText(respVO.getSecret());
        nameTextField.setText(respVO.getName());
        logoLabel.setText(respVO.getLogo());
        descriptionTextArea.setText(respVO.getDescription());
        additionalInformationTextArea.setText(respVO.getAdditionalInformation());
        accessTokenValiditySecondsSpinner.setValue(respVO.getAccessTokenValiditySeconds() == null ? 0 : respVO.getAccessTokenValiditySeconds());
        refreshTokenValiditySecondsSpinner.setValue(respVO.getRefreshTokenValiditySeconds() == null ? 0 : respVO.getRefreshTokenValiditySeconds());
        statusComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(respVO.getStatus(), 0));

        scopesComboBox.setText(Optional.ofNullable(respVO.getScopes()).orElse(Arrays.asList()) // 如果为 null，替换为一个空 List
                .stream().collect(Collectors.joining(",")));
        autoApproveScopesComboBox.setText(Optional.ofNullable(respVO.getAutoApproveScopes()).orElse(Arrays.asList()) // 如果为 null，替换为一个空 List
                .stream().collect(Collectors.joining(",")));
        redirectUrisComboBox.setText(Optional.ofNullable(respVO.getRedirectUris()).orElse(Arrays.asList()) // 如果为 null，替换为一个空 List
                .stream().collect(Collectors.joining(",")));
        authoritiesComboBox.setText(Optional.ofNullable(respVO.getAuthorities()).orElse(Arrays.asList()) // 如果为 null，替换为一个空 List
                .stream().collect(Collectors.joining(",")));
        resourceIdsComboBox.setText(Optional.ofNullable(respVO.getResourceIds()).orElse(Arrays.asList()) // 如果为 null，替换为一个空 List
                .stream().collect(Collectors.joining(",")));


    }

    public OAuth2ClientSaveReqVO getValue() {
        OAuth2ClientSaveReqVO reqVO = new OAuth2ClientSaveReqVO();
        reqVO.setId(id);
        reqVO.setClientId(clientIdTextField.getText());
        reqVO.setSecret(secretTextField.getText());
        reqVO.setName(nameTextField.getText());
        reqVO.setLogo(logoLabel.getText());
        reqVO.setDescription(descriptionTextArea.getText());
        reqVO.setAccessTokenValiditySeconds(Convert.toInt(accessTokenValiditySecondsSpinner.getValue()));
        reqVO.setRefreshTokenValiditySeconds(Convert.toInt(refreshTokenValiditySecondsSpinner.getValue()));
        reqVO.setStatus(statusComboBox.getSelectedIndex());
        Object[] types = authorizedGrantTypesCheckBoxList.getCheckBoxListSelectedValues();
        List<String> stringList = Arrays.stream(types).map(Object::toString)  // 转换为 String
                .collect(Collectors.toList());
        reqVO.setAuthorizedGrantTypes(stringList);


        reqVO.setScopes(Optional.ofNullable(scopesComboBox.getText()).filter(s -> !s.isEmpty()).map(s -> Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList())) //
                .orElse(Collections.emptyList()));
        reqVO.setAutoApproveScopes(Optional.ofNullable(autoApproveScopesComboBox.getText()).filter(s -> !s.isEmpty()).map(s -> Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList())) //
                .orElse(Collections.emptyList()));
        reqVO.setRedirectUris(Optional.ofNullable(redirectUrisComboBox.getText()).filter(s -> !s.isEmpty()).map(s -> Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList())) //
                .orElse(Collections.emptyList()));
        reqVO.setAuthorities(Optional.ofNullable(authoritiesComboBox.getText()).filter(s -> !s.isEmpty()).map(s -> Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList())) //
                .orElse(Collections.emptyList()));
        reqVO.setResourceIds(Optional.ofNullable(resourceIdsComboBox.getText()).filter(s -> !s.isEmpty()).map(s -> Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList())) //
                .orElse(Collections.emptyList()));

        return reqVO;
    }

    private void initListeners() {

        authorizedGrantTypesComboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    showAuthorizedTypePopupMenu();
                }
            }
        });
        authorizedGrantTypesCheckBoxList.getCheckBoxListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Object[] objects = authorizedGrantTypesCheckBoxList.getCheckBoxListSelectedValues();
                    authorizedGrantTypesComboBox.setText(ArrayUtil.join(objects, ",", new Editor<Object>() {
                        @Override
                        public Object edit(Object o) {
                            if (o instanceof PostSimpleRespVO) {
                                return ((PostSimpleRespVO) o).getName();
                            }
                            return o;
                        }
                    }));
                }
            }
        });
    }

    private void showAuthorizedTypePopupMenu() {
        authorizedGrantTypesPopupMenu.setPopupSize(authorizedGrantTypesComboBox.getWidth(), 400);
        authorizedGrantTypesPopupMenu.show(authorizedGrantTypesComboBox, 0, authorizedGrantTypesComboBox.getHeight());
    }


    public void updateData(Long id) {

        this.id = id;


        SwingWorker<OAuth2ClientRespVO, OAuth2ClientRespVO> swingWorker = new SwingWorker<OAuth2ClientRespVO, OAuth2ClientRespVO>() {
            @Override
            protected OAuth2ClientRespVO doInBackground() throws Exception {
                OAuth2ClientRespVO auth2ClientRespVO = new OAuth2ClientRespVO();
                if (id != null) {
                    CommonResult<OAuth2ClientRespVO> userResult = Request.connector(OAuth2ClientFeign.class).getOAuth2Client(id);
                    auth2ClientRespVO = userResult.getData();
                }

                return auth2ClientRespVO;
            }


            @Override
            protected void done() {

                try {
                    setValue(get());
                    List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.SYSTEM_OAUTH2_GRANT_TYPE);
                    // 提取所有 label
                    List<String> labels = dictDataSimpleRespVOList.stream().map(DictDataSimpleRespVO::getLabel).collect(Collectors.toList());
                    authorizedGrantTypesCheckBoxList.setModel(new DefaultComboBoxModel(labels.toArray()));
                    authorizedGrantTypesCheckBoxList.setSelectedObjects(get().getAuthorizedGrantTypes().toArray());
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
    private JLabel label16;
    private JTextField clientIdTextField;
    private JLabel label17;
    private JTextField secretTextField;
    private JLabel label18;
    private JTextField nameTextField;
    private JLabel label19;
    private JLabel logoLabel;
    private JLabel label20;
    private JScrollPane scrollPane1;
    private JTextArea descriptionTextArea;
    private JLabel label21;
    private JComboBox statusComboBox;
    private JLabel label22;
    private JSpinner accessTokenValiditySecondsSpinner;
    private JLabel label23;
    private JSpinner refreshTokenValiditySecondsSpinner;
    private JLabel label24;
    private JTextField authorizedGrantTypesComboBox;
    private JLabel label25;
    private JTextField scopesComboBox;
    private JLabel label26;
    private JTextField autoApproveScopesComboBox;
    private JLabel label27;
    private JTextField redirectUrisComboBox;
    private JLabel label28;
    private JTextField authoritiesComboBox;
    private JLabel label29;
    private JTextField resourceIdsComboBox;
    private JLabel label30;
    private JScrollPane scrollPane2;
    private JTextArea additionalInformationTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
