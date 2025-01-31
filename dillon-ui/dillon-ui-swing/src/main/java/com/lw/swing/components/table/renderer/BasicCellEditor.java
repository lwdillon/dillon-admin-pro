package com.lw.swing.components.table.renderer;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;

public class BasicCellEditor extends JPanel implements CellEditor, PropertyChangeListener {
    public BasicCellEditor() {
        this.editor = null;
    }

    public BasicCellEditor(JComponent editor) {
        this.editor = editor;
        this.setLayout(new BorderLayout());
        this.add(editor);
//    editor.addPropertyChangeListener(this);
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override

    public boolean isCellEditable(EventObject evt) {
        editingEvent = evt;
        return true;
    }

    @Override

    public boolean shouldSelectCell(EventObject evt) {
        return true;
    }

    @Override

    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

    @Override

    public void cancelCellEditing() {
        fireEditingCanceled();
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listeners.add(CellEditorListener.class, l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listeners.remove(CellEditorListener.class, l);
    }

    // Returns the editing component
    public JComponent getComponent() {
        return editor;
    }

    // Sets the editing component
    public void setComponent(JComponent comp) {
        editor = comp;
    }

    // Returns the event that triggered the edit
    public EventObject getEditingEvent() {
        return editingEvent;
    }

    // Method invoked when the editor is installed in the table.
    // Overridden in derived classes to take any convenient
    // action.
    public void editingStarted(EventObject event) {
    }

    protected void fireEditingStopped() {
        Object[] l = listeners.getListenerList();
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == CellEditorListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener) l[i + 1]).editingStopped(changeEvent);
            }
        }
    }

    protected void fireEditingCanceled() {
        Object[] l = listeners.getListenerList();
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == CellEditorListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener) l[i + 1]).editingCanceled(changeEvent);
            }
        }
    }

    // Implementation of the PropertyChangeListener interface
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("ancestor".equals(evt.getPropertyName())
                && evt.getNewValue() != null) {
            // Added to table - notify the editor
            editingStarted(editingEvent);
        }
    }

    protected static JCheckBox checkBox = new JCheckBox();

    protected static ChangeEvent changeEvent;

    protected JComponent editor;

    protected EventListenerList listeners = new EventListenerList();

    protected EventObject editingEvent;
}