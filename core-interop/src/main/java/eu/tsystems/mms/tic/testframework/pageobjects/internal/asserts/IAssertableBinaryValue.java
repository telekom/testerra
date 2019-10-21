package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IAssertableBinaryValue<T> {
    T actual();
    IAssertableBinaryValue isTrue();
    IAssertableBinaryValue isFalse();
}
