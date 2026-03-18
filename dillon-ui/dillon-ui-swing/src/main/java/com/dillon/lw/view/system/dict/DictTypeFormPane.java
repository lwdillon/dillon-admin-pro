/*
 * Created by JFormDesigner on Sun Jun 16 15:39:26 CST 2024
 */

package com.dillon.lw.view.system.dict;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.system.DictTypeApi;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.dtflys.forest.Forest;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.swing.rx.SwingRx;

/**
 * @author wenli
 */
public class DictTypeFormPane extends com.dillon.lw.components.AbstractDisposablePanel {
    private Long id = null;

    public DictTypeFormPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        nameTextField = new JTextField();
        label2 = new JLabel();
        typeTextField = new JTextField();
        label3 = new JLabel();
        statusComboBox = new JComboBox();
        label4 = new JLabel();
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
            "[280:n,grow]"));

        //---- label1 ----
        label1.setText("*\u5b57\u5178\u540d\u79f0");
        add(label1, "cell 0 0");
        add(nameTextField, "cell 1 0");

        //---- label2 ----
        label2.setText("*\u5b57\u5178\u7c7b\u578b");
        add(label2, "cell 0 1");
        add(typeTextField, "cell 1 1");

        //---- label3 ----
        label3.setText("*\u72b6\u6001");
        add(label3, "cell 0 2");
        add(statusComboBox, "cell 1 2");

        //---- label4 ----
        label4.setText("\u5907\u6ce8");
        add(label4, "cell 0 3,aligny top,growy 0");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(remarkTextArea);
        }
        add(scrollPane1, "cell 1 3,grow");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
        statusComboBox.addItem("开启");
        statusComboBox.addItem("关闭");
    }

    private void setValue(DictTypeRespVO postRespVO) {
        nameTextField.setText(postRespVO.getName());
        typeTextField.setText(postRespVO.getType());
        statusComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(postRespVO.getStatus(), 0));
        remarkTextArea.setText(postRespVO.getRemark());

    }

    public DictTypeSaveReqVO getValue() {
        DictTypeSaveReqVO reqVO = new DictTypeSaveReqVO();
        reqVO.setId(id);
        reqVO.setName(nameTextField.getText());
        reqVO.setType(typeTextField.getText());
        reqVO.setRemark(remarkTextArea.getText());
        reqVO.setStatus(statusComboBox.getSelectedIndex());
        return reqVO;
    }

    /**
     * 验证表单
     *
     * @return 验证失败的错误消息，null表示验证通过
     */
    public String validates() {
        if (StrUtil.isBlank(nameTextField.getText())) {
            return "请输入字典名称";
        }
        if (StrUtil.isBlank(typeTextField.getText())) {
            return "请输入字典类型";
        }
        return null;
    }


    public void updateData(Long id) {

        typeTextField.setEditable(id == null);
        this.id = id;
        if (id == null) {
            return;
        }

        Single
                /*
                 * 详情接口是同步调用，包装成 Single 后交给 IO 线程，
                 * 这样不会阻塞打开弹窗时的 Swing 主线程。
                 */
                .fromCallable(() -> Forest.client(DictTypeApi.class).getDictType(id).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(this::setValue, SwingExceptionHandler::handle);


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField nameTextField;
    private JLabel label2;
    private JTextField typeTextField;
    private JLabel label3;
    private JComboBox statusComboBox;
    private JLabel label4;
    private JScrollPane scrollPane1;
    private JTextArea remarkTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
