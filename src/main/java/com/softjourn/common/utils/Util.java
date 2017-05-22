package com.softjourn.common.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Util {

    public static String instantToRFC_1123_DATE_TIME(Instant instant, ZoneId zoneId) {
        return DateTimeFormatter
                .RFC_1123_DATE_TIME
                .withLocale(Locale.getDefault())
                .withZone(zoneId)
                .format(instant);
    }

}
