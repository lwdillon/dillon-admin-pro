/*
 * Created by JFormDesigner on Sun Jun 16 15:15:27 CST 2024
 */

package com.lw.swing.view.intra.config;

import cn.hutool.core.convert.Convert;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.ui.request.api.config.ConfigFeign;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

import static com.lw.ui.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;

/**
 * @author wenli
 */
public class ConfigFormPane extends JPanel {
    private Long id = null;

    public ConfigFormPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        categoryField = new JTextField();
        label2 = new JLabel();
        nameField = new JTextField();
        label3 = new JLabel();
        keyField = new JTextField();
        label6 = new JLabel();
        valueField = new JTextField();
        label4 = new JLabel();
        visibleComboBox = new JComboBox();
        label5 = new JLabel();
        scrollPane1 = new JScrollPane();
        remarkTextArea = new JTextArea();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[320:n,grow,shrink 0,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[280:n,grow]"));

        //---- label1 ----
        label1.setText("*\u53c2\u6570\u5206\u7c7b");
        add(label1, "cell 0 0");
        add(categoryField, "cell 1 0");

        //---- label2 ----
        label2.setText("*\u53c2\u6570\u540d\u79f0");
        add(label2, "cell 0 1");
        add(nameField, "cell 1 1");

        //---- label3 ----
        label3.setText("*\u53c2\u6570\u952e\u540d");
        add(label3, "cell 0 2");
        add(keyField, "cell 1 2");

        //---- label6 ----
        label6.setText("*\u53c2\u6570\u952e\u503c");
        add(label6, "cell 0 3");
        add(valueField, "cell 1 3");

        //---- label4 ----
        label4.setText("*\u662f\u5426\u53ef\u89c1");
        add(label4, "cell 0 4");
        add(visibleComboBox, "cell 1 4");

        //---- label5 ----
        label5.setText("\u5907\u6ce8");
        add(label5, "cell 0 5,aligny top,growy 0");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(remarkTextArea);
        }
        add(scrollPane1, "cell 1 5,growy");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        AppStore.getDictDataList(INFRA_BOOLEAN_STRING).forEach(dictDataSimpleRespVO -> {
            visibleComboBox.addItem(dictDataSimpleRespVO);
        });

    }

    private void setValue(ConfigRespVO respVO) {
        categoryField.setText(respVO.getCategory());
        nameField.setText(respVO.getName());
        keyField.setText(respVO.getKey());
        valueField.setText(respVO.getValue());
        visibleComboBox.setSelectedItem(AppStore.getDictDataValueMap(INFRA_BOOLEAN_STRING).get(respVO.getVisible() + ""));
        remarkTextArea.setText(respVO.getRemark());

    }

    public ConfigSaveReqVO getValue() {
        ConfigSaveReqVO reqVO = new ConfigSaveReqVO();
        reqVO.setId(id);
        reqVO.setCategory(categoryField.getText());
        reqVO.setName(nameField.getText());
        reqVO.setKey(keyField.getText());
        reqVO.setValue(valueField.getText());
        reqVO.setRemark(remarkTextArea.getText());
        reqVO.setVisible(Convert.toBool(((DictDataSimpleRespVO) visibleComboBox.getSelectedItem()).getValue()));
        return reqVO;
    }


    public void updateData(Long id) {

        this.id = id;


        SwingWorker<ConfigRespVO, ConfigRespVO> swingWorker = new SwingWorker<ConfigRespVO, ConfigRespVO>() {
            @Override
            protected ConfigRespVO doInBackground() throws Exception {
                ConfigRespVO postRespVO = new ConfigRespVO();
                if (id != null) {
                    CommonResult<ConfigRespVO> userResult = Request.connector(ConfigFeign.class).getConfig(id);
                    postRespVO = userResult.getData();
                }

                return postRespVO;
            }


            @Override
            protected void done() {

                try {
                    setValue(get());
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
    private JLabel label1;
    private JTextField categoryField;
    private JLabel label2;
    private JTextField nameField;
    private JLabel label3;
    private JTextField keyField;
    private JLabel label6;
    private JTextField valueField;
    private JLabel label4;
    private JComboBox visibleComboBox;
    private JLabel label5;
    private JScrollPane scrollPane1;
    private JTextArea remarkTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
