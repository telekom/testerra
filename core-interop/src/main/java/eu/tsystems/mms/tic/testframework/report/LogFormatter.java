package eu.tsystems.mms.tic.testframework.report;

import org.apache.log4j.spi.LoggingEvent;

public interface LogFormatter {
    String format(final LoggingEvent event);
}
