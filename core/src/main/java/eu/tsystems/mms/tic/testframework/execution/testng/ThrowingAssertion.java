package eu.tsystems.mms.tic.testframework.execution.testng;

/**
 * Throws {@link AssertionError} on failed assertion
 */
public class ThrowingAssertion extends AbstractAssertion implements InstantAssertion {
    @Override
    public void fail(String message, Throwable realCause) {
        AssertionError ae = new AssertionError(message);
        ae.initCause(realCause);
        throw ae;
    }
}
