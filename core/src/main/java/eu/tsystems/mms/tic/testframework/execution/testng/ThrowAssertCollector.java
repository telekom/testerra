package eu.tsystems.mms.tic.testframework.execution.testng;

public class ThrowAssertCollector implements IAssertCollector {
    @Override
    public void fail(String message, Throwable realCause) {
        AssertionError ae = new AssertionError(message);
        ae.initCause(realCause);
        throw ae;
    }

    @Override
    public void fail(String message) {
        fail(message, null);
    }
}
