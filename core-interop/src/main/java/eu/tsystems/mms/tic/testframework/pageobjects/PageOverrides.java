package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.testing.TestController;

/**
 * Allows temporary thread local Page related overrides.
 * This interface was originally created to replace the POConfig
 * and may be @deprecated since {@link TestController} for combining them both.
 * @author Mike Reiche
 */
public interface PageOverrides {
    boolean hasTimeoutSeconds();
    /**
     * @return Configured or default element timeout
     */
    int getTimeoutSeconds();

    /**
     * Sets a new element timeout and returns the previously configured
     * @param seconds If < 0, the timeout configuration will be removed
     */
    int setTimeoutSeconds(int seconds);

    boolean hasCheckRule();

    /**
     * @return Configured or default element check rule
     */
    CheckRule getCheckRule();

    /**
     * Sets a new check rule and returns the previously configured
     * @param checkRule If null, the checkrule configuration will be removed
     */
    CheckRule setCheckRule(CheckRule checkRule);
}
