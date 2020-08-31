package eu.tsystems.mms.tic.testframework.events;

/**
 * Called at the abortion of test run by unclear circumstances.
 */
public class ExecutionAbortEvent {
    public interface Listener {
        void onExecutionAbort(ExecutionAbortEvent event);
    }
}
