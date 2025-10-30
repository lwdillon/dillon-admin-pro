package com.dillon.lw.components.table.renderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class WTable12To96CellRenderer extends DefaultTableCellRenderer {
    private String[] HOUR = {"15分", "30分", "45分", "00分"};

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Object newValue = value;
        if (row == 4) {
            if (column == 0) {
                newValue = "时/分";
            } else {
                newValue = (column + 11) + "时";
            }
        } else if (column == 0) {
            if (row < 4) {
                newValue = HOUR[row];
            } else {
                newValue = HOUR[row - 5];
            }
        }
        JComponent component = (JComponent) super.getTableCellRendererComponent(table, newValue, isSelected, hasFocus, row, column);

        if (row == 4 || column == 0) {
            component.setBackground(UIManager.getColor("TableHeader.background"));
            component.setForeground(table.getTableHeader().getForeground());
        } else {
            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
            } else {
                component.setBackground(table.getBackground());
            }
        }
        return component;
    }

    @Override
    public int getHorizontalAlignment() {
        return JLabel.CENTER;
    }
}
