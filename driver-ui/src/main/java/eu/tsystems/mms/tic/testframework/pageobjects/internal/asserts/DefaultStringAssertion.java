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

    @Override
    public boolean contains(String expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertContains((String)provider.getActual(), expected), createFailMessageSupplier(failMessage));
    }

    @Override
    public boolean containsNot(String expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertContainsNot((String)provider.getActual(), expected), createFailMessageSupplier(failMessage));
    }

    @Override
    public boolean startsWith(String expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertBeginsWith(provider.getActual(), expected), createFailMessageSupplier(failMessage));
    }

    @Override
    public boolean endsWith(String expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertEndsWith(provider.getActual(), expected), createFailMessageSupplier(failMessage));
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

    @Override
    public BinaryAssertion<Boolean> containsWords(String... words) {
        final String wordsList = String.join("|", words);
        final Pattern wordsPattern = Pattern.compile("\\b(" + wordsList + ")\\b", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                int found = 0;
                Matcher matcher = wordsPattern.matcher(provider.getActual().toString());
                while (matcher.find()) found++;
                return found >= words.length;
            }

            @Override
            public String getSubject() {
                return String.format("\"%s\".containsWords(%s)", getStringSubject(), wordsList);
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
