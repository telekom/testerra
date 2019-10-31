package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;

public interface PageConfig {
    int getElementTimeoutInSeconds();
    PageConfig setElementTimeoutInSeconds(int uiElementTimeoutInSeconds);
    CheckRule getGuiElementCheckRule();
    PageConfig setGuiElementCheckRule(CheckRule guiElementCheckRule);
}
