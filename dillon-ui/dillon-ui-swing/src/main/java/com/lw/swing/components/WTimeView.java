package com.lw.swing.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @version： 0.0.1
 * @description:
 * @className: TimeView
 * @author: liwen
 * @date: 2021/11/16 16:23
 */
public class WTimeView extends JPanel {

    private final NowLinkLabel nowLinkLabel;
    private final JScrollPane hs;
    private final JScrollPane ms;
    private final JPanel south;
    private JList hourList;
    private JList minuteList;
    private JList secondList;
    private JLabel timeLabel;
    private LocalTime value;
    private DateTimeFormatter formatter;
    private JButton determineButton = new JButton("确定");
    private LocalTime min;
    private LocalTime max;

    public WTimeView() {
        this(LocalTime.now(), DateTimeFormatter.ofPattern("HH:mm:ss"), true);
    }

    public WTimeView(LocalTime value) {
        this(LocalTime.now(), DateTimeFormatter.ofPattern("HH:mm:ss"), true);
    }

    public WTimeView(LocalTime value, boolean showDetermine) {
        this(LocalTime.now(), DateTimeFormatter.ofPattern("HH:mm:ss"), showDetermine);
    }


    public WTimeView(LocalTime value, DateTimeFormatter formatter, boolean showDetermine) {

        super(new BorderLayout(7, 0));
        this.formatter = formatter;
        this.value = value;

        hourList = new JList(new TimeViewModel(24));
        hourList.setSelectedIndex(value.getHour());
        hourList.setCellRenderer(new TimeViewListCellRenderer());
        hourList.setBorder(BorderFactory.createEmptyBorder());
        hourList.addListSelectionListener(e -> {
            LocalTime newValue = getValue().withHour(hourList.getSelectedIndex());
            setValue(newValue);
        });

        minuteList = new JList(new TimeViewModel(60));
        minuteList.setCellRenderer(new TimeViewListCellRenderer());
        minuteList.setSelectedIndex(value.getMinute());
        minuteList.setBorder(BorderFactory.createEmptyBorder());
        minuteList.addListSelectionListener(e -> {
            LocalTime newValue = getValue().withMinute(minuteList.getSelectedIndex());
            setValue(newValue);
        });

        secondList = new JList(new TimeViewModel(60));
        secondList.setBorder(BorderFactory.createEmptyBorder());
        secondList.setCellRenderer(new TimeViewListCellRenderer());
        secondList.setSelectedIndex(value.getSecond());
        secondList.addListSelectionListener(e -> {
            LocalTime newValue = getValue().withSecond(secondList.getSelectedIndex());
            setValue(newValue);
        });
        timeLabel = new JLabel(formatter.format(value), JLabel.CENTER);
        timeLabel.setPreferredSize(new Dimension(0, 45));
        timeLabel.setFont(timeLabel.getFont().deriveFont(20f));
        timeLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("ColorPalette.border")));

        JPanel gridPanel = new JPanel(new GridLayout(1, 3, 0, 0));
        gridPanel.setOpaque(false);
        hs = new JScrollPane(hourList);
        hs.setOpaque(false);
        hs.getViewport().setOpaque(false);
        hs.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("ColorPalette.border")));
        ms = new JScrollPane(minuteList);
        ms.setOpaque(false);
        ms.getViewport().setOpaque(false);
        ms.setBorder(BorderFactory.createEmptyBorder());
        ms.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("ColorPalette.border")));

        JScrollPane ss = new JScrollPane(secondList);
        ss.setOpaque(false);
        ss.getViewport().setOpaque(false);
        ss.setBorder(BorderFactory.createEmptyBorder());
        gridPanel.add(hs);
        gridPanel.add(ms);
        gridPanel.add(ss);

        JPanel box = new JPanel(new BorderLayout());
        box.add(determineButton, BorderLayout.EAST);
        box.add(nowLinkLabel = new NowLinkLabel());
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
        determineButton.addActionListener(e -> {
            firePropertyChange("Value", null, getValue());
            firePropertyChange("Confirm", null, getValue());
        });

        south = new JPanel(new BorderLayout());
        south.add(box);
        south.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("ColorPalette.border")));

        add(timeLabel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
        if (showDetermine) {
            add(south, BorderLayout.SOUTH);
        }
    }

    public LocalTime getValue() {
        return value;
    }

    public void setValue(LocalTime value) {
        this.value = value;
        timeLabel.setText(formatter.format(value));
        hourList.setSelectedIndex(value.getHour());
        minuteList.setSelectedIndex(value.getMinute());
        secondList.setSelectedIndex(value.getSecond());
        nowLinkLabel.setText("此刻: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " ");
        hourList.ensureIndexIsVisible(hourList.getSelectedIndex());
        minuteList.ensureIndexIsVisible(minuteList.getSelectedIndex());
        secondList.ensureIndexIsVisible(secondList.getSelectedIndex());
    }


    public LocalTime getMin() {
        return min;
    }

    public void setMin(LocalTime min) {
        this.min = min;
    }

    public LocalTime getMax() {
        return max;
    }

    public void setMax(LocalTime max) {
        this.max = max;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (hs != null) {
            timeLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("ColorPalette.border")));
            hs.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("ColorPalette.border")));
            ms.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("ColorPalette.border")));
            south.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("ColorPalette.border")));

        }
    }

    private class TimeViewListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            component.setPreferredSize(new Dimension(40, 40));
            setText(index < 10 ? ("0" + index) : (index + ""));
            setHorizontalAlignment(CENTER);
            return component;
        }
    }

    private class TimeViewModel<Integer> extends DefaultListModel<java.lang.Integer> {

        public TimeViewModel(int size) {

            setSize(size);
        }

        @Override
        public java.lang.Integer getElementAt(int index) {
            return index;
        }

    }

    private class NowLinkLabel extends JLabel {

        private final Font normalFont;
        private final Font underlinedFont;

        private NowLinkLabel() {
            super("此刻: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " ");
            setHorizontalAlignment(CENTER);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            normalFont = getFont();
            Map attributes = normalFont.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            underlinedFont = normalFont.deriveFont(attributes);
//            setPreferredSize(new Dimension(0, 40));
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    setFont(underlinedFont);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setFont(normalFont);
                }

                @Override
                public void mouseClicked(MouseEvent me) {
                    LocalTime newValue = LocalTime.now();
                    firePropertyChange("Value", getValue(), newValue);
                    setValue(newValue);
                }
            });
        }
    }

}
