package eu.tsystems.mms.tic.testframework.pageobjects;

public interface GuiElementFactory {
    IGuiElement createWithPage(
        PageObject page,
        Locate locator
    );
    IGuiElement createFromParent(
        IGuiElement parent,
        Locate locator
    );
    IGuiElement createWithFrames(
        Locate locator,
        IGuiElement... frames
    );
}
