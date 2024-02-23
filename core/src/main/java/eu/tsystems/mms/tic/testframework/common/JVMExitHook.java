package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

/**
 * triggers ExecutionAbortEvent in case of none existing report
 * missing report is indicator of unexpected system.exit and reason to trigger report generation
 */
public class JVMExitHook extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(JVMExitHook.class);

    public void run(){

        final IExecutionContextController contextController = Testerra.getInjector().getInstance(IExecutionContextController.class);
        final ExecutionContext executionContext = contextController.getExecutionContext();

        // trigger report generation only when no report exists, as shutdown hook is always executed no matter if normal finish or abortion
        if (!executionContext.getReportModelGenerated()) {
            LOGGER.info("Triggering report generation after unexpected abortion of test execution.");
            final EventBus eventBus = Testerra.getEventBus();
            eventBus.post(new ExecutionAbortEvent());
        }
    }
}
