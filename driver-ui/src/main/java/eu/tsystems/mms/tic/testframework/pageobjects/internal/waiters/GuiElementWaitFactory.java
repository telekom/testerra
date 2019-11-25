package eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters;

import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementStatusCheck;

/**
 * Move this interface to core-interop when {@link GuiElementData} has an interface
 */
public interface GuiElementWaitFactory {
    GuiElementWait create(IGuiElement guiElement);
}
