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
    int getElementTimeoutInSeconds(int fallbackTimeout);
    PageOverrides setElementTimeoutInSeconds(int elementTimeoutInSeconds);
    PageOverrides removeElementTimeoutInSeconds();
    CheckRule getGuiElementCheckRule(CheckRule fallbackCheckRule);
    PageOverrides setGuiElementCheckRule(CheckRule guiElementCheckRule);
    PageOverrides removeGuiElementCheckRule();
}
