/*
 * Created by JFormDesigner on Sun Jun 16 16:33:06 CST 2024
 */

package com.lw.swing.view.system.dict.data;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.ui.request.api.system.DictDataFeign;
import com.lw.ui.utils.DictTypeEnum;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

/**
 * @author wenli
 */
public class DictDataFormPane extends JPanel {
    private Long id = null;

    public DictDataFormPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        dictTypeTextField = new JTextField();
        label2 = new JLabel();
        labelTextField = new JTextField();
        label3 = new JLabel();
        valueTextField = new JTextField();
        label4 = new JLabel();
        sortSpinner = new JSpinner();
        label5 = new JLabel();
        statusComboBox = new JComboBox();
        label6 = new JLabel();
        colorTypeComboBox = new JComboBox();
        label7 = new JLabel();
        cssClassTextField = new JTextField();
        label8 = new JLabel();
        scrollPane1 = new JScrollPane();
        remarkTextArea = new JTextArea();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[320:n,grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[280:n,grow]"));

        //---- label1 ----
        label1.setText("\u5b57\u5178\u7c7b\u578b");
        add(label1, "cell 0 0");

        //---- dictTypeTextField ----
        dictTypeTextField.setEditable(false);
        add(dictTypeTextField, "cell 1 0");

        //---- label2 ----
        label2.setText("*\u6570\u636e\u6807\u7b7e ");
        add(label2, "cell 0 1");
        add(labelTextField, "cell 1 1");

        //---- label3 ----
        label3.setText("*\u6570\u636e\u952e\u503c");
        add(label3, "cell 0 2");
        add(valueTextField, "cell 1 2");

        //---- label4 ----
        label4.setText("*\u663e\u793a\u6392\u5e8f");
        add(label4, "cell 0 3");
        add(sortSpinner, "cell 1 3");

        //---- label5 ----
        label5.setText("*\u72b6\u6001");
        add(label5, "cell 0 4");
        add(statusComboBox, "cell 1 4");

        //---- label6 ----
        label6.setText("\u989c\u8272\u7c7b\u578b ");
        add(label6, "cell 0 5");
        add(colorTypeComboBox, "cell 1 5");

        //---- label7 ----
        label7.setText("CSS Class");
        add(label7, "cell 0 6");
        add(cssClassTextField, "cell 1 6");

        //---- label8 ----
        label8.setText("\u5907\u6ce8");
        add(label8, "cell 0 7");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(remarkTextArea);
        }
        add(scrollPane1, "cell 1 7,grow");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        statusComboBox.addItem("开启");
        statusComboBox.addItem("关闭");

        SpinnerModel model = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        sortSpinner.setModel(model);

        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.SYSTEM_DATA_SCOPE);
        colorTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof ColorTypeOptions) {
                    value = ((ColorTypeOptions) value).getLabel() + " ( " + ((ColorTypeOptions) value).getValue() + " )";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });


    }

    public void setDictType(String dictType){
        dictTypeTextField.setText(dictType);
    }
    private void setValue(DictDataRespVO respVO) {
        if (respVO == null) {
            return;
        }
        dictTypeTextField.setText(respVO.getDictType());
        labelTextField.setText(respVO.getLabel());
        valueTextField.setText(respVO.getValue());
        valueTextField.setText(respVO.getValue());
        cssClassTextField.setText(respVO.getCssClass());
        colorTypeComboBox.setSelectedItem(respVO.getColorType());
        sortSpinner.setValue(respVO.getSort() == null ? 0 : respVO.getSort());
        statusComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(respVO.getStatus(), 0));
        remarkTextArea.setText(respVO.getRemark());

    }

    public DictDataSaveReqVO getValue() {
        DictDataSaveReqVO reqVO = new DictDataSaveReqVO();
        reqVO.setId(id);
        reqVO.setDictType(dictTypeTextField.getText());
        reqVO.setLabel(labelTextField.getText());
        reqVO.setValue(valueTextField.getText());
        reqVO.setSort(Convert.toInt(sortSpinner.getValue()));
        reqVO.setRemark(remarkTextArea.getText());
        reqVO.setStatus(statusComboBox.getSelectedIndex());
        reqVO.setColorType(colorTypeComboBox.getSelectedItem() instanceof ColorTypeOptions ? ((ColorTypeOptions) colorTypeComboBox.getSelectedItem()).getValue() : null);
        reqVO.setCssClass(cssClassTextField.getText());
        return reqVO;
    }


    public void updateData(Long id) {

        this.id = id;

        SwingWorker<Map<String, Object>, DictDataRespVO> swingWorker = new SwingWorker<Map<String, Object>, DictDataRespVO>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                DictDataRespVO respVO = null;
                if (id != null) {
                    CommonResult<DictDataRespVO> userResult = Request.buildApiClient(DictDataFeign.class).getDictData(id);
                    respVO = userResult.getData();
                }

                Vector<ColorTypeOptions> colorTypeOptionsVector = new Vector<>();
                colorTypeOptionsVector.add(new ColorTypeOptions("默认", "default"));
                colorTypeOptionsVector.add(new ColorTypeOptions("主要", "primary"));
                colorTypeOptionsVector.add(new ColorTypeOptions("成功", "success"));
                colorTypeOptionsVector.add(new ColorTypeOptions("信息", "info"));
                colorTypeOptionsVector.add(new ColorTypeOptions("警告", "warning"));
                colorTypeOptionsVector.add(new ColorTypeOptions("危险", "danger"));
                ColorTypeOptions colorTypeOptionSel = null;
                if (respVO != null) {

                    for (ColorTypeOptions colorTypeOptions : colorTypeOptionsVector) {

                        if (StrUtil.equals(respVO.getColorType(), colorTypeOptions.getValue())) {
                            colorTypeOptionSel = colorTypeOptions;
                        }
                    }
                }

                Map<String, Object> map = new HashMap<>();
                map.put("respVO", respVO);
                map.put("colorTypeOptionSel", colorTypeOptionSel);
                map.put("colorTypeOptionsVector", colorTypeOptionsVector);
                return map;
            }


            @Override
            protected void done() {

                try {
                    setValue((DictDataRespVO) get().get("respVO"));
                    colorTypeComboBox.setModel(new DefaultComboBoxModel((Vector) get().get("colorTypeOptionsVector")));

                    ColorTypeOptions colorTypeOptionSel = (ColorTypeOptions) get().get("colorTypeOptionSel");
                    if (colorTypeOptionSel != null) {
                        colorTypeComboBox.setSelectedItem(colorTypeOptionSel);
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

    // 数据标签回显样式
    class ColorTypeOptions {
        private String label;
        private String value;

        public ColorTypeOptions(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField dictTypeTextField;
    private JLabel label2;
    private JTextField labelTextField;
    private JLabel label3;
    private JTextField valueTextField;
    private JLabel label4;
    private JSpinner sortSpinner;
    private JLabel label5;
    private JComboBox statusComboBox;
    private JLabel label6;
    private JComboBox colorTypeComboBox;
    private JLabel label7;
    private JTextField cssClassTextField;
    private JLabel label8;
    private JScrollPane scrollPane1;
    private JTextArea remarkTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

