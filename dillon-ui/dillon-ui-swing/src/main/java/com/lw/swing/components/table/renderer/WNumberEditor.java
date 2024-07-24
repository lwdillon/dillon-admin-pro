package com.lw.swing.components.table.renderer;

//import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.EventObject;

public class WNumberEditor extends DefaultCellEditor {

    Class[] argTypes = new Class[]{String.class};
    java.lang.reflect.Constructor constructor;
    Object value;

    public WNumberEditor( JTextField textField) {
        super(textField);

        editorComponent = textField;
        delegate = new EditorDelegate() {
            @Override
            public void setValue(Object value) {
                textField.setText((value != null) ? value.toString() : "");
            }

            @Override
            public Object getCellEditorValue() {
                return textField.getText();
            }

            @Override
            public boolean shouldSelectCell(EventObject anEvent) {
                textField.selectAll();
                return super.shouldSelectCell(anEvent);
            }
        };
        getComponent().setName("Table.editor");

        textField.addActionListener(delegate);
    }


    @Override
    public boolean stopCellEditing() {
        String s = (String) super.getCellEditorValue();
        // Here we are dealing with the case where a user
        // has deleted the string value in a cell, possibly
        // after a failed validation. Return null, so that
        // they have the option to replace the value with
        // null or use escape to restore the original.
        // For Strings, return "" for backward compatibility.
        try {
            if ("".equals(s)) {
                if (constructor.getDeclaringClass() == String.class) {
                    value = s;
                }
                return super.stopCellEditing();
            }

            SwingUtilities2.checkAccess(constructor.getModifiers());
            value = constructor.newInstance(new Object[]{s});
        } catch (Exception e) {
            ((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
            return false;
        }
        return super.stopCellEditing();
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        this.value = null;
        ((JComponent) getComponent()).setBorder(new LineBorder(Color.black));
        try {
            Class<?> type = table.getColumnClass(column);
            // Since our obligation is to produce a value which is
            // assignable for the required type it is OK to use the
            // String constructor for columns which are declared
            // to contain Objects. A String is an Object.
            if (type == Object.class) {
                type = String.class;
            }
//            ReflectUtil.checkPackageAccess(type);
            SwingUtilities2.checkAccess(type.getModifiers());
            constructor = type.getConstructor(argTypes);
        } catch (Exception e) {
            return null;
        }
        Component component=   super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (editorComponent instanceof JTextComponent) {
            ((JTextComponent) editorComponent).selectAll();
        }
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }
}