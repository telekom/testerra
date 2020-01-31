package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementActions;

/**
 * Provides more action features which cannot be chained
 * @author Mike Reiche
 */
public interface InteractiveUiElement extends
    TestableUiElement,
    UiElementActions
{
    /**
     * Performs actions as a user
     */
    UiElementActions asUser();
}
