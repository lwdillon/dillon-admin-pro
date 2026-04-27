package com.dillon.lw.components;

import javax.swing.*;
import java.util.function.Supplier;

public class ToolDescriptor {

    private final String id;
    private final String name;
    private final String category;
    private final String description;
    private final Icon icon;
    private final boolean readOnly;
    private final boolean closable;
    private final Supplier<JComponent> panelSupplier;
    private JComponent cachedPanel;

    public ToolDescriptor(String id, String name, String category, String description, Icon icon,
                          Supplier<JComponent> panelSupplier) {
        this(id, name, category, description, icon, false, true, panelSupplier);
    }

    public ToolDescriptor(String id, String name, String category, String description, Icon icon,
                          boolean readOnly, Supplier<JComponent> panelSupplier) {
        this(id, name, category, description, icon, readOnly, true, panelSupplier);
    }

    public ToolDescriptor(String id, String name, String category, String description, Icon icon,
                          boolean readOnly, boolean closable, Supplier<JComponent> panelSupplier) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.icon = icon;
        this.readOnly = readOnly;
        this.closable = closable;
        this.panelSupplier = panelSupplier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Icon getIcon() {
        return icon;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isClosable() {
        return closable;
    }

    public JComponent getPanel() {
        if (cachedPanel == null) {
            cachedPanel = panelSupplier.get();
        }
        return cachedPanel;
    }

    public JComponent peekPanel() {
        return cachedPanel;
    }

    @Override
    public String toString() {
        return name;
    }
}
