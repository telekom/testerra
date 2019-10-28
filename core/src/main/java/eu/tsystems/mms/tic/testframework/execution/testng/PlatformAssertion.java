package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.internal.AssertionsCollector;

/**
 * Collects assertions without throwing them
 */
public class PlatformAssertion extends AbstractAssertion implements IAssertion {

    private final AssertionsCollector assertionsCollector = TesterraCommons.ioc().getInstance(AssertionsCollector.class);

    @Override
    public void fail(String message, Throwable realCause) {
        AssertionError ae = new AssertionError(message);
        ae.initCause(realCause);
        assertionsCollector.store(ae);
    }
}
