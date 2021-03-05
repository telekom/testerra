package eu.tsystems.mms.tic.testframework.testing;

import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;

/**
 * UiElement related overrides
 * @author Mike Reiche
 */
public class UiElementOverrides extends DefaultTestControllerOverrides {
    @Override
    public int getTimeoutInSeconds() {
        int timeoutInSeconds = super.getTimeoutInSeconds();
        if (timeoutInSeconds < 0) {
            timeoutInSeconds = UiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong().intValue();
        }
        return timeoutInSeconds;
    }
}
