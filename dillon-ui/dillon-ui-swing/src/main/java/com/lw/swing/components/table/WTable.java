package com.lw.swing.components.table;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTextField;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WTable extends JTable {
    private static final Type DATA_TYPE = new TypeToken<List<Cell>>() {
    }.getType();
    public WTable() {
        this(null);
    }

    public WTable(TableModel dm) {
        super(dm);
        this.setComponentPopupMenu(new WTablePopupMenu(this));

    }


    protected void copyStuff() {

        List<Cell> cellList = getSelectCellIndex();
        int size = cellList.size();
        if (size < 1) {
            return;
        }

        Object indexValue = cellList.get(0).getValue();
        for (int i = 1; i < size; i++) {
            Cell cell = cellList.get(i);
            setValueAt(indexValue, cell.getRow(), cell.getCol());
        }
    }

    protected void increaseStuff() {
    }

    protected void linearIncreaseStuff() {
    }

    protected void copy() {

        List<Cell> cellList = getSelectCellIndex();
        int size = cellList.size();
        if (size < 1) {
            return;
        }

        String dataJson = new Gson().toJson(cellList, DATA_TYPE);

        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(dataJson);
        clip.setContents(tText, null);
    }

    protected void paste() {

        if (getSelectedRow() < 0) {
            return;
        }


        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        List<Cell> dataJson = new Gson().fromJson(ret, DATA_TYPE);

        int index = 0;
        for (int r = getSelectedRow(); r < getRowCount(); r++) {

            for (int c = getSelectedColumn(); c < getColumnCount(); c++) {

                if (index < dataJson.size() && isCellEditable(r, c)) {

                    Cell cell = dataJson.get(index);
                    setValueAt(cell.getValue(), r, c);
                }
                index++;
            }
        }
    }

    protected void fixedValueStuff(Double fv) {



        List<Cell> cellList = getSelectCellIndex();
        int size = cellList.size();
        if (size < 1) {
            return;
        }
        try {
            for (int i = 0; i < size; i++) {
                Cell cell = cellList.get(i);
                Object cellValue = cell.getValue();
                Double value = cellValue instanceof Double ? (Double) cellValue : Double.parseDouble(cellValue.toString());

                setValueAt(value + fv, cell.getRow(), cell.getCol());
            }

        } catch (
                NumberFormatException e) {
            e.printStackTrace();
        }
    }

    protected void fixedRatioStuff(Double fv) {


        List<Cell> cellList = getSelectCellIndex();
        int size = cellList.size();
        if (size < 1) {
            return;
        }
        try {
            for (int i = 0; i < size; i++) {
                Cell cell = cellList.get(i);
                Object cellValue = cell.getValue();

                Double value = cellValue instanceof Double ? (Double) cellValue : Double.parseDouble(cellValue.toString());
                Double newValue = value + value * fv / 100.0;
                setValueAt(newValue, cell.getRow(), cell.getCol());
            }

        } catch (
                NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public List<Cell> getSelectCellIndex() {

        List<Cell> cellList = new ArrayList<>();

        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColumnCount(); c++) {
                if (isCellEditable(r, c) && isCellSelected(r, c)) {
                    cellList.add(new Cell(r, c, getValueAt(r, c)));
                }
            }
        }
        return cellList;
    }


    class WTablePopupMenu extends JPopupMenu implements ActionListener {


        private JMenuItem copyStuffMenuItem;
        private JMenuItem cellIncMenuItem;
        private JMenuItem linearIncMenuItem;
        private JMenuItem copyMenuItem;
        private JMenuItem pasteMenuItem;
        private JButton fixedValueButton;
        private JButton fixedRatioButton;
        private JXTextField fixedValueTextField;
        private JXTextField fixedRatioTextField;
        private JTable table;

        public WTablePopupMenu(JTable table) {

            this.table = table;
            this.add(copyMenuItem = createMenuItem("复制", new FlatSVGIcon("icons/fuzhi.svg", 16, 16)));
            this.add(pasteMenuItem = createMenuItem("粘贴", new FlatSVGIcon("icons/niantie.svg", 16, 16)));
            this.addSeparator();
            this.add(copyStuffMenuItem = createMenuItem("拷贝填充", new FlatSVGIcon("icons/tianchong.svg", 16, 16)));
            this.add(cellIncMenuItem = createMenuItem("递增填充", new FlatSVGIcon("icons/shengxu.svg", 16, 16)));
            this.add(linearIncMenuItem = createMenuItem("线性填充", new FlatSVGIcon("icons/xianxing.svg", 16, 16)));
            this.addSeparator();

            JLabel label = new JLabel("固定数值填充");
            label.setEnabled(false);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(fixedValueTextField = new JXTextField("固定数值填充"));
            panel.add(fixedValueButton = new JButton(), BorderLayout.EAST);
            panel.add(label, BorderLayout.NORTH);
            panel.setOpaque(false);
            this.add(panel);
            fixedValueButton.addActionListener(this);
            fixedValueButton.setIcon(new FlatSVGIcon("icons/queding.svg", 16, 16));
            this.addSeparator();

            JLabel label1 = new JLabel("固定比例填充");
            label1.setEnabled(false);
            JPanel panel1 = new JPanel(new BorderLayout());
            panel1.setOpaque(false);
            panel1.add(fixedRatioTextField = new JXTextField("固定比例填充"));
            panel1.add(fixedRatioButton = new JButton(), BorderLayout.EAST);
            panel1.add(label1, BorderLayout.NORTH);
            this.add(panel1);
            fixedRatioButton.addActionListener(this);
            fixedRatioButton.setIcon(new FlatSVGIcon("icons/queding.svg", 16, 16));
            this.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        }

        private JMenuItem createMenuItem(String item, Icon icon) {
            JMenuItem menuItem = new JMenuItem(item, icon);
            menuItem.addActionListener(this);
            return menuItem;
        }


        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == copyStuffMenuItem) {
                copyStuff();
            } else if (e.getSource() == cellIncMenuItem) {
                increaseStuff();
            } else if (e.getSource() == linearIncMenuItem) {
                linearIncreaseStuff();
            } else if (e.getSource() == copyMenuItem) {
                copy();
            } else if (e.getSource() == pasteMenuItem) {
                paste();
            } else if (e.getSource() == fixedValueButton) {
                Double fv = StringUtils.isEmpty(fixedValueTextField.getText()) ? 0 : Double.parseDouble(fixedValueTextField.getText());
                fixedValueStuff(fv);
            } else if (e.getSource() == fixedRatioButton) {
                Double fv = StringUtils.isEmpty(fixedRatioTextField.getText()) ? 0 : Double.parseDouble(fixedRatioTextField.getText());
                fixedRatioStuff(fv);
            }

        }


    }

}
