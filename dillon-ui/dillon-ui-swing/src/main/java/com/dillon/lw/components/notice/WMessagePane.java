package com.dillon.lw.components.notice;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatClearIcon;

import javax.swing.*;
import java.awt.*;

/**
 * @version： 0.0.1
 * @description:
 * @className: WMessagePane
 * @author: liwen
 * @date: 2021/11/4 10:50
 */
public class WMessagePane extends JPanel {
    private static final int ICON_SIZE = 35;
    private static final int MAX_WIDTH = 880;
    private static final int WIDTH_PADDING = 150;

    /**
     * 消息文字
     */
    private String message;

    /**
     * 主题
     * success/warning/info/error
     */
    private int type;


    private float alpha = 0f;

    /**
     * 显示时间, 毫秒。设为 0 则不会自动关闭
     */
    private int duration = 3000;

    /**
     * 是否显示关闭按钮
     */
    private boolean showClose;
    private Color borderColor = new Color(0xb3d8ff);

    private int arc = 15;

    private JLabel label = new JLabel();

    private JButton closeButton = new JButton();

    public WMessagePane(String message, int duration, boolean showClose, int type) {
        this.message = message;
        this.duration = duration;
        this.showClose = showClose;
        setType(type);
        setOpaque(false);
        setAlpha(0f);
        label.setIconTextGap(10);
        label.setText(message);
        label.setFont(label.getFont().deriveFont(18f));

        closeButton.setVisible(showClose);
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        FlatClearIcon clearIcon = new FlatClearIcon();
        closeButton.setIcon(clearIcon);
        closeButton.setBackground(new Color(0, 0, 0, 0));
        closeButton.putClientProperty("JButton.buttonType", "roundRect");
        this.setLayout(new BorderLayout());
        this.add(label, BorderLayout.CENTER);
        this.add(closeButton, BorderLayout.EAST);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D oldG = (Graphics2D) g;
        oldG.setComposite(AlphaComposite.SrcOver.derive(alpha));
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
        g2.setColor(getBorderColor());
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
        g2.dispose();
    }


    @Override
    public int getWidth() {
        int textWidth = label.getFontMetrics(label.getFont()).stringWidth(message) + WIDTH_PADDING;
        return Math.min(textWidth, MAX_WIDTH);
    }

    @Override
    public int getHeight() {
        return 60;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }


    public int getDuration() {
        return duration;
    }

    public boolean isShowClose() {
        return showClose;
    }


    public int getArc() {
        return arc;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setType(int type) {
        this.type = type;
        NoticeStyle style = NoticeStyle.resolve(type);
        FlatSVGIcon icon = new FlatSVGIcon(style.iconPath, ICON_SIZE, ICON_SIZE);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> style.foreground));
        label.setIcon(icon);
        label.setForeground(style.foreground);
        setBackground(style.background);
        setBorderColor(style.border);
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }
}
