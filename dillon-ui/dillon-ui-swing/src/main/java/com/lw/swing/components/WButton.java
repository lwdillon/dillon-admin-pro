package com.lw.swing.components;


import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by liwen on 2017/5/6.
 */
public class WButton extends JButton {





    /**
     * 直角&圆角 true为直角，flase为圆角
     */
    private boolean corner = false;

    private float alpha = 0f;

    private Animator animationStart;

    /**
     * 是否填充
     */
    private boolean fill = false;

    /**
     * 是否绘制边框
     */
    private boolean drawBorder = true;

    public WButton() {
        this(null, null);
    }

    public WButton(Icon icon) {
        this(null,icon);
    }

    public WButton(String text) {
        this(text,null);
    }

    public WButton(Action a) {
        super(a);
    }

    public WButton(String text, Icon icon) {
        super(text, icon);
        initSwing();
        initListener();
    }

    private void initSwing() {
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void initListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    getAnimationStart().restart();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    getAnimationStart().restartReverse();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }
        });
    }





    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }


    public boolean isCorner() {
        return corner;
    }

    public void setCorner(boolean corner) {
        this.corner = corner;
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth()-2;
        int h = getHeight()-2 ;
        int x = 1;
        int y = 1;

        if (!isDrawBorder()) {
            w = getWidth();
            h = getHeight();
            x = 0;
            y = 0;
        }

        g2.setColor(getBackground());
        if (isCorner()) {//直角
            if (isFill()) {//填充
                g2.fillRect(x, y, w, h);
            } else {
                if (isDrawBorder()) {//绘制边框
                    g2.drawRect(x, y, w, h);
                }
            }

        } else {// 圆角
            if (isFill()) {//填充
                g2.fillRoundRect(x, y, w, h, 5, 5);
            } else {
                if (isDrawBorder()) {//绘制边框
                    g2.drawRoundRect(x, y, w, h, 5, 5);
                }
            }
        }

        if (alpha > 0.0000001f) {
            g2.setColor(getBackground());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER).derive(alpha));
            if (!isCorner()) {
                g2.fillRoundRect(x, y, w, h, 5, 5);
            } else {
                g2.fillRect(x, y, w, h);
            }
        }
        g2.dispose();

        super.paintComponent(g);

    }

      private Animator getAnimationStart() {
        if (animationStart == null) {
            animationStart = new Animator.Builder().setDuration(300, MILLISECONDS).setInterpolator(new AccelerationInterpolator(.5, .5)).build();
            animationStart.addTarget(PropertySetter.getTarget(WButton.this, "alpha", 0f, 1f));
        }
        return animationStart;
    }


    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }
}
