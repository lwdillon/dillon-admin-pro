package com.dillon;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.List;

public class MarkdownAIChatFull extends JFrame {

    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;

    private SimpleAttributeSet userStyle;
    private SimpleAttributeSet aiStyle;

    private JPanel lastBubblePanel;
    private JTextPane lastTextPane;
    private RSyntaxTextArea lastCodeArea;

    public MarkdownAIChatFull() {
        setTitle("AI Markdown Chat Full Demo");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initStyles();
        initComponents();
    }

    private void initStyles() {
        userStyle = new SimpleAttributeSet();
        StyleConstants.setBackground(userStyle, new Color(220, 248, 198));
        StyleConstants.setForeground(userStyle, Color.BLACK);
        StyleConstants.setFontFamily(userStyle, "Arial");
        StyleConstants.setFontSize(userStyle, 14);

        aiStyle = new SimpleAttributeSet();
        StyleConstants.setBackground(aiStyle, new Color(240, 240, 240));
        StyleConstants.setForeground(aiStyle, Color.BLACK);
        StyleConstants.setFontFamily(aiStyle, "Arial");
        StyleConstants.setFontSize(aiStyle, 14);
    }

    private void initComponents() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;

        addBubble(message, true);
        inputField.setText("");

        // 异步流式显示 AI 消息
        new SwingWorker<Void, Object>() {
            @Override
            protected Void doInBackground() throws Exception {
                String aiReply = generateAIReply(message);
                boolean inCodeBlock = false;
                String[] lines = aiReply.split("\n");

                for (String line : lines) {
                    if (line.startsWith("```")) {
                        inCodeBlock = !inCodeBlock;
                        if (inCodeBlock) publish("CODE_START");
                        else publish("CODE_END");
                        continue;
                    }
                    if (inCodeBlock) {
                        publish(new Object[]{"CODE_LINE", line});
                        Thread.sleep(30); // 模拟流式
                    } else {
                        for (char c : line.toCharArray()) {
                            publish(String.valueOf(c));
                            Thread.sleep(10); // 模拟 token 流
                        }
                        publish("\n");
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Object> chunks) {
                for (Object chunk : chunks) {
                    if ("CODE_START".equals(chunk)) addCodeBubbleStart();
                    else if ("CODE_END".equals(chunk)) addCodeBubbleEnd();
                    else if (chunk instanceof Object[]) {
                        Object[] arr = (Object[]) chunk;
                        if ("CODE_LINE".equals(arr[0])) addCodeLine((String) arr[1]);
                    } else {
                        appendTextToLastBubble((String) chunk);
                    }
                }
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        }.execute();
    }

    private String generateAIReply(String message) {
        if (message.toLowerCase().contains("code")) {
            return "Here is a Java example:\n```java\npublic static void main(String[] args) {\n    System.out.println(\"Hello World\");\n}\n```";
        }
        return "This is **Markdown** reply with *italic* text and a [link](https://example.com)";
    }

    private void addBubble(String text, boolean isUser) {
        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setBorder(new EmptyBorder(5, 5, 5, 5));
        bubble.setBackground(isUser ? new Color(220, 248, 198) : new Color(240, 240, 240));

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(bubble.getBackground());
        textPane.setText(text);

        bubble.add(textPane, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        if (isUser) wrapper.add(bubble, BorderLayout.EAST);
        else wrapper.add(bubble, BorderLayout.WEST);

        chatPanel.add(wrapper);
        chatPanel.add(Box.createVerticalStrut(5));

        chatPanel.revalidate();
        lastBubblePanel = bubble;
        lastTextPane = textPane;
    }

    private void addCodeBubbleStart() {
        lastCodeArea = new RSyntaxTextArea();
        lastCodeArea.setEditable(false);
        lastCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        lastCodeArea.setCodeFoldingEnabled(true);
        lastCodeArea.setBackground(new Color(30, 30, 30));
        lastCodeArea.setForeground(new Color(0, 255, 0));

        RTextScrollPane scroll = new RTextScrollPane(lastCodeArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setBorder(new EmptyBorder(5, 5, 5, 5));
        bubble.add(scroll, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(bubble, BorderLayout.WEST);

        chatPanel.add(wrapper);
        chatPanel.add(Box.createVerticalStrut(5));
        chatPanel.revalidate();
    }

    private void addCodeLine(String line) {
        if (lastCodeArea != null) lastCodeArea.append(line + "\n");
    }

    private void addCodeBubbleEnd() {
        lastCodeArea = null;
    }

    private void appendTextToLastBubble(String text) {
        if (lastTextPane != null) lastTextPane.setText(lastTextPane.getText() + text);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MarkdownAIChatFull demo = new MarkdownAIChatFull();
            demo.setVisible(true);
        });
    }
}