package eu.tsystems.mms.tic.testframework.execution.testng;

public interface FunctionalAssertFactory {
    IAssert create();
    IAssert create(final boolean collectable);
}
