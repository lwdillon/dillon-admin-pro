package com.dillon.lw.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutoCompleteField<T> extends JTextField {
    private final JPopupMenu popupMenu;
    private final JList<T> list;
    private List<T> dataStore = new ArrayList<>();
    private JScrollPane scrollPane;
    // 映射函数：定义如何从 T 中提取显示的字符串
    private Function<T, String> mapper = Object::toString;

    public AutoCompleteField() {
        this.popupMenu = new JPopupMenu();
        this.list = new JList<>();

        // UI 配置
        popupMenu.setFocusable(false);
        popupMenu.add(scrollPane=new JScrollPane(list));

        scrollPane.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
        list.setBorder(null);
        // 1. 监听文本输入 (DocumentListener)
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                updateSuggestions();
            }
        });

        // 2. 键盘交互 (Lambda 简化)
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (popupMenu.isVisible()) {
                    handleKeyEvent(e);
                }
            }
        });

        // 3. 鼠标选择
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                confirmSelection();
            }
        });
    }

    /**
     * 设置映射逻辑，例如: user -> user.getName()
     */
    public void setMapper(Function<T, String> mapper) {
        this.mapper = mapper;
    }

    /**
     * 更新数据源
     */
    public void setDataList(List<T> newData) {
        this.dataStore = (newData != null) ? new ArrayList<>(newData) : new ArrayList<>();
    }

    /**
     * 添加单条历史记录
     */
    public void addRecord(T item) {
        String key = mapper.apply(item);
        // 使用 Java 8 removeIf 过滤重复项
        dataStore.removeIf(i -> mapper.apply(i).equalsIgnoreCase(key));
        dataStore.add(0, item);
    }

    /**
     * 对外暴露：设置列表渲染器（例如：给历史记录加个小图标）
     */
    public void setCellRenderer(ListCellRenderer<? super T> renderer) {
        this.list.setCellRenderer(renderer);
    }

    private void updateSuggestions() {
        String text = getText();
        if (text.isEmpty() || dataStore.isEmpty()) {
            popupMenu.setVisible(false);
            return;
        }

        // Java 8 Stream 过滤
        List<T> matches = dataStore.stream()
                .filter(item -> mapper.apply(item).toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());

        if (!matches.isEmpty()) {
            // 必须转换为数组给 JList
            DefaultListModel<T> model = new DefaultListModel<>();
            matches.forEach(model::addElement);
            list.setModel(model);
            list.setSelectedIndex(0);

            // 展示弹出层
            popupMenu.show(this, 0, getHeight());
            popupMenu.setPopupSize(getWidth(), Math.min(matches.size() * 40 + 5, 200));
            this.requestFocusInWindow();
        } else {
            popupMenu.setVisible(false);
        }
    }

    private void handleKeyEvent(KeyEvent e) {
        int index = list.getSelectedIndex();
        int size = list.getModel().getSize();

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            list.setSelectedIndex((index + 1) % size);
            e.consume();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            list.setSelectedIndex((index - 1 + size) % size);
            e.consume();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            confirmSelection();
            e.consume();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            popupMenu.setVisible(false);
        }
    }

    private void confirmSelection() {
        T selected = list.getSelectedValue();
        if (selected != null) {
            this.setText(mapper.apply(selected));
            popupMenu.setVisible(false);
        }
    }

    public T getSelectedValue() {
        return list.getSelectedValue();
    }

    public JList<T> getList() {
        return list;
    }

    // --- 使用演示 ---
    static class User {
        String name;

        User(String name) {
            this.name = name;
        }
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Java 8 AutoComplete");
//        AutoCompleteField<User> field = new AutoCompleteField<>();
//
//        // 配置：告诉组件通过 User 的 name 属性搜索
//        field.setMapper(user -> user.name);
//
//        // 模拟数据
//        List<User> users = new ArrayList<>();
//        users.add(new User("Administrator"));
//        users.add(new User("Alice"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        users.add(new User("Bob"));
//        field.setDataList(users);
//
//        field.setPreferredSize(new Dimension(200, 30));
//        frame.setLayout(new FlowLayout());
//        frame.add(new JLabel("Login:"));
//        frame.add(field);
//        frame.pack();
//        frame.setVisible(true);
//    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (list != null) {
            list.setBackground(bg);
            popupMenu.setBackground(bg);
            scrollPane.setBackground(bg);
        }

    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (list != null) {
            list.setForeground(fg);
        }

    }
}