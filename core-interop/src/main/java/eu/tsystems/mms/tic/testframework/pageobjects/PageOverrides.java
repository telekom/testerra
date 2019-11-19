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
    /**
     * @return Configured or default element timeout
     */
    int getElementTimeoutInSeconds();

    /**
     * Sets a new element timeout and returns the previously configured
     */
    int setElementTimeoutInSeconds(int elementTimeoutInSeconds);

    /**
     * @return Configured or default element check rule
     */
    CheckRule getGuiElementCheckRule();

    /**
     * Sets a new check rule and returns the previously configured
     */
    CheckRule setGuiElementCheckRule(CheckRule guiElementCheckRule);
}
