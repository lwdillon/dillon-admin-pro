package com.dillon.lw.components.notice;


import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatClearIcon;
import org.fife.rsta.ui.CollapsibleSectionPanel;

import javax.swing.*;
import java.awt.*;

public class NoticeInfoPanel extends JPanel {
    private static final int ICON_SIZE = 20;

    private CollapsibleSectionPanel collapsibleSectionPanel;
    private JPanel infoPanel;

    private JButton closeButton;

    private JLabel messageLabel;


    public NoticeInfoPanel() {
        infoPanel = new JPanel(new BorderLayout(0, 0));
        messageLabel = new JLabel("", JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(messageLabel);
        infoPanel.add(closeButton = new JButton(new FlatClearIcon()), BorderLayout.EAST);
        closeButton.addActionListener(e -> {
            collapsibleSectionPanel.hideBottomComponent();
        });

        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setBackground(new Color(0, 0, 0, 0));
        closeButton.putClientProperty("JButton.buttonType", "roundRect");

        collapsibleSectionPanel = new CollapsibleSectionPanel();
        collapsibleSectionPanel.addBottomComponent(infoPanel);
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(0, 0));
        collapsibleSectionPanel.add(label);
        this.setLayout(new BorderLayout(0, 0));
        this.add(collapsibleSectionPanel, BorderLayout.NORTH);
    }

    public void showMessage(String message, int type) {
        NoticeStyle style = NoticeStyle.resolve(type);
        FlatSVGIcon icon = new FlatSVGIcon(style.iconPath, ICON_SIZE, ICON_SIZE);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> style.foreground));
        messageLabel.setText(message);
        messageLabel.setIcon(icon);
        messageLabel.setForeground(style.foreground);
        infoPanel.setBackground(style.background);
        infoPanel.setBorder(BorderFactory.createLineBorder(style.border));
        collapsibleSectionPanel.showBottomComponent(infoPanel);
    }

    public void hideMessage() {
        collapsibleSectionPanel.hideBottomComponent();
    }

}
