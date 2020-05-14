package eu.tsystems.mms.tic.testframework.report;

import org.apache.logging.log4j.core.LogEvent;

public interface LogFormatter {
    String format(final LogEvent event);
}
