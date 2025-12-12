package com.dillon;

import com.formdev.flatlaf.FlatLightLaf;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SwingAIChatDemo {

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup(); // 设置 FlatLaf 主题
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(SwingAIChatDemo::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("AI 聊天窗口 Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        // 左侧 Markdown 渲染区
        JEditorPane chatPane = new JEditorPane();
        chatPane.setContentType("text/html");
        chatPane.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatPane);

        // 右侧代码编辑区
        RSyntaxTextArea codeArea = new RSyntaxTextArea();
        codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeArea.setCodeFoldingEnabled(true);
        RTextScrollPane codeScroll = new RTextScrollPane(codeArea);

        // 分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, codeScroll);
        splitPane.setDividerLocation(500);

        // 底部输入区
        JTextField inputField = new JTextField();
        JButton sendBtn = new JButton("发送");
        JButton copyCodeBtn = new JButton("复制代码块到右侧");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.add(sendBtn);
        btnPanel.add(copyCodeBtn);
        bottomPanel.add(btnPanel, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Flexmark Markdown 解析器
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        // 发送按钮事件
        sendBtn.addActionListener((ActionEvent e) -> {
            String markdown = inputField.getText();
            if (markdown.isEmpty()) return;

            Node document = parser.parse(markdown);
            String html = renderer.render(document);

            // 追加消息
            String current = chatPane.getText();
            chatPane.setText(current + "<hr>" + html);
            inputField.setText("");
            chatPane.setCaretPosition(chatPane.getDocument().getLength());
        });

        // 复制代码块按钮事件
        copyCodeBtn.addActionListener((ActionEvent e) -> {
            String markdown = inputField.getText();
            if (markdown.isEmpty()) return;

            // 简单提取 ``` 代码块内容
            String[] parts = markdown.split("```");
            if (parts.length >= 2) {
                codeArea.setText(parts[1]); // 默认取第一个代码块
            }
        });

        frame.setVisible(true);
    }
}