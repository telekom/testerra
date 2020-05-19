package eu.tsystems.mms.tic.testframework.report;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class DefaultLogFormatter implements LogFormatter {

    private final Layout layout;

    {
        PatternLayout.Builder builder = PatternLayout.newBuilder();
        builder.withPattern("%d{dd.MM.yyyy HH:mm:ss.SSS} [%t][%p]: %c{2} - %m");
        layout = builder.build();
    }

    @Override
    public String format(LogEvent event) {
        return new String(layout.toByteArray(event));
    }
}
