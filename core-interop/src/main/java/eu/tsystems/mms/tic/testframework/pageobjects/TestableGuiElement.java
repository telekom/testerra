package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.GuiElementAssertions;

/**
 * Provides more assertions features which cannot chained
 * @author Mike Reiche
 */
public interface TestableGuiElement extends GuiElementAssertions {
    TestableGuiElement waitFor();
}
