package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;

public interface CheckablePage {
    default PageObject checkUiElements() {
        return checkUiElements(CheckRule.IS_DISPLAYED);
    }
    PageObject checkUiElements(CheckRule checkRule);
}
