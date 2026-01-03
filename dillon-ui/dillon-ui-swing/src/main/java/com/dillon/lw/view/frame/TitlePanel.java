/*
 * Created by JFormDesigner on Thu Jan 01 16:38:11 CST 2026
 */

package com.dillon.lw.view.frame;

import javax.swing.border.*;
import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.config.AppPrefs;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.AddMainTabEvent;
import com.dillon.lw.eventbus.event.LoginEvent;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.theme.DarkTheme;
import com.dillon.lw.theme.GlazzedTheme;
import com.dillon.lw.theme.LightTheme;
import com.dillon.lw.theme.ThemeType;
import com.dillon.lw.utils.ExecuteUtils;
import com.dillon.lw.view.system.notice.MyNotifyMessagePane;
import com.dillon.lw.view.system.user.PersonalCenterPanel;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.util.LoggingFacade;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;

/**
 * @author wenli
 */
public class TitlePanel extends JPanel {
    // 弹出菜单
    private JPopupMenu themePopupMenu;
    private JPopupMenu personalPopupMenu;

    // 自定义组件和状态变量
    private FlatButton themeButton;
    private FlatButton noticeButton;
    private FlatButton userButton;


    public TitlePanel() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        macFullWindowContentButtonsPlaceholder = new JPanel();
        toolBar = new JToolBar();
        titleLabel = new JLabel();
        winFullWindowContentButtonsPlaceholder = new JPanel();

        //======== this ========
        setLayout(new BorderLayout());

        //======== macFullWindowContentButtonsPlaceholder ========
        {
            macFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
        }
        add(macFullWindowContentButtonsPlaceholder, BorderLayout.LINE_START);

        //======== toolBar ========
        {
            toolBar.setFloatable(false);
            toolBar.setBorder(new EmptyBorder(0, 7, 0, 7));

            //---- titleLabel ----
            titleLabel.setText("Dillon-Swing");
            toolBar.add(titleLabel);
        }
        add(toolBar, BorderLayout.CENTER);

        //======== winFullWindowContentButtonsPlaceholder ========
        {
            winFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
        }
        add(winFullWindowContentButtonsPlaceholder, BorderLayout.LINE_END);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        //user
        themeButton = new FlatButton();
        themeButton.setIcon(new FlatSVGIcon("icons/skin.svg", 25, 25));
        themeButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        themeButton.setFocusable(false);
        themeButton.putClientProperty("FlatLaf.internal.testing.ignore", true);
        themeButton.addActionListener(e -> showThemePopupMenu(e));
        toolBar.add(Box.createGlue());
        toolBar.add(themeButton);

        noticeButton = new FlatButton();
        noticeButton.setIcon(new FlatSVGIcon("icons/bell.svg", 25, 25));
        noticeButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        noticeButton.addActionListener(e1 -> EventBusCenter.get().post(new AddMainTabEvent("icons/bell.svg", "我的消息", new MyNotifyMessagePane())));

        noticeButton.setFocusable(false);

        toolBar.add(noticeButton);

        userButton = new FlatButton();
        userButton.setIcon(new FlatSVGIcon("icons/user.svg", 25, 25));
        userButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        userButton.putClientProperty("FlatLaf.internal.testing.ignore", true);
        userButton.addActionListener(e -> showUserInfoPopupMenu(e));
        userButton.setFocusable(false);
        toolBar.add(userButton);

        titleLabel.setIcon(new FlatSVGIcon("icons/guanli.svg", 25, 25));

        titleLabel.setText(System.getProperty("app.name"));
        // on macOS, panel left to toolBar is a placeholder for title bar buttons in fullWindowContent mode
        macFullWindowContentButtonsPlaceholder.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "mac zeroInFullScreen");

        // on Windows/Linux, panel above themesPanel is a placeholder for title bar buttons in fullWindowContent mode
        winFullWindowContentButtonsPlaceholder.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "win");


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
                AppPrefs.prefs().put(AppPrefs.KEY_UI_THEME+"_"+AppStore.getUserId(),theme.createLaf().getClass().getName());

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
        JLabel label = new JLabel(AppStore.getAuthPermissionInfoRespVO().getUser().getNickname(), JLabel.CENTER);
        label.setIcon(new FlatSVGIcon("icons/user.svg", 80, 80));
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        infoPanel.add(label, BorderLayout.CENTER);
        infoPanel.add(new JLabel("系统管理员", JLabel.CENTER), BorderLayout.SOUTH);
        label.setPreferredSize(new Dimension(240, 100));
        popupMenu.add(infoPanel);
        popupMenu.addSeparator();

        // 个人信息项
        JMenuItem personalInfoItem = new JMenuItem("个人信息");
        personalInfoItem.setIcon(new FlatSVGIcon("icons/gerenxinxi.svg", 25, 25));
        personalInfoItem.addActionListener(e1 -> EventBusCenter.get().post(new AddMainTabEvent("icons/gerenxinxi.svg", "个人信息", new PersonalCenterPanel())));
        popupMenu.add(personalInfoItem);
        popupMenu.addSeparator();

        // 退出登录项
        JMenuItem logoutItem = new JMenuItem("退出");
        logoutItem.setIcon(new FlatSVGIcon("icons/logout.svg", 25, 25));
        logoutItem.addActionListener(e1 -> {
            ExecuteUtils.execute(() -> Forest.client(AuthApi.class).logout(), result -> {
                if (result.isSuccess()) {
                    EventBusCenter.get().post(new LoginEvent(1));
                }
            });
        });
//        logoutItem.addActionListener(e -> MainFrame.getInstance().showLogin());
        popupMenu.add(logoutItem);

        popupMenu.show(invoker, 0, invoker.getHeight());
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel macFullWindowContentButtonsPlaceholder;
    private JToolBar toolBar;
    private JLabel titleLabel;
    private JPanel winFullWindowContentButtonsPlaceholder;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
