/**
 * @(#)YearMonthSpinner.java	1.0 2015/02/17
 */
package com.dillon.lw.components;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * YearMonthSpinner is a composite of two spinners, one for the month and the other for the year,
 * used to select a java.time.MonthYear.
 * <P>
 * <B>Note that compiling or using this class requires Java 8</B>
 *
 * @author Darryl
 * @see YearMonth
 *
 */
public class WYearMonthSpinner extends JPanel {

  private boolean programmaticChange;
  private SpinnerTemporalModel<YearMonth> monthModel;
  private SpinnerTemporalModel<YearMonth> yearModel;
  private JSpinner monthSpinner;
  private JSpinner yearSpinner;

  /**
   * Constructs a YearMonthSpinner with the current year and month, with no lower or upper limits.
   *
   */
  public WYearMonthSpinner() {
    this(YearMonth.now());
  }

  /**
   * Constructs a YearMonthSpinner with the supplied year and month, with no lower or upper limits.
   *
   * @param value the initial value
   */
  public WYearMonthSpinner(YearMonth value) {
    this(value, null, null);
  }

  /**
   * Constructs a YearMonthSpinner with the supplied year and month, lower (earliest) and upper
   * (latest) dates.
   *
   * @param value the initial value
   * @param min the minimum (earliest) value
   * @param max the maximum (latest) value
   */
  public WYearMonthSpinner(YearMonth value, YearMonth min, YearMonth max) {
    super(new FlowLayout(FlowLayout.CENTER, 7, 7));

    if (value == null) {
      value = YearMonth.now();
    }
    monthModel = new SpinnerTemporalModel<>(value, min, max, ChronoUnit.MONTHS);
    monthSpinner = new JSpinner(monthModel) {
      @Override
      public ComponentOrientation getComponentOrientation() {
        return ComponentOrientation.LEFT_TO_RIGHT;
      }
    };
    SpinnerTemporalEditor monthEditor = new SpinnerTemporalEditor(monthSpinner,
        DateTimeFormatter.ofPattern("M 月"));
    monthEditor.getTextField().setColumns(5);
    monthSpinner.setEditor(monthEditor);
    monthEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
    monthEditor.getTextField().setFont( monthEditor.getTextField().getFont().deriveFont(18f));
    yearModel = new SpinnerTemporalModel<>(value, min, max, ChronoUnit.YEARS);
    yearSpinner = new JSpinner(yearModel) {
      @Override
      public ComponentOrientation getComponentOrientation() {
        return ComponentOrientation.LEFT_TO_RIGHT;
      }
    };
    SpinnerTemporalEditor yearEditor
        = new SpinnerTemporalEditor(yearSpinner, DateTimeFormatter.ofPattern("yyyy 年"));
    yearEditor.getTextField().setColumns(4);
    yearEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
    yearSpinner.setEditor(yearEditor);

    yearEditor.getTextField().setFont( yearEditor.getTextField().getFont().deriveFont(18f));

    monthSpinner.addChangeListener(ce -> {
      if (!programmaticChange) {
        programmaticChange = true;
        yearSpinner.setValue(monthSpinner.getValue());//value);
        programmaticChange = false;
      }
    });
    yearSpinner.addChangeListener(ce -> {
      if (!programmaticChange) {
        programmaticChange = true;
        monthSpinner.setValue(yearSpinner.getValue());//value);
        programmaticChange = false;
      }
    });

    add(monthSpinner);
    add(yearSpinner);
  }

  /**
   * Adds a listener that is notified each time a change occurs. The source of the
   * <code>ChangeEvent</code> will be the contained month spinner.
   *
   * @param listener the <code>ChangeListeners</code> to add
   * @see #removeChangeListener
   */
  public void addChangeListener(ChangeListener listener) {
    monthSpinner.addChangeListener(listener);
  }

  /**
   * Removes a <code>ChangeListener</code>.
   *
   * @param listener the <code>ChangeListener</code> to remove
   */
  public void removeChangeListener(ChangeListener listener) {
    monthSpinner.removeChangeListener(listener);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    monthSpinner.setEnabled(enabled);
    yearSpinner.setEnabled(enabled);
  }

  /**
   * Returns the current value
   *
   * @return the current value
   */
  public YearMonth getValue() {
    return monthModel.getTemporalValue();
  }

  /**
   * Sets the current value
   * <P>
   * This class does not attempt to verify that min <= value <= max. It is the responsibility of
   * client code to supply a sane value.
   *
   * @param value The value to set
   */
  public void setValue(YearMonth value) {
    programmaticChange = true;
    monthSpinner.setValue(value);
    yearSpinner.setValue(value);
    programmaticChange = false;
  }

  /**
   * Returns the minimum value (earliest year/month), or <CODE>null</CODE> if no limit is set.
   *
   * @return The earliest year/month that can be selected.
   */
  public YearMonth getMin() {
    return monthModel.getMin();
  }

  /**
   * Sets the minimum value (earliest year/month). Call this method with a <CODE>null</CODE> value
   * for no limit.
   * <P>
   * This class does not attempt to verify that min <= value <= max. It is the responsibility of
   * client code to supply a sane value.
   *
   * @param min The earliest year/month that can be selected, or <CODE>null</CODE> for no limit.
   */
  public void setMin(YearMonth min) {
    monthModel.setMin(min);
    yearModel.setMin(min);
  }

  /**
   * Returns the maximum value (latest year/month), or <CODE>null</CODE> if no limit is set.
   *
   * @return The latest year/month that can be selected.
   */
  public YearMonth getMax() {
    return monthModel.getMax();
  }

  /**
   * Sets the maximum value (latest year/month). Call this method with a <CODE>null</CODE> value for
   * no limit.
   * <P>
   * This class does not attempt to verify that min <= value <= max. It is the responsibility of
   * client code to supply a sane value.
   *
   * @param max The latest year/month that can be selected, or <CODE>null</CODE> for no limit.
   */
  public void setMax(YearMonth max) {
    monthModel.setMax(max);
    yearModel.setMax(max);
  }

  @Override
  public void updateUI() {
    super.updateUI();
    if (yearSpinner != null) {
      yearSpinner.updateUI();
      monthSpinner.updateUI();
    }
  }
}