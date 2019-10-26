package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IBinaryAssertion<T> extends IAssertion<T>, INonFunctionalAssertion {
    IBinaryAssertion<T> isTrue();
    IBinaryAssertion<T> isFalse();
}
