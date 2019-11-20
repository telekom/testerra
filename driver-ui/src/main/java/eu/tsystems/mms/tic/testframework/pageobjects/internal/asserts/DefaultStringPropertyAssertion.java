package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.Formatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultStringPropertyAssertion<T> extends DefaultQuantifiedPropertyAssertion<T> implements StringPropertyAssertion<T>, Loggable {

    private final static Formatter formatter = Testerra.injector.getInstance(Formatter.class);

    public DefaultStringPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(String expected) {
        return super.is(expected);
    }

    @Override
    public boolean contains(String expected) {
        return testTimer(t -> instantAssertion.assertContains((String)provider.getActual(), expected, traceSubjectString()));
    }

    @Override
    public boolean containsNot(String expected) {
        return testTimer(t -> instantAssertion.assertContainsNot((String)provider.getActual(), expected, traceSubjectString()));
    }

    @Override
    public boolean beginsWith(String expected) {
        return testTimer(t -> instantAssertion.assertBeginsWith(provider.getActual(), expected, traceSubjectString()));
    }

    @Override
    public boolean endsWith(String expected) {
        return testTimer(t -> instantAssertion.assertEndsWith(provider.getActual(), expected, traceSubjectString()));
    }

    @Override
    public PatternAssertion matches(Pattern pattern) {
        return new DefaultPatternAssertion(this, new AssertionProvider<Matcher>() {
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
    public QuantifiedPropertyAssertion<Integer> length() {
        return new DefaultQuantifiedPropertyAssertion<>(this, new AssertionProvider<Integer>() {
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

    @Override
    public DefaultStringPropertyAssertion<T> perhaps() {
        super.perhaps();
        return this;
    }
}
