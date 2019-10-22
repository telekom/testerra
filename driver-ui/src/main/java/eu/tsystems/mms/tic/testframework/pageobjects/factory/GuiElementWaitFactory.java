package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementStatusCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;

public interface GuiElementWaitFactory {
    GuiElementWait create(
        GuiElementStatusCheck guiElementStatusCheck,
        GuiElementData guiElementData
    );
}
