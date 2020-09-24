package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.util.regex.Matcher;

public class DefaultPatternAssertion extends AbstractTestedPropertyAssertion<Matcher> implements PatternAssertion {

    public DefaultPatternAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<Matcher> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public Matcher getActual() {
        return provider.getActual();
    }

    @Override
    public boolean is(boolean expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertion.equals(actual.find(), expected),
                (actual) -> assertion.formatExpectEquals(actual.find(), expected, createFailMessage(failMessage))
        );
    }
}
