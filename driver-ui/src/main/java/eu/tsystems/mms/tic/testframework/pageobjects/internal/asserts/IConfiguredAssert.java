package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.ILayout;

public interface IConfiguredAssert {
    void assertTrue(boolean value, String assertionMessage);

    void assertFalse(boolean value, String assertionMessage);

    void assertLayout(GuiElementFacade guiElement, ILayout layout);
}
