package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.GuiElementActions;

/**
 * Provides more action features which cannot be chained
 * @author Mike Reiche
 */
public interface InteractiveGuiElement extends
    TestableGuiElement,
    GuiElementActions
{
    /**
     * Performs actions as a user
     */
    GuiElementActions asUser();
}
