package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IAssertableBinaryValue<T, E> extends INonFunctionalAssertableValue {
    T actual();
    E isTrue();
    E isFalse();
}
