package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.ILayout;

public interface IConfiguredAssert {
    void assertTrue(boolean value, String assertionMessage);

    void assertFalse(boolean value, String assertionMessage);

    void assertLayout(IGuiElement guiElement, ILayout layout);
}
