package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IBinaryAssertion<T> extends INonFunctionalAssertion, IAssertion<T> {
    IBinaryAssertion<T> isTrue();
    IBinaryAssertion<T> isFalse();
}
