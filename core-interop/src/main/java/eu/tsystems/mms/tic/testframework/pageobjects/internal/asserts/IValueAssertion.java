package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IValueAssertion<T> extends IQuantifiedAssertion<T>
{
    T actual();
    IValueAssertion<T> contains(final String expected);
    IValueAssertion<T> containsNot(final String expected);
    IValueAssertion<T> beginsWith(final String expected);
    IValueAssertion<T> endsWith(final String expected);

    @Override
    IValueAssertion<T> nonFunctional();
}
