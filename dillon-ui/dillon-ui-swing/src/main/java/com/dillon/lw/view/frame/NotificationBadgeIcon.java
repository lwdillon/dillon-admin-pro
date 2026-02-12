package com.dillon.lw.view.frame;

import javax.swing.*;
import java.awt.*;

/**
 * 在基础图标右上角绘制未读角标（红点 + 数字）。
 * <p>
 * 该类只负责绘制，不持有业务状态。
 * 状态更新由调用方通过 {@link #setCount(int)} 驱动。
 * </p>
 */
public class NotificationBadgeIcon implements Icon {

    private static final int MAX_DISPLAY = 99;
    private static final Color BADGE_BG = new Color(0xE53935);
    private static final Color BADGE_FG = Color.WHITE;
    private static final int BADGE_TOP_OFFSET = 0;
    private static final int BADGE_RIGHT_OFFSET = 2;
    private static final int BADGE_SINGLE_SIZE = 10;

    private final Icon baseIcon;
    private int count;

    public NotificationBadgeIcon(Icon baseIcon) {
        this.baseIcon = baseIcon;
    }

    /**
     * 设置未读数量。
     * 小于等于 0 时仅显示基础图标，不绘制红点。
     */
    public void setCount(int count) {
        this.count = Math.max(0, count);
    }

    public int getCount() {
        return count;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int baseX = x;
        int baseY = y;
        baseIcon.paintIcon(c, g, baseX, baseY);
        if (count <= 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            String text = count > MAX_DISPLAY ? (MAX_DISPLAY + "+") : String.valueOf(count);

            // 角标字号与尺寸按图标高度自适应，避免不同缩放下错位。
            int baseWidth = baseIcon.getIconWidth();
            int baseHeight = baseIcon.getIconHeight();
            int fontSize = Math.max(9, baseHeight / 2 - 1);
            Font font = g2.getFont().deriveFont(Font.BOLD, fontSize);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();

            int iconRight = baseX + baseWidth - 1;
            int badgeY = baseY + BADGE_TOP_OFFSET;

            int badgeWidth;
            int badgeHeight;
            int badgeX;
            if (count < 10) {
                // 单位数字：使用小红点尺寸，贴着右上角。
                badgeWidth = BADGE_SINGLE_SIZE;
                badgeHeight = BADGE_SINGLE_SIZE;
                badgeX = iconRight - badgeWidth + BADGE_RIGHT_OFFSET;
                g2.setColor(BADGE_BG);
                g2.fillOval(badgeX, badgeY, badgeWidth, badgeHeight);
            } else {
                // 多位数字：右边缘固定在同一锚点，宽度只向左扩展。
                int textWidth = fm.stringWidth(text);
                badgeHeight = Math.max(12, fm.getHeight() - 3);
                badgeWidth = Math.max(badgeHeight, textWidth + 7);
                int badgeRight = iconRight + BADGE_RIGHT_OFFSET;
                badgeX = badgeRight - badgeWidth;
                g2.setColor(BADGE_BG);
                g2.fillRoundRect(badgeX, badgeY, badgeWidth, badgeHeight, badgeHeight, badgeHeight);
            }

            if (count >= 10) {
                int textWidth = fm.stringWidth(text);
                g2.setColor(BADGE_FG);
                int textX = badgeX + (badgeWidth - textWidth) / 2;
                int textY = badgeY + (badgeHeight - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, textX, textY);
            }
        } finally {
            g2.dispose();
        }
    }

    @Override
    public int getIconWidth() {
        return baseIcon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return baseIcon.getIconHeight();
    }
}
