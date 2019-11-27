package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

/**
 * Creates preconfigured generic property assertions
 * @author Mike Reiche
 */
public interface PropertyAssertionFactory {
    <ASSERTION extends PropertyAssertion, TYPE> ASSERTION create(
        Class<ASSERTION> assertionClass,
        PropertyAssertion parentAssertion,
        AssertionProvider<TYPE> provider
    );
    default <ASSERTION extends PropertyAssertion, TYPE> ASSERTION create(
        Class<ASSERTION> assertionClass,
        AssertionProvider<TYPE> provider
    ) {
        return create(assertionClass, null, provider);
    }
    PropertyAssertionFactory shouldWait();
    PropertyAssertionFactory setDefaultTimeoutSeconds(int seconds);
}
