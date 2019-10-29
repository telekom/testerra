package eu.tsystems.mms.tic.testframework.execution.testng;

/**
 * Doesn't throw any exception neither storing them.
 * It should do absolutely nothing on failure.
 */
public class SilentAssertion extends AbstractAssertion implements TestAssertion {
    @Override
    public void fail(Throwable cause) {

    }
}
