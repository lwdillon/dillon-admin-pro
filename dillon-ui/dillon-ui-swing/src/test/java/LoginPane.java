import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.lw.ui.api.system.AuthApi;
import http.PayLoad;
import http.RetrofitServiceManager;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
/*
 * Created by JFormDesigner on Thu Jan 23 17:29:01 CST 2025
 */


/**
 * @author wenli
 */
public class LoginPane extends JPanel {
    public LoginPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        logoLabel = new JLabel();
        label1 = new JLabel();
        label2 = new JLabel();
        progressBar1 = new JProgressBar();
        userNameField = new JTextField();
        passwordField = new JPasswordField();
        msgLabel = new JLabel();
        loginBut = new JButton();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[fill]",
            // rows
            "[]" +
            "[]" +
            "[::30,fill]" +
            "[10!]" +
            "[fill]" +
            "[fill]" +
            "[25!]0" +
            "[fill]"));
        add(logoLabel, "cell 0 0,alignx center,grow 0 100");

        //---- label1 ----
        label1.setText("Dillon-Admin-Pro");
        label1.setFont(new Font(".AppleSystemUIFont", Font.BOLD, 28));
        add(label1, "cell 0 1,alignx center,growx 0");

        //---- label2 ----
        label2.setText("\u6b22\u8fce\u767b\u5f55");
        add(label2, "cell 0 2,alignx center,growx 0");

        //---- progressBar1 ----
        progressBar1.setVisible(false);
        add(progressBar1, "cell 0 1,grow");
        add(userNameField, "cell 0 4,growy");
        add(passwordField, "cell 0 5,growy");

        //---- msgLabel ----
        msgLabel.setText("text");
        msgLabel.setVisible(false);
        add(msgLabel, "cell 0 4,alignx center,growx 0");

        //---- loginBut ----
        loginBut.setText("\u767b\u5f55");
        add(loginBut, "cell 0 7,growy");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
        initListeners();
    }

    private void initListeners() {
        loginBut.addActionListener(e -> login());
    }

    private void login() {

        AuthLoginReqVO authLoginReqVO = new AuthLoginReqVO();
        authLoginReqVO.setUsername(userNameField.getText());
        authLoginReqVO.setPassword(passwordField.getText());
        RetrofitServiceManager.getInstance()
                .create(AuthApi.class).login(authLoginReqVO)

                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .flatMap(authLoginRespVOCommonResult -> {
                    System.err.println(" -------------------------> flatmap当前线程：" + Thread.currentThread().getName());
                    RetrofitServiceManager.updateToken(authLoginRespVOCommonResult.getAccessToken());
                    return RetrofitServiceManager.getInstance().create(AuthApi.class).getPermissionInfo();
                })
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .doOnSubscribe(disposable -> {
                    msgLabel.setVisible(false);
                    loginBut.setEnabled(false);
                    loginBut.setText("正在请求登录...");
                })
                .doFinally(() -> {
                    System.err.println(" -------------------------> 请求完成：" + Thread.currentThread().getName());
                    loginBut.setEnabled(true);
                    loginBut.setText("登录");
                })
                .subscribe(authPermissionInfoRespVOCommonResult -> {
                    System.err.println(" -------------------------> 结果线程：" + Thread.currentThread().getName());
                    msgLabel.setVisible(false);
                    msgLabel.setText("");
                    System.out.println(authPermissionInfoRespVOCommonResult.getPermissions());
                }, throwable -> {

                    msgLabel.setVisible(true);
                    msgLabel.setText(throwable.getMessage());
                    throwable.printStackTrace();
                    loginBut.setEnabled(false);
                    loginBut.setText("登录");
                });

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel logoLabel;
    private JLabel label1;
    private JLabel label2;
    private JProgressBar progressBar1;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JLabel msgLabel;
    private JButton loginBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
