package eu.tsystems.mms.tic.testframework.internal.asserts;

public interface ObjectAssertion<TYPE> extends BinaryAssertion<TYPE> {
    default boolean is(Object expected) {
        return is(expected, null);
    }
    boolean is(Object expected, String subject);

    default boolean isNot(Object expected) {
        return isNot(expected, null);
    }
    boolean isNot(Object expected, String subject);
}