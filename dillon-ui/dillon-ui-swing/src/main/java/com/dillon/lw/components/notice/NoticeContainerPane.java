package com.dillon.lw.components.notice;

import com.dillon.lw.components.CenterLayout;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @version： 0.0.1
 * @description:
 * @className: NoticeContainerPane
 * @author: liwen
 * @date: 2021/11/4 10:40
 */
public class NoticeContainerPane extends JPanel {


    /**
     * Message 距离窗口顶部的偏移量
     */
    private int offset = 60;

    private long duration = 300;

    private CenterLayout centerLayout;


    public NoticeContainerPane() {

        this.setLayout(centerLayout = new CenterLayout(20, offset));
        this.setOpaque(false);

    }

    private Animator.Builder getAnimatorBuilder() {
        if (Animator.getDefaultTimingSource() == null) {
            TimingSource ts = new SwingTimerTimingSource();
            Animator.setDefaultTimingSource(ts);
            ts.init();
        }
        return new Animator.Builder();
    }

    public void addMessage(WMessagePane messagePane) {
        add(messagePane);
        messagePane.getCloseButton().addActionListener(e -> {
            del(messagePane);
        });
        if (messagePane.getDuration() != 0) {
            Animator animator = getAnimatorBuilder().setDuration(messagePane.getDuration(), TimeUnit.MILLISECONDS).build();
            animator.addTarget(new TimingTargetAdapter() {
                @Override
                public void end(Animator source) {
                    del(messagePane);
                }
            });
            animator.start();
        }
        revalidate();
        repaint();
        start(messagePane);
    }

    public void del(WMessagePane messagePane) {

        Point startP = new Point(messagePane.getX(), messagePane.getY());
        Point endP = new Point(messagePane.getX(), messagePane.getY() - 60);

        int index = 111110;
        for (int i = 0, c = getComponentCount(); i < c; i++) {
            WMessagePane m = (WMessagePane) getComponent(i);


            if (m == messagePane) {
                index = i;
            }
            if (m.isVisible() && i > index) {

                WMessagePane m1 = (WMessagePane) getComponent(i - 1);
                Point sp = new Point(m.getX(), m.getY());
                Point ep = new Point(m1.getX(), m1.getY());
                Animator animator = getAnimatorBuilder().setDuration(duration, TimeUnit.MILLISECONDS).build();
                animator.addTarget(PropertySetter.getTarget(m, "location", new AccelerationInterpolator(0.5, 0.5), sp, ep));
                animator.start();
            }
        }

        Animator animator = getAnimatorBuilder().setDuration(duration, TimeUnit.MILLISECONDS).build();
        animator.addTarget(PropertySetter.getTarget(messagePane, "alpha", new AccelerationInterpolator(0.5, 0.5), 1f, 0f));
        animator.addTarget(PropertySetter.getTarget(messagePane, "location", new AccelerationInterpolator(0.5, 0.5), startP, endP));
        animator.addTarget(new TimingTargetAdapter() {
            @Override
            public void end(Animator source) {

                remove(messagePane);
                repaint();
            }
        });


        animator.start();

    }

    public void start(WMessagePane messagePane) {

        Insets insets = getInsets();
        Dimension size = getParent().getSize();
        if (size.width <= 0) {
            size=Toolkit.getDefaultToolkit().getScreenSize();
        }
        int width = size.width ;
        int height = insets.top + offset;
        Point startP = new Point(0, 0);
        Point endP = new Point(0, 0);

        if (getComponentCount() == 1) {
            startP = new Point((width - messagePane.getWidth()) / 2, 0);
            endP = new Point((width - messagePane.getWidth()) / 2, height);
        } else {
            for (int i = 0, c = getComponentCount() - 1; i < c; i++) {
                Component m = getComponent(i);
                if (m.isVisible()) {
                    height += m.getSize().height + centerLayout.getGap();
                }
            }
            startP = new Point((width - messagePane.getWidth()) / 2, height -  centerLayout.getGap()-messagePane.getHeight());
            endP = new Point((width - messagePane.getWidth()) / 2, height);
        }


        Animator animator = getAnimatorBuilder().setDuration(duration, TimeUnit.MILLISECONDS).build();
        animator.addTarget(PropertySetter.getTarget(messagePane, "alpha", new AccelerationInterpolator(0.5, 0.5), 0f, 1.0f));
        animator.addTarget(PropertySetter.getTarget(messagePane, "location", new AccelerationInterpolator(0.5, 0.5), startP, endP));
        animator.start();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
        centerLayout.setOffset(offset);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
