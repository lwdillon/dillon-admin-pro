package com.dillon.lw.components.table;

import javax.swing.table.DefaultTableModel;

/**
 * 自定义表格模型，默认不可编辑，但可设置指定列（如操作列）可编辑
 *
 * @author wenli
 */
public class WTableModel extends DefaultTableModel {

    private int editableColumn = -1;

    public WTableModel() {
        super();
    }

    /**
     * 设置可编辑的列索引（通常是最后的操作列）
     * @param column 列索引，-1表示没有可编辑列
     */
    public void setEditableColumn(int column) {
        this.editableColumn = column;
    }

    /**
     * 获取可编辑列索引
     */
    public int getEditableColumn() {
        return editableColumn;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // 只有操作列可编辑（用于按钮点击）
        return column == editableColumn;
    }
}
