/*
 * Created by JFormDesigner on Sun Jun 16 15:15:27 CST 2024
 */

package com.lw.swing.view.system.post;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.lw.swing.http.PayLoad;
import com.lw.swing.http.RetrofitServiceManager;
import com.lw.ui.api.system.PostApi;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * @author wenli
 */
public class PostFormPane extends JPanel {
    private Long id = null;

    public PostFormPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        nameTextField = new JTextField();
        label2 = new JLabel();
        codeTextField = new JTextField();
        label3 = new JLabel();
        sortSpinner = new JSpinner();
        label4 = new JLabel();
        statusComboBox = new JComboBox();
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
            "[280:n,grow]"));

        //---- label1 ----
        label1.setText("*\u5c97\u4f4d\u6807\u9898");
        add(label1, "cell 0 0");
        add(nameTextField, "cell 1 0");

        //---- label2 ----
        label2.setText("*\u5c97\u4f4d\u7f16\u7801");
        add(label2, "cell 0 1");
        add(codeTextField, "cell 1 1");

        //---- label3 ----
        label3.setText("\u5c97\u4f4d\u987a\u5e8f");
        add(label3, "cell 0 2");
        add(sortSpinner, "cell 1 2");

        //---- label4 ----
        label4.setText("*\u72b6\u6001");
        add(label4, "cell 0 3");
        add(statusComboBox, "cell 1 3");

        //---- label5 ----
        label5.setText("\u5907\u6ce8");
        add(label5, "cell 0 4,aligny top,growy 0");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(remarkTextArea);
        }
        add(scrollPane1, "cell 1 4,growy");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        statusComboBox.addItem("开启");
        statusComboBox.addItem("关闭");
        SpinnerModel model = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        sortSpinner.setModel(model);
    }

    private void setValue(PostRespVO postRespVO) {
        nameTextField.setText(postRespVO.getName());
        codeTextField.setText(postRespVO.getCode());
        sortSpinner.setValue(postRespVO.getSort() == null ? 0 : postRespVO.getSort());
        statusComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(postRespVO.getStatus(), 0));
        remarkTextArea.setText(postRespVO.getRemark());

    }

    public PostSaveReqVO getValue() {
        PostSaveReqVO reqVO = new PostSaveReqVO();
        reqVO.setId(id);
        reqVO.setName(nameTextField.getText());
        reqVO.setCode(codeTextField.getText());
        reqVO.setRemark(remarkTextArea.getText());
        reqVO.setSort(Convert.toInt(sortSpinner.getValue()));
        reqVO.setStatus(statusComboBox.getSelectedIndex());
        return reqVO;
    }


    public void updateData(Long id) {

        this.id = id;

        RetrofitServiceManager.getInstance().create(PostApi.class).getPost(id).map(new PayLoad<>())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(result -> {
                    setValue(result);
                }, throwable -> {
                    throwable.printStackTrace();
                });


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField nameTextField;
    private JLabel label2;
    private JTextField codeTextField;
    private JLabel label3;
    private JSpinner sortSpinner;
    private JLabel label4;
    private JComboBox statusComboBox;
    private JLabel label5;
    private JScrollPane scrollPane1;
    private JTextArea remarkTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
