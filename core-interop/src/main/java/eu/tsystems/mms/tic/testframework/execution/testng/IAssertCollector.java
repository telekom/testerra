package eu.tsystems.mms.tic.testframework.execution.testng;

public interface IAssertCollector {
    void fail(String message, Throwable realCause);
    void fail(String message);
}
