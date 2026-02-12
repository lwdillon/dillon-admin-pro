/*
 * Created by JFormDesigner on Thu Jan 01 16:38:11 CST 2026
 */

package com.dillon.lw.view.frame;

import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.config.AppPrefs;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.AddMainTabEvent;
import com.dillon.lw.eventbus.event.LoginEvent;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.theme.ThemeType;
import com.dillon.lw.utils.ExecuteUtils;
import com.dillon.lw.utils.IconLoader;
import com.dillon.lw.view.system.notice.MyNotifyMessagePane;
import com.dillon.lw.view.system.user.PersonalCenterPanel;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.util.LoggingFacade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 主窗口顶部工具栏。
 * <p>
 * 包含主题切换、消息入口、用户菜单与退出登录逻辑。
 * JFormDesigner 生成区仅负责基础布局，工具栏行为在生成区外维护。
 * </p>
 */
public class TitlePanel extends JPanel {
    private static final int TOOL_ICON_SIZE = 24;
    private static final int TOOL_BUTTON_WIDTH = 36;
    private static final int TOOL_BUTTON_HEIGHT = 30;
    private static final int PROFILE_ICON_SIZE = 80;
    private static final int MENU_ICON_SIZE = 25;

    // 自定义组件和状态变量
    private FlatButton themeButton;
    private FlatButton noticeButton;
    private FlatButton userButton;
    private NotificationBadgeIcon noticeBadgeIcon;
    private int unreadNoticeCount;


    public TitlePanel() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        macFullWindowContentButtonsPlaceholder = new JPanel();
        toolPanel = new JPanel();
        titleLabel = new JLabel();
        winFullWindowContentButtonsPlaceholder = new JPanel();

        //======== this ========
        setOpaque(false);
        setLayout(new BorderLayout());

        //======== macFullWindowContentButtonsPlaceholder ========
        {
            macFullWindowContentButtonsPlaceholder.setOpaque(false);
            macFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
        }
        add(macFullWindowContentButtonsPlaceholder, BorderLayout.LINE_START);

        //======== toolPanel ========
        {
            toolPanel.setBorder(new EmptyBorder(1, 10, 1, 1));
            toolPanel.setLayout(new BorderLayout(5, 5));

            //---- titleLabel ----
            titleLabel.setText("Dillon-Swing");
            toolPanel.add(titleLabel, BorderLayout.WEST);
        }
        add(toolPanel, BorderLayout.CENTER);

        //======== winFullWindowContentButtonsPlaceholder ========
        {
            winFullWindowContentButtonsPlaceholder.setOpaque(false);
            winFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
        }
        add(winFullWindowContentButtonsPlaceholder, BorderLayout.LINE_END);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        // 下方为手写逻辑区域，不修改 JFormDesigner 生成块，保持 .jfd 可回写。
        themeButton = createToolBarButton("icons/skin.svg");
        themeButton.addActionListener(e -> showThemePopupMenu(e));

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);
        toolBar.add(Box.createGlue());
        toolBar.add(themeButton);
        toolBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        titleLabel.setFont(titleLabel.getFont().deriveFont(18f).deriveFont(Font.BOLD));
        noticeButton = createToolBarButton("icons/bell.svg");
        noticeBadgeIcon = new NotificationBadgeIcon(IconLoader.getSvgIcon("icons/bell.svg", TOOL_ICON_SIZE, TOOL_ICON_SIZE));
        noticeButton.setIcon(noticeBadgeIcon);
        noticeButton.addActionListener(e1 -> {
            // 用户主动查看消息时，视为已读并清空角标。
            resetUnreadNoticeCount();
            EventBusCenter.get().post(new AddMainTabEvent("icons/bell.svg", "我的消息", new MyNotifyMessagePane()));
        });
        toolBar.add(noticeButton);

        userButton = createToolBarButton("icons/user.svg");
        userButton.addActionListener(e -> showUserInfoPopupMenu(e));
        toolBar.add(userButton);

        toolPanel.add(toolBar, BorderLayout.EAST);
        toolPanel.setOpaque(false);
        titleLabel.setIcon(new FlatSVGIcon("icons/guanli.svg", 25, 25));

        titleLabel.setText(System.getProperty("app.name"));
        // on macOS, panel left to toolBar is a placeholder for title bar buttons in fullWindowContent mode
        macFullWindowContentButtonsPlaceholder.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "mac zeroInFullScreen");

        // on Windows/Linux, panel above themesPanel is a placeholder for title bar buttons in fullWindowContent mode
        winFullWindowContentButtonsPlaceholder.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "win");


    }

    private FlatButton createToolBarButton(String iconPath) {
        FlatButton button = new FlatButton();
        button.setIcon(IconLoader.getSvgIcon(iconPath, TOOL_ICON_SIZE, TOOL_ICON_SIZE));
        button.setButtonType(FlatButton.ButtonType.toolBarButton);
        button.setFocusable(false);
        // 统一顶部工具按钮尺寸，避免消息角标引入后与其它按钮视觉不协调。
        Dimension buttonSize = new Dimension(TOOL_BUTTON_WIDTH, TOOL_BUTTON_HEIGHT);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.putClientProperty("FlatLaf.internal.testing.ignore", true);
        return button;
    }

    private void showThemePopupMenu(ActionEvent e) {
        Component invoker = (Component) e.getSource();

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.applyComponentOrientation(getComponentOrientation());

        ButtonGroup group = new ButtonGroup();

        for (ThemeType theme : ThemeType.values()) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(theme.getText());

            item.addActionListener(ev -> theme(theme));

            group.add(item);
            popupMenu.add(item);

            // 默认选中（按当前 LAF）
            if (UIManager.getLookAndFeel().getClass().equals(theme.createLaf().getClass())) {
                item.setSelected(true);
            }
        }

        popupMenu.show(invoker, 0, invoker.getHeight());
    }

    /**
     * 切换应用程序的主题外观。
     *
     * @param theme 主题名称 ("白色", "深色", "玻璃")。
     */
    public void theme(ThemeType theme) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();

            try {
                // 1. 切换 LookAndFeel
                UIManager.setLookAndFeel(theme.createLaf());
                AppPrefs.prefs().put(AppPrefs.KEY_UI_THEME + "_" + AppStore.getUserId(), theme.createLaf().getClass().getName());

            } catch (Exception ex) {
                LoggingFacade.INSTANCE.logSevere(null, ex);
            }

            // 4. 刷新所有窗口（关键）
            FlatLaf.updateUI();

            // 6. 动画结束
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    /**
     * 创建个人信息弹出菜单的内容。
     */
    private void showUserInfoPopupMenu(ActionEvent e) {
        Component invoker = (Component) e.getSource();
        JPopupMenu popupMenu = new JPopupMenu();

        // 顶部用户信息面板
        JPanel infoPanel = new JPanel(new BorderLayout());
        String nickName = AppStore.getUserVO() != null ? AppStore.getUserVO().getNickname() : "未登录";
        JLabel label = new JLabel(nickName, JLabel.CENTER);
        label.setIcon(new FlatSVGIcon("icons/user.svg", PROFILE_ICON_SIZE, PROFILE_ICON_SIZE));
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        infoPanel.add(label, BorderLayout.CENTER);
        infoPanel.add(new JLabel("系统管理员", JLabel.CENTER), BorderLayout.SOUTH);
        label.setPreferredSize(new Dimension(240, 100));
        popupMenu.add(infoPanel);
        popupMenu.addSeparator();

        // 个人信息项
        JMenuItem personalInfoItem = new JMenuItem("个人信息");
        personalInfoItem.setIcon(new FlatSVGIcon("icons/gerenxinxi.svg", MENU_ICON_SIZE, MENU_ICON_SIZE));
        personalInfoItem.addActionListener(e1 -> EventBusCenter.get().post(new AddMainTabEvent("icons/gerenxinxi.svg", "个人信息", new PersonalCenterPanel())));
        popupMenu.add(personalInfoItem);
        popupMenu.addSeparator();

        // 退出登录项
        JMenuItem logoutItem = new JMenuItem("退出");
        logoutItem.setIcon(new FlatSVGIcon("icons/logout.svg", MENU_ICON_SIZE, MENU_ICON_SIZE));
        logoutItem.addActionListener(e1 -> {
            int option = JOptionPane.showConfirmDialog(
                    MainFrame.getInstance(),
                    "确认退出当前账号吗？",
                    "退出登录",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            logoutItem.setEnabled(false);
            ExecuteUtils.execute(
                    () -> Forest.client(AuthApi.class).logout(),
                    result -> {
                        if (result == null || !result.isSuccess()) {
                            WMessage.showMessageWarning(MainFrame.getInstance(), "服务端退出失败，已本地退出");
                        }
                        doLocalLogout();
                    },
                    () -> logoutItem.setEnabled(true)
            );
        });
//        logoutItem.addActionListener(e -> MainFrame.getInstance().showLogin());
        popupMenu.add(logoutItem);

        popupMenu.show(invoker, 0, invoker.getHeight());
    }

    private void doLocalLogout() {
        // 本地会话先清理，确保即使服务端退出失败也能回到干净登录态。
        AppStore.clearSession();
        resetUnreadNoticeCount();
        EventBusCenter.get().post(new LoginEvent(LoginEvent.LOGOUT_OR_INVALID));
    }

    /**
     * 收到新消息后递增未读数。
     * 该方法对非 EDT 调用安全，会自动切回 EDT 更新 UI。
     */
    public void increaseUnreadNoticeCount() {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(this::increaseUnreadNoticeCount);
            return;
        }
        unreadNoticeCount++;
        refreshNoticeBadge();
    }

    /**
     * 清空未读数并刷新角标。
     */
    public void resetUnreadNoticeCount() {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(this::resetUnreadNoticeCount);
            return;
        }
        unreadNoticeCount = 0;
        refreshNoticeBadge();
    }

    private void refreshNoticeBadge() {
        if (noticeBadgeIcon == null || noticeButton == null) {
            return;
        }
        noticeBadgeIcon.setCount(unreadNoticeCount);
        noticeButton.repaint();
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel macFullWindowContentButtonsPlaceholder;
    private JPanel toolPanel;
    private JLabel titleLabel;
    private JPanel winFullWindowContentButtonsPlaceholder;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
