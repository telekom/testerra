package eu.tsystems.mms.tic.testframework.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultFormatter implements Formatter {
    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT());

    @Override
    public String logTime(Date date) {
        return SIMPLE_DATE_FORMAT.format(date);
    }
}
