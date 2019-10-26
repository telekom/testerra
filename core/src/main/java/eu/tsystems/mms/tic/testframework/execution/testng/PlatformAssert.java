package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.internal.CollectedAssertions;

public class PlatformAssert extends DefaultAssert implements IAssert {
    @Override
    public void fail(String message, Throwable realCause) {
        AssertionError ae = new AssertionError(message);
        ae.initCause(realCause);
        CollectedAssertions.store(ae);
    }
}
