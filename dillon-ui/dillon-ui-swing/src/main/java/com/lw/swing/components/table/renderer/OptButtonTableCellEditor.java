package com.lw.swing.components.table.renderer;

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

    public OptButtonTableCellEditor(JComponent editor) {
        this(editor, -1, null);
    }

    public OptButtonTableCellEditor(JComponent editor, int hideCol, Object hideValue) {
        super(editor);
        this.hideCol = hideCol;
        this.hideValue = hideValue;
        if (editor instanceof JButton) {
            ((JButton) editor).addActionListener(this);
        } else if (editor instanceof JCheckBox) {
            ((JCheckBox) editor).addActionListener(this);
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
                    ((JButton) editor).doClick();
                }
            });
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        editor.setBackground(table.getSelectionBackground());
        this.setBackground(table.getSelectionBackground());
        this.value = value;
        if (editor instanceof JXHyperlink) {
            ((JXHyperlink) editor).setText(value + "");
        }
        if (hideCol !=  -1) {
            Object v = table.getValueAt(row, hideCol);
            editor.setVisible(!ObjectUtil.equal(v, hideValue));
        }

        return this;
    }


    @Override
    public void actionPerformed(ActionEvent evt) {
        // Button pressed - stop the edit
        stopCellEditing();

        if (editor instanceof JCheckBox) {
            this.value = ((JCheckBox) editor).isSelected();

        }

    }

    protected Object value;


}