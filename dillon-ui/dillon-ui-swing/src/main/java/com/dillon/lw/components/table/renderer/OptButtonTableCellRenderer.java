package com.dillon.lw.components.table.renderer;

import cn.hutool.core.util.ObjectUtil;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class OptButtonTableCellRenderer extends DefaultTableCellRenderer {
    private JComponent component;

    private JPanel panel = new JPanel();

    private int hideCol = -1;
    private Object hideValue;

    public OptButtonTableCellRenderer(JComponent component) {
        this(component, -1, null);
    }

    public OptButtonTableCellRenderer(JComponent component, int hideCol, Object hideValue) {

        this.hideCol = hideCol;
        this.hideValue = hideValue;
        this.component = component;
        panel.setLayout(new BorderLayout());
        panel.add(component);
        this.component.setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {


        if (component instanceof JXHyperlink) {
            ((JXHyperlink) component).setText(value + "");
        }

        if (hideCol != -1) {
            Object v = table.getValueAt(row, hideCol);
            this.component.setVisible(!ObjectUtil.equal(v, hideValue));
        }
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());
        }
        return panel;
    }


}