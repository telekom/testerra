package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class DefaultLogFormatter implements LogFormatter {
    private final Formatter formatter = Testerra.injector.getInstance(Formatter.class);
    private final Layout layout ;

    {
        PatternLayout.Builder builder = PatternLayout.newBuilder();
        builder.withPattern("%d{"+formatter.DATE_TIME_FORMAT()+"} [%t][%p]: %c{2} - %m");
        layout = builder.build();
    }

    @Override
    public String format(LogEvent event) {
        return new String(layout.toByteArray(event));
    }
}
