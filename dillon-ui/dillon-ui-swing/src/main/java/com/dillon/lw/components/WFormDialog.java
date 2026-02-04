package com.dillon.lw.components;

import cn.hutool.core.util.StrUtil;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.view.frame.MainFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 通用表单对话框，支持客户端验证和异步提交
 * 验证失败或提交异常时对话框不关闭，用户可继续修改
 *
 * @author wenli
 */
public class WFormDialog<T> extends JDialog {

    private final JPanel formPane;
    private final JButton confirmButton;
    private final JButton cancelButton;
    private boolean confirmed = false;

    /**
     * 表单验证接口
     */
    public interface FormValidator {
        /**
         * 验证表单
         * @return 验证失败的错误消息，返回null或空字符串表示验证通过
         */
        String validates();
    }

    /**
     * 创建表单对话框
     *
     * @param owner    父窗口
     * @param title    对话框标题
     * @param formPane 表单面板
     */
    public WFormDialog(Window owner, String title, JPanel formPane) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.formPane = formPane;

        // 初始化按钮
        confirmButton = new JButton("确定");
        cancelButton = new JButton("取消");

        initComponents();
        initListeners();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 表单区域
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        contentPane.add(formPane, BorderLayout.CENTER);
        add(contentPane, BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPane = new JPanel(new MigLayout("insets 10, fillx", "[grow][80][80]", "[]"));
        buttonPane.add(new JLabel(), "grow");
        buttonPane.add(confirmButton, "width 80!");
        buttonPane.add(cancelButton, "width 80!");
        add(buttonPane, BorderLayout.SOUTH);
    }

    private void initListeners() {
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmed = false;
            }
        });
    }

    /**
     * 显示对话框，带客户端验证
     *
     * @param validator 验证器，返回错误消息，null表示验证通过
     * @param onConfirm 验证通过后的回调，返回表单数据
     * @return 是否确认提交
     */
    public boolean showDialog(FormValidator validator, Supplier<T> onConfirm) {
        confirmButton.addActionListener(e -> {
            // 执行客户端验证
            if (validator != null) {
                String errorMsg = validator.validates();
                if (StrUtil.isNotBlank(errorMsg)) {
                    WMessage.showMessageWarning(MainFrame.getInstance(), errorMsg);
                    return;
                }
            }
            confirmed = true;
            dispose();
        });

        setVisible(true);
        return confirmed;
    }

    /**
     * 显示对话框，带客户端验证和异步提交
     * 提交成功后才关闭对话框，失败时保持打开状态
     *
     * @param validator    验证器
     * @param dataSupplier 获取表单数据
     * @param asyncTask    异步提交任务
     * @param onSuccess    成功回调
     * @param successMsg   成功消息
     */
    public void showDialogWithAsyncSubmit(
            FormValidator validator,
            Supplier<T> dataSupplier,
            Function<T, ?> asyncTask,
            Runnable onSuccess,
            String successMsg) {

        confirmButton.addActionListener(e -> {
            // 执行客户端验证
            if (validator != null) {
                String errorMsg = validator.validates();
                if (StrUtil.isNotBlank(errorMsg)) {
                    WMessage.showMessageWarning(MainFrame.getInstance(), errorMsg);
                    return;
                }
            }

            // 禁用按钮，防止重复提交
            confirmButton.setEnabled(false);
            confirmButton.setText("提交中...");

            T data = dataSupplier.get();

            CompletableFuture.supplyAsync(() -> asyncTask.apply(data))
                    .thenAcceptAsync(result -> {
                        confirmed = true;
                        if (StrUtil.isNotBlank(successMsg)) {
                            WMessage.showMessageSuccess(MainFrame.getInstance(), successMsg);
                        }
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                        dispose();
                    }, SwingUtilities::invokeLater)
                    .exceptionally(throwable -> {
                        SwingUtilities.invokeLater(() -> {
                            // 恢复按钮状态
                            confirmButton.setEnabled(true);
                            confirmButton.setText("确定");
                            // 显示错误，但不关闭对话框
                            SwingExceptionHandler.handle(throwable);
                        });
                        return null;
                    });
        });

        setVisible(true);
    }

    /**
     * 是否已确认
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * 获取确认按钮（可用于自定义）
     */
    public JButton getConfirmButton() {
        return confirmButton;
    }

    /**
     * 获取取消按钮（可用于自定义）
     */
    public JButton getCancelButton() {
        return cancelButton;
    }
}
