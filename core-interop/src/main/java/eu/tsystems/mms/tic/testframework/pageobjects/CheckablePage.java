package eu.tsystems.mms.tic.testframework.pageobjects;

public interface CheckablePage extends WebDriverRetainer {
    /**
     * @deprecated Use {@link #checkGuiElements()} instead
     */
    @Deprecated
    void checkPage();

    default void checkGuiElements() {
        checkPage();
    }
}
