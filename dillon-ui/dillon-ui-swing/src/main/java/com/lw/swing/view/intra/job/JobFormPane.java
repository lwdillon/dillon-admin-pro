/*
 * Created by JFormDesigner on Sun Jun 16 17:23:39 CST 2024
 */

package com.lw.swing.view.intra.job;

import cn.hutool.core.convert.Convert;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import com.lw.swing.http.PayLoad;
import com.lw.swing.http.RetrofitServiceManager;
import com.lw.ui.api.job.JobApi;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * @author wenli
 */
public class JobFormPane extends JPanel {
    private Long id;
    public JobFormPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        nameField = new JTextField();
        label2 = new JLabel();
        handlerNameField = new JTextField();
        label4 = new JLabel();
        handlerParamField = new JTextField();
        label5 = new JLabel();
        cronExpressionField = new JTextField();
        label6 = new JLabel();
        retryCountField = new JTextField();
        label7 = new JLabel();
        retryIntervalFiled = new JTextField();
        label3 = new JLabel();
        monitorTimeoutField = new JTextField();

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
            "[]" +
            "[]" +
            "[]" +
            "[]"));

        //---- label1 ----
        label1.setText("*\u4efb\u52a1\u540d\u79f0");
        add(label1, "cell 0 0");
        add(nameField, "cell 1 0");

        //---- label2 ----
        label2.setText("*\u5904\u7406\u5668\u7684\u540d\u5b57 ");
        add(label2, "cell 0 1");
        add(handlerNameField, "cell 1 1");

        //---- label4 ----
        label4.setText("\u5904\u7406\u5668\u7684\u53c2\u6570");
        add(label4, "cell 0 2");
        add(handlerParamField, "cell 1 2");

        //---- label5 ----
        label5.setText("*CRON \u8868\u8fbe\u5f0f");
        add(label5, "cell 0 3");
        add(cronExpressionField, "cell 1 3");

        //---- label6 ----
        label6.setText("*\u91cd\u8bd5\u6b21\u6570");
        add(label6, "cell 0 4");
        add(retryCountField, "cell 1 4");

        //---- label7 ----
        label7.setText("*\u91cd\u8bd5\u95f4\u9694");
        add(label7, "cell 0 5");
        add(retryIntervalFiled, "cell 1 5");

        //---- label3 ----
        label3.setText("\u76d1\u63a7\u8d85\u65f6\u65f6\u95f4");
        add(label3, "cell 0 6");
        add(monitorTimeoutField, "cell 1 6");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

       
    }
    private void setValue(JobRespVO respVO) {
        nameField.setText(respVO.getName());
        handlerNameField.setText(respVO.getHandlerName());
        handlerParamField.setText(respVO.getHandlerParam());
        cronExpressionField.setText(respVO.getCronExpression());
        retryCountField.setText(respVO.getRetryCount()+"");
        retryIntervalFiled.setText(respVO.getRetryInterval()+"");
        monitorTimeoutField.setText(respVO.getMonitorTimeout()+"");

    }

    public JobSaveReqVO getValue() {
        JobSaveReqVO reqVO = new JobSaveReqVO();
        reqVO.setId(id);
        reqVO.setName(nameField.getText());
        reqVO.setHandlerName(handlerNameField.getText());
        reqVO.setHandlerParam(handlerParamField.getText());
        reqVO.setCronExpression(cronExpressionField.getText());
        reqVO.setRetryCount(Convert.toInt(retryCountField.getText(),0));
        reqVO.setRetryInterval(Convert.toInt(retryIntervalFiled.getText(),0));
        reqVO.setMonitorTimeout(Convert.toInt(monitorTimeoutField.getText(),0));
        return reqVO;
    }


    public void updateData(Long id) {
        this.id = id;

        RetrofitServiceManager.getInstance().create(JobApi.class).getJob(id).map(new PayLoad<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(jobRespVO -> setValue(jobRespVO), throwable -> throwable.printStackTrace());


    }
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField nameField;
    private JLabel label2;
    private JTextField handlerNameField;
    private JLabel label4;
    private JTextField handlerParamField;
    private JLabel label5;
    private JTextField cronExpressionField;
    private JLabel label6;
    private JTextField retryCountField;
    private JLabel label7;
    private JTextField retryIntervalFiled;
    private JLabel label3;
    private JTextField monitorTimeoutField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
