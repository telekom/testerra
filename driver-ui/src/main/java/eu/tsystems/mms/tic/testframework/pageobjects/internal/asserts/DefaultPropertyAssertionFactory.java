package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultPropertyAssertionFactory implements PropertyAssertionFactory, Loggable {
    private boolean nextShouldWait = false;
    @Override
    public <ASSERTION extends PropertyAssertion, TYPE> ASSERTION create(
        Class<ASSERTION> assertionClass,
        PropertyAssertion parentAssertion,
        AssertionProvider<TYPE> provider
    ) {
        ASSERTION assertion;
        try {
            Constructor<ASSERTION> constructor = assertionClass.getDeclaredConstructor(PropertyAssertion.class, AssertionProvider.class);
            assertion = constructor.newInstance(parentAssertion, provider);
            assertion.shouldWait(nextShouldWait);
            nextShouldWait = false;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log().error(String.format("Unable to create assertion: %s", e.getMessage()), e);
            assertion = null;
        }
        return assertion;
    }

    @Override
    public PropertyAssertionFactory nextShouldWait() {
        nextShouldWait = true;
        return this;
    }
}
