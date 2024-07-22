package com.lw.swing.components.table;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * @author liwen
 */
public class Multi12CellSelectionTable extends WTable {

    protected ListSelectionModel selectionModel = new DefaultListSelectionModel();

    public Multi12CellSelectionTable() {
        this(null);

    }

    public Multi12CellSelectionTable(TableModel dm) {
        super(dm);


    }


    @Override
    public boolean isCellSelected(int row, int column) {
        return selectionModel.isSelectedIndex(getIndex(row, column));
    }


    @Override
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {

        boolean selected = isCellSelected(rowIndex, columnIndex);
        changeSelectionModel(selectionModel, rowIndex, columnIndex, toggle, extend, selected);

        if (getAutoscrolls()) {
            Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
            if (cellRect != null) {
                scrollRectToVisible(cellRect);
            }
        }
    }

    private int getIndex(int rowIndex, int colIndex) {

        int index = 0;
        if (rowIndex < 4) {
            index = rowIndex + colIndex * 4;
        } else {
            index = rowIndex + colIndex * 4 + 52;
        }
        return index;
    }


    private void changeSelectionModel(ListSelectionModel sm, int rowIndex, int colIndex, boolean toggle, boolean extend, boolean selected) {
        int index = getIndex(rowIndex, colIndex);

        // use customed selection model to save selection status.
        // at the same time, update the original selection model JTable used.
        ListSelectionModel rsm = this.getSelectionModel();
        ListSelectionModel csm = this.getColumnModel().getSelectionModel();
        if (extend) {
            if (toggle) {
                sm.setAnchorSelectionIndex(index);
                rsm.setAnchorSelectionIndex(rowIndex);
                csm.setAnchorSelectionIndex(colIndex);
            } else {
                sm.setLeadSelectionIndex(index);
                rsm.setLeadSelectionIndex(rowIndex);
                csm.setLeadSelectionIndex(colIndex);
            }
        } else {
            if (toggle) {
                if (selected) {
                    sm.removeSelectionInterval(index, index);
                    rsm.removeSelectionInterval(rowIndex, rowIndex);
                    csm.removeSelectionInterval(colIndex, colIndex);
                } else {
                    sm.addSelectionInterval(index, index);
                    rsm.addSelectionInterval(rowIndex, rowIndex);
                    csm.addSelectionInterval(colIndex, colIndex);
                }
            } else {
                sm.setSelectionInterval(index, index);
                rsm.setSelectionInterval(rowIndex, rowIndex);
                csm.setSelectionInterval(colIndex, colIndex);
                sm.setAnchorSelectionIndex(index);
                rsm.setAnchorSelectionIndex(rowIndex);
                csm.setAnchorSelectionIndex(colIndex);
            }
        }
        repaint();
    }

    @Override
    public void selectAll() {
        // If I'm currently editing, then I should stop editing
        if (isEditing()) {
            removeEditor();
        }
        if (getRowCount() > 0 && getColumnCount() > 0) {

            for (int i = 0; i < getRowCount(); i++) {
                for (int j = 0; j < getColumnCount(); j++) {

                    changeSelectionModel(selectionModel, i, j, true, false, false);
                }
            }
        }
    }



}