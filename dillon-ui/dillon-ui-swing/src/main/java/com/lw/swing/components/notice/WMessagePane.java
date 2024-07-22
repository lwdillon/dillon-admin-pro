package com.lw.swing.components.notice;

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
        Graphics2D oldG= (Graphics2D) g;
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
       int mesW= label.getFontMetrics(label.getFont()).stringWidth(message)+150;
        return mesW>900?880:mesW;
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
        Color foreground = null;
        Color background = null;
        Color border = null;
        FlatSVGIcon icon = null;

        switch (type) {
            case WMessage.SUCCESS:
                foreground = UIManager.getColor("ColorPalette.notice.success.fg") == null ? new Color(0x67C23A) : UIManager.getColor("ColorPalette.notice.success.fg");
                background = UIManager.getColor("ColorPalette.notice.success.bg") == null ? new Color(0xf0f9eb) : UIManager.getColor("ColorPalette.notice.success.bg");
                border = UIManager.getColor("ColorPalette.notice.success.bd") == null ? new Color(0xc2e7b0) : UIManager.getColor("ColorPalette.notice.success.bd");
                icon = new FlatSVGIcon("icons/success.svg", 35, 35);
                break;
            case WMessage.WARNING:
                foreground = UIManager.getColor("ColorPalette.notice.warn.fg") == null ? new Color(0xE6A23C) : UIManager.getColor("ColorPalette.notice.warn.fg");
                background = UIManager.getColor("ColorPalette.notice.warn.bg") == null ? new Color(0xfdf6ec) : UIManager.getColor("ColorPalette.notice.warn.bg");
                border = UIManager.getColor("ColorPalette.notice.warn.bd") == null ? new Color(0xf5dab1) : UIManager.getColor("ColorPalette.notice.warn.bd");
                icon = new FlatSVGIcon("icons/warning.svg", 35, 35);
                break;
            case WMessage.ERROR:
                foreground = UIManager.getColor("ColorPalette.notice.error.fg") == null ? new Color(0xF56C6C) : UIManager.getColor("ColorPalette.notice.error.fg");
                background = UIManager.getColor("ColorPalette.notice.error.bg") == null ? new Color(0xfef0f0) : UIManager.getColor("ColorPalette.notice.error.bg");
                border = UIManager.getColor("ColorPalette.notice.error.bd") == null ? new Color(0xfbc4c4) : UIManager.getColor("ColorPalette.notice.error.bd");
                icon = new FlatSVGIcon("icons/error.svg", 35, 35);
                break;
            default:
                foreground = UIManager.getColor("ColorPalette.notice.info.fg") == null ? new Color(0x409EFF) : UIManager.getColor("ColorPalette.notice.info.fg");
                background = UIManager.getColor("ColorPalette.notice.info.bg") == null ? new Color(0xecf5ff) : UIManager.getColor("ColorPalette.notice.info.bg");
                border = UIManager.getColor("ColorPalette.notice.info.bd") == null ? new Color(0xb3d8ff) : UIManager.getColor("ColorPalette.notice.info.bd");
                icon = new FlatSVGIcon("icons/info.svg", 35, 35);
        }

        Color finalForeground = foreground;
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
            return finalForeground;
        }));
        label.setIcon(icon);
        label.setForeground(foreground);
        setBackground(background);
        setBorderColor(border);
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
