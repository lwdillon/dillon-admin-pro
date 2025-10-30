import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.Resources;
import com.dillon.lw.http.RetrofitServiceManager;
import com.dillon.lw.api.ai.AiChatMessageApi;
import com.dillon.lw.api.ai.AiChatMessageSendReqVO;
import com.dillon.lw.api.ai.AiChatMessageSendRespVO;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ChatUI {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;

    public ChatUI() {

        // 创建界面
        frame = new JFrame("AI Chat");
        frame.setLayout(new BorderLayout());

        // 创建显示对话的文本区域
        textArea = new JTextArea(20, 50);
        textArea.setEditable(false); // 使文本区域不可编辑
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 创建输入框和按钮
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        textField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // 设置按钮点击事件
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // 设置窗口属性
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null); // 居中显示
        frame.setVisible(true);
    }

    // 发送消息并监听流式响应
    private void sendMessage() {
        String message = textField.getText().trim();
        if (message.isEmpty()) {
            return; // 空消息不处理
        }


        // 创建请求对象
        AiChatMessageSendReqVO sendReqVO = new AiChatMessageSendReqVO();
        sendReqVO.setContent(message);
        sendReqVO.setUseContext(true);
        sendReqVO.setConversationId(1781604279872581728L);

        // 使用 Retrofit 和 RxJava 调用流式 API
        RetrofitServiceManager.getInstance().create(AiChatMessageApi.class)

                .sendChatMessageStream(sendReqVO).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .doOnSubscribe(disposable -> {
                    // 显示用户的消息
                    textArea.append("User: " + message + "\n");

                    // 清空输入框
                    textField.setText("");

                    // UI 更新：开始请求
                    textField.setEditable(false);

                })
                .doFinally(() -> {
                    // UI 更新：请求结束
                    textField.setEditable(true);
                })
                .subscribe(
                        result -> {
                            // 处理响应结果并更新文本区域
                            if (result != null) {

                                String jstonStr = result.string();
                                // 去掉开头的 "data:" 部分
                                if (jstonStr.startsWith("data:")) {
                                    jstonStr = jstonStr.substring(5); // 去掉前面的 "data:" 5个字符
                                }

                                if (JSONUtil.isTypeJSON(jstonStr)) {
                                    // 如果是 JSON 类型，去掉前面的 "data:" 部分
                                    CommonResult<AiChatMessageSendRespVO> commonResult = JSONUtil.toBean(jstonStr, new TypeReference<CommonResult<AiChatMessageSendRespVO>>() {
                                    }, true);
                                    if (commonResult.isSuccess()) {
                                        AiChatMessageSendRespVO data = commonResult.getData();
                                        textArea.append("AI: " + data.getReceive().getContent() + "\n");
                                    } else {
                                        textArea.append("Error:" + commonResult.getMsg() + "\n");
                                    }
                                }else {
                                    textArea.append("Error:" + jstonStr + "\n");
                                }

                            }
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            // 处理错误
                            textArea.append("Error: " + throwable.getMessage() + "\n");
                        }
                );


    }

    // 模拟获取登录用户的 ID
    private String getLoginUserId() {
        return "12345"; // 替换成实际的用户 ID
    }

    public static void main(String[] args) {
        loadApplicationProperties();
        SwingUtilities.invokeLater(() -> new ChatUI()); // 启动 UI
    }

    private static void loadApplicationProperties() {
        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(Resources.getResourceAsStream("/application.properties"),
                UTF_8)) {
            properties.load(in);
            properties.forEach((key, value) -> System.setProperty(
                    String.valueOf(key),
                    String.valueOf(value)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}