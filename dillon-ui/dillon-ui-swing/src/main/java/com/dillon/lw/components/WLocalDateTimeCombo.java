
package com.dillon.lw.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatComboBoxUI;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Map;


/**
 * @Description:
 * @param:
 * @return:
 * @auther: liwen
 * @date: 2021/11/16 10:18 上午
 */
public class WLocalDateTimeCombo extends JComboBox<LocalDateTime> {

    private final DefaultComboBoxModel<LocalDateTime> comboModel = new DefaultComboBoxModel<>();
    private final JPanel south;
    private final CurrentDateLinkLabel nowLinkLabel;
    private LocalDate min;
    private LocalDate max;
    private final WMonthView WMonthView;
    private final WTimeView WTimeView;
    private final JPopupMenu popupMenu = new JPopupMenu();
    private JButton determineButton = new JButton("确定");

    /**
     * Constructs a LocalDateCombo with today's date, no upper or lower limits, formatted in a medium
     * style.
     *
     * @see FormatStyle#MEDIUM
     */
    public WLocalDateTimeCombo() {
        this(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    /**
     * Constructs a LocalDateCombo with today's date and no upper or lower limits, formatted according
     * to the provided formatter.
     *
     * @param formatter Formats the date for display
     * @see DateTimeFormatter
     */
    public WLocalDateTimeCombo(DateTimeFormatter formatter) {
        this(LocalDateTime.now(), formatter);
    }

    /**
     * Constructs a LocalDateCombo with the date provided and no lower or upper limits, formatted in a
     * medium style.
     *
     * @param value The initial value
     * @see FormatStyle#MEDIUM
     */
    public WLocalDateTimeCombo(LocalDateTime value) {
        this(value, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    /**
     * Constructs a LocalDateCombo with the date provided and no lower or upper limits, formatted
     * according to the provided formatter.
     *
     * @param value     The initial value
     * @param formatter Formats the date for display
     */
    public WLocalDateTimeCombo(LocalDateTime value, DateTimeFormatter formatter) {
        this(value, null, null, formatter);
    }

    /**
     * Constructs a LocalDateCombo with the date, lower (earliest) and upper (latest) limits provided,
     * formatted in a medium style.
     * <p>
     * Dates outside the specified range are not displayed.
     * <p>
     * This class does not attempt to verify that minDate <= value <= maxDate. It is the
     * responsibility of client code to supply sane values.
     *
     * @param value   The initial value
     * @param minDate The minimum value (earliest date); <CODE>null</CODE> for no limit.
     * @param maxDate The maximum value (latest date); <CODE>null</CODE> for no limit.
     * @see FormatStyle#MEDIUM
     */
    public WLocalDateTimeCombo(LocalDateTime value, LocalDate minDate, LocalDate maxDate) {
        this(value, minDate, maxDate, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    /**
     * Constructs a LocalDateCombo with the date, lower (earliest) and upper (latest) limits provided,
     * formatted according to the provided formatter.
     * <p>
     * Dates outside the specified range are not displayed.
     * <p>
     * This class does not attempt to verify that minDate <= value <= maxDate. It is the
     * responsibility of client code to supply sane values.
     *
     * @param value     The initial value
     * @param minDate   The minimum value (earliest date); <CODE>null</CODE> for no limit.
     * @param maxDate   The maximum value (latest date); <CODE>null</CODE> for no limit.
     * @param formatter Formats the date for display
     */
    public WLocalDateTimeCombo(LocalDateTime value, LocalDate minDate, LocalDate maxDate,
                               DateTimeFormatter formatter) {


        JPanel center = new JPanel(new BorderLayout(3, 0));
        WMonthView = new WMonthView(value.toLocalDate(), minDate, maxDate, false);
        WMonthView.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("ColorPalette.border")));

        WTimeView = new WTimeView(value.toLocalTime(), false);
        WTimeView.setPreferredSize(new Dimension(180, 325));

        south = new JPanel(new BorderLayout());
        south.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("ColorPalette.border")));

        Box box = Box.createHorizontalBox();
        box.add(nowLinkLabel = new CurrentDateLinkLabel());
        box.add(Box.createHorizontalGlue());
        box.add(determineButton);
        box.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        determineButton.addActionListener(e -> {
            LocalDateTime localDateTime = LocalDateTime.of(WMonthView.getValue(), WTimeView.getValue());
            popupMenu.setVisible(false);
            setValue(localDateTime);
        });
        south.add(box);
        center.add(WMonthView, BorderLayout.CENTER);
        center.add(WTimeView, BorderLayout.EAST);
        center.add(south, BorderLayout.SOUTH);

        comboModel.addElement(value);
        setModel(comboModel);
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean hasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                setText(formatter.format((TemporalAccessor) value));
                return this;
            }
        });

        min = minDate;
        max = maxDate;
        popupMenu.add(center);

        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
                final boolean popupShown = popupMenu.isShowing();
                SwingUtilities.invokeLater(() -> {
                    hidePopup();
                    if (popupShown) {
                        popupMenu.setVisible(false);
                    } else {
                        nowLinkLabel.setText("当前: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" ");
                        WMonthView.setValue(getValue().toLocalDate());
                        WTimeView.setValue(getValue().toLocalTime());
                        popupMenu.show(WLocalDateTimeCombo.this, 0, getHeight());
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent pme) {
            }
        });


    }

    /**
     * Returns the current value
     *
     * @return the current value
     */
    public LocalDateTime getValue() {
        return comboModel.getElementAt(0);//value;
    }

    /**
     * Sets the current value, adjusted to be within any specified min/max range.
     *
     * @param value The value to set
     */
    public void setValue(LocalDateTime value) {
        if (getSelectedItem().equals(value)) {
            return;
        }

        comboModel.removeAllElements();
        comboModel.addElement(value);
        if (!WMonthView.getValue().equals(value.toLocalDate())) {
            WMonthView.setValue(value.toLocalDate());
        }
    }

    /**
     * Returns the minimum value (earliest date), or <CODE>null</CODE> if no limit is set.
     *
     * @return The earliest date that can be selected.
     */
    public LocalDate getMin() {
        return min;
    }

    /**
     * Sets the minimum value (earliest date). Call this method with a <CODE>null</CODE> value for no
     * limit.
     * <p>
     * This class does not attempt to verify that min <= value <= max. It is the responsibility of
     * client code to supply a sane value.
     *
     * @param min The earliest date that can be selected, or <CODE>null</CODE> for no limit.
     */
    public void setMin(LocalDate min) {
        this.min = min;
        WMonthView.setMin(min);
    }

    /**
     * Returns the maximum value (latest date), or <CODE>null</CODE> if no limit is set.
     *
     * @return The latest date that can be selected.
     */
    public LocalDate getMax() {
        return max;
    }

    /**
     * Sets the maximum value (latest date). Call this method with a <CODE>null</CODE> value for no
     * limit.
     * <p>
     * This class does not attempt to verify that min <= value <= max. It is the responsibility of
     * client code to supply a sane value.
     *
     * @param max The latest date that can be selected, or <CODE>null</CODE> for no limit.
     */
    public void setMax(LocalDate max) {
        this.max = max;
        WMonthView.setMax(max);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setUI(new MyFlatComboBoxUI());

        if (popupMenu != null) {
            south.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("ColorPalette.border")));
            WMonthView.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("ColorPalette.border")));

            SwingUtilities.updateComponentTreeUI(popupMenu);
        }
    }

    private class MyFlatComboBoxUI extends FlatComboBoxUI {

        public MyFlatComboBoxUI() {
            super();

        }

        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton();
            button.setOpaque(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBackground(new Color(1f, 1f, 1f, 0f));
            button.setIcon(new FlatSVGIcon("icons/rili.svg", 16, 16));
            return button;
        }
    }

    private class CurrentDateLinkLabel extends JLabel {

        private final Font normalFont;
        private final Font underlinedFont;

        private CurrentDateLinkLabel() {
            super("当前: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" ");
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
                    LocalDateTime newValue = LocalDateTime.now();
                    WMonthView.setValue(newValue.toLocalDate());
                    WTimeView.setValue(newValue.toLocalTime());
                }
            });
        }
    }
}