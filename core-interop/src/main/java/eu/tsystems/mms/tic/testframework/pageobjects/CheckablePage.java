package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.WebDriverRetainer;

public interface CheckablePage extends WebDriverRetainer {
    /**
     * @deprecated Use {@link #checkGuiElements()} instead
     */
    @Deprecated
    void checkPage();

    @Deprecated
    default void checkGuiElements() {
        checkPage();
    }
}
