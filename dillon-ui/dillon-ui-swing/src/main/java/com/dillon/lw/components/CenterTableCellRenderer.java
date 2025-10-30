package com.dillon.lw.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CenterTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component=super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (component instanceof JLabel) {
            ((JLabel) component).setHorizontalAlignment(JLabel.CENTER);
        }
        return  component;
    }
}
