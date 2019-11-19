package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;

public interface CheckablePage extends WebDriverRetainer {
    /**
     * @deprecated Use {@link #checkGuiElements()} instead
     */
    @Deprecated
    void checkPage();

    default void checkGuiElements() {
        checkGuiElements(CheckRule.IS_DISPLAYED);
    }
    void checkGuiElements(CheckRule checkRule);
}
