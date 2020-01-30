package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.util.regex.Pattern;

/**
 * Allows string based assertions
 * @author Mike Reiche
 */
public interface StringAssertion<T> extends QuantityAssertion<T>
{
//    default boolean is(String expected) {
//        return is(expected, null);
//    }
//    boolean is(String expected, String message);

    default boolean contains(String expected) {
        return contains(expected, null);
    }
    boolean contains(String expected, String message);

    default boolean containsNot(String expected) {
        return containsNot(expected, null);
    }
    boolean containsNot(String expected, String message);

    default boolean beginsWith(String expected) {
        return beginsWith(expected, null);
    }
    boolean beginsWith(String expected, String message);

    default boolean endsWith(String expected) {
        return endsWith(expected, null);
    }
    boolean endsWith(String expected, String message);

    default PatternAssertion matches(String pattern) {
        return matches(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE));
    }
    PatternAssertion matches(Pattern pattern);

    QuantityAssertion<Integer> length();
}
