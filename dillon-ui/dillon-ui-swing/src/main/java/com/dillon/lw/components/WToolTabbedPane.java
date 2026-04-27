package com.dillon.lw.components;

import cn.hutool.core.convert.Convert;
import com.dillon.lw.utils.ColorUtils;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatTabbedPane;
import com.formdev.flatlaf.icons.FlatClearIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class WToolTabbedPane extends JPanel {
    private static final int TAB_SHELL_HEIGHT = 80;
    private static final int TAB_CONTENT_TOP = 50;

    private static final String TOOL_ID_KEY = "toolId";
    private static final String TOOL_DESCRIPTOR_KEY = "toolDescriptor";
    private static final String TAB_READ_ONLY_KEY = "tabReadOnly";
    private static final String TAB_CLOSABLE_KEY = "tabClosable";

    private final FlatTabbedPane tabbedPane;
    private Consumer<ToolDescriptor> selectionListener;
    private Consumer<String> statusMessageConsumer;
    private int popupTabIndex = -1;
    private boolean suppressSelectionEvents;

    public WToolTabbedPane() {
        super(new BorderLayout());
        this.tabbedPane = new FlatTabbedPane();

        setOpaque(false);
        configureTabbedPane();
        installBehavior();

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void setSelectionListener(Consumer<ToolDescriptor> selectionListener) {
        this.selectionListener = selectionListener;
    }

    public void setStatusMessageConsumer(Consumer<String> statusMessageConsumer) {
        this.statusMessageConsumer = statusMessageConsumer;
    }

    public FlatTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setLeadingComponent(Component component) {
        tabbedPane.setLeadingComponent(component);
    }

    public void setTrailingComponent(Component component) {
        tabbedPane.setTrailingComponent(component);
    }

    public void restoreTabs(List<ToolDescriptor> descriptors, List<String> openToolIds, String activeToolId) {
        suppressSelectionEvents = true;
        try {
            tabbedPane.removeAll();
            for (String toolId : openToolIds) {
                ToolDescriptor descriptor = findDescriptorById(descriptors, toolId);
                if (descriptor != null) {
                    addToolTab(descriptor);
                }
            }

            if (tabbedPane.getTabCount() > 0) {
                String targetToolId = activeToolId != null ? activeToolId : openToolIds.get(0);
                int selectedIndex = indexOfOpenTab(targetToolId);
                tabbedPane.setSelectedIndex(selectedIndex >= 0 ? selectedIndex : 0);
            }
        } finally {
            suppressSelectionEvents = false;
        }

        refreshTabHeaders();
        notifySelectionChanged();
    }

    public void openTool(ToolDescriptor descriptor) {
        if (descriptor == null) {
            return;
        }

        int tabIndex = indexOfOpenTab(descriptor.getId());
        if (tabIndex < 0) {
            tabIndex = addToolTab(descriptor);
        }

        tabbedPane.setSelectedIndex(tabIndex);
    }

    public int getTabCount() {
        return tabbedPane.getTabCount();
    }

    public List<String> getOpenToolIds() {
        List<String> openToolIds = new ArrayList<String>();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String toolId = getToolId(tabbedPane.getComponentAt(i));
            if (toolId != null) {
                openToolIds.add(toolId);
            }
        }
        return openToolIds;
    }

    public String getSelectedToolId() {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        return selectedComponent == null ? null : getToolId(selectedComponent);
    }

    public boolean hasOpenTabs() {
        return tabbedPane.getTabCount() > 0;
    }



    private void configureTabbedPane() {
        tabbedPane.setOpaque(false);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabType(FlatTabbedPane.TabType.underlined);
        tabbedPane.setTabAreaAlignment(FlatTabbedPane.TabAreaAlignment.leading);
        tabbedPane.setTabAlignment(FlatTabbedPane.TabAlignment.leading);
        tabbedPane.setTabWidthMode(FlatTabbedPane.TabWidthMode.compact);
        tabbedPane.setTabsPopupPolicy(FlatTabbedPane.TabsPopupPolicy.asNeeded);
        tabbedPane.setTabsClosable(false);
        tabbedPane.setHideTabAreaWithOneTab(false);
        tabbedPane.setShowContentSeparators(false);
        tabbedPane.setShowTabSeparators(false);
        tabbedPane.setHasFullBorder(false);
        tabbedPane.setTabHeight(Math.max(40, UIManager.getInt("TabbedPane.tabHeight")));
        tabbedPane.setMinimumTabWidth(92);
        tabbedPane.setMaximumTabWidth(240);
        tabbedPane.setTabInsets(new Insets(2, 2, 2, 2));
        tabbedPane.setTabAreaInsets(new Insets(0, 0, 0, 0));
        tabbedPane.putClientProperty(FlatClientProperties.STYLE,
                "selectedBackground: #0000;"
                        + " hoverColor: #0000;"
                        + " underlineColor: #0000;"
                        + " inactiveUnderlineColor: #0000;");
    }

    private void installBehavior() {
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refreshTabHeaders();
                notifySelectionChanged();
            }
        });
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowTabPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowTabPopup(e);
            }
        });
    }

    private void notifySelectionChanged() {
        if (suppressSelectionEvents || selectionListener == null) {
            return;
        }
        selectionListener.accept(getSelectedDescriptor());
    }

    private ToolDescriptor getSelectedDescriptor() {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        return selectedComponent == null ? null : getDescriptor(selectedComponent);
    }

    private int addToolTab(ToolDescriptor descriptor) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.putClientProperty(TOOL_ID_KEY, descriptor.getId());
        wrapper.putClientProperty(TOOL_DESCRIPTOR_KEY, descriptor);
        wrapper.putClientProperty(TAB_READ_ONLY_KEY, descriptor.isReadOnly());
        wrapper.putClientProperty(TAB_CLOSABLE_KEY, descriptor.isClosable());
        wrapper.add(descriptor.getPanel(), BorderLayout.CENTER);

        tabbedPane.addTab(descriptor.getName(), descriptor.getIcon(), wrapper);
        int tabIndex = tabbedPane.getTabCount() - 1;
        tabbedPane.setToolTipTextAt(tabIndex, descriptor.getDescription());
        tabbedPane.setTabComponentAt(tabIndex, createTabHeader(descriptor));
        refreshTabHeaders();
        return tabIndex;
    }

    private int indexOfOpenTab(String toolId) {
        if (toolId == null) {
            return -1;
        }
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (toolId.equals(getToolId(tabbedPane.getComponentAt(i)))) {
                return i;
            }
        }
        return -1;
    }

    private void closeTab(int tabIndex) {
        if (tabIndex < 0 || tabIndex >= tabbedPane.getTabCount() || !isClosableTab(tabIndex)) {
            return;
        }

        String closedTitle = tabbedPane.getTitleAt(tabIndex);
        Component closedComponent = tabbedPane.getComponentAt(tabIndex);
        suppressSelectionEvents = true;
        try {
            tabbedPane.removeTabAt(tabIndex);
            SwingLifecycleUtils.disposeComponentTree(closedComponent);

            if (tabbedPane.getTabCount() > 0) {
                int nextIndex = Math.min(tabIndex, tabbedPane.getTabCount() - 1);
                tabbedPane.setSelectedIndex(nextIndex);
            }
        } finally {
            suppressSelectionEvents = false;
        }

        refreshTabHeaders();
        notifySelectionChanged();
        publishStatus("Closed " + closedTitle);
    }

    private void maybeShowTabPopup(MouseEvent e) {
        if (!e.isPopupTrigger()) {
            return;
        }

        int tabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
        if (tabIndex < 0) {
            return;
        }

        popupTabIndex = tabIndex;
        if (tabbedPane.getSelectedIndex() != tabIndex) {
            tabbedPane.setSelectedIndex(tabIndex);
        }
        createTabPopupMenu(tabIndex).show(tabbedPane, e.getX(), e.getY());
    }

    private JPopupMenu createTabPopupMenu(int tabIndex) {
        JPopupMenu popupMenu = new JPopupMenu();
        int closableTabCount = getClosableTabCount();

        popupMenu.add(createMenuItem("Close", e -> closeTab(tabIndex), isClosableTab(tabIndex)));
        popupMenu.add(createMenuItem("Close Others", e -> closeOtherTabs(tabIndex), closableTabCount > (isClosableTab(tabIndex) ? 1 : 0)));
        popupMenu.add(createMenuItem("Close All", e -> closeAllTabs(), closableTabCount > 0));
        popupMenu.addSeparator();
        popupMenu.add(createMenuItem("Close Left Tabs", e -> closeLeftTabs(tabIndex), hasClosableTabsOnLeft(tabIndex)));
        popupMenu.add(createMenuItem("Close Right Tabs", e -> closeRightTabs(tabIndex),
                hasClosableTabsOnRight(tabIndex)));
        popupMenu.addSeparator();
        popupMenu.add(createMenuItem("Close All Read-Only Tabs", e -> closeAllReadOnlyTabs(), hasReadOnlyTabs()));

        return popupMenu;
    }

    private JMenuItem createMenuItem(String text, java.awt.event.ActionListener listener, boolean enabled) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(listener);
        item.setEnabled(enabled);
        return item;
    }

    private void closeOtherTabs(int keepIndex) {
        List<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (i != keepIndex && isClosableTab(i)) {
                indexes.add(i);
            }
        }
        closeTabs(indexes, "Closed other tabs");
    }

    public void closeAllTabs() {
        List<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (isClosableTab(i)) {
                indexes.add(i);
            }
        }
        closeTabs(indexes, "Closed all tabs");
    }

    private void closeLeftTabs(int tabIndex) {
        List<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < tabIndex; i++) {
            if (isClosableTab(i)) {
                indexes.add(i);
            }
        }
        closeTabs(indexes, "Closed left tabs");
    }

    private void closeRightTabs(int tabIndex) {
        List<Integer> indexes = new ArrayList<Integer>();
        for (int i = tabIndex + 1; i < tabbedPane.getTabCount(); i++) {
            if (isClosableTab(i)) {
                indexes.add(i);
            }
        }
        closeTabs(indexes, "Closed right tabs");
    }

    private void closeAllReadOnlyTabs() {
        List<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (isReadOnlyTab(i) && isClosableTab(i)) {
                indexes.add(i);
            }
        }
        closeTabs(indexes, "Closed all read-only tabs");
    }

    private void closeTabs(List<Integer> indexes, String statusMessage) {
        if (indexes.isEmpty()) {
            return;
        }

        suppressSelectionEvents = true;
        try {
            Collections.sort(indexes, Collections.reverseOrder());
            for (Integer index : indexes) {
                if (index != null && index >= 0 && index < tabbedPane.getTabCount() && isClosableTab(index.intValue())) {
                    Component closedComponent = tabbedPane.getComponentAt(index.intValue());
                    tabbedPane.removeTabAt(index.intValue());
                    SwingLifecycleUtils.disposeComponentTree(closedComponent);
                }
            }

            if (tabbedPane.getTabCount() > 0) {
                int nextIndex = popupTabIndex;
                if (nextIndex < 0 || nextIndex >= tabbedPane.getTabCount()) {
                    nextIndex = tabbedPane.getTabCount() - 1;
                }
                tabbedPane.setSelectedIndex(nextIndex);
            }
        } finally {
            suppressSelectionEvents = false;
        }

        refreshTabHeaders();
        notifySelectionChanged();
        publishStatus(statusMessage);
    }

    private boolean hasReadOnlyTabs() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (isReadOnlyTab(i) && isClosableTab(i)) {
                return true;
            }
        }
        return false;
    }

    private int getClosableTabCount() {
        int count = 0;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (isClosableTab(i)) {
                count++;
            }
        }
        return count;
    }

    private boolean hasClosableTabsOnLeft(int tabIndex) {
        for (int i = 0; i < tabIndex; i++) {
            if (isClosableTab(i)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasClosableTabsOnRight(int tabIndex) {
        for (int i = tabIndex + 1; i < tabbedPane.getTabCount(); i++) {
            if (isClosableTab(i)) {
                return true;
            }
        }
        return false;
    }

    private boolean isReadOnlyTab(int tabIndex) {
        if (tabIndex < 0 || tabIndex >= tabbedPane.getTabCount()) {
            return false;
        }
        Component component = tabbedPane.getComponentAt(tabIndex);
        if (!(component instanceof JPanel)) {
            return false;
        }
        Object readOnly = ((JPanel) component).getClientProperty(TAB_READ_ONLY_KEY);
        return Boolean.TRUE.equals(readOnly);
    }

    private boolean isClosableTab(int tabIndex) {
        if (tabIndex < 0 || tabIndex >= tabbedPane.getTabCount()) {
            return false;
        }
        Component component = tabbedPane.getComponentAt(tabIndex);
        if (!(component instanceof JPanel)) {
            return true;
        }
        Object closable = ((JPanel) component).getClientProperty(TAB_CLOSABLE_KEY);
        return !Boolean.FALSE.equals(closable);
    }

    private Component createTabHeader(ToolDescriptor descriptor) {
        Component header = new TabHeaderComponent(descriptor);
        installTabPopupRelay(header);
        return header;
    }

    private void installTabPopupRelay(Component component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectTabFromHeader(component, e);
                relayTabPopup(component, e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                relayTabPopup(component, e);
            }
        });

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                installTabPopupRelay(child);
            }
        }
    }

    private void selectTabFromHeader(Component source, MouseEvent e) {
        if (e.isPopupTrigger() || !SwingUtilities.isLeftMouseButton(e) || source instanceof JButton) {
            return;
        }
        int tabIndex = tabbedPane.indexOfTabComponent(findTabHeaderComponent(source));
        if (tabIndex >= 0 && tabbedPane.getSelectedIndex() != tabIndex) {
            tabbedPane.setSelectedIndex(tabIndex);
        }
    }

    private void relayTabPopup(Component source, MouseEvent e) {
        if (!e.isPopupTrigger()) {
            return;
        }
        int tabIndex = tabbedPane.indexOfTabComponent(findTabHeaderComponent(source));
        if (tabIndex < 0) {
            return;
        }
        popupTabIndex = tabIndex;
        if (tabbedPane.getSelectedIndex() != tabIndex) {
            tabbedPane.setSelectedIndex(tabIndex);
        }
        MouseEvent convertedEvent = SwingUtilities.convertMouseEvent(source, e, tabbedPane);
        createTabPopupMenu(tabIndex).show(tabbedPane, convertedEvent.getX(), convertedEvent.getY());
    }

    private Component findTabHeaderComponent(Component source) {
        Component current = source;
        while (current != null) {
            if (tabbedPane.indexOfTabComponent(current) >= 0) {
                return current;
            }
            current = current.getParent();
        }
        return source;
    }

    private void refreshTabHeaders() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component tabComponent = tabbedPane.getTabComponentAt(i);
            if (tabComponent instanceof TabHeaderComponent) {
                ((TabHeaderComponent) tabComponent).setSelected(i == tabbedPane.getSelectedIndex());
            }
        }
    }

    private void publishStatus(String statusMessage) {
        if (statusMessageConsumer != null) {
            statusMessageConsumer.accept(statusMessage);
        }
    }

    private ToolDescriptor findDescriptorById(List<ToolDescriptor> descriptors, String toolId) {
        if (descriptors == null || toolId == null) {
            return null;
        }
        for (ToolDescriptor descriptor : descriptors) {
            if (toolId.equals(descriptor.getId())) {
                return descriptor;
            }
        }
        return null;
    }

    private String getToolId(Component component) {
        if (!(component instanceof JPanel)) {
            return null;
        }
        Object toolId = ((JPanel) component).getClientProperty(TOOL_ID_KEY);
        return toolId == null ? null : String.valueOf(toolId);
    }

    private ToolDescriptor getDescriptor(Component component) {
        if (!(component instanceof JPanel)) {
            return null;
        }
        Object descriptor = ((JPanel) component).getClientProperty(TOOL_DESCRIPTOR_KEY);
        return descriptor instanceof ToolDescriptor ? (ToolDescriptor) descriptor : null;
    }

    private Color resolveColor(String key, Color fallback) {
        Color color = UIManager.getColor(key);
        return color != null ? color : fallback;
    }

    private Color blend(Color start, Color end, float ratio) {
        float clampedRatio = Math.max(0f, Math.min(1f, ratio));
        float inverse = 1f - clampedRatio;
        return new Color(
                Math.round(start.getRed() * inverse + end.getRed() * clampedRatio),
                Math.round(start.getGreen() * inverse + end.getGreen() * clampedRatio),
                Math.round(start.getBlue() * inverse + end.getBlue() * clampedRatio)
        );
    }

    private boolean isDark(Color color) {
        if (color == null) {
            return false;
        }
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255d;
        return luminance < 0.58d;
    }

    private Color getReadableForeground(Color background) {
        return isDark(background) ? Color.WHITE : new Color(28, 34, 40);
    }

    private String resolveBadgeText(ToolDescriptor descriptor) {
        if (descriptor == null || descriptor.getName() == null || descriptor.getName().trim().isEmpty()) {
            return "T";
        }
        return descriptor.getName().substring(0, 1).toUpperCase(Locale.ENGLISH);
    }

    private class TabHeaderComponent extends JPanel {
        private final JComponent leadingComponent;
        private final BadgeComponent badgeComponent;
        private final JLabel iconLabel;
        private final JLabel titleLabel;
        private final JButton closeButton;
        private final javax.swing.Icon closeIcon;
        private final boolean closable;
        private boolean selected;
        private boolean hovered;

        private TabHeaderComponent(ToolDescriptor descriptor) {
            super(new BorderLayout(6, 0));
            this.iconLabel = createIconLabel(descriptor.getIcon());
            this.badgeComponent = iconLabel == null ? new BadgeComponent(resolveBadgeText(descriptor)) : null;
            this.leadingComponent = iconLabel != null ? iconLabel : badgeComponent;
            this.titleLabel = new JLabel(descriptor.getName());
            this.closeIcon = new FlatClearIcon();
            this.closeButton = createCloseButton();
            this.closable = descriptor.isClosable();

            setOpaque(false);
            setBorder(new EmptyBorder(8, 16, 8, 10));

            titleLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 15f));
            closeButton.setToolTipText("Close");
            closeButton.addActionListener(e -> {
                int index = tabbedPane.indexOfTabComponent(TabHeaderComponent.this);
                closeTab(index);
            });

            add(leadingComponent, BorderLayout.WEST);
            add(titleLabel, BorderLayout.CENTER);
            add(closeButton, BorderLayout.EAST);

            installHoverBehavior(this);
            installHoverBehavior(titleLabel);
            installHoverBehavior(closeButton);
            installHoverBehavior(leadingComponent);

            setSelected(false);
        }

        @Override
        public void updateUI() {
            super.updateUI();

            if (titleLabel != null && closeButton != null && leadingComponent != null) {
                updatePresentation();
            }
        }

        private void setSelected(boolean selected) {
            this.selected = selected;
            updatePresentation();
        }

        private void setHovered(boolean hovered) {
            this.hovered = hovered;
            updatePresentation();
        }

        private void updatePresentation() {
            Color focusColor = getFocusColor();
            Color labelColor = getLabelColor();
            Color selectedTextColor = getReadableForeground(getSelectedFillColor(focusColor));
            Color normalTextColor = ColorUtils.withAlpha(labelColor, hovered ? 228 : 176);
            titleLabel.setForeground(selected ? selectedTextColor : normalTextColor);
            closeButton.setForeground(selected ? selectedTextColor : normalTextColor);
            updateCloseButton(selected || hovered);
            if (badgeComponent != null) {
                badgeComponent.setState(selected || hovered, selected, focusColor, selectedTextColor);
            }
            repaint();
        }

        private JLabel createIconLabel(Icon icon) {
            Icon tabIcon = createTabIcon(icon);
            if (tabIcon == null) {
                return null;
            }
            JLabel label = new JLabel(tabIcon);
            label.setOpaque(false);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setPreferredSize(new Dimension(18, 18));
            label.setMinimumSize(new Dimension(18, 18));
            return label;
        }

        private Icon createTabIcon(Icon icon) {
            if (icon == null) {
                return null;
            }
            if (icon instanceof FlatSVGIcon) {
                return new FlatSVGIcon((FlatSVGIcon) icon).derive(16, 16);
            }
            return icon;
        }

        private JButton createCloseButton() {
            JButton button = new JButton();
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setFocusable(false);
            button.setOpaque(false);
            button.setRolloverEnabled(true);
            button.setPreferredSize(new Dimension(16, 16));
            button.setMinimumSize(new Dimension(16, 16));
            button.setMaximumSize(new Dimension(16, 16));
            button.putClientProperty("JButton.buttonType", "toolBarButton");
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return button;
        }

        private void updateCloseButton(boolean visibleState) {
            boolean showClose = closable && visibleState;
            closeButton.setIcon(showClose ? closeIcon : null);
            closeButton.setEnabled(showClose);
        }

        private Color getFocusColor() {
            Color focusColor = UIManager.getColor("App.accent.color");
            if (focusColor == null) {
                focusColor = UIManager.getColor("Component.focusColor");
            }
            if (focusColor == null) {
                focusColor = UIManager.getColor("TabbedPane.selectedBackground");
            }
            return focusColor != null ? focusColor : new Color(70, 130, 255);
        }

        private Color getLabelColor() {
            Color labelColor = UIManager.getColor("Label.foreground");
            return labelColor != null ? labelColor : new Color(240, 240, 240);
        }

        private Color getSelectedFillColor(Color focusColor) {
            Color shellBackground = resolveColor("App.baseBackground", resolveColor("Panel.background", getBackground()));
            return blend(shellBackground, focusColor, isDark(shellBackground) ? 0.78f : 0.68f);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                Color focusColor = getFocusColor();
                Color base = UIManager.getColor("Panel.background");
                if (base == null) {
                    base = getBackground();
                }

                int arc = 13;
                if (selected) {
                    Color selectedFillColor = UIManager.getColor("App.accent.color");
                    RoundRectangle2D.Float capsule =
                            new RoundRectangle2D.Float(1, 1, getWidth() - 2f, getHeight() - 2f, arc, arc);
                    g2.setColor(ColorUtils.withAlpha(selectedFillColor, hovered ? 50 : 70));
                    g2.fill(capsule);
                    g2.setColor(selectedFillColor);
                    g2.draw(capsule);
                } else if (hovered) {
                    RoundRectangle2D.Float capsule =
                            new RoundRectangle2D.Float(1, 1, getWidth() - 2f, getHeight() - 2f, arc, arc);
                    g2.setColor(ColorUtils.withAlpha(base, 120));
                    g2.fill(capsule);
                    g2.setColor(ColorUtils.withAlpha(focusColor, 120));
                    g2.draw(capsule);
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        private void installHoverBehavior(Component component) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setHovered(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), TabHeaderComponent.this);
                    setHovered(TabHeaderComponent.this.contains(point));
                }
            });
        }
    }

    private static class BadgeComponent extends JComponent {
        private final String text;
        private boolean active;
        private boolean selected;
        private Color accentColor = new Color(70, 130, 255);
        private Color textColor = Color.WHITE;

        private BadgeComponent(String text) {
            this.text = text;
            setOpaque(false);
            setPreferredSize(new Dimension(21, 21));
            setMinimumSize(new Dimension(21, 21));
        }

        private void setState(boolean active, boolean selected, Color accentColor, Color textColor) {
            this.active = active;
            this.selected = selected;
            if (accentColor != null) {
                this.accentColor = accentColor;
            }
            if (textColor != null) {
                this.textColor = textColor;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                Ellipse2D.Float circle = new Ellipse2D.Float(1.5f, 1.5f, getWidth() - 4f, getHeight() - 4f);
                g2.setColor(ColorUtils.withAlpha(textColor, active ? 224 : 176));
                if (selected) {
                    g2.setColor(accentColor);
                } else {
                    g2.setColor(ColorUtils.withAlpha(textColor, active ? 224 : 176));

                }
                g2.draw(circle);
                Font font = getFont().deriveFont(Font.BOLD, 11f);
                g2.setFont(font);
                FontMetrics metrics = g2.getFontMetrics(font);
                int textX = (getWidth() - metrics.stringWidth(text)) / 2;
                int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                if (selected) {
                    g2.setColor(accentColor);
                }
                g2.drawString(text, textX, textY);
            } finally {
                g2.dispose();
            }
        }
    }
}
