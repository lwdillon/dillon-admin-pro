import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ColumnHighlightDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Column Highlight Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Object[][] data = {
                    {"1", "John", "Doe"},
                    {"2", "Jane", "Smith"},
                    {"3", "Bob", "Johnson"}
            };

            String[] columns = {"ID", "First Name", "Last Name"};

            DefaultTableModel model = new DefaultTableModel(data, columns);
            JTable table = new JTable(model);

            // 添加选中监听器
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    // 如果是列选择变化
                    if (!e.getValueIsAdjusting() && table.getSelectedColumn() != -1) {
                        int selectedColumn = table.getSelectedColumn();
                        // 渲染器
                        TableCellRenderer renderer = new DefaultTableCellRenderer() {
                            @Override
                            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                                // 如果是选中的列
                                if (column == selectedColumn) {
                                    setBackground(Color.YELLOW);
                                } else {
                                    setBackground(table.getBackground());
                                }
                                return this;
                            }
                        };
                        // 设置渲染器到表格中
                        for (int i = 0; i < table.getColumnCount(); i++) {
                            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
                        }
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            frame.setSize(400, 200);
            frame.setVisible(true);
        });
    }
}
