package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Adds {@link SessionContext} and {@link MethodContext} ids to log message
 * @author Mike Reiche
 */
public class ContextLogFormatter implements LogFormatter {
    private final Layout layout = new PatternLayout("%d{dd.MM.yyyy HH:mm:ss.SSS} [%t] [%-5p]%X{ids}: %c{2} - %m");

    @Override
    public String format(LoggingEvent event) {
        StringBuilder sb = new StringBuilder();

        // enhance with method context id
        SessionContext currentSessionContext = ExecutionContextController.getCurrentSessionContext();
        if (currentSessionContext != null) {
            sb.append("[SCID:").append(currentSessionContext.id).append("]");
        }

        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            sb.append("[MCID:").append(methodContext.id).append("]");
        }
        event.setProperty("ids", sb.toString());
        return layout.format(event);
    }
}
