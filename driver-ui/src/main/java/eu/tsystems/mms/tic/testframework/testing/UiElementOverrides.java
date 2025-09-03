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

    /**
     * A custom timeout with CONTROL.withTimeout with default settings will be ignored.
     * Otherwise, the previous timeout (maybe the default) is set as new thread local value in DefaultTestControllerOverrides. But
     * this has an impact on other custom timeout settings with @PageOptions or @Check.
     */
    @Override
    public int setTimeout(int seconds) {
        int defaultTimeoutInSeconds = UiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong().intValue();
        if (seconds == defaultTimeoutInSeconds) {
            return super.setTimeout(-1);
        } else {
            return super.setTimeout(seconds);
        }
    }
}
