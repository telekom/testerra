package eu.tsystems.mms.tic.testframework.pageobjects;

public interface CheckablePage extends WebDriverRetainer {
    /**
     * @deprecated Use {@link #checkElements()} instead
     */
    @Deprecated
    void checkPage();

    default void checkElements() {
        checkPage();
    }
}
