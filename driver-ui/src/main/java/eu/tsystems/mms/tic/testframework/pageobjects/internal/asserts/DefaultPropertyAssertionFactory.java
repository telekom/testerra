package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultPropertyAssertionFactory implements PropertyAssertionFactory, Loggable {
    private boolean wait = false;
    private int timeoutInSeconds = UiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong().intValue();

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
            assertion.shouldWait(wait);
            assertion.setTimeoutSeconds(timeoutInSeconds);
            wait = false;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log().error(String.format("Unable to create assertion: %s", e.getMessage()), e);
            assertion = null;
        }
        return assertion;
    }

    @Override
    public PropertyAssertionFactory shouldWait() {
        this.wait = true;
        return this;
    }

    @Override
    public PropertyAssertionFactory setDefaultTimeoutSeconds(int seconds) {
        timeoutInSeconds = seconds;
        return this;
    }
}
