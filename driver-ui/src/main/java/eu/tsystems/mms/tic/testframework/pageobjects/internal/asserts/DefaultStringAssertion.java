package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.Formatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultStringAssertion<T> extends DefaultQuantityAssertion<T> implements StringAssertion<T>, Loggable {

    private final static Formatter formatter = Testerra.injector.getInstance(Formatter.class);

    public DefaultStringAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }
//
//    @Override
//    public boolean is(String expected, String message) {
//        return super.is(expected, message);
//    }
//
//    @Override
//    public boolean is(String expected, String message) {
//        return super.is(expected, message);
//    }

    @Override
    public boolean contains(String expected, String message) {
        return testTimer(t -> instantAssertion.assertContains((String)provider.getActual(), expected, new Assertion.Message(message, traceSubjectString())));
    }

    @Override
    public boolean containsNot(String expected, String message) {
        return testTimer(t -> instantAssertion.assertContainsNot((String)provider.getActual(), expected, new Assertion.Message(message, traceSubjectString())));
    }

    @Override
    public boolean beginsWith(String expected, String message) {
        return testTimer(t -> instantAssertion.assertBeginsWith(provider.getActual(), expected, new Assertion.Message(message, traceSubjectString())));
    }

    @Override
    public boolean endsWith(String expected, String message) {
        return testTimer(t -> instantAssertion.assertEndsWith(provider.getActual(), expected, new Assertion.Message(message, traceSubjectString())));
    }

    @Override
    public PatternAssertion matches(Pattern pattern) {
        return propertyAssertionFactory.create(DefaultPatternAssertion.class, this, new AssertionProvider<Matcher>() {
            @Override
            public Matcher getActual() {
                return pattern.matcher(provider.getActual().toString());
            }

            @Override
            public String getSubject() {
                return String.format("\"%s\".matches(pattern: %s)", getStringSubject(), pattern.toString());
            }
        });
    }

    private String getStringSubject() {
        return formatter.cutString(provider.getActual().toString(), 30);
    }

    @Override
    public QuantityAssertion<Integer> length() {
        return propertyAssertionFactory.create(DefaultQuantityAssertion.class, this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                return provider.getActual().toString().length();
            }

            @Override
            public String getSubject() {
                return String.format("\"%s\".length", getStringSubject());
            }
        });
    }
}
