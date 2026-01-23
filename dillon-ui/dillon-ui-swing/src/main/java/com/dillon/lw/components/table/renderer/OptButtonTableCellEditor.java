package com.dillon.lw.components.table.renderer;

import cn.hutool.core.util.ObjectUtil;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class OptButtonTableCellEditor extends BasicCellEditor implements ActionListener, TableCellEditor {

    private int hideCol = -1;
    private Object hideValue;
    private JPanel panel = new JPanel();
    private JComponent component;
    public OptButtonTableCellEditor(JComponent component) {
        this(component, -1, null);
    }

    public OptButtonTableCellEditor(JComponent component, int hideCol, Object hideValue) {
        this.hideCol = hideCol;
        this.hideValue = hideValue;
        this.component = component;
        this.component.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.add(component);
        if (component instanceof JButton) {
            ((JButton) component).addActionListener(this);
        } else if (component instanceof JCheckBox) {
            ((JCheckBox) component).addActionListener(this);
        }

    }

    @Override
    public Object getCellEditorValue() {


        return value;
    }

    @Override
    public void editingStarted(EventObject event) {
        // Edit starting - click the button if necessary
        if (!(event instanceof MouseEvent)) {
            // Keyboard event - click the button
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((JButton) component).doClick();
                }
            });
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        this.value = value;
        if (component instanceof JXHyperlink) {
            ((JXHyperlink) component).setText(value + "");
        }
        if (hideCol != -1) {
            Object v = table.getValueAt(row, hideCol);
            component.setVisible(!ObjectUtil.equal(v, hideValue));
        }
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());

        }

        return panel;
    }


    @Override
    public void actionPerformed(ActionEvent evt) {
        // Button pressed - stop the edit
        stopCellEditing();

        if (component instanceof JCheckBox) {
            this.value = ((JCheckBox) component).isSelected();

        }

    }

    protected Object value;


}