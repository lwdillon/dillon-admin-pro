

package com.dillon.lw.components;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.StackLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @Description:
 * @param:
 * @return:
 * @auther: liwen
 * @date: 2021/7/19 11:11 上午
 */
public class WaitPane extends JPanel {
    // dictates sizing, scrolling
    private Component master;
    private JComponent messageLayer;
    private JXBusyLabel busyLabel;
    private final static float DEFAULT_ALPHA = 0.4f;
    private float alpha = DEFAULT_ALPHA;

    public WaitPane(Component master) {
        this.master = master;
        this.setLayout(new StackLayout());
        this.setOpaque(false);

        add(master, StackLayout.BOTTOM);
        add(getMessageLayer(), StackLayout.TOP);
    }


    public JXBusyLabel getBusyLabel() {
        if (busyLabel == null) {
            busyLabel = new JXBusyLabel();
            busyLabel.setOpaque(false);
            busyLabel.setForeground(Color.WHITE);

        }
        return busyLabel;
    }

    public JComponent getMessageLayer() {
        if (messageLayer == null) {
            messageLayer = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0, 0, 0, alpha));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


                    g2.setColor(UIManager.getColor("ColorPalette.waitBg") == null ? new Color(0, 0, 0, 0.3f) : UIManager.getColor("ColorPalette.waitBg"));
                    int w = getBusyLabel().getWidth() + 30;
                    int h = 60;
                    g2.fillRoundRect((getWidth() - w) / 2, (getHeight() - h) / 2, w, h, 15, 15);
                    g2.dispose();
                }
            };
            messageLayer.setVisible(false);

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.CENTER;

            messageLayer.setLayout(gridbag);
            messageLayer.add(getBusyLabel(), c);

            messageLayer.addMouseListener(new MouseAdapter() {
            });
            messageLayer.addMouseMotionListener(new MouseMotionAdapter() {
            });
            messageLayer.addKeyListener(new KeyAdapter() {
            });
            messageLayer.setFocusTraversalKeysEnabled(false);
            messageLayer.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent evt) {
                    requestFocusInWindow();
                }
            });

        }
        return messageLayer;
    }

    /**
     * Fades in the specified message component in the top layer of this
     * layered pane.
     *
     * @param message    the component to be displayed in the message layer
     * @param finalAlpha the alpha value of the component when fade in is complete
     */
    public void showMessageLayer(String message, final float finalAlpha) {

        getMessageLayer().setVisible(true);
        setAlpha(finalAlpha);
        getBusyLabel().setBusy(true);
        getBusyLabel().setText(message);
    }

    public void showMessageLayer(String message) {

        showMessageLayer(message, DEFAULT_ALPHA);

    }

    public void showMessageLayer() {

        showMessageLayer("");

    }

    /**
     * Fades out and removes the current message component
     */
    public void hideMessageLayer() {
        if (getMessageLayer().isVisible()) {
            getMessageLayer().setVisible(false);
            getBusyLabel().setBusy(false);
        }
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }
}
