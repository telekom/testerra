package eu.tsystems.mms.tic.testframework.hook;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.listener.GenerateReportModelListener;
import eu.tsystems.mms.tic.testframework.report.ReportLogFormatter;

public class ReportModelHook implements ModuleHook {

    @Override
    public void init() {
        TesterraEventService.addListener(new GenerateReportModelListener());

        // Enable report formatter here
        TesterraCommons.getTesterraLogger().setFormatter(new ReportLogFormatter());
    }

    @Override
    public void terminate() {

    }
}
