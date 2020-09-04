package eu.tsystems.mms.tic.testframework.hook;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.listeners.GenerateXmlReportListener;
import eu.tsystems.mms.tic.testframework.report.ContextLogFormatter;
import eu.tsystems.mms.tic.testframework.report.ReportLogFormatter;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;

public class ReportModelHook implements ModuleHook {

    @Override
    public void init() {
        EventBus eventBus = TesterraListener.getEventBus();
        eventBus.register(new GenerateXmlReportListener());

        // Enable report formatter here
        TesterraCommons.getTesterraLogger().setFormatter(new ReportLogFormatter());
    }

    @Override
    public void terminate() {
        // Reset to default logger
        TesterraCommons.getTesterraLogger().setFormatter(new ContextLogFormatter());
    }
}
