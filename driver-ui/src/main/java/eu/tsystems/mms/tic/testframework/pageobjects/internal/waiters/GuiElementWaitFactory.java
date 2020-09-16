package eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;

/**
 * Move this interface to core-interop when {@link GuiElementData} has an interface
 */
@Deprecated
public interface GuiElementWaitFactory {
    GuiElementWait create(GuiElementCore core, GuiElementData guiElement);
}
