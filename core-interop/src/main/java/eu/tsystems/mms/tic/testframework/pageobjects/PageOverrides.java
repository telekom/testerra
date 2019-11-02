package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;

/**
 * Allows temporary thread local Page related overrides
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
