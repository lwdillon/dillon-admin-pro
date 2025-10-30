/*
 * Created by JFormDesigner on Thu Jul 25 16:52:21 CST 2024
 */

package com.lw.swing.view.system.notice;

import cn.hutool.core.convert.Convert;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.template.NotifyTemplateSendReqVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.lw.swing.http.PayLoad;
import com.lw.swing.http.RetrofitServiceManager;
import com.lw.swing.store.AppStore;
import com.lw.ui.api.system.NotifyTemplateApi;
import com.lw.ui.api.system.UserApi;
import com.lw.ui.utils.DictTypeEnum;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenli
 */
public class NotifyTemplateSendPane extends JPanel {
    private Long id;
    private String code;
    private Map<String, JTextField> paramTextFieldMap = new HashMap<>();

    public NotifyTemplateSendPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label6 = new JLabel();
        scrollPane1 = new JScrollPane();
        contentArea = new JTextArea();
        label4 = new JLabel();
        userTypeComboBox = new JComboBox();
        label5 = new JLabel();
        userComboBox = new JComboBox();
        paramsPane = new JPanel();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[400:480,grow,fill]",
            // rows
            "[240,fill]" +
            "[]" +
            "[]" +
            "[22,grow]"));

        //---- label6 ----
        label6.setText("\u6a21\u677f\u5185\u5bb9");
        add(label6, "cell 0 0,aligny top,growy 0");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(contentArea);
        }
        add(scrollPane1, "cell 1 0,grow");

        //---- label4 ----
        label4.setText("*\u7528\u6237\u7c7b\u578b");
        add(label4, "cell 0 1");
        add(userTypeComboBox, "cell 1 1");

        //---- label5 ----
        label5.setText("*\u63a5\u6536\u4eba");
        add(label5, "cell 0 2");
        add(userComboBox, "cell 1 2");

        //======== paramsPane ========
        {
            paramsPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]" +
                "[grow,fill]",
                // rows
                "[]"));
        }
        add(paramsPane, "cell 0 3 2 1,grow");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on


        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.USER_TYPE);
        userTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof DictDataSimpleRespVO) {
                    value = ((DictDataSimpleRespVO) value).getLabel();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        for (DictDataSimpleRespVO dictDataSimpleRespVO : dictDataSimpleRespVOList) {
            userTypeComboBox.addItem(dictDataSimpleRespVO);
        }
    }

    private void setValue(NotifyTemplateRespVO respVO) {
        contentArea.setText(respVO.getContent());
        userComboBox.setSelectedItem(null);
        userTypeComboBox.setSelectedItem(null);
        List<String> params = respVO.getParams();
        String rows = "";
        for (int i = 0; i < params.size(); i++) {
            rows += "[]";
        }
        paramsPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill][grow,fill]",
                // rows
                rows));
        int row = 0;
        for (String param : params) {
            JLabel label = new JLabel("* 参数{" + param + "}");
            JTextField textField = new JTextField();
            paramsPane.add(label, "cell 0 " + row);
            paramsPane.add(textField, "cell 1 " + row);
            paramTextFieldMap.put(param, textField);
            row++;
        }
    }

    public NotifyTemplateSendReqVO getValue() {
        NotifyTemplateSendReqVO reqVO = new NotifyTemplateSendReqVO();
        UserSimpleRespVO userSimpleRespVO = (UserSimpleRespVO) userComboBox.getSelectedItem();
        DictDataSimpleRespVO userTypeComboBoxSelectedItem = (DictDataSimpleRespVO) userTypeComboBox.getSelectedItem();
        reqVO.setUserId(Convert.toLong(userSimpleRespVO.getId(), null));
        reqVO.setUserType(Convert.toInt(userTypeComboBoxSelectedItem.getValue(), null));
        reqVO.setTemplateCode(code);

        Map<String, Object> templateParams = new HashMap<>();
        paramTextFieldMap.forEach((param, textField) -> {
            templateParams.put(param, textField.getText());
        });
        reqVO.setTemplateParams(templateParams);

        return reqVO;
    }


    public void updateData(NotifyTemplateRespVO respVO) {
        this.id = respVO.getId();
        this.code = respVO.getCode();
        RetrofitServiceManager.getInstance().create(NotifyTemplateApi.class).getNotifyTemplate(id).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    setValue(result);
                }, throwable -> {
                    throwable.printStackTrace();
                });

        RetrofitServiceManager.getInstance().create(UserApi.class).getSimpleUserList().map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(result -> {
                    for (UserSimpleRespVO userSimpleRespVO : result) {
                        userComboBox.addItem(userSimpleRespVO);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label6;
    private JScrollPane scrollPane1;
    private JTextArea contentArea;
    private JLabel label4;
    private JComboBox userTypeComboBox;
    private JLabel label5;
    private JComboBox userComboBox;
    private JPanel paramsPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
