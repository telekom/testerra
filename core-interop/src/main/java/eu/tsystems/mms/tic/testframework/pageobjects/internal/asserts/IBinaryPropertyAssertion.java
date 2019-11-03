package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

/**
 * Allows boolean or string assertions
 * @author Mike Reiche
 */
public interface IBinaryPropertyAssertion<T> extends ActualProperty<T> {
    /**
     * The property is boolean true or a string like 'true', 'on', '1' or 'no'
     */
    IBinaryPropertyAssertion<T> isTrue();

    /**
     * The property is boolean false or a string like 'false', 'off', '0' or 'no'
     */
    IBinaryPropertyAssertion<T> isFalse();
}
