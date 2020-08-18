package eu.tsystems.mms.tic.testframework.testing;

/**
 * Allows changes of the test flow during runtime.
 * @author Mike Reiche
 */
public interface TestController {
    void collectAssertions(Runnable runnable);
    void nonFunctionalAssertions(Runnable runnable);
    void elementTimeout(int seconds, Runnable runnable);
}
