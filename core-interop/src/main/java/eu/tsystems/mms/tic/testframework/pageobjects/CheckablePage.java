package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;

public interface CheckablePage extends WebDriverRetainer {
    /**
     * @deprecated Use {@link #checkGuiElements()} instead
     */
    @Deprecated
    default void checkPage() {
        checkGuiElements();
    }

    default IPage checkGuiElements() {
        return checkGuiElements(CheckRule.IS_DISPLAYED);
    }
    IPage checkGuiElements(CheckRule checkRule);
}
