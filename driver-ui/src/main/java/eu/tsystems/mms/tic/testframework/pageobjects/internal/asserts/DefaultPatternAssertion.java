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
    public boolean is(boolean expected, String failMessage) {
        this.testTimer(matcher -> instantAssertion.assertEquals(getActual().find(), expected), createFailMessageSupplier(failMessage));
        return false;
    }
}
