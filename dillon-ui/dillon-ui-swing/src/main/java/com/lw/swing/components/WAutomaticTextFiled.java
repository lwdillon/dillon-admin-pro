package com.lw.swing.components;

import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.json.JSONObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wenli
 * @date 2024/05/09
 */
public class WAutomaticTextFiled extends JPanel {

    private JComboBox<JSONObject> comboBox;
    private JTextField textField;
    private DefaultComboBoxModel model;
    private List<Object> sourceData = new ArrayList<>();

    public WAutomaticTextFiled() {
        this("");
    }

    public WAutomaticTextFiled(String text) {
        textField = new JTextField(text);
        createFeatureSearchComboBox();

        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.add(textField, BorderLayout.CENTER);
        this.add(comboBox, BorderLayout.SOUTH);
    }

    public JTextField getTextField() {
        return textField;
    }

    public JComboBox getComboBox() {
        return comboBox;
    }

    private boolean isAdjusting(JComboBox comboBox) {
        if (comboBox.getClientProperty("is_adjusting") instanceof Boolean) {
            return (Boolean) comboBox.getClientProperty("is_adjusting");
        }
        return false;
    }

    private void setAdjusting(JComboBox comboBox, boolean adjusting) {
        comboBox.putClientProperty("is_adjusting", adjusting);
    }

    public void createFeatureSearchComboBox() {
        comboBox = new JComboBox(model = new DefaultComboBoxModel()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };
        comboBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    this.setText(value.toString());
                    this.setPreferredSize(new Dimension(this.getWidth(), 40));
                }
                return this;
            }
        });

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdjusting(comboBox)) {
                    Object obj = comboBox.getSelectedItem();
                    if (obj != null && obj instanceof JSONObject) {
                        textField.setText(((JSONObject) obj).getStr("username"));
                    }
                }
            }
        });
        textField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                setAdjusting(comboBox, true);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (comboBox.isPopupVisible()) {
                        e.setKeyCode(KeyEvent.VK_ENTER);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.setSource(comboBox);
                    comboBox.dispatchEvent(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        Object obj = comboBox.getSelectedItem();
                        if (obj != null && obj instanceof JSONObject) {
                            textField.setText(((JSONObject) obj).getStr("username"));
                        }
                        comboBox.setPopupVisible(false);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    comboBox.setPopupVisible(false);
                }
                setAdjusting(comboBox, false);
            }
        });
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateList();
            }

            private void updateList() {
                setAdjusting(comboBox, true);
                model.removeAllElements();
                String input = textField.getText();
                if (!input.isEmpty()) {
                    for (Object item : sourceData) {
                        String pinyingName = PinyinUtil.getPinyin(item.toString()).toLowerCase();
                        String inputText = input.toLowerCase();
                        if (item.toString().toLowerCase().contains(inputText) || pinyingName.contains(inputText)) {
                            model.addElement(item);
                        }
                    }
                }
                comboBox.setPopupVisible(model.getSize() > 0);
                setAdjusting(comboBox, false);
            }
        });

    }

    public List<Object> getSourceData() {
        return sourceData;
    }

    public void setSourceData(List<Object> sourceData) {
        this.sourceData = sourceData;
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String t) {
        textField.setText(t);
    }
}
