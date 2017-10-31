/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package support.ui.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>FastDatePrinter is a fast and thread-safe version of
 * {@link java.text.SimpleDateFormat}.</p>
 *
 * <p>This class can be used as a direct replacement to
 * {@code SimpleDateFormat} in most formatting situations.
 * This class is especially useful in multi-threaded server environments.
 * {@code SimpleDateFormat} is not thread-safe in any JDK version,
 * nor will it be as Sun have closed the bug/RFE.
 * </p>
 *
 * <p>Only formatting is supported, but all patterns are compatible with
 * SimpleDateFormat (except time zones and some year patterns - see below).</p>
 *
 * <p>Java 1.4 introduced a new pattern letter, {@code 'Z'}, to represent
 * time zones in RFC822 format (eg. {@code +0800} or {@code -1100}).
 * This pattern letter can be used here (on all JDK versions).</p>
 *
 * <p>In addition, the pattern {@code 'ZZ'} has been made to represent
 * ISO8601 full format time zones (eg. {@code +08:00} or {@code -11:00}).
 * This introduces a minor incompatibility with Java 1.4, but at a gain of
 * useful functionality.</p>
 *
 * <p>Javadoc cites for the year pattern: <i>For formatting, if the number of
 * pattern letters is 2, the year is truncated to 2 digits; otherwise it is
 * interpreted as a number.</i> Starting with Java 1.7 a pattern of 'Y' or
 * 'YYY' will be formatted as '2003', while it was '03' in former Java
 * versions. FastDatePrinter implements the behavior of Java 7.</p>
 *
 * @version $Id: FastDatePrinter.java 1567799 2014-02-12 23:25:58Z sebb $
 * @since 3.2
 */
public class FastDatePrinter implements DatePrinter, Serializable {
  // A lot of the speed in this class comes from caching, but some comes
  // from the special int to StringBuffer conversion.
  //
  // The following produces a padded 2 digit number:
  //   buffer.append((char)(value / 10 + '0'));
  //   buffer.append((char)(value % 10 + '0'));
  //
  // Note that the fastest append to StringBuffer is a single char (used here).
  // Note that Integer.toString() is not called, the conversion is simply
  // taking the value and adding (mathematically) the ASCII value for '0'.
  // So, don't change this code! It works and is very fast.

  /**
   * FULL locale dependent date or time style.
   */
  public static final int FULL = DateFormat.FULL;
  /**
   * LONG locale dependent date or time style.
   */
  public static final int LONG = DateFormat.LONG;
  /**
   * MEDIUM locale dependent date or time style.
   */
  public static final int MEDIUM = DateFormat.MEDIUM;
  /**
   * SHORT locale dependent date or time style.
   */
  public static final int SHORT = DateFormat.SHORT;
  /**
   * Required for serialization support.
   *
   * @see Serializable
   */
  private static final long serialVersionUID = 1L;
  private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache =
      new ConcurrentHashMap<TimeZoneDisplayKey, String>(7);
  /**
   * The pattern.
   */
  private final String mPattern;
  /**
   * The time zone.
   */
  private final TimeZone mTimeZone;
  /**
   * The locale.
   */
  private final Locale mLocale;
  /**
   * The parsed rules.
   */
  private transient Rule[] mRules;

  // Constructor
  //-----------------------------------------------------------------------
  /**
   * The estimated maximum length.
   */
  private transient int mMaxLengthEstimate;

  /**
   * <p>Constructs a new FastDatePrinter.</p>
   *
   * @param pattern {@link java.text.SimpleDateFormat} compatible pattern
   * @param timeZone non-null time zone to use
   * @param locale non-null locale to use
   * @throws NullPointerException if pattern, timeZone, or locale is null.
   */
  protected FastDatePrinter(final String pattern, final TimeZone timeZone, final Locale locale) {
    mPattern = pattern;
    mTimeZone = timeZone;
    mLocale = locale;

    init();
  }

  // Parse the pattern
  //-----------------------------------------------------------------------

  /**
   * <p>Gets the time zone display name, using a cache for performance.</p>
   *
   * @param tz the zone to query
   * @param daylight true if daylight savings
   * @param style the style to use {@code TimeZone.LONG} or {@code TimeZone.SHORT}
   * @param locale the locale to use
   * @return the textual name of the time zone
   */
  static String getTimeZoneDisplay(final TimeZone tz, final boolean daylight, final int style,
      final Locale locale) {
    final TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
    String value = cTimeZoneDisplayCache.get(key);
    if (value == null) {
      // This is a very slow call, so cache the results.
      value = tz.getDisplayName(daylight, style, locale);
      final String prior = cTimeZoneDisplayCache.putIfAbsent(key, value);
      if (prior != null) {
        value = prior;
      }
    }
    return value;
  }

  /**
   * <p>Initializes the instance for first use.</p>
   */
  private void init() {
    final List<Rule> rulesList = parsePattern();
    mRules = rulesList.toArray(new Rule[rulesList.size()]);

    int len = 0;
    for (int i = mRules.length; --i >= 0; ) {
      len += mRules[i].estimateLength();
    }

    mMaxLengthEstimate = len;
  }

  /**
   * <p>Returns a list of Rules given a pattern.</p>
   *
   * @return a {@code List} of Rule objects
   * @throws IllegalArgumentException if pattern is invalid
   */
  protected List<Rule> parsePattern() {
    final DateFormatSymbols symbols = new DateFormatSymbols(mLocale);
    final List<Rule> rules = new ArrayList<Rule>();

    final String[] ERAs = symbols.getEras();
    final String[] months = symbols.getMonths();
    final String[] shortMonths = symbols.getShortMonths();
    final String[] weekdays = symbols.getWeekdays();
    final String[] shortWeekdays = symbols.getShortWeekdays();
    final String[] AmPmStrings = symbols.getAmPmStrings();

    final int length = mPattern.length();
    final int[] indexRef = new int[1];

    for (int i = 0; i < length; i++) {
      indexRef[0] = i;
      final String token = parseToken(mPattern, indexRef);
      i = indexRef[0];

      final int tokenLen = token.length();
      if (tokenLen == 0) {
        break;
      }

      Rule rule;
      final char c = token.charAt(0);

      switch (c) {
        case 'G': // era designator (text)
          rule = new TextField(Calendar.ERA, ERAs);
          break;
        case 'y': // year (number)
          if (tokenLen == 2) {
            rule = TwoDigitYearField.INSTANCE;
          } else {
            rule = selectNumberRule(Calendar.YEAR, tokenLen < 4 ? 4 : tokenLen);
          }
          break;
        case 'M': // month in year (text and number)
          if (tokenLen >= 4) {
            rule = new TextField(Calendar.MONTH, months);
          } else if (tokenLen == 3) {
            rule = new TextField(Calendar.MONTH, shortMonths);
          } else if (tokenLen == 2) {
            rule = TwoDigitMonthField.INSTANCE;
          } else {
            rule = UnpaddedMonthField.INSTANCE;
          }
          break;
        case 'd': // day in month (number)
          rule = selectNumberRule(Calendar.DAY_OF_MONTH, tokenLen);
          break;
        case 'h': // hour in am/pm (number, 1..12)
          rule = new TwelveHourField(selectNumberRule(Calendar.HOUR, tokenLen));
          break;
        case 'H': // hour in day (number, 0..23)
          rule = selectNumberRule(Calendar.HOUR_OF_DAY, tokenLen);
          break;
        case 'm': // minute in hour (number)
          rule = selectNumberRule(Calendar.MINUTE, tokenLen);
          break;
        case 's': // second in minute (number)
          rule = selectNumberRule(Calendar.SECOND, tokenLen);
          break;
        case 'S': // millisecond (number)
          rule = selectNumberRule(Calendar.MILLISECOND, tokenLen);
          break;
        case 'E': // day in week (text)
          rule = new TextField(Calendar.DAY_OF_WEEK, tokenLen < 4 ? shortWeekdays : weekdays);
          break;
        case 'D': // day in year (number)
          rule = selectNumberRule(Calendar.DAY_OF_YEAR, tokenLen);
          break;
        case 'F': // day of week in month (number)
          rule = selectNumberRule(Calendar.DAY_OF_WEEK_IN_MONTH, tokenLen);
          break;
        case 'w': // week in year (number)
          rule = selectNumberRule(Calendar.WEEK_OF_YEAR, tokenLen);
          break;
        case 'W': // week in month (number)
          rule = selectNumberRule(Calendar.WEEK_OF_MONTH, tokenLen);
          break;
        case 'a': // am/pm marker (text)
          rule = new TextField(Calendar.AM_PM, AmPmStrings);
          break;
        case 'k': // hour in day (1..24)
          rule = new TwentyFourHourField(selectNumberRule(Calendar.HOUR_OF_DAY, tokenLen));
          break;
        case 'K': // hour in am/pm (0..11)
          rule = selectNumberRule(Calendar.HOUR, tokenLen);
          break;
        case 'z': // time zone (text)
          if (tokenLen >= 4) {
            rule = new TimeZoneNameRule(mTimeZone, mLocale, TimeZone.LONG);
          } else {
            rule = new TimeZoneNameRule(mTimeZone, mLocale, TimeZone.SHORT);
          }
          break;
        case 'Z': // time zone (value)
          if (tokenLen == 1) {
            rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
          } else {
            rule = TimeZoneNumberRule.INSTANCE_COLON;
          }
          break;
        case '\'': // literal text
          final String sub = token.substring(1);
          if (sub.length() == 1) {
            rule = new CharacterLiteral(sub.charAt(0));
          } else {
            rule = new StringLiteral(sub);
          }
          break;
        default:
          throw new IllegalArgumentException("Illegal pattern component: " + token);
      }

      rules.add(rule);
    }

    return rules;
  }

  // Format methods
  //-----------------------------------------------------------------------

  /**
   * <p>Performs the parsing of tokens.</p>
   *
   * @param pattern the pattern
   * @param indexRef index references
   * @return parsed token
   */
  protected String parseToken(final String pattern, final int[] indexRef) {
    final StringBuilder buf = new StringBuilder();

    int i = indexRef[0];
    final int length = pattern.length();

    char c = pattern.charAt(i);
    if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
      // Scan a run of the same character, which indicates a time
      // pattern.
      buf.append(c);

      while (i + 1 < length) {
        final char peek = pattern.charAt(i + 1);
        if (peek == c) {
          buf.append(c);
          i++;
        } else {
          break;
        }
      }
    } else {
      // This will identify token as text.
      buf.append('\'');

      boolean inLiteral = false;

      for (; i < length; i++) {
        c = pattern.charAt(i);

        if (c == '\'') {
          if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
            // '' is treated as escaped '
            i++;
            buf.append(c);
          } else {
            inLiteral = !inLiteral;
          }
        } else if (!inLiteral && (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')) {
          i--;
          break;
        } else {
          buf.append(c);
        }
      }
    }

    indexRef[0] = i;
    return buf.toString();
  }

  /**
   * <p>Gets an appropriate rule for the padding required.</p>
   *
   * @param field the field to get a rule for
   * @param padding the padding required
   * @return a new rule with the correct padding
   */
  protected NumberRule selectNumberRule(final int field, final int padding) {
    switch (padding) {
      case 1:
        return new UnpaddedNumberField(field);
      case 2:
        return new TwoDigitNumberField(field);
      default:
        return new PaddedNumberField(field, padding);
    }
  }

  /**
   * <p>Formats a {@code Date}, {@code Calendar} or
   * {@code Long} (milliseconds) object.</p>
   *
   * @param obj the object to format
   * @param toAppendTo the buffer to append to
   * @param pos the position - ignored
   * @return the buffer passed in
   */
  @Override public StringBuffer format(final Object obj, final StringBuffer toAppendTo,
      final FieldPosition pos) {
    if (obj instanceof Date) {
      return format((Date) obj, toAppendTo);
    } else if (obj instanceof Calendar) {
      return format((Calendar) obj, toAppendTo);
    } else if (obj instanceof Long) {
      return format(((Long) obj).longValue(), toAppendTo);
    } else {
      throw new IllegalArgumentException(
          "Unknown class: " + (obj == null ? "<null>" : obj.getClass().getName()));
    }
  }

  /* (non-Javadoc)
   * @see org.apache.commons.lang3.time.DatePrinter#format(long)
   */
  @Override public String format(final long millis) {
    final Calendar c = newCalendar();  // hard code GregorianCalendar
    c.setTimeInMillis(millis);
    return applyRulesToString(c);
  }

  /**
   * Creates a String representation of the given Calendar by applying the rules of this printer to
   * it.
   *
   * @param c the Calender to apply the rules to.
   * @return a String representation of the given Calendar.
   */
  private String applyRulesToString(final Calendar c) {
    return applyRules(c, new StringBuffer(mMaxLengthEstimate)).toString();
  }

  /**
   * Creation method for ne calender instances.
   *
   * @return a new Calendar instance.
   */
  private GregorianCalendar newCalendar() {
    // hard code GregorianCalendar
    return new GregorianCalendar(mTimeZone, mLocale);
  }

  /* (non-Javadoc)
   * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Date)
   */
  @Override public String format(final Date date) {
    final Calendar c = newCalendar();  // hard code GregorianCalendar
    c.setTime(date);
    return applyRulesToString(c);
  }

  /* (non-Javadoc)
   * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Calendar)
   */
  @Override public String format(final Calendar calendar) {
    return format(calendar, new StringBuffer(mMaxLengthEstimate)).toString();
  }

  /* (non-Javadoc)
   * @see org.apache.commons.lang3.time.DatePrinter#format(long, java.lang.StringBuffer)
   */
  @Override public StringBuffer format(final long millis, final StringBuffer buf) {
    return format(new Date(millis), buf);
  }

  /* (non-Javadoc)
   * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Date, java.lang.StringBuffer)
   */
  @Override public StringBuffer format(final Date date, final StringBuffer buf) {
    final Calendar c = newCalendar();  // hard code GregorianCalendar
    c.setTime(date);
    return applyRules(c, buf);
  }

  /* (non-Javadoc)
   * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Calendar, java.lang.StringBuffer)
   */
  @Override public StringBuffer format(final Calendar calendar, final StringBuffer buf) {
    return applyRules(calendar, buf);
  }

  /**
   * <p>Performs the formatting by applying the rules to the
   * specified calendar.</p>
   *
   * @param calendar the calendar to format
   * @param buf the buffer to format into
   * @return the specified string buffer
   */
  protected StringBuffer applyRules(final Calendar calendar, final StringBuffer buf) {
    for (final Rule rule : mRules) {
      rule.appendTo(buf, calendar);
    }
    return buf;
  }

  // Accessors
  //-----------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#getPattern()
     */
  @Override public String getPattern() {
    return mPattern;
  }

  /* (non-Javadoc)
   * @see org.apache.commons.lang3.time.DatePrinter#getTimeZone()
   */
  @Override public TimeZone getTimeZone() {
    return mTimeZone;
  }

  // Basics
  //-----------------------------------------------------------------------

  /* (non-Javadoc)
   * @see org.apache.commons.lang3.time.DatePrinter#getLocale()
   */
  @Override public Locale getLocale() {
    return mLocale;
  }

  /**
   * <p>Gets an estimate for the maximum string length that the
   * formatter will produce.</p>
   * <p/>
   * <p>The actual formatted length will almost always be less than or
   * equal to this amount.</p>
   *
   * @return the maximum formatted length
   */
  public int getMaxLengthEstimate() {
    return mMaxLengthEstimate;
  }

  /**
   * <p>Compares two objects for equality.</p>
   *
   * @param obj the object to compare to
   * @return {@code true} if equal
   */
  @Override public boolean equals(final Object obj) {
    if (!(obj instanceof FastDatePrinter)) {
      return false;
    }
    final FastDatePrinter other = (FastDatePrinter) obj;
    return mPattern.equals(other.mPattern) && mTimeZone.equals(other.mTimeZone) && mLocale.equals(
        other.mLocale);
  }

  // Serializing
  //-----------------------------------------------------------------------

  /**
   * <p>Returns a hashcode compatible with equals.</p>
   *
   * @return a hashcode compatible with equals
   */
  @Override public int hashCode() {
    return mPattern.hashCode() + 13 * (mTimeZone.hashCode() + 13 * mLocale.hashCode());
  }

  // Rules
  //-----------------------------------------------------------------------

  /**
   * <p>Gets a debugging string version of this formatter.</p>
   *
   * @return a debugging string
   */
  @Override public String toString() {
    return "FastDatePrinter[" + mPattern + "," + mLocale + "," + mTimeZone.getID() + "]";
  }

  /**
   * Create the object after serialization. This implementation reinitializes the
   * transient properties.
   *
   * @param in ObjectInputStream from which the object is being deserialized.
   * @throws IOException if there is an IO issue.
   * @throws ClassNotFoundException if a class cannot be found.
   */
  private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    init();
  }

  /**
   * <p>Inner class defining a rule.</p>
   */
  private interface Rule {
    /**
     * Returns the estimated lentgh of the result.
     *
     * @return the estimated length
     */
    int estimateLength();

    /**
     * Appends the value of the specified calendar to the output buffer based on the rule
     * implementation.
     *
     * @param buffer the output buffer
     * @param calendar calendar to be appended
     */
    void appendTo(StringBuffer buffer, Calendar calendar);
  }

  /**
   * <p>Inner class defining a numeric rule.</p>
   */
  private interface NumberRule extends Rule {
    /**
     * Appends the specified value to the output buffer based on the rule implementation.
     *
     * @param buffer the output buffer
     * @param value the value to be appended
     */
    void appendTo(StringBuffer buffer, int value);
  }

  /**
   * <p>Inner class to output a constant single character.</p>
   */
  private static class CharacterLiteral implements Rule {
    private final char mValue;

    /**
     * Constructs a new instance of {@code CharacterLiteral}
     * to hold the specified value.
     *
     * @param value the character literal
     */
    CharacterLiteral(final char value) {
      mValue = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      buffer.append(mValue);
    }
  }

  /**
   * <p>Inner class to output a constant string.</p>
   */
  private static class StringLiteral implements Rule {
    private final String mValue;

    /**
     * Constructs a new instance of {@code StringLiteral}
     * to hold the specified value.
     *
     * @param value the string literal
     */
    StringLiteral(final String value) {
      mValue = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return mValue.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      buffer.append(mValue);
    }
  }

  /**
   * <p>Inner class to output one of a set of values.</p>
   */
  private static class TextField implements Rule {
    private final int mField;
    private final String[] mValues;

    /**
     * Constructs an instance of {@code TextField}
     * with the specified field and values.
     *
     * @param field the field
     * @param values the field values
     */
    TextField(final int field, final String[] values) {
      mField = field;
      mValues = values;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      int max = 0;
      for (int i = mValues.length; --i >= 0; ) {
        final int len = mValues[i].length();
        if (len > max) {
          max = len;
        }
      }
      return max;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      buffer.append(mValues[calendar.get(mField)]);
    }
  }

  /**
   * <p>Inner class to output an unpadded number.</p>
   */
  private static class UnpaddedNumberField implements NumberRule {
    private final int mField;

    /**
     * Constructs an instance of {@code UnpadedNumberField} with the specified field.
     *
     * @param field the field
     */
    UnpaddedNumberField(final int field) {
      mField = field;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return 4;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      appendTo(buffer, calendar.get(mField));
    }

    /**
     * {@inheritDoc}
     */
    @Override public final void appendTo(final StringBuffer buffer, final int value) {
      if (value < 10) {
        buffer.append((char) (value + '0'));
      } else if (value < 100) {
        buffer.append((char) (value / 10 + '0'));
        buffer.append((char) (value % 10 + '0'));
      } else {
        buffer.append(Integer.toString(value));
      }
    }
  }

  /**
   * <p>Inner class to output an unpadded month.</p>
   */
  private static class UnpaddedMonthField implements NumberRule {
    static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

    /**
     * Constructs an instance of {@code UnpaddedMonthField}.
     */
    UnpaddedMonthField() {
      super();
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      appendTo(buffer, calendar.get(Calendar.MONTH) + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override public final void appendTo(final StringBuffer buffer, final int value) {
      if (value < 10) {
        buffer.append((char) (value + '0'));
      } else {
        buffer.append((char) (value / 10 + '0'));
        buffer.append((char) (value % 10 + '0'));
      }
    }
  }

  /**
   * <p>Inner class to output a padded number.</p>
   */
  private static class PaddedNumberField implements NumberRule {
    private final int mField;
    private final int mSize;

    /**
     * Constructs an instance of {@code PaddedNumberField}.
     *
     * @param field the field
     * @param size size of the output field
     */
    PaddedNumberField(final int field, final int size) {
      if (size < 3) {
        // Should use UnpaddedNumberField or TwoDigitNumberField.
        throw new IllegalArgumentException();
      }
      mField = field;
      mSize = size;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return 4;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      appendTo(buffer, calendar.get(mField));
    }

    /**
     * {@inheritDoc}
     */
    @Override public final void appendTo(final StringBuffer buffer, final int value) {
      if (value < 100) {
        for (int i = mSize; --i >= 2; ) {
          buffer.append('0');
        }
        buffer.append((char) (value / 10 + '0'));
        buffer.append((char) (value % 10 + '0'));
      } else {
        int digits;
        if (value < 1000) {
          digits = 3;
        } else {
          digits = Integer.toString(value).length();
        }
        for (int i = mSize; --i >= digits; ) {
          buffer.append('0');
        }
        buffer.append(Integer.toString(value));
      }
    }
  }

  /**
   * <p>Inner class to output a two digit number.</p>
   */
  private static class TwoDigitNumberField implements NumberRule {
    private final int mField;

    /**
     * Constructs an instance of {@code TwoDigitNumberField} with the specified field.
     *
     * @param field the field
     */
    TwoDigitNumberField(final int field) {
      mField = field;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      appendTo(buffer, calendar.get(mField));
    }

    /**
     * {@inheritDoc}
     */
    @Override public final void appendTo(final StringBuffer buffer, final int value) {
      if (value < 100) {
        buffer.append((char) (value / 10 + '0'));
        buffer.append((char) (value % 10 + '0'));
      } else {
        buffer.append(Integer.toString(value));
      }
    }
  }

  /**
   * <p>Inner class to output a two digit year.</p>
   */
  private static class TwoDigitYearField implements NumberRule {
    static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

    /**
     * Constructs an instance of {@code TwoDigitYearField}.
     */
    TwoDigitYearField() {
      super();
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      appendTo(buffer, calendar.get(Calendar.YEAR) % 100);
    }

    /**
     * {@inheritDoc}
     */
    @Override public final void appendTo(final StringBuffer buffer, final int value) {
      buffer.append((char) (value / 10 + '0'));
      buffer.append((char) (value % 10 + '0'));
    }
  }

  /**
   * <p>Inner class to output a two digit month.</p>
   */
  private static class TwoDigitMonthField implements NumberRule {
    static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

    /**
     * Constructs an instance of {@code TwoDigitMonthField}.
     */
    TwoDigitMonthField() {
      super();
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      appendTo(buffer, calendar.get(Calendar.MONTH) + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override public final void appendTo(final StringBuffer buffer, final int value) {
      buffer.append((char) (value / 10 + '0'));
      buffer.append((char) (value % 10 + '0'));
    }
  }

  //-----------------------------------------------------------------------

  /**
   * <p>Inner class to output the twelve hour field.</p>
   */
  private static class TwelveHourField implements NumberRule {
    private final NumberRule mRule;

    /**
     * Constructs an instance of {@code TwelveHourField} with the specified
     * {@code NumberRule}.
     *
     * @param rule the rule
     */
    TwelveHourField(final NumberRule rule) {
      mRule = rule;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return mRule.estimateLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      int value = calendar.get(Calendar.HOUR);
      if (value == 0) {
        value = calendar.getLeastMaximum(Calendar.HOUR) + 1;
      }
      mRule.appendTo(buffer, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final int value) {
      mRule.appendTo(buffer, value);
    }
  }

  /**
   * <p>Inner class to output the twenty four hour field.</p>
   */
  private static class TwentyFourHourField implements NumberRule {
    private final NumberRule mRule;

    /**
     * Constructs an instance of {@code TwentyFourHourField} with the specified
     * {@code NumberRule}.
     *
     * @param rule the rule
     */
    TwentyFourHourField(final NumberRule rule) {
      mRule = rule;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return mRule.estimateLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      int value = calendar.get(Calendar.HOUR_OF_DAY);
      if (value == 0) {
        value = calendar.getMaximum(Calendar.HOUR_OF_DAY) + 1;
      }
      mRule.appendTo(buffer, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final int value) {
      mRule.appendTo(buffer, value);
    }
  }

  /**
   * <p>Inner class to output a time zone name.</p>
   */
  private static class TimeZoneNameRule implements Rule {
    private final Locale mLocale;
    private final int mStyle;
    private final String mStandard;
    private final String mDaylight;

    /**
     * Constructs an instance of {@code TimeZoneNameRule} with the specified properties.
     *
     * @param timeZone the time zone
     * @param locale the locale
     * @param style the style
     */
    TimeZoneNameRule(final TimeZone timeZone, final Locale locale, final int style) {
      mLocale = locale;
      mStyle = style;

      mStandard = getTimeZoneDisplay(timeZone, false, style, locale);
      mDaylight = getTimeZoneDisplay(timeZone, true, style, locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      // We have no access to the Calendar object that will be passed to
      // appendTo so base estimate on the TimeZone passed to the
      // constructor
      return Math.max(mStandard.length(), mDaylight.length());
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      final TimeZone zone = calendar.getTimeZone();
      //noinspection WrongConstant
      if (zone.useDaylightTime() && calendar.get(Calendar.DST_OFFSET) != 0) {
        buffer.append(getTimeZoneDisplay(zone, true, mStyle, mLocale));
      } else {
        buffer.append(getTimeZoneDisplay(zone, false, mStyle, mLocale));
      }
    }
  }

  /**
   * <p>Inner class to output a time zone as a number {@code +/-HHMM}
   * or {@code +/-HH:MM}.</p>
   */
  private static class TimeZoneNumberRule implements Rule {
    static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
    static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);

    final boolean mColon;

    /**
     * Constructs an instance of {@code TimeZoneNumberRule} with the specified properties.
     *
     * @param colon add colon between HH and MM in the output if {@code true}
     */
    TimeZoneNumberRule(final boolean colon) {
      mColon = colon;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int estimateLength() {
      return 5;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void appendTo(final StringBuffer buffer, final Calendar calendar) {
      int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);

      if (offset < 0) {
        buffer.append('-');
        offset = -offset;
      } else {
        buffer.append('+');
      }

      final int hours = offset / (60 * 60 * 1000);
      buffer.append((char) (hours / 10 + '0'));
      buffer.append((char) (hours % 10 + '0'));

      if (mColon) {
        buffer.append(':');
      }

      final int minutes = offset / (60 * 1000) - 60 * hours;
      buffer.append((char) (minutes / 10 + '0'));
      buffer.append((char) (minutes % 10 + '0'));
    }
  }

  // ----------------------------------------------------------------------

  /**
   * <p>Inner class that acts as a compound key for time zone names.</p>
   */
  private static class TimeZoneDisplayKey {
    private final TimeZone mTimeZone;
    private final int mStyle;
    private final Locale mLocale;

    /**
     * Constructs an instance of {@code TimeZoneDisplayKey} with the specified properties.
     *
     * @param timeZone the time zone
     * @param daylight adjust the style for daylight saving time if {@code true}
     * @param style the timezone style
     * @param locale the timezone locale
     */
    TimeZoneDisplayKey(final TimeZone timeZone, final boolean daylight, final int style,
        final Locale locale) {
      mTimeZone = timeZone;
      if (daylight) {
        mStyle = style | 0x80000000;
      } else {
        mStyle = style;
      }
      mLocale = locale;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int hashCode() {
      return (mStyle * 31 + mLocale.hashCode()) * 31 + mTimeZone.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof TimeZoneDisplayKey) {
        final TimeZoneDisplayKey other = (TimeZoneDisplayKey) obj;
        return mTimeZone.equals(other.mTimeZone) && mStyle == other.mStyle && mLocale.equals(
            other.mLocale);
      }
      return false;
    }
  }
}
