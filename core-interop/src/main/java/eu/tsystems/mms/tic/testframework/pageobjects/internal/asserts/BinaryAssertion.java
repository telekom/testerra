package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

/**
 * Allows boolean or string assertions
 * @author Mike Reiche
 */
public interface BinaryAssertion<T> extends ActualProperty<T> {
    default boolean is(boolean expected) {
        return is(expected, null);
    }

    boolean is(boolean expected, String failMessage);
}
