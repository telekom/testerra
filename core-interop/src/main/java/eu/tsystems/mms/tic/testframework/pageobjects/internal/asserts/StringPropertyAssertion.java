package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.util.regex.Pattern;

/**
 * Allows string based assertions
 * @author Mike Reiche
 */
public interface StringPropertyAssertion<T> extends QuantifiedPropertyAssertion<T>
{
    T getActual();
    boolean is(String expected);
    boolean contains(String expected);
    boolean containsNot(String expected);
    boolean beginsWith(String expected);
    boolean endsWith(String expected);
    PatternAssertion matches(Pattern pattern);
    default PatternAssertion matches(String pattern) {
        return matches(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE));
    }
    QuantifiedPropertyAssertion<Integer> length();
    StringPropertyAssertion<T> perhaps();
}
