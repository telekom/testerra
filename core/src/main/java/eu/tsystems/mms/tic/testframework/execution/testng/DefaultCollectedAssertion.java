package eu.tsystems.mms.tic.testframework.execution.testng;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.internal.AssertionsCollector;

/**
 * Collects {@link AssertionError} on failed assertion
 */
public class DefaultCollectedAssertion extends AbstractAssertion implements CollectedAssertion {

    private final AssertionsCollector collector;

    @Inject
    DefaultCollectedAssertion(AssertionsCollector collector) {
        this.collector = collector;
    }

    @Override
    public void fail(String message, Throwable realCause) {
        AssertionError ae = new AssertionError(message);
        ae.initCause(realCause);
        collector.store(ae);
    }
}
