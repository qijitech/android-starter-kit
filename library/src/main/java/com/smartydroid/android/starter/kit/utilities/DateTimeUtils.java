/**
 * Created by YuGang Yang on October 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.utilities;

import android.text.format.DateUtils;
import java.util.Date;

import static android.text.format.DateUtils.FORMAT_NUMERIC_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_YEAR;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

public final class DateTimeUtils {

  /**
   * 获取与当前时间相隔时间
   */
  public static CharSequence getRelativeTime(final long time) {
    return getRelativeTime(new Date(time * 1000));
  }

  public static CharSequence getRelativeTime(final Date date) {
    long now = System.currentTimeMillis();
    if (Math.abs(now - date.getTime()) > 60000) {
      return DateUtils.getRelativeTimeSpanString(date.getTime(), now, MINUTE_IN_MILLIS,
          FORMAT_SHOW_DATE | FORMAT_SHOW_YEAR | FORMAT_NUMERIC_DATE);
    }
    return "刚刚";
  }

  private DateTimeUtils() {
  }
}
