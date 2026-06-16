package com.dillon.lw.components.notice;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatClearIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

/**
 * InfoCenterPane 使用的单条通知卡片。
 */
class InfoCenterCard extends JPanel {

    private static final int ICON_SIZE = 20;
    private static final int DOT_SIZE = 8;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final InfoCenterItem item;
    private final JButton closeButton = new JButton(new FlatClearIcon());
    private final Runnable onClose;
    private final Runnable onReadChanged;
    private boolean hovered;
    private int slideOffsetX;

    InfoCenterCard(InfoCenterItem item, Runnable onClose, Runnable onReadChanged) {
        super(new BorderLayout(10, 0));
        this.item = item;
        this.onClose = onClose;
        this.onReadChanged = onReadChanged;
        initStyle();
        initContent();
        initMouseBehavior();
    }

    private void initStyle() {
        setOpaque(false);
        setBorder(new EmptyBorder(12, 12, 12, 8));
        setCursor(item.hasAction() ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
    }

    private void initContent() {
        NoticeStyle style = NoticeStyle.resolve(item.getType());

        JLabel iconLabel = new JLabel(new FlatSVGIcon(style.iconPath, ICON_SIZE, ICON_SIZE));
        ((FlatSVGIcon) iconLabel.getIcon()).setColorFilter(new FlatSVGIcon.ColorFilter(color -> style.foreground));
        iconLabel.setBorder(new EmptyBorder(2, 0, 0, 0));
        add(iconLabel, BorderLayout.WEST);

        JPanel textPane = new JPanel(new BorderLayout(0, 6));
        textPane.setOpaque(false);
        textPane.add(createHeader(style), BorderLayout.NORTH);
        textPane.add(createMessageLabel(), BorderLayout.CENTER);
        add(textPane, BorderLayout.CENTER);

        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setToolTipText("关闭");
        closeButton.addActionListener(e -> onClose.run());
        add(closeButton, BorderLayout.EAST);
    }

    private JComponent createHeader(NoticeStyle style) {
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);

        JPanel titlePane = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titlePane.setOpaque(false);
        titlePane.add(new UnreadDot(style));

        JLabel titleLabel = new JLabel(emptyToDefault(item.getTitle(), "通知"));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setForeground(uiColor("App.textColor", "Label.foreground", 0x1f2937));
        titlePane.add(titleLabel);
        header.add(titlePane, BorderLayout.CENTER);

        JLabel timeLabel = new JLabel(TIME_FORMATTER.format(item.getTime()));
        timeLabel.setFont(timeLabel.getFont().deriveFont(12f));
        timeLabel.setForeground(uiColor("App.secondaryTextColor", "Label.disabledForeground", 0x8a919f));
        header.add(timeLabel, BorderLayout.EAST);
        return header;
    }

    private JLabel createMessageLabel() {
        JLabel label = new JLabel(toHtml(item.getMessage()));
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setForeground(uiColor("App.secondaryTextColor", "Label.foreground", 0x4b5563));
        return label;
    }

    private void initMouseBehavior() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isDescendingFrom(e.getComponent(), closeButton)) {
                    return;
                }
                markRead();
                item.runAction();
            }
        });
    }

    private void markRead() {
        if (!item.isRead()) {
            item.setRead(true);
            onReadChanged.run();
            repaint();
        }
    }

    void playSlideIn() {
        slideOffsetX = 44;
        Timer timer = new Timer(12, null);
        timer.addActionListener(event -> {
            slideOffsetX = Math.max(0, slideOffsetX - 4);
            repaint();
            if (slideOffsetX == 0) {
                timer.stop();
            }
        });
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        if (slideOffsetX <= 0) {
            super.paint(g);
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.translate(slideOffsetX, 0);
            super.paint(g2);
        } finally {
            g2.dispose();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = uiInt("App.arc", 14);
            g2.setColor(cardBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(uiColor("App.borderColor", "Component.borderColor", 0xd9dee8));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }

    private Color cardBackground() {
        if (hovered) {
            return uiColor("List.hoverBackground", "App.hoverBackground", 0xf5f7fb);
        }
        if (!item.isRead()) {
            return uiColor("App.baseBackground", "Panel.background", 0xffffff);
        }
        Color base = uiColor("App.baseBackground", "Panel.background", 0xffffff);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), 190);
    }

    private static String toHtml(String text) {
        String value = emptyToDefault(text, "");
        return "<html><body style='width: 250px'>" + escapeHtml(value).replace("\n", "<br>") + "</body></html>";
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String emptyToDefault(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }

    private static Color uiColor(String primaryKey, String fallbackKey, int fallback) {
        Color color = UIManager.getColor(primaryKey);
        if (color == null) {
            color = UIManager.getColor(fallbackKey);
        }
        return color == null ? new Color(fallback) : color;
    }

    private static int uiInt(String key, int fallback) {
        int value = UIManager.getInt(key);
        return value > 0 ? value : fallback;
    }

    private class UnreadDot extends JComponent {
        private final NoticeStyle style;

        private UnreadDot(NoticeStyle style) {
            this.style = style;
            setPreferredSize(new Dimension(DOT_SIZE, DOT_SIZE));
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (item.isRead()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(style.foreground);
                g2.fillOval(0, 0, DOT_SIZE, DOT_SIZE);
            } finally {
                g2.dispose();
            }
        }
    }
}
