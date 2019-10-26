package eu.tsystems.mms.tic.testframework.execution.testng;

public interface IAssert {
    void fail(String message, Throwable realCause);
    void fail(String message);
}
