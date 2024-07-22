package com.lw.swing.components.table;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liwen
 */
public abstract class MultiCellSelectionTable extends WTable {

    protected ListSelectionModel selectionModel = new DefaultListSelectionModel();

    public MultiCellSelectionTable() {
        super();
    }

    public MultiCellSelectionTable(TableModel dm) {
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
        return getColumnCount() * rowIndex + colIndex;
    }

    @Override
    public int[] getSelectedColumns() {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {

                if (isCellSelected(i, j)) {
                    list.add(j);
                }
            }
        }
        int[] col = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            col[i] = list.get(i);
        }

        return col;
    }


    public int[][] getSelectedCells() {
        List<Integer[]> list = new ArrayList<Integer[]>();
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {

                if (isCellSelected(i, j)) {
                    Integer[] cell = new Integer[2];
                    cell[0] = i;
                    cell[1] = j;
                    list.add(cell);
                }
            }
        }
        int[][] sel = new int[list.size()][2];

        for (int i = 0; i < list.size(); i++) {
            Integer[] cell = list.get(i);
            sel[i][0] = cell[0];
            sel[i][1] = cell[1];

        }

        return sel;
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