package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.IGuiElement;

public interface GuiElementFactory {
    IGuiElement create(
        Locate locator,
        PageObject page
    );
    IGuiElement createFromAncestor(
        Locate locator,
        IGuiElement ancestor
    );
    IGuiElement createWithFrames(
        Locate locator,
        IGuiElement... frames
    );
}
