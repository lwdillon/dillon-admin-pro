/*
 * Created by JFormDesigner on Sat Jan 24 15:44:54 CST 2026
 */

package com.dillon.lw.view.home;

import java.awt.*;
import javax.swing.*;

import net.miginfocom.swing.*;

/**
 * @author wenli
 */
public class HomDashboardPanel extends JPanel {
    public HomDashboardPanel() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        panel4 = new JPanel();
        panel5 = new JPanel();
        panel6 = new JPanel();
        panel7 = new JPanel();
        panel8 = new JPanel();
        panel9 = new JPanel();
        panel10 = new JPanel();

        //======== this ========
        setLayout(new MigLayout(
            "fill,insets 5,hidemode 3,gap 10 10",
            // columns
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]",
            // rows
            "[140!,sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[grow]"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new BorderLayout());
        }
        add(panel1, "cell 0 0 1 3,grow");

        //======== panel2 ========
        {
            panel2.setOpaque(false);
            panel2.setLayout(new BorderLayout());
        }
        add(panel2, "cell 1 0,grow");

        //======== panel3 ========
        {
            panel3.setOpaque(false);
            panel3.setLayout(new BorderLayout());
        }
        add(panel3, "cell 2 0,grow");

        //======== panel4 ========
        {
            panel4.setOpaque(false);
            panel4.setLayout(new BorderLayout());
        }
        add(panel4, "cell 3 0,grow");

        //======== panel5 ========
        {
            panel5.setOpaque(false);
            panel5.setLayout(new BorderLayout());
        }
        add(panel5, "cell 1 1 3 2,grow");

        //======== panel6 ========
        {
            panel6.setOpaque(false);
            panel6.setLayout(new BorderLayout());
        }
        add(panel6, "cell 0 3,grow");

        //======== panel7 ========
        {
            panel7.setOpaque(false);
            panel7.setLayout(new BorderLayout());
        }
        add(panel7, "cell 1 3,grow");

        //======== panel8 ========
        {
            panel8.setOpaque(false);
            panel8.setLayout(new BorderLayout());
        }
        add(panel8, "cell 2 3,grow");

        //======== panel9 ========
        {
            panel9.setOpaque(false);
            panel9.setLayout(new BorderLayout());
        }
        add(panel9, "cell 3 3,grow");

        //======== panel10 ========
        {
            panel10.setOpaque(false);
            panel10.setLayout(new BorderLayout());
        }
        add(panel10, "cell 0 4 4 1,grow");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        //user
        panel1.add(new RadialChartPanel());
        panel2.add(new TotalProfitPanel("Total profit", "$559.25k", "+16.24 %", new Color(45, 211, 126)));
        panel3.add(new TotalProfitPanel("Total profit", "$559.25k", "+16.24 %", new Color(45, 211, 126)));
        panel4.add(new TotalProfitPanel("Total profit", "$559.25k", "+16.24 %", new Color(45, 211, 126)));
        panel5.add(new RevenueDashboardPanel());
        // 1. 模拟数据)
        float[] dataPoints = {15, 40, 30, 55, 45, 80, 70, 90, 85, 100, 60, 40, 20};
        Color themeGreen = new Color(45, 211, 126);
        panel6.add(new ProjectCardPanel("Active projects", "825", "Projects this month", dataPoints, themeGreen, "icons/project.svg")); // 此处替换为你的图标对象
        panel7.add(new ProjectCardPanel("Active projects", "825", "Projects this month", dataPoints, themeGreen, "icons/project.svg")); // 此处替换为你的图标对象
        panel8.add(new ProjectCardPanel("Active projects", "825", "Projects this month", dataPoints, themeGreen, "icons/project.svg")); // 此处替换为你的图标对象
        panel9.add(new ProjectCardPanel("Active projects", "825", "Projects this month", dataPoints, themeGreen, "icons/project.svg")); // 此处替换为你的图标对象
        panel10.add(new CryptoTablePanel());
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel panel4;
    private JPanel panel5;
    private JPanel panel6;
    private JPanel panel7;
    private JPanel panel8;
    private JPanel panel9;
    private JPanel panel10;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
