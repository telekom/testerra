package eu.tsystems.mms.tic.testframework.testing;

/**
 * Allows changes of the test flow during runtime.
 * @author Mike Reiche
 */
public interface TestController {
    void collectAssertions(Runnable runnable);
    TestController collectAssertions();
    void nonFunctionalAssertions(Runnable runnable);
    TestController nonFunctionalAssertions();
    void withTimeout(int seconds, Runnable runnable);
    TestController withTimeout(int seconds);
    void retryFor(int seconds, Runnable runnable);
    TestController retryFor(int seconds);
}
