package eu.tsystems.mms.tic.testframework.execution.testng;

/**
 * Throws exceptions
 */
public class ThrowedAssertion extends AbstractAssertion implements InstantAssertion {
    @Override
    public void fail(String message, Throwable realCause) {
        AssertionError ae = new AssertionError(message);
        ae.initCause(realCause);
        throw ae;
    }
}
