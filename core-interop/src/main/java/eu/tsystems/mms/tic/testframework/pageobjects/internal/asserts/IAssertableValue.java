package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IAssertableValue<T> extends IAssertableBinaryValue, IAssertableQuantifiedValue {
    T actual();
    IAssertableValue contains(final String expected);
    IAssertableValue containsNot(final String expected);
    IAssertableValue beginsWith(final String expected);
    IAssertableValue endsWith(final String expected);
}
