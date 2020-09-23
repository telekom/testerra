package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultPropertyAssertionFactory implements PropertyAssertionFactory, Loggable {
    private boolean nextShouldWait = false;
    private final TestController.Overrides overrides;

    @Inject
    DefaultPropertyAssertionFactory(TestController.Overrides overrides) {
        this.overrides = overrides;
    }

    @Override
    public <ASSERTION extends AbstractPropertyAssertion, TYPE> ASSERTION create(
        Class<ASSERTION> assertionClass,
        AbstractPropertyAssertion parentAssertion,
        AssertionProvider<TYPE> provider
    ) {
        ASSERTION assertion;
        try {
            Constructor<ASSERTION> constructor = assertionClass.getDeclaredConstructor(AbstractPropertyAssertion.class, AssertionProvider.class);
            assertion = constructor.newInstance(parentAssertion, provider);

            if (parentAssertion != null) {
                assertion.config = parentAssertion.config;
            } else {
                assertion.config = new PropertyAssertionConfig();
                assertion.config.shouldWait = nextShouldWait;
                assertion.config.timeoutInSeconds = overrides.getTimeoutInSeconds();
                assertion.config.pauseIntervalMs = UiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong();
            }

            nextShouldWait = false;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log().error(String.format("Unable to create assertion: %s", e.getMessage()), e);
            assertion = null;
        }
        return assertion;
    }

    @Override
    public PropertyAssertionFactory shouldWait() {
        this.nextShouldWait = true;
        return this;
    }
}
