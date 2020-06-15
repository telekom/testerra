package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Adds {@link SessionContext} and {@link MethodContext} ids to log message
 * @author Mike Reiche
 */
public class ContextLogFormatter implements LogFormatter {
    private final Layout layout;

    {
        PatternLayout.Builder builder = PatternLayout.newBuilder();
        builder.withPattern("%d{dd.MM.yyyy HH:mm:ss.SSS} [%t][%p]%X{ids}: %c{2} - %m");
        layout = builder.build();
    }

    @Override
    public String format(LogEvent event) {
        StringBuilder sb = new StringBuilder();

        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            sb.append("[MCID:").append(methodContext.id).append("]");
        }

        // enhance with method context id
        SessionContext currentSessionContext = ExecutionContextController.getCurrentSessionContext();
        if (currentSessionContext != null) {
            sb.append("[SCID:").append(currentSessionContext.id).append("]");
        }

        try (final CloseableThreadContext.Instance ctc = CloseableThreadContext.put("ids", sb.toString())) {
            return new String(layout.toByteArray(event));
        }
    }
}
