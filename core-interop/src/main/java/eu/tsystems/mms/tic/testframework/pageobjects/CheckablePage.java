package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;

public interface CheckablePage {
    /**
     * @deprecated Use {@link #checkGuiElements()} instead
     */
    @Deprecated
    default void checkPage() {
        checkGuiElements();
    }

    default PageObject checkGuiElements() {
        return checkGuiElements(CheckRule.IS_DISPLAYED);
    }
    PageObject checkGuiElements(CheckRule checkRule);
}
