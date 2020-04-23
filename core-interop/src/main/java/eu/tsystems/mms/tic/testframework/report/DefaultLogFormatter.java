package eu.tsystems.mms.tic.testframework.report;

import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class DefaultLogFormatter implements LogFormatter {
    private final Layout layout = new PatternLayout("%d{dd.MM.yyyy HH:mm:ss.SSS} [%t][%p]: %c{2} - %m");

    @Override
    public String format(LoggingEvent event) {
        return layout.format(event);
    }
}
