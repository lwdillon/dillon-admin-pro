/*
 * Created by JFormDesigner on Sun Jul 28 10:24:06 CST 2024
 */

package com.lw.swing.view.intra.file;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonObject;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.swing.http.PayLoad;
import com.lw.swing.http.RetrofitServiceManager;
import com.lw.swing.store.AppStore;
import com.lw.ui.api.file.FileConfigApi;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static com.lw.ui.utils.DictTypeEnum.INFRA_FILE_STORAGE;

/**
 * @author wenli
 */
public class FileConfigFromPane extends JPanel {
    private Long id = null;

    public FileConfigFromPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        namePane = new JPanel();
        label1 = new JLabel();
        nameField = new JTextField();
        remarkPane = new JPanel();
        label2 = new JLabel();
        remarkField = new JTextField();
        storagePane = new JPanel();
        label3 = new JLabel();
        storageComboBox = new JComboBox();
        basePathPane = new JPanel();
        label4 = new JLabel();
        basePathField = new JTextField();
        hostPane = new JPanel();
        label5 = new JLabel();
        hostField = new JTextField();
        portPane = new JPanel();
        label6 = new JLabel();
        portField = new JTextField();
        usernamePane = new JPanel();
        label7 = new JLabel();
        usernameField = new JTextField();
        passwordPane = new JPanel();
        label8 = new JLabel();
        passwordField = new JTextField();
        modePane = new JPanel();
        label9 = new JLabel();
        panel3 = new JPanel();
        activeRb = new JRadioButton();
        passiveRb = new JRadioButton();
        endpointPane = new JPanel();
        label10 = new JLabel();
        endpointField = new JTextField();
        bucketPane = new JPanel();
        label11 = new JLabel();
        bucketField = new JTextField();
        accessKeyPane = new JPanel();
        label13 = new JLabel();
        accessKeyField = new JTextField();
        accessSecretPane = new JPanel();
        label14 = new JLabel();
        accessSecretField = new JTextField();
        domainPane = new JPanel();
        label15 = new JLabel();
        domainField = new JTextField();

        //======== this ========
        setMinimumSize(new Dimension(340, 500));
        setLayout(new MigLayout(
            "fill,insets 0,gap 10 5",
            // columns
            "[500,grow,fill]",
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
            "[]"));

        //======== namePane ========
        {
            namePane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[320,fill]",
                // rows
                "[]"));

            //---- label1 ----
            label1.setText("\u914d\u7f6e\u540d");
            label1.setMinimumSize(new Dimension(80, 16));
            label1.setHorizontalAlignment(SwingConstants.RIGHT);
            namePane.add(label1, "cell 0 0");

            //---- nameField ----
            nameField.setMinimumSize(new Dimension(180, 34));
            nameField.setPreferredSize(new Dimension(240, 34));
            namePane.add(nameField, "cell 0 0,growx");
        }
        add(namePane, "cell 0 0,grow");

        //======== remarkPane ========
        {
            remarkPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label2 ----
            label2.setText("\u5907\u6ce8");
            label2.setMinimumSize(new Dimension(80, 16));
            label2.setHorizontalAlignment(SwingConstants.RIGHT);
            remarkPane.add(label2, "cell 0 0");
            remarkPane.add(remarkField, "cell 0 0,growx");
        }
        add(remarkPane, "cell 0 1,grow");

        //======== storagePane ========
        {
            storagePane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label3 ----
            label3.setText("\u5b58\u50a8\u5668");
            label3.setMinimumSize(new Dimension(80, 16));
            label3.setHorizontalAlignment(SwingConstants.RIGHT);
            storagePane.add(label3, "cell 0 0");
            storagePane.add(storageComboBox, "cell 0 0,growx");
        }
        add(storagePane, "cell 0 2,grow");

        //======== basePathPane ========
        {
            basePathPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label4 ----
            label4.setText("\u57fa\u7840\u8def\u5f84");
            label4.setMinimumSize(new Dimension(80, 16));
            label4.setHorizontalAlignment(SwingConstants.RIGHT);
            basePathPane.add(label4, "cell 0 0");
            basePathPane.add(basePathField, "cell 0 0,growx");
        }
        add(basePathPane, "cell 0 3,grow");

        //======== hostPane ========
        {
            hostPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label5 ----
            label5.setText("\u4e3b\u673a\u5730\u5740");
            label5.setMinimumSize(new Dimension(80, 16));
            label5.setHorizontalAlignment(SwingConstants.RIGHT);
            hostPane.add(label5, "cell 0 0");
            hostPane.add(hostField, "cell 0 0,growx");
        }
        add(hostPane, "cell 0 4,grow");

        //======== portPane ========
        {
            portPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label6 ----
            label6.setText("\u4e3b\u673a\u7aef\u53e3");
            label6.setMinimumSize(new Dimension(80, 16));
            label6.setHorizontalAlignment(SwingConstants.RIGHT);
            portPane.add(label6, "cell 0 0");
            portPane.add(portField, "cell 0 0,growx");
        }
        add(portPane, "cell 0 5,grow");

        //======== usernamePane ========
        {
            usernamePane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label7 ----
            label7.setText("\u7528\u6237\u540d");
            label7.setMinimumSize(new Dimension(80, 16));
            label7.setHorizontalAlignment(SwingConstants.RIGHT);
            usernamePane.add(label7, "cell 0 0");
            usernamePane.add(usernameField, "cell 0 0,growx");
        }
        add(usernamePane, "cell 0 6,grow");

        //======== passwordPane ========
        {
            passwordPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label8 ----
            label8.setText("\u5bc6\u7801");
            label8.setMinimumSize(new Dimension(80, 16));
            label8.setHorizontalAlignment(SwingConstants.RIGHT);
            passwordPane.add(label8, "cell 0 0");
            passwordPane.add(passwordField, "cell 0 0,growx");
        }
        add(passwordPane, "cell 0 7,grow");

        //======== modePane ========
        {
            modePane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label9 ----
            label9.setText("\u8fde\u63a5\u6a21\u5f0f");
            label9.setMinimumSize(new Dimension(80, 16));
            label9.setHorizontalAlignment(SwingConstants.RIGHT);
            modePane.add(label9, "cell 0 0");

            //======== panel3 ========
            {
                panel3.setLayout(new HorizontalLayout());

                //---- activeRb ----
                activeRb.setText("\u4e3b\u52a8\u6a21\u5f0f");
                panel3.add(activeRb);

                //---- passiveRb ----
                passiveRb.setText("\u88ab\u52a8\u6a21\u5f0f");
                panel3.add(passiveRb);
            }
            modePane.add(panel3, "cell 0 0,growx");
        }
        add(modePane, "cell 0 8,grow");

        //======== endpointPane ========
        {
            endpointPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label10 ----
            label10.setText("\u8282\u70b9\u5730\u5740");
            label10.setHorizontalAlignment(SwingConstants.RIGHT);
            label10.setMinimumSize(new Dimension(80, 16));
            endpointPane.add(label10, "cell 0 0,alignx left,growx 0");
            endpointPane.add(endpointField, "cell 0 0,growx");
        }
        add(endpointPane, "cell 0 9,grow");

        //======== bucketPane ========
        {
            bucketPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label11 ----
            label11.setText("\u5b58\u50a8 bucket");
            label11.setMinimumSize(new Dimension(80, 16));
            label11.setHorizontalAlignment(SwingConstants.RIGHT);
            bucketPane.add(label11, "cell 0 0");
            bucketPane.add(bucketField, "cell 0 0,growx");
        }
        add(bucketPane, "cell 0 10,grow");

        //======== accessKeyPane ========
        {
            accessKeyPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label13 ----
            label13.setText("accessKey");
            label13.setMinimumSize(new Dimension(80, 16));
            label13.setHorizontalAlignment(SwingConstants.RIGHT);
            accessKeyPane.add(label13, "cell 0 0");
            accessKeyPane.add(accessKeyField, "cell 0 0,growx");
        }
        add(accessKeyPane, "cell 0 11,grow");

        //======== accessSecretPane ========
        {
            accessSecretPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label14 ----
            label14.setText("accessSecret");
            label14.setMinimumSize(new Dimension(80, 16));
            label14.setHorizontalAlignment(SwingConstants.RIGHT);
            accessSecretPane.add(label14, "cell 0 0");
            accessSecretPane.add(accessSecretField, "cell 0 0,growx");
        }
        add(accessSecretPane, "cell 0 12,grow");

        //======== domainPane ========
        {
            domainPane.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label15 ----
            label15.setText("\u81ea\u5b9a\u4e49\u57df\u540d");
            label15.setMinimumSize(new Dimension(80, 16));
            label15.setHorizontalAlignment(SwingConstants.RIGHT);
            domainPane.add(label15, "cell 0 0");
            domainPane.add(domainField, "cell 0 0,growx");
        }
        add(domainPane, "cell 0 13,grow");

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(activeRb);
        buttonGroup1.add(passiveRb);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on


        AppStore.getDictDataList(INFRA_FILE_STORAGE).forEach(dictDataSimpleRespVO -> {
            storageComboBox.addItem(dictDataSimpleRespVO);
        });

        storageComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                showStorage(((DictDataSimpleRespVO) e.getItem()).getLabel());   //修改后
            }
        });

        showStorage("");
    }

    private void showStorage(String type) {

        domainPane.setVisible(false);
        basePathPane.setVisible(false);
        hostPane.setVisible(false);
        portPane.setVisible(false);
        usernamePane.setVisible(false);
        passwordPane.setVisible(false);
        endpointPane.setVisible(false);
        bucketPane.setVisible(false);
        accessKeyPane.setVisible(false);
        accessSecretPane.setVisible(false);
        modePane.setVisible(false);

        switch (type) {
            case "数据库": {
                domainPane.setVisible(true);
                break;
            }
            case "本地磁盘": {
                domainPane.setVisible(true);
                basePathPane.setVisible(true);
                break;
            }
            case "FTP 服务器": {
                domainPane.setVisible(true);
                basePathPane.setVisible(true);
                hostPane.setVisible(true);
                portPane.setVisible(true);
                usernamePane.setVisible(true);
                passwordPane.setVisible(true);
                break;
            }
            case "SFTP 服务器": {
                domainPane.setVisible(true);
                basePathPane.setVisible(true);
                hostPane.setVisible(true);
                portPane.setVisible(true);
                usernamePane.setVisible(true);
                passwordPane.setVisible(true);
                modePane.setVisible(true);
                break;
            }
            case "S3 对象存储": {

                endpointPane.setVisible(true);
                bucketPane.setVisible(true);
                accessKeyPane.setVisible(true);
                accessSecretPane.setVisible(true);
                break;
            }
            default: {

            }
        }
        revalidate();
        repaint();
    }

    public void updateData(Long id) {

        this.id = id;
        if (id != null) {
            RetrofitServiceManager.getInstance().create(FileConfigApi.class).getFileConfig(id).map(new PayLoad<>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                    .subscribe(jsonObject -> {
                        FileConfigSaveReqVO saveReqVO = new FileConfigSaveReqVO();

                        saveReqVO.setName(jsonObject.get("name").getAsString());
                        saveReqVO.setStorage(jsonObject.get("storage").getAsInt());
                        saveReqVO.setRemark(jsonObject.get("remark").getAsString());
                        saveReqVO.setId(jsonObject.get("id").getAsLong());
                        JsonObject config = jsonObject.getAsJsonObject("config");
                        Map<String, Object> map = new HashMap<>();
                        Iterator<String> keys = config.keySet().iterator();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            Object value = config.get(key).getAsString();
                            map.put(key, value);
                        }
                        saveReqVO.setConfig(map);

                        setValue(saveReqVO);
                    }, throwable -> {
                        throwable.printStackTrace();
                    });
        }else {
            setValue(new FileConfigSaveReqVO());
        }



    }

    private void setValue(FileConfigSaveReqVO rel) {
        nameField.setText(rel.getName());
        remarkField.setText(rel.getRemark());
        DictDataSimpleRespVO sel = AppStore.getDictDataValueMap(INFRA_FILE_STORAGE).get(rel.getStorage() + "");
        storageComboBox.setSelectedItem(sel);
        if (rel.getConfig() != null) {

            domainField.setText(Convert.toStr(rel.getConfig().get("domain")));
            basePathField.setText(Convert.toStr(rel.getConfig().get("basePath")));
            hostField.setText(Convert.toStr(rel.getConfig().get("host")));
            portField.setText(Convert.toStr(rel.getConfig().get("port")));
            usernameField.setText(Convert.toStr(rel.getConfig().get("username")));
            passwordField.setText(Convert.toStr(rel.getConfig().get("password")));
            activeRb.setSelected(StrUtil.equals(Convert.toStr(rel.getConfig().get("mode")), "Active"));
            endpointField.setText(Convert.toStr(rel.getConfig().get("endpoint")));
            bucketField.setText(Convert.toStr(rel.getConfig().get("bucket")));
            accessKeyField.setText(Convert.toStr(rel.getConfig().get("accessKey")));
            accessSecretField.setText(Convert.toStr(rel.getConfig().get("accessSecret")));

        }

        showStorage(Optional.ofNullable(sel).map(DictDataSimpleRespVO::getLabel).orElse(""));

    }

    public FileConfigSaveReqVO getValue() {
        FileConfigSaveReqVO saveReqVO = new FileConfigSaveReqVO();

        saveReqVO.setName(nameField.getText());
        saveReqVO.setId(id);
        saveReqVO.setRemark(remarkField.getText());
        if (storageComboBox.getSelectedItem() != null) {
            DictDataSimpleRespVO dictDataSimpleRespVO = (DictDataSimpleRespVO) storageComboBox.getSelectedItem();
            saveReqVO.setStorage(Convert.toInt(dictDataSimpleRespVO.getValue()));
        }
        Map<String, Object> config = new HashMap<>();
        createConfig("domain", domainField.getText(), config);
        createConfig("basePath", basePathField.getText(), config);
        createConfig("host", hostField.getText(), config);
        createConfig("username", usernameField.getText(), config);
        createConfig("mode", activeRb.isSelected() ? "Active" : "Passive", config);
        createConfig("endpoint", endpointField.getText(), config);
        createConfig("bucket", bucketField.getText(), config);
        createConfig("accessKey", accessKeyField.getText(), config);
        createConfig("accessSecret", accessSecretField.getText(), config);
        saveReqVO.setConfig(config);
        return saveReqVO;
    }

    private void createConfig(String key, String value, Map<String, Object> config) {

        if (StrUtil.isNotBlank(value)) {
            config.put(key, value);
        }

    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel namePane;
    private JLabel label1;
    private JTextField nameField;
    private JPanel remarkPane;
    private JLabel label2;
    private JTextField remarkField;
    private JPanel storagePane;
    private JLabel label3;
    private JComboBox storageComboBox;
    private JPanel basePathPane;
    private JLabel label4;
    private JTextField basePathField;
    private JPanel hostPane;
    private JLabel label5;
    private JTextField hostField;
    private JPanel portPane;
    private JLabel label6;
    private JTextField portField;
    private JPanel usernamePane;
    private JLabel label7;
    private JTextField usernameField;
    private JPanel passwordPane;
    private JLabel label8;
    private JTextField passwordField;
    private JPanel modePane;
    private JLabel label9;
    private JPanel panel3;
    private JRadioButton activeRb;
    private JRadioButton passiveRb;
    private JPanel endpointPane;
    private JLabel label10;
    private JTextField endpointField;
    private JPanel bucketPane;
    private JLabel label11;
    private JTextField bucketField;
    private JPanel accessKeyPane;
    private JLabel label13;
    private JTextField accessKeyField;
    private JPanel accessSecretPane;
    private JLabel label14;
    private JTextField accessSecretField;
    private JPanel domainPane;
    private JLabel label15;
    private JTextField domainField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
