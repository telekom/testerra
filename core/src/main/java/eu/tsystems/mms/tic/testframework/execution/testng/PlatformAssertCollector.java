package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.internal.CollectedAssertions;

public class PlatformAssertCollector implements IAssertCollector {
    @Override
    public void fail(String message, Throwable realCause) {
        AssertionError ae = new AssertionError(message);
        ae.initCause(realCause);
        CollectedAssertions.store(ae);
    }

    @Override
    public void fail(String message) {
        fail(message, null);
    }
}
