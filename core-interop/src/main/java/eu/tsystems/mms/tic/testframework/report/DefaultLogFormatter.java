package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class DefaultLogFormatter implements LogFormatter {
    private final Formatter formatter = Testerra.injector.getInstance(Formatter.class);
    private final Layout layout = new PatternLayout("%d{"+formatter.DATE_TIME_FORMAT()+"} [%t] [%-5p]: %c{2} - %m");

    @Override
    public String format(LoggingEvent event) {
        return layout.format(event);
    }
}
