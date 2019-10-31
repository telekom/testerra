package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.WebDriver;

public interface GuiElementFactory {
    IGuiElement create(
        Locate locator,
        WebDriver webDriver
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
