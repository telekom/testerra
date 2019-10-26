package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IAssertionProvider<T> {
    T actual();
    Object subject();
    void failed();
    void passed();
}
