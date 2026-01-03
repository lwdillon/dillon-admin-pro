package com.dillon;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCompleteField extends JTextField {
    private final JPopupMenu popupMenu;
    private final JList<String> list;
    private final List<String> history;

    public AutoCompleteField(List<String> historyData) {
        this.history = historyData;
        this.popupMenu = new JPopupMenu();
        this.list = new JList<>();
        
        // 设置弹出菜单样式
        popupMenu.setFocusable(false);
        popupMenu.add(new JScrollPane(list));

        // 1. 监听文本变化
        this.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        // 2. 监听列表点击选择
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectFromList();
                }
            }
        });
        // 在构造函数或初始化方法中添加以下键盘监听器
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (!popupMenu.isVisible()) return;

                int keyCode = e.getKeyCode();
                int selectedIndex = list.getSelectedIndex();
                int size = list.getModel().getSize();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_DOWN:
                        // 向下移动选择项
                        if (selectedIndex < size - 1) {
                            list.setSelectedIndex(selectedIndex + 1);
                            list.ensureIndexIsVisible(selectedIndex + 1);
                        }
                        e.consume(); // 阻止光标移动
                        break;

                    case java.awt.event.KeyEvent.VK_UP:
                        // 向上移动选择项
                        if (selectedIndex > 0) {
                            list.setSelectedIndex(selectedIndex - 1);
                            list.ensureIndexIsVisible(selectedIndex - 1);
                        }
                        e.consume(); // 阻止光标移动
                        break;

                    case java.awt.event.KeyEvent.VK_ENTER:
                        // 按回车键确认选择
                        selectFromList();
                        e.consume();
                        break;

                    case java.awt.event.KeyEvent.VK_ESCAPE:
                        // 按 ESC 键关闭弹出层
                        popupMenu.setVisible(false);
                        break;
                }
            }
        });
    }

    private void updateSuggestions() {
        String text = getText();
        if (text.isEmpty()) {
            popupMenu.setVisible(false);
            return;
        }

        // 过滤匹配项 (不区分大小写)
        List<String> suggestions = history.stream()
                .filter(s -> s.toLowerCase().startsWith(text.toLowerCase()))
                .collect(Collectors.toList());

        if (!suggestions.isEmpty()) {
            list.setListData(suggestions.toArray(new String[0]));
            list.setSelectedIndex(0); // 默认选中第一项
            
            // 确保弹出菜单宽度与输入框一致
            popupMenu.setPreferredSize(new Dimension(getWidth(), Math.min(suggestions.size() * 25, 150)));
            popupMenu.show(this, 0, getHeight());
            requestFocusInWindow(); // 保持输入框焦点
        } else {
            popupMenu.setVisible(false);
        }
    }

    private void selectFromList() {
        String selected = list.getSelectedValue();
        if (selected != null) {
            this.setText(selected);
            popupMenu.setVisible(false);
        }
    }

    // 主函数测试
    public static void main(String[] args) {
        JFrame frame = new JFrame("自动补全历史用户");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 模拟历史数据
        List<String> history = new ArrayList<>();
        history.add("admin");
        history.add("alice");
        history.add("bob");
        history.add("apple");
        history.add("administrator");

        AutoCompleteField textField = new AutoCompleteField(history);
        textField.setPreferredSize(new Dimension(200, 30));

        frame.add(new JLabel("用户名: "));
        frame.add(textField);
        frame.pack();
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}