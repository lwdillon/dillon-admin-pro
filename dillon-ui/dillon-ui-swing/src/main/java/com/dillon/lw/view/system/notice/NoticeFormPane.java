/*
 * Created by JFormDesigner on Sun Jun 16 17:23:39 CST 2024
 */

package com.dillon.lw.view.system.notice;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.system.NoticeApi;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.notice.vo.NoticeRespVO;
import com.dillon.lw.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.DictTypeEnum;
import com.dtflys.forest.Forest;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.swing.rx.SwingRx;

/**
 * @author wenli
 */
public class NoticeFormPane extends com.dillon.lw.components.AbstractDisposablePanel {
    private Long id;

    public NoticeFormPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        titleTextFiled = new JTextField();
        label2 = new JLabel();
        typeComboBox = new JComboBox();
        label3 = new JLabel();
        statusComboBox = new JComboBox();
        label4 = new JLabel();
        scrollPane2 = new JScrollPane();
        contentTextPane = new JTextPane();
        label5 = new JLabel();
        scrollPane1 = new JScrollPane();
        remarkTextArea = new JTextArea();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[520:n,grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[480:n,grow]" +
            "[120:n]"));

        //---- label1 ----
        label1.setText("*\u516c\u544a\u6807\u9898");
        add(label1, "cell 0 0");
        add(titleTextFiled, "cell 1 0");

        //---- label2 ----
        label2.setText("*\u516c\u544a\u7c7b\u578b ");
        add(label2, "cell 0 1");
        add(typeComboBox, "cell 1 1,alignx left,growx 0");

        //---- label3 ----
        label3.setText("*\u72b6\u6001");
        add(label3, "cell 0 2");
        add(statusComboBox, "cell 1 2,alignx left,growx 0");

        //---- label4 ----
        label4.setText("*\u516c\u544a\u5185\u5bb9");
        add(label4, "cell 0 3,aligny top,growy 0");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(contentTextPane);
        }
        add(scrollPane2, "cell 1 3,grow");

        //---- label5 ----
        label5.setText("\u5907\u6ce8");
        add(label5, "cell 0 4,aligny top,growy 0");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(remarkTextArea);
        }
        add(scrollPane1, "cell 1 4,grow");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
        statusComboBox.addItem("开启");
        statusComboBox.addItem("关闭");

        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.SYSTEM_NOTICE_TYPE);
        typeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof DictDataSimpleRespVO) {
                    value = ((DictDataSimpleRespVO) value).getLabel();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        for (DictDataSimpleRespVO dictDataSimpleRespVO : dictDataSimpleRespVOList) {
            typeComboBox.addItem(dictDataSimpleRespVO);
        }
    }

    private void setValue(NoticeRespVO respVO) {
        titleTextFiled.setText(respVO.getTitle());
        contentTextPane.setText(respVO.getContent());
        statusComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(respVO.getStatus(), 0));
        Integer type = respVO.getType();
        if (type != null) {
            ComboBoxModel<DictDataSimpleRespVO> model = typeComboBox.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                DictDataSimpleRespVO item = model.getElementAt(i);
                if (ObjectUtil.equals(item.getValue(), type)) {
                    typeComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        remarkTextArea.setText(respVO.getRemark());

    }

    public NoticeSaveReqVO getValue() {
        NoticeSaveReqVO reqVO = new NoticeSaveReqVO();
        reqVO.setId(id);
        reqVO.setTitle(titleTextFiled.getText());
        DictDataSimpleRespVO selected = (DictDataSimpleRespVO) typeComboBox.getSelectedItem();
        if (selected != null) {
            reqVO.setType(Convert.toInt(selected.getValue(), 1));
        }
        reqVO.setContent(contentTextPane.getText());
        reqVO.setStatus(statusComboBox.getSelectedIndex());
        return reqVO;
    }

    /**
     * 验证表单
     *
     * @return 验证失败的错误消息，null表示验证通过
     */
    public String validates() {
        if (StrUtil.isBlank(titleTextFiled.getText())) {
            return "请输入公告标题";
        }
        if (StrUtil.isBlank(contentTextPane.getText())) {
            return "请输入公告内容";
        }
        return null;
    }


    public void updateData(NoticeRespVO respVO) {
        this.id = respVO.getId();

        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.SYSTEM_NOTICE_TYPE);

        for (DictDataSimpleRespVO dictDataSimpleRespVO : dictDataSimpleRespVOList) {
            if (StrUtil.equals(dictDataSimpleRespVO.getValue(), respVO.getType() + "")) {
                typeComboBox.setSelectedItem(dictDataSimpleRespVO);
                break;
            }

        }

        Single
                /*
                 * 公告详情回填与其他表单保持一致：请求放到 IO，表单赋值放到 EDT。
                 */
                .fromCallable(() -> Forest.client(NoticeApi.class).getNotice(id).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(this::setValue, SwingExceptionHandler::handle);

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField titleTextFiled;
    private JLabel label2;
    private JComboBox typeComboBox;
    private JLabel label3;
    private JComboBox statusComboBox;
    private JLabel label4;
    private JScrollPane scrollPane2;
    private JTextPane contentTextPane;
    private JLabel label5;
    private JScrollPane scrollPane1;
    private JTextArea remarkTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
