package com.dillon;

import com.dillon.lw.components.notice.InfoCenterPane;
import com.dillon.lw.components.notice.WMessage;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InfoCenterPaneTest {

    private static int sequence = 1;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            initDemoThemeDefaults();

            JFrame frame = new JFrame("InfoCenterPane Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(createContent(frame), BorderLayout.CENTER);
            frame.setSize(980, 640);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JComponent createContent(JFrame frame) {
        InfoCenterPane infoCenterPane = new InfoCenterPane();
        infoCenterPane.setPreferredSize(new Dimension(380, 0));
        infoCenterPane.setDisplayThreshold(3);
        infoCenterPane.configureGroup("system", "系统通知", group -> group.setPinned(true));
        infoCenterPane.configureGroup("approval", "审批消息", group -> group.setDisplayThreshold(2));
        infoCenterPane.configureGroup("custom", "自定义视图", group -> {
            group.setDisplayThreshold(2);
            group.setViewFactory((item, onClose, onChanged) -> createCustomNotificationView(item.getTitle(), item.getMessage(), onClose));
        });
        seedNotifications(infoCenterPane);

        JTextArea eventLog = new JTextArea();
        eventLog.setEditable(false);
        eventLog.setLineWrap(true);
        eventLog.setWrapStyleWord(true);

        infoCenterPane.addPropertyChangeListener(InfoCenterPane.UNREAD_COUNT_PROPERTY, event -> {
            eventLog.append("未读数变化：" + event.getOldValue() + " -> " + event.getNewValue() + "\n");
        });

        JPanel root = new JPanel(new BorderLayout(16, 0));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel playground = new JPanel(new BorderLayout(0, 12));
        playground.add(createToolbar(frame, infoCenterPane, eventLog), BorderLayout.NORTH);
        playground.add(new JScrollPane(eventLog), BorderLayout.CENTER);

        root.add(playground, BorderLayout.CENTER);
        root.add(infoCenterPane, BorderLayout.EAST);
        return root;
    }

    private static JComponent createToolbar(JFrame frame, InfoCenterPane infoCenterPane, JTextArea eventLog) {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.add(createButton("系统", () -> infoCenterPane.addNotification("system", "系统通知", nextTitle("System"), nowMessage(), WMessage.INFO)));
        toolbar.add(createButton("审批", () -> infoCenterPane.addNotification("approval", "审批消息", nextTitle("Approval"), nowMessage(), WMessage.SUCCESS)));
        toolbar.add(createButton("预警", () -> infoCenterPane.addNotification("warning", "风险预警", nextTitle("Warning"), nowMessage(), WMessage.WARNING)));
        toolbar.add(createButton("自定义", () -> infoCenterPane.addNotification("custom", "自定义视图", nextTitle("Custom"), nowMessage(), WMessage.INFO)));
        toolbar.add(createButton("Error", () -> infoCenterPane.addError(nextTitle("Error"), nowMessage())));
        toolbar.add(createButton("带回调", () -> infoCenterPane.addNotification(
                "approval",
                "审批消息",
                nextTitle("Action"),
                "点击这条通知会写入左侧日志，并弹出一个 WMessage。",
                WMessage.INFO,
                () -> {
                    String message = "通知回调执行：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    eventLog.append(message + "\n");
                    WMessage.showMessageSuccess(frame, message);
                }
        )));
        toolbar.add(createButton("全部已读", infoCenterPane::markAllRead));
        toolbar.add(createButton("清空", infoCenterPane::clearNotifications));
        return toolbar;
    }

    private static JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(event -> action.run());
        return button;
    }

    private static void seedNotifications(InfoCenterPane infoCenterPane) {
        infoCenterPane.addNotification("system", "系统通知", "欢迎使用 InfoCenterPane", "置顶组会固定在顶部，不跟随普通列表滚动。", WMessage.INFO);
        infoCenterPane.addNotification("system", "系统通知", "构建完成", "新收到的通知会以滑入动画出现。", WMessage.SUCCESS);
        infoCenterPane.addNotification("approval", "审批消息", "待处理提醒", "审批组阈值为 2，超过阈值后可以切换到列表视图。", WMessage.WARNING);
        infoCenterPane.addNotification("approval", "审批消息", "付款审批", "点击组标题右侧按钮可以在堆叠和展开之间切换。", WMessage.INFO);
        infoCenterPane.addNotification("approval", "审批消息", "合同审批", "这是超过阈值的第三条审批消息。", WMessage.INFO);
        infoCenterPane.addNotification("custom", "自定义视图", "自定义工厂", "这个组使用自己的通知视图创建工厂。", WMessage.INFO);
        infoCenterPane.addError("异常示例", "默认分组仍然保持兼容，用于检查原有 API。");
    }

    private static JComponent createCustomNotificationView(String title, String message, Runnable onClose) {
        JPanel panel = new JPanel(new BorderLayout(8, 4));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x7c3aed)),
                new EmptyBorder(10, 12, 10, 10)
        ));
        panel.setBackground(new Color(0xf5f3ff));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setForeground(new Color(0x5b21b6));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel("<html><body style='width:250px'>" + message + "</body></html>");
        messageLabel.setForeground(new Color(0x4c1d95));
        panel.add(messageLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("关闭");
        closeButton.setFocusable(false);
        closeButton.addActionListener(event -> onClose.run());
        panel.add(closeButton, BorderLayout.EAST);
        return panel;
    }

    private static String nextTitle(String type) {
        return type + " #" + sequence++;
    }

    private static String nowMessage() {
        return "创建时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static void initDemoThemeDefaults() {
        UIManager.put("App.arc", 16);
        UIManager.put("App.baseBackground", Color.WHITE);
        UIManager.put("App.borderColor", new Color(0xd9dee8));
        UIManager.put("App.secondaryTextColor", new Color(0x6b7280));
        UIManager.put("ColorPalette.notice.info.fg", new Color(0x409EFF));
        UIManager.put("ColorPalette.notice.info.bg", new Color(0xecf5ff));
        UIManager.put("ColorPalette.notice.info.bd", new Color(0xb3d8ff));
        UIManager.put("ColorPalette.notice.success.fg", new Color(0x67C23A));
        UIManager.put("ColorPalette.notice.success.bg", new Color(0xf0f9eb));
        UIManager.put("ColorPalette.notice.success.bd", new Color(0xc2e7b0));
        UIManager.put("ColorPalette.notice.warn.fg", new Color(0xE6A23C));
        UIManager.put("ColorPalette.notice.warn.bg", new Color(0xfdf6ec));
        UIManager.put("ColorPalette.notice.warn.bd", new Color(0xf5dab1));
        UIManager.put("ColorPalette.notice.error.fg", new Color(0xF56C6C));
        UIManager.put("ColorPalette.notice.error.bg", new Color(0xfef0f0));
        UIManager.put("ColorPalette.notice.error.bd", new Color(0xfbc4c4));
    }
}
