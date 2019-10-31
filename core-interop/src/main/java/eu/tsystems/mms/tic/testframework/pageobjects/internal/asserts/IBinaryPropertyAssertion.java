package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IBinaryPropertyAssertion<T> extends ActualProperty<T> {
    IBinaryPropertyAssertion<T> isTrue();
    IBinaryPropertyAssertion<T> isFalse();
}
