package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;

public interface GuiElementAssertFactory {
    GuiElementAssert create(
        boolean functional,
        GuiElementCore guiElementCore,
        GuiElementWait guiElementWait,
        GuiElementData guiElementData
    );
}
