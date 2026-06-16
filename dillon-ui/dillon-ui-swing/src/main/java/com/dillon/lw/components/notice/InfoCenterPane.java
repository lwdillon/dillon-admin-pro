package com.dillon.lw.components.notice;

import com.dillon.lw.components.WScrollPane;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Swing 版信息中心面板，提供类似 GemsFX InfoCenterPane 的集中通知管理能力。
 */
public class InfoCenterPane extends JPanel {

    public static final String UNREAD_COUNT_PROPERTY = "unreadCount";

    private final List<InfoCenterItem> items = new ArrayList<>();
    private final Map<String, InfoCenterGroup> groups = new LinkedHashMap<>();
    private final JPanel pinnedPane = new JPanel();
    private final JPanel listPane = new ScrollableListPane();
    private final JLabel titleLabel = new JLabel("消息中心");
    private final JLabel countLabel = new JLabel();
    private final JLabel emptyLabel = new JLabel("暂无消息", SwingConstants.CENTER);
    private final JButton markAllReadButton = new JButton("全部已读");
    private final JButton clearButton = new JButton("清空");
    private final InfoCenterNotificationViewFactory defaultViewFactory = InfoCenterCard::new;
    private int unreadCount;
    private String emptyText = "暂无消息";
    private int displayThreshold = 3;
    private String pendingAnimatedItemId;

    public InfoCenterPane() {
        super(new BorderLayout(0, 10));
        initHeader();
        initList();
        refreshList();
    }

    private void initHeader() {
        setOpaque(false);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JPanel titlePane = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePane.setOpaque(false);
        FlatSVGIcon icon = new FlatSVGIcon("icons/bell.svg", 18, 18);
        titleLabel.setIcon(icon);
        titleLabel.setIconTextGap(8);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titlePane.add(titleLabel);
        titlePane.add(countLabel);
        header.add(titlePane, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        configureActionButton(markAllReadButton);
        configureActionButton(clearButton);
        markAllReadButton.addActionListener(e -> markAllRead());
        clearButton.addActionListener(e -> clearNotifications());
        actions.add(markAllReadButton);
        actions.add(clearButton);
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void initList() {
        JPanel contentPane = new JPanel(new BorderLayout(0, 10));
        contentPane.setOpaque(false);

        pinnedPane.setLayout(new BoxLayout(pinnedPane, BoxLayout.Y_AXIS));
        pinnedPane.setOpaque(false);

        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
        listPane.setOpaque(false);

        emptyLabel.setBorder(new EmptyBorder(48, 12, 48, 12));
        emptyLabel.setForeground(uiColor("App.secondaryTextColor", "Label.disabledForeground", 0x8a919f));

        WScrollPane scrollPane = new WScrollPane(listPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(pinnedPane, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        add(contentPane, BorderLayout.CENTER);
    }

    private void configureActionButton(JButton button) {
        button.setFocusable(false);
        button.putClientProperty("JButton.buttonType", "roundRect");
    }

    public InfoCenterItem addInfo(String title, String message) {
        return addNotification(title, message, WMessage.INFO);
    }

    public InfoCenterItem addSuccess(String title, String message) {
        return addNotification(title, message, WMessage.SUCCESS);
    }

    public InfoCenterItem addWarning(String title, String message) {
        return addNotification(title, message, WMessage.WARNING);
    }

    public InfoCenterItem addError(String title, String message) {
        return addNotification(title, message, WMessage.ERROR);
    }

    public InfoCenterItem addNotification(String title, String message, int type) {
        return addNotification(new InfoCenterItem(title, message, type));
    }

    public InfoCenterItem addNotification(String title, String message, int type, Runnable action) {
        return addNotification(new InfoCenterItem(title, message, type, LocalDateTime.now(), false, action));
    }

    public InfoCenterItem addNotification(String groupId, String groupTitle, String title, String message, int type) {
        return addNotification(new InfoCenterItem(groupId, groupTitle, title, message, type));
    }

    public InfoCenterItem addNotification(String groupId, String groupTitle, String title, String message, int type, Runnable action) {
        return addNotification(new InfoCenterItem(groupId, groupTitle, title, message, type, LocalDateTime.now(), false, action));
    }

    public InfoCenterItem addNotification(InfoCenterItem item) {
        runOnEdt(() -> {
            ensureGroup(item.getGroupId(), item.getGroupTitle());
            items.add(0, item);
            pendingAnimatedItemId = item.getId();
            refreshList();
        });
        return item;
    }

    public void removeNotification(InfoCenterItem item) {
        if (item == null) {
            return;
        }
        runOnEdt(() -> {
            items.remove(item);
            refreshList();
        });
    }

    public void markAllRead() {
        runOnEdt(() -> {
            for (InfoCenterItem item : items) {
                item.setRead(true);
            }
            refreshList();
        });
    }

    public void clearNotifications() {
        runOnEdt(() -> {
            items.clear();
            refreshList();
        });
    }

    public List<InfoCenterItem> getNotifications() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public InfoCenterGroup getGroup(String groupId) {
        return groups.get(groupId);
    }

    public InfoCenterGroup configureGroup(String groupId, String groupTitle, Consumer<InfoCenterGroup> configurer) {
        InfoCenterGroup group = ensureGroup(groupId, groupTitle);
        if (configurer != null) {
            configurer.accept(group);
        }
        refreshList();
        return group;
    }

    public void setGroupViewFactory(String groupId, InfoCenterNotificationViewFactory viewFactory) {
        configureGroup(groupId, groupId, group -> group.setViewFactory(viewFactory));
    }

    public int getDisplayThreshold() {
        return displayThreshold;
    }

    public void setDisplayThreshold(int displayThreshold) {
        this.displayThreshold = Math.max(1, displayThreshold);
        refreshList();
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public String getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText == null || emptyText.trim().isEmpty() ? "暂无消息" : emptyText;
        emptyLabel.setText(this.emptyText);
    }

    private void refreshList() {
        int oldUnreadCount = unreadCount;
        unreadCount = calculateUnreadCount();

        pinnedPane.removeAll();
        listPane.removeAll();
        if (items.isEmpty()) {
            listPane.add(emptyLabel);
        } else {
            Map<String, List<InfoCenterItem>> groupedItems = groupItems();
            for (Map.Entry<String, List<InfoCenterItem>> entry : groupedItems.entrySet()) {
                InfoCenterGroup group = ensureGroup(entry.getKey(), entry.getValue().get(0).getGroupTitle());
                InfoCenterGroupPanel groupPanel = new InfoCenterGroupPanel(group, entry.getValue());
                groupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                if (group.isPinned()) {
                    pinnedPane.add(groupPanel);
                    pinnedPane.add(Box.createVerticalStrut(10));
                } else {
                    listPane.add(groupPanel);
                    listPane.add(Box.createVerticalStrut(10));
                }
            }
        }

        countLabel.setText(unreadCount > 0 ? unreadCount + " 未读" : "无未读");
        countLabel.setForeground(unreadCount > 0
                ? uiColor("ColorPalette.notice.info.fg", "Label.foreground", 0x409EFF)
                : uiColor("App.secondaryTextColor", "Label.disabledForeground", 0x8a919f));
        markAllReadButton.setEnabled(unreadCount > 0);
        clearButton.setEnabled(!items.isEmpty());

        pinnedPane.setVisible(pinnedPane.getComponentCount() > 0);
        pinnedPane.revalidate();
        pinnedPane.repaint();
        listPane.revalidate();
        listPane.repaint();
        firePropertyChange(UNREAD_COUNT_PROPERTY, oldUnreadCount, unreadCount);
        pendingAnimatedItemId = null;
    }

    private Map<String, List<InfoCenterItem>> groupItems() {
        Map<String, List<InfoCenterItem>> groupedItems = new LinkedHashMap<>();
        for (InfoCenterItem item : items) {
            groupedItems.computeIfAbsent(item.getGroupId(), key -> new ArrayList<>()).add(item);
        }
        return groupedItems;
    }

    private InfoCenterGroup ensureGroup(String groupId, String groupTitle) {
        String key = groupId == null || groupId.trim().isEmpty() ? InfoCenterItem.DEFAULT_GROUP_ID : groupId;
        InfoCenterGroup group = groups.get(key);
        if (group == null) {
            group = new InfoCenterGroup(key, groupTitle);
            group.setDisplayThreshold(displayThreshold);
            groups.put(key, group);
        } else if (groupTitle != null && !groupTitle.trim().isEmpty()) {
            group.setTitle(groupTitle);
        }
        return group;
    }

    private int calculateUnreadCount() {
        int count = 0;
        for (InfoCenterItem item : items) {
            if (!item.isRead()) {
                count++;
            }
        }
        return count;
    }

    private void runOnEdt(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    private static Color uiColor(String primaryKey, String fallbackKey, int fallback) {
        Color color = UIManager.getColor(primaryKey);
        if (color == null) {
            color = UIManager.getColor(fallbackKey);
        }
        return color == null ? new Color(fallback) : color;
    }

    private static int unreadCount(List<InfoCenterItem> items) {
        int count = 0;
        for (InfoCenterItem item : items) {
            if (!item.isRead()) {
                count++;
            }
        }
        return count;
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private class InfoCenterGroupPanel extends JPanel {

        private final InfoCenterGroup group;
        private final List<InfoCenterItem> groupItems;
        private final JPanel cardsPane = new JPanel();

        private InfoCenterGroupPanel(InfoCenterGroup group, List<InfoCenterItem> groupItems) {
            super(new BorderLayout(0, 8));
            this.group = group;
            this.groupItems = groupItems;
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 0, 0));
            add(createHeader(), BorderLayout.NORTH);
            add(createCardsPane(), BorderLayout.CENTER);
        }

        private JComponent createHeader() {
            JPanel header = new JPanel(new BorderLayout(8, 0));
            header.setOpaque(false);
            header.setBorder(new EmptyBorder(0, 2, 0, 2));

            String title = group.isPinned() ? "置顶 · " + group.getTitle() : group.getTitle();
            JLabel label = new JLabel(title + "  " + unreadCount(groupItems) + "/" + groupItems.size());
            label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
            label.setForeground(uiColor("App.textColor", "Label.foreground", 0x1f2937));
            header.add(label, BorderLayout.WEST);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            actions.setOpaque(false);
            actions.add(createGroupButton(group.isExpanded() ? "堆叠" : "展开", () -> {
                group.setExpanded(!group.isExpanded());
                group.setListView(false);
                refreshList();
            }));
            if (groupItems.size() > group.getDisplayThreshold()) {
                actions.add(createGroupButton(group.isListView() ? "收起列表" : "全部", () -> {
                    group.setListView(!group.isListView());
                    group.setExpanded(true);
                    refreshList();
                }));
            }
            header.add(actions, BorderLayout.EAST);
            return header;
        }

        private JComponent createCardsPane() {
            cardsPane.setLayout(new BoxLayout(cardsPane, BoxLayout.Y_AXIS));
            cardsPane.setOpaque(false);

            List<InfoCenterItem> visibleItems = resolveVisibleItems();
            int index = 0;
            for (InfoCenterItem item : visibleItems) {
                JComponent card = createNotificationView(item, index);
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                cardsPane.add(card);
                cardsPane.add(Box.createVerticalStrut(group.isExpanded() || group.isListView() ? 8 : 0));
                index++;
            }

            if (!group.isListView() && groupItems.size() > group.getDisplayThreshold()) {
                cardsPane.add(createOverflowHint(groupItems.size() - group.getDisplayThreshold()));
            }
            return cardsPane;
        }

        private List<InfoCenterItem> resolveVisibleItems() {
            if (group.isListView()) {
                return groupItems;
            }
            if (group.isExpanded()) {
                return groupItems.subList(0, Math.min(group.getDisplayThreshold(), groupItems.size()));
            }
            int count = group.isStacked() ? Math.min(3, groupItems.size()) : 1;
            return groupItems.subList(0, count);
        }

        private JComponent createNotificationView(InfoCenterItem item, int stackIndex) {
            InfoCenterNotificationViewFactory factory = group.getViewFactory() == null
                    ? defaultViewFactory
                    : group.getViewFactory();
            JComponent view = factory.createView(item, () -> removeNotification(item), InfoCenterPane.this::refreshList);
            if (view instanceof InfoCenterCard && item.getId().equals(pendingAnimatedItemId)) {
                ((InfoCenterCard) view).playSlideIn();
            }
            if (!group.isExpanded() && !group.isListView() && group.isStacked() && stackIndex > 0) {
                view.setBorder(BorderFactory.createCompoundBorder(
                        new EmptyBorder(2, stackIndex * 8, 0, stackIndex * 8),
                        view.getBorder()
                ));
            }
            return view;
        }

        private JComponent createOverflowHint(int hiddenCount) {
            JButton button = createGroupButton("还有 " + hiddenCount + " 条，切换到列表视图", () -> {
                group.setExpanded(true);
                group.setListView(true);
                refreshList();
            });
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            return button;
        }

        private JButton createGroupButton(String text, Runnable action) {
            JButton button = new JButton(text);
            button.setFocusable(false);
            button.putClientProperty("JButton.buttonType", "roundRect");
            button.addActionListener(event -> action.run());
            return button;
        }
    }

    private static class ScrollableListPane extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(visibleRect.height - 24, 24);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
