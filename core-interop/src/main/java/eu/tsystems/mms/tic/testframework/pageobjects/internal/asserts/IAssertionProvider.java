package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IAssertionProvider<T> extends IActualProperty<T> {
    Object subject();
    void failed();
}
