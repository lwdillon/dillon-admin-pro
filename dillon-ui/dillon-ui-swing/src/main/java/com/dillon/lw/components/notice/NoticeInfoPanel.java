package com.dillon.lw.components.notice;


import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatClearIcon;
import org.fife.rsta.ui.CollapsibleSectionPanel;

import javax.swing.*;
import java.awt.*;

public class NoticeInfoPanel extends JPanel {
    private CollapsibleSectionPanel collapsibleSectionPanel;
    private JPanel infoPanel;

    private JButton colseBut;

    private JLabel messageLabel;


    public NoticeInfoPanel() {
        infoPanel = new JPanel(new BorderLayout(0, 0));
        messageLabel = new JLabel("", JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        infoPanel.add(messageLabel);
        infoPanel.add(colseBut = new JButton(new FlatClearIcon()), BorderLayout.EAST);
        colseBut.addActionListener(e -> {
            collapsibleSectionPanel.hideBottomComponent();
        });

        colseBut.setFocusable(false);
        colseBut.setBorderPainted(false);
        colseBut.setFocusPainted(false);
        colseBut.setBackground(new Color(0, 0, 0, 0));
        colseBut.putClientProperty("JButton.buttonType", "roundRect");

        collapsibleSectionPanel = new CollapsibleSectionPanel();
        collapsibleSectionPanel.addBottomComponent(infoPanel);
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(0, 0));
        collapsibleSectionPanel.add(label);
        this.setLayout(new BorderLayout(0, 0));
        this.add(collapsibleSectionPanel, BorderLayout.NORTH);
    }

    public void showMessage(String message, int type) {
        Color foreground = null;
        Color background = null;
        Color border = null;
        FlatSVGIcon icon = null;

        switch (type) {
            case WMessage.SUCCESS:
                foreground = UIManager.getColor("ColorPalette.notice.success.fg") == null ? new Color(0x67C23A) : UIManager.getColor("ColorPalette.notice.success.fg");
                background = UIManager.getColor("ColorPalette.notice.success.bg") == null ? new Color(0xf0f9eb) : UIManager.getColor("ColorPalette.notice.success.bg");
                border = UIManager.getColor("ColorPalette.notice.success.bd") == null ? new Color(0xc2e7b0) : UIManager.getColor("ColorPalette.notice.success.bd");
                icon = new FlatSVGIcon("icons/success.svg", 20, 20);
                break;
            case WMessage.WARNING:
                foreground = UIManager.getColor("ColorPalette.notice.warn.fg") == null ? new Color(0xE6A23C) : UIManager.getColor("ColorPalette.notice.warn.fg");
                background = UIManager.getColor("ColorPalette.notice.warn.bg") == null ? new Color(0xfdf6ec) : UIManager.getColor("ColorPalette.notice.warn.bg");
                border = UIManager.getColor("ColorPalette.notice.warn.bd") == null ? new Color(0xf5dab1) : UIManager.getColor("ColorPalette.notice.warn.bd");
                icon = new FlatSVGIcon("icons/warning.svg", 20, 20);
                break;
            case WMessage.ERROR:
                foreground = UIManager.getColor("ColorPalette.notice.error.fg") == null ? new Color(0xF56C6C) : UIManager.getColor("ColorPalette.notice.error.fg");
                background = UIManager.getColor("ColorPalette.notice.error.bg") == null ? new Color(0xfef0f0) : UIManager.getColor("ColorPalette.notice.error.bg");
                border = UIManager.getColor("ColorPalette.notice.error.bd") == null ? new Color(0xfbc4c4) : UIManager.getColor("ColorPalette.notice.error.bd");
                icon = new FlatSVGIcon("icons/error.svg", 20, 20);
                break;
            default:
                foreground = UIManager.getColor("ColorPalette.notice.info.fg") == null ? new Color(0x409EFF) : UIManager.getColor("ColorPalette.notice.info.fg");
                background = UIManager.getColor("ColorPalette.notice.info.bg") == null ? new Color(0xecf5ff) : UIManager.getColor("ColorPalette.notice.info.bg");
                border = UIManager.getColor("ColorPalette.notice.info.bd") == null ? new Color(0xb3d8ff) : UIManager.getColor("ColorPalette.notice.info.bd");
                icon = new FlatSVGIcon("icons/info.svg", 20, 20);
        }

        Color finalForeground = foreground;
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
            return finalForeground;
        }));
        messageLabel.setText(message);
        messageLabel.setIcon(icon);
        messageLabel.setForeground(foreground);
        infoPanel.setBackground(background);
        infoPanel.setBorder(BorderFactory.createLineBorder(border));
        collapsibleSectionPanel.showBottomComponent(infoPanel);
    }

    public void hideMessage() {
        collapsibleSectionPanel.hideBottomComponent();
    }

}
