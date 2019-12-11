package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementAssertions;

/**
 * Provides more assertions features which cannot chained
 * @author Mike Reiche
 */
public interface TestableUiElement extends UiElementAssertions {
    TestableUiElement waitFor();
}
