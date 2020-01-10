package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

/**
 * Allows boolean or string assertions
 * @author Mike Reiche
 */
public interface BinaryAssertion<T> extends ActualProperty<T> {

    default boolean isTrue() {
        return isTrue(null);
    }

    default boolean isFalse() {
        return isFalse(null);
    }

    /**
     * The property is boolean true or a string like 'true', 'on', '1' or 'no'
     */
    boolean isTrue(String message);

    /**
     * The property is boolean false or a string like 'false', 'off', '0' or 'no'
     */
    boolean isFalse(String message);
}
