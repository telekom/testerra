package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IBinaryPropertyAssertion<T> extends IActualProperty<T>, INonFunctionalPropertyAssertion {
    IBinaryPropertyAssertion<T> isTrue();
    IBinaryPropertyAssertion<T> isFalse();
}
