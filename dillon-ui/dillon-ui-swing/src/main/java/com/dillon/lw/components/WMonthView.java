/**
 * @(#)MonthView.java 1.0 2015/02/18
 */
package com.dillon.lw.components;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * MonthView is a composite of a {@link WYearMonthSpinner} and a tabular calendar display of the
 * selected month. The month can also be changed by the mouse scroll wheel. Optionally, a link for
 * setting the value to the current date can be displayed.
 * <p>
 * Clicking on a displayed date sets it as the selected value.
 * <p>
 * <B>Note that compiling or using this class requires Java 8</B>
 *
 * @author Darryl
 * @see LocalDate
 */
public class WMonthView extends JPanel {

    private static final DateTimeFormatter LABEL_FORMATTER
            = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    private final JPanel north;
    private TodayLinkLabel todayLinkLabel;
    private volatile boolean programmaticChange;
    private YearMonth yearMonth;
    private LocalDate startDate;
    private LocalDate value;
    private LocalDate min;
    private LocalDate max;
    private final WYearMonthSpinner yearMonthSpinner;
    private final JTable table = new JTable(new MonthViewTableModel());

    /**
     * Constructs a MonthView with the value set to the current date, with no upper or lower limits.
     */
    public WMonthView() {
        this(LocalDate.now());
    }

    /**
     * Constructs a MonthView with the value set to the supplied date, with no lower or upper limits.
     *
     * @param initialDate The date to set
     */
    public WMonthView(LocalDate initialDate) {
        this(initialDate, null, null);
    }

    /**
     * Constructs a MonthView with the value set to the supplied date, with the supplied lower
     * (earliest) and upper (latest) dates.
     * <p>
     * Dates outside the specified range are not displayed.
     * <p>
     * This class does not attempt to verify that minDate <= value <= maxDate. It is the
     * responsibility of client code to supply sane values.
     *
     * @param initialDate The date to set
     * @param minDate     The minimum value (earliest date); <CODE>null</CODE> for no limit.
     * @param maxDate     The maximum value (latest date); <CODE>null</CODE> for no limit.
     */
    public WMonthView(LocalDate initialDate, LocalDate minDate, LocalDate maxDate) {
        this(initialDate, minDate, maxDate, false);
    }

    /**
     * Constructs a MonthView with the value set to the supplied date, with the supplied lower
     * (earliest) and upper (latest) dates, and, optionally, a link to set the value to the current
     * date.
     * <p>
     * Dates outside the specified range are not displayed.
     * <p>
     * This class does not attempt to verify that minDate <= value <= maxDate. It is the
     * responsibility of client code to supply sane values.
     *
     * @param initialDate The date to set
     * @param minDate     The minimum value (earliest date); <CODE>null</CODE> for no limit.
     * @param maxDate     The maximum value (latest date); <CODE>null</CODE> for no limit.
     * @param showtoday   true to show a link to the current date, false otherwise
     */
    public WMonthView(LocalDate initialDate, LocalDate minDate, LocalDate maxDate, boolean showtoday) {
        super(new BorderLayout(7, 0));
        min = minDate;
        max = maxDate;
        yearMonth = YearMonth.from(initialDate);
        startDate = yearMonth.atDay(1).minusDays(yearMonth.atDay(1).getDayOfWeek().getValue());

        YearMonth firstMonth = null;
        if (minDate != null) {
            firstMonth = YearMonth.from(minDate);
        }
        YearMonth lastMonth = null;
        if ((maxDate != null)) {
            lastMonth = YearMonth.from(maxDate);
        }
        yearMonthSpinner = new WYearMonthSpinner(yearMonth, firstMonth, lastMonth);
        yearMonthSpinner.addChangeListener((ChangeEvent ce) -> {
            if (!programmaticChange) {
                setYearMonth((YearMonth) yearMonthSpinner.getValue());
            }
        });

        setValue(initialDate);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setShowGrid(false);
//        table.setBorder(BorderFactory.createLineBorder(table.getForeground()));
        table.setCellSelectionEnabled(true);
        table.setRowHeight(40);
        table.setDefaultRenderer(LocalDate.class, new MonthViewTableCellRenderer());
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(40);
        }
        JTableHeader header = table.getTableHeader();
        header.setBorder(table.getBorder());
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setDefaultRenderer(new MonthViewTableHeaderCellRenderer(header.getDefaultRenderer()));

        Timer selectionTimer = new Timer(50, ae -> {
            LocalDate newValue = (LocalDate) table.getValueAt(table.getSelectedRow(),
                    table.getSelectedColumn());
            if (newValue.equals(value)) {
                return;
            }
            boolean needSetTableSelection = false;
            if (min != null && newValue.isBefore(min)) {
                newValue = min;
                needSetTableSelection = true;
            }
            if (max != null && newValue.isAfter(max)) {
                newValue = max;
                needSetTableSelection = true;
            }
            firePropertyChange("Value", value, newValue);
            value = newValue;
            if (needSetTableSelection) {
                setTableSelection();
            }
            setYearMonth(YearMonth.from(newValue));
        });
        selectionTimer.setRepeats(false);

        ListSelectionListener selectionListener = lse -> {
            if (!programmaticChange && !lse.getValueIsAdjusting()) {
                if (selectionTimer.isRunning()) {
                    selectionTimer.restart();
                } else {
                    selectionTimer.start();
                }
            }
        };
        ListSelectionModel rowSelectionModel = table.getSelectionModel();
        rowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rowSelectionModel.addListSelectionListener(selectionListener);

        ListSelectionModel columnSelectionModel = columnModel.getSelectionModel();
        columnSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        columnSelectionModel.addListSelectionListener(selectionListener);
        table.addMouseWheelListener(new MouseAdapter() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
                if (mwe.getWheelRotation() < 0) {
                    yearMonthSpinner.setValue(yearMonthSpinner.getValue().plusMonths(1));
                } else {
                    yearMonthSpinner.setValue(yearMonthSpinner.getValue().minusMonths(1));
                }
            }
        });
        setTableSelection();

        changeTableAction(KeyEvent.VK_LEFT, ae -> {
            if (min != null && value.equals(min)) {
                return false;
            }
            if (table.getSelectedColumn() == 0) {
                LocalDate newValue = value.minusDays(1);
                WMonthView.this.firePropertyChange("Value", value, newValue);
                setValue(newValue);
                return false;
            }
            return true;
        });
        changeTableAction(KeyEvent.VK_UP, ae -> {
            if (min != null && value.isBefore(min.plusDays(7))) {
                WMonthView.this.firePropertyChange("Value", value, min);
                setValue(min);
                return false;
            }
            if (table.getSelectedRow() == 0) {
                LocalDate newValue = value.minusDays(7);
                WMonthView.this.firePropertyChange("Value", value, newValue);
                setValue(newValue);
                return false;
            }
            return true;
        });
        changeTableAction(KeyEvent.VK_RIGHT, ae -> {
            if (max != null && value.equals(max)) {
                return false;
            }
            if (table.getSelectedColumn() == 6) {
                LocalDate newValue = value.plusDays(1);
                WMonthView.this.firePropertyChange("Value", value, newValue);
                setValue(newValue);
                return false;
            }
            return true;
        });
        changeTableAction(KeyEvent.VK_DOWN, ae -> {
            if (max != null && value.isAfter(max.minusDays(7))) {
                WMonthView.this.firePropertyChange("Value", value, max);
                setValue(max);
                return false;
            }
            if (table.getSelectedRow() == 5) {
                LocalDate newValue = value.plusDays(7);
                WMonthView.this.firePropertyChange("Value", value, newValue);
                setValue(newValue);
                return false;
            }
            return true;
        });
        changeTableAction(KeyEvent.VK_ENTER, ae -> {
            WMonthView.this.firePropertyChange("Confirm", null, value);
            return false;
        });
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent me) {
                WMonthView.this.firePropertyChange("Confirm", null, value);
            }
        });

        setFocusable(true);

        north = new JPanel(new BorderLayout());
        north.setPreferredSize(new Dimension(0, 60));
        north.add(yearMonthSpinner, BorderLayout.CENTER);
        north.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("ColorPalette.border")));

        Box tableBox = Box.createVerticalBox();
        tableBox.add(header);
        tableBox.add(table);
        add(north, BorderLayout.NORTH);
        add(tableBox, BorderLayout.CENTER);
        todayLinkLabel = getTodayLinkLabel();
        if (showtoday) {
            add(todayLinkLabel, BorderLayout.SOUTH);
        }

    }

    @Override
    public void updateUI() {
        super.updateUI();

        if (north != null) {
            north.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("ColorPalette.border")));
        }
    }

    private void changeTableAction(int key, Function<ActionEvent, Boolean> function) {
        InputMap ancestorMap = table.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        InputMap windowMap = table.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = table.getActionMap();
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key, 0);
        Object actionKey = ancestorMap.get(keyStroke);
        windowMap.put(keyStroke, actionKey);

        Action oldAction = actionMap.get(actionKey);
        Action replacement = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (function.apply(ae)) {
                    oldAction.actionPerformed(ae);
                }
            }
        };
        actionMap.put(actionKey, replacement);
    }

    private void setYearMonth(YearMonth yearMonth) {
        if (min != null && yearMonth.atEndOfMonth().isBefore(min)) {
            yearMonth = YearMonth.from(min);
        }
        if (max != null && yearMonth.atDay(1).isAfter(max)) {
            yearMonth = YearMonth.from(max);
        }
        if (yearMonth.equals(this.yearMonth)) {
            //return; // no, we still have to setTableSelection
        }

        if (!yearMonth.equals(yearMonthSpinner.getValue())) {
            programmaticChange = true;
            yearMonthSpinner.setValue(yearMonth);
            programmaticChange = false;
        }
        this.yearMonth = yearMonth;
        startDate = yearMonth.atDay(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        setTableSelection();
        repaint();
    }

    /**
     * Returns the current value
     *
     * @return the current value
     */
    public LocalDate getValue() {
        return value;
    }

    /**
     * Sets the current value, adjusted to be within any specified min/max range.
     *
     * @param value The value to set
     */
    public void setValue(LocalDate value) {
        if (min != null && value.isBefore(min)) {
            value = min;
        }
        if (max != null && value.isAfter(max)) {
            value = max;
        }
        this.value = value;
        if (value != null) {
            setYearMonth(YearMonth.from(value));
        }

        getTodayLinkLabel().setText("今天: " + LocalDate.now().format(LABEL_FORMATTER));
    }

    private void setTableSelection() {
        programmaticChange = true;
        if (value.isBefore(startDate)
                || value.isAfter(startDate.plusDays(41))) {
            table.clearSelection();
        } else {
            int days = (int) ChronoUnit.DAYS.between(startDate, value);
            table.setRowSelectionInterval(days / 7, days / 7);
            table.setColumnSelectionInterval(days % 7, days % 7);
        }
        programmaticChange = false;
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
     * This method does not attempt to verify that min <= value <= max. It is the responsibility of
     * client code to supply a sane value.
     *
     * @param min The earliest date that can be selected, or <CODE>null</CODE> for no limit.
     */
    public void setMin(LocalDate min) {
        this.min = min;
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
     * This method does not attempt to verify that min <= value <= max. It is the responsibility of
     * client code to supply a sane value.
     *
     * @param max The latest date that can be selected, or <CODE>null</CODE> for no limit.
     */
    public void setMax(LocalDate max) {
        this.max = max;
    }

    private class MonthViewTableModel<T extends LocalDate> extends AbstractTableModel {

        @Override
        public Class<?> getColumnClass(int column) {
            return LocalDate.class;
        }

        @Override
        public String getColumnName(int column) {
            String week = DayOfWeek.of((column + 6) % 7 + 1)
                    .getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
            return week.substring(2);
        }

        @Override
        public int getRowCount() {
            return 6;
        }

        @Override
        public int getColumnCount() {
            return 7;
        }

        @Override
        public Object getValueAt(int row, int column) {
            return startDate.plusDays(row * 7 + column);
        }
    }

    private class MonthViewTableHeaderCellRenderer implements TableCellRenderer {

        private final TableCellRenderer defaultRenderer;

        private MonthViewTableHeaderCellRenderer(TableCellRenderer defaultRenderer) {
            this.defaultRenderer = defaultRenderer;

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JComponent c = (JComponent) defaultRenderer.getTableCellRendererComponent(table, value,
                    hasFocus, hasFocus, row, row);
            c.setPreferredSize(new Dimension(40, 40));
            c.setBorder(BorderFactory.createEmptyBorder());
            c.setForeground(column == 0 ? Colors.normalSunday
                    : column == 6 ? Colors.normalSaturday
                    : getForeground());
            return c;
        }

    }


    private class MonthViewTableCellRenderer extends DefaultTableCellRenderer {

        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");

        private MonthViewTableCellRenderer() {
            setHorizontalAlignment(CENTER);

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            if (value == null) {
                return this;
            }
            LocalDate dateValue = (LocalDate) value;

            boolean isSunday = dateValue.getDayOfWeek() == DayOfWeek.SUNDAY;
            boolean isSaturday = dateValue.getDayOfWeek() == DayOfWeek.SATURDAY;
            if (dateValue.getMonth() == yearMonth.getMonth()) {
                setForeground(isSunday ? Colors.normalSunday : isSaturday ? Colors.normalSaturday : table.getForeground());
            } else {
                Color fadedc = UIManager.getColor("ColorPalette.dis") == null ? new Color(0x8b8b8c) : UIManager.getColor("ColorPalette.dis");
                setForeground(isSunday ? Colors.fadedSunday : isSaturday ? Colors.fadedSaturday : fadedc);
            }
            hasFocus = true;
            if (min != null && dateValue.isBefore(min)
                    || max != null && dateValue.isAfter(max)) {
                setForeground(Colors.blank);
                isSelected = false;
                hasFocus = false;
            }
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setText(formatter.format(dateValue));
            setBorder(BorderFactory.createEmptyBorder());
            return this;
        }
    }

    public TodayLinkLabel getTodayLinkLabel() {
        if (todayLinkLabel == null) {
            todayLinkLabel = new TodayLinkLabel();
        }
        return todayLinkLabel;
    }

    private static class Colors {

        public static final Color faded = UIManager.getColor("ColorPalette.dis") == null ? new Color(0x8b8b8c) : UIManager.getColor("ColorPalette.dis");
        public static final Color normalSunday = new Color(0xF56C6C);
        public static final Color fadedSunday = new Color(0xfbc4c4);
        public static final Color normalSaturday = new Color(0x67C23A);
        public static final Color fadedSaturday = new Color(0xc2e7b0);
        public static final Color blank = new Color(0x8b8b8c);


    }

    private class TodayLinkLabel extends JLabel {

        private final Font normalFont;
        private final Font underlinedFont;

        private TodayLinkLabel() {
            super("今天: " + LocalDate.now().format(LABEL_FORMATTER));
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
                    LocalDate newValue = LocalDate.now();
                    WMonthView.this.firePropertyChange("Value", value, newValue);
                    setValue(newValue);
//                    MonthView.this.firePropertyChange("Confirm", null, newValue);
                }
            });
        }
    }
}