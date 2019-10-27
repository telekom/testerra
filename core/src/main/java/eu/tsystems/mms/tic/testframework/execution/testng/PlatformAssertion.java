package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.internal.CollectedAssertions;

/**
 * Collects assertions without throwing them
 */
public class PlatformAssertion extends AbstractAssertion implements IAssertion {
    @Override
    public void fail(String message, Throwable realCause) {
        AssertionError ae = new AssertionError(message);
        ae.initCause(realCause);
        CollectedAssertions.store(ae);
    }
}
