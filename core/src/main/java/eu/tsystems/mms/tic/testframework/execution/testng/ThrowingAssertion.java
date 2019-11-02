package eu.tsystems.mms.tic.testframework.execution.testng;

/**
 * Throws {@link AssertionError} on failed assertion
 */
public class ThrowingAssertion extends AbstractAssertion implements InstantAssertion {
    @Override
    public void fail(String message, Throwable cause) {
        throw new AssertionError(message, cause);
    }
    @Override
    public void fail(AssertionError error) {
        throw error;
    }
}
