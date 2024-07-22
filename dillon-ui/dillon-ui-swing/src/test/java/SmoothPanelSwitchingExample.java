import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SmoothPanelSwitchingExample {
    private JFrame frame;
    private JPanel panelContainer;
    private CardLayout cardLayout;
    private JPanel[] panels;
    private Timer timer;
    private final int SWITCH_DELAY = 1000; // 切换延迟（单位：毫秒）
    private final int ANIMATION_DURATION = 500; // 动画持续时间（单位：毫秒）
    private final int PANEL_WIDTH = 300; // 面板宽度
    private final int PANEL_HEIGHT = 200; // 面板高度

    public SmoothPanelSwitchingExample() {
        frame = new JFrame("Smooth Panel Switching Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建面板数组
        panels = new JPanel[2];
        for (int i = 0; i < panels.length; i++) {
            panels[i] = new JPanel();
            panels[i].setBackground(getRandomColor());
            panels[i].setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        }

        // 创建面板容器，并使用CardLayout
        panelContainer = new JPanel();
        cardLayout = new CardLayout();
        panelContainer.setLayout(cardLayout);
        panelContainer.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        for (int i = 0; i < panels.length; i++) {
            panelContainer.add(panels[i], "panel" + i);
        }

        // 创建计时器
        timer = new Timer(SWITCH_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels();
            }
        });
    }

    public void startTimer() {
        timer.start();
    }

    private void switchPanels() {
        cardLayout.next(panelContainer);
    }

    private Color getRandomColor() {
        // 生成随机颜色
        return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }

    public void showFrame() {
        frame.add(panelContainer);
        frame.pack(); // 根据面板容器的大小自动调整窗口大小
        frame.setLocationRelativeTo(null); // 窗口居中显示
        frame.setVisible(true);
        startTimer(); // 开始计时器
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SmoothPanelSwitchingExample example = new SmoothPanelSwitchingExample();
                example.showFrame();
            }
        });
    }


}
