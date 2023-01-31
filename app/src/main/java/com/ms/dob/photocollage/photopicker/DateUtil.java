package com.ms.dob.photocollage.photopicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateUtil {
  public static String formatDate(long time) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy year MM month dd day", Locale.CHINA);
    return simpleDateFormat.format(new Date(time));
  }
}
