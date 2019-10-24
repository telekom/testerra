package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IAssertableValue<T, E> extends
    IAssertableBinaryValue<T, E>,
    IAssertableQuantifiedValue<T, E>
{
    T actual();
    E contains(final String expected);
    E containsNot(final String expected);
    E beginsWith(final String expected);
    E endsWith(final String expected);

    @Override
    IAssertableValue<T, E> nonFunctional();
}
