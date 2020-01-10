package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.util.regex.Matcher;

public class DefaultPatternAssertion extends AbstractTestedPropertyAssertion<Matcher> implements PatternAssertion {

    public DefaultPatternAssertion(PropertyAssertion parentAssertion, AssertionProvider<Matcher> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public Matcher getActual() {
        return provider.getActual();
    }

    @Override
    public boolean isTrue(String prefixMessage) {
        return this.testTimer(matcher -> instantAssertion.assertTrue(getActual().find(), traceSubjectString()));
    }

    @Override
    public boolean isFalse(String prefixMessage) {
        return this.testTimer(matcher -> instantAssertion.assertFalse(getActual().find(), traceSubjectString()));
    }
}
