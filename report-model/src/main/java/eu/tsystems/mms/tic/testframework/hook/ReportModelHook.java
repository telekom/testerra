package eu.tsystems.mms.tic.testframework.hook;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.listeners.GenerateXmlReportListener;
import eu.tsystems.mms.tic.testframework.report.TestStepLogAppender;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import org.apache.logging.log4j.core.Appender;

public class ReportModelHook implements ModuleHook {

    private Appender reportLogAppender;

    @Override
    public void init() {
        EventBus eventBus = TesterraListener.getEventBus();
        eventBus.register(new GenerateXmlReportListener());

        // Enable report formatter here
        this.reportLogAppender = new TestStepLogAppender();
        this.reportLogAppender.start();
        TesterraListener.getLoggerContext().getRootLogger().addAppender(this.reportLogAppender);
    }

    @Override
    public void terminate() {
        // Reset to default logger
        TesterraListener.getLoggerContext().getRootLogger().removeAppender(this.reportLogAppender);
    }
}
