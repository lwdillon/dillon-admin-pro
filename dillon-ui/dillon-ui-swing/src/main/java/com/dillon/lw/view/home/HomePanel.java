package com.dillon.lw.view.home;

import com.dillon.lw.components.WScrollPane;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel() {
        this.setLayout(new BorderLayout());
        this.setOpaque(false);


        JScrollPane scrollPane = new JScrollPane(new HomDashboardPanel());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        this.add(scrollPane);
    }
}
