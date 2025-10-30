import javax.swing.*;

public class RxJaveTest {
    public static void main(String[] args) throws InterruptedException {


        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("RxJava Swing Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 400);
            frame.setContentPane(new LoginPane());
            frame.setVisible(true);
        });


    }


}
