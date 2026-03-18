/*
 * Created by JFormDesigner on Sun Jun 16 15:15:27 CST 2024
 */

package com.dillon.lw.view.intra.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.infra.ConfigApi;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.store.AppStore;
import com.dtflys.forest.Forest;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import static com.dillon.lw.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.swing.rx.SwingRx;

/**
 * @author wenli
 */
public class ConfigFormPane extends com.dillon.lw.components.AbstractDisposablePanel {
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

    /**
     * 验证表单
     *
     * @return 验证失败的错误消息，null表示验证通过
     */
    public String validates() {
        if (StrUtil.isBlank(categoryField.getText())) {
            return "请输入参数分类";
        }
        if (StrUtil.isBlank(nameField.getText())) {
            return "请输入参数名称";
        }
        if (StrUtil.isBlank(keyField.getText())) {
            return "请输入参数键名";
        }
        if (StrUtil.isBlank(valueField.getText())) {
            return "请输入参数键值";
        }
        return null;
    }


    public void updateData(Long id) {
        this.id = id;
        if (id != null) {
            Single
                    /*
                     * 编辑参数时先在后台读取详情，避免弹窗打开时卡住 EDT。
                     */
                    .fromCallable(() -> Forest.client(ConfigApi.class).getConfig(id).getCheckedData())
                    .subscribeOn(Schedulers.io())
                    .observeOn(SwingSchedulers.edt())
                    .compose(SwingRx.bindTo(this))
                    .subscribe(this::setValue, SwingExceptionHandler::handle);
        } else {
            setValue(new ConfigRespVO());
        }
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
