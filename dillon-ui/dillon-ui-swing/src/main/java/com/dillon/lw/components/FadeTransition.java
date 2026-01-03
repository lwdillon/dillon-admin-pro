package com.dillon.lw.components;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;

import javax.swing.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class FadeTransition {

    public static void fade(
            JFrame frame,
            AlphaPanel from,
            AlphaPanel to
    ) {
        to.setAlpha(0f);

        JLayeredPane layeredPane = frame.getLayeredPane();
        to.setBounds(from.getBounds());

        layeredPane.add(to, JLayeredPane.POPUP_LAYER);

        Animator animator = new Animator.Builder().setDuration(350, MILLISECONDS).setInterpolator(new AccelerationInterpolator(.5, .5)).build();
        animator.addTarget(new TimingTargetAdapter() {

            @Override
            public void timingEvent(Animator source, double fraction) {
                from.setAlpha((float) (1f - fraction));
                to.setAlpha((float) fraction);
            }

            @Override
            public void end(Animator source) {
                layeredPane.remove(from);
                to.setAlpha(1f);
                frame.getContentPane().add(to);
                frame.revalidate();
                frame.repaint();
            }
        });

        animator.start();
    }
}