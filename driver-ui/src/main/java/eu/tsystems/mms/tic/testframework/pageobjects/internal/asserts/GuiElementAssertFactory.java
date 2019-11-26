package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;

/**
 * Move this interface to core-interop when {@link GuiElementData} has an interface
 */
public interface GuiElementAssertFactory {
    GuiElementAssert create(
        GuiElementData data,
        Assertion assertion,
        GuiElementWait guiElementWait
    );
}
