
package com.lw.swing.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatComboBoxUI;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;


/**
 * @Description:
 * @param:
 * @return:
 * @auther: liwen
 * @date: 2021/11/16 10:18 上午
 */
public class WLocalDateCombo extends JComboBox<LocalDate> {

    private final DefaultComboBoxModel<LocalDate> comboModel = new DefaultComboBoxModel<>();
    private LocalDate min;
    private LocalDate max;
    private final WMonthView WMonthView;
    private final JPopupMenu popupMenu = new JPopupMenu();

    /**
     * Constructs a LocalDateCombo with today's date, no upper or lower limits, formatted in a medium
     * style.
     *
     * @see FormatStyle#MEDIUM
     */
    public WLocalDateCombo() {
        this(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    /**
     * Constructs a LocalDateCombo with today's date and no upper or lower limits, formatted according
     * to the provided formatter.
     *
     * @param formatter Formats the date for display
     * @see DateTimeFormatter
     */
    public WLocalDateCombo(DateTimeFormatter formatter) {
        this(LocalDate.now(), formatter);
    }

    /**
     * Constructs a LocalDateCombo with the date provided and no lower or upper limits, formatted in a
     * medium style.
     *
     * @param value The initial value
     * @see FormatStyle#MEDIUM
     */
    public WLocalDateCombo(LocalDate value) {
        this(value, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    /**
     * Constructs a LocalDateCombo with the date provided and no lower or upper limits, formatted
     * according to the provided formatter.
     *
     * @param value     The initial value
     * @param formatter Formats the date for display
     */
    public WLocalDateCombo(LocalDate value, DateTimeFormatter formatter) {
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
    public WLocalDateCombo(LocalDate value, LocalDate minDate, LocalDate maxDate) {
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
    public WLocalDateCombo(LocalDate value, LocalDate minDate, LocalDate maxDate,
                           DateTimeFormatter formatter) {
        // Thursday after 24 of September
        LocalDate longNameDate = LocalDate.now().withDayOfMonth(24).withMonth(9);
        longNameDate = longNameDate.plusDays(4 - longNameDate.getDayOfWeek().getValue());
        setPrototypeDisplayValue(longNameDate);

        WMonthView = new WMonthView(value, minDate, maxDate, true);
        comboModel.addElement(value);
        setModel(comboModel);
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean hasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                if (value != null) {
                    setText(formatter.format((TemporalAccessor) value));
                }
                return this;
            }
        });

        min = minDate;
        max = maxDate;
        popupMenu.add(WMonthView);

        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
                final boolean popupShown = popupMenu.isShowing();
                SwingUtilities.invokeLater(() -> {
                    hidePopup();
                    if (popupShown) {
                        popupMenu.setVisible(false);
                    } else {
                        WMonthView.setValue(getValue());
                        popupMenu.show(WLocalDateCombo.this, 0, getHeight());
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

        WMonthView.addPropertyChangeListener("Confirm", pce -> {
            popupMenu.setVisible(false);
        });
        WMonthView.addPropertyChangeListener("Value", pce -> {
            setValue((LocalDate) pce.getNewValue());
            firePropertyChange("Value", pce.getOldValue(), pce.getNewValue());
        });
    }

    /**
     * Returns the current value
     *
     * @return the current value
     */
    public LocalDate getValue() {
        return comboModel.getElementAt(0);//value;
    }

    /**
     * Sets the current value, adjusted to be within any specified min/max range.
     *
     * @param value The value to set
     */
    public void setValue(LocalDate value) {
        if (getSelectedItem() != null) {
            if (getSelectedItem().equals(value)) {
                return;
            }
        }

        if (min != null && value.isBefore(min)) {
            value = min;
        }
        if (max != null && value.isAfter(max)) {
            value = max;
        }
        comboModel.removeAllElements();
        comboModel.addElement(value);
        if (WMonthView.getValue() != null) {
            if (!WMonthView.getValue().equals(value)) {
                WMonthView.setValue(value);
            }
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
            SwingUtilities.updateComponentTreeUI( popupMenu );
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
}