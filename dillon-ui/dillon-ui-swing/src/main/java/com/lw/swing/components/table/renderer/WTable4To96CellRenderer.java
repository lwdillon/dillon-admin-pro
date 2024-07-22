package com.lw.swing.components.table.renderer;

import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class WTable4To96CellRenderer extends DefaultTableCellRenderer {
    public final static String[] ROW_HEADER_VALUE = {"00:15-04:00", "04:15-08:00", "08:15-12:00", "12:15-16:00", "16:15-20:00", "20:15-00:00"};


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Object newValue = value;
        if (column == 0) {
            newValue = ROW_HEADER_VALUE[row];
        }
        JComponent component = (JComponent) super.getTableCellRendererComponent(table, newValue, isSelected, hasFocus, row, column);
        List<Integer> list = Arrays.asList(ArrayUtils.toObject(table.getSelectedRows()));

        if (column == 0) {


            if (list.contains(row)) {
                if (isSelected) {
                    component.setBackground(UIManager.getColor("TableHeader.background"));
                } else {
                    component.setBackground(UIManager.getColor("TableHeader.background").brighter());
                }
            } else {
                component.setBackground(UIManager.getColor("TableHeader.background"));
                component.setForeground(table.getTableHeader().getForeground());
            }

        } else {
            if (list.contains(row)) {
                if (isSelected) {
                    component.setBackground(table.getSelectionBackground());
                } else {
                    component.setBackground(new Color(0x6F719FF3, true));
                }
            } else {
                component.setBackground(new Color(0xffffff));
            }
        }
        return component;
    }

    @Override
    public int getHorizontalAlignment() {
        return JLabel.CENTER;
    }
}
