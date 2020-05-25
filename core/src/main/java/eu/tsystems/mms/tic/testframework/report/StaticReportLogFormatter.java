package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.utils.LoggingDispatcher;
import org.apache.logging.log4j.core.LogEvent;

/**
 * Adds log events as {@link LogMessage} to {@link TestStep}
 * This is required for the local static report only.
 * @author Mike Reiche
 */
public class StaticReportLogFormatter extends ContextLogFormatter {
    @Override
    public String format(LogEvent event) {
        appendForReport(event);
        return super.format(event);
    }
    /**
     * Appends a LoggingEvent to the HTML Report using Reporter.log().
     *
     * @param event The event to be logged.
     */
    private TestStepAction appendForReport(final LogEvent event) {

        /**
         * We dont log any messages from steps package,
         * because logs from {@link TestStep} also triggering logs by {@link LoggingDispatcher}
         * which may result in a callstack loop.
         */
        if (event.getLoggerName().startsWith(TestStep.class.getPackage().getName())) {
            return null;
        }

        /*
        Throwable toggle
         */
        //final String htmlOrigMessage = StringUtils.prepareStringForHTML(origMessage);
//        if (event.getThrowableInformation() != null) {
//            String[] throwableStrRep = event.getThrowableInformation().getThrowableStrRep();
//
//            if (throwableStrRep != null) {
//
//                /*
//                 * Reformat log
//                 */
//                String id = System.currentTimeMillis() + "";
//                out = "<a href=\"javascript:toggleElement('exception-" + id + "')\">"
//                                + htmlOrigMessage
//                                + "</a><br/><div id='exception-" + id + "' class='stackTrace'>";
//
//                for (String line : throwableStrRep) {
//                    line = StringUtils.prepareStringForHTML(line);
//                    out += line + "<br/>";
//                }
//
//                out += "</div>";
//            }
//            else {
//                out = htmlOrigMessage;
//            }
//        }
//        else {
//            out = htmlOrigMessage;
//        }

        /*
         * Add log message.
         */
        LogMessage logMessage = new LogMessage(event);
        return LoggingDispatcher.addLogMessage(logMessage);
    }
}
