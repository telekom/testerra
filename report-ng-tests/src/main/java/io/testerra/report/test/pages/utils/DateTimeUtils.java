package io.testerra.report.test.pages.utils;

import java.text.SimpleDateFormat;

public class DateTimeUtils {

    public static final SimpleDateFormat formatMilliSeconds = new SimpleDateFormat("SSS'ms'");
    public static final SimpleDateFormat formatSeconds = new SimpleDateFormat("s's' SSS'ms'");
    public static final SimpleDateFormat formatMinutes = new SimpleDateFormat("mm'mins' s's' SSS'ms'");
    public static final SimpleDateFormat formatHours = new SimpleDateFormat("H'h' mm'mins' s's' SSS'ms'");


    public static boolean verifyDateTimeString(final String dateTimeString) {
       return verifyFormat(dateTimeString);
    }

    private static boolean verifyFormat(final String dateTimeString) {
        if (isParsable(dateTimeString, DateTimeUtils.formatMilliSeconds)) {
            return true;
        }

        if (isParsable(dateTimeString, DateTimeUtils.formatSeconds)) {
            return true;
        }

        if (isParsable(dateTimeString, DateTimeUtils.formatMinutes)) {
            return true;
        }

        return isParsable(dateTimeString, DateTimeUtils.formatHours);
    }

    public static boolean isParsable(final String dateTimeString, final SimpleDateFormat format) {
        try {
            format.parse(dateTimeString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
